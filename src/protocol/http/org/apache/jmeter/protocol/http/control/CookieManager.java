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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.testelement.PerThreadClonable;
import org.apache.jmeter.testelement.category.ConfigCategory;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.JMeterTabbedReader;
import org.apache.jmeter.gui.util.JMeterTabbedWriter;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;


/**
 * This class provides an interface to the netscape cookies file to
 * pass cookies along with a request.
 *
 * @author  <a href="mailto:sdowd@arcmail.com">Sean Dowd</a>
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Revision$ $Date$
 */
public class CookieManager extends ConfigTestElement implements Serializable, PerThreadClonable, ConfigCategory
{

    transient private static Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor("jmeter.protocol.http");

    public static final String COOKIES = "cookies";
    /**
     * A vector of Cookies managed by this class.
     * @associates <{org.apache.jmeter.controllers.Cookie}>
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd-MMM-yyyy HH:mm:ss zzz");

    private static List addableList = new LinkedList();


    private List cookies = new LinkedList();


    static
    {
        // The cookie specification requires that the timezone be GMT.
        // See http://developer.netscape.com/docs/manuals/communicator/jsguide4/cookies.htm
        // See http://www.cookiecentral.com/faq/
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    public CookieManager()
    {
    }


    public List getCookies()
    {
        return cookies;
    }


    public void setCookies(List cookies)
    {
        this.cookies = cookies;
    }


    public int getCookieCount()
    {
        return getCookies().size();
    }


    // Incorrect method. Always returns String. I changed CookiePanel code to perform
    // this lookup.
    //public Class getColumnClass(int column)
    //{
    //	return columnNames[column].getClass();
    //}

    public Cookie getCookie(int row)
    {
        return (Cookie)getCookies().get(row);
    }


    /** add a cookie */
    public void add(Cookie c)
    {
        getCookies().add(c);
    }


    /** add an empty cookie */
    public void add()
    {
        getCookies().add(new Cookie());
    }


    /** remove a cookie */
    public void remove(int index)
    {
        getCookies().remove(index);
    }


    /** return the number cookies */
    public int size()
    {
        return getCookies().size();
    }


    /** return the cookie at index i */
    public Cookie get(int i)
    {
        return (Cookie)getCookies().get(i);
    }


    public String convertLongToDateFormatStr(long dateLong)
    {
        return dateFormat.format(new Date(dateLong));
    }


    public long convertDateFormatStrToLong(String dateStr)
    {
        long time = 0;

        try
        {
            Date date = dateFormat.parse(dateStr);
            time = date.getTime();
        } catch (ParseException e)
        {
            // ERROR!!!
            // Later, display error dialog?  For now, we have
            // to specify a number that can be converted to
            // a Date. So, I chose 0. The Date will appear as
            // the beginning of the Epoch (Jan 1, 1970 00:00:00 GMT)
            time = 0;
            log.error("DateFormat.ParseException: ", e);
        }

        return time;
    }


    public String getCookieHeaderForURL(URL url)
    {
        if (!url.getProtocol().toUpperCase().trim().equals("HTTP")
            &&
            !url.getProtocol().toUpperCase().trim().equals("HTTPS"))
            return null;

        StringBuffer header = new StringBuffer();
        for (Iterator enum = getCookies().iterator(); enum.hasNext();)
        {
            Cookie cookie = (Cookie)enum.next();
            if (url.getHost().endsWith(cookie.getDomain()) &&
                url.getFile().startsWith(cookie.getPath()) &&
                (System.currentTimeMillis() / 1000) <= cookie.getExpires())
            {
                if (header.length() > 0)
                {
                    header.append("; ");
                }
                header.append(cookie.getName()).append("=").append(cookie.getValue());
            }
        }

        if (header.length() != 0)
        {
            return header.toString();
        } else
        {
            return null;
        }
    }


    public void addCookieFromHeader(String cookieHeader, URL url)
    {
        StringTokenizer st = new StringTokenizer(cookieHeader, ";");
        String nvp;

        // first n=v is name=value
        nvp = st.nextToken();
        int index = nvp.indexOf("=");
        String name = nvp.substring(0, index);
        String value = nvp.substring(index + 1);
        String domain = url.getHost();
        String path = "/";

        Cookie newCookie = new Cookie(name, value, domain, path, false,
                                      System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        // check the rest of the headers
        while (st.hasMoreTokens())
        {
            nvp = st.nextToken();
            nvp = nvp.trim();
            index = nvp.indexOf("=");
            if (index == -1)
            {
                index = nvp.length();
            }
            String key = nvp.substring(0, index);
            if (key.equalsIgnoreCase("expires"))
            {
                try
                {
                    String expires = nvp.substring(index + 1);
                    Date date = dateFormat.parse(expires);
                    newCookie.setExpires(date.getTime());
                } catch (ParseException pe)
                {
                }
            } else if (key.equalsIgnoreCase("domain"))
            {
                newCookie.setDomain(nvp.substring(index + 1));
            } else if (key.equalsIgnoreCase("path"))
            {
                newCookie.setPath(nvp.substring(index + 1));
            } else if (key.equalsIgnoreCase("secure"))
            {
                newCookie.setSecure(true);
            }
        }

        Vector removeIndices = new Vector();
        for (int i = getCookies().size() - 1; i >= 0; i--)
        {
            Cookie cookie = (Cookie)getCookies().get(i);
            if (cookie == null)
                continue;
            if (cookie.getPath().equals(newCookie.getPath()) &&
                cookie.getDomain().equals(newCookie.getDomain()) &&
                cookie.getName().equals(newCookie.getName()))
            {
                removeIndices.addElement(new Integer(i));
            }
        }

        for (Enumeration e = removeIndices.elements(); e.hasMoreElements();)
        {
            index = ((Integer)e.nextElement()).intValue();
            getCookies().remove(index);
        }

        if (newCookie.getExpires() >= System.currentTimeMillis())
        {
            getCookies().add(newCookie);
        }
    }


    public void removeCookieNamed(String name)
    {
        Vector removeIndices = new Vector();
        for (int i = getCookies().size() - 1; i > 0; i--)
        {
            Cookie cookie = (Cookie)getCookies().get(i);
            if (cookie == null)
                continue;
            if (cookie.getName().equals(name))
                removeIndices.addElement(new Integer(i));
        }

        for (Enumeration e = removeIndices.elements(); e.hasMoreElements();)
        {
            getCookies().remove(((Integer)e.nextElement()).intValue());
        }

    }


    /**
     * Store a collection of cookies in the given file.
     *
     * @param cookies collection of {@link Cookie} instances
     * @param authFile file name
     * @throws IOException
     */
    public static void saveCookies(Collection cookies, String authFile) throws IOException
    {
        File file = new File(authFile);

        if (!file.isAbsolute())
        {
            file = new File(System.getProperty("user.dir") + File.separator + authFile);
        }

        JMeterTabbedWriter writer = new JMeterTabbedWriter(new FileWriter(file));

        writer.writeLine("# JMeter generated Cookie file");
        Iterator iterator = cookies.iterator();

        while (iterator.hasNext())
        {
            Cookie cookie = (Cookie)iterator.next();
            String[] tokens = new String[]{cookie.getDomain(), "TRUE", cookie.getPath(), String.valueOf(cookie.isSecure()).toUpperCase(),
                                           String.valueOf(cookie.getExpires()), cookie.getName(), cookie.getValue()};

            writer.write(tokens);
        }

        writer.flush();
        writer.close();
    }


    /**
     * Load cookies from the given file.
     * @param cookieFile file name
     * @return list of {@link Cookie} instances.
     * @throws IOException
     */
    public static List loadCookies(String cookieFile) throws IOException
    {
        File file = new File(cookieFile);

        if (!file.isAbsolute())
        {
            file = new File(System.getProperty("user.dir") + File.separator + cookieFile);
        }

        if (!file.canRead())
        {
            throw new IOException("The file you specified cannot be read.");
        }

        JMeterTabbedReader reader = new JMeterTabbedReader(new FileReader(file));

        ArrayList answer = new ArrayList();
        String[] tokens;

        while ((tokens = reader.readLine()) != null)
        {
            try
            {
                int domain = 0;
                int foo = 1;  // ignore this part of Cookie.toString()
                int path = 2;
                if (tokens[path].equals(" "))
                {
                    tokens[path] = "/";
                }
                boolean secure = new Boolean(tokens[3]).booleanValue();
                long expires = new Long(tokens[4]).longValue();
                int name = 5;
                int value = 6;
                Cookie cookie = new Cookie(tokens[name].trim(), tokens[value].trim(), tokens[domain].trim(),
                                           tokens[path].trim(), secure, expires);
                answer.add(cookie);
            } catch (Exception e)
            {
                throw new IOException("Error parsing cookie tokens\n\t'" + tokens + "'\n\t" + e);
            }
        }
        reader.close();
        return answer;
    }

}
