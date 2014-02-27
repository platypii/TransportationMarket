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

import TMarket.common.*;


public interface Shuttle {

  // Returns the shuttle's id
  public String id();

  // Returns the current time
  public double getTime();

  // Returns the location now
  public Loc getLoc();

  // Returns the location at a given time
//  public Loc getLoc(double time);

  // Returns the current plan
  public Plan getPlan();

  // Called when a request comes in
  public void receiveRequest(Request req);

  // Called when a consumer confirms an offer
  public void receiveConfirm(Offer offer);

  // Called when a consumer rejects an offer
  public void receiveReject(Offer offer);

  // Steps time forward one minute
  public void step(int new_time);

  // Resets the shuttle back to its inital state
  public void reset();

  // Stats
  public int offered();
  public int confirmed();
  public int passengers();
  public double distance();
  public double waittime();
  public double occupancy();
  public double payment();

  // Clean up
  public void terminate();

}

