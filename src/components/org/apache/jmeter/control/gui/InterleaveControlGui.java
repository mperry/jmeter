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
package org.apache.jmeter.control.gui;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.apache.jmeter.control.InterleaveControl;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.testelement.NamedTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 *@author    Kevin Hammond
 *  @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 *@created   $Date$
 *@version   1.0
 ***************************************/
// todo: rename to InterleaveControllerGui

public class InterleaveControlGui extends AbstractControllerGui implements ItemListener
{

    InterleaveControl model;
    JCheckBox style;


    public InterleaveControlGui()
    {
    }


    public void configure(TestElement element)
    {
        super.configure(element);
        int styleValue = ((Integer)element.getPropertyValue(InterleaveControl.STYLE)).intValue();

        if (styleValue == InterleaveControl.DEFAULT_STYLE)
        {
            style.setSelected(true);
        } else
        {
            style.setSelected(false);
        }
    }

    public NamedTestElement createTestElement()
    {
        InterleaveControl ic = new InterleaveControl();
        configureTestElement(ic);
        if (style.isSelected())
        {
            ic.setStyle(ic.DEFAULT_STYLE);
        } else
        {
            ic.setStyle(ic.NEW_STYLE);
        }
        return ic;
    }

    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public String getStaticLabel()
    {
        return "interleave_control_title";
    }

    protected void initComponents()
    {
        super.initComponents();
        JPanel panel = GUIFactory.createPanel();
        panel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        style = new JCheckBox(JMeterUtils.getResString("ignore_subcontrollers"));
        style.addItemListener(this);
        style.setName("ignore_subcontrollers");
        constraints.fillHorizontal(1.0);
        panel.add(style, constraints);
        add(panel);
    }


    public void itemStateChanged(ItemEvent e)
    {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
            getElement().setProperty(InterleaveControl.STYLE, new Integer(InterleaveControl.DEFAULT_STYLE));
        } else
        {
            getElement().setProperty(InterleaveControl.STYLE, new Integer(InterleaveControl.NEW_STYLE));
        }

    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{style});
    }

}
