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

import java.io.*;
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;


// TODO: Fix load sim


public class UI {
  Sim sim;

  JFrame frame;
  private VizPanel vizPanel;
  private AuctionServerPanel aserverPanel;
  private SharingPanel sharingPanel;
  private ShuttlePanel shuttlePanel;
  private ConsumerPanel consumerPanel;
  private ControlBar controlBar;


  // Launch the UI
  public static void main(String args[]) {
//    System.out.println("usage: java UI -nodebug [simulation files]");

    // Parse command line
    ArrayList<String> sim_files = new ArrayList<String>();
    for(int i = 0; i < args.length; i++) {
      if(args[i].equals("-nodebug")) {
        // Disable debug printing
        Debug.print_debug = false;
      } else {
        // Else assume it is a sim file
        sim_files.add(args[i]);
      }
    }

    Sim sim;
    if(sim_files.size() == 0) {
      // Default simulation (resources/test.sim)
      sim = new Sim("resources" + File.separator + "test.sim");
    } else if(sim_files.size() == 1) {
      // 1 simulation loaded into interactive GUI
      sim = new Sim(sim_files.get(0));
    } else {
      sim = new Sim(sim_files.get(0));
      // TODO: Run batch
    }
    UI ui = new UI();
    ui.loadSim(sim);

  }

  // Create the UI
  public UI() {
    frame = new JFrame("The Transportation Market");

    JMenuBar menu = new JMenuBar();
    frame.setJMenuBar(menu);

    // File menu
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    JMenuItem saveCityItem = new JMenuItem("Save City", 'C' );
    saveCityItem.setActionCommand("saveCity");
    saveCityItem.addActionListener(listen);
    fileMenu.add(saveCityItem);
    JMenuItem saveSimItem = new JMenuItem("Save Sim", 'S' );
    saveSimItem.setActionCommand("saveSim");
    saveSimItem.addActionListener(listen);
    fileMenu.add(saveSimItem);
    JMenuItem saveLogItem = new JMenuItem("Save Log", 'L' );
    saveLogItem.setActionCommand("saveLog");
    saveLogItem.addActionListener(listen);
    fileMenu.add(saveLogItem);
    JMenuItem exitItem = new JMenuItem("Exit", 'X' );
    exitItem.setActionCommand("exit");
    exitItem.addActionListener(listen);
    fileMenu.add(exitItem);

    // Simulation menu
    JMenu simMenu = new JMenu("Simulation");
    simMenu.setMnemonic('S');
    JMenuItem loadSimItem = new JMenuItem("Load Simulation...", 'L');
    loadSimItem.setActionCommand("loadSim");
    loadSimItem.addActionListener(listen);
    simMenu.add(loadSimItem);
    JMenuItem newSimItem = new JMenuItem("New Simulation...", 'N');
    newSimItem.setActionCommand("newSim");
    newSimItem.addActionListener(listen);
    simMenu.add(newSimItem);

    // Batch Menu
    JMenu batchMenu = new JMenu("Testing");
    batchMenu.setMnemonic('B');
    JMenuItem loadBatchItem = new JMenuItem("Load Batch...", 'L');
    loadBatchItem.setActionCommand("loadBatch");
    loadBatchItem.addActionListener(listen);
    batchMenu.add(loadBatchItem);
    JMenuItem genBatchItem = new JMenuItem("Generate Batch...", 'G');
    genBatchItem.setActionCommand("genBatch");
    genBatchItem.addActionListener(listen);
    batchMenu.add(genBatchItem);

    menu.add(fileMenu);
    menu.add(simMenu);
    menu.add(batchMenu);

    // Add the tabbed panels
    JTabbedPane tabbedPane = new JTabbedPane();
    vizPanel = new VizPanel();
    aserverPanel = new AuctionServerPanel();
    sharingPanel = new SharingPanel();
    shuttlePanel = new ShuttlePanel();
    consumerPanel = new ConsumerPanel();
    tabbedPane.add("City", vizPanel);
    tabbedPane.add("Auction Server", aserverPanel);
    tabbedPane.add("Cost Sharing", sharingPanel);
    tabbedPane.add("Shuttles", shuttlePanel);
    tabbedPane.add("Consumers", consumerPanel);

    // Control Bar
    controlBar = new ControlBar(this);

    frame.add(tabbedPane, BorderLayout.CENTER);
    frame.add(controlBar, BorderLayout.SOUTH);

    // display the frame
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  // Loads a new simulation into the UI
  public void loadSim(Sim sim) {
    if(this.sim != sim) {
      if(this.sim != null)
        this.sim.terminate();
      this.sim = sim;
      controlBar.loadSim(sim);
      vizPanel.loadSim(sim);
      aserverPanel.loadSim(sim);
      sharingPanel.loadSim(sim);
      shuttlePanel.loadSim(sim);
      consumerPanel.loadSim(sim);
    }
  }

  // Updates the UI
  public void updateSim() {
    controlBar.updateSim();
    vizPanel.updateSim();
    aserverPanel.updateSim();
    sharingPanel.updateSim();
    shuttlePanel.updateSim();
    consumerPanel.updateSim();
  }

  // ActionListener for the various controls
  private NewSimDialog newSimDialog = new NewSimDialog(this);
  private BatchDialog batchDialog = new BatchDialog(frame);
  private ActionListener listen = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        // File menu
        if(action.equals("loadSim")) {
//          LoadSimDialog simDialog = new LoadSimDialog();
          FileDialog simDialog = new FileDialog(frame, "Load Simulation File", FileDialog.LOAD);
          simDialog.setVisible(true);
          if(simDialog.getFile() != null) {
            String filename = simDialog.getDirectory() + simDialog.getFile();
            sim.terminate();
            loadSim(new Sim(filename));
          }
        } else if(action.equals("newSim")) {
          newSimDialog.show();

        } else if(action.equals("loadBatch")) {
          JFileChooser fc = new JFileChooser();
          FileNameExtensionFilter filter = new FileNameExtensionFilter("Sim Files", "sim");
          fc.setFileFilter(filter);
          fc.setMultiSelectionEnabled(true);
          int returnVal = fc.showOpenDialog(frame);
          if(returnVal == JFileChooser.APPROVE_OPTION) {
            // Results
            try{
              SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
              String now = formatter.format(new Date());
              String filename = "results" + File.separator + "results-" + now + ".csv";
              PrintWriter out = new PrintWriter(new FileWriter(filename));

              File files[] = fc.getSelectedFiles();
              // Loop thru all selected files:
              // TODO: Progress bar
              for(File file : files) {
                // Run simulation
                System.out.println("Running simulation: " + file);

                Sim sim = new Sim(file);
                sim.play();
                // Print out results
//                System.out.println(sim.aserver.log.getReport());
                out.println(file.getName() + ", " + sim.getReport());
              }

              out.close();
              System.out.println("Finished Batch!");
            } catch(IOException exn) {
              System.out.println(exn);
            }
          }

        } else if(action.equals("genBatch")) {
          batchDialog.show();

        } else if(action.equals("saveSim")) {
          // Save results
          FileDialog saveDialog = new FileDialog(frame, "Save Sim File", FileDialog.SAVE);
          saveDialog.setVisible(true);
          if(saveDialog.getFile() != null) {
            String filename = saveDialog.getDirectory() + saveDialog.getFile();
            sim.saveSim(filename);
          }

        } else if(action.equals("saveCity")) {
          // Save results
          FileDialog saveDialog = new FileDialog(frame, "Save City File", FileDialog.SAVE);
          saveDialog.setVisible(true);
          if(saveDialog.getFile() != null) {
            String filename = saveDialog.getDirectory() + saveDialog.getFile();
            sim.city.saveCity(filename);
          }

        } else if(action.equals("saveLog")) {
          // Save results
          FileDialog saveDialog = new FileDialog(frame, "Save Log File", FileDialog.SAVE);
          saveDialog.setVisible(true);
          if(saveDialog.getFile() != null) {
            String filename = saveDialog.getDirectory() + saveDialog.getFile();
            sim.aserver.log.saveLog(filename);
          }

        } else if(action.equals("exit")) {
          System.exit(0);

        } else {
          System.err.println("Received unknown event: " + e);
        }

      }
    };

}


