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


import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.action.AddElement;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.gui.util.MenuFactory;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.*;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 * @author    Michael Stover
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   1.0
 ***************************************/

public class TestPlanGui extends AbstractJMeterGuiComponent implements LocaleChangeListener, ItemListener
{

    JCheckBox functionalMode;
    ArgumentsPanel argsPanel;
    private JPopupMenu popup;
    private JLabel argsLabel;
    private JTextArea explain;
    private TestPlan testPlan;


    public TestPlanGui()
    {
    }


    public JPopupMenu createPopupMenu(TestElement testElement)
    {
//        if (popup == null) {
        ActionListener addListener = new AddElement(testElement);
            JPopupMenu popup = new JPopupMenu();
            JMenu addMenu = new JMenu(JMeterUtils.getResString("add"));
            addMenu.add(MenuFactory.makeMenuItem(JMeterUtils.getResString("threadgroup"),
                                                 org.apache.jmeter.threads.ThreadGroup.class.getName(), addListener));
            addMenu.add(MenuFactory.makeMenu(MenuFactory.LISTENERS, "Add"));
            addMenu.add(MenuFactory.makeMenu(MenuFactory.CONFIG_ELEMENTS, addListener));
            addMenu.add(MenuFactory.makeMenu(MenuFactory.ASSERTIONS, addListener));
            addMenu.add(MenuFactory.makeMenu(MenuFactory.MODIFIERS, "Add"));
            addMenu.add(MenuFactory.makeMenu(MenuFactory.RESPONSE_BASED_MODIFIERS, addListener));
            addMenu.add(MenuFactory.makeMenu(MenuFactory.TIMERS, addListener));
            popup.add(addMenu);
//		MenuFactory.addFileMenu(popup);
//        }
        return popup;
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


    public String getStaticLabel()
    {
        return "test_plan";
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


    public void configure(TestElement element)
    {
        super.configure(element);
        testPlan = (TestPlan)element;
        functionalMode.setSelected(testPlan.getFunctionalMode());
        argsPanel.setElement(testPlan.getUserDefinedVariables());
    }


    protected JPanel initVariablePanel()
    {
        JPanel argsBorderPanel = GUIFactory.createPanel();
        argsBorderPanel.setLayout(new GridBagLayout());
        argsPanel = new ArgumentsPanel();
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        argsLabel = new JLabel(JMeterUtils.getResString("user_defined_variables"));
        argsLabel.setName("user_defined_variables");
        argsBorderPanel.add(argsLabel, constraints);
        constraints = constraints.nextRow();
        constraints.fillBoth(1.0, 1.0);
        argsBorderPanel.add(argsPanel, constraints);
        return argsBorderPanel;
    }


    protected void initComponents()
    {
        super.initComponents();

//        JPanel panel = new JPanel();
//        panel.setLayout(new BorderLayout(10, 10));
//        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        JPanel functionalModePanel = GUIFactory.createPanel();
        functionalModePanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        functionalMode = new JCheckBox(JMeterUtils.getResString("functional_mode"));
        functionalMode.setName("functional_mode");
        functionalMode.addItemListener(this);
        functionalModePanel.add(functionalMode, constraints);
        explain = new JTextArea(JMeterUtils.getResString("functional_mode_explanation"));
        explain.setName("functional_mode_explanation");
        explain.setColumns(50);
        explain.setRows(10);
        explain.setBackground(this.getBackground());
        constraints = constraints.nextRow();
        functionalModePanel.add(explain, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        functionalModePanel.add(filler, constraints);

        add(initVariablePanel());
        add(functionalModePanel);
//        add(panel);
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{functionalMode, argsLabel, explain});
    }


    public void itemStateChanged(ItemEvent e)
    {
        testPlan.setFunctionalMode(e.getStateChange() == ItemEvent.SELECTED);
    }
}
