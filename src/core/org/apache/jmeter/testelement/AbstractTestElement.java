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


import java.awt.datatransfer.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.util.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log.Hierarchy;
import org.apache.log.Logger;

import org.apache.jmeter.testelement.property.*;


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


    private HashSet dirtyChildren;
    private TestElement parent;
    private List children = new LinkedList();
    private long id;
    private boolean dirty = false;


    private static final synchronized long getNextId()
    {
        return idCounter++;
    }


    public AbstractTestElement()
    {
        id = getNextId();
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

            clone.children = new LinkedList();
            clone.id = getNextId();
            clone.parent = null;

            Iterator iterator = children.iterator();

            while (iterator.hasNext())
            {
                TestElement testElement = (TestElement)iterator.next();
                clone.addChildElement((TestElement)testElement.clone());
            }

            iterator = getProperties().entrySet().iterator();

            while (iterator.hasNext())
            {
                Map.Entry entry = (Map.Entry)iterator.next();
                String name = (String)entry.getKey();
                Property property = (Property)((Property)entry.getValue()).clone();
                setProperty(name, property);
            }

            return clone;
        } catch (CloneNotSupportedException e)
        {
            throw new InternalError("Object.clone failed");
        }
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




    /**
     * Get all properties by reflection;
     * @return collection of {@link Property} instances
     */
    public Map getProperties()
    {
        HashMap properties = new HashMap();
        Class clazz = this.getClass();

        while (AbstractTestElement.class.isAssignableFrom(clazz))
        {
            collectProperties(clazz, properties);
            clazz = clazz.getSuperclass();
        }

        return properties;
    }

    private void collectProperties(Class clazz, HashMap properties)
    {
        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++)
        {
            if (Property.class.isAssignableFrom(fields[i].getType()))
            {
                fields[i].setAccessible(true);
                try
                {
                    properties.put(fields[i].getName(), fields[i].get(this));
                } catch (IllegalArgumentException e)
                {
                    log.warn("Introspection failed", e);
                } catch (IllegalAccessException e)
                {
                    log.warn("Introspection failed", e);
                }
            }
        }
    }

    public Set getPropertyNames()
    {
        return new HashSet(getProperties().keySet());
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
            return 0;
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
        return getIntValue(getPropertyValue(key));
    }


    public boolean getPropertyAsBoolean(String key)
    {
        return getBooleanValue(getPropertyValue(key));
    }


    public float getPropertyAsFloat(String key)
    {
        return getFloatValue(getPropertyValue(key));
    }


    public long getPropertyAsLong(String key)
    {
        return getLongValue(getPropertyValue(key));
    }


    public double getPropertyAsDouble(String key)
    {
        return getDoubleValue(getPropertyValue(key));
    }


    public String getPropertyAsString(String key)
    {
        return getStringValue(getPropertyValue(key));
    }


    /****************************************
     * !ToDo (Method description)
     *
     *@param element  !ToDo (Parameter description)
     ***************************************/
    protected void mergeIn(NamedTestElement element)
    {
//        Iterator iter = element.getPropertyNames().iterator();
//        while (iter.hasNext())
//        {
//            String key = (String)iter.next();
//            Object value = element.getPropertyValue(key);
//            if (getPropertyValue(key) == null || getPropertyValue(key).equals(""))
//            {
//                setProperty(key, value);
//                continue;
//            }
//            if (value instanceof NamedTestElement)
//            {
//                if (getPropertyValue(key) == null)
//                {
//                    setProperty(key, value);
//                } else if (getPropertyValue(key) instanceof NamedTestElement)
//                {
//                    ((NamedTestElement)getPropertyValue(key)).addChildElement((NamedTestElement)value);
//                }
//            } else if (value instanceof Collection)
//            {
//                Iterator iter2 = ((Collection)value).iterator();
//                Collection localCollection = (Collection)getPropertyValue(key);
//                if (localCollection == null)
//                {
//                    setProperty(key, value);
//                } else
//                {
//                    while (iter2.hasNext())
//                    {
//                        Object item = iter2.next();
//                        if (!localCollection.contains(item))
//                        {
//                            localCollection.add(item);
//                        }
//                    }
//                }
//            }
//        }
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
        setDirty(true);
    }


    public void removeChildElement(TestElement child)
    {
        if (children.remove(child))
        {
            child.setParent(null);
            if (dirtyChildren != null && dirtyChildren.remove(child)) {
                if (dirtyChildren.isEmpty()) {
                    notifyParent();
                }
            }

        }
    }


    public List getChildElements()
    {
        return Collections.unmodifiableList(children);
    }

    public void setChildElements(List children)
    {
        this.children = new LinkedList(children);
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


    public Property getProperty(String property)
    {
        try
        {
            Field field = findField(property, getClass());

            if (field == null)
            {
                throw new IllegalArgumentException("Invalid property name " + property);
            }

            field.setAccessible(true);
            return (Property)field.get(this);
        } catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("Invalid property name " + property);
        }
    }


    private Field findField(String fieldName, Class clazz)
    {
        try
        {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e)
        {
            Class superclass = clazz.getSuperclass();

            if (NamedTestElement.class.isAssignableFrom(superclass))
            {
                return findField(fieldName, superclass);
            }
        }
        return null;
    }

    public Object getPropertyValue(String property)
    {
        return getProperty(property).getValue();
    }


    public void setProperty(String name, Object value)
    {
        Property property = getProperty(name);
        property.setValue(value);
    }


    public void setProperty(String key, boolean value)
    {
        Property property = getProperty(key);

        if (property instanceof BooleanProperty)
        {
            ((BooleanProperty)property).setBooleanValue(value);
        } else
        {
            property.setValue(String.valueOf(value));
        }
    }

    public void setProperty(String key, long value)
    {
        Property property = getProperty(key);

        if (property instanceof LongProperty)
        {
            ((LongProperty)property).setLongValue(value);
        } else
        {
            property.setValue(String.valueOf(value));
        }
    }

    public void setProperty(String key, int value)
    {
        Property property = getProperty(key);

        if (property instanceof IntProperty)
        {
            ((IntProperty)property).setIntValue(value);
        } else
        {
            property.setValue(String.valueOf(value));
        }
    }


    protected void setProperty(String name, Property property)
    {
        try
        {
            Field field = findField(name, getClass());

            if (field == null)
            {
                throw new IllegalArgumentException("Invalid property name " + property);
            }

            field.setAccessible(true);
            field.set(this, property);
        } catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("Invalid property name " + property);
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

    public boolean isDirty()
    {
        return dirty || hasDirtyChildren();
    }

    protected boolean hasDirtyChildren()
    {
        if (dirtyChildren == null)
        {
            return false;
        }
        return dirtyChildren.size() > 0;
    }

    public void resetDirty()
    {
        dirty = false;
        if (dirtyChildren != null)
        {
            Iterator iterator = dirtyChildren.iterator();

            while (iterator.hasNext())
            {
                TestElement element = (TestElement)iterator.next();

                element.resetDirty();
            }
            dirtyChildren.clear();
        }

    }


    public void dirtyFlagChanged(TestElement childElement)
    {
        if (getChildElements().contains(childElement) && childElement.isDirty()) {
            if (dirtyChildren == null) {
                dirtyChildren = new HashSet();
            }
            if (dirtyChildren.add(childElement)) {
                notifyParent();
            }
        } else if (! childElement.isDirty() && dirtyChildren != null) {
            if (dirtyChildren.remove(childElement)) {
                notifyParent();
            }
        }
    }

    public void propertyChanged(Property property)
    {
        setDirty(true);

    }

    protected void setDirty(boolean flag)
    {
        dirty = flag;

        notifyParent();
    }

    protected void notifyParent()
    {
        if (getParentElement() != null)
        {
            getParentElement().dirtyFlagChanged(this);
        }
    }
}
