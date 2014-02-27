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

import TMarket.common.*;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;

@SuppressWarnings("serial")


public class ColorIcon extends ImageIcon {

  private static BufferedImage base;

  public ColorIcon(Color color) {
    super();

    if(base == null) {
      try {
        // Read images from TMarket/resources/
//        String red_file = getClass().getResource("/TMarket/resources/red.png").getFile();
        InputStream red_stream = getClass().getResourceAsStream("/TMarket/resources/red.png");
        base = ImageIO.read(red_stream);
      } catch(Exception e) {
        Debug.println("Failed to open image resources.");
      }
    }

    if(base != null) {
      BufferedImage image = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
      for(int x = 0; x < base.getWidth(); x++) {
        for(int y = 0; y < base.getHeight(); y++) {
          Color rgb = new Color(base.getRGB(x,y), true);
          int intensity = rgb.getRed();
          Color new_rgb = new Color(intensity * color.getRed() / 255,
                                    intensity * color.getGreen() / 255,
                                    intensity * color.getBlue() / 255,
                                    rgb.getAlpha());
          image.setRGB(x, y, new_rgb.getRGB());
        }
      }

      this.setImage(image);
    }
  }

}
