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

import TMarket.shuttle.Plan;

import java.util.*;


public class Offer {

  public String shuttle_id;
  public Request req;
  public double pickup_time;
  public double dropoff_time;
  public double distance;
  public double cost3;

  public Plan plan; // Used to store plans associated with an offer


  public Offer(String shuttle_id, Request req, double pickup_time, double dropoff_time, double distance, double cost3) {
    this.shuttle_id = shuttle_id;
    this.req = req;
    this.pickup_time = pickup_time;
    this.dropoff_time = dropoff_time;
    this.distance = distance;
    this.cost3 = cost3;

    assert req.earliest_pickup_time <= pickup_time;
    assert pickup_time <= dropoff_time;
    assert dropoff_time <= req.latest_dropoff_time;
    assert 0.0 <= distance;
    assert 0.0 <= cost3;
  }

  // Parses the offer string
  public Offer(String line) {
    Scanner sc = new Scanner(line);
    String command = sc.next();
    assert command.equals("OFFER") || command.equals("CONFIRM") || command.equals("REJECT") || command.equals("NOOFFER");

    this.shuttle_id = sc.next();

    // Request portion:
    String consumer_id = sc.next();
    String start = sc.next();
    String end = sc.next();
    double earliest_pickup_time = Time.parseTime(sc.next());
    double latest_pickup_time = Time.parseTime(sc.next());
    double earliest_dropoff_time = Time.parseTime(sc.next());
    double latest_dropoff_time = Time.parseTime(sc.next());
    double request_time = Time.parseTime(sc.next());
    this.req = new Request(consumer_id, start, end, earliest_pickup_time, latest_pickup_time, earliest_dropoff_time, latest_dropoff_time, request_time);

    if(command.equals("NOOFFER")) {
      this.pickup_time = -1;
      this.dropoff_time = -1;
      this.distance = -1;
      this.cost3 = -1;
    } else {
      this.pickup_time = Time.parseTime(sc.next());
      this.dropoff_time = Time.parseTime(sc.next());
      this.distance = Double.parseDouble(sc.next());
      this.cost3 = Double.parseDouble(sc.next());
    }
  }

  public String toString() {
    return shuttle_id + " " + req + " " + Time.formatTime(pickup_time) + " " + Time.formatTime(dropoff_time) + " " + distance + " " + cost3;
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

}