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
import java.net.*;
import java.util.*;


// TODO: Log times


public class AuctionServer implements Runnable {
  private City city;
  public Log log;

  public CostSharing costSharing = new CostSharing();

  Map<String,ClientConnection> consumers = new HashMap<String,ClientConnection>();
  Map<String,ClientConnection> shuttles = new HashMap<String,ClientConnection>();

  private TreeMap<Request,RequestData> requests = new TreeMap<Request,RequestData>();
//  private List<Request> requests = new ArrayList<Request>();
//  private List<RequestData> requestData = new ArrayList<RequestData>();
//  private List<Offer> confirmed = new ArrayList<Offer>();

//  private Map<Request,List<Offer>> offers = new HashMap<Request,List<Offer>>();
//  private Map<Request,Offer> confirmed = new HashMap<Request,Offer>();
//  private Map<Request,Set<Offer>> rejected = new HashMap<Request,Set<Offer>>();

  // True iff connection is over IP
  boolean network_enabled;

  // Network connection
  private ServerSocket serverSocket;
  private static int port = 1337;

  private int time = 0; // Used only for logging


  public static void main(String args[]) {
    if(args.length == 0) {
      // Launch network AuctionServer with no city info
      AuctionServer aserver = new AuctionServer(null, true);
    } else if(args.length == 1) {
      // Launch network AuctionServer with city info (gives better stats)
      AuctionServer aserver = new AuctionServer(new City(args[0]), true);
    } else {
      System.out.println("Usage: java AuctionServer [city file]");
    }
  }

  // Run the auction server with a given log
  public AuctionServer(City city, boolean network_enabled) {
    this.city = city;
    this.log = new Log(city);

    this.network_enabled = network_enabled;
    if(network_enabled) {
      // Start listening thread
      new Thread(this, "Auction server thread").start();
    }
  }

  // Resets the AuctionServer
  public void reset() {
    log.reset();
    costSharing = new CostSharing();
    consumers = new HashMap<String,ClientConnection>();
    shuttles = new HashMap<String,ClientConnection>();
    requests = new TreeMap<Request,RequestData>();
  }

  // Adds a consumer to the auction server
  public void initConsumer(ClientConnection conn) {
    assert conn.type == ClientConnection.ConnType.CONSUMER;
    consumers.put(conn.id, conn);
    conn.state = CState.REQ_WAIT;
  }

  // Adds a shuttle to the auction server
  public void initShuttle(ClientConnection conn) {
    assert conn.type == ClientConnection.ConnType.SHUTTLE;
    shuttles.put(conn.id, conn);
    conn.state = CState.REQ_WAIT;
  }

  // Called when consumer makes a request
  public void receiveRequest(Request req) {
    ClientConnection consumer = consumers.get(req.consumer_id);
    assert consumer.state == CState.REQ_WAIT;
    consumer.state = CState.OFFER_WAIT;

    RequestData reqData = new RequestData(req);
    requests.put(req, reqData);

    // Broadcast request to shuttles
    reqData.num_forwarded = shuttles.size();
    for(ClientConnection shuttle : shuttles.values()) {
      assert shuttle.state == CState.REQ_WAIT || shuttle.state == CState.OFFER_WAIT || shuttle.state == CState.CONFIRM_WAIT; // TODO
      shuttle.state = CState.OFFER_WAIT;
      shuttle.sendRequest(req);
    }

    log.logRequest(req);
  }

  // Called when shuttle returns an offer
  public void receiveOffer(Offer offer) {
    assert offer != null;

    ClientConnection consumer = consumers.get(offer.req.consumer_id);
    ClientConnection shuttle = shuttles.get(offer.shuttle_id);

    assert consumer.state == CState.OFFER_WAIT || consumer.state == CState.CONFIRM_WAIT || consumer.state == CState.PICKUP_WAIT;

    assert shuttle.state == CState.OFFER_WAIT;

    consumer.state = CState.CONFIRM_WAIT;
    shuttle.state = CState.CONFIRM_WAIT;

    assert requests.containsKey(offer.req);
    RequestData reqData = requests.get(offer.req);
    reqData.offers.add(offer);
    reqData.num_offers++;

    if(reqData.num_offers >= reqData.num_forwarded) {
      chooseWinning(reqData);
    }

    log.logOffer(offer);
  }

  // Called when shuttle returns no offer
  public void receiveNoOffer(Request req, String shuttle_id) {
    assert requests.containsKey(req);
    RequestData reqData = requests.get(req);
    reqData.offers.add(null);
    reqData.num_offers++;

    // reqData should contain the number of requests sent
    if(reqData.num_offers == reqData.num_forwarded) {
      chooseWinning(reqData);
    }
  }

  // Choose the winning offer from a set of offers, and confirm it
  private void chooseWinning(RequestData reqData) {
    Offer best = null;
    for(Offer offer : reqData.offers) {
      if(offer != null && (best == null || offer.cost3 < best.cost3)) {
        best = offer;
      }
    }

    ClientConnection consumer = consumers.get(reqData.req.consumer_id);
//    ClientConnection shuttle = shuttles.get(best.shuttle_id);
    if(best == null) {
      // No offers
      consumer.sendNoOffer(reqData.req);
    } else {
      // Send best offer to consumer

      // Adjust price based on cost sharing:
      double alpha = alpha(reqData.req);
      double shared_cost = costSharing.peek(best.cost3, alpha);
      Offer offered = new Offer(best.shuttle_id, best.req, best.pickup_time,
                                best.dropoff_time, best.distance, shared_cost);

      reqData.winning = best;
      reqData.offered = offered;
      consumer.sendOffer(offered);
    }
  }

  // ALPHA:
  public double alpha(Request req) {
    return city.dist(req.start, req.end);
  }

  // Called when consumer confirms an offer
  public void receiveConfirm(Offer offer) {
    assert offer != null;
    assert requests.containsKey(offer.req); // request was made

    RequestData reqData = requests.get(offer.req);

    assert reqData.offered.equals(offer);
//    assert reqData.offers.contains(offer); // this offer was made
    assert !reqData.rejected.contains(offer); // Offer has not been reject yet
    assert reqData.confirmed == null; // no existing confirmed offer

    ClientConnection consumer = consumers.get(offer.req.consumer_id);
    ClientConnection shuttle = shuttles.get(offer.shuttle_id);
    assert consumer.state == CState.CONFIRM_WAIT;
    assert shuttle.state == CState.CONFIRM_WAIT;
    consumer.state = CState.PICKUP_WAIT;
    shuttle.state = CState.REQ_WAIT;

    reqData.confirmed = offer;
    costSharing.add(reqData.winning.cost3, alpha(offer.req));

    // Confirm ride to shuttle
    shuttle.sendConfirm(reqData.winning);

    // Let other shuttles know that they were rejected
    for(Offer other_offer : reqData.offers) {
      if(other_offer != null && !other_offer.equals(offer)) {
        ClientConnection other_shuttle = shuttles.get(other_offer.shuttle_id);
        other_shuttle.sendReject(other_offer);
        log.logReject(other_offer); // Rejects come from auction server not the consumer, so we have to log here.
      }
    }

    log.logConfirm(offer);
  }

  public void receiveReject(Offer offer) {
    assert offer != null;
    RequestData reqData = requests.get(offer.req);

    assert reqData.offers.contains(offer);

    ClientConnection consumer = consumers.get(offer.req.consumer_id);
    ClientConnection shuttle = shuttles.get(offer.shuttle_id);
    assert consumer.state == CState.CONFIRM_WAIT;
    assert shuttle.state == CState.CONFIRM_WAIT || shuttle.state == CState.REQ_WAIT;
//    consumer.state = CState.CONFIRM_WAIT;
    shuttle.state = CState.REQ_WAIT;

    // Reject ride from shuttle
    reqData.rejected.add(offer);
//    shuttle.sendReject(offer);

    // TODO: send out next best offer?

    log.logReject(offer);
  }

  public void receivePickup(String shuttle_id, String consumer_id, String loc) {
    // Rebroadcast to consumer
    ClientConnection consumer = consumers.get(consumer_id);

    // State
    if(consumer.state == CState.DROPOFF_WAIT)
      Debug.println("error: " + consumer_id + " already picked up.");
    assert consumer.state == CState.PICKUP_WAIT;
    consumer.state = CState.DROPOFF_WAIT;

    consumer.sendPickup(shuttle_id, consumer_id, loc);

    log.logPickup();
  }

  public void receiveDropoff(String shuttle_id, String consumer_id, String loc) {
    // Rebroadcast to consumer
    ClientConnection consumer = consumers.get(consumer_id);
    assert consumer.state == CState.DROPOFF_WAIT;
    consumer.state = CState.IDLE;

    consumer.sendDropoff(shuttle_id, consumer_id, loc);

    log.logDropoff();
  }

  // Called by the simulation when the clock ticks
  public void step(int new_time) {
//    Debug.println("AuctionServer.step("+new_time+")");
    assert time < new_time;

    for(ClientConnection consumer : consumers.values())
      consumer.sendTick(new_time);
    for(ClientConnection shuttle : shuttles.values())
      shuttle.sendTick(new_time);

    time = new_time;
  }

  // New process that waits for incoming connections
  private boolean running;
  public void run() {
    assert network_enabled;

//    Debug.println("[aserver] Waiting for connections");
    try {
      serverSocket = new ServerSocket(port);
      try {
        running = true;
        while(running) {
          Socket clientSocket = serverSocket.accept();
//        Debug.println("[aserver] Accepted a connection from: " + clientSocket.getInetAddress());
          ClientConnection conn = new ClientConnection(this, clientSocket);
          // TODO: store uninitialized connection?
        }
        serverSocket.close();
      } catch(IOException e) {
        Debug.println("[aserver] Server socket failed: "+e);
      }
    } catch(IOException e) {
      Debug.println("[aserver] Failed to start auction server: "+e);
    }
  }

  // Clean up
  public void terminate() {
    // TODO: Close client connections only for networked?
    for(ClientConnection consumer : consumers.values())
      consumer.terminate();
    for(ClientConnection shuttle : shuttles.values())
      shuttle.terminate();

    if(network_enabled) {
      // Close connection
      running = false;
      try {
        // TODO: Signal when terminated
        Thread.sleep(5000);
      } catch(InterruptedException e) {}
    }
  }

}

