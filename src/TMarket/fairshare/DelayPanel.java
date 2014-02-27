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


public class DelayPanel extends JPanel {

  private Plot left = new Plot();
  private Plot right = new Plot();

  private JCheckBox outlineButton;
  private JCheckBox uniformButton;
  private JLabel deltaLabel;

  private Random rand = new Random();

  // Options:
  private int batch_instances = 10000;
  private int type = Plot.BAR_WIDTH_PLOT;
  private int min_n = 10;
  private int max_n = 10;

  // Average
  private double total[] = new double[max_n];
  private double avg[] = new double[max_n];
  private int instances = 0;


  // Generate a random instance
  public double randomInstance(boolean uniform) {
    int n = min_n + rand.nextInt(max_n - min_n + 1);

    // Generate random marginal costs and alpha
    double mc1[] = new double[n];
    double alpha1[] = new double[n];
    for(int i = 0; i < n; i++) {
      mc1[i] = rand.nextDouble() * 50.0;

      if(!uniform) {
        // Generate random (but not independent) alpha values
        // alpha is between 50%-150% of the marginal cost
        double factor = 0.1 + 2.0 * rand.nextDouble();
        alpha1[i] = factor * mc1[i];
      } else {
        // Uniform:
        alpha1[i] = 1;
      }
    }

    double delta = runInstance(mc1, alpha1);
    return delta;
  }

  private void runBatch() {
    Stat delta_stats = new Stat();
    for(int i = 0; i < batch_instances; i++) {
      double delta = randomInstance(uniformButton.isSelected());
      delta_stats.addSample(delta);
    }
    System.out.println("delta = " + delta_stats);
  }


  public double runInstance(double mc1[], double alpha1[]) {
//    return runCostshareCompare(mc1,alpha1);
    return runPermuteInstance(mc1,alpha1);
  }

  // Returns the change in cost from the permutation
  public double runPermuteInstance(double mc1[], double alpha1[]) {
    assert mc1.length == alpha1.length;
    int n = mc1.length;

    double mc2[] = new double[n];
    double alpha2[] = new double[n];
    for(int i = 0; i < n; i++) {
      mc2[i] = mc1[i];
      alpha2[i] = alpha1[i];
    }

    // Random permutation, move i to j
/*
    int i = rand.nextInt(n - 1);
    int j = i + 1 + rand.nextInt(n - i - 1);
    move(mc2, i, j);
    move(alpha2, i, j);
*/

    // Random permutation, shuffle i+1 to n, move i to j
/*
    int i = rand.nextInt(n - 1);
    int j = i + rand.nextInt(n - i - 1);
    for(int x = 0; x < 1000; x++) {
      int y = i + 1 + rand.nextInt(n - i - 1);
      int z = i + 1 + rand.nextInt(n - i - 1);
      swap(mc2, y, z);
      swap(alpha2, y, z);
    }
    swap(mc2, i, j);
    swap(alpha2, i, j);
*/

    // Random permutation, move i to n-1, some users dropout
    int i = rand.nextInt(n - 1);
    int j = n-1;
    move(mc2, i, n-1);
    move(alpha2, i, n-1);

    for(int k = i; k < j; ) {
      if(rand.nextBoolean()) {
        move(mc2, k, n-1);
        move(alpha2, k, n-1);
        j--;
      } else {
        k++;
      }
    }

    // Initial cost
    double init1[] = FairShare.nonUniformInitial(mc1, alpha1);
    double init2[] = FairShare.nonUniformInitial(mc2, alpha2);

    // Final cost
    double fs1[] = FairShare.nonUniform(mc1, alpha1);
    double fs2[] = FairShare.nonUniform(mc2, alpha2);

    System.out.println("Delta: fs1["+i+"] = " + fs1[i] + ", fs2["+j+"] = " + fs2[j]);

    // Compute averages
    // TODO: Only computes avg for left plot
    instances++;
    for(int k = 0; k < n; k++) {
      total[k] += fs1[k];
      avg[k] = total[k] / instances;
    }

    double delta = fs2[j] - fs1[i]; // Change in cost due to delaying
//    System.out.println(delta);
    if(delta < -0.0000001) { // Epsilon
      System.out.println("Counterexample!! fs1["+i+"] = " + fs1[i] + ", fs2["+j+"] = " + fs2[j]);
//      assert false;
    }

    // Display results
//    left.set(0, mc1, alpha1, type, outlineButton.isSelected());
    left.set(1, fs1, alpha1, type, outlineButton.isSelected());
    left.set(2, init1, alpha1, type, outlineButton.isSelected());
    left.set(0, avg, alpha1, Plot.LINE_PLOT, true); // Avg
//    right.set(0, mc2, alpha2, type, outlineButton.isSelected());
    right.set(1, fs2, alpha2, type, outlineButton.isSelected());
    right.set(2, init2, alpha2, type, outlineButton.isSelected());
    left.repaint();
    right.repaint();
    deltaLabel.setText("+$" + String.format("%.6f", delta));

    return delta;
  }

  // Swap i with j
  private void swap(double a[], int i, int j) {
    double temp = a[i];
    a[i] = a[j];
    a[j] = temp;
  }

  // Move i to j in a[], shift i+1..j back.
  private void move(double a[], int i, int j) {
    double temp = a[i];
    for(int k = i; k < j; k++) {
      a[k] = a[k+1];
    }
    a[j] = temp;
  }

/*
  // Returns the change in cost between two costshare algorithms
  public double runCostshareCompare(double mc1[], double alpha1[]) {
    assert mc1.length == alpha1.length;
    int n = mc1.length;

    // Run FairShare
    double fs1[] = FairShare.nonUniform(mc1, alpha1);
    double fs2[] = FairShare.nonUniform2(mc1, alpha1);

    // Compute averages
    // TODO: Only computes avg for left plot
    instances++;
    for(int k = 0; k < n; k++) {
      total[k] += fs1[k];
      avg[k] = total[k] / instances;
    }

    for(int k = 0; k < n; k++) {
      double delta = fs2[k] - fs1[k]; // Change in cost due to delaying
//    System.out.println(delta);
      if(delta < -0.0000001 || 0.0000001 < delta) { // Epsilon
        System.out.println("Counterexample!! fs1["+k+"] = " + fs1[k] + ", fs2["+k+"] = " + fs2[k]);
      }
    }
    double delta = 0;

    // Display results
    left.set(0, mc1, alpha1, type, outlineButton.isSelected());
    left.set(1, fs1, alpha1, type, outlineButton.isSelected());
    left.set(2, avg, alpha1, Plot.LINE_PLOT, true);
    right.set(0, mc1, alpha1, type, outlineButton.isSelected());
    right.set(1, fs2, alpha1, type, outlineButton.isSelected());
    left.repaint();
    right.repaint();
    deltaLabel.setText("+$" + String.format("%.6f", delta));

    return delta; // Meaningless in this context
  }
*/

  public DelayPanel() {

    // Viz Panel
    double nodata[] = {};
    left.add(nodata, nodata, 0, false);
    left.add(nodata, nodata, 0, false);
    left.add(nodata, nodata, 0, false);
    right.add(nodata, nodata, 0, false);
    right.add(nodata, nodata, 0, false);
    right.add(nodata, nodata, 0, false);
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BorderLayout());
    leftPanel.add(new JLabel("Truthful:"), BorderLayout.NORTH);
    leftPanel.add(left, BorderLayout.CENTER);
    leftPanel.add(new JLabel(" "), BorderLayout.SOUTH);
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BorderLayout());
    rightPanel.add(new JLabel("Delayed:"), BorderLayout.NORTH);
    rightPanel.add(right, BorderLayout.CENTER);
    deltaLabel = new JLabel(" $0");
    rightPanel.add(deltaLabel, BorderLayout.SOUTH);

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
    JButton batchButton = new JButton("Batch");
    batchButton.setActionCommand("batch");
    batchButton.addActionListener(listen);

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
    outlineButton.setSelected(true);
    outlineButton.setActionCommand("outline");
    outlineButton.addActionListener(listen);

    JPanel controlPanel = new JPanel();
    controlPanel.add(uniformButton);
    controlPanel.add(newButton);
    controlPanel.add(batchButton);
    controlPanel.add(linePlot);
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

        } else if(action.equals("batch")) {
          runBatch();

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

        } else {
          System.err.println("Received unknown event: " + e);
        }

      }
    };

}