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

package TMarket.shuttle;

import TMarket.auctionserver.*;
import TMarket.common.*;

import java.util.*;
import java.net.*;
import java.io.*;


// A class used to represent the shuttle's connection to the auction server
// Has an option to be initialized with a local AuctionServer object, to bypass the networking
public class ShuttleAgent implements Runnable {
  private Shuttle shuttle;
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
  public ShuttleAgent(Shuttle shuttle, AuctionServer aserver) {
    this.shuttle = shuttle;
    id = shuttle.id();
    network_enabled = false;
    aserver_local = new ClientConnection(aserver, this);
    initialize();
  }

  // Connects to the given auction server using IP
  public ShuttleAgent(Shuttle shuttle, String hostname, int port) {
    this.shuttle = shuttle;
    id = shuttle.id();
    network_enabled = true;
    try{
      socket = new Socket(hostname, port);
      aserver_out = new PrintWriter(socket.getOutputStream(), true);
      aserver_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    } catch (IOException e) {
      System.err.println("Unable to connect to host: " + hostname);
      System.exit(1);
    }

    new Thread(this, "Server listening thread").start();
    initialize();
  }

  // Announces the shuttle to the auction server
  public void initialize() {
    send("S_INIT " + shuttle.id());
  }

  // Send an offer to the auction server
  public void sendOffer(Offer offer) {
    send("OFFER " + offer);
  }

  // Send a no-offer to the auction server
  public void noOffer(Request req) {
    send("NOOFFER " + shuttle.id() + " " + req);
  }

  // Announce a pickup to the auction server
  public void sendPickup(String shuttle_id, String consumer_id, Loc loc) {
    send("PICKUP " + shuttle_id + " " + consumer_id + " " + loc);
  }

  // Announce a dropoff to the auction server
  public void sendDropoff(String shuttle_id, String consumer_id, Loc loc) {
    send("DROPOFF " + shuttle_id + " " + consumer_id + " " + loc);
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

    if(command.equals("REQ")) {
      Request req = new Request(input);
      shuttle.receiveRequest(req);
    } else if(command.equals("CONFIRM")) {
      Offer offer = new Offer(input);
      shuttle.receiveConfirm(offer);
    } else if(command.equals("REJECT")) {
      Offer offer = new Offer(input);
      shuttle.receiveReject(offer);
    } else if(command.equals("TICK")) {
      int new_time = Time.parseTime(sc.next());
      shuttle.step(new_time);
    } else if(command.equals("QUIT")) {
      terminate();
    } else {
      Debug.println("["+shuttle.id()+"] Invalid input: " + input);
    }
  }

  // Process input from the auction server
  private boolean running;
  public void run() {
    String input;

    try {
      running = true;
      while(running && (input = aserver_in.readLine()) != null) {
        receive(input);
      }
      aserver_in.close();
      aserver_out.close();
      Debug.println("["+shuttle.id()+"] Connection to server closed");
    } catch(IOException e) {
      Debug.println("["+shuttle.id()+"] Connection to server failed");
    }
  }

  public void terminate() {
    if(network_enabled) {
      running = false;
      try {
        aserver_out.println("QUIT");
        aserver_in.close();
        aserver_out.close();
        socket.close();
      } catch(IOException e) {
        Debug.println("["+shuttle.id()+"] Failed to close connection");
      }
    }
  }

}