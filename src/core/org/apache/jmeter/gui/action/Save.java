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
package org.apache.jmeter.gui.action;


import java.awt.event.ActionEvent;
import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.*;

import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.JMeterGUIComponent;
import org.apache.jmeter.gui.document.JMeterDocument;
import org.apache.jmeter.gui.document.JMeterDocumentManager;
import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.save.JTPFileFormat;
import org.apache.jmeter.testelement.NamedTestElement;
import org.apache.jmeter.util.JMeterUtils;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 *@author    Michael Stover
 *@created   February 13, 2001
 *@version   1.0
 ***************************************/

public class Save extends JMeterAction
{

    transient private static Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor(
        "jmeter.gui");


    public Save(String resourceKey)
    {
        super(resourceKey);
    }

    public Save(String resourceKey, int mnemonic)
    {
        super(resourceKey, mnemonic);
    }

    public Save(String resourceKey, int mnemonic, KeyStroke accelerator)
    {
        super(resourceKey, mnemonic, accelerator);
    }


    public void actionPerformed(ActionEvent event)
    {
        JMeterDocument document = JMeterDocumentManager.getInstance().getCurrentDocument();
        String fileName = "";

        if (!document.isNew())
        {
            fileName = document.getFileName();
        }
        JFileChooser chooser = FileDialoger.promptToSaveFile(fileName);

        if (chooser == null)
        {
            return;
        }
        try
        {
            JMeterDocumentManager.getInstance().saveCurrentDocument(chooser.getSelectedFile());
        } catch (IOException e)
        {
            // todo: display dialog
            log.error("Save failed", e);
        }
    }


    protected ImageIcon createIcon()
    {
        return JMeterUtils.getImage("toolbar/Save24.png");
    }

    protected ImageIcon createPressedIcon()
    {
        return JMeterUtils.getImage("toolbar/Save24.pressed.png");
    }


    private void convertSubTree(HashTree tree)
    {
        Iterator iter = new LinkedList(tree.list()).iterator();
        while (iter.hasNext())
        {
            JMeterGUIComponent item = (JMeterGUIComponent)iter.next();
            convertSubTree(tree.getTree(item));
            NamedTestElement testElement = item.createTestElement();
            tree.replace(item, testElement);
        }
    }

    public static class Test extends junit.framework.TestCase
    {

        Save save;

        public Test(String name)
        {
            super(name);
        }

        public void setUp()
        {
            save = new Save("", ' ');
        }

        public void testTreeConversion() throws Exception
        {
            HashTree tree = new ListedHashTree();
            JMeterGUIComponent root = new org.apache.jmeter.config.gui.ArgumentsPanel();
            tree.add(root, root);
            tree.getTree(root).add(root, root);
            save.convertSubTree(tree);
            assertEquals(tree.getArray()[0].getClass().getName(), root.createTestElement().getClass().getName());
            tree = tree.getTree(tree.getArray()[0]);
            assertEquals(tree.getArray()[0].getClass().getName(),
                         root.createTestElement().getClass().getName());
            assertEquals(tree.getTree(tree.getArray()[0]).getArray()[0].getClass().getName(),
                         root.createTestElement().getClass().getName());
        }
    }


    /****************************************
     * Description of the Method
     *
     *@param writer  Description of Parameter
     ***************************************/
    private void closeWriter(OutputStream writer)
    {
        if (writer != null)
        {
            try
            {
                writer.close();
            } catch (Exception ex)
            {
                log.error("", ex);
            }
        }
    }
}
