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

@SuppressWarnings("serial")


// The bottom simulation control bar
public class ControlBar extends JPanel {
  private UI ui;
  private Sim sim;

  private PlayThread playThread; // Separate process to run the simulation

  private JButton playButton;
  private JLabel timeLabel;
  private JLabel requestedLabel;
  private JLabel offeredLabel;
  private JLabel confirmedLabel;
  private JLabel completedLabel;

  public ControlBar(UI ui) {
    super();

    this.ui = ui;

    // Buttons
    JPanel buttonPanel = new JPanel();
    playThread = new PlayThread();
    new Thread(playThread, "Play thread").start();

    playButton = new JButton("play");
    playButton.setActionCommand("play");
    playButton.addActionListener(listen);

    JButton stepButton = new JButton("step");
    stepButton.setActionCommand("step");
    stepButton.addActionListener( listen );

    JButton resetButton = new JButton("reset");
    resetButton.setActionCommand("reset");
    resetButton.addActionListener( listen );

    buttonPanel.add(playButton, BorderLayout.WEST);
    buttonPanel.add(stepButton, BorderLayout.WEST);
    buttonPanel.add(resetButton, BorderLayout.WEST);

    // Stats
    JPanel statsPanel = new JPanel();
    requestedLabel = new JLabel("Requested: 0");
    offeredLabel = new JLabel("Offered: 0");
    confirmedLabel = new JLabel("Confirmed: 0");
    completedLabel = new JLabel("Completed: 0");
    timeLabel = new JLabel("Time: 00:00");
    timeLabel.setFont(new java.awt.Font("Courier New", 1, 24));
    statsPanel.add(requestedLabel);
    statsPanel.add(offeredLabel);
    statsPanel.add(confirmedLabel);
    statsPanel.add(completedLabel);

    this.setLayout(new BorderLayout());
    this.add(buttonPanel, BorderLayout.WEST);
    this.add(statsPanel, BorderLayout.CENTER);
    this.add(timeLabel, BorderLayout.EAST);
  }

  public void loadSim(Sim sim) {
    this.sim = sim;
    updateSim();
  }

  // Updates the stats and time
  public void updateSim() {
    // Stats
    requestedLabel.setText("Requested: " + sim.aserver.log.requested);
    offeredLabel.setText("Offered: " + sim.aserver.log.offered);
    confirmedLabel.setText("Confirmed: " + sim.aserver.log.confirmed);
    completedLabel.setText("Completed: " + sim.aserver.log.completed);
    // Time
    timeLabel.setText("Time: " + Time.formatTime(sim.time));
  }

  private ActionListener listen = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if(action.equals("reset")) {
          sim.reset();
          ui.updateSim();
        } else if(action.equals("play")) {
          if(playThread.play) {
            playThread.play = false;
            playButton.setText("play");
          } else {
            playThread.play = true;
            playButton.setText("pause");
          }
        } else if(action.equals("step")) {
          sim.step();
          ui.updateSim();
        } else {
          Debug.println("Received unknown event: " + e);
        }

      }
    };

  private class PlayThread implements Runnable {
    public boolean play = false;
    public int delay = 30;

    public void run() {

      while(true) {
        if(play) {
          if(sim.time >= 24 * 60) {
            play = false;
            playButton.setText("play");

//            String report = sim.aserver.log.toString();
//            JOptionPane.showMessageDialog(null, report);
//            JOptionPane.showMessageDialog(null, "Completed Successfully!");
          } else {
            sim.step();
            ui.updateSim();
          }
        }

        try {
          Thread.sleep(delay);
        } catch(InterruptedException ie) {}

      }
    }
  }

}