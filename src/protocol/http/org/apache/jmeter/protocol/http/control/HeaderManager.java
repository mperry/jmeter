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


import java.io.*;
import java.util.*;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.gui.util.JMeterTabbedWriter;
import org.apache.jmeter.testelement.category.ConfigCategory;
import org.apache.jmeter.util.JMeterTabbedReader;


/**
 * This class provides an interface to headers file to
 * pass HTTP headers along with a request.
 *
 * @author  <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Revision$ $Date$
 */
public class HeaderManager extends ConfigTestElement implements Serializable, ConfigCategory
{

    public static final String HEADERS = "headers";


    private List headers = new LinkedList();


    public HeaderManager()
    {
    }


    public List getHeaders()
    {
        return headers;
    }


    public void setHeaders(List headers)
    {
        this.headers = headers;
    }


    public Header getHeader(int row)
    {
        return (Header)getHeaders().get(row);
    }


    /** add a header */
    public void add(Header h)
    {
        getHeaders().add(h);
    }


    /** add an empty header */
    public void add()
    {
        getHeaders().add(new Header());
    }


    /** remove a header */
    public void remove(int index)
    {
        getHeaders().remove(index);
    }


    /** return the number headers */
    public int size()
    {
        return getHeaders().size();
    }


    /** return the header at index i */
    public Header get(int i)
    {
        return (Header)getHeaders().get(i);
    }

    /*
    public String getHeaderHeaderForURL(URL url) {
         if (!url.getProtocol().toUpperCase().trim().equals("HTTP") &&
                    ! url.getProtocol().toUpperCase().trim().equals("HTTPS")) {
               return null;
         }

         StringBuffer sbHeader = new StringBuffer();
         for (Iterator enum = headers.iterator(); enum.hasNext();) {
               Header header = (Header) enum.next();
               if (url.getHost().endsWith(header.getDomain()) &&
                         url.getFile().startsWith(header.getPath()) &&
                         (System.currentTimeMillis() / 1000) <= header.getExpires()) {
                    if (sbHeader.length() > 0) {
                         sbHeader.append("; ");
                    }
                    sbHeader.append(header.getName()).append("=").append(header.getValue());
               }
         }

         if (sbHeader.length() != 0) {
               return sbHeader.toString();
         } else {
               return null;
         }
    }
    */

    /*
    public void addHeaderFromHeader(String headerHeader, URL url) {
         StringTokenizer st = new StringTokenizer(headerHeader, ";");
         String nvp;

         // first n=v is name=value
         nvp = st.nextToken();
         int index = nvp.indexOf("=");
         String name = nvp.substring(0,index);
         String value = nvp.substring(index+1);
         String domain = url.getHost();

         Header newHeader = new Header(name, value);
         // check the rest of the headers
         while (st.hasMoreTokens()) {
               nvp = st.nextToken();
               nvp = nvp.trim();
               index = nvp.indexOf("=");
               if(index == -1) {
                    index = nvp.length();
               }
               String key = nvp.substring(0,index);

               Vector removeIndices = new Vector();
               for (int i = headers.size() - 1; i >= 0; i--) {
               Header header = (Header) headers.get(i);
               if (header == null) {
                    continue;
               }
               if (header.getName().equals(newHeader.getName())) {
                    removeIndices.addElement(new Integer(i));
               }
         }

         for (Enumeration e = removeIndices.elements(); e.hasMoreElements();) {
               index = ((Integer) e.nextElement()).intValue();
               headers.remove(index);
         }

    }
    */

    public void removeHeaderNamed(String name)
    {
        Vector removeIndices = new Vector();
        for (int i = getHeaders().size() - 1; i > 0; i--)
        {
            Header header = (Header)getHeaders().get(i);
            if (header == null)
            {
                continue;
            }
            if (header.getName().equalsIgnoreCase(name))
            {
                removeIndices.addElement(new Integer(i));
            }
        }

        for (Enumeration e = removeIndices.elements(); e.hasMoreElements();)
        {
            getHeaders().remove(((Integer)e.nextElement()).intValue());
        }
    }


    /**
     * Save the header data to a file.
     * @param headers collection of {@link Header} instances
     * @param headFile file name
     * @throws IOException
     */
    public static void saveHeaders(Collection headers, String headFile) throws IOException
    {
        File file = new File(headFile);

        if (!file.isAbsolute())
        {
            file = new File(System.getProperty("user.dir") + File.separator + headFile);
        }

        JMeterTabbedWriter writer = new JMeterTabbedWriter(new FileWriter(file));

        writer.writeLine("# JMeter generated Header file");
        Iterator iterator = headers.iterator();

        while (iterator.hasNext())
        {
            Header header = (Header)iterator.next();
            String[] tokens = new String[]{header.getName(), header.getValue()};

            writer.write(tokens);
        }

        writer.flush();
        writer.close();
    }


    /**
     * Load header data from a file.
     *
     * @param headerFile file name
     * @return collection of {@link Header} instances
     * @throws IOException
     */
    public static List loadHeaders(String headerFile) throws IOException
    {
        File file = new File(headerFile);

        if (!file.isAbsolute())
        {
            file = new File(System.getProperty("user.dir") + File.separator + headerFile);
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
                answer.add(new Header(tokens[0].trim(), tokens[1].trim()));
            } catch (Exception e)
            {
                throw new IOException("Error parsing header line\n\t'" + tokens + "'\n\t" + e);
            }
        }

        reader.close();
        return answer;
    }
}
