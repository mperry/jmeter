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
import org.apache.jmeter.testelement.property.Property;


/**
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 */
public class TestPlanTreeNode extends DefaultMutableTreeNode implements TestElementConfiguration
{

    private TestPlanTreeModel treeModel;


    TestPlanTreeNode(TestElementConfiguration testElement)
    {
        super(testElement);
    }


    public TestPlanTreeNode(TestElementConfiguration testElement, TestPlanTreeModel treeModel)
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


    public TestElementConfiguration getElement()
    {
        return (TestElementConfiguration)getUserObject();
    }


    public Class getElementClass()
    {
        return getElement().getElementClass();
    }


    public ImageIcon getIcon()
    {
        return GUIFactory.getIcon(getElementClass());
    }


    public JComponent getGUI()
    {
        return GUIFactory.getGUI(getElement().getElementClass());
    }


    public JPopupMenu getPopupMenu(JTree tree)
    {
        return ((JMeterGUIComponent)getGUI()).createPopupMenu();
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
//                menuItem = new JMenuItem(subelements[i].getTagName());
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
        getElement().setName(name);
        treeModel.nodeChanged(this);
    }


//    public NamedTestElement addChildElement(NamedTestElement element) {
//        ((NamedTestElement)getUserObject()).addChildElement(element);
//        TestPlanTreeNode newNode = new TestPlanTreeNode(element, treeModel);
//        treeModel.insertNodeInto(newNode, this, treeModel.getChildCount(this));
//        return newNode;
//    }


//    public List getChildren() {
//        return ((NamedTestElement)getUserObject()).getChildren();
//    }
//
//
//    public Class[] getAllowedSubelementTypes() {
//        return ((NamedTestElement)getUserObject()).getAllowedSubelementTypes();
//    }
//
//
//    public String getFunctionalGroup() {
//        return ((NamedTestElement)getUserObject()).getFunctionalGroup();
//    }

    public String getProperty(String property)
    {
        return getElement().getProperty(property);
    }

    public void setProperty(String property, String value) throws IllegalArgumentException
    {
        getElement().setProperty(property,  value);
    }

    public String[] getPropertyNames()
    {
        return getElement().getPropertyNames();
    }

    public void addChild(TestElementConfiguration child)
    {
        getElement().addChild(child);
        treeModel.addChild(this, child);
    }

    public void removeChild(TestElementConfiguration child)
    {
        getElement().removeChild(child);
    }

    public List getChildren()
    {
        return getElement().getChildren();
    }

    public TestElementConfiguration getParentElement()
    {
        return getElement().getParentElement();
    }

    public void setParentElement(TestElementConfiguration parent)
    {
        getElement().setParentElement(parent);
    }

    public void accept(TestElementConfigurationVisitor visitor)
    {
        getElement().accept(visitor);
    }

    public void remove()
    {
        getElement().getParentElement().removeChild(getElement());
        treeModel.removeNodeFromParent(this);
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
}




