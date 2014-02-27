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

import java.util.*;


// Implements an ordering based on request time
public class Request implements Comparable {

  public String consumer_id;
//  public Loc start;
//  public Loc end;
  public String start;
  public String end;
  public double earliest_pickup_time;
  public double latest_pickup_time;
  public double earliest_dropoff_time;
  public double latest_dropoff_time;
  public double request_time;
//  public double max_cost;


  public Request(String id, Loc start, Loc end, double earliest_pickup_time, double latest_pickup_time, double earliest_dropoff_time, double latest_dropoff_time, double request_time) {
    this(id, start.toString(), end.toString(), earliest_pickup_time, latest_pickup_time, earliest_dropoff_time, latest_dropoff_time, request_time);
  }

  public Request(String id, String start, String end, double earliest_pickup_time, double latest_pickup_time, double earliest_dropoff_time, double latest_dropoff_time, double request_time) {
    this.consumer_id = id;
    this.start = start;
    this.end = end;
    this.earliest_pickup_time = earliest_pickup_time;
    this.latest_pickup_time = latest_pickup_time;
    this.earliest_dropoff_time = earliest_dropoff_time;
    this.latest_dropoff_time = latest_dropoff_time;
    this.request_time = request_time;

    assert earliest_pickup_time <= latest_pickup_time;
    assert earliest_dropoff_time <= latest_dropoff_time;
    assert latest_pickup_time <= latest_dropoff_time;
  }

  // Parses the request string
  public Request(String line) {
    Scanner sc = new Scanner(line);
    String command = sc.next();
    assert command.equals("REQ") || command.equals("NOOFFER");
    this.consumer_id = sc.next();
    this.start = sc.next();
    this.end = sc.next();
    this.earliest_pickup_time = Time.parseTime(sc.next());
    this.latest_pickup_time = Time.parseTime(sc.next());
    this.earliest_dropoff_time = Time.parseTime(sc.next());
    this.latest_dropoff_time = Time.parseTime(sc.next());
    this.request_time = Time.parseTime(sc.next());
  }

  public String toString() {
    return consumer_id + " " + start + " " + end
      + " " + Time.formatTime(earliest_pickup_time)
      + " " + Time.formatTime(latest_pickup_time)
      + " " + Time.formatTime(earliest_dropoff_time)
      + " " + Time.formatTime(latest_dropoff_time)
      + " " + Time.formatTime(request_time);
  }

  public boolean equals(Object obj) {
    if(obj == null)
      return false;
    else
      return this.toString().equals(obj.toString());
  }

  public int hashCode() {
    return this.toString().hashCode();
  }

  public int compareTo(Object obj) {
    // sgn(this - that)
    Request that = (Request)obj;
    if(this.request_time < that.request_time)
      return -1;
    else if(this.request_time == that.request_time)
      return 0;
    else
      return 1;
  }

}