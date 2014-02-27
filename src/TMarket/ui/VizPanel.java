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
import TMarket.consumer.*;
import TMarket.shuttle.*;
import TMarket.ui.layers.*;

import java.util.*;
import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")


// TODO: Fix window resizing


// The visualization component
public class VizPanel extends JPanel {

  // Information about what layers to display on the visualization
  private LayerNode.Folder root;
  private LayerNode.Folder shuttles;
  private LayerNode.Folder consumers;

  // Components
  private Viz viz;
  private LayerTree layerPanel;


  public VizPanel() {
    super();

    viz = new Viz();
    layerPanel = new LayerTree(viz);

    JSplitPane vizSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, layerPanel, viz);

    this.setLayout(new BorderLayout());
    this.add(vizSplit, BorderLayout.CENTER);

  }

  // Loads a simulation into the visualizer
  public void loadSim(Sim sim) {

    if(root == null) {
      // Default Layer Tree:
      root = new LayerNode.Folder("Layers", Color.BLACK);
      root.addChild(new LayerNode.Leaf("Labels", Color.BLACK, null, false));
      root.addChild(new LayerNode.Leaf("Roads", Color.BLACK, null, true));
      if(sim.city.map_image != null) {
        root.addChild(new LayerNode.Leaf("Map", Color.BLACK, null, true));
      }

      // Shuttles
      shuttles = new LayerNode.Folder("Shuttles", Color.BLUE);
      shuttles.addChild(new LayerNode.Leaf("Heatmap", Color.BLUE, null, false));
      root.addChild(shuttles);

      // Consumers
      consumers = new LayerNode.Folder("Consumers", Color.RED);
      consumers.addChild(new LayerNode.Leaf("Heatmap", Color.RED, null, false));
      root.addChild(consumers);

    } else {
      // Use previously selected layers

      if(root.getChild("Map") == null && sim.city.map_image != null) {
        root.addChild(new LayerNode.Leaf("Map", Color.BLACK, null, true));
      }

      // Shuttles
      shuttles.removeAllChildren();
      shuttles.addChild(new LayerNode.Leaf("Heatmap", Color.BLUE, null, false));

      // Consumers
      consumers.removeAllChildren();
      consumers.addChild(new LayerNode.Leaf("Heatmap", Color.RED, null, false));

    }

    // Shuttles
    LayerNode.Folder shuttleIcons = new LayerNode.Folder("Icons", Color.BLUE);
    LayerNode.Folder shuttleRoutes = new LayerNode.Folder("Routes", Color.BLUE);
    for(int i = 0; i < sim.shuttles.size(); i++) {
      Shuttle sh = sim.shuttles.get(i);
      Color color = new Color(192 * i / sim.shuttles.size(), 64 + 160 * i / sim.shuttles.size(), 255); // Blue -> Light Blue
      ImageIcon icon = new ColorIcon(color);
      shuttleIcons.addChild(new LayerNode.Leaf(sh.id(), color, icon, true));
      shuttleRoutes.addChild(new LayerNode.Leaf(sh.id(), color, null, true));
    }
    shuttles.addChild(shuttleIcons);
    shuttles.addChild(shuttleRoutes);

    // Consumers
    LayerNode.Folder consumerIcons = new LayerNode.Folder("Icons", Color.RED);
    LayerNode.Folder consumerRoutes = new LayerNode.Folder("Routes", Color.RED);
    for(int i = 0; i < sim.consumers.size(); i++) {
      Consumer con = sim.consumers.get(i);
      Color color = new Color(255 - 127 * i / sim.consumers.size(), 0, 0); // Red -> Dark Red
      ImageIcon icon = new ColorIcon(color);
      consumerIcons.addChild(new LayerNode.Leaf(con.id(), color, icon, true));
      consumerRoutes.addChild(new LayerNode.Leaf(con.id(), color, null, false));
    }
    consumers.addChild(consumerIcons);
    consumers.addChild(consumerRoutes);

    layerPanel.loadSim(sim, root);
    viz.loadSim(sim, root);

    updateSim();
  }

  public void updateSim() {
    viz.repaint();
  }

}


