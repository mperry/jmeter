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

package org.apache.jmeter.testelement;


import java.io.*;
import java.util.*;
import java.lang.reflect.InvocationTargetException;
import java.beans.PropertyDescriptor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;

import org.apache.jmeter.gui.NamePanel;

import org.apache.commons.beanutils.PropertyUtils;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 * @author    Michael Stover
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   1.0
 ***************************************/

public abstract class AbstractTestElement implements TestElement, Transferable
{

    transient private static Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor(
        "jmeter.elements");

    private static long idCounter = 0L;


    private List children = new LinkedList();
    private String name;
    private long id;
    private TestElement parent;


    private static final synchronized long getNextId()
    {
        return idCounter++;
    }


    public AbstractTestElement()
    {
        this("Test Element");
    }


    public AbstractTestElement(String name)
    {
        id = getNextId();
        setName(name);
    }


    public long getId()
    {
        return id;
    }


    public Object clone()
    {
        try
        {
            AbstractTestElement clone = (AbstractTestElement)super.clone();
            LinkedList childrenClone = new LinkedList();
            Iterator iterator = children.iterator();

            while (iterator.hasNext())
            {
                TestElement testElement = (TestElement)iterator.next();
                childrenClone.add(testElement.clone());
            }

            clone.children = childrenClone;
            clone.id = getNextId();
            clone.parent = null;
            return clone;
        } catch (CloneNotSupportedException e)
        {
            // this will never happen as we implement Cloneable
        }
        // to make the compiler happy
        return null;
    }


//	public boolean equals(Object o)
//	{
//		if(o instanceof AbstractTestElement)
//		{
//			return ((AbstractTestElement)o).testInfo.equals(testInfo);
//		}
//		else
//		{
//			return false;
//		}
//	}



    /****************************************
     * !ToDo (Method description)
     *
     *@param name  !ToDo (Parameter description)
     ***************************************/
    public void setName(String name)
    {
        this.name = name;
    }


    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public String getName()
    {
        return name;
    }


    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public Collection getPropertyNames()
    {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(getClass());
        ArrayList list = new ArrayList(descriptors.length);

        for (int i = 0; i < descriptors.length; i++)
        {
            list.add(descriptors[i].getName());
        }
        return list;
    }


    private long getLongValue(Object bound)
    {
        if (bound == null)
        {
            return (long)0;
        } else if (bound instanceof Long)
        {
            return ((Long)bound).longValue();
        } else
        {
            return Long.parseLong((String)bound);
        }
    }


    private float getFloatValue(Object bound)
    {
        if (bound == null)
        {
            return (float)0;
        } else if (bound instanceof Float)
        {
            return ((Float)bound).floatValue();
        } else
        {
            return Float.parseFloat((String)bound);
        }
    }


    private double getDoubleValue(Object bound)
    {
        if (bound == null)
        {
            return (double)0;
        } else if (bound instanceof Double)
        {
            return ((Double)bound).doubleValue();
        } else
        {
            return Double.parseDouble((String)bound);
        }
    }


    private String getStringValue(Object bound)
    {
        if (bound == null)
        {
            return "";
        } else
            return bound.toString();
    }


    private int getIntValue(Object bound)
    {
        if (bound == null)
        {
            return (int)0;
        } else if (bound instanceof Integer)
        {
            return ((Integer)bound).intValue();
        } else
        {
            try
            {
                return Integer.parseInt((String)bound);
            } catch (NumberFormatException e)
            {
                return 0;
            }
        }
    }


    private boolean getBooleanValue(Object bound)
    {
        if (bound == null)
        {
            return false;
        } else if (bound instanceof Boolean)
        {
            return ((Boolean)bound).booleanValue();
        } else
        {
            return new Boolean((String)bound).booleanValue();
        }
    }


    public int getPropertyAsInt(String key)
    {
        return getIntValue(getProperty(key));
    }


    public boolean getPropertyAsBoolean(String key)
    {
        return getBooleanValue(getProperty(key));
    }


    public float getPropertyAsFloat(String key)
    {
        return getFloatValue(getProperty(key));
    }


    public long getPropertyAsLong(String key)
    {
        return getLongValue(getProperty(key));
    }


    public double getPropertyAsDouble(String key)
    {
        return getDoubleValue(getProperty(key));
    }


    public String getPropertyAsString(String key)
    {
        return getStringValue(getProperty(key));
    }


    /****************************************
     * !ToDo (Method description)
     *
     *@param element  !ToDo (Parameter description)
     ***************************************/
    protected void mergeIn(TestElement element)
    {
        Iterator iter = element.getPropertyNames().iterator();
        while (iter.hasNext())
        {
            String key = (String)iter.next();
            Object value = element.getProperty(key);
            if (getProperty(key) == null || getProperty(key).equals(""))
            {
                setProperty(key, value);
                continue;
            }
            if (value instanceof TestElement)
            {
                if (getProperty(key) == null)
                {
                    setProperty(key, value);
                } else if (getProperty(key) instanceof TestElement)
                {
                    ((TestElement)getProperty(key)).addChildElement((TestElement)value);
                }
            } else if (value instanceof Collection)
            {
                Iterator iter2 = ((Collection)value).iterator();
                Collection localCollection = (Collection)getProperty(key);
                if (localCollection == null)
                {
                    setProperty(key, value);
                } else
                {
                    while (iter2.hasNext())
                    {
                        Object item = iter2.next();
                        if (!localCollection.contains(item))
                        {
                            localCollection.add(item);
                        }
                    }
                }
            }
        }
    }


    public void addChildElement(TestElement element)
    {
        if (!isValidSubelementType(element))
        {
            throw new IllegalArgumentException("Invalid subelement type " + element.getClass().getName());
        }
        if (element.getParentElement() != null)
        {
            throw new IllegalArgumentException("Element already has another parent");
        }
        children.add(element);
        element.setParent(this);
    }


    public void removeChildElement(TestElement child)
    {
        if (children.remove(child))
        {
            child.setParent(null);
        }
    }


    public List getChildren()
    {
        return Collections.unmodifiableList(children);
    }


    public boolean isValidSubelementType(TestElement element)
    {
        Class elementClass = element.getClass();

        Iterator iterator = getValidSubelementTypes().iterator();

        while (iterator.hasNext())
        {
            Class aClass = (Class)iterator.next();
            if (aClass.isAssignableFrom(elementClass))
            {
                return true;
            }
        }
        return false;
    }


    public Set getValidSubelementTypes()
    {
        return new HashSet();
    }


    public Object getProperty(String property)
    {
        try
        {
            return PropertyUtils.getSimpleProperty(this, property);
        } catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("Invalid property name " + property);
        } catch (InvocationTargetException e)
        {
            throw new IllegalArgumentException("Invalid property name " + property);
        } catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("Invalid property name " + property);
        }
    }


    public void setProperty(String property, Object value)
    {
        try
        {
            PropertyUtils.setSimpleProperty(this, property, value);
        } catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("Invalid property name or value " + property + "/" + value);
        } catch (InvocationTargetException e)
        {
            throw new IllegalArgumentException("Invalid property name or value " + property + "/" + value);
        } catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("Invalid property name or value " + property + "/" + value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid property name or value " + property + "/" + value);
        }
    }


    // todo: remove as soon as all subclasses have a valid implementation
    public String getFunctionalGroup()
    {
        return null;
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
            return this.clone();
        } else
        {
            return null;
        }
    }


    public TestElement getParentElement()
    {
        return parent;
    }


    public void setParent(TestElement parent)
    {
        this.parent = parent;
    }


    public void accept(TestElementVisitor visitor)
    {
        visitor.visit(this);
    }
}
