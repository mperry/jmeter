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
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.gui.*;
import org.apache.jmeter.gui.util.*;
import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.*;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 * @author    Michael Stover
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   1.0
 ***************************************/

public class HttpTestSampleGui extends AbstractSamplerGui implements ActionListener, ItemListener
{

    private JCheckBox getImages;
    private BorderedPanel serverPanel;
    private JLabel domainLabel;
    private JTextField domainInput;
    private JLabel portLabel;
    private JTextField portInput;
    private BorderedPanel requestPanel;
    private JLabel pathLabel;
    private JTextField pathInput;
    private JLabel protocolLabel;
    private JRadioButton rbHTTP;
    private JRadioButton rbHTTPS;
    private JRadioButton rbPOST;
    private JRadioButton rbGET;
    private JLabel methodLabel;
    private JLabel optionsLabel;
    private JCheckBox followRedirects;
    private JCheckBox keepAlive;
    private HTTPArgumentsPanel argsPanel;
    private JCheckBox uploadFile;
    private JLabel filenameLabel;
    private JTextField filenameField;
    private JButton browseFileButton;
    private JLabel paramNameLabel;
    private JTextField paramNameField;
    private JLabel mimetypeLabel;
    private JTextField mimetypeField;
    private BorderedPanel optionalTasksPanel;
    private JLabel argsLabel;


    public HttpTestSampleGui()
    {
    }


    public void configure(TestElementConfiguration config)
    {
        super.configure(config);

        int port = new Integer(config.getProperty(HTTPSampler.PORT)).intValue();

        if (port == HTTPSampler.USE_DEFAULT_PORT)
        {
            portInput.setText("");
        } else
        {
            portInput.setText(String.valueOf(port));
        }

        domainInput.setText(config.getProperty(HTTPSampler.DOMAIN));
        pathInput.setText(config.getProperty(HTTPSampler.PATH));
        followRedirects.setSelected(new Boolean(config.getProperty(HTTPSampler.FOLLOW_REDIRECTS)).booleanValue());
        keepAlive.setSelected(new Boolean(config.getProperty(HTTPSampler.KEEP_ALIVE)).booleanValue());

        String method = config.getProperty(HTTPSampler.METHOD);

        if (HTTPSampler.METHOD_GET.equals(method))
        {
            rbGET.setSelected(true);
        } else
        {
            rbPOST.setSelected(true);
        }

        String protocol = config.getProperty(HTTPSampler.PROTOCOL);

        if (HTTPSampler.PROTOCOL_HTTP.equals(protocol))
        {
            rbHTTP.setSelected(true);
        } else
        {
            rbHTTPS.setSelected(true);
        }

        boolean upload = new Boolean(config.getProperty(HTTPSampler.UPLOAD_FILE)).booleanValue();

        uploadFile.setSelected(upload);
        setUploadFile(upload);
        paramNameField.setText(config.getProperty(HTTPSampler.FILE_FIELD));
        filenameField.setText(config.getProperty(HTTPSampler.FILE_NAME));
        mimetypeField.setText(config.getProperty(HTTPSampler.FILE_MIMETYPE));

        getImages.setSelected(new Boolean(config.getProperty(HTTPSampler.LOAD_IMAGES)).booleanValue());
        argsPanel.setElement(config);
    }


    public NamedTestElement createTestElement()
    {
        HTTPSampler sampler = null;
//        NamedTestElement el = urlConfigGui.createTestElement();
//        if (getImages.isSelected()) {
//            sampler = new HTTPSamplerFull();
//        } else {
//            sampler = new HTTPSampler();
//        }
//        sampler.addChildElement(el);
//        this.configureTestElement(sampler);
        return sampler;
    }


    public String getStaticLabel()
    {
        return "web_testing_title";
    }


    protected void initComponents()
    {
        super.initComponents();
        add(initServerPanel());
        add(initRequestPanel());
        add(initOptionalTasksPanel());
    }


    private JPanel initOptionalTasksPanel()
    {
        optionalTasksPanel = GUIFactory.createBorderedPanel("optional_tasks");
        optionalTasksPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();
        getImages = new JCheckBox(JMeterUtils.getResString("web_testing_retrieve_images"));
        getImages.addItemListener(this);
        optionalTasksPanel.add(getImages, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        optionalTasksPanel.add(filler, constraints);

        return optionalTasksPanel;
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
        portInput.getDocument().addDocumentListener(new PortFieldDocumentListener(HTTPSampler.PORT, portInput, this));
        constraints = constraints.incrementX();
        serverPanel.add(portInput, constraints);
        return serverPanel;
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

        JPanel protocolPanel = new JPanel();
        protocolPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();
        constraints.insets = new Insets(1, 3, 1, 3);
        protocolLabel = new JLabel(JMeterUtils.getResString("protocol"));
        protocolLabel.setName("protocol");
        protocolPanel.add(protocolLabel, constraints);
        rbHTTP = new JRadioButton(JMeterUtils.getResString("url_config_http"));
        rbHTTP.setName("url_config_http");
        rbHTTP.addActionListener(this);
        rbHTTP.setActionCommand("http");
        constraints = constraints.incrementX();
        protocolPanel.add(rbHTTP, constraints);
        rbHTTPS = new JRadioButton(JMeterUtils.getResString("url_config_https"));
        rbHTTPS.setName("url_config_https");
        rbHTTPS.addActionListener(this);
        rbHTTPS.setActionCommand("https");
        constraints = constraints.incrementY();
        protocolPanel.add(rbHTTPS, constraints);
        ButtonGroup protocolButtonGroup = new ButtonGroup();
        protocolButtonGroup.add(rbHTTP);
        protocolButtonGroup.add(rbHTTPS);

        JPanel methodPanel = new JPanel();
        methodPanel.setLayout(new GridBagLayout());
        constraints = new JMeterGridBagConstraints();
        constraints.insets = new Insets(1, 3, 1, 3);
        methodLabel = new JLabel(JMeterUtils.getResString("method"));
        methodLabel.setName("method");
        methodPanel.add(methodLabel, constraints);
        rbPOST = new JRadioButton(JMeterUtils.getResString("url_config_post"));
        rbPOST.setName("url_config_post");
        rbPOST.addActionListener(this);
        rbPOST.setActionCommand("post");
        constraints = constraints.incrementX();
        methodPanel.add(rbPOST, constraints);
        rbGET = new JRadioButton(JMeterUtils.getResString("url_config_get"));
        rbGET.setName("url_config_get");
        rbGET.addActionListener(this);
        rbGET.setActionCommand("get");
        constraints = constraints.incrementY();
        methodPanel.add(rbGET, constraints);
        ButtonGroup methodButtonGroup = new ButtonGroup();
        methodButtonGroup.add(rbPOST);
        methodButtonGroup.add(rbGET);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridBagLayout());
        constraints = new JMeterGridBagConstraints();
        constraints.insets = new Insets(1, 3, 1, 3);
        optionsLabel = new JLabel(JMeterUtils.getResString("web_options"));
        optionsLabel.setName("web_options");
        optionsPanel.add(optionsLabel, constraints);
        followRedirects = new JCheckBox(JMeterUtils.getResString("follow_redirects"));
        followRedirects.setName("follow_redirects");
        followRedirects.addItemListener(this);
        constraints = constraints.incrementX();
        optionsPanel.add(followRedirects, constraints);
        keepAlive = new JCheckBox(JMeterUtils.getResString("use_keepalive"));
        keepAlive.setName("use_keepalive");
        keepAlive.addItemListener(this);
        constraints = constraints.incrementY();
        optionsPanel.add(keepAlive, constraints);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        constraints = new JMeterGridBagConstraints();
        constraints.insets = new Insets(3, 0, 3, 30);
        buttonPanel.add(protocolPanel, constraints);
        constraints = constraints.incrementX();
        buttonPanel.add(methodPanel, constraints);
        constraints = constraints.incrementX();
        buttonPanel.add(optionsPanel, constraints);

        JPanel pathAndButtonPanel = new JPanel();
        pathAndButtonPanel.setLayout(new GridBagLayout());
        constraints = new JMeterGridBagConstraints();
        constraints.insets = new Insets(0, 0, 0, 0);
        pathAndButtonPanel.add(pathPanel, constraints);
        constraints = constraints.incrementY();
        pathAndButtonPanel.add(buttonPanel, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        pathAndButtonPanel.add(filler, constraints);

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new GridBagLayout());
        constraints = new JMeterGridBagConstraints();
        constraints.gridwidth = 2;
        uploadFile = new JCheckBox(JMeterUtils.getResString("send_file"));
        uploadFile.setName("send_file");
        uploadFile.addItemListener(this);
        filePanel.add(uploadFile, constraints);
        paramNameLabel = new JLabel(JMeterUtils.getResString("send_file_param_name_label"));
        paramNameLabel.setName("send_file_param_name_label");
        constraints = constraints.nextRow();
        constraints.gridwidth = 1;
        filePanel.add(paramNameLabel, constraints);
        paramNameField = new JTextField(20);
        paramNameField.getDocument().addDocumentListener(new StringFieldDocumentListener(HTTPSampler.FILE_FIELD, paramNameField, this));
        constraints = constraints.incrementX();
        filePanel.add(paramNameField, constraints);
        filenameLabel = new JLabel(JMeterUtils.getResString("send_file_filename_label"));
        filenameLabel.setName("send_file_filename_label");
        constraints = constraints.nextRow();
        filePanel.add(filenameLabel, constraints);
        filenameField = new JTextField(40);
        filenameField.getDocument().addDocumentListener(new StringFieldDocumentListener(HTTPSampler.FILE_NAME, filenameField, this));
        constraints = constraints.incrementX();
        filePanel.add(filenameField, constraints);
        browseFileButton = new JButton(JMeterUtils.getResString("send_file_browse"));
        browseFileButton.setName("send_file_browse");
        browseFileButton.addActionListener(new BrowseButtonActionListener());
        constraints = constraints.incrementX();
        filePanel.add(browseFileButton, constraints);
        filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        filePanel.add(filler, constraints);
        mimetypeLabel = new JLabel(JMeterUtils.getResString("send_file_mime_label"));
        mimetypeLabel.setName("send_file_mime_label");
        constraints = constraints.nextRow();
        constraints.fillNone();
        filePanel.add(mimetypeLabel, constraints);
        mimetypeField = new JTextField(30);
        mimetypeField.getDocument().addDocumentListener(new StringFieldDocumentListener(HTTPSampler.FILE_MIMETYPE, mimetypeField, this));
        constraints = constraints.incrementX();
        filePanel.add(mimetypeField, constraints);

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
        requestPanel.add(pathAndButtonPanel, constraints);
        constraints = constraints.nextRow();
        requestPanel.add(argsBorderPanel, constraints);
        constraints = constraints.nextRow();
        requestPanel.add(filePanel, constraints);
        return requestPanel;
    }


    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("http"))
        {
            getElement().setProperty(HTTPSampler.PROTOCOL, HTTPSampler.PROTOCOL_HTTP);
        } else if (e.getActionCommand().equals("https"))
        {
            getElement().setProperty(HTTPSampler.PROTOCOL, HTTPSampler.PROTOCOL_HTTPS);
        } else if (e.getActionCommand().equals("get"))
        {
            getElement().setProperty(HTTPSampler.METHOD, HTTPSampler.METHOD_GET);
        } else if (e.getActionCommand().equals("post"))
        {
            getElement().setProperty(HTTPSampler.METHOD, HTTPSampler.METHOD_POST);
        }
    }


    public void itemStateChanged(ItemEvent e)
    {
        Object source = e.getSource();

        if (source == followRedirects)
        {
            getElement().setProperty(HTTPSampler.FOLLOW_REDIRECTS, String.valueOf(e.getStateChange() == ItemEvent.SELECTED));
        } else if (source == keepAlive)
        {
            getElement().setProperty(HTTPSampler.KEEP_ALIVE, String.valueOf(e.getStateChange() == ItemEvent.SELECTED));
        } else if (source == uploadFile)
        {
            getElement().setProperty(HTTPSampler.UPLOAD_FILE, String.valueOf(e.getStateChange() == ItemEvent.SELECTED));
            setUploadFile(e.getStateChange() == ItemEvent.SELECTED);
        } else if (source == getImages)
        {
            getElement().setProperty(HTTPSampler.LOAD_IMAGES, String.valueOf(e.getStateChange() == ItemEvent.SELECTED));
        }
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{rbGET, rbPOST, rbHTTP, rbHTTPS, domainLabel, filenameLabel, followRedirects, keepAlive, methodLabel, protocolLabel,
                                                mimetypeLabel, paramNameLabel, pathLabel, portLabel, optionsLabel, uploadFile, getImages, argsLabel});
        requestPanel.localeChanged(event);
        serverPanel.localeChanged(event);
//        filePanel.localeChanged(event);
        optionalTasksPanel.localeChanged(event);
    }


    private void setUploadFile(boolean flag)
    {
        paramNameLabel.setEnabled(flag);
        paramNameField.setEnabled(flag);
        filenameLabel.setEnabled(flag);
        filenameField.setEnabled(flag);
        browseFileButton.setEnabled(flag);
        mimetypeLabel.setEnabled(flag);
        mimetypeField.setEnabled(flag);
    }


    public static class PortFieldDocumentListener implements DocumentListener
    {

        private JMeterGUIComponent gui;
        private String property;
        private JTextField field;


        public PortFieldDocumentListener(String property, JTextField field, JMeterGUIComponent gui)
        {
            this.property = property;
            this.field = field;
            this.gui = gui;
        }


        public void insertUpdate(DocumentEvent e)
        {
            setValue();
        }


        public void removeUpdate(DocumentEvent e)
        {
            setValue();
        }


        public void changedUpdate(DocumentEvent e)
        {
            //
        }


        private void setValue()
        {
            try
            {
                if (field.getText().trim().length() == 0)
                {
                    gui.getElement().setProperty(property, String.valueOf(HTTPSampler.USE_DEFAULT_PORT));
                } else
                {
                    gui.getElement().setProperty(property, field.getText());
                }
            } catch (NumberFormatException e)
            {
                //
            }
        }
    }


    private class BrowseButtonActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            JFileChooser chooser = FileDialoger.promptToOpenFile();
            if (chooser == null)
            {
                return;
            }
            File file = chooser.getSelectedFile();
            if (file != null)
            {
                filenameField.setText(file.getPath());
            }
        }
    }
}
