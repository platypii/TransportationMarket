/*
 * The Transportation Market
 *
 * Copyright 2011 Kenny Daniel
 * Copyright 2011 Xiaoqing Wang
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


// Each entry in the plan maps to a Node, which stores various info
public class Node {

  // Parameters:
  public Loc loc;
  public double hit_time; // pickup time for a pickup node, dropoff time a the dropoff node
  public double wait_time_before;//wait at the loc before serve the customer
  public double wait_time_after;//wait at the loc after serve the customer
  double earliest_time;
  double latest_time;
  double service_time;
  int index;
  // Task at this node:
  String id; // Consumer id

  // Node type:
  public enum NodeType {HOME, PICKUP, DROPOFF,TEMP}
  public static final NodeType HOME = NodeType.HOME; // Shortcuts
  public static final NodeType PICKUP = NodeType.PICKUP;
  public static final NodeType DROPOFF = NodeType.DROPOFF;
  public static final NodeType TEMP = NodeType.TEMP;
  // The actual type of this node
  NodeType type;


  // Creates a copy of the given node
  public Node(Node node) {
    this(node.id,node.index, node.type, node.hit_time, node.wait_time_before,node.wait_time_after, node.loc, node.earliest_time, node.latest_time, node.service_time);
  }

  public Node(String id, int index, NodeType type, double hit_time, double wait_time_before,double wait_time_after,Loc loc, double earliest_time, double latest_time, double service_time) {
    this.hit_time = hit_time;
    this.wait_time_before = wait_time_before;
    this.wait_time_after = wait_time_after;
    this.loc = loc;
    this.earliest_time = earliest_time;
    this.latest_time = latest_time;
    this.service_time = service_time;
    this.id = id;
    this.type = type;
    this.index=index;

  }

  public boolean isPickup() {
    return (type == NodeType.PICKUP);
  }

  public boolean isDropoff() {
    return (type == NodeType.DROPOFF);
  }

  // Returns a string representation of this Node
  public String toString() {
    String str_rep = loc + " [";
    if(isPickup()) str_rep += "+";
    if(isDropoff()) str_rep += "-";
    str_rep += id + "] [" + earliest_time + " " + hit_time + " " + latest_time + " " + wait_time_before + " " + wait_time_after + "]";
    return str_rep;
  }

  public boolean equals(Object obj) {
    return toString().equals(obj.toString());
  }

}
