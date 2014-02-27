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

package TMarket.fairshare;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

@SuppressWarnings("serial")


// TODO: 3D bar plot
// TODO: Blending mode


// A class to visualize the FairShare algorithm
public class Plot extends Component {

  // Formating:
  private static final int margin_north = 20;
  private static final int margin_south = 25;
  private static final int margin_east = 20;
  private static final int margin_west = 40;

  // Constants:
  public static final int LINE_PLOT = 0;
  public static final int BAR_PLOT = 1;
  public static final int BAR_WIDTH_PLOT = 2;

  // Data:
  private ArrayList<Series> series = new ArrayList<Series>();

  // A class to represent a data series within the plot
  private class Series {
    public double[] data;
    public double[] alpha;
    public int type;
    public boolean outline;
    public Series(double data[], double alpha[], int type, boolean outline) {
      this.data = data;
      this.alpha = alpha;
      this.type = type;
      this.outline = outline;
    }
  }

  private static final Color colors[] = {
    new Color(255, 0, 0, 127), // Red
    new Color(0, 0, 255, 127), // Blue
    new Color(0, 255, 0, 127), // Green
    new Color(255, 255, 0, 127) // Yellow
  };


  // Adds data to the plot
  public void add(double data[], double alpha[], int type, boolean outline) {
    series.add(new Series(data, alpha, type, outline));
  }

  public Series get(int i) {
    return series.get(i);
  }

  public void set(int i, double data[], double alpha[], int type, boolean outline) {
    series.set(i, new Series(data, alpha, type, outline));
  }

  // Set the outline of all series
  public void setOutline(boolean outline) {
    for(Series s : series) {
      s.outline = outline;
    }
  }

  // Set the type of all series
  public void setType(int type) {
    for(Series s : series) {
      s.type = type;
    }
  }

  // Updates the bounds on the plot
  private int max_x = 1; // The maximum data length
  private double max_y = 1; // The maximum data value
  private double max_cpa = 1; // The maximum cost per alpha
  private double max_alpha = 1; // The maximum total alpha of any series
  private void updateBounds() {
    max_x = 1;
    max_y = 1;
    max_cpa = 1;
    max_alpha = 1;
    for(int i = 0; i < series.size(); i++) {
      double data[] = series.get(i).data;
      double alpha[] = series.get(i).alpha;

      assert data.length <= alpha.length;

      // Find data bounds and total alpha
      max_x = Math.max(max_x, data.length - 1);
      double total_alpha = 0;
      for(int j = 0; j < data.length; j++) {
        max_y = Math.max(max_y, data[j]);
        max_cpa = Math.max(max_cpa, data[j] / alpha[j]);
        total_alpha += alpha[j];
      }
      max_alpha = Math.max(max_alpha, total_alpha);
    }
  }

  // Draws the graph
  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    Dimension size = getSize();

    updateBounds();

    // Axes
    g2.draw(new Line2D.Double(margin_west, margin_north - 5,
                              margin_west, size.height - margin_south + 5));
    g2.draw(new Line2D.Double(margin_west - 5, size.height - margin_south,
                              size.width - margin_east + 5, size.height - margin_south));

    // Labels
    g2.drawString("0", margin_west - 10, size.height - margin_south + 13);
    g2.drawString(Integer.toString(max_x), size.width - margin_east - 15, size.height - margin_south + 13);
    g2.drawString(String.format("%.1f", max_y), margin_west - 30, margin_north);

    // Data
    for(int i = 0; i < series.size(); i++) {
      drawSeries(g2, series.get(i), colors[i]);
    }

  }

  // Draw the series
  public void drawSeries(Graphics2D g2, Series s, Color c) {
    AffineTransform xform = new AffineTransform();
    xform.translate(margin_west, margin_north);
    Dimension size = getSize();
    Dimension plotSize = new Dimension(size.width - margin_east - margin_west,
                                       size.height - margin_north - margin_south);
    Shape shape;

    switch(s.type) {
      case Plot.LINE_PLOT:
        shape = linePlot(s.data, plotSize);
        shape = xform.createTransformedShape(shape);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(shape);
        break;
      case Plot.BAR_PLOT:
        shape = barPlot(s.data, plotSize);
        shape = xform.createTransformedShape(shape);
        // Fill area
        g2.setColor(c);
        g2.fill(shape);
        // Draw outline
        if(s.outline) {
          g2.setColor(Color.BLACK);
          g2.setStroke(new BasicStroke(1.0f));
          g2.draw(shape);
        }
        break;
      case Plot.BAR_WIDTH_PLOT:
        shape = barWidthPlot(s.data, s.alpha, plotSize);
        shape = xform.createTransformedShape(shape);
        // Fill area
        g2.setColor(c);
        g2.fill(shape);
        // Draw outline
        if(s.outline) {
          g2.setColor(Color.BLACK);
          g2.setStroke(new BasicStroke(1.0f));
          g2.draw(shape);
        }
        break;
      default:
        System.err.println("Invalid plot type");
    }
  }

  // Draws the given data as a shape
  // TODO: improper objectification (size as argument, max from globals)
  private Shape linePlot(double data[], Dimension size) {
    // Construct shape
    Polygon shape = new Polygon();
    shape.addPoint(size.width, size.height);
    shape.addPoint(0, size.height);
    for(int i = 0; i < data.length; i++) {
      int x = size.width * i / max_x;
      int y = (int)(size.height - size.height * data[i] / max_y);
      shape.addPoint(x, y);
    }
    return shape;
  }

  private Shape barPlot(double data[], Dimension size) {
    double bar_width = size.width / (max_x + 1);

    // Construct shape
    Polygon shape = new Polygon();
    shape.addPoint(size.width, size.height);
    shape.addPoint(0, size.height);
    for(int i = 0; i < data.length; i++) {
      int x1 = size.width * i / (max_x + 1);
      int x2 = size.width * (i+1) / (max_x + 1);
      int y = (int)(size.height - size.height * data[i] / max_y);
      shape.addPoint(x1, y);
      shape.addPoint(x2, y);
      shape.addPoint(x2, size.height);
    }
    return shape;
  }

  private Shape barWidthPlot(double data[], double alpha[], Dimension size) {

    // Construct shape
    Polygon shape = new Polygon();
    shape.addPoint(size.width, size.height);
    shape.addPoint(0, size.height);

    double partial_alpha = 0;

    for(int i = 0; i < data.length; i++) {
      int x1 = (int)(size.width * partial_alpha / max_alpha);
      partial_alpha += alpha[i];
      int x2 = (int)(size.width * partial_alpha / max_alpha);
      int y = (int)(size.height - size.height * data[i] / alpha[i] / max_cpa);
      shape.addPoint(x1, y);
      shape.addPoint(x2, y);
      shape.addPoint(x2, size.height);
    }

    return shape;
  }

  public Dimension getPreferredSize() {
    return new Dimension(500, 250);
  }

}
