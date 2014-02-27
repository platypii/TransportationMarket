/*
 * The Transportation Market
 *
 * Copyright 2011 Kenny Daniel
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

package TMarket.common;

import TMarket.auctionserver.*;
import TMarket.consumer.*;
import TMarket.shuttle.*;

import java.util.*;
import java.io.*;


public class Sim {
  public City city;

  public AuctionServer aserver;
  public int time;

  public Random rand = new Random();

  // Participants:
  public List<Shuttle> shuttles;
  public List<Consumer> consumers;
  private int min_time = 0;

  public HashMap<String,Shuttle> shuttles_by_id;

  // Constants:
  public static final int gas_price = 1; // dollars per mile

  private static final boolean network_enabled = false;


  // Simulation loaded from file
  public Sim(String filename) {
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));
      String path = filename.substring(0, filename.lastIndexOf(File.separator));
      loadFile(in, path);
    } catch(FileNotFoundException e) {
      System.out.println(e);
    }
  }
  public Sim(File file) {
    try {
      BufferedReader in = new BufferedReader(new FileReader(file));
      String path = file.getPath();
      loadFile(in, path);
    } catch(FileNotFoundException e) {
      System.out.println(e);
    }
  }

  // Simulation loaded from file
  private void loadFile(BufferedReader in, String path) {
    this.min_time = Integer.MAX_VALUE; // find the minimum
    this.shuttles = new ArrayList<Shuttle>();
    this.consumers = new ArrayList<Consumer>();
    this.shuttles_by_id = new HashMap<String,Shuttle>();

    try {
      String line;
      // Read file line by line
      while((line = in.readLine()) != null) {
        if(line.length() > 0 && line.charAt(0) != '%') {
          // Non-empty, uncommented line
          Scanner sc = new Scanner(line);
          String command = sc.next();

          if(command.equalsIgnoreCase("CITY")) {
            assert city == null; // No multiple city declarations
            String city_filename = path + File.separator + sc.next();
            this.city = new City(city_filename);
            this.aserver = new AuctionServer(city, network_enabled);
          } else if(command.equalsIgnoreCase("GRID")) {
            // "GRID 10 16" means a 10x16 grid
            assert city == null; // No multiple city declarations
            int rows = sc.nextInt();
            int cols = sc.nextInt();
            this.city = new City(rows, cols);
            this.aserver = new AuctionServer(city, network_enabled);

          } else if(command.equalsIgnoreCase("SHUTTLE")) {
            assert city != null; // City must be declared before shuttles
            String id = sc.next();
            Loc home = city.getLocByName(sc.next());
            double start_time = Time.parseTime(sc.next());
            double end_time = Time.parseTime(sc.next());
            assert start_time <= end_time;
            min_time = Math.min(min_time, (int)start_time);

            Shuttle shuttle;
            if(network_enabled)
              shuttle = new SmartShuttle(city, id, home, start_time, end_time);
            else
              shuttle = new SmartShuttle(aserver, city, id, home, start_time, end_time);
            shuttles.add(shuttle);
            shuttles_by_id.put(shuttle.id(), shuttle);
          } else if(command.equalsIgnoreCase("CONSUMER")) {
            assert city != null; // City must be declared before consumers
            String id = sc.next();
            Loc start = city.getLocByName(sc.next());
            Loc end = city.getLocByName(sc.next());
            double request_time = Time.parseTime(sc.next());
            double earliest_pickup_time = Time.parseTime(sc.next());
            double latest_pickup_time = Time.parseTime(sc.next());
            double earliest_dropoff_time = Time.parseTime(sc.next());
            double latest_dropoff_time = Time.parseTime(sc.next());
            min_time = Math.min(min_time, (int)earliest_pickup_time);

            Consumer consumer;
            if(network_enabled)
              consumer = new Consumer(id, start, end, request_time, earliest_pickup_time, latest_pickup_time, earliest_dropoff_time, latest_dropoff_time);
            else
              consumer = new Consumer(aserver, id, start, end, request_time, earliest_pickup_time, latest_pickup_time, earliest_dropoff_time, latest_dropoff_time);
            consumers.add(consumer);
          } else {
            System.out.println("Invalid command in simulation file: " + command);
          }
        }
      }
    } catch(IOException e) {
      System.out.println("Failed to open simulation file. " + e);
      System.exit(1);
    }

    time = min_time;
    assert city != null;
//    assert aserver != null;
  }

  // Simulation with a number of randomly generated consumers and shuttles
  public Sim(City city, int num_consumers, int num_smartshuttles) {
    this.city = city;
    this.aserver = new AuctionServer(city, network_enabled);
    this.time = 0;
    this.shuttles = new ArrayList<Shuttle>();
    this.consumers = new ArrayList<Consumer>();
    this.shuttles_by_id = new HashMap<String,Shuttle>();

    for(int i = 0; i < num_consumers; i++) {
      Consumer consumer = randomConsumer("consumer"+i);
      consumers.add(consumer);
    }

    for(int i = 0; i < num_smartshuttles; i++) {
      Loc home = city.randomLoc();
      double start_time = rand.nextInt(Time.parseTime("16:00")); // Random time between midnight and 4pm
      double end_time = start_time + Time.parseTime("8:00"); // 8 hour shift
      Shuttle shuttle;
      if(network_enabled)
        shuttle = new SmartShuttle(city, "smartshuttle"+i, home, start_time, end_time);
      else
        shuttle = new SmartShuttle(aserver, city, "smartshuttle"+i, home, start_time, end_time);
      shuttles.add(shuttle);
      shuttles_by_id.put(shuttle.id(), shuttle);
    }

    // DEBUG:
//    shuttles.get(0).receiveRequest(new Request(city, "REQ consumer0 A1 C4 06:00 06:30 06:00 07:00"));

  }

  private Consumer randomConsumer(String id) {
    double request_time = 0; // TODO: Online requests (worst case, request_time = earliest_pickup_time)
    double earliest_pickup_time = rand.nextInt(24 * 60 - 90); // Minutes since midnight
    double latest_pickup_time = earliest_pickup_time + 30;
    double earliest_dropoff_time = earliest_pickup_time;
    double latest_dropoff_time = latest_pickup_time + 60;
    Loc start = city.randomLoc();
    Loc end;
    do {
      end = city.randomLoc(); // Destination
    } while(end == start);
    if(network_enabled)
      return new Consumer(id, start, end, request_time, earliest_pickup_time, latest_pickup_time, earliest_dropoff_time, latest_dropoff_time);
    else
      return new Consumer(aserver, id, start, end, request_time, earliest_pickup_time, latest_pickup_time, earliest_dropoff_time, latest_dropoff_time);
  }

  // Runs the whole simulation
  public void play() {
    while(time < 24*60) {
      step();
    }
  }

  // Steps time forward by one unit
  public void step() {

    // Step auction server (which steps consumer and shuttles)
//    aserver.step(time);

    time++;

    // Step consumers
    for(Consumer consumer : consumers)
      consumer.step(time);

    // Step shuttles
    for(Shuttle shuttle : shuttles)
      shuttle.step(time);

    if(network_enabled) {
      try {
        // TODO: Deal with asyncronicity properly
        Thread.yield();
        Thread.sleep(50); // milliseconds
      } catch(Exception e) {}
    }

  }

  // Returns a string representing the output of the simulation
  public String getReport() {
    int offered = 0;
    int confirmed = 0;
    int passengers = 0;
    double distance = 0.0;
    double waittime = 0.0;
    double occupancy = 0.0;

    for(int i = 0; i < shuttles.size(); i++) {
      Shuttle sh = shuttles.get(i);

      // TODO: get offers, confirmed, passengers, etc from the log instead of the shuttle
      if(sh instanceof SmartShuttle) {

        offered += sh.offered();
        confirmed += sh.confirmed();
        passengers += sh.passengers();
        distance += sh.distance();
        waittime += sh.waittime();
        occupancy += sh.occupancy();
      }
    }

    return aserver.log.toString() + ", " + distance + ", " + occupancy;
  }

  // Resets the simulation back to its initial state
  public void reset() {
    time = min_time;
    aserver.reset();
    for(Consumer consumer : consumers) {
      consumer.reset();
    }
    for(Shuttle shuttle : shuttles) {
      shuttle.reset();
    }
  }

  // Ends the simulation and kills connections and threads
  public void terminate() {
    // Terminate Shuttles
    for(Shuttle sh : shuttles) {
      sh.terminate();
    }

    // Terminate Consumers
    for(Consumer c : consumers) {
      c.terminate();
    }

    // Terminate Auction Server
    aserver.terminate();

  }

  // Write simulation out to file
  public void saveSim(String filename) {
    try {
      PrintWriter out = new PrintWriter(new FileWriter(filename));

      // TODO: Print city
      out.println(city.city_rep);
      out.println();

      // Print shuttles
      out.println("% SHUTTLE  id home start_time end_time");
      for(Shuttle sh : shuttles) {
        Plan plan = sh.getPlan();
        Node first = plan.plan.get(0);
        Node last = plan.plan.get(plan.plan.size()-1);
        out.println("SHUTTLE\t" +
                    sh.id() + "\t" +
                    first.loc + "\t" +
                    Time.formatTime(first.hit_time) + "\t" +
                    Time.formatTime(last.hit_time));
      }
      out.println();

      // Print consumers
      out.println("% CONSUMER id start end request_time earliest_pickup latest_pickup earliest_dropoff latest_dropoff");
      for(Consumer con : consumers) {
        out.println("CONSUMER\t" +
                    con.id() + "\t" +
                    con.start + "\t" +
                    con.end + "\t" +
                    Time.formatTime(con.request_time) + "\t" +
                    Time.formatTime(con.earliest_pickup_time) + "\t" +
                    Time.formatTime(con.latest_pickup_time) + "\t" +
                    Time.formatTime(con.earliest_dropoff_time) + "\t" +
                    Time.formatTime(con.latest_dropoff_time));
      }

       out.close();

    } catch(IOException e) {
      System.out.println("Error writing sim file: " + filename);
      e.printStackTrace();
    }
  }

}