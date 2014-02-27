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


// A dialog box which prompts the user for parameters to a randomly generated simulation
public class NewSimDialog {
  private UI ui;

  private JDialog dialog;

  private CityPanel cityPanel;
  private JSpinner consumersBox;
  private JSpinner smartshuttlesBox;

  // Default values
  String filename;
  int num_consumers = 100;
  int num_smartshuttles = 10;


  public NewSimDialog(UI ui) {
    this.ui = ui;

    dialog = new JDialog(ui.frame, true);

    JPanel mainPanel = new JPanel();
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10,30,10,30));
    mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
    dialog.getContentPane().add(mainPanel);

    // City selection
    cityPanel = new CityPanel();

    // Participants (consumer + shuttles)
    JPanel participantsPanel = new JPanel();
    participantsPanel.setLayout(new GridLayout(3, 2));

    participantsPanel.add(new JLabel("Consumers: "));
    consumersBox = new JSpinner(new SpinnerNumberModel(num_consumers, 0, 100000, 1));
    participantsPanel.add(consumersBox);

    participantsPanel.add(new JLabel("Smart Shuttles: "));
    smartshuttlesBox = new JSpinner(new SpinnerNumberModel(num_smartshuttles, 0, 10000, 1));
    participantsPanel.add( smartshuttlesBox );

    // OK and Cancel buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    JButton okButton = new JButton("OK");
    okButton.setActionCommand("ok");
    okButton.addActionListener(listen);
    buttonPanel.add(okButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("cancel");
    cancelButton.addActionListener(listen);
    buttonPanel.add(cancelButton);

    mainPanel.add(cityPanel);
    mainPanel.add(participantsPanel);
    mainPanel.add(buttonPanel);

    dialog.getRootPane().setDefaultButton(okButton);
    dialog.pack();
    dialog.setLocationRelativeTo(ui.frame);
    dialog.setTitle("Input");
    dialog.setResizable(false);
  }

  // Show the dialog
  public void show() {
    dialog.setVisible(true);
  }

  // ActionListener for the various menu items. 
  // This is using a short-hand notation for an anonymous sub-class.
  private ActionListener listen = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if(action.equals("ok")) {
          City city = cityPanel.getCity();

          int num_consumers = (Integer)consumersBox.getValue();
          int num_smartshuttles = (Integer)smartshuttlesBox.getValue();

          Sim sim = new Sim(city, num_consumers, num_smartshuttles);
          ui.loadSim(sim);

          dialog.setVisible(false);
          dialog.dispose();
        } else if(action.equals("cancel")) {
          dialog.setVisible(false);
          dialog.dispose();
        } else {
          Debug.println("Received unknown event: " + e);
        }
      }
    };
}


