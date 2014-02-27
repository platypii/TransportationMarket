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
import TMarket.shuttle.*;
import TMarket.consumer.*;
import java.util.*;
import java.net.*;
import java.io.*;


public class ClientConnection implements Runnable {
  private AuctionServer aserver;

  private boolean network_enabled;

  // Network enabled:
  private Socket client_socket;
  private BufferedReader client_in;
  private PrintWriter client_out;

  // Network disabled:
  private ShuttleAgent shuttle; // Shuttle only
  private ConsumerAgent consumer; // Consumer only

  // Connection type: shuttle, consumer, or none (no INIT received yet)
  public enum ConnType {NONE, SHUTTLE, CONSUMER}
  public ConnType type;

  CState state;

  String id;


  // Network connection to one consumer or shuttle
  public ClientConnection(AuctionServer aserver, Socket client_socket) {
    this.aserver = aserver;
    this.network_enabled = true;
    this.client_socket = client_socket;
    try {
      client_in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
      client_out = new PrintWriter(client_socket.getOutputStream(), true);
    } catch(IOException e1) {
      Debug.println("Error opening connection. " + e1);
      e1.printStackTrace();
      try {
        client_socket.close();
      } catch(IOException e2) {
        e2.printStackTrace();
      }
      return;
    }

    this.type = ConnType.NONE;
    this.state = CState.INIT_WAIT;
    this.id = "";

    new Thread(this, "Client listening thread").start();
  }

  // Local (non-network) connection to a consumer
  public ClientConnection(AuctionServer aserver, ConsumerAgent consumer) {
    this.aserver = aserver;
    this.network_enabled = false;
    this.type = ConnType.CONSUMER;
    this.id = consumer.id;
    this.consumer = consumer;
  }

  // Local (non-network) connection to a shuttle
  public ClientConnection(AuctionServer aserver, ShuttleAgent shuttle) {
    this.aserver = aserver;
    this.network_enabled = false;
    this.type = ConnType.SHUTTLE;
    this.id = shuttle.id;
    this.shuttle = shuttle;
  }

  // Send a request to a shuttle
  public void sendRequest(Request req) {
    assert type == ConnType.SHUTTLE;
    send("REQ " + req);
  }

  // Send an offer to a consumer
  public void sendOffer(Offer offer) {
    assert type == ConnType.CONSUMER;
    send("OFFER " + offer);
  }

  // Send an offer to a consumer
  public void sendNoOffer(Request req) {
    assert type == ConnType.CONSUMER;
    send("NOOFFER " + req);
  }

  // Send a confirmation to a shuttle
  public void sendConfirm(Offer offer) {
    assert type == ConnType.SHUTTLE;
    send("CONFIRM " + offer);
  }

  // Send a rejection to a shuttle
  public void sendReject(Offer offer) {
    assert type == ConnType.SHUTTLE;
    send("REJECT " + offer);
  }

  // Send a pickup notice to a consumer
  public void sendPickup(String shuttle_id, String consumer_id, String loc) {
    assert type == ConnType.CONSUMER;
    send("PICKUP " + shuttle_id + " " + consumer_id + " " + loc);
  }

  // Send a dropoff notice to a consumer
  public void sendDropoff(String shuttle_id, String consumer_id, String loc) {
    assert type == ConnType.CONSUMER;
    send("DROPOFF " + shuttle_id + " " + consumer_id + " " + loc);
  }

  // Send a time tick to a consumer or shuttle
  public void sendTick(int new_time) {
    assert type == ConnType.SHUTTLE || type == ConnType.CONSUMER;
    send("TICK " + Time.formatTime(new_time));
  }

  // Loop to process network input
  public void run() {
    String input;

    try {
      while((input = client_in.readLine()) != null) {
        receive(input);
      }

      // close streams and connections
      client_in.close();
      client_out.close();
      client_socket.close();
    } catch(IOException e) {
      Debug.println("["+id+"] Connection to client failed");
      e.printStackTrace();
    }
  }

  // Sends a command to the client
  public void send(String line) {
    aserver.log.logConsole("[aserver->"+id+"] " + line);
    if(network_enabled) {
      client_out.println(line);
    } else {
      if(type == ConnType.SHUTTLE) {
        shuttle.receive(line);
      } else if(type == ConnType.CONSUMER) {
        consumer.receive(line);
      }
    }
  }

  // Process input to the auction server
  public void receive(String input) {
    Scanner sc = new Scanner(input);
    String command = sc.next();

    aserver.log.logConsole("["+id+"->aserver] " + input);

    if(command.equals("QUIT")) {
      return;

      // INITIALIZATION:
    } else if(command.equals("C_INIT")) {
      assert type == ConnType.NONE || type == ConnType.CONSUMER;
      type = ConnType.CONSUMER;
      id = sc.next();
      aserver.initConsumer(this);
    } else if(command.equals("S_INIT")) {
      assert type == ConnType.NONE || type == ConnType.SHUTTLE;
      type = ConnType.SHUTTLE;
      id = sc.next();
      aserver.initShuttle(this);

      // CONSUMERS:
    } else if(command.equals("REQ")) {
      assert type == ConnType.CONSUMER;
      Request req = new Request(input);
      aserver.receiveRequest(req);
    } else if(command.equals("CONFIRM")) {
      assert type == ConnType.CONSUMER;
      Offer offer = new Offer(input);
      aserver.receiveConfirm(offer);
    } else if(command.equals("REJECT")) {
      assert type == ConnType.CONSUMER;
      Offer offer = new Offer(input);
      aserver.receiveReject(offer);

      // SHUTTLES:
    } else if(command.equals("OFFER")) {
      assert type == ConnType.SHUTTLE;
      Offer offer = new Offer(input);
      aserver.receiveOffer(offer);
    } else if(command.equals("NOOFFER")) {
      assert type == ConnType.SHUTTLE;
      Offer offer = new Offer(input);
      aserver.receiveNoOffer(offer.req, offer.shuttle_id);
    } else if(command.equals("PICKUP")) {
      assert type == ConnType.SHUTTLE;
      String shuttle_id = sc.next();
      String consumer_id = sc.next();
      String loc = sc.next();
      aserver.receivePickup(shuttle_id, consumer_id, loc);
    } else if(command.equals("DROPOFF")) {
      assert type == ConnType.SHUTTLE;
      String shuttle_id = sc.next();
      String consumer_id = sc.next();
      String loc = sc.next();
      aserver.receiveDropoff(shuttle_id, consumer_id, loc);

    } else {
      // Invalid input
      Debug.println("Received invalid command: " + command);
    }
  }

  // Terminate the client
  public void terminate() {
    if(network_enabled) {
      send("QUIT");
    } else if(type == ConnType.CONSUMER) {
      consumer.terminate();
    } else if(type == ConnType.SHUTTLE) {
      shuttle.terminate();
    }
  }
}
