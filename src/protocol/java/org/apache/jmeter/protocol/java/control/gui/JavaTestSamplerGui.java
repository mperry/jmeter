/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
package org.apache.jmeter.protocol.java.control.gui;


import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.gui.BorderedPanel;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.protocol.java.config.JavaConfig;
import org.apache.jmeter.protocol.java.config.gui.JavaConfigGui;
import org.apache.jmeter.protocol.java.sampler.JavaSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.*;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;
import org.apache.jmeter.plugin.ElementClassRegistry;


/**
 * The <code>JavaTestSamplerGui</code> class provides the user interface
 * for the JavaTestSampler.
 *
 * @author Brad Kiewel
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Revision$
 */

public class JavaTestSamplerGui extends AbstractSamplerGui implements ItemListener
{

    private JavaConfigGui javaPanel = null;
    private BorderedPanel requestPanel;
    private JLabel classnameLabel;
    private JComboBox classnameCombo;
    private ArgumentsPanel argsPanel;
    private JLabel argsLabel;


    public String getStaticLabel()
    {
        return "protocol_java_test_title";
    }


    protected void initComponents()
    {
        super.initComponents();

        requestPanel = GUIFactory.createBorderedPanel("protocol_java_border");
        requestPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        classnameLabel = new JLabel(JMeterUtils.getResString("protocol_java_classname"));
        classnameLabel.setName("protocol_java_classname");
        requestPanel.add(classnameLabel, constraints);
        classnameCombo = new JComboBox(new String[0]);
        classnameCombo.addItemListener(this);
        constraints = constraints.incrementX();
        requestPanel.add(classnameCombo, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        requestPanel.add(filler, constraints);
        argsLabel = new JLabel(JMeterUtils.getResString("paramtable"));
        argsLabel.setName("paramtable");
        constraints = constraints.nextRow();
        constraints.fillNone();
        Insets insets = constraints.insets;
        constraints.insets = new Insets(insets.top + 10, insets.left, insets.bottom, insets.right);
        requestPanel.add(argsLabel, constraints);
        argsPanel = new ArgumentsPanel();
        constraints = constraints.nextRow();
        constraints.insets = insets;
        constraints.gridwidth = 2;
        requestPanel.add(argsPanel, constraints);

        add(requestPanel);
    }


    public NamedTestElement createTestElement()
    {
        JavaConfig config = (JavaConfig)javaPanel.createTestElement();
        JavaSampler sampler = new JavaSampler();
        this.configureTestElement(sampler);
        sampler.addChildElement(config);
        return sampler;
    }

    public void configure(TestElementConfiguration config)
    {
        super.configure(config);

        argsPanel.setElement(config);
        fillComboBox(config);
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{classnameLabel, argsLabel});
        requestPanel.localeChanged(event);
        fillComboBox(getElement());
    }


    public void itemStateChanged(ItemEvent e)
    {
        String selected = (String)classnameCombo.getSelectedObjects()[0];
        if (JMeterUtils.getResString("select_class").equals(selected))
        {
            getElement().setProperty(JavaSampler.CLASSNAME, null);
        } else
        {
            getElement().setProperty(JavaSampler.CLASSNAME, selected);
        }
    }


    private void fillComboBox(TestElementConfiguration element)
    {
        if (element == null)
        {
            // todo: remove this check as soon as gui refactoring is done
            return;
        }
        String className = element.getProperty(JavaSampler.CLASSNAME);
        String selectOne = JMeterUtils.getResString("select_class");

        if (className == null)
        {
            className = selectOne;
        }
        String[] classNames = ElementClassRegistry.getInstance().getJavaSamplerClientClassNames();
        String[] comboEntries = new String[classNames.length + 1];
        comboEntries[0] = selectOne;
        System.arraycopy(classNames, 0, comboEntries, 1, classNames.length);
        classnameCombo.setModel(new DefaultComboBoxModel(comboEntries));
        classnameCombo.setSelectedItem(className);
    }
}

