/*
 * $Id$
 *
 * (c) 2002 Oliver Rossmueller
 *
 * This file is part of tuxerra.
 *
 * tuxerra is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * tuxerra is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with tuxerra; if not, mailto:oliver@oross.net or have a look at
 * http://www.gnu.org/licenses/licenses.html#GPL
 */
package org.apache.jmeter.gui.document;


import java.util.*;
import java.io.*;
import java.lang.ref.WeakReference;

import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.*;


public class JMeterDocumentManager
{

    private static JMeterDocumentManager instance;

    private Map documents = new HashMap();
    private long count = 0L;
    private Collection listeners = new HashSet();
    private JMeterDocument currentDocument;


    public static synchronized JMeterDocumentManager getInstance()
    {
        if (instance == null)
        {
            instance = new JMeterDocumentManager();
        }
        return instance;
    }

    private JMeterDocumentManager()
    {
    }


    public JMeterDocument loadDocument(File file) throws IOException
    {
        FileInputStream in = new FileInputStream(file);
        TestElement element = SaveService.loadSubTree(in);
        return createDocument(file, element);

    }

    private JMeterDocument createDocument(File file, TestElement element)
    {
        String documentName = "document" + getCount();
        JMeterDocument document = new JMeterDocument(documentName, file, element);

        documents.put(documentName, document);
        notifyListeners(document, false);
        return document;
    }

    public JMeterDocument newTestPlanDocument()
    {
        // todo: i18n ?
        TestPlan testplan = new TestPlan("Test Plan");

        return createDocument(null, testplan);
    }

    /**
     * Register a listener.
     */
    public synchronized void addListener(JMeterDocumentManagerListener listener)
    {
        listeners.add(new WeakReference(listener));
    }

    /**
     * Unregister a listener.
     */
    public synchronized void removeListener(JMeterDocumentManagerListener listener)
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
    private synchronized void notifyListeners(JMeterDocument document, boolean removed)
    {
        Iterator iterator = listeners.iterator();

        while (iterator.hasNext())
        {
            WeakReference reference = (WeakReference)iterator.next();
            JMeterDocumentManagerListener listener = (JMeterDocumentManagerListener)reference.get();

            if (listener != null)
            {
                if (removed)
                {
                    listener.documentRemoved(document);
                } else
                {
                    listener.documentAdded(document);
                }
            } else
            {
                iterator.remove();
            }
        }
    }

    private synchronized long getCount()
    {
        return count++;
    }

    public JMeterDocument getCurrentDocument()
    {
        return currentDocument;
    }

    public void setCurrentDocument(JMeterDocument currentDocument)
    {
        this.currentDocument = currentDocument;
    }

    public TestElement getCurrentTestElement()
    {
        if(getCurrentDocument() == null) {
            return null;
        }

        return getCurrentDocument().getCurrentTestElement();
    }
}

