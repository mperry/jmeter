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
package org.apache.jmeter.gui.document;


import java.io.*;
import java.util.*;
import java.lang.ref.WeakReference;

import javax.swing.*;

import org.apache.jmeter.testelement.*;
import org.apache.jmeter.gui.GUIFactory;


/**
 * Represents a JMeter file and holds all relevant information.
 *
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Revision$
 */
public class JMeterDocument implements TestPlan.TestPlanObserver
{

    private File file;
    private TestElementConfiguration element;
    private String name;
    private TestElementConfiguration currentTestElement;
    private Collection listeners = new HashSet();


    public JMeterDocument(String name, TestElementConfiguration rootElement)
    {
        this(name, null, rootElement);
    }

    public JMeterDocument(String name, File file, TestElementConfiguration rootElement)
    {
        this.file = file;
        this.element = rootElement;
        this.name = name;

//        rootElement.resetDirty();

        if (rootElement instanceof TestPlan) {
            ((TestPlan)element).setObserver(this);
        }
    }


    public boolean isNew()
    {
        return getFile() == null;
    }

    public boolean isDirty()
    {
//        return element.isDirty();
        return false;
    }

    public void resetDirty()
    {
//        element.resetDirty();
//        notifyListeners();
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
        notifyListeners();
    }

    public TestElementConfiguration getElement()
    {
        return element;
    }

    public void setElement(TestElementConfiguration element)
    {
        this.element = element;
    }

    public String getFileName()
    {
        if (getFile() != null)
        {
            return getFile().getName();
        } else
        {
            // todo: i18n
            return "New";
        }
    }

    public ImageIcon getIcon()
    {
        return GUIFactory.getIcon(getElement().getClass().getName() + "_TAB");
    }


    public TestElementConfiguration getCurrentTestElement()
    {
        return currentTestElement;
    }

    public void setCurrentTestElement(TestElementConfiguration currentTestElement)
    {
        this.currentTestElement = currentTestElement;
    }

    public String getAbsolutePath()
    {
        if (getFile() == null)
        {
            // todo: i18n
            return "new file";
        } else
        {
            return getFile().getAbsolutePath();
        }
    }

    public String getName()
    {
        return name;
    }

    public void dirtyChanged(TestPlan element)
    {
        notifyListeners();
    }

    public synchronized void addListener(JMeterDocumentListener listener)
    {
        listeners.add(new WeakReference(listener));
    }

    /**
     * Unregister a listener.
     */
    public synchronized void removeListener(JMeterDocumentListener listener)
    {
        Iterator iterator = listeners.iterator();

        while (iterator.hasNext())
        {
            WeakReference reference = (WeakReference)iterator.next();
            if (reference.get() == listener)
            {
                iterator.remove();
                return;
            }
        }
    }

    /**
     * Notify all listeners
     *
     */
    private synchronized void notifyListeners()
    {
        Iterator iterator = listeners.iterator();

        while (iterator.hasNext())
        {
            WeakReference reference = (WeakReference)iterator.next();
            JMeterDocumentListener listener = (JMeterDocumentListener)reference.get();

            if (listener != null)
            {
                listener.documentChanged(this);
            } else
            {
                iterator.remove();
            }
        }
    }


}
