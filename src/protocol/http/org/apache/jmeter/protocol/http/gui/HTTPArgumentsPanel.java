/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001, 2003 The Apache Software Foundation.  All rights
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
package org.apache.jmeter.protocol.http.gui;


import java.util.*;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;


/**
 * @author Administrator
 * @author    <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 *
 */
public class HTTPArgumentsPanel extends ArgumentsPanel
{

    private static final String ENCODED_VALUE = JMeterUtils.getResString("encoded_value");
    private static final String ENCODE_OR_NOT = JMeterUtils.getResString("encode?");


    public HTTPArgumentsPanel()
    {
    }


    protected ArgumentsPanel.ArgumentsTableModel createTableModel(TestElement element)
    {
        return new HTTPArgumentsTableModel((List)element.getProperty(Arguments.ARGUMENTS));
    }


    public TestElement createTestElement()
    {
//		Data model = tableModel.getData();
        Arguments args = new Arguments();
//		model.reset();
//		while(model.next())
//		{
//			if(((Boolean)model.getColumnValue(ENCODE_OR_NOT)).booleanValue())
//			{
//				args.addArgument(new HTTPArgument((String)model.getColumnValue(Arguments.COLUMN_NAMES[0]),
//						model.getColumnValue(Arguments.COLUMN_NAMES[1])));
//			}
//			else
//			{
//				HTTPArgument arg = new HTTPArgument();
//				arg.setAlwaysEncode(false);
//				arg.setName((String)model.getColumnValue(Arguments.COLUMN_NAMES[0]));
//				arg.setValue(model.getColumnValue(Arguments.COLUMN_NAMES[1]));
//				args.addArgument(arg);
//			}
//		}
//		this.configureTestElement(args);
        return (TestElement)args.clone();
    }


    protected static class HTTPArgumentsTableModel extends ArgumentsTableModel
    {

        public HTTPArgumentsTableModel(List arguments)
        {
            super(arguments);
        }


        public int getColumnCount()
        {
            return 3;
        }


        public String getColumnName(int column)
        {
            if (column == 2)
            {
                return JMeterUtils.getResString("encode");
            } else
            {
                return super.getColumnName(column);
            }
        }


        public Class getColumnClass(int columnIndex)
        {
            if (columnIndex == 2)
            {
                return Boolean.class;
            } else
            {
                return super.getColumnClass(columnIndex);
            }
        }


        public Object getValueAt(int row, int column)
        {
            if (column == 2)
            {
                Argument argument = (Argument)getArguments().get(row);

                return argument.getMetaData();
            } else
            {
                return super.getValueAt(row, column);
            }
        }


        public void setValueAt(Object value, int row, int column)
        {
            if (column == 2)
            {
                Argument argument = (Argument)getArguments().get(row);

                argument.setMetaData(value);
            } else
            {
                super.setValueAt(value, row, column);
            }
        }

    }
}
