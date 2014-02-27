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

package TMarket.ui;

import TMarket.common.*;

import java.io.*;


// java Batch
// java Batch [filename.sim]
// java Batch [consumers] [smartshuttles]

public class Batch {

  public static void main(String args[]) throws FileNotFoundException {

    if(args.length == 0) {
      // Default experimental setup

      Sim sim = new Sim("resources" + File.separator + "test.sim");
      sim.step();
      sim.terminate();
      System.out.println(sim.aserver.log.getReport());

    } else if(args.length == 1) {

      // Load from file
      String filename = args[0];

      // Initialize
      Sim sim = new Sim(filename);

/*
      System.out.println("DEBUG: A0 -> B2 = " + sim.city.time(sim.city.getLocByName("A0"), sim.city.getLocByName("B2")));
      System.out.println("DEBUG: A0 -> D1 = " + sim.city.time(sim.city.getLocByName("A0"), sim.city.getLocByName("D1")));
      System.out.println("DEBUG: B2 -> D1 = " + sim.city.time(sim.city.getLocByName("B2"), sim.city.getLocByName("D1")));
      System.out.println("DEBUG: A0 -> A0 = " + sim.city.time(sim.city.getLocByName("A0"), sim.city.getLocByName("A0")));
      System.out.println("DEBUG: A0 -> B1 = " + sim.city.time(sim.city.getLocByName("A0"), sim.city.getLocByName("B1")));
      System.out.println("DEBUG: A0 -> C2 = " + sim.city.time(sim.city.getLocByName("A0"), sim.city.getLocByName("C2")));
      System.out.println("DEBUG: B1 -> C2 = " + sim.city.time(sim.city.getLocByName("B1"), sim.city.getLocByName("C2")));
      System.out.println("DEBUG: B1 -> B2 = " + sim.city.time(sim.city.getLocByName("B1"), sim.city.getLocByName("B2")));
      System.out.println("DEBUG: B1 -> D1 = " + sim.city.time(sim.city.getLocByName("B1"), sim.city.getLocByName("D1")));
      System.out.println("DEBUG: C2 -> B2 = " + sim.city.time(sim.city.getLocByName("C2"), sim.city.getLocByName("B2")));
      System.out.println("DEBUG: C2 -> D1 = " + sim.city.time(sim.city.getLocByName("C2"), sim.city.getLocByName("D1")));
*/

      // Run simulation
      sim.play();

      // Close connections and whatnot
      sim.terminate();

      // Print report
      System.out.println(sim.aserver.log.getReport());

    } else if(args.length == 3) {
      int num_consumers = Integer.parseInt(args[0]);
      int num_smartshuttles = Integer.parseInt(args[2]);

      System.out.println("consumers smartshuttles rides cpm waittime");
      System.out.println(runSim(num_consumers, num_smartshuttles));

    } else {
      System.out.println("usage: java Batch");
      System.out.println("usage: java Batch [num_consumers] [num_smartshuttles]");
    }
  }

  // Runs a full day simulation, returns a string with the results
  public static String runSim(int num_consumers, int num_smartshuttles) {

    // Initialize
    City city = new City(16, 10); // new City("city.txt");
    Sim sim = new Sim(city, num_consumers, num_smartshuttles);

    // Run simulation
    sim.play();

    // Close connections and whatnot
    sim.terminate();

    // consumers smartshuttles rides cpm waittime;
    String results = num_consumers + " " + num_smartshuttles + " " + sim.aserver.log;

    return results;

  }



    // Output aggregate data:
/*
    System.out.println("AVERAGE RESULTS:");

    System.out.println("number of rides: " + num_rides);

    double cost_per_mi = total_cost / total_dist;
    System.out.println("cost per mile: " + cost_per_mi);

    double avg_waittime = total_waittime / num_rides;
    System.out.println("average wait time: " + avg_waittime);
*/

}

