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
package org.apache.jmeter.testelement;


import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 *@author    Michael Stover
 *@created   March 13, 2001
 *@version   1.0
 ***************************************/

public class TestPlan extends AbstractTestElement implements Serializable
{

    /****************************************
     * !ToDo (Field description)
     ***************************************/
    public final static String THREAD_GROUPS = "TestPlan.thread_groups";
    public final static String FUNCTIONAL_MODE = "TestPlan.functional_mode";
    public final static String USER_DEFINED_VARIABLES = "TestPlan.user_defined_variables";

    private List threadGroups = new LinkedList();
    private List configs = new LinkedList();
    private static List itemsCanAdd = new LinkedList();
    private static TestPlan plan;
    private Map userDefinedVariables = new HashMap();

    static
    {
        // WARNING! This String value must be identical to the String value returned
        // in org.apache.jmeter.threads.ThreadGroup.getClassLabel() method.
        // If it's not you will not be able to add a Thread Group element to a Test Plan.
        itemsCanAdd.add(JMeterUtils.getResString("threadgroup"));
    }

    /****************************************
     * !ToDo (Constructor description)
     ***************************************/
    public TestPlan()
    {
        this("Test Plan");
        setFunctionalMode(false);
    }

    public boolean isFunctionalMode()
    {
        return getPropertyAsBoolean(FUNCTIONAL_MODE);
    }

    public void setUserDefinedVariables(Arguments vars)
    {
        setProperty(USER_DEFINED_VARIABLES, vars);
    }

    public Map getUserDefinedVariables()
    {
        Arguments args = (Arguments)getProperty(USER_DEFINED_VARIABLES);
        if (args != null)
        {
            return args.getArgumentsAsMap();
        }
        return new HashMap();
    }

    public void setFunctionalMode(boolean funcMode)
    {
        setProperty(FUNCTIONAL_MODE, new Boolean(funcMode));
    }

    /****************************************
     * !ToDo (Constructor description)
     *
     *@param name  !ToDo (Parameter description)
     ***************************************/
    public TestPlan(String name)
    {
        setName(name);
        setProperty(THREAD_GROUPS, threadGroups);
    }

    public void addParameter(String name, String value)
    {
        userDefinedVariables.put(name, value);
    }

    /****************************************
     * Description of the Method
     *
     *@param name  Description of Parameter
     *@return      Description of the Returned Value
     ***************************************/
    public static TestPlan createTestPlan(String name)
    {
        if (plan == null)
        {
            if (name == null)
            {
                plan = new TestPlan();
            } else
            {
                plan = new TestPlan(name);
            }
            plan.setProperty(TestElement.GUI_CLASS, "org.apache.jmeter.control.gui.TestPlanGui");
        }
        return plan;
    }

    /****************************************
     * !ToDo
     *
     *@param tg  !ToDo
     ***************************************/
    public void addTestElement(TestElement tg)
    {
        if (tg instanceof ThreadGroup)
        {
            addThreadGroup((ThreadGroup)tg);
        }
    }

    /****************************************
     * !ToDo
     *
     *@param child  !ToDo
     ***************************************/
    public void addJMeterComponent(TestElement child)
    {
        if (child instanceof ThreadGroup)
        {
            addThreadGroup((ThreadGroup)child);
        }
    }

    /****************************************
     * Gets the ThreadGroups attribute of the TestPlan object
     *
     *@return   The ThreadGroups value
     ***************************************/
    public Collection getThreadGroups()
    {
        return threadGroups;
    }

    /****************************************
     * Adds a feature to the ConfigElement attribute of the TestPlan object
     *
     *@param c  The feature to be added to the ConfigElement attribute
     ***************************************/
    public void addConfigElement(ConfigElement c)
    {
        configs.add(c);
    }

    /****************************************
     * Adds a feature to the ThreadGroup attribute of the TestPlan object
     *
     *@param group  The feature to be added to the ThreadGroup attribute
     ***************************************/
    public void addThreadGroup(ThreadGroup group)
    {
        threadGroups.add(group);
    }
}
