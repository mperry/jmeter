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


import java.io.*;
import java.util.*;

import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.config.*;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.testelement.property.*;
import org.apache.jmeter.save.TestElementConfiguration;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 * @author    Michael Stover
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   March 13, 2001
 * @version   1.0
 ***************************************/
public class TestPlan extends AbstractNamedTestElement implements Serializable
{

    /****************************************
     * !ToDo (Field description)
     ***************************************/
    public final static String THREAD_GROUPS = "TestPlan.thread_groups";
    public final static String FUNCTIONAL_MODE = "functionalMode";
    public final static String USER_DEFINED_VARIABLES = "userDefinedVariables";

    private List threadGroups = new LinkedList();
    private List configs = new LinkedList();
    private static TestPlan plan;
    private ElementProperty userDefinedVariables = new ElementProperty(new Arguments());
    private BooleanProperty functionalMode = new BooleanProperty(false);


    public TestPlan()
    {
        this("Test Plan");
    }


    public TestPlan(String name)
    {
        super(name);
//        setProperty(THREAD_GROUPS, threadGroups);
    }


    public boolean isFunctionalMode() {
        return getFunctionalMode();
    }
    
    public boolean getFunctionalMode()
    {
        return functionalMode.getBooleanValue();
    }


    public void setFunctionalMode(boolean functionalMode)
    {
        this.functionalMode.setBooleanValue(functionalMode);
    }


    public Arguments getUserDefinedVariables()
    {
        return (Arguments)userDefinedVariables.getElement();
    }


    public void setUserDefinedVariables(Arguments userDefinedVariables)
    {
        this.userDefinedVariables.setElement(userDefinedVariables);
    }


    public Map getUserDefinedVariablesMap()
    {
        return getUserDefinedVariables().getArgumentsAsMap();
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
            plan.setProperty(NamedTestElement.GUI_CLASS, "org.apache.jmeter.control.gui.TestPlanGui");
        }
        return plan;
    }

//	/****************************************
//	 * !ToDo
//	 *
//	 *@param tg  !ToDo
//	 ***************************************/
//	public NamedTestElement addChildElement(NamedTestElement tg)
//	{
//		if(tg instanceof ThreadGroup)
//		{
//			addThreadGroup((ThreadGroup)tg);
//		}
//	}


    public Set getValidSubelementTypes()
    {
        Set answer = super.getValidSubelementTypes();

        answer.add(ThreadGroup.class);
        answer.add(org.apache.jmeter.timers.Timer.class);
        answer.add(ConfigTestElement.class);
        answer.add(Assertion.class);
        answer.add(ResponseBasedModifier.class);

        return answer;
    }


    /****************************************
     * !ToDo
     *
     *@param child  !ToDo
     ***************************************/
    public void addJMeterComponent(NamedTestElement child)
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
