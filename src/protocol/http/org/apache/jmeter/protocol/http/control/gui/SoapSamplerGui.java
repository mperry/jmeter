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
package org.apache.jmeter.protocol.http.control.gui;


import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.gui.util.StringFieldDocumentListener;
import org.apache.jmeter.protocol.http.sampler.SoapSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.*;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/**
 * @author mstover
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 *
 */
public class SoapSamplerGui extends AbstractSamplerGui
{

    private JLabel urlLabel;
    private JTextField urlField;
    private JLabel dataLabel;
    private JTextArea dataField;
    private JScrollPane dataScroller;


    public SoapSamplerGui()
    {
    }


    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#getStaticLabel()
     */
    public String getStaticLabel()
    {
        return "soap_sampler_title";
    }


    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    public NamedTestElement createTestElement()
    {
        SoapSampler sampler = new SoapSampler();
        this.configureTestElement(sampler);
        try {
            URL url = new URL(urlField.getText());
            sampler.setDomain(url.getHost());
            sampler.setPort(url.getPort());
            sampler.setProtocol(url.getProtocol());
            sampler.setMethod(SoapSampler.METHOD_POST);
            sampler.setPath(url.getPath());
            sampler.setXmlData(dataField.getText());
        } catch (MalformedURLException e) {
        }
        return sampler;
    }


    protected void initComponents()
    {
        super.initComponents();

        JPanel requestPanel = GUIFactory.createPanel();
        requestPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        urlLabel = new JLabel(JMeterUtils.getResString("url"));
        urlLabel.setName("url");
        requestPanel.add(urlLabel, constraints);
        urlField = new JTextField(50);
        urlField.getDocument().addDocumentListener(new StringFieldDocumentListener(SoapSampler.URL, urlField, this));
        constraints = constraints.incrementX();
        requestPanel.add(urlField, constraints);
        dataLabel = new JLabel(JMeterUtils.getResString("soap_data_title"));
        dataLabel.setName("soap_data_title");
        constraints = constraints.nextRow();
        constraints.gridwidth = 2;
        Insets insets = constraints.insets;
        constraints.insets = new Insets(insets.top + 10, insets.left, insets.bottom, insets.right);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        requestPanel.add(dataLabel, constraints);
        dataField = new JTextArea(30, 60);
        dataField.getDocument().addDocumentListener(new StringFieldDocumentListener(SoapSampler.XML_DATA, dataField, this));
        dataScroller = new JScrollPane(dataField);
        dataScroller.setBorder(BorderFactory.createEtchedBorder());
        constraints = constraints.nextRow();
        constraints.insets = insets;
        constraints.gridwidth = 2;
        requestPanel.add(dataScroller, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        constraints.gridwidth = 1;
        requestPanel.add(filler, constraints);
        this.add(requestPanel);
    }


    public void configure(TestElementConfiguration config)
    {
        super.configure(config);
        urlField.setText(config.getProperty(SoapSampler.URL));
        dataField.setText(config.getProperty(SoapSampler.XML_DATA));
        dataField.setCaretPosition(0);
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{urlLabel});
    }
}
