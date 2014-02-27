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

import TMarket.auctionserver.*;
import TMarket.common.*;

import java.util.*;
import java.net.*;
import java.io.*;


// A class used to represent the consumer's connection to the auction server
// Has an option to be initialized with a local AuctionServer object, to bypass the networking
public class ConsumerAgent implements Runnable {
  private Consumer consumer;
  public String id;

  // True iff connection is over IP
  boolean network_enabled;

  // Network connection to auction server
  Socket socket;
  PrintWriter aserver_out;
  BufferedReader aserver_in;

  // Local connection to auction server
  ClientConnection aserver_local;


  // Connects to the given auction server using local java function calls
  public ConsumerAgent(Consumer consumer, AuctionServer aserver) {
    this.consumer = consumer;
    id = consumer.id();
    network_enabled = false;
    aserver_local = new ClientConnection(aserver, this);
    initialize();
  }

  // Connects to the given auction server using IP
  public ConsumerAgent(Consumer consumer, String hostname, int port) {
    this.consumer = consumer;
    network_enabled = true;
    try{
      socket = new Socket(hostname, port);
      aserver_out = new PrintWriter(socket.getOutputStream(), true);
      aserver_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (IOException e) {
      System.err.println("Unable to connect to auction server: " + hostname);
    }

    new Thread(this, "Server listening thread").start();

    initialize();
  }

  // Announces the consumer to the auction server
  public void initialize() {
    send("C_INIT " + consumer.id());
  }

  // Send a request to the auction server
  public void sendRequest(Request req) {
    send("REQ " + req);
  }

  // Send a confirmation to the auction server
  public void sendConfirm(Offer offer) {
    send("CONFIRM " + offer);
  }

  // Send a rejection to the auction server
  public void sendReject(Offer offer) {
    send("REJECT " + offer);
  }

  public void send(String line) {
    if(network_enabled) {
      aserver_out.println(line);
    } else {
      aserver_local.receive(line);
    }
  }

  public void receive(String input) {
    Scanner sc = new Scanner(input);
    String command = sc.next();

    if(command.equals("OFFER")) {
      Offer offer = new Offer(input);
      consumer.receiveOffer(offer);
    } else if(command.equals("NOOFFER")) {
      Request req = new Request(input);
      consumer.receiveNoOffer(req);
    } else if(command.equals("PICKUP")) {
      String shuttle_id = sc.next();
      String consumer_id = sc.next();
      String loc = sc.next();
      consumer.receivePickup(shuttle_id, consumer_id, loc);
    } else if(command.equals("DROPOFF")) {
      String shuttle_id = sc.next();
      String consumer_id = sc.next();
      String loc = sc.next();
      consumer.receiveDropoff(shuttle_id, consumer_id, loc);
    } else if(command.equals("TICK")) {
      int new_time = Time.parseTime(sc.next());
      consumer.step(new_time);
    } else if(command.equals("QUIT")) {
      running = false;
    } else {
      Debug.println("["+consumer.id()+"] Invalid input: " + input);
    }
  }

  // Process input from the auction server
  private boolean running;
  public void run() {
    String input;

    assert network_enabled;

    try {
      running = true;
      while(running && (input = aserver_in.readLine()) != null) {
        receive(input);
      }

      aserver_out.println("QUIT");
      aserver_in.close();
      aserver_out.close();
    } catch(IOException e) {
      Debug.println("["+consumer.id()+"] Connection to server failed");
    }
  }

  public void terminate() {
    if(network_enabled) {
      try {
        aserver_out.println("QUIT");
        aserver_in.close();
        aserver_out.close();
        socket.close();
      } catch(IOException e) {
        Debug.println("["+consumer.id()+"] Failed to close connection");
      }
    }
  }

}