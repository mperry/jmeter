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
package org.apache.jmeter.protocol.ftp.control.gui;


import java.awt.*;

import javax.swing.*;

import org.apache.jmeter.gui.*;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.gui.util.StringFieldDocumentListener;
import org.apache.jmeter.protocol.ftp.sampler.FTPSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.*;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/****************************************
 * Title: Apache JMeter Description: Copyright: Copyright (c) 2000 Company:
 * Apache Foundation
 *
 * @author    Michael Stover
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   1.0
 ***************************************/

public class FtpTestSamplerGui extends AbstractSamplerGui
{

    private JLabel serverLabel;
    private JLabel filenameLabel;
    private JTextField serverInput;
    private JTextField filenameInput;
    private BorderedPanel serverPanel;
    private JLabel usernameLabel;
    private JTextField usernameInput;
    private JLabel passwordLabel;
    private JTextField passwordInput;


    public FtpTestSamplerGui()
    {
    }


    public void configure(TestElementConfiguration config)
    {
        super.configure(config);

        serverInput.setText(config.getProperty(FTPSampler.SERVER));
        usernameInput.setText(config.getProperty(FTPSampler.USERNAME));
        passwordInput.setText(config.getProperty(FTPSampler.PASSWORD));
        filenameInput.setText(config.getProperty(FTPSampler.FILENAME));
    }


    public NamedTestElement createTestElement()
    {
        FTPSampler sampler = new FTPSampler();
//        sampler.addChildElement(ftpDefaultPanel.createTestElement());
//        sampler.addChildElement(loginPanel.createTestElement());
//        this.configureTestElement(sampler);
        return sampler;
    }


    public String getStaticLabel()
    {
        return "ftp_testing_title";
    }


    protected void initComponents()
    {
        super.initComponents();

        serverPanel = GUIFactory.createBorderedPanel("ftp_server_config");
        serverPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();
        serverLabel = new JLabel(JMeterUtils.getResString("server"));
        serverLabel.setName("server");
        serverPanel.add(serverLabel, constraints);
        serverInput = new JTextField(30);
        serverInput.setName(FTPSampler.SERVER);
        serverInput.getDocument().addDocumentListener(new StringFieldDocumentListener(FTPSampler.SERVER, serverInput, this));
        constraints = constraints.incrementX();
        serverPanel.add(serverInput, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        serverPanel.add(filler, constraints);

        usernameLabel = new JLabel(JMeterUtils.getResString("username"));
        usernameLabel.setName("username");
        constraints = constraints.nextRow();
        constraints.fillNone();
        serverPanel.add(usernameLabel, constraints);
        usernameInput = new JTextField(30);
        usernameInput.setName(FTPSampler.USERNAME);
        usernameInput.getDocument().addDocumentListener(new StringFieldDocumentListener(FTPSampler.USERNAME, usernameInput, this));
        constraints = constraints.incrementX();
        serverPanel.add(usernameInput, constraints);

        passwordLabel = new JLabel(JMeterUtils.getResString("password"));
        passwordLabel.setName("password");
        constraints = constraints.nextRow();
        serverPanel.add(passwordLabel, constraints);
        passwordInput = new JTextField(30);
        passwordInput.setName(FTPSampler.PASSWORD);
        passwordInput.getDocument().addDocumentListener(new StringFieldDocumentListener(FTPSampler.PASSWORD, passwordInput, this));
        constraints = constraints.incrementX();
        serverPanel.add(passwordInput, constraints);
        add(serverPanel);


        JPanel panel = GUIFactory.createPanel();
        panel.setLayout(new GridBagLayout());
        filenameLabel = new JLabel(JMeterUtils.getResString("file_to_retrieve"));
        filenameLabel.setName("file_to_retrieve");
        constraints = new JMeterGridBagConstraints();
        panel.add(filenameLabel, constraints);

        filenameInput = new JTextField(50);
        filenameInput.setName(FTPSampler.FILENAME);
        filenameInput.getDocument().addDocumentListener(new StringFieldDocumentListener(FTPSampler.FILENAME, filenameInput, this));
        constraints = constraints.incrementX();
        panel.add(filenameInput, constraints);
        add(panel);
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{serverLabel, usernameLabel, passwordLabel, filenameLabel});
        serverPanel.localeChanged(event);
    }
}
