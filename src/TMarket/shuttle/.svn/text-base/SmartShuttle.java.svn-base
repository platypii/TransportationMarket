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
import TMarket.auctionserver.*;

import java.util.*;


public class SmartShuttle implements Shuttle {
  private City city;
  private String id;

  // Connection to auction server
  private ShuttleAgent agent;

  // Vehicle capacity
  private static final int capacity = 10; // TODO: enforce

  private double start_time;
  private double end_time;
  private Loc home;
  public Plan plan;
  public Set<String> passengers = new HashSet<String>(); // passengers on the shuttle
  public Map<Request,Offer> offers = new HashMap<Request,Offer>();
  public Set<Offer> confirmed = new HashSet<Offer>();
  public Set<Offer> rejected = new HashSet<Offer>();
  private double payment = 0.0;

  // Current state
  private double time = 0.0;
  private int visited_index = 0; // Index of the next node to visit in the plan


  // Create new SmartShuttle (local)
  public SmartShuttle(AuctionServer aserver, City city, String id, Loc home, double start_time, double end_time) {
    this.city = city;
    this.id = id;
    this.start_time = start_time;
    this.end_time = end_time;
    this.home = home;
    this.plan = new Plan(city, this, home, start_time, end_time);

    // Initialize auction server connection
    this.agent = new ShuttleAgent(this, aserver); // local AuctionServer object
  }

  // Create new SmartShuttle (networked)
  public SmartShuttle(City city, String id, Loc home, double start_time, double end_time) {
    this.city = city;
    this.id = id;
    this.start_time = start_time;
    this.end_time = end_time;
    this.home = home;
    this.plan = new Plan(city, this, home, start_time, end_time);

    // Initialize auction server connection
    this.agent = new ShuttleAgent(this, "localhost", 1337);
  }

  // Called when a request comes in
  public void receiveRequest(Request req) {
//    Debug.println("SmartShuttle.receiveRequest(" + req + ")");

    // Find cheapest insertion
    Offer offer = plan.cheapestInsertion(req, capacity);

    if(offer != null) {
      assert time <= req.latest_pickup_time;
      assert time <= offer.pickup_time;
//      assert plan.cost() <= new_plan.cost();

      offers.put(req, offer);
      agent.sendOffer(offer);
    } else {
      offers.put(req, null);
      agent.noOffer(req);
    }
  }

  // Called when a consumer confirms an offer
  public void receiveConfirm(Offer offer) {
    assert offers.get(offer.req).equals(offer);
    assert !rejected.contains(offer);

    // TODO: Check that plan has not changed since we made the offer

//    Debug.println("plan before insert: \n" + plan);

    plan = offers.get(offer.req).plan;

    Debug.println("plan after insert(" + offer + "): \n" + plan);

    confirmed.add(offer);
    payment += offer.cost3;
  }

  // Called when a consumer rejects an offer
  public void receiveReject(Offer offer) {
    assert offers.get(offer.req).equals(offer);

    // Remove offer
    confirmed.remove(offer);
    rejected.add(offer);
  }

  // Steps forward 1 time step
  public void step(int new_time) {
    assert time < new_time;

    // Find all nodes in plan such that time <= node.hit_time < time+1
    while(visited_index < plan.plan.size() && 
          plan.plan.get(visited_index).hit_time < new_time) {
      Node node = plan.plan.get(visited_index);

      Debug.println(id + " visited " + node.loc + " (" + passengers.size() + " passengers)");

      // Pick up / drop off
      if(node.isPickup()) {
        // Pickup
        passengers.add(node.id);
        agent.sendPickup(id, node.id, node.loc);
      } else if(node.isDropoff()) {
        assert passengers.contains(node.id);
        // Dropoff
        passengers.remove(node.id);
        agent.sendDropoff(id, node.id, node.loc);
      }

      visited_index++;
    }

    time = new_time;
  }

  // Returns the current time
  public double getTime() {
    return time;
  }

  // Returns the location of the shuttle now
  public Loc getLoc() {
    return plan.getLoc(time);
  }

  // Returns the location of the shuttle at given time
  public Loc getLoc(double time) {
    return plan.getLoc(time);
  }

  // Returns the current plan
  public Plan getPlan() {
    return plan;
  }

  // Returns the id of this shuttle
  public String id() {
    return id;
  }

  public String toString() {
    return id;
  }

  // Resets the shuttle back to its initial state
  public void reset() {
    plan = new Plan(city, this, home, start_time, end_time);
    passengers = new HashSet<String>();
    offers = new HashMap<Request,Offer>();
    confirmed = new HashSet<Offer>();
    rejected = new HashSet<Offer>();
    time = 0.0;
    visited_index = 0;
    agent.initialize();
  }

  // Stats
  public int offered() {
    return offers.size();
  }
  public int confirmed() {
    return confirmed.size();
  }
  public int passengers() {
    return passengers.size();
  }
  public double distance() {
    return plan.driver_total_driving_distance(); // TODO
  }
  public double waittime() {
    return plan.total_wait_time(); // TODO
  }
  public double occupancy() {
    double driving_duration = plan.plan.get(plan.plan.size()-1).hit_time-plan.plan.get(0).hit_time;
    return plan.consumer_time_on_ride() / driving_duration;
  }
  public double payment() {
    return payment;
  }

  // Clean up
  public void terminate() {
    // Close connection to auction server
    agent.terminate();
  }

}

