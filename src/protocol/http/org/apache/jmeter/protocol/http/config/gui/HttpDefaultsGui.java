/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
package org.apache.jmeter.protocol.http.config.gui;


import java.awt.*;

import javax.swing.*;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.gui.BorderedPanel;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.gui.util.StringFieldDocumentListener;
import org.apache.jmeter.protocol.http.config.HTTPDefaults;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.testelement.NamedTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;


/**
 * @author Administrator
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 */
public class HttpDefaultsGui extends AbstractConfigGui
{

    private BorderedPanel requestPanel;
    private JLabel pathLabel;
    private JTextField pathInput;
    private JMeterGridBagConstraints constraints;
    private HTTPArgumentsPanel argsPanel;
    private JLabel argsLabel;
    private BorderedPanel serverPanel;
    private JLabel domainLabel;
    private JTextField domainInput;
    private JLabel portLabel;
    private JTextField portInput;


    public HttpDefaultsGui()
    {
    }


    public String getStaticLabel()
    {
        return "url_config_title";
    }


    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    public NamedTestElement createTestElement()
    {
        ConfigTestElement config = new ConfigTestElement();
//        super.configureTestElement(config);
//        config.setProperty(HTTPSampler.DOMAIN, domain.getText());
//        config.setProperty(HTTPSampler.PATH, path.getText());
//        config.setProperty(HTTPSampler.ARGUMENTS, argPanel.createTestElement());
//        config.setProperty(HTTPSampler.PORT, port.getText());
        return config;
    }


    public void configure(TestElement element)
    {
        super.configure(element);

        int port = ((Integer)element.getPropertyValue(HTTPSampler.PORT)).intValue();

        if (port == HTTPSampler.USE_DEFAULT_PORT)
        {
            portInput.setText("");
        } else
        {
            portInput.setText(String.valueOf(port));
        }

        domainInput.setText(element.getPropertyAsString(HTTPDefaults.DOMAIN));
        pathInput.setText(element.getPropertyAsString(HTTPDefaults.PATH));
        argsPanel.configure((NamedTestElement)element.getPropertyValue(HTTPDefaults.ARGUMENTS));
    }


    protected void initComponents()
    {
        super.initComponents();
        add(initServerPanel());
        add(initRequestPanel());
    }


    private BorderedPanel initRequestPanel()
    {
        requestPanel = GUIFactory.createBorderedPanel("web_request");
        requestPanel.setLayout(new GridBagLayout());

        JPanel pathPanel = new JPanel();
        pathLabel = new JLabel(JMeterUtils.getResString("path"));
        pathLabel.setName("path");
        pathPanel.add(pathLabel);
        pathInput = new JTextField(50);
        pathInput.setName(HTTPSampler.PATH);
        pathInput.getDocument().addDocumentListener(new StringFieldDocumentListener(HTTPSampler.PATH, pathInput, this));
        pathPanel.add(pathInput);

        // perhaps this is also usefull to set protocol/method defaults for some later revision
//        JPanel protocolPanel = new JPanel();
//        protocolPanel.setLayout(new GridBagLayout());
//        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();
//        constraints.insets = new Insets(1, 3, 1, 3);
//        protocolLabel = new JLabel(JMeterUtils.getResString("protocol"));
//        protocolLabel.setName("protocol");
//        protocolPanel.add(protocolLabel, constraints);
//        rbHTTP = new JRadioButton(JMeterUtils.getResString("url_config_http"));
//        rbHTTP.setName("url_config_http");
//        rbHTTP.addActionListener(this);
//        rbHTTP.setActionCommand("http");
//        constraints = constraints.incrementX();
//        protocolPanel.add(rbHTTP, constraints);
//        rbHTTPS = new JRadioButton(JMeterUtils.getResString("url_config_https"));
//        rbHTTPS.setName("url_config_https");
//        rbHTTPS.addActionListener(this);
//        rbHTTPS.setActionCommand("https");
//        constraints = constraints.incrementY();
//        protocolPanel.add(rbHTTPS, constraints);
//        ButtonGroup protocolButtonGroup = new ButtonGroup();
//        protocolButtonGroup.add(rbHTTP);
//        protocolButtonGroup.add(rbHTTPS);
//
//        JPanel methodPanel = new JPanel();
//        methodPanel.setLayout(new GridBagLayout());
//        constraints = new JMeterGridBagConstraints();
//        constraints.insets = new Insets(1, 3, 1, 3);
//        methodLabel = new JLabel(JMeterUtils.getResString("method"));
//        methodLabel.setName("method");
//        methodPanel.add(methodLabel, constraints);
//        rbPOST = new JRadioButton(JMeterUtils.getResString("url_config_post"));
//        rbPOST.setName("url_config_post");
//        rbPOST.addActionListener(this);
//        rbPOST.setActionCommand("post");
//        constraints = constraints.incrementX();
//        methodPanel.add(rbPOST, constraints);
//        rbGET = new JRadioButton(JMeterUtils.getResString("url_config_get"));
//        rbGET.setName("url_config_get");
//        rbGET.addActionListener(this);
//        rbGET.setActionCommand("get");
//        constraints = constraints.incrementY();
//        methodPanel.add(rbGET, constraints);
//        ButtonGroup methodButtonGroup = new ButtonGroup();
//        methodButtonGroup.add(rbPOST);
//        methodButtonGroup.add(rbGET);



        JPanel argsBorderPanel = new JPanel();
        argsBorderPanel.setLayout(new GridBagLayout());
        argsPanel = new HTTPArgumentsPanel();
        constraints = new JMeterGridBagConstraints();
        argsLabel = new JLabel(JMeterUtils.getResString("paramtable"));
        argsLabel.setName("paramtable");
        argsBorderPanel.add(argsLabel, constraints);
        constraints = constraints.nextRow();
        constraints.fillBoth(1.0, 1.0);
        argsBorderPanel.add(argsPanel, constraints);

        constraints = new JMeterGridBagConstraints();
        requestPanel.add(pathPanel, constraints);
        constraints = constraints.nextRow();
        requestPanel.add(argsBorderPanel, constraints);
        constraints = constraints.nextRow();
        return requestPanel;
    }


    private BorderedPanel initServerPanel()
    {
        serverPanel = GUIFactory.createBorderedPanel("web_server");
        serverPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        domainLabel = new JLabel(JMeterUtils.getResString("server"));
        domainLabel.setName("server");
        serverPanel.add(domainLabel, constraints);
        domainInput = new JTextField(30);
        domainInput.setName(HTTPSampler.DOMAIN);
        domainInput.getDocument().addDocumentListener(new StringFieldDocumentListener(HTTPSampler.DOMAIN, domainInput, this));
        constraints = constraints.incrementX();
        serverPanel.add(domainInput, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        serverPanel.add(filler, constraints);

        portLabel = new JLabel(JMeterUtils.getResString("web_server_port"));
        portLabel.setName("web_server_port");
        constraints = constraints.nextRow();
        constraints.fillNone();
        serverPanel.add(portLabel, constraints);
        portInput = new JTextField(5);
        portInput.setName(HTTPSampler.PORT);
        portInput.getDocument().addDocumentListener(new HttpTestSampleGui.PortFieldDocumentListener(HTTPSampler.PORT, portInput, this));
        constraints = constraints.incrementX();
        serverPanel.add(portInput, constraints);
        return serverPanel;
    }
}
