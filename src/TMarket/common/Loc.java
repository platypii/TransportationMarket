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


// Represents a location in the city
// Also stores shortest path data using shortestDist and parent
// loc1.parent.get(loc2) is the successor after loc1 on the shortest path from loc1 to loc2
public class Loc {

  private City city;

  public String name;
  public int x;
  public int y;

  // Successors (neighbors) of this location
  public Set<Loc> succ;

  // Shortest paths data
  private Map<Loc,Double> shortestDist;
  private Map<Loc,Loc> parent;

  public static final double INFINITY = Double.POSITIVE_INFINITY; // Integer.MAX_VALUE;

  private int hashCode; // Precompute to save time


  public Loc(City city, int x, int y) {
    this(city, x + "x" + y, x, y);
  }

  public Loc(City city, String name, int x, int y) {
    this.city = city;
    this.name = name;
    this.x = x;
    this.y = y;

    succ = new HashSet<Loc>();

    // This location's hashCode
    hashCode = (name+" "+x+" "+y).hashCode();
  }

  // Shortest road distance from this to loc
  public double getShortestDist(Loc loc) {
    assert shortestDist != null;

    Double d = shortestDist.get(loc);
    return (d == null) ? INFINITY : d;
  }

  // Compute all shortest paths from this location using dijkstra
  public void computeShortestPaths() {
    shortestDist = new HashMap<Loc,Double>();
    parent = new HashMap<Loc,Loc>();

    Set<Loc> closed = new HashSet<Loc>();
    PriorityQueue<Loc> pq = new PriorityQueue<Loc>(11, comp); // 11 = default initialCapacity

    shortestDist.put(this, 0.0);
//    parent.put(this, this);
    pq.add(this);

    while(!pq.isEmpty()) {
      Loc s = pq.poll();

      closed.add(s);

      for(Loc t : s.succ) {
	if(!closed.contains(t)) {
          assert shortestDist.containsKey(s);
	  double stDist = shortestDist.get(s) + city.euclideanDist(s, t);

	  if(stDist < getShortestDist(t)) {
	    pq.remove(t);
	    shortestDist.put(t, stDist);
	    pq.add(t);
	    parent.put(t, s);
	  }
	}
      }
    }
  }

  // Returns the shortest path from this to dest
  public List<Loc> getPath(Loc dest) {
    List<Loc> path = new ArrayList<Loc>();
    Loc temp = dest;

    path.add(temp);
    while(temp != this) {
      temp = parent.get(temp);
      path.add(0, temp);
    }

//    System.out.println(this + ".getPath("+end+") = " + path);

    return path;
  }

  public String toString() {
    return name;
  }

  // Precomputed, since this function is called more than any other in the program
  public int hashCode() {
    return hashCode;
  }

  public boolean equals(Object obj) {
    if(obj instanceof Loc) {
      Loc l = (Loc) obj;
      assert ((l.x == x) && (l.y == y)) == (l.name == name);
      return name.equals(l.name);
    } else if(obj instanceof String) {
      return name.equals(obj);
    } else {
      return false;
    }
  }

  private Comparator<Loc> comp = new Comparator<Loc>() {
    public int compare(Loc loc1, Loc loc2) {
      double cost1 = getShortestDist(loc1);
      double cost2 = getShortestDist(loc2);
      if(cost1 < cost2)
	return -1;
      else if(cost1 == cost2)
	return 0;
      else
	return 1;
    }
  };

}