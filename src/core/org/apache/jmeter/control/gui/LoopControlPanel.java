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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.IntegerFieldDocumentListener;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 * @author    Michael Stover
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   1.0
 ***************************************/
// todo: rename to LoopControllerGui
public class LoopControlPanel extends AbstractControllerGui implements KeyListener, ActionListener
{


	private static String INFINITE = "Infinite Field";
	private static String LOOPS = "Loops Field";



    private JLabel loopsLabel;
    private JRadioButton rbForever;
    private JRadioButton rbCount;
    private JTextField loopInput;
    private JLabel timesLabel;


	public LoopControlPanel()
	{
	}


    public void configure(org.apache.jmeter.testelement.TestElement element)
    {
        loopInput.setText(String.valueOf(element.getProperty(LoopController.LOOP_COUNT)));
        boolean forever = ((Boolean)element.getProperty(LoopController.LOOP_FOREVER)).booleanValue();

        if (forever) {
            rbForever.setSelected(true);
            loopInput.setEnabled(false);
        } else {
            rbCount.setSelected(true);
            loopInput.setEnabled(true);
        }
    }


	/****************************************
	 * !ToDo (Method description)
	 *
	 *@return   !ToDo (Return description)
	 ***************************************/
	public TestElement createTestElement()
	{
		LoopController lc = new LoopController();
//		configureTestElement(lc);
//		if(loops.getText().length() > 0)
//		{
//			lc.setLoopCount(Integer.parseInt(loops.getText()));
//		}
//		else
//		{
//			lc.setLoopCount(-1);
//		}
		return lc;
	}


    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("infinite")) {
            loopInput.setEnabled(false);
            getElement().setProperty(LoopController.LOOP_FOREVER, Boolean.TRUE);
        } else {
            loopInput.setEnabled(true);
            getElement().setProperty(LoopController.LOOP_FOREVER, Boolean.FALSE);
        }
    }

	/****************************************
	 * Description of the Method
	 *
	 *@param e  Description of Parameter
	 ***************************************/
	public void keyPressed(KeyEvent e) { }

	/****************************************
	 * Description of the Method
	 *
	 *@param e  Description of Parameter
	 ***************************************/
	public void keyTyped(KeyEvent e) { }

	/****************************************
	 * Description of the Method
	 *
	 *@param e  Description of Parameter
	 ***************************************/
	public void keyReleased(KeyEvent e)
	{
        // todo: use special input field
		String temp = e.getComponent().getName();
		if(temp.equals(LOOPS))
		{
			try
			{
				Integer.parseInt(loopInput.getText());
			}
			catch(NumberFormatException ex)
			{
				if(loopInput.getText().length() > 0)
				{
					// We need a standard warning/error dialog. The problem with
					// having it here is that the dialog is centered over this
					// LoopControlPanel instead of begin centered in the entire
					// JMeter GUI window.
					JOptionPane.showMessageDialog(this, "You must enter a valid number",
							"Invalid data", JOptionPane.WARNING_MESSAGE);
					loopInput.setText("");
				}
			}
		}
	}


	public String getStaticLabel()
	{
		return "loop_controller_title";
	}


    protected void initComponents()
    {
        super.initComponents();
        JPanel panel = GUIFactory.createPanel();
        panel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        loopsLabel = new JLabel(JMeterUtils.getResString("iterator_num"));
        loopsLabel.setName("iterator_num");
        panel.add(loopsLabel, constraints);

        ButtonGroup loopGroup = new ButtonGroup();
        rbForever = new JRadioButton(JMeterUtils.getResString("infinite"), true);
        rbForever.addActionListener(this);
        rbForever.setActionCommand("infinite");
        constraints = constraints.incrementX();
        constraints.gridwidth = 3;
        constraints.insets = new Insets(3, 3, 1, 3);
        panel.add(rbForever, constraints);
        rbForever.setName("infinite");

        rbCount = new JRadioButton(JMeterUtils.getResString("iterator_loop"), false);
        rbCount.addActionListener(this);
        rbCount.setActionCommand("iterator_loop");
        constraints = constraints.nextRow();
        constraints.gridx++;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(1, 3, 3, 3);
        panel.add(rbCount, constraints);
        rbCount.setName("iterator_loop");

        loopGroup.add(rbForever);
        loopGroup.add(rbCount);
        loopInput = new JTextField(5);
        constraints = constraints.incrementX();
        panel.add(loopInput, constraints);
        loopInput.setName("loopCount");
        loopInput.setText("1");
        loopInput.setEnabled(false);
        loopInput.getDocument().addDocumentListener(new IntegerFieldDocumentListener(LoopController.LOOP_COUNT, loopInput, this));

        timesLabel = new JLabel(JMeterUtils.getResString("iterator_times"));
        timesLabel.setName("iterator_times");
        constraints = constraints.incrementX();
        panel.add(timesLabel, constraints);

        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        panel.add(filler, constraints);

        add(panel);
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{loopsLabel, timesLabel, rbCount, rbForever});
    }

}
