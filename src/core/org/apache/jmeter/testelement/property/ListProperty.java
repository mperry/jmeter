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
package org.apache.jmeter.testelement.property;


import java.util.*;


/**
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Revision$
 */
public class ListProperty extends ObjectProperty implements List {

    public ListProperty()
    {
        super(new LinkedList());
    }

    private LinkedList getList() {
        return (LinkedList)getValue();
    }

    public void setValue(Object value)
    {
        super.setValue(new LinkedList((Collection)value));
    }


    public int size()
    {
        return getList().size();
    }

    public boolean isEmpty()
    {
        return getList().isEmpty();
    }

    public boolean contains(Object o)
    {
        return getList().contains(o);
    }

    public Iterator iterator()
    {
        return getList().iterator();
    }

    public Object[] toArray()
    {
        return getList().toArray();
    }

    public Object[] toArray(Object a[])
    {
        return getList().toArray(a);
    }

    public boolean add(Object o)
    {
        return getList().add(o);
    }

    public boolean remove(Object o)
    {
        return getList().remove(o);
    }

    public boolean containsAll(Collection c)
    {
        return getList().containsAll(c);
    }

    public boolean addAll(Collection c)
    {
        return getList().addAll(c);
    }

    public boolean addAll(int index, Collection c)
    {
        return getList().addAll(index, c);
    }

    public boolean removeAll(Collection c)
    {
        return getList().removeAll(c);
    }

    public boolean retainAll(Collection c)
    {
        return getList().retainAll(c);
    }

    public void clear()
    {
        getList().clear();
    }

    public Object get(int index)
    {
        return getList().get(index);
    }

    public Object set(int index, Object element)
    {
        return getList().set(index, element);
    }

    public void add(int index, Object element)
    {
        getList().add(index, element);
    }

    public Object remove(int index)
    {
        return getList().remove(index);
    }

    public int indexOf(Object o)
    {
        return getList().indexOf(o);
    }

    public int lastIndexOf(Object o)
    {
        return getList().lastIndexOf(o);
    }

    public ListIterator listIterator()
    {
        return getList().listIterator();
    }

    public ListIterator listIterator(int index)
    {
        return getList().listIterator(index);
    }

    public List subList(int fromIndex, int toIndex)
    {
        return getList().subList(fromIndex, toIndex);
    }

    public boolean isList()
    {
        return true;
    }

    public Object clone()
    {
        ListProperty clone = (ListProperty)super.clone();

        clone.setValue(getList().clone());

        return clone;
    }
}
