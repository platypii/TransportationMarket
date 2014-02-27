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

package TMarket.ui.layers;

import TMarket.common.*;
import TMarket.consumer.*;
import TMarket.shuttle.*;
import TMarket.ui.*;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")


// The actual visualization of the city
public class Viz extends JComponent {
  private Sim sim;
  private LayerNode.Folder vizOptions;
  private LayerNode.Folder shuttleOptions;
  private LayerNode.Folder consumerOptions;

  private HeatMap shuttle_heat_map;
  private HeatMap consumer_heat_map;

  private final int zoom_levels = 65; // 2^n + 1 works nicely if you want clean zoom percentages
  private int zoom_level = zoom_levels / 2;
  private double zoom = 1.0;

  private int center_x; // the center of the view (in city space)
  private int center_y;

  private static final Color fg = Color.WHITE; // Foreground
  private static final Color bg = Color.BLACK; // Background

  // prefered size:
  private static final int WIDTH = 800; // 1024
  private static final int HEIGHT = 600; // 768


  public Viz() {
    super();

    // handle mouse controls
    this.addMouseMotionListener(mouseListen); 
    this.addMouseWheelListener(mouseListen);
    this.addMouseListener(mouseListen);

  }

  // Loads a simulation into the visualizer
  public void loadSim(Sim sim, LayerNode.Folder vizOptions) {
    this.sim = sim;
    this.vizOptions = vizOptions;
    this.shuttleOptions = (LayerNode.Folder)vizOptions.getChild("Shuttles");
    this.consumerOptions = (LayerNode.Folder)vizOptions.getChild("Consumers");

    shuttle_heat_map = new HeatMap(sim.city.x_size, sim.city.y_size, 300, HeatMap.Theme.BLUEFIRE);
    consumer_heat_map = new HeatMap(sim.city.x_size, sim.city.y_size, 250, HeatMap.Theme.FIRE);

    // Default zoom
    double fit_x = ((double)WIDTH) / (sim.city.x_size + 100);
    double fit_y = ((double)HEIGHT) / (sim.city.y_size + 100);
    double zoom_to_fit = Math.min(fit_x, fit_y);
    int zoom_level = (int)invZoom(zoom_to_fit);
    setZoomLevel(zoom_level);

    // Re-center
    center_x = sim.city.x_min + sim.city.x_size / 2;
    center_y = sim.city.y_min + sim.city.y_size / 2;

    repaint();
  }

  public Dimension getPreferredSize() {
    return new Dimension(WIDTH, HEIGHT);
  }

  public void paintComponent(Graphics g) {
//    Debug.println("Viz.paintComponent(g)");
//    super.paintComponent(g);

    Dimension dim = this.getSize();
    Image bufferedImage = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB); // ARGB
    Graphics2D buffer = (Graphics2D) bufferedImage.getGraphics();

    buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    buffer.setPaint(bg);
    buffer.fillRect(0,0,dim.width,dim.height);

    if(sim != null) {
      // TODO: Pre-draw background, map, edges, etc

      // Draw map
      drawMap(buffer, dim);

      // Draw shuttle heat map
      if(shuttleOptions.getChild("Heatmap").isSelected() == TriState.ALL)
        drawShuttleHeatMap(buffer, dim);

      // Draw consumer heat map
      if(consumerOptions.getChild("Heatmap").isSelected() == TriState.ALL)
        drawConsumerHeatMap(buffer, dim);

      // Draw roads
      if(vizOptions.getChild("Roads").isSelected() == TriState.ALL) {
        buffer.setPaint(Color.BLACK);
        buffer.setStroke(new BasicStroke((float)Math.max(6.0, 6.0 * zoom)));
        drawRoads(buffer);
        buffer.setPaint(Color.WHITE);
        buffer.setStroke(new BasicStroke((float)Math.max(2.0, zoom)));
        drawRoads(buffer);
      }

      // Draw labels
      if(vizOptions.getChild("Labels").isSelected() == TriState.ALL) {
        buffer.setPaint(fg);
        drawLabels(buffer);
      }

      // Draw shuttle routes
      drawShuttleRoutes(buffer);

      // Draw consumer routes
      drawConsumerRoutes(buffer);

      // Draw shuttles
      drawShuttleIcons(buffer);

      // Draw consumers
      drawConsumerIcons(buffer);

    }

    // Draw the double buffer
    g.drawImage(bufferedImage, 0, 0, this);
  }


  // Draw node labels
  private void drawLabels(Graphics2D buffer) {
    for(Loc loc : sim.city.nodes) {
      buffer.drawString(loc.name, zoom_x(loc.x) + 4, zoom_y(loc.y) - 4);
    }
  }

  // Draw shuttle icons  // Draw city roads
  private void drawRoads(Graphics2D buffer) {
    for(Loc a : sim.city.nodes) {
      for(Loc b : a.succ) {
        // TODO: Color based on speed

        // Don't draw twice:
        if(a.hashCode() < b.hashCode()) {
          buffer.draw(new Line2D.Double(zoom_x(a.x), zoom_y(a.y),
                                        zoom_x(b.x), zoom_y(b.y)));
        }
      }
    }
  }

  // Draw map image
  private void drawMap(Graphics2D buffer, Dimension dim) {
    if(sim.city.map_image != null && 
       vizOptions.getChild("Map").isSelected() == TriState.ALL) {
      AffineTransform xform = new AffineTransform();
      xform.translate(dim.width / 2, dim.height / 2);
      xform.scale(zoom, zoom);
      xform.translate(-center_x, -center_y);
      buffer.drawImage(sim.city.map_image, xform, null);
//    buffer.drawImage(sim.city.map_image, 0, 0, null);
    }
  }

  // Draw shuttle heat map
  private void drawShuttleHeatMap(Graphics2D buffer, Dimension dim) {

    // Reset shuttle heat map
    shuttle_heat_map.reset();

    // Draw shuttles
    for(Shuttle sh : sim.shuttles) {
      // Draw shuttle
      Loc loc = sh.getLoc();
      if(loc != null) {
        // Add to heat map
        shuttle_heat_map.add(loc.x - sim.city.x_min, loc.y - sim.city.y_min);
      }
    }

    // Draw heat map
    BufferedImage heat_image = shuttle_heat_map.getImage();

    AffineTransform xform = new AffineTransform();
    xform.translate(dim.width / 2, dim.height / 2);
    xform.scale(zoom, zoom);
    xform.translate(sim.city.x_min - center_x - shuttle_heat_map.radius, sim.city.y_min - center_y - shuttle_heat_map.radius);
    buffer.drawImage(heat_image, xform, null);
//    buffer.drawImage(heat_image, 0, 0, this);
  }

  // Draw shuttle icons
  private void drawShuttleIcons(Graphics2D buffer) {
    LayerNode.Folder iconOptions = (LayerNode.Folder)shuttleOptions.getChild("Icons");
    buffer.setStroke(new BasicStroke((float)Math.max(1.0, 3.0 * zoom)));
    for(Shuttle sh : sim.shuttles) {
      LayerNode.Leaf option = (LayerNode.Leaf)iconOptions.getChild(sh.id());
      if(option.isSelected() == TriState.ALL) {
        // Draw shuttle
        Loc loc = sh.getLoc();
        if(loc != null) {
          buffer.drawImage(option.getIcon().getImage(), zoom_x(loc.x) - 3, zoom_y(loc.y) - 17, null);
        }
      }
    }
  }

  // Draw shuttle routes
  private void drawShuttleRoutes(Graphics2D buffer) {
    LayerNode.Folder routeOptions = (LayerNode.Folder)shuttleOptions.getChild("Routes");
    buffer.setStroke(new BasicStroke((float)Math.max(1.0, 3.0 * zoom)));
    // Draw routes
    for(Shuttle sh : sim.shuttles) {
      LayerNode.Leaf option = (LayerNode.Leaf)routeOptions.getChild(sh.id());
      if(option.isSelected() == TriState.ALL) {
        buffer.setColor(option.getColor());
        // Go thru plan
        Plan plan = sh.getPlan();
        for(int i = 1; i < plan.plan.size(); i++) {
          Loc a = plan.plan.get(i-1).loc;
          Loc b = plan.plan.get(i).loc;
          java.util.List<Loc> path = a.getPath(b);
          for(int j = 1; j < path.size(); j++) {
            Loc c = path.get(j-1);
            Loc d = path.get(j);
            buffer.draw(new Line2D.Double(zoom_x(c.x), zoom_y(c.y),
                                          zoom_x(d.x), zoom_y(d.y)));
          }
        }
      }
    }
  }

  // Draw consumer heat map
  private void drawConsumerHeatMap(Graphics2D buffer, Dimension dim) {
//    Debug.println("Viz.drawConsumerHeatMap()");

    // Reset consumer heat map
    consumer_heat_map.reset();
    
    // Draw consumers
    for(Consumer con : sim.consumers) {
      //Loc loc = con.getLoc();
      Loc loc;
      if(con.curr != null) {
        loc = con.curr;
      } else {
        // Consumers don't really know where they are when traveling, check with shuttle
        String shuttle_id = con.confirmed_offer.shuttle_id;
        Shuttle shuttle = sim.shuttles_by_id.get(shuttle_id);
        loc = shuttle.getLoc();
      }

      // Add to heat map
      consumer_heat_map.add(loc.x - sim.city.x_min, loc.y - sim.city.y_min);

    }

    // Draw heat map
    BufferedImage heat_image = consumer_heat_map.getImage();

    AffineTransform xform = new AffineTransform();
    xform.translate(dim.width / 2, dim.height / 2);
    xform.scale(zoom, zoom);
    xform.translate(sim.city.x_min - center_x - consumer_heat_map.radius, sim.city.y_min - center_y - consumer_heat_map.radius);
    buffer.drawImage(heat_image, xform, null);
//    buffer.drawImage(heat_image, 0, 0, this);
  }

  // Draw consumer icons
  private void drawConsumerIcons(Graphics2D buffer) {
    LayerNode.Folder iconOptions = (LayerNode.Folder)consumerOptions.getChild("Icons");
    for(Consumer con : sim.consumers) {
      LayerNode.Leaf option = (LayerNode.Leaf)iconOptions.getChild(con.id());
      if(option.isSelected() == TriState.ALL) {
        //Loc loc = con.getLoc();
        Loc loc;
        if(con.curr != null) {
          loc = con.curr;
        } else {
          // Consumers don't really know where they are when traveling, check with shuttle
          String shuttle_id = con.confirmed_offer.shuttle_id;
          Shuttle shuttle = sim.shuttles_by_id.get(shuttle_id);
          loc = shuttle.getLoc();
        }

        buffer.drawImage(option.getIcon().getImage(), zoom_x(loc.x) - 7, zoom_y(loc.y) - 17, null);
      }
    }
  }

  // Draw consumer routes
  private void drawConsumerRoutes(Graphics2D buffer) {
    LayerNode.Folder routeOptions = (LayerNode.Folder)consumerOptions.getChild("Routes");
    buffer.setStroke(new BasicStroke((float)Math.max(1.0, 2.0 * zoom)));
    for(Consumer con : sim.consumers) {
      LayerNode.Leaf option = (LayerNode.Leaf)routeOptions.getChild(con.id());
      if(option.isSelected() == TriState.ALL) {
        buffer.setColor(option.getColor());
        Loc a = con.start;
        Loc b = con.end;
        buffer.draw(new Line2D.Double(zoom_x(a.x), zoom_y(a.y),
                                      zoom_x(b.x), zoom_y(b.y)));
      }
    }
  }

  // Required for good double-buffering. 
  // Cause the component not to first wipe off 
  // previous drawings but to immediately repaint. 
  // the wiping off also causes flickering. 
  // Update is called automatically when repaint() is called.
  public void update(Graphics g) {
    paint(g);
  }

  // Computes the exponential zoom from the zoom_level
  private double zoom(int zoom_level) {
    return Math.pow(2.0, (double)zoom_level * 4.0 / ((double)(zoom_levels - 1)) - 2.0);
  }

  // Computes the inverse of the zoom function (returns zoom_level)
  private double invZoom(double zoom) {
    double log = Math.log(4.0 * zoom) / Math.log(2.0);
    return log * (zoom_levels - 1) / 4.0;
  }

  // Returns a zoomed and translated x value
  // input = city space, output = screen space
  private int zoom_x(int x) {
    return getSize().width / 2 + (int)(zoom * (x - center_x));
  }

  // Returns a zoomed and translated y value
  private int zoom_y(int y) {
    return getSize().height / 2 + (int)(zoom * (y - center_y));
  }

  // Updates the zoom parameters
  public void setZoomLevel(int new_zoom_level) {
//    assert new_zoom_level >= 1;
//    assert new_zoom_level <= zoom_levels;
    if(new_zoom_level < 0) new_zoom_level = 0;
    if(new_zoom_level >= zoom_levels) new_zoom_level = zoom_levels - 1;

    this.zoom_level = new_zoom_level;
    this.zoom = zoom(new_zoom_level);

//    Debug.println("zoom = " + zoom);

    this.repaint();

//      Debug.println("DEBUG: zoom_level = " + zoom_level + ", zoom = " + zoom);
  }

  // Updates the center parameters
  private void setCenter(int x, int y) {
    // TODO: restrict dragging off screen
    center_x = x;
    center_y = y;
    this.repaint();
  }

  // Zooms to new_zoom_level holding the given point constant (in screen space)
  private void setZoomOnPoint(int new_zoom_level, int x, int y) {
    if(new_zoom_level < 0) new_zoom_level = 0;
    if(new_zoom_level >= zoom_levels) new_zoom_level = zoom_levels - 1;
    double new_zoom = zoom(new_zoom_level);
    double z_x = (double)(2 * x - getSize().width) / 2.0;
    double z_y = (double)(2 * y - getSize().height) / 2.0;

    int new_center_x = center_x + (int)(z_x * (new_zoom - zoom) / (zoom * new_zoom));
    int new_center_y = center_y + (int)(z_y * (new_zoom - zoom) / (zoom * new_zoom));
    setZoomLevel(new_zoom_level);
    setCenter(new_center_x, new_center_y);
  }


  // Mouse handling
  private VizMouseListener mouseListen = new VizMouseListener();
  private class VizMouseListener extends MouseInputAdapter implements MouseMotionListener,MouseWheelListener {
    private final Cursor dc;
    private final Cursor hc = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private Point start;

    public VizMouseListener() {
      dc = getCursor();
    }

    public void mouseClicked(MouseEvent e) {
      if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
        // Double click to zoom in
        setZoomOnPoint(zoom_level + 3, e.getX(), e.getY());
      }
      if(e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() >= 2) {
        // Left double click to zoom out
        setZoomOnPoint(zoom_level - 3, e.getX(), e.getY());
      }
    }

    public void mouseDragged(MouseEvent e) {
      Point pt = e.getPoint();
      int delta_x = (int)((double)(pt.x - start.x) / zoom);
      int delta_y = (int)((double)(pt.y - start.y) / zoom);
      start = pt;
      setCenter(center_x - delta_x, center_y - delta_y);

      // TODO: Left-drag to zoom?
    }
    
    public void mousePressed(MouseEvent e) {
      setCursor(hc);
      start = e.getPoint();
    }

    public void mouseReleased(MouseEvent e) {
      setCursor(dc);
    }

    public void mouseMoved(MouseEvent e) {}

    public void mouseWheelMoved(MouseWheelEvent e) {
      // Zoom onto mouse location
      Point pt = e.getPoint();
      int notches = e.getWheelRotation();
      setZoomOnPoint(zoom_level - notches, pt.x, pt.y);
    }
  }
}



