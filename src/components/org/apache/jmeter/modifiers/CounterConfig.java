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


import java.io.Serializable;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.testelement.VariablesCollection;
import org.apache.jmeter.testelement.property.*;
import org.apache.jmeter.testelement.category.ConfigCategory;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;


/**
 * @author Administrator
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 *
 */
public class CounterConfig extends ConfigTestElement implements Serializable, ThreadListener, ConfigCategory
{

    private static Logger log = LoggingManager.getLoggerFor(JMeterUtils.ELEMENTS);

    public final static String START = "start";
    public final static String END = "end";
    public final static String INCREMENT = "increment";
    public final static String PER_USER = "perUser";
    public final static String VAR_NAME = "varName";

    private int globalCounter = -1;
    private VariablesCollection vars = new VariablesCollection();
    private BooleanProperty perUser = new BooleanProperty(false);
    private IntProperty increment = new IntProperty(1);
    private IntProperty start = new IntProperty(0);
    private IntProperty end = new IntProperty(0);
    private StringProperty varName = new StringProperty("counter");


    /**
     * @see org.apache.jmeter.testelement.ThreadListener#iterationStarted(int)
     */
    public synchronized void iterationStarted(int iterationCount)
    {
        JMeterVariables variables = vars.getVariables();
        if (!getPerUser()) {
            globalCounter++;
            int value = getStart() + (getIncrement() * globalCounter);
            if (value > getEnd()) {
                globalCounter = 0;
                value = getStart();
            }
            variables.put(getVarName(), Integer.toString(value));
        } else {
            String value = variables.get(getVarName());
            if (value == null) {
                variables.put(getVarName(), Integer.toString(getStart()));
            } else {
                try {
                    int current = Integer.parseInt(value);
                    current += getIncrement();
                    if (current > getEnd()) {
                        current = getStart();
                    }
                    variables.put(getVarName(), Integer.toString(current));
                } catch (NumberFormatException e) {
                    log.info("Bad number in Counter config", e);
                }
            }
        }
    }


    /**
     * @see org.apache.jmeter.testelement.ThreadListener#setJMeterVariables(JMeterVariables)
     */
    // todo: implement
    public void setJMeterVariables(JMeterVariables jmVars)
    {
//        vars.addJMeterVariables(jmVars);
//        start = getStart();
//        end = getEnd();
//        increment = getIncrement();
//        perUser = getPerUser();
    }


    public boolean getPerUser()
    {
        return perUser.getBooleanValue();
    }


    public void setPerUser(boolean perUser)
    {
        this.perUser.setBooleanValue(perUser);
    }


    public int getIncrement()
    {
        return increment.getIntValue();
    }


    public void setIncrement(int increment)
    {
        this.increment.setIntValue(increment);
    }


    public int getStart()
    {
        return start.getIntValue();
    }


    public void setStart(int start)
    {
        this.start.setIntValue(start);
    }


    public int getEnd()
    {
        return end.getIntValue();
    }


    public void setEnd(int end)
    {
        this.end.setIntValue(end);
    }


    public String getVarName()
    {
        return varName.getStringValue();
    }


    public void setVarName(String varName)
    {
        this.varName.setValue(varName);
    }
}
