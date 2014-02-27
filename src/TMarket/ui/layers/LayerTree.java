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

package TMarket.ui.layers;

import TMarket.common.*;
import TMarket.shuttle.*;
import TMarket.consumer.*;
import TMarket.ui.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

@SuppressWarnings("serial")


// TODO: Fix quirky layers panel
// TODO:  collapse a long list, processes click after collapse, and thus clicks wrong


public class LayerTree extends JPanel {

  private Viz viz;
  private Sim sim;

  // Visualization options
  private LayerNode.Folder vizOptions;

  // JTree copy vizOptions
  private TreeNode root;

  // Components
  private DefaultTreeModel treeModel;
  private LayerRenderer renderer;
//  private LayerEditor editor;
  private JTree tree;


  public LayerTree(Viz viz) {
    super();

    this.viz = viz;

    // Build tree
    treeModel = new DefaultTreeModel(null, true);
    tree = new JTree(treeModel);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addMouseListener(new NodeSelectionListener());

    // Checkbox renderer
    renderer = new LayerRenderer();
    tree.setCellRenderer(renderer);

    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);

    JScrollPane sp = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//    this.setPreferredSize(new Dimension(180, 20));

    this.setLayout(new BorderLayout());
    this.add(sp, BorderLayout.CENTER);

  }

  public void loadSim(Sim sim, LayerNode.Folder vizOptions) {
    this.sim = sim;
    this.vizOptions = vizOptions;

    // Build tree based on vizOptions
    root = new TreeNode(vizOptions);
    treeModel.setRoot(root);
    treeModel.nodeStructureChanged(root);

    // Expand all nodes added so far
    // TODO: Only expand up to depth n
//    for(int i = 0; i < tree.getRowCount(); i++) {
//      tree.expandRow(i);
//    }

    // Expand up to depth 2
    DefaultMutableTreeNode node = root;
    while(node != null) {
      if(node.getLevel() <= 1) {
        tree.expandPath(new TreePath(node.getPath()));
      }
      node = node.getNextNode();
    }


    repaint();

  }


  // Tree structure that mimics the LayerNode tree
  private class TreeNode extends DefaultMutableTreeNode {
    private LayerNode node;
    public TreeNode(LayerNode node) {
      super(node.getName());
      this.node = node;
      // Recursively build tree
      if(node instanceof LayerNode.Folder)
        for(LayerNode child : ((LayerNode.Folder)node).children())
          this.add(new TreeNode(child));
    }
  }

  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    return new Dimension(150, d.height);
  }

  // Listen for mouse clicks to toggle selection
  private class NodeSelectionListener extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {

      if(e.getClickCount() == 1) {
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
//        TreePath path = tree.getSelectionPath();
        if(path != null) {
          LayerNode node = ((TreeNode)path.getLastPathComponent()).node;
          // Processes a click on this node's check box ((some,all)->none, none->all)
          if(node.isSelected() == TriState.NONE) {
            node.setSelected(true);
          } else {
            node.setSelected(false);
          }
          tree.repaint();
          viz.repaint();
        }
      }
    }
  }

  private class LayerRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {
    private LayerCell2 cell = new LayerCell2();

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus) {

      LayerNode node = ((TreeNode)value).node;

      // Check box state:
      if(node.isSelected() == TriState.NONE) {
        cell.check.setSelected(false);
        cell.check.setEnabled(true);
      } else if(node.isSelected() == TriState.SOME) {
        // TODO: Some children selected
        cell.check.setSelected(true);
        cell.check.setEnabled(false);
      } else {
        cell.check.setSelected(true);
        cell.check.setEnabled(true);
      }
      cell.label.setText(node.getName());
      cell.label.setForeground(node.getColor());
      cell.label.setIcon(node.getIcon());

      return cell;
    }
  }

  // A class with a checkbox and label
  private class LayerCell extends JComponent { // TODO: Component
    protected JCheckBox check = new JCheckBox();
    protected JLabel label = new JLabel();

    public LayerCell() {
      super();
//      this.setLayout(null); // TODO: auto layout instead of manual?
//      this.setLayout(new BorderLayout()); // TODO: auto layout instead of manual?
      this.add(check);
      this.add(label);
      check.setBackground(UIManager.getColor("Tree.textBackground"));
      label.setForeground(UIManager.getColor("Tree.textForeground"));
    }

    public Dimension getPreferredSize() {
      Dimension d_check = check.getPreferredSize();
      Dimension d_label = label.getPreferredSize();
      Dimension dim = new Dimension(d_check.width + d_label.width,
                                     Math.max(d_label.height, d_check.height));
      return dim;
    }

    public void doLayout() {
      super.doLayout();

//      Debug.println("LayerRenderer.doLayout();");

      Dimension d_check = check.getPreferredSize();
      Dimension d_label = label.getPreferredSize();
      int y_check = 0;
      int y_label = 0;
      if(d_check.height < d_label.height) {
        y_check = (d_label.height - d_check.height) / 2;
      } else {
        y_label = (d_check.height - d_label.height) / 2;
      }
      check.setBounds(0, y_check, d_check.width, d_check.height);
      label.setBounds(d_check.width, y_label, d_label.width + 50, d_label.height); // TODO: HACK! +50
    }
  }





  // A class with a checkbox and label
  private class LayerCell2 extends JComponent { // TODO: Component
    protected JCheckBox check = new JCheckBox();
    protected JLabel label = new JLabel();

    public LayerCell2() {
      super();
//      this.setLayout(null); // TODO: auto layout instead of manual?
      this.setLayout(new BorderLayout()); // TODO: auto layout instead of manual?
      this.add(check, BorderLayout.WEST);
      this.add(label, BorderLayout.CENTER);
      check.setBackground(UIManager.getColor("Tree.textBackground"));
//      label.setForeground(UIManager.getColor("Tree.textForeground"));
    }

    public Dimension getPreferredSize() {
      Dimension d_check = check.getPreferredSize();
      Dimension d_label = label.getPreferredSize();
      Dimension dim = new Dimension(d_check.width + d_label.width,
                                     Math.max(d_label.height, d_check.height));
      return dim;
    }

  }


}