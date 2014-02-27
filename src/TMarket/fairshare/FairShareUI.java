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


public class FairShareUI {

  public static void main(String args[]) {
    FairShareUI ui = new FairShareUI();
  }

  public FairShareUI() {

    StepPanel stepPanel = new StepPanel();
    DelayPanel delayPanel = new DelayPanel();

    JTabbedPane tabPane = new JTabbedPane();
    tabPane.add(stepPanel, "Step");
    tabPane.add(delayPanel, "Delay");

    JFrame frame = new JFrame("FairShare");
    frame.add(tabPane);

    // Display the frame
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

  }

}
