/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

package org.apache.jmeter.plugin;


import java.util.*;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;

import org.apache.jmeter.testelement.category.*;


/**
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 */
public class ElementClassRegistry
{

    private static Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor("jmeter.registry");


    public static final String ASSERTION = "ASSERTION";
    public static final String CONFIG = "CONFIG";
    public static final String CONTROLLER = "CONTROLLER";
    public static final String LISTENER = "LISTENER";
    public static final String MODIFIER = "MODIFIER";
    public static final String NONTEST = "NONTEST";
    public static final String RESPONSEBASEDMODIFIER = "RESPONSEBASEDMODIFIER";
    public static final String SAMPLER = "SAMPLER";
    public static final String TIMER = "TIMER";
    public static final String NONE = "NONE";

    private static final Object[][] classification = new Object[][]{
        {AssertionCategory.class, ASSERTION},
        {ConfigCategory.class, CONFIG},
        {ControllerCategory.class, CONTROLLER},
        {ListenerCategory.class, LISTENER},
        {ModifierCategory.class, MODIFIER},
        {NonTestElementCategory.class, NONTEST},
        {ResponseBasedModifierCategory.class, RESPONSEBASEDMODIFIER},
        {SamplerCategory.class, SAMPLER},
        {TimerCategory.class, TIMER}
    };

    private static final ElementClassRegistry instance = new ElementClassRegistry();


    private Set elementClasses = new HashSet();
    private Map categorizedElementClasses = new HashMap();
    private Set javaSamplerClients = new HashSet();
    private SortedSet javaSamplerClientClassNames = new TreeSet();


    public static final ElementClassRegistry getInstance()
    {
        return instance;
    }


    public synchronized void registerElementClass(Class elementClass)
    {
        elementClasses.add(elementClass);
        categorizeElementClass(elementClass);
    }

    public synchronized void registerJavaSamplerClientClass(Class elementClass)
    {
        javaSamplerClients.add(elementClass);
        javaSamplerClientClassNames.add(elementClass.getName());
    }


    public synchronized Set getElementClassesForCategory(String category)
    {
        Set classes = (Set)categorizedElementClasses.get(category);

        if (classes == null)
        {
            return Collections.EMPTY_SET;
        }
        return Collections.unmodifiableSet(classes);
    }


    public synchronized Set getJavaSamplerClientClasses()
    {
        return Collections.unmodifiableSet(javaSamplerClients);
    }

    public synchronized String[] getJavaSamplerClientClassNames()
    {
        String[] answer = new String[javaSamplerClientClassNames.size()];

        javaSamplerClientClassNames.toArray(answer);
        return answer;
    }

    private synchronized void categorizeElementClass(Class elementClass)
    {
        String category = NONE;

        for (int i = 0; i < classification.length; i++)
        {
            Class clazz = (Class)classification[i][0];

            if (clazz.isAssignableFrom(elementClass))
            {
                category = (String)classification[i][1];
                break;
            }
        }

        Set coll = (Set)categorizedElementClasses.get(category);

        if (coll == null)
        {
            coll = new HashSet();
            categorizedElementClasses.put(category, coll);
        }
        coll.add(elementClass);
    }
}
