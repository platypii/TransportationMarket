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

import TMarket.common.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")


public class StepPanel extends JPanel {

  private Plot left = new Plot();
  private Plot right = new Plot();

  private String seriesNames[] = {"Marginal cost",
                                  "Initial cost",
                                  "Cost at time t",
                                  "Final cost"};

  private JComboBox leftBox1 = new JComboBox(seriesNames);
  private JComboBox leftBox2 = new JComboBox(seriesNames);
  private JComboBox rightBox1 = new JComboBox(seriesNames);
  private JComboBox rightBox2 = new JComboBox(seriesNames);

  private JCheckBox outlineButton;
  private JCheckBox uniformButton;
  private JLabel timeLabel;

  private Random rand = new Random();

  // Options:
  private int type = Plot.BAR_WIDTH_PLOT;
  private int min_n = 10;
  private int max_n = 10;

  // Time:
  private double mc1[];
  private double alpha1[];
  private int t;


  // Generate a random instance
  public void randomInstance(boolean uniform) {
    int n = min_n + rand.nextInt(max_n - min_n + 1);

    // Generate random marginal costs and alpha
    mc1 = new double[n];
    alpha1 = new double[n];
    for(int i = 0; i < n; i++) {
      mc1[i] = rand.nextDouble() * 50.0;

      if(!uniform) {
        // Generate random (but not independent) alpha values
        // alpha is between 50%-150% of the marginal cost
        double factor = 0.5 + 1.5 * rand.nextDouble();
        alpha1[i] = factor * mc1[i];
      } else {
        // Uniform:
        alpha1[i] = 1;
      }
    }

    t = 1;
    runInstance();
  }

  public void runInstance() {
    assert mc1.length == alpha1.length;
    int n = mc1.length;

    // Copy first t
    double mc2[] = new double[t];
    double alpha2[] = new double[t];
    for(int i = 0; i < t; i++) {
      mc2[i] = mc1[i];
      alpha2[i] = alpha1[i];
    }

    // Initial cost
    double init1[] = FairShare.nonUniformInitial(mc1, alpha1);
    double init2[] = FairShare.nonUniformInitial(mc2, alpha2);

    // Final cost
    double fs1[] = FairShare.nonUniform(mc1, alpha1);
    double fs2[] = FairShare.nonUniform(mc2, alpha2);

    double series[][] = {mc1, init1, fs2, fs1};

    // Display results
    left.set(0, series[leftBox1.getSelectedIndex()], alpha1, type, outlineButton.isSelected());
    left.set(1, series[leftBox2.getSelectedIndex()], alpha1, type, outlineButton.isSelected());
    right.set(0, series[rightBox1.getSelectedIndex()], alpha1, type, outlineButton.isSelected());
    right.set(1, series[rightBox2.getSelectedIndex()], alpha1, type, outlineButton.isSelected());
    left.repaint();
    right.repaint();
    timeLabel.setText("t = " + t);

  }


  public StepPanel() {
    // Initially empty
    double nodata[] = {};
    left.add(nodata, nodata, 0, false);
    left.add(nodata, nodata, 0, false);
    right.add(nodata, nodata, 0, false);
    right.add(nodata, nodata, 0, false);

    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BorderLayout());
    JPanel leftKey = new JPanel();
//    leftKey.setLayout(new GridLayout(0,2));
//    JComboBox leftBox1 = new JComboBox(seriesNames);
    leftBox1.setForeground(Color.RED);
    leftBox1.setSelectedIndex(0);
    leftBox1.addActionListener(listen);
//    JComboBox leftBox2 = new JComboBox(seriesNames);
    leftBox2.setForeground(Color.BLUE);
    leftBox2.setSelectedIndex(3);
    leftBox2.addActionListener(listen);
    leftKey.add(leftBox1);
    leftKey.add(leftBox2);
    leftPanel.add(leftKey, BorderLayout.NORTH);
    leftPanel.add(left, BorderLayout.CENTER);
    leftPanel.add(new JLabel(" "), BorderLayout.SOUTH);

    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BorderLayout());
    JPanel rightKey = new JPanel();
//    JComboBox rightBox1 = new JComboBox(seriesNames);
    rightBox1.setForeground(Color.RED);
    rightBox1.setSelectedIndex(0);
    rightBox1.addActionListener(listen);
//    JComboBox rightBox2 = new JComboBox(seriesNames);
    rightBox2.setForeground(Color.BLUE);
    rightBox2.setSelectedIndex(2);
    rightBox2.addActionListener(listen);
    rightKey.add(rightBox1);
    rightKey.add(rightBox2);
    rightPanel.add(rightKey, BorderLayout.NORTH);
    rightPanel.add(right, BorderLayout.CENTER);
    rightPanel.add(new JLabel(" "), BorderLayout.SOUTH);
    timeLabel = new JLabel("t = 0");
    rightPanel.add(timeLabel, BorderLayout.SOUTH);

    JPanel vizPanel = new JPanel();
    vizPanel.setLayout(new GridLayout(0,2));
    vizPanel.add(leftPanel);
    vizPanel.add(rightPanel);

    // Control Panel
    uniformButton = new JCheckBox("Uniform");
    uniformButton.setSelected(true);
//    uniformButton.setActionCommand("uniform");
//    uniformButton.addActionListener(listen);

    JButton newButton = new JButton("New");
    newButton.setActionCommand("new");
    newButton.addActionListener(listen);
    JButton stepButton = new JButton("Step");
    stepButton.setActionCommand("step");
    stepButton.addActionListener(listen);
    JButton resetButton = new JButton("Reset");
    resetButton.setActionCommand("reset");
    resetButton.addActionListener(listen);

    JRadioButton linePlot = new JRadioButton("Line Plot");
    linePlot.setActionCommand("linePlot");
    linePlot.addActionListener(listen);
    JRadioButton barPlot = new JRadioButton("Bar Plot");
    barPlot.setActionCommand("barPlot");
    barPlot.addActionListener(listen);
    JRadioButton barWidthPlot = new JRadioButton("Bar Width Plot");
    barWidthPlot.setSelected(true);
    barWidthPlot.setActionCommand("barWidthPlot");
    barWidthPlot.addActionListener(listen);

    ButtonGroup group = new ButtonGroup();
    group.add(linePlot);
    group.add(barPlot);
    group.add(barWidthPlot);

    outlineButton = new JCheckBox("Outline");
    outlineButton.setSelected(false);
    outlineButton.setActionCommand("outline");
    outlineButton.addActionListener(listen);

    JPanel controlPanel = new JPanel();
    controlPanel.add(uniformButton);
    controlPanel.add(newButton);
    controlPanel.add(stepButton);
    controlPanel.add(resetButton);
//    controlPanel.add(linePlot);
    controlPanel.add(barPlot);
    controlPanel.add(barWidthPlot);
    controlPanel.add(outlineButton);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(vizPanel, BorderLayout.CENTER);
    mainPanel.add(controlPanel, BorderLayout.SOUTH);

    this.add(mainPanel);

    // Generate the initial random instance
    randomInstance(uniformButton.isSelected());

  }

  private ActionListener listen = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if(action.equals("new")) {
          randomInstance(uniformButton.isSelected());

        } else if(action.equals("step")) {
          if(t < mc1.length) {
            t++;
            runInstance();
          }

        } else if(action.equals("reset")) {
          t = 1;
          runInstance();

        } else if(action.equals("linePlot")) {
          type = Plot.LINE_PLOT;
          left.setType(Plot.LINE_PLOT);
          right.setType(Plot.LINE_PLOT);
          left.repaint();
          right.repaint();
        } else if(action.equals("barPlot")) {
          type = Plot.BAR_PLOT;
          left.setType(Plot.BAR_PLOT);
          right.setType(Plot.BAR_PLOT);
          left.repaint();
          right.repaint();
        } else if(action.equals("barWidthPlot")) {
          type = Plot.BAR_WIDTH_PLOT;
          left.setType(Plot.BAR_WIDTH_PLOT);
          right.setType(Plot.BAR_WIDTH_PLOT);
          left.repaint();
          right.repaint();

        } else if(action.equals("outline")) {
          left.setOutline(outlineButton.isSelected());
          right.setOutline(outlineButton.isSelected());
          left.repaint();
          right.repaint();

        } else if(action.equals("comboBoxChanged")) {
          runInstance();

        } else {
          System.err.println("Received unknown action: " + action);
          System.err.println("Received unknown event: " + e);
        }

      }
    };

}


