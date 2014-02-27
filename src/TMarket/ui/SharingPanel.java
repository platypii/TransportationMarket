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

import TMarket.auctionserver.*;
import TMarket.common.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")


public class SharingPanel extends JPanel {

  private Plot plot = new Plot();

  private String seriesNames[] = {"Marginal cost",
                                  "Initial cost",
                                  "Shared cost"};

  private JComboBox<String> seriesBox1 = new JComboBox<String>(seriesNames);
  private JComboBox<String> seriesBox2 = new JComboBox<String>(seriesNames);

  // Options:
  private int type = Plot.BAR_WIDTH_PLOT;
  private boolean outline = false;

  // Data:
  private CostSharing costSharing;


  public SharingPanel() {
    // Initially empty
    double nodata[] = {};
    plot.add(nodata, nodata, 0, false);
    plot.add(nodata, nodata, 0, false);

    JPanel vizPanel = new JPanel();
    vizPanel.setLayout(new BorderLayout());
    JPanel keyPanel = new JPanel();
//    JComboBox rightBox1 = new JComboBox(seriesNames);
    seriesBox1.setForeground(Color.RED);
    seriesBox1.setSelectedIndex(0);
    seriesBox1.addActionListener(listen);
//    JComboBox rightBox2 = new JComboBox(seriesNames);
    seriesBox2.setForeground(Color.BLUE);
    seriesBox2.setSelectedIndex(2);
    seriesBox2.addActionListener(listen);
    keyPanel.add(seriesBox1);
    keyPanel.add(seriesBox2);
    vizPanel.add(plot, BorderLayout.CENTER);
    vizPanel.add(keyPanel, BorderLayout.SOUTH);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(vizPanel, BorderLayout.CENTER);

    this.add(mainPanel);

  }

  // Generate a random instance
  public void loadSim(Sim sim) {
//    this.sim = sim;
    this.costSharing = sim.aserver.costSharing;
  }

  public void updateSim() {

    // Current time: costSharing
    double alpha[] = costSharing.alpha;

    double series[][] = {costSharing.mc,
                         costSharing.init,
                         costSharing.sc};

    // Display results
    plot.set(0, series[seriesBox1.getSelectedIndex()], alpha, type, outline);
    plot.set(1, series[seriesBox2.getSelectedIndex()], alpha, type, outline);
    plot.repaint();

  }


  private ActionListener listen = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if(action.equals("comboBoxChanged")) {
          updateSim();

        } else {
          System.err.println("Received unknown action: " + action);
          System.err.println("Received unknown event: " + e);
        }

      }
    };

}


