/*
 * $Id$
 *
 * (c) 2002 Oliver Rossmueller
 *
 * This file is part of tuxerra.
 *
 * tuxerra is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * tuxerra is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with tuxerra; if not, mailto:oliver@oross.net of have a look at
 * http://www.gnu.org/licenses/licenses.html#GPL
 */

package org.apache.jmeter.gui.panel;


import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.apache.jmeter.gui.JMeterGUIComponent;
import org.apache.jmeter.gui.document.JMeterDocument;
import org.apache.jmeter.gui.tree.*;
import org.apache.jmeter.testelement.TestPlan;

/**
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 */
public class TestPlanPanel extends DocumentPanel implements TreeSelectionListener, TreeModelListener, FocusListener
{

    private TreeModel treeModel;
    private JSplitPane treeAndMain;
    private JTree tree;
    private JPanel mainPanel;
    private JPanel cardPanel;
    private Collection availableGuis = new HashSet();


    public TestPlanPanel(JMeterDocument document)
    {
        super(document);
        treeModel = new TestPlanTreeModel(document.getElement());
        init();
    }

    private void init()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = gbc.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        treeAndMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        treeAndMain.setLeftComponent(createTreePanel());
        mainPanel = createMainPanel();
        treeAndMain.setRightComponent(new JScrollPane(mainPanel));

        // The setResizeWeight() method was added to JDK1.3. For now, JMeter should
        // remain compatible with JDK1.2.
        //treeAndMain.setResizeWeight(.2);

        treeAndMain.setContinuousLayout(true);
        add(treeAndMain, gbc);
        // select first child - root is not visible
        tree.setSelectionRow(0);
        treeModel.addTreeModelListener(this);
    }


    private JComponent createTreePanel()
    {
        tree = createTree();
        JScrollPane treePanel = new JScrollPane(tree);
        treePanel.setMinimumSize(new Dimension(200, 0));
        return treePanel;
    }


    private JTree createTree()
    {
        JTree tree = new TestPlanJTree(treeModel);
        tree.setCellRenderer(getCellRenderer());
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setExpandsSelectedPaths(true);
//        treeListener.setJTree(tree);
        tree.addTreeSelectionListener(this);

//        tree.addTreeSelectionListener(treeListener);
//        tree.addMouseListener(treeListener);
//        tree.addMouseMotionListener(treeListener);
//        tree.addKeyListener(treeListener);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.addMouseListener(new PopupListener());
        return tree;
    }


    private JPanel createMainPanel()
    {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        cardPanel = new JPanel(new CardLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.fill = constraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;

        mainPanel.add(cardPanel, constraints);
        mainPanel.addFocusListener(this);
        return mainPanel;
    }


    private TreeCellRenderer getCellRenderer()
    {
        DefaultTreeCellRenderer renderer = new TestPlanTreeCellRenderer();

        renderer.setFont(new Font("Dialog", Font.PLAIN, 11));
        return renderer;
    }


    // TreeSelectionListener
    public void valueChanged(TreeSelectionEvent e)
    {
        TestPlanTreeNode current = (TestPlanTreeNode)tree.getLastSelectedPathComponent();
        activateGUI(current);
        getDocument().setCurrentTestElement(current);
    }


    private void activateGUI(TestPlanTreeNode node)
    {
        if (node != null) {
            JComponent gui = node.getGUI();
            CardLayout layout = (CardLayout)cardPanel.getLayout();
            String card = gui.getClass().getName();

            if (!availableGuis.contains(card)) {
                cardPanel.add(gui, card);
                availableGuis.add(card);
                cardPanel.validate();
            }
            ((JMeterGUIComponent)gui).setElement(node);
            layout.show(cardPanel, card);
        }
    }


    public void treeNodesChanged(TreeModelEvent e)
    {
    }


    public void treeNodesInserted(TreeModelEvent e)
    {
        final Object[] children = e.getChildren();

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                for (int i = 0; i < children.length; i++) {
                    TreePath path = new TreePath(((TestPlanTreeNode)children[i]).getPath());
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                }
            }
        });

    }


    public void treeNodesRemoved(TreeModelEvent e)
    {
    }


    public void treeStructureChanged(TreeModelEvent e)
    {
    }


    private class PopupListener extends MouseAdapter
    {

        public void mousePressed(MouseEvent e)
        {
            maybeShowPopup(e);
        }


        public void mouseReleased(MouseEvent e)
        {
            maybeShowPopup(e);
        }


        private void maybeShowPopup(MouseEvent e)
        {
            if (e.isPopupTrigger()) {
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                TestPlanTreeNode node = (TestPlanTreeNode)path.getLastPathComponent();
                JPopupMenu popup = node.getPopupMenu(tree);

                if (popup == null) {
                    return;
                }
                if (!tree.isPathSelected(path)) {
                    tree.setSelectionPath(path);
                }
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }


    // FocusListener methods

    public void focusGained(FocusEvent e)
    {
        System.out.println(this + " focusGained");
    }


    public void focusLost(FocusEvent e)
    {
        System.out.println(this + " focusLost");
    }

}
