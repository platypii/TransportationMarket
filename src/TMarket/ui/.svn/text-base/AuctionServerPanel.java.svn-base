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

import TMarket.auctionserver.*;
import TMarket.common.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

@SuppressWarnings("serial")


// TODO: Auto scroll to end of console


public class AuctionServerPanel extends JPanel {
  private Sim sim;
  private Log log;

  // Auction server reports
  private JTextArea reportArea;
  private JTextArea consoleArea;
  private JTextField searchBox;
  private String query;

  // The number of lines we have already displayed
  private int lines;

  private final static Color SEARCH_COLOR = Color.WHITE;
  private final static Color ERROR_COLOR = Color.PINK;
  private final Highlighter highlighter;
  private final Highlighter.HighlightPainter painter;


  public AuctionServerPanel() {
    super();

    this.sim = null;
    this.log = null;

    // Summary report
    reportArea = new JTextArea(30, 20);

    // Console
    JPanel consolePanel = new JPanel();
    consolePanel.setLayout(new BorderLayout());
    consoleArea = new JTextArea(30, 20);//20, 80);
    consoleArea.setEditable(false);

    lines = 0;
    highlighter = new DefaultHighlighter();
    painter = new DefaultHighlighter.DefaultHighlightPainter(null);
    consoleArea.setHighlighter(highlighter);

    JScrollPane consoleScrollPane = new JScrollPane(consoleArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    // Console search
    JPanel searchPanel = new JPanel();

    searchBox = new JTextField(20);
    searchBox.getDocument().addDocumentListener(documentListen);
    query = "";

    searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
    searchPanel.add(new JLabel(" Search: "));
    searchPanel.add(searchBox);

    consolePanel.add(consoleScrollPane, BorderLayout.CENTER);
    consolePanel.add(searchPanel, BorderLayout.SOUTH);
    JSplitPane aserverSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, reportArea, consolePanel);

//    this.setLayout(new BoxLayout(aserverPanel, BoxLayout.Y_AXIS));
    this.setLayout(new BorderLayout());
    this.add(aserverSplit, BorderLayout.CENTER);
  }

  // Loads a simulation
  public void loadSim(Sim sim) {
    this.sim = sim;
    this.log = sim.aserver.log;

    // Clear existing log
    consoleArea.setText("");
    lines = 0;

    updateSim();
  }

  // TODO: Edit in separate Document, then set document (text double buffer)

  // Updates the report, and appends new console lines
  public void updateSim() {
    // Update the report
    reportArea.setText(log.getReport());

    // Append new lines on the console
    int current_size = consoleArea.getText().length();
    StringBuilder buffer = new StringBuilder();
    while(lines < log.console.size()) {
      String line = log.console.get(lines);
      if(query.equals("")) {
        buffer.append(line);
        buffer.append('\n');
      } else if(line.contains(query)) {
        buffer.append(line);
        buffer.append('\n');
      }
      lines++;
    }

    if(buffer.length() > 0) {
      String filteredConsoleAppend = buffer.toString();
      consoleArea.append(filteredConsoleAppend);

      if(!query.equals("")) {
        // TODO: Highlight
        int index = filteredConsoleAppend.indexOf(query, 0);
        // Find all matches
        while(index >= 0) {
          try {
            int start = current_size + index;
            int end = start + query.length();
            highlighter.addHighlight(start, end, painter);
//            consoleArea.setCaretPosition(end);
          } catch (BadLocationException e) {
            e.printStackTrace();
          }
          index = filteredConsoleAppend.indexOf(query, index+1);
        }
      }
    }

    // Update search box color
    if(query.equals("") || consoleArea.getText().length() > 0) {
      // Empty console, or match found
      searchBox.setBackground(SEARCH_COLOR);
    } else {
      // No match found
      searchBox.setBackground(ERROR_COLOR);
    }
  }

  // Updates the console search
  private void updateSearch() {
    // Update the console
    if(query.equals("")) {
      highlighter.removeAllHighlights();
      consoleArea.setText(log.getConsole());
    } else {
      StringBuilder buffer = new StringBuilder();
      for(String line : log.console) {
    // TODO: More advanced queries based on individual words (not literal string match)
        if(line.contains(query)) {
          buffer.append(line);
          buffer.append('\n');
        }
      }
      String filteredConsole = buffer.toString();
      consoleArea.setText(filteredConsole);

      // Highlight
      highlighter.removeAllHighlights();
      int index = filteredConsole.indexOf(query, 0);
      // Find all matches
      while(index >= 0) {
        try {
          int end = index + query.length();
          highlighter.addHighlight(index, end, painter);
//        consoleArea.setCaretPosition(end);
        } catch (BadLocationException e) {
          e.printStackTrace();
        }
        index = filteredConsole.indexOf(query, index+1);
      }
    }

    // Update search box color
    if(query.equals("") || consoleArea.getText().length() > 0) {
      // Empty console, or match found
      searchBox.setBackground(SEARCH_COLOR);
    } else {
      // No match found
      searchBox.setBackground(ERROR_COLOR);
    }
  }

  // DocumentListener for the search bar
  private DocumentListener documentListen = new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        query = searchBox.getText();
        updateSearch();
      }
      public void removeUpdate(DocumentEvent e) {
        query = searchBox.getText();
        updateSearch();
      }
      public void changedUpdate(DocumentEvent e) {
      }
    };

}