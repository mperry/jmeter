/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

package org.apache.jmeter.protocol.http.control;


import java.io.Serializable;

import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.testelement.AbstractTestElement;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;


/**
 * This class is a Cookie encapsulator.
 *
 * @author  <a href="mailto:sdowd@arcmail.com">Sean Dowd</a>
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 */
public class Cookie extends AbstractTestElement implements Serializable
{

    transient private static Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor(
        "jmeter.protocol.http");
    private static String NAME = "name";
    private static String VALUE = "value";
    private static String DOMAIN = "domain";
    private static String EXPIRES = "expires";
    private static String SECURE = "secure";
    private static String PATH = "path";


    private String name = "";
    private String value = "";
    private String domain = "";
    private String path = "";
    private boolean secure = false;
    private long expires = 0;


    public Cookie()
    {
    }

    /**
     * create the coookie
     */
    public Cookie(String name, String value, String domain, String path, boolean secure, long expires)
    {
        this.setName(name);
        this.setValue(value);
        this.setDomain(domain);
        this.setPath(path);
        this.setSecure(secure);
        this.setExpires(expires);
    }

    public void addConfigElement(ConfigElement config)
    {
    }

    public boolean expectsModification()
    {
        return false;
    }

    public String getClassLabel()
    {
        return "Cookie";
    }


    public String getName()
    {
        return name;
    }


    // todo: why synchronized??
    public synchronized void setName(String name)
    {
        this.name = name;
    }


    public String getValue()
    {
        return value;
    }


    // todo: why synchronized??
    public synchronized void setValue(String value)
    {
        this.value = value;
    }


    public String getDomain()
    {
        return domain;
    }


    // todo: why synchronized??
    public synchronized void setDomain(String domain)
    {
        this.domain = domain;
    }


    public long getExpires()
    {
        return expires;
    }


    // todo: why synchronized??
    public synchronized void setExpires(long expires)
    {
        this.expires = expires;
    }


    public String getPath()
    {
        return path;
    }


    // todo: why synchronized??
    public synchronized void setPath(String path)
    {
        this.path = path;
    }


    public boolean isSecure()
    {
        return secure;
    }


    // todo: why synchronized??
    public synchronized void setSecure(boolean secure)
    {
        this.secure = secure;
    }


    /**
     * creates a string representation of this cookie
     */
    public String toString()
    {
        return getDomain() + "\tTRUE\t" + getPath() + "\t" +
            new Boolean(isSecure()).toString().toUpperCase() + "\t" +
            getExpires() + "\t" + getName() + "\t" + getValue();
    }
}
