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


import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.JMeterGUIComponent;
import org.apache.jmeter.testelement.*;


/**
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 */
public class TestPlanTreeNode extends DefaultMutableTreeNode implements TestElementWrapper
{

    private TestPlanTreeModel treeModel;


    TestPlanTreeNode(TestElement testElement)
    {
        super(testElement);
    }


    public TestPlanTreeNode(TestElement testElement, TestPlanTreeModel treeModel)
    {
        super(testElement);
        this.treeModel = treeModel;
    }


    void setTreeModel(TestPlanTreeModel treeModel)
    {
        this.treeModel = treeModel;
    }


    public String getName()
    {
        return getElement().getName();
    }


    public TestElement getElement()
    {
        return (TestElement)getUserObject();
    }


    public Class getElementClass()
    {
        return getUserObject().getClass();
    }


    public ImageIcon getIcon()
    {
        return GUIFactory.getIcon(getElementClass());
    }


    public JComponent getGUI()
    {
        return GUIFactory.getGUI(getElement());
    }


    public JPopupMenu getPopupMenu(JTree tree)
    {
        return ((JMeterGUIComponent)getGUI()).createPopupMenu(this);
    }


//    private JPopupMenu createPopupMenu(JTree tree) {
//        JPopupMenu menu = new JPopupMenu();
//        JMenuItem menuItem;
//        JMenu submenu;
//        Class[] subelements = getElement().getAllowedSubelementTypes();
//
//        if (subelements.length > 0) {
//            submenu = new JMenu("Add");
//            submenu.setMnemonic(KeyEvent.VK_A);
//
//            for (int i = 0; i < subelements.length; i++) {
//                menuItem = new JMenuItem(subelements[i].getName());
//                menuItem.addActionListener(new AddAction(subelements[i], tree));
//                submenu.add(menuItem);
//                menu.add(submenu);
//            }
//        }
//        menuItem = new JMenuItem("Delete");
//        menu.add(menuItem);
//        return menu;
//    }


    public void setName(String name)
    {
        ((TestElement)getUserObject()).setName(name);
        treeModel.nodeChanged(this);
    }


//    public TestElement addChildElement(TestElement element) {
//        ((TestElement)getUserObject()).addChildElement(element);
//        TestPlanTreeNode newNode = new TestPlanTreeNode(element, treeModel);
//        treeModel.insertNodeInto(newNode, this, treeModel.getChildCount(this));
//        return newNode;
//    }


//    public List getChildren() {
//        return ((TestElement)getUserObject()).getChildren();
//    }
//
//
//    public Class[] getAllowedSubelementTypes() {
//        return ((TestElement)getUserObject()).getAllowedSubelementTypes();
//    }
//
//
//    public String getFunctionalGroup() {
//        return ((TestElement)getUserObject()).getFunctionalGroup();
//    }


    public Object getProperty(String property)
    {
        return ((TestElement)getUserObject()).getProperty(property);
    }


    public void setProperty(String property, Object value)
    {
        ((TestElement)getUserObject()).setProperty(property, value);
    }


    public void addChildElement(TestElement child)
    {
        getElement().addChildElement(child);
        treeModel.addChild(this, child);
    }


    public void removeChildElement(TestElement child)
    {
        getElement().removeChildElement(child);
    }


    public TestElement getParentElement()
    {
        return getElement().getParentElement();
    }


    public void setParent(TestElement parent)
    {
        getElement().setParent(parent);
    }


    public List getChildren()
    {
        return getElement().getChildren();
    }


    public void remove()
    {
        getElement().getParentElement().removeChildElement(getElement());
        treeModel.removeNodeFromParent(this);
    }

    public Collection getPropertyNames()
    {
        return getElement().getPropertyNames();
    }


    public String getPropertyAsString(String key)
    {
        return getElement().getPropertyAsString(key);
    }


    // Action classes

//    class AddAction implements ActionListener {
//
//        private Class elementClass;
//        private JTree tree;
//
//
//        public AddAction(Class elementClass, JTree tree) {
//            this.elementClass = elementClass;
//            this.tree = tree;
//        }
//
//
//        public void actionPerformed(ActionEvent e) {
//            try {
//                TestElement element = (TestElement)elementClass.newInstance();
//                element.setName(elementClass.getName().substring(elementClass.getPackage().getName().length()));
//                if (element.getName().startsWith(".")) {
//                    element.setName(element.getName().substring(1));
//                }
//                TestPlanTreeNode node = (TestPlanTreeNode)addChildElement(element);
//                TreePath path = new TreePath(node.getPath());
//                tree.scrollPathToVisible(path);
//                tree.setSelectionPath(path);
//            } catch (InstantiationException e1) {
//                // assert: the class has be checked before so this will not happen
//            } catch (IllegalAccessException e1) {
//                // assert: the class has be checked before so this will not happen
//            }
//        }
//    }

    public Set getValidSubelementTypes()
    {
        return getElement().getValidSubelementTypes();
    }


    public boolean isValidSubelementType(TestElement element)
    {
        return getElement().isValidSubelementType(element);
    }


    public String getFunctionalGroup()
    {
        return getElement().getFunctionalGroup();
    }


    public long getId()
    {
        return getElement().getId();
    }


    public void accept(TestElementVisitor visitor)
    {
        getElement().accept(visitor);
    }


    public DataFlavor[] getTransferDataFlavors()
    {
        return getElement().getTransferDataFlavors();
    }


    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        return getElement().isDataFlavorSupported(flavor);
    }


    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        return getElement().getTransferData(flavor);
    }


    public String toString()
    {
        return getName();
    }

    public TestElement unwrap()
    {
        return getElement();
    }
}




