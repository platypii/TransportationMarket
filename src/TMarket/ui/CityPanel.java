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


// A JPanel for selecting a new city (grid or from file)
public class CityPanel extends JPanel {

  ButtonGroup sourceGroup;
  JPanel gridPanel;
  JPanel filePanel;
  JLabel fileLabel;

  JRadioButton gridRadioButton;
  JRadioButton fileRadioButton;

  JSpinner rowBox;
  JSpinner colBox;

  // Default values
  int num_rows = 10;
  int num_cols = 16;
  String filename;

  // c0
  // c1 c2 c3
  //    c4 c5
  // c6 c7 c8
  Component c0, c1, c2, c3, c4, c5, c6, c7, c8;

  public CityPanel() {
    super();

    // Grid vs. File radio buttons
    gridRadioButton = new JRadioButton("Grid");
    gridRadioButton.setActionCommand("gridRadioButton");
    gridRadioButton.addActionListener(listen);
    gridRadioButton.setSelected(true);
    fileRadioButton = new JRadioButton("File");
    fileRadioButton.setActionCommand("fileRadioButton");
    fileRadioButton.addActionListener(listen);
    sourceGroup = new ButtonGroup();
    sourceGroup.add(gridRadioButton);
    sourceGroup.add(fileRadioButton);

    // New grid
    rowBox = new JSpinner(new SpinnerNumberModel(num_rows, 1, 1000, 1));
    colBox = new JSpinner(new SpinnerNumberModel(num_cols, 1, 1000, 1));

    // Load from file
    fileLabel = new JLabel("(n/a)");
    if(filename != null)
      fileLabel.setText(filename);
    JButton browseButton = new JButton("Browse");
    browseButton.setActionCommand("browse");
    browseButton.addActionListener( listen );

    // Build the panel:
    c0 = new JLabel("City:");

    c1 = gridRadioButton;
    c2 = new JLabel("Rows: ");
    c3 = rowBox;

    c4 = new JLabel("Cols: ");
    c5 = colBox;

    c6 = fileRadioButton;
    c7 = fileLabel;
    c8 = browseButton;

    enableGrid();

    this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//    this.setLayout(new BoxLayout(cityPanel,BoxLayout.Y_AXIS));
//    this.setLayout(new GridLayout(3, 2));

    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);

    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);

    layout.setHorizontalGroup(
      layout.createSequentialGroup()
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(c0)
                .addComponent(c1)
                .addComponent(c6))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                       GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(c2)
                .addComponent(c4)
                .addComponent(c7))
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(c3)
                .addComponent(c5)
                .addComponent(c8))
      );
    layout.setVerticalGroup(
      layout.createSequentialGroup()
      .addComponent(c0)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(c1)
                .addComponent(c2)
                .addComponent(c3))
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(c4)
                .addComponent(c5))
      .addGap(10)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(c6)
                .addComponent(c7)
                .addComponent(c8))
      );

  }

  // Returns a new city based on the given specs
  public City getCity() {
    if(gridRadioButton.isSelected()) {
      int rows = (Integer)rowBox.getValue();
      int cols = (Integer)colBox.getValue();
      return new City(cols, rows);
    } else {
      assert fileRadioButton.isSelected();
      assert filename != null;
      return new City(filename);
    }
  }

  // Enables the grid options, disables file options
  private void enableGrid() {
    c2.setEnabled(true);
    c3.setEnabled(true);
    c4.setEnabled(true);
    c5.setEnabled(true);

    c7.setEnabled(false);
    c8.setEnabled(false);
  }

  // Enables the file options, disables grid options
  private void enableFile() {
    c2.setEnabled(false);
    c3.setEnabled(false);
    c4.setEnabled(false);
    c5.setEnabled(false);

    c7.setEnabled(true);
    c8.setEnabled(true);
  }

  // ActionListener for the various menu items.
  // This is using a short-hand notation for an anonymous sub-class.
//  private JFrame frame = SwingUtilities.getWindowAncestor( this )
  private JFrame frame = null; // Works apparently
  private ActionListener listen = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if(action.equals("gridRadioButton")) {
          enableGrid();
        } else if(action.equals("fileRadioButton")) {
          enableFile();
          // Force browse, revert to grid if cancel
          FileDialog chooser = new FileDialog(frame, "Load City File", FileDialog.LOAD);
          chooser.setVisible(true); // blocks
          if(chooser.getFile() != null) {
            filename = chooser.getDirectory() + chooser.getFile();
            fileLabel.setText(chooser.getFile());
          } else {
            // Revert to grid if they cancel
            enableGrid();
            gridRadioButton.setSelected(true);
          }
        } else if(action.equals("browse")) {
          FileDialog chooser = new FileDialog(frame, "Load City File", FileDialog.LOAD);
          chooser.setVisible(true); // blocks
          if(chooser.getFile() != null) {
            filename = chooser.getDirectory() + chooser.getFile();
            fileLabel.setText(chooser.getFile());
          }
        } else {
          Debug.println("Received unknown event: " + e);
        }
      }
    };

}