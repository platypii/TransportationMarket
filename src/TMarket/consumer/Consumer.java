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

package TMarket.consumer;

import TMarket.common.*;
import TMarket.auctionserver.*;


public class Consumer {
  private String id;

  // Connection to auction server
  private ConsumerAgent agent;

  public Loc start;
  public Loc end;
  public double request_time;
  public double earliest_pickup_time;
  public double latest_pickup_time;
  public double earliest_dropoff_time;
  public double latest_dropoff_time;
 
  
  public Loc curr; // null during DROPOFF_WAIT
  public Offer confirmed_offer; // non-null during PICKUP_WAIT and DROPOFF_WAIT

  private enum State {IDLE, OFFER_WAIT, PICKUP_WAIT, DROPOFF_WAIT}
  private State state;
  private int time;


  // Create a new Consumer (local)
  public Consumer(AuctionServer aserver, String id, Loc start, Loc end, double request_time, double earliest_pickup_time, double latest_pickup_time, double earliest_dropoff_time, double latest_dropoff_time) {
    this.id = id;

    this.request_time = request_time;
    this.earliest_pickup_time = earliest_pickup_time; 
    this.latest_pickup_time = latest_pickup_time;
    this.earliest_dropoff_time = earliest_dropoff_time;
    this.latest_dropoff_time = latest_dropoff_time;
    this.start = start;
    this.end = end;

    this.curr = start;
    this.confirmed_offer = null;
    this.state = State.IDLE;
    this.time = 0;

//    Debug.println("new Consumer(" + start + "," + end + ")");

    // Initialize auction server connection
    this.agent = new ConsumerAgent(this, aserver);
  }

  // Create a new Consumer (networked)
  public Consumer(String id, Loc start, Loc end, double request_time, double earliest_pickup_time, double latest_pickup_time, double earliest_dropoff_time, double latest_dropoff_time) {
    this.id = id;

    this.request_time = request_time;
    this.earliest_pickup_time = earliest_pickup_time; 
    this.latest_pickup_time = latest_pickup_time;
    this.earliest_dropoff_time = earliest_dropoff_time;
    this.latest_dropoff_time = latest_dropoff_time;
    this.start = start;
    this.end = end;

    this.curr = start;
    this.confirmed_offer = null;
    this.state = State.IDLE;
    this.time = 0;

//    Debug.println("new Consumer(" + start + "," + end + ")");

    // Initialize auction server connection
    this.agent = new ConsumerAgent(this, "localhost", 1337);
  }

  // Called to broadcast a request to the auction server
  public void makeRequest(Request req) {
//    Debug.println("Consumer.makeRequest(" + req + ")");

    assert state == State.IDLE || state == State.OFFER_WAIT;
    // Only make request once
    if(state == State.IDLE) {
      state = State.OFFER_WAIT;
      agent.sendRequest(req);
    }
  }

  // Called when consumer receives an offer from the auction server
  public void receiveOffer(Offer offer) {
    assert state == State.OFFER_WAIT || state == State.PICKUP_WAIT;

    // TODO: Collect all offers, and choose best one

    // Confirm best offer so far
    if(confirmed_offer == null) {
      assert state == State.OFFER_WAIT;

      // Confirm first offer
      agent.sendConfirm(offer);
      confirmed_offer = offer;
      state = State.PICKUP_WAIT;      
    } else if(offer.cost3 < confirmed_offer.cost3) {
      assert state == State.PICKUP_WAIT;

      // Confirm improved offer
      agent.sendConfirm(offer);
      // Reject old offer
      agent.sendReject(confirmed_offer);
      confirmed_offer = offer;
      state = State.PICKUP_WAIT;
    } else {
      assert state == State.PICKUP_WAIT;
    }
  }

  public void receiveNoOffer(Request req) {
    // TODO
  }

  // Called when consumer is picked up by a shuttle
  public void receivePickup(String shuttle_id, String consumer_id, String loc) {
    assert shuttle_id.equals(confirmed_offer.shuttle_id);
    assert consumer_id.equals(id);
    assert start.equals(loc);
    assert state == State.PICKUP_WAIT;

    state = State.DROPOFF_WAIT;
    curr = null;
  }

  // Called when consumer is picked up by a shuttle
  public void receiveDropoff(String shuttle_id, String consumer_id, String loc) {
    assert shuttle_id.equals(confirmed_offer.shuttle_id);
    assert consumer_id.equals(id);
    assert end.equals(loc);
    assert state == State.DROPOFF_WAIT;

    state = State.IDLE;
    curr = end;
   // confirmed_offer = null;
  }

  public void step(int new_time) {
    assert time < new_time;

    if(curr != end && confirmed_offer == null &&
       time <= request_time && request_time < new_time &&
       time < latest_pickup_time) {

      Request req = new Request(id, curr, end, earliest_pickup_time, latest_pickup_time, earliest_dropoff_time, latest_dropoff_time, request_time);
      makeRequest(req);
    }

    time = new_time;
  }

  // Returns the id of this consumer
  public String id() {
    return id;
  }

  public String toString() {
    return id;
  }

  // Resets the consumer back to its initial state
  public void reset() {
    curr = start;
    confirmed_offer = null;
    state = State.IDLE;
    time = 0;
    agent.initialize();
  }

  // Clean up
  public void terminate() {
    // Close connection to auction server
    agent.terminate();
  }

}


