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

import java.util.ArrayList;
import java.awt.Color;
import javax.swing.*;


// A class to store information about how to display layers in the visualizer
public abstract class LayerNode {

  public abstract String getName();
  public abstract Color getColor();
  public abstract ImageIcon getIcon();
  public abstract TriState isSelected();
  public abstract void setSelected(boolean isSelected);


  // Leaf node
  public static class Leaf extends LayerNode {

    // Appearance
    private String name;
    private Color color;
    private ImageIcon icon;
    private boolean isSelected;

    public Leaf(String name, Color color, ImageIcon icon, boolean isSelected) {
      this.name = name;
      this.color = color;
      this.icon = icon;
      this.isSelected = isSelected;
    }

    public String getName() {
      return name;
    }
    public Color getColor() {
      return color;
    }
    public ImageIcon getIcon() {
      return icon;
    }
    public TriState isSelected() {
      return isSelected? TriState.ALL : TriState.NONE;
    }
    public void setSelected(boolean isSelected) {
      this.isSelected = isSelected;
    }

  }

  // Folder node
  public static class Folder extends LayerNode {

    // Appearance
    private String name;
    private Color color;

    // Children
    private ArrayList<LayerNode> children = new ArrayList<LayerNode>();

    public Folder(String name, Color color) {
      this.name = name;
      this.color = color;
    }

    public void addChild(LayerNode child) {
      children.add(child);
    }

    public ArrayList<LayerNode> children() {
      return children;
    }

    public LayerNode getChild(String name) {
      for(LayerNode child : children)
        if(child.getName().equals(name))
          return child;
      return null;
    }

    public void removeAllChildren() {
      children.clear();
    }

    public String getName() {
      return name;
    }
    public Color getColor() {
      return color;
    }
    public ImageIcon getIcon() {
//      return (ImageIcon)UIManager.getIcon("Tree.openIcon");
      return null;
    }
    public TriState isSelected() {
      // Check if children are selected
      double numSelected = selectedLeafs();
      double numLeafs = numLeafs();
      if(numSelected == 0) {
        return TriState.NONE;
      } else if(numSelected < numLeafs) {
        return TriState.SOME;
      } else {
        return TriState.ALL;
      }
    }
    // Returns the number of leafs
    private int numLeafs() {
      int leafs = 0;
      for(LayerNode child : children) {
        if(child instanceof Folder) {
          Folder folder = (Folder) child;
          leafs += folder.numLeafs();
        } else {
          // Leaf
          if(child.isSelected() == TriState.ALL) {
            leafs++;
          }
        }
      }
      return leafs;
    }
    // Returns the number of selected leafs
    private int selectedLeafs() {
      int selected = 0;
      for(LayerNode child : children) {
        if(child instanceof Folder) {
          Folder folder = (Folder) child;
          selected += folder.selectedLeafs();
        } else {
          // Leaf
          if(child.isSelected() == TriState.ALL) {
            selected++;
          }
        }
      }
      return selected;
    }
    public void setSelected(boolean isSelected) {
      for(LayerNode child : children) {
        child.setSelected(isSelected);
      }
    }

  }

}


