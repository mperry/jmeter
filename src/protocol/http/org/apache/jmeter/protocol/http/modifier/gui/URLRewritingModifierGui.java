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

package org.apache.jmeter.protocol.http.modifier.gui;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.apache.jmeter.config.gui.AbstractResponseBasedModifierGui;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.gui.util.StringFieldDocumentListener;
import org.apache.jmeter.protocol.http.modifier.URLRewritingModifier;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/**
 * @author mstover
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 *
 */
public class URLRewritingModifierGui extends AbstractResponseBasedModifierGui implements ItemListener
{

    private JLabel argumentLabel;
    private JTextField argumentInput;
    private JCheckBox pathExt;


    public URLRewritingModifierGui()
    {
    }


    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#getStaticLabel()
     */
    public String getStaticLabel()
    {
        return "http_url_rewriting_modifier_title";
    }


    protected void initComponents()
    {
        super.initComponents();

        JPanel panel = GUIFactory.createPanel();
        panel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        constraints = new JMeterGridBagConstraints();
        argumentLabel = new JLabel(JMeterUtils.getResString("session_argument_name"));
        argumentLabel.setName("session_argument_name");
        panel.add(argumentLabel, constraints);
        argumentInput = new JTextField(30);
        argumentInput.getDocument().addDocumentListener(new StringFieldDocumentListener(URLRewritingModifier.ARGUMENT_NAME, argumentInput, this));
        constraints = constraints.incrementX();
        panel.add(argumentInput, constraints);
        pathExt = new JCheckBox(JMeterUtils.getResString("path_extension_choice"));
        pathExt.setName("path_extension_choice");
        pathExt.addItemListener(this);
        constraints = constraints.incrementY();
        panel.add(pathExt, constraints);

        add(panel);
    }


    public void itemStateChanged(ItemEvent e)
    {
        getElement().setProperty(URLRewritingModifier.PATH_EXTENSION, new Boolean(e.getStateChange() == ItemEvent.SELECTED));
    }


    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    public TestElement createTestElement()
    {
        URLRewritingModifier modifier = new URLRewritingModifier();
//		this.configureTestElement(modifier);
//		modifier.setArgumentName(argumentName.getText());
//		modifier.setPathExtension(pathExt.isSelected());
        return modifier;
    }

    public void configure(TestElement element)
    {
        super.configure(element);

        argumentInput.setText((String)element.getProperty(URLRewritingModifier.ARGUMENT_NAME));
        pathExt.setSelected(((Boolean)element.getProperty(URLRewritingModifier.PATH_EXTENSION)).booleanValue());
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);

        updateLocalizedStrings(new JComponent[]{argumentLabel, pathExt});
    }
}

