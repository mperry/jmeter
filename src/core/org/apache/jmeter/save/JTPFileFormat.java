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
package org.apache.jmeter.save;


import java.io.*;
import java.beans.IntrospectionException;
import java.util.*;

import org.xml.sax.SAXException;

import org.apache.commons.betwixt.strategy.DecapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;
import org.apache.commons.betwixt.io.BeanWriter;

import org.apache.jmeter.testelement.NamedTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.Property;
import org.apache.jmeter.testelement.property.ObjectProperty;


/**
 * Implementation of the new JMeter Testplan (JTP) file format.
 *
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Revision$
 */
public class JTPFileFormat implements FileFormat
{

    public void store(NamedTestElement element, OutputStream out) throws IOException
    {
        SaveService.saveSubTree(element, System.out);


        write(element, System.out);
    }

    public NamedTestElement load(InputStream in) throws IOException
    {
        return (NamedTestElement)SaveService.loadSubTree(in);
    }


    protected void write(TestElement element, OutputStream out) throws IOException
    {
        Writer writer = new OutputStreamWriter(out, "UTF-8");
        XMLWriter xml = new XMLWriter(writer, true, 3);

        xml.declaration();
        writeElement(element, null, xml);
        xml.close();
    }

    protected void writeElement(TestElement element, String tagName, XMLWriter xml) throws IOException
    {
        if (tagName == null)
        {
            xml.startTag(getTagName(element));
        } else
        {
            xml.startTag(tagName);
        }

        Map properties = element.getProperties();
        Map elementProperties = new HashMap();
        Map listProperties = new HashMap();
        Iterator iterator = properties.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            Property property = (Property)entry.getValue();

            if (property.isPrimitive())
            {
                xml.attribute((String)entry.getKey(), property.getValue().toString());
            } else
            {
                ObjectProperty objectProperty = (ObjectProperty)property;

                if (objectProperty.isElement())
                {
                    elementProperties.put(entry.getKey(), property);
                } else
                {
                    listProperties.put(entry.getKey(), property);
                }
            }
        }

        iterator = elementProperties.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            TestElement elem = (TestElement)((Property)entry.getValue()).getValue();

            writeElement(elem, (String)entry.getKey(), xml);
        }

        iterator = listProperties.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            Iterator items = ((List)entry.getValue()).iterator();

            while (items.hasNext())
            {
                TestElement elem = (TestElement)items.next();

                writeElement(elem, null, xml);
            }
        }

        iterator = element.getChildElements().iterator();

        while (iterator.hasNext())
        {
            TestElement elem = (TestElement)iterator.next();

            writeElement(elem, null, xml);
        }

        xml.end();
    }


    public String getTagName(TestElement element)
    {
        int packageNameLength = element.getClass().getPackage().getName().length();
        if (packageNameLength > 0)
        {
            packageNameLength++; // remove . between package name and class name
        }
        String name = element.getClass().getName().substring(packageNameLength);

        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
