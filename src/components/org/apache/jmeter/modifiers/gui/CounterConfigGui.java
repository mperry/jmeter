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

package org.apache.jmeter.modifiers.gui;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.*;
import org.apache.jmeter.modifiers.CounterConfig;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/**
 * Gui to configure a counter.
 *
 * @author Administrator
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 *
 */
public class CounterConfigGui extends AbstractConfigGui implements ItemListener
{

    private JCheckBox perUser;
    private JLabel startLabel;
    private JTextField startInput;
    private JLabel maxLabel;
    private JTextField maxInput;
    private JLabel incrementLabel;
    private JTextField incrementInput;
    private JLabel nameLabel;
    private JTextField nameInput;


    public CounterConfigGui()
    {
    }


    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#getStaticLabel()
     */
    public String getStaticLabel()
    {
        return "counter_config_title";
    }


    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    public TestElement createTestElement()
    {
        CounterConfig config = new CounterConfig();
//        config.setStart(startField.getText());
//        if (endField.getText().length() > 0) {
//            config.setEnd(endField.getText());
//        }
//        config.setIncrement(incrField.getText());
//        config.setVarName(varNameField.getText());
//        config.setPerUser(perUser.isSelected());
//        super.configureTestElement(config);
        return config;
    }


    public void configure(TestElement element)
    {
        super.configure(element);
        startInput.setText(element.getProperty(CounterConfig.START).toString());
        maxInput.setText(element.getProperty(CounterConfig.END).toString());
        incrementInput.setText(element.getProperty(CounterConfig.INCREMENT).toString());
        nameInput.setText(element.getProperty(CounterConfig.VAR_NAME).toString());
        perUser.setSelected(((Boolean)element.getProperty(CounterConfig.PER_USER)).booleanValue());
    }


    protected void initComponents()
    {
        super.initComponents();

        add(initCounterPanel());
    }


    private JPanel initCounterPanel()
    {
        JPanel counterPanel = GUIFactory.createPanel();
        counterPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        nameLabel = new JLabel(JMeterUtils.getResString("var_name"));
        nameLabel.setName("var_name");
        counterPanel.add(nameLabel, constraints);
        nameInput = new JTextField(30);
        nameInput.getDocument().addDocumentListener(new StringFieldDocumentListener(CounterConfig.VAR_NAME, nameInput, this));
        constraints = constraints.incrementX();
        counterPanel.add(nameInput, constraints);

        startLabel = new JLabel(JMeterUtils.getResString("counter_start"));
        startLabel.setName("counter_start");
        constraints = constraints.nextRow();
        counterPanel.add(startLabel, constraints);
        startInput = new JTextField(8);
        startInput.getDocument().addDocumentListener(new IntegerFieldDocumentListener(CounterConfig.START, startInput, this));
        constraints = constraints.incrementX();
        counterPanel.add(startInput, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        counterPanel.add(filler, constraints);

        maxLabel = new JLabel(JMeterUtils.getResString("counter_max"));
        maxLabel.setName("counter_max");
        constraints = constraints.nextRow();
        constraints.fillNone();
        counterPanel.add(maxLabel, constraints);
        maxInput = new JTextField(8);
        maxInput.getDocument().addDocumentListener(new IntegerFieldDocumentListener(CounterConfig.END, maxInput, this));
        constraints = constraints.incrementX();
        counterPanel.add(maxInput, constraints);

        incrementLabel = new JLabel(JMeterUtils.getResString("increment"));
        incrementLabel.setName("increment");
        constraints = constraints.nextRow();
        constraints.fillNone();
        counterPanel.add(incrementLabel, constraints);
        incrementInput = new JTextField(8);
        incrementInput.getDocument().addDocumentListener(new IntegerFieldDocumentListener(CounterConfig.INCREMENT, incrementInput, this));
        constraints = constraints.incrementX();
        counterPanel.add(incrementInput, constraints);

        perUser = new JCheckBox(JMeterUtils.getResString("counter_per_user"));
        perUser.setName("counter_per_user");
        perUser.addItemListener(this);
        constraints = constraints.nextRow();
        constraints.gridwidth = 2;
        counterPanel.add(perUser, constraints);

        return counterPanel;
    }


    public void itemStateChanged(ItemEvent e)
    {
        getElement().setProperty(CounterConfig.PER_USER, new Boolean(e.getStateChange() == ItemEvent.SELECTED));
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{nameLabel, startLabel, incrementLabel, maxLabel, perUser});
    }
}
