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

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

@SuppressWarnings("serial")


// TODO: Actual pickup and dropoff


public class ConsumerPanel extends JPanel {
  private Sim sim;

  // Consumer table data
  private JTable table;
  private DefaultTableModel model;


  public ConsumerPanel() {
    super();

    // Consumer Table
    model = new DefaultTableModel();
    table = new JTable(model);
    table.setAutoCreateRowSorter(true);

    model.addColumn("Consumer");
    model.addColumn("Start");
    model.addColumn("End");
    model.addColumn("Request Time");
    model.addColumn("Earliest Pickup");
    model.addColumn("Latest Pickup");
    model.addColumn("Earliest Dropoff");
    model.addColumn("Latest Dropoff");
    model.addColumn("Actual Pickup");
    model.addColumn("Actual Dropoff");
    model.addColumn("Shuttle");
    model.addColumn("Cost");

    // Right align:
    DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
    rightRenderer.setHorizontalAlignment( JLabel.RIGHT );
    table.getColumnModel().getColumn(11).setCellRenderer( rightRenderer );

    JScrollPane consumerScroll = new JScrollPane(table);
//    this.setLayout(new BoxLayout(consumerPanel, BoxLayout.Y_AXIS));
    this.setLayout(new BorderLayout());
    this.add(consumerScroll, BorderLayout.CENTER);

  }

  public void loadSim(Sim sim) {
    this.sim = sim;
    model.setRowCount(0);

    for(Consumer con : sim.consumers) {
      model.addRow(new Object[]{});
    }
    model.addRow(new Object[]{}); // Totals
    model.addRow(new Object[]{}); // Averages

    updateSim();
  }

  public void updateSim() {
    double cost = 0.0;

    for(int i = 0; i < sim.consumers.size(); i++) {
      Consumer con = sim.consumers.get(i);
      model.setValueAt(con.id(), i, 0);
      model.setValueAt(con.start, i, 1);
      model.setValueAt(con.end, i, 2);
      model.setValueAt(Time.formatTime((int)con.request_time), i, 3);
      model.setValueAt(Time.formatTime((int)con.earliest_pickup_time), i, 4);
      model.setValueAt(Time.formatTime((int)con.latest_pickup_time), i, 5);
      model.setValueAt(Time.formatTime((int)con.earliest_dropoff_time), i, 6);
      model.setValueAt(Time.formatTime((int)con.latest_dropoff_time), i, 7);
      if(con.confirmed_offer != null) {
        Offer offer = con.confirmed_offer;
        model.setValueAt(Time.formatTime((int)offer.pickup_time), i, 8);
        model.setValueAt(Time.formatTime((int)offer.dropoff_time), i, 9);
        model.setValueAt(offer.shuttle_id, i, 10);
        model.setValueAt(String.format("$%.2f", offer.cost3), i, 11);

        cost += offer.cost3;
      }

    }

    int num_consumers = sim.consumers.size();
    // Totals:
    model.setValueAt("<html><b>Total:</b></html>", num_consumers, 0);
    model.setValueAt(String.format("$%.2f", cost), num_consumers, 11);

    // Averages:
    // TODO: only divide if participating?
    model.setValueAt("<html><b>Average:</b></html>", num_consumers+1, 0);
    model.setValueAt(String.format("$%.2f", cost/num_consumers), num_consumers+1, 11);
  }

}

