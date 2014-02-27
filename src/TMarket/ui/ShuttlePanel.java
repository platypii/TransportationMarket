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
import TMarket.shuttle.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

@SuppressWarnings("serial")


public class ShuttlePanel extends JPanel {
  private Sim sim;

  // Shuttle table data
  private JTable table;
  private DefaultTableModel model;
 // private double d=0;

  public ShuttlePanel() {
    super();

    // Shuttle Table
    model = new DefaultTableModel();
    table = new JTable(model);
    table.setAutoCreateRowSorter(true);

    model.addColumn("Shuttle");
    model.addColumn("Type");
    model.addColumn("Offered");
    model.addColumn("Confirmed");
    model.addColumn("Passengers");
    model.addColumn("Distance");
    model.addColumn("Waittime");
    model.addColumn("Occupancy");
    model.addColumn("Payment");

    // Right align:
    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
    rightRenderer.setHorizontalAlignment( JLabel.RIGHT );
    table.getColumnModel().getColumn(8).setCellRenderer( rightRenderer );

    JScrollPane shuttleScroll = new JScrollPane(table);
//    shuttleScroll.setPreferredSize(viz.getPreferredSize());
    this.setLayout(new BorderLayout());
    this.add(shuttleScroll, BorderLayout.CENTER);

  }

  public void loadSim(Sim sim) {
    this.sim = sim;
    model.setRowCount(0);

    for(Shuttle sh : sim.shuttles) {
      model.addRow(new Object[]{});
    }
    model.addRow(new Object[]{}); // Totals
    model.addRow(new Object[]{}); // Averages

    updateSim();
  }

  public void updateSim() {
    int offered = 0;
    int confirmed = 0;
    int passengers = 0;
    double distance = 0.0;
    double waittime = 0.0;
    double occupancy = 0.0;
    double payment = 0.0;

    for(int i = 0; i < sim.shuttles.size(); i++) {
      Shuttle sh = sim.shuttles.get(i);

      model.setValueAt(sh.id(), i, 0);

      // TODO: get offers, confirmed, passengers, etc from the log instead of the shuttle
      if(sh instanceof SmartShuttle) {
        model.setValueAt("SmartShuttle", i, 1);
//      } else if(sh instanceof Taxi) {
//        model.setValueAt("Taxi", i, 1);
      }

      model.setValueAt(sh.offered(), i, 2);
      model.setValueAt(sh.confirmed(), i, 3);
      model.setValueAt(sh.passengers(), i, 4);
      model.setValueAt(sh.distance(), i, 5);
      model.setValueAt(sh.waittime(), i, 6);
      model.setValueAt(String.format("%.4f", sh.occupancy()), i, 7);
      model.setValueAt(String.format("$%.2f", sh.payment()), i, 8);

      offered += sh.offered();
      confirmed += sh.confirmed();
      passengers += sh.passengers();
      distance += sh.distance();
      waittime += sh.waittime();
      occupancy += sh.occupancy();
      payment += sh.payment();
    }

    int num_shuttles = sim.shuttles.size();
    // Totals:
    model.setValueAt("<html><b>Total:</b></html>", num_shuttles, 0);
    model.setValueAt(offered, num_shuttles, 2);
    model.setValueAt(confirmed, num_shuttles, 3);
    model.setValueAt(passengers, num_shuttles, 4);
    model.setValueAt(distance, num_shuttles, 5);
    model.setValueAt(waittime, num_shuttles, 6);
    model.setValueAt(String.format("%.4f", occupancy), num_shuttles, 7);
    model.setValueAt(String.format("$%.2f", payment), num_shuttles, 8);

    // Averages:
    model.setValueAt("<html><b>Average:</b></html>", num_shuttles+1, 0);
    model.setValueAt(((double)offered)/num_shuttles, num_shuttles+1, 2);
    model.setValueAt(((double)confirmed)/num_shuttles, num_shuttles+1, 3);
    model.setValueAt(((double)passengers)/num_shuttles, num_shuttles+1, 4);
    model.setValueAt(distance/num_shuttles, num_shuttles+1, 5);
    model.setValueAt(waittime/num_shuttles, num_shuttles+1, 6);
    model.setValueAt(occupancy/num_shuttles, num_shuttles+1, 7);
    model.setValueAt(String.format("%.4f", occupancy/num_shuttles), num_shuttles+1, 7);
    model.setValueAt(String.format("$%.2f", payment/num_shuttles), num_shuttles+1, 8);
  }

}