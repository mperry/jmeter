/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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

package org.apache.jmeter.gui;


import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.jmeter.testelement.TestElement;


/**
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 */
public class GUIFactory
{

    private static final Map guiMap = new HashMap();
    private static final Map iconMap = new HashMap();
    private static final Map guiClassMap = new HashMap();

    public static ImageIcon getIcon(Class elementClass)
    {
        String key = elementClass.getName();
        ImageIcon icon = (ImageIcon)iconMap.get(key);

        if (icon != null)
        {
            return icon;
        }
        if (elementClass.getSuperclass() != null)
        {
            return getIcon(elementClass.getSuperclass());
        }
        return null;
    }

    public static ImageIcon getIcon(String key)
    {
        return (ImageIcon)iconMap.get(key);
    }

    public static synchronized JComponent getGUI(TestElement element)
    {
        String key = element.getClass().getName();
        JComponent gui = (JComponent)guiMap.get(key);

        if (gui != null)
        {
            return gui;
        }
        Class guiClass = getGuiClass(element.getClass());
        gui = createGuiInstance(guiClass);
        guiMap.put(key, gui);
        return gui;
    }


    private static Class getGuiClass(Class elementClass)
    {
        Class answer = (Class)guiClassMap.get(elementClass);

        if (answer != null)
        {
            return answer;
        }
        if (elementClass.getSuperclass() == null)
        {
            return null;
        }
        return getGuiClass(elementClass.getSuperclass());
    }


    public static void registerIcon(String key, ImageIcon icon)
    {
        iconMap.put(key, icon);
    }


    private static JComponent createGuiInstance(Class guiClass)
    {
        try
        {
            JMeterGUIComponent gui = (JMeterGUIComponent)guiClass.newInstance();
            return (JComponent)gui;
        } catch (InstantiationException e)
        {
            throw new IllegalArgumentException("Can not create gui instance " + e.getMessage());
        } catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("Can not create gui instance " + e.getMessage());
        }
    }


    public static void registerGUI(Class elementClass, Class guiClass)
    {
        if (JMeterGUIComponent.class.isAssignableFrom(guiClass) && (JComponent.class.isAssignableFrom(guiClass)))
        {
            guiClassMap.put(elementClass, guiClass);
        } else
        {
            throw new IllegalArgumentException("Class " + guiClass.getName() + " does not implement JComponent/JMeterGUIComponent");
        }
    }


    public static final JPanel createPanel()
    {
        JPanel mainPanel = new JPanel();
        Border margin = new EmptyBorder(5, 10, 1, 10);
        mainPanel.setBorder(margin);
        return mainPanel;
    }


    public static final BorderedPanel createBorderedPanel(String title)
    {
        return new BorderedPanel(title);
    }
}
