/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 - 2003 The Apache Software Foundation.  All rights
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
package org.apache.jmeter.control.gui;


import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.util.MenuFactory;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 *@author    Michael Stover
 *@created   $Date$
 *@version   1.0
 ***************************************/

public class TestPlanGui extends AbstractJMeterGuiComponent
{

    JCheckBox functionalMode;
    ArgumentsPanel argsPanel;

    /****************************************
     * !ToDo (Constructor description)
     ***************************************/
    public TestPlanGui()
    {
        init();
    }

    /****************************************
     * !ToDo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public JPopupMenu createPopupMenu()
    {
        JPopupMenu pop = new JPopupMenu();
        JMenu addMenu = new JMenu(JMeterUtils.getResString("Add"));
        addMenu.add(MenuFactory.makeMenuItem(new ThreadGroupGui().getStaticLabel(),
                                             ThreadGroupGui.class.getName(), "Add"));
        addMenu.add(MenuFactory.makeMenu(MenuFactory.LISTENERS, "Add"));
        addMenu.add(MenuFactory.makeMenu(MenuFactory.CONFIG_ELEMENTS, "Add"));
        addMenu.add(MenuFactory.makeMenu(MenuFactory.ASSERTIONS, "Add"));
        addMenu.add(MenuFactory.makeMenu(MenuFactory.MODIFIERS, "Add"));
        addMenu.add(MenuFactory.makeMenu(MenuFactory.RESPONSE_BASED_MODIFIERS, "Add"));
        addMenu.add(MenuFactory.makeMenu(MenuFactory.TIMERS, "Add"));
        pop.add(addMenu);
        MenuFactory.addFileMenu(pop);
        return pop;
    }

    /****************************************
     * !ToDo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public TestElement createTestElement()
    {
        TestPlan tp = new TestPlan();
        tp.setProperty(TestElement.GUI_CLASS, this.getClass().getName());
        tp.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        tp.setName(getName());
        tp.setFunctionalMode(functionalMode.isSelected());
        tp.setUserDefinedVariables((Arguments)argsPanel.createTestElement());
        return tp;
    }

    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public String getStaticLabel()
    {
        return JMeterUtils.getResString("Test Plan");
    }

    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public Collection getMenuCategories()
    {
        return null;
    }

    /****************************************
     * !ToDo (Method description)
     *
     *@param el  !ToDo (Parameter description)
     ***************************************/
    public void configure(TestElement el)
    {
        super.configure(el);
        functionalMode.setSelected(((AbstractTestElement)el).getPropertyAsBoolean(TestPlan.FUNCTIONAL_MODE));
        if (el.getProperty(TestPlan.USER_DEFINED_VARIABLES) != null)
        {
            argsPanel.configure((Arguments)el.getProperty(TestPlan.USER_DEFINED_VARIABLES));
        }
    }

    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    protected JPanel getVariablePanel()
    {
        argsPanel = new ArgumentsPanel(JMeterUtils.getResString("user_defined_variables"));

        return argsPanel;
    }

    private void init()
    {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        this.add(getNamePanel(), BorderLayout.NORTH);
        JPanel southPanel = new JPanel(new BorderLayout());
        functionalMode = new JCheckBox(JMeterUtils.getResString("functional_mode"));
        southPanel.add(functionalMode, BorderLayout.NORTH);
        JTextArea explain = new JTextArea(JMeterUtils.getResString("functional_mode_explanation"));
        explain.setColumns(30);
        explain.setRows(10);
        explain.setBackground(this.getBackground());
        southPanel.add(explain, BorderLayout.CENTER);
        add(getVariablePanel(), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }
}
