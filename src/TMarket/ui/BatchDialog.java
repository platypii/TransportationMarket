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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class BatchDialog {
  private JDialog dialog;

  private CityPanel cityPanel;

  private JSpinner conMinSpinner;
  private JSpinner conMaxSpinner;
  private JSpinner conStepSpinner;

  private JSpinner ssMinSpinner;
  private JSpinner ssMaxSpinner;
  private JSpinner ssStepSpinner;

//  private JProgressBar progressBar;


  public BatchDialog(JFrame frame) {
    dialog = new JDialog(frame, true);

    JPanel mainPanel = new JPanel();
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10,30,10,30));
    mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
    dialog.getContentPane().add(mainPanel);

    // City selection
    cityPanel = new CityPanel();

    // Batch options
    JPanel batchOptPanel = new JPanel();
    batchOptPanel.setLayout(new GridLayout(4, 4));

    batchOptPanel.add(new JLabel());
    batchOptPanel.add(new JLabel("Min"));
    batchOptPanel.add(new JLabel("Max"));
    batchOptPanel.add(new JLabel("Step"));

    // Consumers
    conMinSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 100000, 1));
    conMaxSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 100000, 1));
    conStepSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    batchOptPanel.add(new JLabel("Consumers: "));
    batchOptPanel.add(conMinSpinner);
    batchOptPanel.add(conMaxSpinner);
    batchOptPanel.add(conStepSpinner);
    // SmartShuttles
    ssMinSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 10000, 1));
    ssMaxSpinner = new JSpinner(new SpinnerNumberModel(20, 0, 10000, 1));
    ssStepSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    batchOptPanel.add(new JLabel("SmartShuttles: "));
    batchOptPanel.add(ssMinSpinner);
    batchOptPanel.add(ssMaxSpinner);
    batchOptPanel.add(ssStepSpinner);

    // TODO: add number of instances count
    // instances = (max - min + 1) / step

    // OK and Cancel buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    JButton okButton = new JButton("Run Batch");
    okButton.setActionCommand("runBatch");
    okButton.addActionListener(listen);
    buttonPanel.add(okButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("cancel");
    cancelButton.addActionListener(listen);
    buttonPanel.add(cancelButton);

    mainPanel.add(cityPanel);
    mainPanel.add(batchOptPanel);
    mainPanel.add(buttonPanel);

    dialog.getRootPane().setDefaultButton(okButton);
    dialog.pack();
    dialog.setLocationRelativeTo(frame);
    dialog.setTitle("Input");
    dialog.setResizable(false);

  }

  // Show the dialog
  public void show() {
    dialog.setVisible(true);
  }

  // ActionListener for the various controls
  private ActionListener listen = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if(action.equals("runBatch")) {
          City city = cityPanel.getCity();
          int con_min = (Integer)conMinSpinner.getValue();
          int con_max = (Integer)conMinSpinner.getValue();
          int con_step = (Integer)conMinSpinner.getValue();
          int ss_min = (Integer)ssMinSpinner.getValue();
          int ss_max = (Integer)ssMaxSpinner.getValue();
          int ss_step = (Integer)ssStepSpinner.getValue();

          // Check input
          if(con_min <= con_max && ss_min <= ss_max) {
            int con_instances = (con_max-con_min+1) / con_step;
            int ss_instances = (ss_max - ss_min+1) / ss_step;
            int num_instances = con_instances * ss_instances;
            ProgressMonitor progressMonitor = new ProgressMonitor(dialog, "", "",
                                                                  0, num_instances);

            int count = 0;
            for(int con = con_min; con <= con_max; con++) {
              for(int ss = ss_min; ss <= ss_max; ss++) {
                if(progressMonitor.isCanceled()) {
                  // Cancel
                  progressMonitor.close();
                  return;
                } else {
                  // Run the instance
                  Sim instance = new Sim(city, con, ss);
                  instance.play();
                  // TODO: Output stats

                  // update progress bar
                  progressMonitor.setProgress(count++);
                }
              }
            }
//          runBatchButton.setEnabled(true);
          }

        } else if(action.equals("cancel")) {
          dialog.setVisible(false);
          dialog.dispose();
        } else {
          Debug.println("Received unknown event: " + e);
        }

      }
    };


}