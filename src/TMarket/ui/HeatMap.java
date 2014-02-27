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

package TMarket.ui;

import java.awt.*;
import java.awt.image.*;


// TODO: Fade heat map over time
// TODO: Go straight to BufferedImage, and skip int array?


public class HeatMap {

  private int width;
  private int height;
  public int radius;

  private int map_width;
  private int map_height;

  private double stamp[][]; // The pattern added to the map

  private double map[][];
  private double max_heat;

  // Color themes:
  public enum Theme {FIRE, BLUEFIRE, NIGHT}

  // FIRE: transparent, red, yellow, white
  private final static Color fire[] = {
    new Color(255,   0,   0,   0),
    new Color(255,   0,   0,  30),
    new Color(255,   0,   0,  60),
    new Color(255,   0,   0, 100),
    new Color(255, 255,   0, 170),
    new Color(255, 255, 255, 255)
  };

  // BLUEFIRE: transparent, blue, cyan, white
  private final static Color bluefire[] = {
    new Color(  0,   0,   0,   0),
    new Color(  0, 128, 255,  64),
    new Color(  0, 128, 255, 128),
    new Color(  0, 128, 255, 192),
    new Color(  0, 255, 255, 255),
    new Color(255, 255, 255, 255)
  };

  // NIGHT: black, deep blue, light yellow
  private final static Color night[] = {
    new Color(  0,   0,   0, 255),
    new Color(  0,   0,  32, 255),
    new Color(  0,   0,  32, 255),
    new Color(  0,   0,  32, 255),
    new Color(  0,   0,  32, 255),
    new Color(  0,   0,  32, 255),
    new Color(  0,   0,  32, 255),
    new Color(  0,   0,  32, 255),
    new Color(  0,   0,  32, 255),
    new Color(  0,   0,  32, 255),
    new Color(  0,   0,  32, 255),
    new Color(255, 255, 224, 255)
  };

  // TODO: purple, red, orange, yellow, tranparent

  // Chosen theme:
  private Color colors[];


  public HeatMap(int width, int height, int radius, Theme theme) {
    this.width = width;
    this.height = height;
    this.radius = radius;

    switch(theme) {
      case FIRE:
        colors = fire;
        break;
      case BLUEFIRE:
        colors = bluefire;
        break;
      case NIGHT:
        colors = night;
        break;
    }

    map_width = width + radius * 2 + 1;
    map_height = height + radius * 2 + 1;

    map = new double[map_width][map_height];
    max_heat = 1.0;

    // Create stamp
    stamp = makeStamp(radius);

  }

  public void add(int x, int y) {
    assert 0 <= x && x < width;
    assert 0 <= y && y < height;

    // Copy stamp to heat map
    for(int xx = 0; xx <= 2 * radius; xx++) {
      for(int yy = 0; yy <= 2 * radius; yy++) {
        double value = map[x + xx][y + yy] + stamp[xx][yy];
        map[x + xx][y + yy] = value;
        if(value > max_heat) {
          // TODO: Moving average on max_heat
          max_heat = (5 * max_heat + value) / 6;
//          max_heat = value;
        }
      }
    }
  }

  // Resets the heat map between drawings
  public void reset() {
    map = new double[map_width][map_height];
    // TODO: Fade instead of erase
  }

  // Draw the heat map
  public BufferedImage getImage() {
    BufferedImage image = new BufferedImage(map_width, map_height, BufferedImage.TYPE_INT_ARGB);
    image.setAccelerationPriority(1.0f);

    // TODO: Draw vector based on view, instead of in map-space and scaling

    for(int x = 0; x < map_width; x++) {
      for(int y = 0; y < map_height; y++) {
        // Map heat to color/transparency
        Color color = getColor(map[x][y]);
        image.setRGB(x, y, color.getRGB());
      }
    }
    return image;
  }

  // Generates a heat stamp for a given radius
  private double[][] makeStamp(int radius) {
    double stamp[][] = new double[radius * 2 + 1][radius * 2 + 1];

    for(int x = -radius; x <= radius; x++) {
      for(int y = -radius; y <= radius; y++) {
        double dist = radius - Math.sqrt(x * x + y * y);
        double heat = Math.max(0, dist / radius);
        heat = heat * heat * heat * heat * heat * heat;
        stamp[x + radius][y + radius] = heat;
      }
    }

    return stamp;
  }

  // Returns the color corresponding to the heat intensity (with transparency)
  // Range: [0.0, 1.0]
  private Color getColor(double heat) {
    assert 0.0 <= heat;

    // Normalize
//    heat = 1.5 * heat / max_heat;

    // Interpolated color bands
    heat = heat * (colors.length - 1);
    int band = (int) Math.floor(heat); // int part = color band
    double pct = heat - band; // frac part = interpolation

    if(band >= colors.length - 1)
      return colors[colors.length - 1];

    // Return the color interpolated between color1 and color2 by given value
    Color color1 = colors[band];
    Color color2 = colors[band + 1];

    int r = (int)((1.0 - pct) * color1.getRed() + (pct) * color2.getRed());
    int g = (int)((1.0 - pct) * color1.getGreen() + (pct) * color2.getGreen());
    int b = (int)((1.0 - pct) * color1.getBlue() + (pct) * color2.getBlue());
    int a = (int)((1.0 - pct) * color1.getAlpha() + (pct) * color2.getAlpha());
    return new Color(r, g, b, a);
  }

}


