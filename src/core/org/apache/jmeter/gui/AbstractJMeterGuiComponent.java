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
package org.apache.jmeter.gui;


import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import org.apache.jorphan.gui.layout.VerticalLayout;

import org.apache.jmeter.gui.util.JMeterBoxedLayout;
import org.apache.jmeter.testelement.*;
import org.apache.jmeter.util.*;


/**
 * This abstract class takes care of the most basic functions necessary to create a viable
 * JMeter GUI component.  It extends JPanel and implements JMeterGUIComponent.  This
 * abstract is, in turn, extended by several other abstract classes that create different
 * classes of GUI components for JMeter (ie Visualizers, Timers, Samplers, Modifiers, Controllers, etc).
 *
 * @author mstover
 *
 * @see JMeterGUIComponent
 * @see org.apache.jmeter.config.gui.AbstractConfigGui
 * @see org.apache.jmeter.config.gui.AbstractModifierGui
 * @see org.apache.jmeter.config.gui.AbstractResponseBasedModifierGui
 * @see org.apache.jmeter.assertions.gui.AbstractAssertionGui
 * @see org.apache.jmeter.control.gui.AbstractControllerGui
 * @see org.apache.jmeter.timers.gui.AbstractTimerGui
 * @see org.apache.jmeter.visualizers.gui.AbstractVisualizer
 * @see org.apache.jmeter.samplers.gui.AbstractSamplerGui
 */
public abstract class AbstractJMeterGuiComponent
    extends JPanel
    implements JMeterGUIComponent, LocaleChangeListener
{

    private TestElement element;
    // todo: make this private
    protected NamePanel namePanel;
    private JLabel panelTitleLabel;
    private boolean namedPanel = true;
    private boolean isConfigured = false;


    /**
     * Create a new gui component with a title and name field.
     */
    public AbstractJMeterGuiComponent()
    {
        this(true);
    }


    /**
     * Create a new gui component. The flag indicates whether the new instance
     * has a title and name field.
     *
     * @param named true, if title and name field are required
     */
    public AbstractJMeterGuiComponent(boolean named)
    {
        this.namedPanel = named;
        initComponents();
        if (named)
        {
            panelTitleLabel.setText(JMeterUtils.getResString(getStaticLabel()));
            panelTitleLabel.setName(getStaticLabel());
        }
        JMeterUtils.addLocaleChangeListener(this);
    }


    public TestElement getElement()
    {
        return element;
    }


    /**
     * Set the test element this gui will operate on.
     *
     * @param element the test element
     */
    public final void setElement(TestElement element)
    {
        isConfigured = false;
        this.element = element;
        if (namedPanel)
        {
            getNamePanel().setElement(element);
        }
        if (element instanceof TestElementWrapper) {
            configure(((TestElementWrapper)element).unwrap());
        } else {
            configure(element);
        }
        isConfigured = true;
    }

    /**
     * Provides the Name Panel for extending classes.
     *
     * @return NamePanel name panel or null, if no name panel is present.
     */
    protected NamePanel getNamePanel()
    {
        return namePanel;
    }


    /**
     * This method should be overriden, but the extending class has also to call super.configure(element), as
     * it does the work necessary to configure the name of the component from the
     * given Test Element.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#configure(org.apache.jmeter.testelement.NamedTestElement)
     */
    // todo: make this protected
    public void configure(TestElement element)
    {
    }

    /**
     * This provides a convenience for extenders when they implement the createTestElement()
     * method.  This method will set the name, gui class, and test class for the created
     * Test Element.  It should be called by every extending class when creating Test
     * Elements, as that will best assure consistent behavior.
     * @param mc Test Element being created.
     */
    // todo: remove this as no longer necessary
    protected void configureTestElement(TestElement mc)
    {
        mc.setProperty(NamedTestElement.NAME, getName());
        mc.setProperty(NamedTestElement.GUI_CLASS, this.getClass().getName());
        mc.setProperty(NamedTestElement.TEST_CLASS, mc.getClass().getName());
    }


    /**
     * Utility method to update localized messages. The given components have to have
     * the resource key set as the component's name using setName(java.lang.String).
     *
     * @param components components to be updated.
     */
    protected void updateLocalizedStrings(JComponent[] components)
    {
        for (int i = 0; i < components.length; i++)
        {
            JComponent component = components[i];

            if (component.getName() != null)
            {
                String text = JMeterUtils.getResString(component.getName());

                if (component instanceof JLabel)
                {
                    ((JLabel)component).setText(text);
                } else if (component instanceof JButton)
                {
                    ((JButton)component).setText(text);
                } else if (component instanceof JToggleButton)
                {
                    ((JToggleButton)component).setText(text);
                } else if (component instanceof JTextArea)
                {
                    ((JTextArea)component).setText(text);
                }
            }
        }
    }


    /**
     * Utility method to update localized table headers.
     *
     * @param table
     * @param tableModel
     */
    protected void updateLocalizedTableHeaders(JTable table, TableModel tableModel)
    {
        TableColumnModel columns = table.getColumnModel();

        for (int i = 0; i < columns.getColumnCount(); i++)
        {
            columns.getColumn(i).setHeaderValue(tableModel.getColumnName(i));
        }
        table.getTableHeader().repaint();
    }


    protected void initComponents()
    {

        this.setLayout(new JMeterBoxedLayout(5, VerticalLayout.BOTH, VerticalLayout.TOP));

        if (namedPanel)
        {
            // MAIN PANEL
            JPanel mainPanel = GUIFactory.createPanel();
            mainPanel.setLayout(new VerticalLayout(5, VerticalLayout.LEFT));

            // TITLE
            panelTitleLabel = new JLabel();
            Font curFont = panelTitleLabel.getFont();
            int curFontSize = curFont.getSize();
            curFontSize += 4;
            panelTitleLabel.setFont(new Font(curFont.getFontName(), curFont.getStyle(), curFontSize));
            mainPanel.add(panelTitleLabel);

            // NAME
            // todo: clean this mess up
            namePanel = new NamePanel();
            mainPanel.add(namePanel);

            this.add(mainPanel);
        }
    }


    // LocaleChangeListener methods

    public void localeChanged(LocaleChangeEvent event)
    {
        if (namedPanel)
        {
            namePanel.localeChanged(event);
            updateLocalizedStrings(new JComponent[]{panelTitleLabel});
        }
    }


    public JPopupMenu createPopupMenu(NamedTestElement testElement)
    {
        return null;
    }


    public boolean isConfigured()
    {
        return isConfigured;
    }
}
