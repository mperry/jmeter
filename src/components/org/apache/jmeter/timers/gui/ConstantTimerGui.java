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
package org.apache.jmeter.timers.gui;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.gui.util.LongFieldDocumentListener;
import org.apache.jmeter.testelement.*;
import org.apache.jmeter.timers.ConstantTimer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 * @author    Michael Stover
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   1.0
 ***************************************/

public class ConstantTimerGui extends AbstractTimerGui implements KeyListener
{

    private JTextField delayInput;
    private JLabel delayLabel;
    private JLabel millisecondsLabel;


    public ConstantTimerGui()
    {
    }


    /****************************************
     * !ToDo (Method description)
     *
     *@param e        !ToDo (Parameter description)
     *@param thrower  !ToDo (Parameter description)
     ***************************************/
    public static void error(Exception e, JComponent thrower)
    {
        JOptionPane.showMessageDialog(thrower, e, "Error", JOptionPane.ERROR_MESSAGE);
    }


    public String getStaticLabel()
    {
        return "constant_timer_title";
    }


    public NamedTestElement createTestElement()
    {
        ConstantTimer timer = new ConstantTimer();
        this.configureTestElement(timer);
//        timer.setDelay(delayInput.getText());
        return timer;
    }


    public void configure(TestElementConfiguration config)
    {
        super.configure(config);
        // todo: variable substitution
//        delayInput.setText(String.valueOf(((ConstantTimer)element).getDelay()));
    }


    protected void initComponents()
    {
        super.initComponents();

        JPanel delayPanel = GUIFactory.createPanel();
        delayPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        delayLabel = new JLabel(JMeterUtils.getResString("constant_timer_delay"));
        delayLabel.setName("constant_timer_delay");
        delayPanel.add(delayLabel, constraints);
        delayInput = new JTextField(6);
        delayInput.addKeyListener(this);
        delayInput.setHorizontalAlignment(JTextField.RIGHT);
        delayInput.getDocument().addDocumentListener(new LongFieldDocumentListener(ConstantTimer.DELAY, delayInput, this));
        constraints = constraints.incrementX();
        delayPanel.add(delayInput, constraints);
        millisecondsLabel = new JLabel(JMeterUtils.getResString("milliseconds"));
        millisecondsLabel.setName("milliseconds");
        constraints = constraints.incrementX();
        delayPanel.add(millisecondsLabel, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        delayPanel.add(filler, constraints);

        add(delayPanel);
    }


    // KeyListener methods
    public void keyReleased(KeyEvent e)
    {
        if (e.getComponent() == delayInput) {
            try {
                Long.parseLong(delayInput.getText());
            } catch (NumberFormatException nfe) {
                if (delayInput.getText().length() > 0) {
                    JOptionPane.showMessageDialog(this, "You must enter a valid number",
                                                  "Invalid data", JOptionPane.WARNING_MESSAGE);
                    // We reset the text to be an empty string instead
                    // of the default value. If we reset it to the
                    // default value, then the user has to delete
                    // that value and reenter his/her own. That's
                    // too much trouble for the user.
                }
            }
        }
    }


    public void keyPressed(KeyEvent e)
    {
    }


    public void keyTyped(KeyEvent e)
    {
    }


    // LocaleChangeListener methdo
    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{delayLabel, millisecondsLabel});
    }
}
