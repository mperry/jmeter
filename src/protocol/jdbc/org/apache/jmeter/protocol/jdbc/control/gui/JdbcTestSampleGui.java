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
package org.apache.jmeter.protocol.jdbc.control.gui;


import java.awt.*;

import javax.swing.*;

import org.apache.jmeter.gui.BorderedPanel;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.*;
import org.apache.jmeter.protocol.jdbc.sampler.JDBCSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.NamedTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/****************************************
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 *
 *@author    Michael Stover
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 *@created   $Date$
 *@version   1.0
 ***************************************/

public class JdbcTestSampleGui extends AbstractSamplerGui
{

    private BorderedPanel connectionPanel;
    private JLabel driverLabel;
    private JTextField driverInput;
    private JLabel urlLabel;
    private JTextField urlInput;
    private JLabel usernameLabel;
    private JTextField usernameInput;
    private JLabel passwordLabel;
    private JTextField passwordInput;
    private BorderedPanel poolPanel;
    private JLabel connectionsLabel;
    private JTextField connectionsInput;
    private JLabel maxUsageLabel;
    private JTextField maxUsageInput;
    private JLabel sqlLabel;
    private JTextArea sqlInput;
    private JScrollPane sqlInputScroller;


    /****************************************
     * !ToDo (Constructor description)
     ***************************************/
    public JdbcTestSampleGui()
    {
    }


    public void configure(TestElement element)
    {
        super.configure(element);

        driverInput.setText((String)element.getPropertyValue(JDBCSampler.DRIVER));
        urlInput.setText((String)element.getPropertyValue(JDBCSampler.URL));
        usernameInput.setText((String)element.getPropertyValue(JDBCSampler.USERNAME));
        passwordInput.setText((String)element.getPropertyValue(JDBCSampler.PASSWORD));
        sqlInput.setText((String)element.getPropertyValue(JDBCSampler.QUERY));
        connectionsInput.setText((element.getPropertyValue(JDBCSampler.CONNECTIONS)).toString());
        maxUsageInput.setText((element.getPropertyValue(JDBCSampler.MAXUSE)).toString());
    }


    public String getStaticLabel()
    {
        return "database_testing_title";
    }


    public NamedTestElement createTestElement()
    {
        JDBCSampler sampler = new JDBCSampler();
//        sampler.addChildElement(dbGui.createTestElement());
//        sampler.addChildElement(poolGui.createTestElement());
//        sampler.addChildElement(sqlGui.createTestElement());
        configureTestElement(sampler);
        return sampler;
    }


    protected void initComponents()
    {
        super.initComponents();

        add(initConnectionPanel());
        add(initConnectionPoolPanel());
        add(initSqlPanel());
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{driverLabel, urlLabel, usernameLabel, passwordLabel, connectionsLabel, maxUsageLabel, sqlLabel});
        connectionPanel.localeChanged(event);
        poolPanel.localeChanged(event);
    }


    private BorderedPanel initConnectionPanel()
    {
        connectionPanel = GUIFactory.createBorderedPanel("database_url_jdbc_props");
        connectionPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        driverLabel = new JLabel(JMeterUtils.getResString("database_driver_class"));
        driverLabel.setName("database_driver_class");
        connectionPanel.add(driverLabel, constraints);
        driverInput = new JTextField(30);
        driverInput.getDocument().addDocumentListener(new StringFieldDocumentListener(JDBCSampler.DRIVER, driverInput, this));
        constraints = constraints.incrementX();
        connectionPanel.add(driverInput, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        connectionPanel.add(filler, constraints);

        urlLabel = new JLabel(JMeterUtils.getResString("database_url"));
        urlLabel.setName("database_url");
        constraints = constraints.nextRow();
        constraints.fillNone();
        connectionPanel.add(urlLabel, constraints);
        urlInput = new JTextField(30);
        urlInput.getDocument().addDocumentListener(new StringFieldDocumentListener(JDBCSampler.URL, urlInput, this));
        constraints = constraints.incrementX();
        connectionPanel.add(urlInput, constraints);


        usernameLabel = new JLabel(JMeterUtils.getResString("username"));
        usernameLabel.setName("username");
        constraints = constraints.nextRow();
        constraints.fillNone();
        connectionPanel.add(usernameLabel, constraints);
        usernameInput = new JTextField(20);
        usernameInput.getDocument().addDocumentListener(new StringFieldDocumentListener(JDBCSampler.USERNAME, usernameInput, this));
        constraints = constraints.incrementX();
        connectionPanel.add(usernameInput, constraints);

        passwordLabel = new JLabel(JMeterUtils.getResString("password"));
        passwordLabel.setName("password");
        constraints = constraints.nextRow();
        connectionPanel.add(passwordLabel, constraints);
        passwordInput = new JTextField(20);
        passwordInput.getDocument().addDocumentListener(new StringFieldDocumentListener(JDBCSampler.PASSWORD, passwordInput, this));
        constraints = constraints.incrementX();
        connectionPanel.add(passwordInput, constraints);

        return connectionPanel;
    }


    private BorderedPanel initConnectionPoolPanel()
    {
        poolPanel = GUIFactory.createBorderedPanel("database_conn_pool_props");
        poolPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        connectionsLabel = new JLabel(JMeterUtils.getResString("database_conn_pool_size"));
        connectionsLabel.setName("database_conn_pool_size");
        poolPanel.add(connectionsLabel, constraints);
        connectionsInput = new JTextField(5);
        connectionsInput.setHorizontalAlignment(JTextField.RIGHT);
        connectionsInput.getDocument().addDocumentListener(new IntegerFieldDocumentListener(JDBCSampler.CONNECTIONS, connectionsInput, this));
        constraints = constraints.incrementX();
        poolPanel.add(connectionsInput, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        poolPanel.add(filler, constraints);

        maxUsageLabel = new JLabel(JMeterUtils.getResString("database_conn_pool_max_usage"));
        maxUsageLabel.setName("database_conn_pool_max_usage");
        constraints = constraints.nextRow();
        constraints.fillNone();
        poolPanel.add(maxUsageLabel, constraints);
        maxUsageInput = new JTextField(5);
        maxUsageInput.setHorizontalAlignment(JTextField.RIGHT);
        maxUsageInput.getDocument().addDocumentListener(new IntegerFieldDocumentListener(JDBCSampler.MAXUSE, maxUsageInput, this));
        constraints = constraints.incrementX();
        poolPanel.add(maxUsageInput, constraints);

        return poolPanel;
    }


    private JPanel initSqlPanel()
    {
        JPanel panel = GUIFactory.createPanel();
        panel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        sqlLabel = new JLabel(JMeterUtils.getResString("database_sql_query_string"));
        sqlLabel.setName("database_sql_query_string");
        panel.add(sqlLabel, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        panel.add(filler, constraints);
        sqlInput = new JTextArea(10, 60);
        sqlInput.getDocument().addDocumentListener(new StringFieldDocumentListener(JDBCSampler.QUERY, sqlInput, this));
        sqlInputScroller = new JScrollPane(sqlInput);
        sqlInputScroller.setBorder(BorderFactory.createEtchedBorder());
        constraints = constraints.nextRow();
        panel.add(sqlInputScroller, constraints);

        return panel;
    }
}
