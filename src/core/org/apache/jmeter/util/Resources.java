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

package org.apache.jmeter.util;


import java.util.*;


/**
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 */
public class Resources
{

    private static final Map bundles = new HashMap();
    private static final Map effectiveBundles = new HashMap();


    public static void registerBundle(String bundleName, String resources, ClassLoader classLoader)
    {
        bundles.put(bundleName, new ResourceBundleSpec(resources, classLoader));
        effectiveBundles.clear();
    }


    public static ResourceBundle getBundle(Locale locale)
    {
        ResourceBundle answer = (ResourceBundle)effectiveBundles.get(locale);

        if (answer == null)
        {
            answer = createEffectiveBundle(locale);
            effectiveBundles.put(locale, answer);
        }
        return answer;
    }


    public static String getString(String key, Locale locale)
    {
        if (key == null)
        {
            return null;
        }
        return getBundle(locale).getString(key);
    }


    private static ResourceBundle createEffectiveBundle(Locale locale)
    {
        Map resources = new HashMap();
        Iterator iterator = bundles.values().iterator();

        while (iterator.hasNext())
        {
            ResourceBundleSpec spec = (ResourceBundleSpec)iterator.next();
            ResourceBundle bundle = spec.getBundle(locale);

            Enumeration enum = bundle.getKeys();
            while (enum.hasMoreElements())
            {
                String key = (String)enum.nextElement();
                resources.put(key, bundle.getString(key));
            }
        }
        return new EffectiveResourceBundle(resources);
    }


    private static class ResourceBundleSpec
    {

        private String resources;
        private ClassLoader classLoader;


        public ResourceBundleSpec(String resouces, ClassLoader classLoader)
        {
            this.resources = resouces;
            this.classLoader = classLoader;
        }


        public ResourceBundle getBundle(Locale locale)
        {
            return ResourceBundle.getBundle(resources, locale, classLoader);
        }
    }


    public static class EffectiveResourceBundle extends ResourceBundle
    {

        private Map resources;


        public EffectiveResourceBundle(Map resources)
        {
            this.resources = resources;
        }


        public final Object handleGetObject(String key)
        {
            if (key == null)
            {
                throw new NullPointerException();
            }
            return resources.get(key); // this class ignores locales
        }


        public Enumeration getKeys()
        {
            return Collections.enumeration(Collections.unmodifiableCollection(resources.keySet()));
        }
    }
}