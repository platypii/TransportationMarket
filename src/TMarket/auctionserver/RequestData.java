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

import java.util.*;


// Implements an ordering based on request time
public class RequestData implements Comparable<RequestData> {

  public Request req;
  public int num_forwarded = 0; // number of times the request was forwarded
  public List<Offer> offers = new ArrayList<Offer>();
  public int num_offers = 0;

  // Sent to consumer:
  public Offer winning; // the winning offer
  public Offer offered; // the shared-cost offer
  public Offer confirmed; // the confirmed offer
  public List<Offer> rejected = new ArrayList<Offer>(); // rejected offers


  public RequestData(Request req) {
    this.req = req;
  }

  public int compareTo(RequestData that) {
    // Math.sgn(this - that)
    if(this.req.request_time < that.req.request_time)
      return -1;
    else if(this.req.request_time == that.req.request_time)
      return 0;
    else
      return 1;
  }

}

