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
package org.apache.jmeter.threads.gui;


import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import org.apache.jorphan.gui.layout.VerticalLayout;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.*;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.*;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 * @author    Michael Stover
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   1.0
 ***************************************/

public class ThreadGroupGui extends AbstractJMeterGuiComponent implements LocaleChangeListener, ActionListener
{

    private JTextField threadInput;
    private JTextField rampInput;
    private JTextField loopInput;
    private JLabel threadLabel;
    private JLabel loopsLabel;
    private JLabel rampLabel;
    private JRadioButton rbForever;
    private JRadioButton rbCount;
    private JLabel timesLabel;
    private JLabel secondsLabel;


    public ThreadGroupGui()
    {
    }


    public void configure(org.apache.jmeter.testelement.TestElement element)
    {
        threadInput.setText(String.valueOf(element.getProperty(org.apache.jmeter.threads.ThreadGroup.NUMBER_OF_THREADS)));
        rampInput.setText(String.valueOf(element.getProperty(org.apache.jmeter.threads.ThreadGroup.RAMP_UP_PERIOD)));
        loopInput.setText(String.valueOf(element.getProperty(org.apache.jmeter.threads.ThreadGroup.LOOP_COUNT)));
        boolean forever = ((Boolean)element.getProperty(org.apache.jmeter.threads.ThreadGroup.LOOP_FOREVER)).booleanValue();

        if (forever) {
            rbForever.setSelected(true);
            loopInput.setEnabled(false);
        } else {
            rbCount.setSelected(true);
            loopInput.setEnabled(true);
        }
    }


    public JPopupMenu createPopupMenu(TestElement element)
    {
        ActionListener addListener = new org.apache.jmeter.gui.action.AddElement(element);
        JPopupMenu pop = new JPopupMenu();
        pop.add(MenuFactory.makeMenus(new String[]{MenuFactory.CONTROLLERS,
                                                   MenuFactory.LISTENERS, MenuFactory.SAMPLERS, MenuFactory.TIMERS,
                                                   MenuFactory.CONFIG_ELEMENTS}, JMeterUtils.getResString("Add"),
                                      addListener));
//        MenuFactory.addEditMenu(pop, true);
//        MenuFactory.addFileMenu(pop);
        return pop;
    }


    protected void initComponents()
    {
        super.initComponents();

        JPanel threadPropsPanel = GUIFactory.createPanel();
        threadPropsPanel.setLayout(new VerticalLayout(0, VerticalLayout.LEFT));

        JPanel threadPanel = new JPanel();
        threadPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        threadLabel = new JLabel(JMeterUtils.getResString("number_of_threads"));
        threadLabel.setName("number_of_threads");
        threadPanel.add(threadLabel, constraints);
        threadInput = new JTextField(5);
        threadInput.setText("1");
        threadInput.addFocusListener(NumberFieldErrorListener.getNumberFieldErrorListener());
        threadInput.setName("numberOfThreads");
        threadInput.getDocument().addDocumentListener(new IntegerFieldDocumentListener(org.apache.jmeter.threads.ThreadGroup.NUMBER_OF_THREADS, threadInput, this));
        constraints = constraints.incrementX();
        threadPanel.add(threadInput, constraints);
        new FocusRequester(threadInput);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        constraints.gridwidth = 2;
        threadPanel.add(filler, constraints);

        rampLabel = new JLabel(JMeterUtils.getResString("ramp_up"));
        rampLabel.setName("ramp_up");
        constraints = constraints.nextRow();
        constraints.fillNone();
        threadPanel.add(rampLabel, constraints);
        rampInput = new JTextField(5);
        rampInput.setText("1");
        rampInput.setName("rampUpPeriod");
        rampInput.addFocusListener(NumberFieldErrorListener.getNumberFieldErrorListener());
        rampInput.getDocument().addDocumentListener(new IntegerFieldDocumentListener(org.apache.jmeter.threads.ThreadGroup.RAMP_UP_PERIOD, rampInput, this));
        constraints = constraints.incrementX();
        threadPanel.add(rampInput, constraints);
        secondsLabel = new JLabel(JMeterUtils.getResString("seconds"));
        secondsLabel.setName("seconds");
        constraints = constraints.incrementX();
        threadPanel.add(secondsLabel, constraints);


        loopsLabel = new JLabel(JMeterUtils.getResString("iterator_num"));
        loopsLabel.setName("iterator_num");
        constraints = constraints.nextRow();
        threadPanel.add(loopsLabel, constraints);

        ButtonGroup loopGroup = new ButtonGroup();
        rbForever = new JRadioButton(JMeterUtils.getResString("infinite"), true);
        rbForever.addActionListener(this);
        rbForever.setActionCommand("infinite");
        constraints = constraints.incrementX();
        constraints.gridwidth = 3;
        constraints.insets = new Insets(3, 3, 1, 3);
        threadPanel.add(rbForever, constraints);
        rbForever.setName("infinite");

        rbCount = new JRadioButton(JMeterUtils.getResString("iterator_loop"), false);
        rbCount.addActionListener(this);
        rbCount.setActionCommand("iterator_loop");
        constraints = constraints.incrementY();
        constraints.gridwidth = 1;
        constraints.insets = new Insets(1, 3, 3, 3);
        threadPanel.add(rbCount, constraints);
        rbCount.setName("iterator_loop");

        loopGroup.add(rbForever);
        loopGroup.add(rbCount);
        loopInput = new JTextField(5);
        constraints = constraints.incrementX();
        threadPanel.add(loopInput, constraints);
        loopInput.setName("loopCount");
        loopInput.setText("1");
        loopInput.setEnabled(false);
        loopInput.getDocument().addDocumentListener(new IntegerFieldDocumentListener(org.apache.jmeter.threads.ThreadGroup.LOOP_COUNT, loopInput, this));

        timesLabel = new JLabel(JMeterUtils.getResString("iterator_times"));
        timesLabel.setName("iterator_times");
        constraints = constraints.incrementX();
        threadPanel.add(timesLabel, constraints);

        threadPropsPanel.add(threadPanel);
        add(threadPropsPanel);
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{threadLabel, rampLabel, loopsLabel, timesLabel, rbCount, rbForever, secondsLabel});
    }


    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("infinite")) {
            loopInput.setEnabled(false);
            getElement().setProperty(org.apache.jmeter.threads.ThreadGroup.LOOP_FOREVER, Boolean.TRUE);
        } else {
            loopInput.setEnabled(true);
            getElement().setProperty(org.apache.jmeter.threads.ThreadGroup.LOOP_FOREVER, Boolean.FALSE);
        }
    }


    public String getStaticLabel()
    {
        return "thread_group_title";
    }


    public TestElement createTestElement()
    {
        return null;
    }


    public Collection getMenuCategories()
    {
        return null;
    }

}
