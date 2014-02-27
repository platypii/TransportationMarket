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

package TMarket.auctionserver;

import TMarket.common.*;

import java.io.*;
import java.util.*;


// Class to capture statistics about the simulation
//  - what to record?
//  - what to output?
public class Log {

  private City city;

  public Stat waittime;
  public Stat marginal_distance; // Distance added by the consumer
  public Stat cost3;
  public Stat direct_distance; // Distance if consumer had a car

  public int requested;
  public int offered;
  public int confirmed;
  public int rejected;
  public int pickups;
  public int dropoffs;
  public int completed;

  public List<String> console;
  private StringBuilder consoleBuffer;


  // Logs with city info give more stats
  public Log(City city) {
    this.city = city;
    waittime = new Stat();
    marginal_distance = new Stat();
    cost3 = new Stat();
    console = new ArrayList<String>();
    consoleBuffer = new StringBuilder();
    if(city != null) {
      direct_distance = new Stat();
    }
  }

  // Resets the log
  public void reset() {
    waittime = new Stat();
    marginal_distance = new Stat();
    cost3 = new Stat();
    console = new ArrayList<String>();
    consoleBuffer = new StringBuilder();
    if(city != null) {
      direct_distance = new Stat();
    }
    requested = 0;
    offered = 0;
    confirmed = 0;
    rejected = 0;
    pickups = 0;
    dropoffs = 0;
    completed = 0;
  }

  // Logs a request
  public void logRequest(Request req) {
    requested++; // TODO: check for duplicate requests?
  }

  // Logs an offer
  public void logOffer(Offer offer) {
    if(offer != null) {
      offered++;
    } else {
      // TODO
    }
  }

  // Logs a confirmed offer
  public void logConfirm(Offer offer) {
    double waittime = offer.dropoff_time - offer.req.earliest_pickup_time;
    this.waittime.addSample(waittime);
    marginal_distance.addSample(offer.distance);
    cost3.addSample(offer.cost3);
    confirmed++;
 
    if(city != null) {
      double direct_dist = city.dist(offer.req.start, offer.req.end); // distance if consumer had a car
      direct_distance.addSample(direct_dist);
    }
  }

  // Logs a rejected offer
  public void logReject(Offer offer) {
    rejected++;
  }

  public void logPickup() {
    pickups++;
  }

  public void logDropoff() {
    dropoffs++;
    completed++;
  }

  public void logConsole(String line) {
    Debug.println(line);
    console.add(line);
    consoleBuffer.append(line);
    consoleBuffer.append('\n');
  }

  // Returns a summary of the logged stats
  public String toString() {
    // TODO: requested miles, total miles actually traveled

    double cost_per_mi = cost3.total() / direct_distance.total();
    double avg_waittime = waittime.mean();
   

// SHORT:
    // rides cpm waittime
    return confirmed + ", " + cost_per_mi + ", " + avg_waittime ;
  }

  // Returns a longer report of the logged stats
  public String getReport() {
    String report;

    report = "Requested rides: " + requested + "\n";
    report += "Offered rides: " + offered + "\n";
    report += "Confirmed rides: " + confirmed + "\n";
    report += "Rejected rides: " + rejected + "\n";
    report += "Completed rides: " + completed + "\n";
    report += "Wait time: " + waittime + "\n";
    report += "Marginal distance: " + marginal_distance + "\n";
    report += "Cost3: " + cost3 + "\n";
  

    if(city != null) {
      double cost_per_mi = cost3.total() / direct_distance.total();

      report += "Direct distance: " + direct_distance + "\n";
      report += "Cost per mile: " + String.format("%.3f", cost_per_mi) + "\n";
    }

    return report;
  }

  // Returns the console as one big String
  public String getConsole() {
    return consoleBuffer.toString();
  }

  // Saves a detailed log to the given filename
  public void saveLog(String filename) {
    try {
      PrintWriter out = new PrintWriter(new FileWriter(filename));
      out.println(getReport());
      out.println();
      for(String line : console) {
        out.println(line);
      }
    } catch(IOException e) {
      System.out.println("Error writing log file: " + filename);
      e.printStackTrace();
    }
  }

}
