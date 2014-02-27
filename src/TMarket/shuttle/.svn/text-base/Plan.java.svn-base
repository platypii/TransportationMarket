/*
 * The Transportation Market
 *
 * Copyright 2011 Kenny Daniel
 * Copyright 2011 Xiaoqing Wang
 *
 * The Transportation Market is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The Transportation Market is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * The Transportation Market.  If not, see <http://www.gnu.org/licenses/>.
 */

package TMarket.shuttle;

import TMarket.common.*;

import java.util.*;


// Represents a schedule
public class Plan {

  private City city;
  private Shuttle shuttle;

  // Service time represents the amount of time needed to stop and pickup a customer
  private static double SERVICE_TIME = 0.0;

  // The plan data structure. Maps times to locations (and stores some other info like time windows)
  public List<Node> plan;
  public int num_consumers;

  // alpha is the coefficient of the total consumer time on ride of the plan
  private static double alpha=1;
  private static double beta=1;
  private static double gamma=1; //weight on c3 which is the cost of increase in distance
  HashMap<String,Node> pickup_node = new HashMap<String,Node>();
  HashMap<String,Node> dropoff_node = new HashMap<String,Node>();
  HashMap<String,Node> new_plan_pickup_node = new HashMap<String,Node>();
  HashMap<String,Node> new_plan_dropoff_node = new HashMap<String,Node>();


  //record the index of the real time current node (used in insert node function)
  public int real_time_current_loc_index=0;

  // Creates an empty plan (used in insertNode())
  public Plan(City city, Shuttle shuttle, int num_consumers) {
    this.city = city;
    this.shuttle = shuttle;
    this.plan = new ArrayList<Node>();
    this.num_consumers = num_consumers;
  }

  // Creates a new plan
  public Plan(City city, Shuttle shuttle, Loc home, double driver_start_time, double driver_end_time) {
    this.city = city;
    this.shuttle = shuttle;
    this.plan = new ArrayList<Node>();
    this.num_consumers = 0;
    // driver end time or real arrival time

  //TODO: wait time here only consider the same depot. should be changed if the start and end is not the same.(driver_end_time - driver_start_time-time(start, end))/2
    plan.add(0, new Node(shuttle.id(), 0,Node.HOME, driver_start_time, 0.0, 0.0,home, driver_start_time, driver_start_time, 0.0));
    // add to the last place of the list
    plan.add(new Node(shuttle.id(),1, Node.HOME, driver_end_time,0.0,0.0, home, driver_end_time, driver_end_time, 0.0));

    //there're differences between the two driver's nodes setting

    //TODO: wait time here only consider the same depot. should be changed if the start and end is not the same.(driver_end_time - driver_start_time-time(start, end))/2
  /*  plan.add(0, new Node(shuttle.id(), 0,Node.HOME, driver_start_time, 0.0, (driver_end_time - driver_start_time)/2,home, driver_start_time, driver_start_time, 0.0));
    // add to the last place of the list
    plan.add(new Node(shuttle.id(),1, Node.HOME, driver_end_time,(driver_end_time - driver_start_time)/2,0.0, home, driver_end_time, driver_end_time, 0.0));
  */
  }

  // Returns the location of the shuttle at given time
  public Loc getLoc(double time) {
    if(time < plan.get(0).hit_time) {
      // Before start of plan
      return plan.get(0).loc;
    } else if(plan.get(plan.size()-1).hit_time < time) {
      // After end of plan
      return plan.get(plan.size()-1).loc;
    } else {
      // Search thru plan for current time
      // TODO: Go directly instead of searching using an index data structure
      int prev_index = -1; // index to the previous node
      for(int i = 0; i < plan.size(); i++) {
        if(plan.get(i).hit_time <= time && time <=plan.get(i).hit_time+plan.get(i).wait_time_after) {//consider the reassignment of wait time(stay after the hit time)
          return plan.get(i).loc;
        } else if(plan.get(i).hit_time+plan.get(i).wait_time_after < time && time < plan.get(i+1).hit_time) {
          prev_index = i;
          break;
        }
      }
      Node prev = plan.get(prev_index);
      Node next = plan.get(prev_index+1);

      assert prev.hit_time <= time && time <= next.hit_time;

      if(prev.hit_time + prev.wait_time_after + city.time(prev.loc, next.loc) <= time) {
        // Finished driving toward next, waiting there now
        return next.loc;
      } else {
        // Expand path between prev and next
        List<Loc> path = prev.loc.getPath(next.loc);

        double path_time = prev.hit_time + prev.wait_time_after;
        for(int i = 1; i < path.size(); i++) {
          double segment_time = city.time(path.get(i-1), path.get(i));

//          Debug.println("pathTime = "+path_time+", segmentTime = "+segment_time);

          if(path_time + segment_time >= time) {
            // Shuttle is currently between path[i-1] and path[i]

            // fraction of distance traveled along this segment
            double frac = (time - path_time) / segment_time;
            int x = (int) (path.get(i).x * frac + path.get(i-1).x * (1 - frac));
            int y = (int) (path.get(i).y * frac + path.get(i-1).y * (1 - frac));

            return new Loc(city, x, y);
          }

          path_time += segment_time;
        }

//        Debug.println("getLoc("+time+") = null");
//        Debug.println("plan = \n" + this);

        assert false;
        return null;
      }
    }
  } // end of getLoc

  public Loc getNextLoc(double time) {
    if(time < plan.get(0).hit_time) {
      // Before start of plan
      return plan.get(0).loc;
    } else if(plan.get(plan.size()-1).hit_time < time) {
      // After end of plan
      return plan.get(plan.size()-1).loc;
    } else {

      int prev_index = -1; // index to the previous node
      for(int i = 0; i < plan.size(); i++) {
        if(plan.get(i).hit_time <= time && time <=plan.get(i).hit_time+plan.get(i).wait_time_after) {//consider the reassignment of wait time(stay after the hit time)
          return plan.get(i).loc;
        } else if(plan.get(i).hit_time+plan.get(i).wait_time_after < time && time < plan.get(i+1).hit_time) {
          prev_index = i;
          break;
        }
      }

      Node prev = plan.get(prev_index);
      Node next = plan.get(prev_index+1);

      assert prev.hit_time <= time && time <= next.hit_time;

      if(prev.hit_time + prev.wait_time_after + city.time(prev.loc, next.loc) <= time) {
        // Finished driving toward next, waiting there now
        return next.loc;
      } else {
        // Expand path between prev and next
        List<Loc> path = prev.loc.getPath(next.loc);

        double path_time = prev.hit_time + prev.wait_time_after;
        for(int i = 1; i < path.size(); i++) {
          double segment_time = city.time(path.get(i-1), path.get(i));

//          Debug.println("pathTime = "+path_time+", segmentTime = "+segment_time);

          if(path_time + segment_time >= time) {
            // Shuttle is currently between path[i-1] and path[i]

            return path.get(i);
          }

          path_time += segment_time;
        }

//        Debug.println("getLoc("+time+") = null");
//        Debug.println("plan = \n" + this);

        assert false;
        return null;
      }
    }
  } // end of getNextLoc

  // Returns a new plan that visits start and end via the cheapest legal insertion
  // Returned offer guaranteed to contain the associated plan
  // TODO: Enforce capacity constraints
  // TODO: sequence of insertion matters even all requests made at 00:00
  public Offer cheapestInsertion(Request req, int capacity) {
    Loc start = city.getLocByName(req.start);
    Loc end = city.getLocByName(req.end);

    // The best insertion found so far
    double best_cost = Double.POSITIVE_INFINITY;
    Offer best_offer = null;

    Debug.println("cheapestInsertion("+shuttle.id()+" " + req + ")");
    Debug.println("plan = \n" +shuttle.id()+" "+ this);

    int start_prev_index = 0; // Index to the node to insert start after

    // insert the node of shuttle's current location
    for(int i = 0; i < plan.size()-1; i++) {
      if(plan.get(i).hit_time <= req.request_time && plan.get(i+1).hit_time > req.request_time) {
        Double new_time = plan.get(i).hit_time+plan.get(i).wait_time_after + city.time(plan.get(i).loc, getNextLoc(req.request_time));
        //TODO:consider if the wait_time of the temp node(now it could have wait time). consider real time situation.
        if(plan.get(i).loc==getNextLoc(req.request_time)){

          plan.add(i+1, new Node(null, i+1,Node.TEMP, req.request_time,0.0,0.0, getNextLoc(req.request_time), req.request_time, req.request_time, 0.0));

        } else {

          plan.add(i+1, new Node(null, i+1,Node.TEMP, Math.max(new_time,req.request_time),0.0,0.0, getNextLoc(req.request_time), req.request_time, Math.max(new_time,req.request_time), 0.0));

        }
        start_prev_index = i+1;
        real_time_current_loc_index = i+1;
        break;
      }
    }

    // Iterate through plan to find start location
    while(start_prev_index + 1 < plan.size()) {
      Node start_prev = plan.get(start_prev_index);
      //change to real time
      //TODO: think about if the driver has pass the node, the maxpostpone time of the previous nodes would not affect the latter ones.
      if(start_prev_index == 0 || start_prev.hit_time > req.request_time ||start_prev_index + 2 == plan.size()) {

        // time for driver to get the customer
        // double driving_start_time = shuttle.getTime(); // TODO: Max time to next node
        // double start_arrival_time = Math.max(Math.max(arrival_time(start_prev), start_prev.earliest_time) + start_prev.service_time, driving_start_time) + city.time(start_prev.loc, req.start);
        double start_arrival_time = Math.max(arrival_time(start_prev_index), start_prev.earliest_time) + start_prev.service_time + city.time(start_prev.loc, start);
        double start_hit_time = Math.max(start_arrival_time, req.earliest_pickup_time);
        double start_wait_time_before = Math.max(0, req.earliest_pickup_time-start_arrival_time);
        // Check that the start time is within the time window
//      if(req.earliest_pickup_time <= start_arrival_time && start_arrival_time <= req.latest_pickup_time) {

        // Insert start node in new_plan1, if legal
        Node start_node = new Node(req.consumer_id, start_prev_index+1,Node.PICKUP, start_hit_time, start_wait_time_before,0.0, start, req.earliest_pickup_time, req.latest_pickup_time, SERVICE_TIME);
        if(this.insertLegal(start_node, start_prev_index)) {
          Plan new_plan1 = this.insertNode(start_node, start_prev_index);

          // Iterate through plan to find end location
          // earliest possible dropoff time
          int end_prev_index = start_prev_index + 1;
          while(end_prev_index + 1 < new_plan1.plan.size()) {
            Node end_prev = new_plan1.plan.get(end_prev_index);

//          assert end_prev.equals(start_node);

            // time for driver to get the customer
            double end_arrival_time = Math.max(new_plan1.arrival_time(end_prev_index), end_prev.earliest_time) + end_prev.service_time + city.time(end_prev.loc, end);
            double end_hit_time = Math.max(end_arrival_time, req.earliest_dropoff_time);
            double end_wait_time_before = Math.max(0,req.earliest_dropoff_time-end_arrival_time);
            // Check that the end time is within the time window

//            Debug.println("cheapestInsertion - start_node = " + start_node);

            // Insert end node in new_plan2, if legal
            Node end_node = new Node(req.consumer_id,end_prev_index+1, Node.DROPOFF, end_hit_time, end_wait_time_before,0.0, end, req.earliest_dropoff_time, req.latest_dropoff_time, SERVICE_TIME);
            if(new_plan1.insertLegal(end_node, end_prev_index)) {
              // As of now, plan2 contains the start and end nodes inserted at start_arrival_time and end_arrival_time

              Debug.println("arrival_time(end_prev) = " + shuttle.id() + " " + new_plan1.arrival_time(end_prev_index));

              Plan new_plan2 = new_plan1.insertNode(end_node, end_prev_index);
              new_plan2.num_consumers++;

              double subcost=this.subtotalcost(start_node, start_prev_index);

              //double cost = alpha * new_plan2.consumer_time_on_ride()+beta*new_plan2.driver_total_driving_distance();
              double cost = this.subtotalcost(start_node, start_prev_index) + new_plan1.subtotalcost(end_node, end_prev_index)+ alpha * new_plan2.consumer_time_on_ride();//+beta*new_plan2.driver_total_driving_distance();

              //double cost = this.cost3(start_node, start_prev_index) + new_plan1.cost3(end_node, end_prev_index);//+ alpha * new_plan2.consumer_time_on_ride();//+beta*new_plan2.driver_total_driving_distance();

              Debug.println("trying plan, start = " + shuttle.id() + " " + start_node);
              Debug.println("trying plan, end = " + shuttle.id() + " " + end_node);
              Debug.println("trying plan, cost1(start) = " + shuttle.id() + " " + this.cost1(start_node, start_prev_index));
              Debug.println("trying plan, cost1(end) = " + shuttle.id() + " " + new_plan1.cost1(end_node, end_prev_index));
              Debug.println("trying plan, cost2(start) = " + shuttle.id() + " " + this.cost2(start_node, start_prev_index));
              Debug.println("trying plan, cost2(end) = " + shuttle.id() + " " + new_plan1.cost2(end_node, end_prev_index));
              Debug.println("trying plan, cost3(start) = " + shuttle.id() + " " + gamma*this.cost3(start_node, start_prev_index));
              Debug.println("trying plan, cost3(end) = " + shuttle.id() + " " + gamma*new_plan1.cost3(end_node, end_prev_index));
              Debug.println("trying plan, cost3 start prev node=" + shuttle.id() + " " + this.prevNode(start_node));
              Debug.println("trying plan, cost3 start next node=" + shuttle.id() + " " + this.nextNode(start_node));
              Debug.println("trying plan, cost3 end prev node=" + shuttle.id() + " " + new_plan1.prevNode(end_node));
              Debug.println("trying plan, cost3 end next node=" + shuttle.id() + " " + new_plan1.nextNode(end_node));
             // Debug.println("trying plan, consumer time on the car=" + shuttle.id() + " " + (end_node.hit_time-start_node.hit_time));
              Debug.println("trying plan, this = "  + this);
             // Debug.println("trying plan, copythis = "  + copythis);
              Debug.println("trying plan, cost new_plan1 = " + subcost + ": \n" + shuttle.id() + " " + new_plan1);
              //Debug.println("trying plan, cost copynew_plan1 = " + subcost + ": \n" + shuttle.id() + " " + copynew_plan1);
              Debug.println("trying plan, cost new plan2= " + cost + ": \n" + shuttle.id() + " " + new_plan2);

              // Check if this is the best insertion found so far
              if(cost < best_cost) {
                double pickup_time = Math.max(start_arrival_time, req.earliest_pickup_time);
                double distance = this.distance_change(start_node, start_prev_index) + new_plan1.distance_change(end_node, end_prev_index);
                double cost3 = this.cost3(start_node, start_prev_index) + new_plan1.cost3(end_node, end_prev_index);

                best_offer = new Offer(shuttle.id(), req, pickup_time, end_arrival_time, distance, cost3);
                /*for(int i = 1; i < new_plan2.plan.size()-1; i++) {

                  if(new_plan2.plan.get(i).type == Node.PICKUP) {
                    new_plan_pickup_node.put(new_plan2.plan.get(i).id,new_plan2.plan.get(i));
                  }
                  if(new_plan2.plan.get(i).type == Node.DROPOFF) {
                    new_plan_dropoff_node.put(new_plan2.plan.get(i).id,new_plan2.plan.get(i));
                   for(int j= new_plan_pickup_node.get(new_plan2.plan.get(i).id).index; j<i;j++){
                     if(new_plan2.plan.get(j).loc==new_plan2.plan.get(i).loc){
                       Node new_node= new_plan2.plan.get(i);
                       new_node.hit_time=new_plan2.plan.get(j).hit_time;
                       new_plan2.plan.remove(i);
                       new_plan2.plan.add(j+1, new_node);
                       //new_plan2 = new_plan2.insertNode(new_node, j);
                     }
                   }
                  }
                }*/

                best_offer.plan = new_plan2;
                best_cost = cost;

                // TODO: cost vs cost3?
              }
            }

            end_prev_index++;
          }
        }
      } // end of if function
      start_prev_index++;
    }

//    assert best_cost != Double.POSITIVE_INFINITY;
    Debug.println("best_cost="+best_cost);
    Debug.println("best offer:"+best_offer);

    return best_offer;
    //return best_cost;

  }

  // Return true if the insertion insertNode(insert_node, insert_after) is legal
  private boolean insertLegal(Node insert_node, int insert_after_index) {
    assert insert_node != null;
    assert 0 <= insert_after_index;
    assert insert_after_index < plan.size();

//    Debug.println("InsertLegal(" + insert_node);
//    Debug.println("plan = \n" + this);

    // Return false if request time window before or after plan
    if(insert_node.latest_time < plan.get(0).earliest_time)
      return false;
    if(plan.get(plan.size()-1).latest_time < insert_node.earliest_time)
      return false;

    if(insert_node.earliest_time > insert_node.hit_time || insert_node.hit_time > insert_node.latest_time)
      return false;

    Node insert_after = plan.get(insert_after_index);
    Node next = plan.get(insert_after_index + 1);
    // arrival_time = Math.max(prev.getKey(), prev.getValue().earliest_time) + prev.getValue().service_time + City.time(prev.getValue().loc,loc);

    // Deal with collision in the plan
    if(next.hit_time == insert_node.hit_time && next.loc.equals(insert_node.loc)) {
      return true;
    }

    if((insert_after.hit_time + insert_after.service_time + city.time(insert_after.loc, insert_node.loc) <= insert_node.latest_time) &&
       (Math.max(insert_after.hit_time + insert_after.service_time + city.time(insert_after.loc, insert_node.loc), insert_node.earliest_time) + insert_node.service_time + city.time(insert_node.loc, next.loc) <= arrival_time(insert_after_index + 1) + waittime(insert_after_index + 1) + maxpostpone_time(insert_after_index + 1)))
      return true;
    else
      return false;

  }

  // Creates a new plan with insert_node inserted after insert_node
  private Plan insertNode(Node insert_node, int insert_after_index) {
    assert insertLegal(insert_node, insert_after_index);

    Debug.println("insertNode(" + insert_node + ","+"index:"+insert_node.index +" "+ plan.get(insert_after_index) + ")");

    // Create a new plan (since most data will change in the current plan anyway)
    Plan new_plan = new Plan(city, shuttle, num_consumers);

    for(int index = 0; index <= insert_after_index; index++) {
      Node current = plan.get(index);
      Node new_node;

      // Copy current node to new_plan
    //shrink the timewindow if two neighbour nodes has same location and hit time, and the prev node of the insert node has larger latest time
      if(index==insert_after_index&&insert_node.loc==plan.get(insert_after_index).loc&&insert_node.hit_time==plan.get(insert_after_index).hit_time&&plan.get(insert_after_index).latest_time>insert_node.latest_time){
      //TODO: the wait_time here don't have value yet.
        new_node = new Node(current.id,index, current.type, current.hit_time,current.wait_time_before,current.wait_time_after, current.loc, current.earliest_time, insert_node.latest_time, current.service_time);

      }
      else{
     //TODO: wait_time;
        new_node = new Node(current.id,index, current.type, current.hit_time, current.wait_time_before,current.wait_time_after,current.loc, current.earliest_time, current.latest_time, current.service_time);
      }
      new_plan.plan.add(new_node);

    }

    new_plan.plan.add(insert_node);

    for(int index = insert_after_index + 1; index < plan.size(); index++) {
      Node new_prev = new_plan.plan.get(index); // previous node in new_plan
      Node current = plan.get(index);
      Node new_node;
      double new_arrival_time = Math.max(new_plan.arrival_time(index), new_prev.earliest_time) + new_prev.service_time + city.time(new_prev.loc, current.loc);
      double new_hit_time = Math.max(new_arrival_time, current.earliest_time);
      double new_wait_time_before = Math.max(0, current.earliest_time-new_arrival_time);

      // shrink the timewindow if two neighbour nodes has same location and hit time, and the after node of the insert node has larger latest time
      if(index==insert_after_index+1&&insert_node.loc==plan.get(insert_after_index+1).loc&&insert_node.hit_time==plan.get(insert_after_index+1).hit_time&&plan.get(insert_after_index+1).latest_time>insert_node.latest_time){
       // TODO: WAIT TIME
        new_node = new Node(current.id,index+1, current.type, new_hit_time, new_wait_time_before,0.0, current.loc, current.earliest_time, insert_node.latest_time, current.service_time);

      } else {

        // TODO: WAIT TIME
        new_node= new Node(current.id, index+1,current.type, new_hit_time, new_wait_time_before,0.0, current.loc, current.earliest_time, current.latest_time, current.service_time);

      }
      new_plan.plan.add(new_node);

//      Debug.println("new_plan.arrival_time(new_prev) = "+new_plan.arrival_time(new_prev));

    }
  //the wait time would be reassigned among the nodes after the current loc under real time (real_time_current_loc_index)
  //TODO: consider if it is ok to wait at temp loc
  /*  for(int i=new_plan.plan.size()-1; i>real_time_current_loc_index; i--){
    if((new_plan.maxpostpone_time(i-1)+new_plan.waittime(i-1))<(new_plan.maxpostpone_time(i)+new_plan.waittime(i)+new_plan.plan.get(i).wait_time_after)){
      new_plan.plan.get(i-1).wait_time_after=Math.min(new_plan.waittime(i), ((new_plan.maxpostpone_time(i)+new_plan.waittime(i)+new_plan.plan.get(i).wait_time_after)-(new_plan.maxpostpone_time(i-1)+new_plan.waittime(i-1)))/2);
    }
    else {
      new_plan.plan.get(i-1).wait_time_after=0;
    }
    new_plan.plan.get(i).wait_time_before=new_plan.waittime(i)-new_plan.plan.get(i-1).wait_time_after;

    }
   */

    return new_plan;
  }

  // Returns the next node strictly after given time, or null if no such node
  private Node nextNode(Node node) {
    int index = plan.indexOf(node);

    if(index == plan.size()-1)
      return null;
    else
      return plan.get(index + 1);
  }

  // Returns the previous node strictly before given time, or null if no such node
  private Node prevNode(Node node) {
    int index = plan.indexOf(node);

    if(index <= 0)
      return null;
    else
      return plan.get(index-1);
  }

  // Returns the earliest time that we visit a location, after min_time (without changing the plan)
  public double timeToLoc(Loc loc, double min_time) {

    // Check if it is on our existing route
    for(Node node : plan) {
      double t = node.hit_time;
      Loc l = node.loc;

      if(min_time <= t && l.equals(loc)) {
        return t;
      }
    }

    assert false;
    return -1.0;
  }

  // Returns a list of locations visited between time t1 <= t < t2
/*  public List<Loc> subPlan(double t1, double t2) {
    List<Loc> visited = new ArrayList<Loc>();

   // for(Node node : plan.subMap(t1, t2).values())
   //   visited.add(node.loc);

    for(int i=0; i<plan.size();i++) {
      if(plan.get(i).hit_time >= t1 && plan.get(i).hit_time < t2) {
        visited.add(plan.get(i).loc);
      }
    }

    return visited;
    }*/

  //computes the arrival time for node in the plan
  private double arrival_time(int node_index) {
    Node node = plan.get(node_index);
    if(node_index == 0) {
      return node.hit_time;
    } else {
      Node prev = plan.get(node_index - 1);
      return prev.hit_time + prev.service_time + city.time(prev.loc, node.loc);
    }
  }

  // Computes the waittime for node in the plan
  private double waittime(int node_index) {
    Node node = plan.get(node_index);
    return Math.max(0, node.earliest_time - arrival_time(node_index));
  }
  // TODO: show wait time to driver to deal with idle time

  // Compute the maxpostpone time for node in the plan
  private double maxpostpone_time(int node_index) {

    // Debug.println("maxpostpone_time("+node+")");

    Node node = plan.get(node_index);
    if(node_index + 1 < plan.size()) {
      return Math.min(node.latest_time - Math.max(arrival_time(node_index), node.earliest_time), waittime(node_index + 1) + maxpostpone_time(node_index + 1));
    } else {
      return node.latest_time - Math.max(arrival_time(node_index), node.earliest_time);
    }
  }

  // reduction of time window for the nodes before the insertion node
  private double cost1(Node node, int insert_after_index) {
    Node prev = plan.get(insert_after_index);
    double beta = Math.max(0, node.earliest_time - (prev.hit_time+prev.service_time+city.time(prev.loc, node.loc))) + Math.min(node.latest_time - Math.max(prev.hit_time+prev.service_time + city.time(prev.loc, node.loc), node.earliest_time), waittime(insert_after_index + 1) + maxpostpone_time(insert_after_index + 1) - cost3(node, insert_after_index) - node.service_time - Math.max(0, node.earliest_time - (prev.hit_time + prev.service_time + city.time(prev.loc, node.loc))));
    //Debug.println("k" +insert_after_index+"beta" +beta+"maxpostpone" + " "+prev.id +" "+ Math.min(node.latest_time - Math.max(prev.hit_time+prev.service_time + city.time(prev.loc, node.loc), node.earliest_time), waittime(insert_after_index + 1) + maxpostpone_time(insert_after_index + 1) - cost3(node, insert_after_index) - node.service_time - Math.max(0, node.earliest_time - (prev.hit_time + prev.service_time + city.time(prev.loc, node.loc)))));
   // Debug.println("maxpostpone_time(insert_after_index)"+maxpostpone_time(insert_after_index));
   // Debug.println("waittime(last node)"+waittime(plan.size()-2));
   // Debug.println("maxpostpone_time(last node)"+maxpostpone_time(plan.size()-2));
    double cost1 = 0;
    // current is the k in the algorithm
    int current_index = insert_after_index;
    while(current_index > 0 && beta < maxpostpone_time(current_index)) {
      Node current = plan.get(current_index);
      if(current.hit_time == prev.hit_time) {
        cost1 = maxpostpone_time(current_index) - beta;
      } else if(waittime(current_index + 1) > 0) {
        cost1 = cost1 + Math.min(maxpostpone_time(current_index) - beta, waittime(current_index + 1));
      }
      beta = waittime(current_index) + beta;
      current_index--;
    }
    return cost1;
  }

  private double cost2(Node node, int insert_after_index) {
    Node prev = plan.get(insert_after_index);

    return node.latest_time - Math.max(prev.hit_time + prev.service_time+city.time(prev.loc, node.loc), node.earliest_time) - Math.min(node.latest_time - Math.max(prev.hit_time + prev.service_time + city.time(prev.loc, node.loc), node.earliest_time), waittime(insert_after_index + 1) + maxpostpone_time(insert_after_index + 1) - cost3(node, insert_after_index) - node.service_time - Math.max(0, node.earliest_time - (prev.hit_time + prev.service_time + city.time(prev.loc, node.loc))));
  }

  private double cost3(Node node, int insert_after_index) {
//    Debug.println("cost3(" + node +")");

   // if(plan.containsKey(node.hit_time) && plan.get(node.hit_time).loc.equals(node.loc) && insert_after.equals(prevNode(node.hit_time)))
   //   return 0.0;
    Node prev = plan.get(insert_after_index);
    Node next = plan.get(insert_after_index + 1);

//    Debug.println("prev = " + prev);
//    Debug.println("node = " + node);
//    Debug.println("next = " + next + "\n");

    double cost3 = city.time(prev.loc, node.loc) + city.time(node.loc, next.loc) - city.time(prev.loc, next.loc);
//    Debug.println("cost3 = " + cost3);

    if(-0.000000001 < cost3 && cost3 < 0.0)
      return 0.0;

    return cost3;
  }

  public double consumer_time_on_ride() {
    double t = 0;
    for(int i = 1; i < plan.size()-1; i++) {

      if(plan.get(i).type == Node.PICKUP) {
        pickup_node.put(plan.get(i).id,plan.get(i));
      }
      if(plan.get(i).type == Node.DROPOFF) {
        dropoff_node.put(plan.get(i).id,plan.get(i));
        t = t + plan.get(i).hit_time - pickup_node.get(plan.get(i).id).hit_time;
      }
    }
    return t;
  }

  public double total_wait_time() {
    double w = 0;
    for(int i = 0 ; i < plan.size()-1; i++){
      w = w + plan.get(i+1).hit_time-plan.get(i).hit_time-city.time(plan.get(i+1).loc, plan.get(i).loc);
    }
    return w;
  }

 /* public double average_vehicle_occupancy(){
    double v = 0;
    double o = 0;
    for(int i = 1; i < plan.size()-1; i++) {
      if(plan.get(i).type == Node.PICKUP) {
        pickup_node.put(plan.get(i).id,plan.get(i));
      }
      if(plan.get(i).type == Node.DROPOFF) {
        dropoff_node.put(plan.get(i).id,plan.get(i));
        v = v + plan.get(i).hit_time - pickup_node.get(plan.get(i).id).hit_time

        o = v / driver_duration;
      }
    }
    return o;
  }
  */

  public double driver_total_driving_distance() {
    double d = 0;
    for(int i = 1; i < plan.size(); i++) {
      d = d + city.dist(plan.get(i-1).loc, plan.get(i).loc);
    }
    return d;
  }

  private double subtotalcost(Node node, int insert_after_index) {
    if(node.loc==plan.get(insert_after_index).loc&&node.hit_time==plan.get(insert_after_index).hit_time&&plan.get(insert_after_index).latest_time>node.latest_time){
      return 0;
    }
    else if(node.loc==plan.get(insert_after_index+1).loc&&node.hit_time==plan.get(insert_after_index+1).hit_time&&plan.get(insert_after_index+1).latest_time>node.latest_time){
      return 0;
    }
    else{
      return cost1(node, insert_after_index) + cost2(node, insert_after_index) + gamma*cost3(node, insert_after_index);
    }
  }

  private double distance_change(Node node, int insert_after_index) {
    // if(plan.containsKey(node.hit_time) && plan.get(node.hit_time).loc.equals(node.loc) && insert_after.equals(prevNode(node.hit_time)))
    // return 0.0;

    Node prev = plan.get(insert_after_index);
    Node next = plan.get(insert_after_index + 1);

    double distance = city.dist(prev.loc, node.loc) + city.dist(node.loc, next.loc) - city.dist(prev.loc, next.loc);

    if(-0.000000001 < distance && distance < 0.0)
      return 0.0;

    return distance;
  }

  // Returns a string representation of this Plan
  public String toString() {
    String output = "";
    for(Node node : plan) {
      output += node + "\n";
    }
    return output;
  }

}


