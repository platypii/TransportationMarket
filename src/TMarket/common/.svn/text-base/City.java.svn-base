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
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;


public class City {
  // City representation (filename or grid)
  public String city_rep;

  // Map image
  private String map_name;
  public BufferedImage map_image;

  // Graph structure
  public List<Loc> nodes;
  private HashMap<String,Loc> nodes_by_name;
  public int num_edges;

  // City bounds
  public int x_min;
  public int x_max;
  public int x_size;
  public int y_min;
  public int y_max;
  public int y_size;

  public static final double SPEED = 20.0; // pixels / minute


  // Creates a city from a file
  public City(String filename) {
    city_rep = "CITY " + filename;

    nodes = new ArrayList<Loc>();
    nodes_by_name = new HashMap<String,Loc>();
    num_edges = 0;

    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));
      String line;
      // Read file line by line
      while((line = in.readLine()) != null) {
        if(line.length() > 0 && line.charAt(0) != '%') {
          // Non-empty, uncommented line
          Scanner sc = new Scanner(line);
          String command = sc.next();

          if(command.equals("MAP")) {
            String path = filename.substring(0, filename.lastIndexOf(File.separator));
            String map_filename = path + File.separator + sc.next();
            try {
              map_image = ImageIO.read(new File(map_filename));
            } catch(IOException e) {
              Debug.println("Failed to open map: " + map_filename);
            }
          } else if(command.equals("NODE")) {
            // Read in a node
            String name = sc.next();
            int x = sc.nextInt();
            int y = sc.nextInt();
            Loc node = new Loc(this, name, x, y);
            addNode(node);
          } else if(command.equals("EDGE")) {
            // Read in an edge
            String name1 = sc.next();
            String name2 = sc.next();
            assert nodes_by_name.containsKey(name1);
            assert nodes_by_name.containsKey(name2);
            Loc node1 = nodes_by_name.get(name1);
            Loc node2 = nodes_by_name.get(name2);
            node1.succ.add(node2);
            node2.succ.add(node1); // Comment me for directed-edges
            num_edges++;
          } else {
            Debug.println("Invalid command in city file: " + command);
          }
        }
      }
    } catch(IOException e) {
      Debug.println(e);
      System.exit(1);
    }

    // Computes all-pairs shortest paths in the city graph
    for(Loc source : nodes) {
      source.computeShortestPaths();
    }
  }

  // Creates a grid city of size NxM
  // NYC: 125x15
  public City(int n, int m) {
    city_rep = "GRID " + n + " " + m;

    Loc grid[][] = new Loc[n][m];

    map_image = null;
    nodes = new ArrayList<Loc>();
    nodes_by_name = new HashMap<String,Loc>();

    for(int x = 0; x < n; x++) {
      for(int y = 0; y < m; y++) {
        // Make Vertex
        // TODO: What if x > 'Z'?
        String name = ((char)('A' + x)) + "" + y;
        grid[x][y] = new Loc(this, name, x*100, y*100);
        addNode(grid[x][y]);
      }
    }

    // Sort nodes by hashcode (random)
    Collections.sort(nodes, new Comparator<Loc>() {
            public int compare(Loc o1, Loc o2) {
              if(o1.hashCode() < o2.hashCode())
                return -1;
              else if(o1.hashCode() == o2.hashCode())
                return 0;
              else
                return 1;
            }});

    // Make Edges
    num_edges = 2 * n * m - n - m;
    for(int x = 0; x < n; x++) {
      for(int y = 0; y < m; y++) {
        if(x > 0) grid[x][y].succ.add(grid[x-1][y]);
        if(y > 0) grid[x][y].succ.add(grid[x][y-1]);
        if(x < n-1) grid[x][y].succ.add(grid[x+1][y]);
        if(y < m-1) grid[x][y].succ.add(grid[x][y+1]);
      }
    }

    // Computes all-pairs shortest paths in the city graph
    for(Loc source : nodes) {
      source.computeShortestPaths();
    }

  }

  // Adds a location to the city
  private void addNode(Loc loc) {
    // Update bounds
    if(x_size == 0 || loc.x < x_min) x_min = loc.x;
    if(x_size == 0 || loc.x > x_max) x_max = loc.x;
    if(y_size == 0 || loc.y < y_min) y_min = loc.y;
    if(y_size == 0 || loc.y > y_max) y_max = loc.y;
    x_size = x_max - x_min + 1;
    y_size = y_max - y_min + 1;

    nodes.add(loc);
    nodes_by_name.put(loc.name, loc);
  }

  // Returns the road distance (in pixels)
  public double dist(Loc start, Loc end) {
    assert start != null;
    assert end != null;
    if(start == null || end == null)
      return 0.0; // TODO

    return start.getShortestDist(end);
  }
  public double dist(String start, String end) {
    return dist(getLocByName(start), getLocByName(end));
  }

  // Returns the time (in minutes)
  public double time(Loc start, Loc end) {
    return dist(start, end) / SPEED;
  }
  public double time(String start, String end) {
    return time(getLocByName(start), getLocByName(end));
  }

  // Returns the euclidean distance (in pixels)
  public double euclideanDist(Loc start, Loc end) {
    int dx = start.x - end.x;
    int dy = start.y - end.y;

//    return Math.abs(dx) + Math.abs(dy); // Manhattan distance
    return Math.sqrt(dx*dx + dy*dy); // Euclidean distance

  }

  // Returns a random location
  public Loc uniformRandomLoc() {
    Random rand = new Random();
    // non-uniform
    int r = rand.nextInt(nodes.size());
    return nodes.get(r);
  }

  // Returns a random location (non-uniform)
  public Loc randomLoc() {
    Random rand = new Random();
    // non-uniform
    int r = rand.nextInt(nodes.size());
    r = r * r / nodes.size();
    return nodes.get(r);
  }

  // Returns the location corresponding to the given name
  public Loc getLocByName(String name) {
    return nodes_by_name.get(name);
  }

  // Returns the cost of traveling from location l1 to location l2
  public double cost(Loc l1, Loc l2) {
    return dist(l1, l2);
  }

  // Writes this city out to a file
  public void saveCity(String filename) {
    try {
      PrintWriter out = new PrintWriter(new FileWriter(filename));

      // Write map
      if(map_image != null && map_name != null) {
        out.println("MAP " + map_name);
        out.println();
      }

      // Write nodes
      for(Loc node : nodes) {
        out.println("NODE " + node.name + " " + node.x + " " + node.y);
      }
      out.println();

      // Write edges
      for(Loc node1 : nodes) {
        for(Loc node2 : node1.succ) {
          // Don't output duplicates (undirected)
          if(node1.hashCode() < node2.hashCode()) {
            out.println("EDGE " + node1.name + " " + node2.name);
          }
        }
      }
    } catch(IOException e) {
      Debug.println("Error writing city file: " + filename);
      e.printStackTrace();
    }
  }

}


