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
package org.apache.jmeter.testelement;


import java.util.*;
import java.awt.datatransfer.*;
import java.io.*;

import org.apache.jmeter.testelement.property.Property;


/**
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Revision$
 */
public class DefaultTestElementConfiguration implements TestElementConfiguration
{

    private Class elementClass;
    private String name;
    private Map properties;
    private List children;
    private TestElementConfiguration parent;

    public DefaultTestElementConfiguration(Class elementClass, String name, Map properties)
    {
        this.elementClass = elementClass;
        this.name = name;
        this.properties = properties;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Class getElementClass()
    {
        return elementClass;
    }

    public String getProperty(String property)
    {
        Property prop = (Property)getProperties().get(property);

        if (prop == null)
        {
            return null;
        }
        return (String)prop.getValue();
    }

    public void setProperty(String property, String value) throws IllegalArgumentException
    {
        Property prop = (Property)getProperties().get(property);

        if (prop != null)
        {
            prop.setValue(value);
        } else
        {
            throw new IllegalArgumentException("Unknown property " + property);
        }
    }

    public String[] getPropertyNames()
    {
        Collection names = getProperties().keySet();
        String[] answer = new String[names.size()];

        names.toArray(answer);
        return answer;
    }

    public List getChildren()
    {
        if (children == null)
        {
            return Collections.EMPTY_LIST;
        }
        return children;
    }

    public void addChild(TestElementConfiguration child)
    {
        if (children == null)
        {
            children = new LinkedList();
        }
        children.add(child);
        child.setParentElement(this);
    }

    public void removeChild(TestElementConfiguration child)
    {
        if (children == null)
        {
            return;
        }
        children.remove(child);
        child.setParentElement(null);
    }

    public void accept(TestElementConfigurationVisitor visitor)
    {
        visitor.visit(this);
    }

    public TestElementConfiguration getParentElement()
    {
        return parent;
    }

    public void setParentElement(TestElementConfiguration parent)
    {
        this.parent = parent;
    }


    public DataFlavor[] getTransferDataFlavors()
    {
        return new DataFlavor[]{DATAFLAVOR};
    }


    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        return flavor.equals(DATAFLAVOR);
    }


    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        if (flavor.equals(DATAFLAVOR))
        {
            return this;
        } else
        {
            return null;
        }
    }



    protected Map getProperties()
    {
        return properties;
    }
}
