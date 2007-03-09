/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *  
 */

package org.apache.jmeter.config.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;

/**
 * Default config gui for Configuration Element.
 */
public class ObsoleteGui extends AbstractConfigGui {

	private JLabel obsoleteMessage = 
		new JLabel(JMeterUtils.getResString("obsolete_test_element")); // $NON-NLS-1$

	public ObsoleteGui(){
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout(0, 10));
		setBorder(makeBorder());
		add(makeTitlePanel(), BorderLayout.NORTH);
		add(obsoleteMessage,BorderLayout.WEST);
	}

	public TestElement createTestElement() {
		return null;
	}

	public void modifyTestElement(TestElement element) {	
	}

	public String getLabelResource() {
		return "obsolete_test_element"; // $NON-NLS-1$
	}
}