/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 * if any, must include the following acknowledgment:
 * "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself,
 * if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 * "Apache JMeter" must not be used to endorse or promote products
 * derived from this software without prior written permission. For
 * written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 * "Apache JMeter", nor may "Apache" appear in their name, without
 * prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.jmeter.gui.tree;


import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.apache.jmeter.testelement.NamedTestElement;
import org.apache.jmeter.testelement.TestElementConfiguration;


/**
 * Special JTree used for the test plan tree panel. Implements clipboard operations.
 *
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 */

public class TestPlanJTree extends JTree implements TreeSelectionListener, DragGestureListener
{

    protected TreePath selectedTreePath = null;
    protected TestPlanTreeNode selectedNode = null;
    private DragSource dragSource = null;


    public TestPlanJTree(TreeModel model)
    {
        super(model);

        addTreeSelectionListener(this);
        dragSource = DragSource.getDefaultDragSource();
        DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK, this);

        // Eliminates right mouse clicks as valid actions to avoid conflicts with JPopupMenu for the JTree
        dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);

        setDropTarget(new DropTarget(this, new TargetAdapter(this)));
        getActionMap().put("cut", new CutAction(this));
        getActionMap().put("copy", new CopyAction(this));
        getActionMap().put("paste", new PasteAction(this));
        setRowHeight(21);
    }


    /** DragGestureListener interface method */
    public void dragGestureRecognized(DragGestureEvent e)
    {
        //Get the selected node
        TestPlanTreeNode dragNode = getSelectedNode();
        if (dragNode != null)
        {
            Transferable transferable = (Transferable)dragNode.getUserObject();
            Cursor cursor = DragSource.DefaultCopyNoDrop;

            if (e.getDragAction() == DnDConstants.ACTION_MOVE)
            {
                cursor = DragSource.DefaultMoveNoDrop;
            }
            dragSource.startDrag(e, cursor, transferable, new SourceAdapter(this, dragNode));
        }
    }


    public TestPlanTreeNode getSelectedNode()
    {
        return selectedNode;
    }


    public void valueChanged(TreeSelectionEvent evt)
    {
        selectedTreePath = evt.getNewLeadSelectionPath();
        if (selectedTreePath == null)
        {
            selectedNode = null;
            return;
        }
        selectedNode = (TestPlanTreeNode)selectedTreePath.getLastPathComponent();
    }


    private static class SourceAdapter extends DnDSourceAdapter
    {

        private JTree tree;
        TestPlanTreeNode dragNode;


        public SourceAdapter(JTree tree, TestPlanTreeNode dragNode)
        {
            this.tree = tree;
            this.dragNode = dragNode;
        }


        public void dragDropEnd(DragSourceDropEvent event)
        {
            if (event.getDropSuccess() && (event.getDropAction() == DnDConstants.ACTION_MOVE))
            {
                dragNode.remove();
            }
        }
    };






    private static class TargetAdapter extends DnDTargetAdapter
    {

        private TestPlanJTree tree;


        public TargetAdapter(TestPlanJTree tree)
        {
            this.tree = tree;
        }


        protected Object getTarget(Point point)
        {
            return tree.getPathForLocation(point.x, point.y);
        }


        public void drop(DropTargetDropEvent event)
        {
            DropTargetContext targetContext = event.getDropTargetContext();
            Transferable transferable = event.getTransferable();
            TreePath targetPath = (TreePath)getTarget(event.getLocation());

            if (targetPath == null)
            {
                event.rejectDrop();
            } else
            {
                TestElementConfiguration element = null;

                try
                {
                    element = (TestElementConfiguration)transferable.getTransferData(TestElementConfiguration.DATAFLAVOR);
                    TestPlanTreeNode target = (TestPlanTreeNode)targetPath.getLastPathComponent();

                    event.acceptDrop(event.getDropAction());
                    target.addChild(element);
                    targetContext.dropComplete(true);
                    return;
                } catch (UnsupportedFlavorException e)
                {
                    event.rejectDrop();
                } catch (IOException e)
                {
                    event.rejectDrop();
                }
            }
        }


        public void dragOver(DropTargetDragEvent event)
        {
            TreePath targetPath = (TreePath)getTarget(event.getLocation());

            if (targetPath != null)
            {
                TreePath sourcePath = new TreePath(tree.getSelectedNode().getPath());
                if (testDropTarget(targetPath, sourcePath))
                {
                    event.acceptDrag(event.getDropAction());
                } else
                {
                    event.rejectDrag();
                }
            } else
            {
                event.rejectDrag();
            }
        }


        private boolean testDropTarget(TreePath destination, TreePath dropper)
        {
            if (destination == null)
            {
                return false;
            }

            TestPlanTreeNode node = (TestPlanTreeNode)destination.getLastPathComponent();

            if (destination.equals(dropper))
            {
                return false;
            }

            TestPlanTreeNode source = (TestPlanTreeNode)dropper.getLastPathComponent();
//todo:            if (!node.isValidSubelementType(source.getElement()))
//            {
//                return false;
//            }

            return true;
        }
    }


    private static class CutAction extends AbstractAction implements ClipboardOwner
    {

        private JTree tree;


        public CutAction(JTree tree)
        {
            this.tree = tree;
        }


        public void actionPerformed(ActionEvent e)
        {
            TestPlanTreeNode node = (TestPlanTreeNode)tree.getLastSelectedPathComponent();

            if (node != null)
            {
                TestElementConfiguration element = node.getElement();
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                node.remove();
                clipboard.setContents(element, this);
            }
        }


        public void lostOwnership(Clipboard clipboard, Transferable contents)
        {
        }
    }


    private static class CopyAction extends AbstractAction implements ClipboardOwner
    {

        private JTree tree;


        public CopyAction(JTree tree)
        {
            this.tree = tree;
        }


        public void actionPerformed(ActionEvent e)
        {
            TestPlanTreeNode node = (TestPlanTreeNode)tree.getLastSelectedPathComponent();

            if (node != null)
            {
                TestElementConfiguration element = node.getElement();
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(element, this);
            }
        }


        public void lostOwnership(Clipboard clipboard, Transferable contents)
        {
        }
    }


    private static class PasteAction extends AbstractAction
    {

        private JTree tree;


        public PasteAction(JTree tree)
        {
            this.tree = tree;
        }


        public void actionPerformed(ActionEvent e)
        {
            TestPlanTreeNode node = (TestPlanTreeNode)tree.getLastSelectedPathComponent();

            if (node != null)
            {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable transferable = clipboard.getContents(this);

                if (transferable != null)
                {
                    try
                    {
                        TestElementConfiguration element = (TestElementConfiguration)transferable.getTransferData(TestElementConfiguration.DATAFLAVOR);
                        node.addChild(element);
                    } catch (UnsupportedFlavorException e1)
                    {
                        // todo: handle or log
                        e1.printStackTrace();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
