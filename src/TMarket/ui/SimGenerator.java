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

import java.util.*;
import java.io.*;


// A class to generate simulation files
public class SimGenerator {

  // Defaults:
  static int num_instances = 50; // The number of instances to generate
  static int num_shuttles = 5;
  static int num_consumers = 50;

  static int width = 10;
  static int height = 10;

  
  public static void main(String args[]) throws IOException {    
    Random rand = new Random();

    if(args.length == 0) {
      // Use defaults
    } else if(args.length == 1) {
      num_instances = Integer.parseInt(args[0]);
    } else if(args.length == 3) {
      num_instances = Integer.parseInt(args[0]);
      num_shuttles = Integer.parseInt(args[1]);
      num_consumers = Integer.parseInt(args[2]);
    } else {
      System.out.println("usage: java SimGenerator [num_instances] [num_shuttles] [num_consumers]");
      System.out.println("usage: java SimGenerator [num_instances]");
      System.out.println("usage: java SimGenerator");
      System.exit(0);
    }

    for(int i = 0; i < num_instances; i++) {
      String filename = "resources/generated/test"+i+".sim";
      PrintWriter out = new PrintWriter(new FileWriter(filename));

      // City:
      City city = new City(width, height);
      out.println("GRID " + width + " " + height);
      out.println();

      // Shuttles:
      out.println("% SHUTTLE  id home start_time end_time");
      for(int j = 0; j < num_shuttles; j++) {
        Loc home = city.uniformRandomLoc();
//    int start_time = Time.parseTime("12:30");
//    int start_time = 12*60 + 30; 
        int start_time = 0;
//    int end_time = Math.min(start_time + 8*60, 24*60-1);
        int end_time = 24*60-1;

       // int start_time=rand.nextInt(24*60-1);
       // int end_time=Math.min(24*60-2,start_time + 120 + rand.nextInt(6*60));
        
        out.println("SHUTTLE shuttle"+j+" "+home+" "+Time.formatTime(start_time)+" "+Time.formatTime(end_time));
      }

      out.println();
      //CONSUMER consumer1 A1 A2 00:05 00:00 00:30 00:00 00:50
      // Consumers:
      out.println("% CONSUMER id start end request_time earliest_pickup latest_pickup earliest_dropoff latest_dropoff");
      //pick up time stationary
   /*   for(int j = 0; j < num_consumers; j++) {
        // rand.nextInt(x) = random int between 0 and x
        Loc start = city.uniformRandomLoc();
        Loc end;
        do {
          end = city.uniformRandomLoc();
        } while(start.equals(end));
        int earliest_pickup = rand.nextInt(24*60-1);
        int request_time = Math.max(0,earliest_pickup-rand.nextInt(2*60));
        int latest_pickup = Math.min(24*60-2,earliest_pickup + 5+ rand.nextInt(25));
        //int earliest_dropoff = Math.max(request_time,rand.nextInt(24*60));
         int latest_dropoff = Math.min(24*60-1,latest_pickup + rand.nextInt(24*60-1-latest_pickup));

        out.println("CONSUMER consumer"+j+" "+start+" " +end+ " "+Time.formatTime(request_time)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_pickup)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_dropoff));
      }
     */
      //pick up time non-stationary
      //0:00-6:00 5 requests
      for(int j = 0; j < 5; j++) {
        // rand.nextInt(x) = random int between 0 and x
        Loc start = city.uniformRandomLoc();
        Loc end;
        do {
          end = city.uniformRandomLoc();
        } while(start.equals(end));
        int earliest_pickup = rand.nextInt(6*60-1);
        int request_time = Math.max(0,earliest_pickup-rand.nextInt(2*60));
        int latest_pickup = Math.min(24*60-2,earliest_pickup + 5+ rand.nextInt(25));
        //int earliest_dropoff = Math.max(request_time,rand.nextInt(24*60));
         int latest_dropoff = Math.min(24*60-1,latest_pickup + rand.nextInt(24*60-1-latest_pickup));

        out.println("CONSUMER consumer"+j+" "+start+" " +end+ " "+Time.formatTime(request_time)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_pickup)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_dropoff));
      }
      //6-8 4
      for(int j = 5; j < 9; j++) {
        // rand.nextInt(x) = random int between 0 and x
        Loc start = city.uniformRandomLoc();
        Loc end;
        do {
          end = city.uniformRandomLoc();
        } while(start.equals(end));
        int earliest_pickup = 6*60+rand.nextInt(2*60-1);
        int request_time = Math.max(0,earliest_pickup-rand.nextInt(2*60));
        int latest_pickup = Math.min(24*60-2,earliest_pickup + 5+ rand.nextInt(25));
        //int earliest_dropoff = Math.max(request_time,rand.nextInt(24*60));
         int latest_dropoff = Math.min(24*60-1,latest_pickup + rand.nextInt(24*60-1-latest_pickup));

        out.println("CONSUMER consumer"+j+" "+start+" " +end+ " "+Time.formatTime(request_time)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_pickup)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_dropoff));
      }
      //8-10 10
      for(int j = 9; j < 19; j++) {
        // rand.nextInt(x) = random int between 0 and x
        Loc start = city.uniformRandomLoc();
        Loc end;
        do {
          end = city.uniformRandomLoc();
        } while(start.equals(end));
        int earliest_pickup = 8*60+rand.nextInt(2*60-1);
        int request_time = Math.max(0,earliest_pickup-rand.nextInt(2*60));
        int latest_pickup = Math.min(24*60-2,earliest_pickup + 5+ rand.nextInt(25));
        //int earliest_dropoff = Math.max(request_time,rand.nextInt(24*60));
         int latest_dropoff = Math.min(24*60-1,latest_pickup + rand.nextInt(24*60-1-latest_pickup));

        out.println("CONSUMER consumer"+j+" "+start+" " +end+ " "+Time.formatTime(request_time)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_pickup)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_dropoff));
      }
      //10-12 4
      for(int j = 19; j < 23; j++) {
        // rand.nextInt(x) = random int between 0 and x
        Loc start = city.uniformRandomLoc();
        Loc end;
        do {
          end = city.uniformRandomLoc();
        } while(start.equals(end));
        int earliest_pickup = 10*60+rand.nextInt(2*60-1);
        int request_time = Math.max(0,earliest_pickup-rand.nextInt(2*60));
        int latest_pickup = Math.min(24*60-2,earliest_pickup + 5+ rand.nextInt(25));
        //int earliest_dropoff = Math.max(request_time,rand.nextInt(24*60));
         int latest_dropoff = Math.min(24*60-1,latest_pickup + rand.nextInt(24*60-1-latest_pickup));

        out.println("CONSUMER consumer"+j+" "+start+" " +end+ " "+Time.formatTime(request_time)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_pickup)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_dropoff));
      }
      //12-14 4
      for(int j = 23; j < 27; j++) {
        // rand.nextInt(x) = random int between 0 and x
        Loc start = city.uniformRandomLoc();
        Loc end;
        do {
          end = city.uniformRandomLoc();
        } while(start.equals(end));
        int earliest_pickup = 12*60+rand.nextInt(2*60-1);
        int request_time = Math.max(0,earliest_pickup-rand.nextInt(2*60));
        int latest_pickup = Math.min(24*60-2,earliest_pickup + 5+ rand.nextInt(25));
        //int earliest_dropoff = Math.max(request_time,rand.nextInt(24*60));
         int latest_dropoff = Math.min(24*60-1,latest_pickup + rand.nextInt(24*60-1-latest_pickup));

        out.println("CONSUMER consumer"+j+" "+start+" " +end+ " "+Time.formatTime(request_time)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_pickup)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_dropoff));
      }
      //14-16 4
      for(int j = 27; j < 31; j++) {
        // rand.nextInt(x) = random int between 0 and x
        Loc start = city.uniformRandomLoc();
        Loc end;
        do {
          end = city.uniformRandomLoc();
        } while(start.equals(end));
        int earliest_pickup = 14*60+rand.nextInt(2*60-1);
        int request_time = Math.max(0,earliest_pickup-rand.nextInt(2*60));
        int latest_pickup = Math.min(24*60-2,earliest_pickup + 5+ rand.nextInt(25));
        //int earliest_dropoff = Math.max(request_time,rand.nextInt(24*60));
         int latest_dropoff = Math.min(24*60-1,latest_pickup + rand.nextInt(24*60-1-latest_pickup));

        out.println("CONSUMER consumer"+j+" "+start+" " +end+ " "+Time.formatTime(request_time)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_pickup)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_dropoff));
      }
      //16-18 10
      for(int j = 31; j < 41; j++) {
        // rand.nextInt(x) = random int between 0 and x
        Loc start = city.uniformRandomLoc();
        Loc end;
        do {
          end = city.uniformRandomLoc();
        } while(start.equals(end));
        int earliest_pickup = 16*60+rand.nextInt(2*60-1);
        int request_time = Math.max(0,earliest_pickup-rand.nextInt(2*60));
        int latest_pickup = Math.min(24*60-2,earliest_pickup + 5+ rand.nextInt(25));
        //int earliest_dropoff = Math.max(request_time,rand.nextInt(24*60));
         int latest_dropoff = Math.min(24*60-1,latest_pickup + rand.nextInt(24*60-1-latest_pickup));

        out.println("CONSUMER consumer"+j+" "+start+" " +end+ " "+Time.formatTime(request_time)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_pickup)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_dropoff));
      }
      //18-20 4
      for(int j = 41; j < 45; j++) {
        // rand.nextInt(x) = random int between 0 and x
        Loc start = city.uniformRandomLoc();
        Loc end;
        do {
          end = city.uniformRandomLoc();
        } while(start.equals(end));
        int earliest_pickup = 18*60+rand.nextInt(2*60-1);
        int request_time = Math.max(0,earliest_pickup-rand.nextInt(2*60));
        int latest_pickup = Math.min(24*60-2,earliest_pickup + 5+ rand.nextInt(25));
        //int earliest_dropoff = Math.max(request_time,rand.nextInt(24*60));
         int latest_dropoff = Math.min(24*60-1,latest_pickup + rand.nextInt(24*60-1-latest_pickup));

        out.println("CONSUMER consumer"+j+" "+start+" " +end+ " "+Time.formatTime(request_time)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_pickup)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_dropoff));
      }
      //20-24 5
      for(int j = 45; j < 50; j++) {
        // rand.nextInt(x) = random int between 0 and x
        Loc start = city.uniformRandomLoc();
        Loc end;
        do {
          end = city.uniformRandomLoc();
        } while(start.equals(end));
        int earliest_pickup = 20*60+rand.nextInt(4*60-1);
        int request_time = Math.max(0,earliest_pickup-rand.nextInt(2*60));
        int latest_pickup = Math.min(24*60-2,earliest_pickup + 5+ rand.nextInt(25));
        //int earliest_dropoff = Math.max(request_time,rand.nextInt(24*60));
         int latest_dropoff = Math.min(24*60-1,latest_pickup + rand.nextInt(24*60-1-latest_pickup));

        out.println("CONSUMER consumer"+j+" "+start+" " +end+ " "+Time.formatTime(request_time)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_pickup)+" " +Time.formatTime(earliest_pickup)+" "+Time.formatTime(latest_dropoff));
      }
      
      out.println();
    
      out.close(); 

      Debug.println("Generated sim file: " + filename);
    }
  }
  
}
