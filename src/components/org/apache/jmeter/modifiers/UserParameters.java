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
package org.apache.jmeter.modifiers;


import java.io.*;
import java.util.*;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.testelement.VariablesCollection;
import org.apache.jmeter.testelement.category.ConfigCategory;
import org.apache.jmeter.threads.JMeterVariables;


/**
 * @author Administrator
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 *
 */
public class UserParameters extends ConfigTestElement implements Serializable, ThreadListener, ConfigCategory
{

    private static final String NAMES = "names";
    private static final String THREAD_VALUES = "thread_values";

    public static final String PARAMETERS = "parameters";

    VariablesCollection vars = new VariablesCollection();
    int counter = 0;
    transient Iterator threadValues;

    private List parameters = new LinkedList();


    public List getParameters()
    {
        return parameters;
    }


    public void setParameters(List parameters)
    {
        this.parameters = parameters;
    }


    /**
     * @see org.apache.jmeter.config.Modifier#modifyEntry(Sampler)
     */
    public boolean modifyEntry(Sampler Sampler)
    {
        return false;
    }


    public void iterationStarted(int iter)
    {
        if (iter == 1)
        {
            setVariables();
        }
    }


    public void setJMeterVariables(JMeterVariables vars)
    {
        this.vars.addJMeterVariables(vars);
    }


    public List getNames()
    {
        return (List)getPropertyValue(NAMES);
    }


    public List getThreadLists()
    {
        return (List)getPropertyValue(THREAD_VALUES);
    }


    /**
     * The list of names of the variables to hold values.  This list must come in
     * the same order as the sub lists that are given to setThreadLists(List).
     */
    public void setNames(List list)
    {
        setProperty(NAMES, list);
    }


    /**
     * The thread list is a list of lists.  Each list within the parent list is a
     * collection of values for a simulated user.  As many different sets of
     * values can be supplied in this fashion to cause JMeter to set different
     * values to variables for different test threads.
     */
    public void setThreadLists(List threadLists)
    {
        setProperty(THREAD_VALUES, threadLists);
    }


    private synchronized List getValues()
    {
        if (threadValues == null || !threadValues.hasNext())
        {
            threadValues = ((List)getPropertyValue(THREAD_VALUES)).iterator();
        }
        if (threadValues.hasNext())
        {
            return (List)threadValues.next();
        } else
        {
            return new LinkedList();
        }
    }


    private void setVariables()
    {
        Iterator namesIter = getNames().iterator();
        Iterator valueIter = getValues().iterator();
        JMeterVariables jmvars = vars.getVariables();
        while (namesIter.hasNext() && valueIter.hasNext())
        {
            String name = (String)namesIter.next();
            String value = (String)valueIter.next();
            jmvars.put(name, value);
        }
    }


    public static class Parameter
    {

        private String name = "";
        private List values = new LinkedList();


        public Parameter()
        {
            addValue(0);
        }


        public String getName()
        {
            return name;
        }


        public void setName(String name)
        {
            this.name = name;
        }


        public void setValue(int index, String value)
        {
            values.remove(index);
            values.add(index, value);
        }


        public String getValue(int index)
        {
            return (String)values.get(index);
        }


        public void addValue(int index)
        {
            if (index == values.size())
            {
                values.add("");
            } else
            {
                values.add(index, "");
            }
        }


        public void removeValue(int index)
        {
            values.remove(index);
        }


        public int getValueCount()
        {
            return values.size();
        }
    }


}
