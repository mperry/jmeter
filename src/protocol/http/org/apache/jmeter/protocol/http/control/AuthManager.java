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
import java.net.URL;
import java.util.*;

import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.protocol.http.util.Base64Encoder;
import org.apache.jmeter.testelement.category.ConfigCategory;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.JMeterTabbedReader;
import org.apache.jmeter.gui.util.JMeterTabbedWriter;


/****************************************
 * This class provides a way to provide Authorization in jmeter requests. The
 * format of the authorization file is: URL user pass where URL is an HTTP URL,
 * user a username to use and pass the appropriate password.
 *
 * @author    <a href="mailto:luta.raphael@networks.vivendi.com">Raphaël Luta</a>
 * @author    <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   0.9
 ***************************************/
public class AuthManager extends ConfigTestElement implements ConfigElement, Serializable, ConfigCategory
{

    public final static String AUTHORIZATIONS = "authorizations";

    private final static int columnCount = 3;
    private final static String[] columnNames = {
        JMeterUtils.getResString("auth_base_url"),
        JMeterUtils.getResString("username"),
        JMeterUtils.getResString("password")
    };


    private List authorizations = new LinkedList();


    public AuthManager()
    {
    }


    public List getAuthorizations()
    {
        return authorizations;
    }


    public void setAuthorizations(List authorizations)
    {
        this.authorizations = authorizations;
    }


    /****************************************
     * update an authentication record
     *
     *@param index  !ToDo (Parameter description)
     *@param url    !ToDo (Parameter description)
     *@param user   !ToDo (Parameter description)
     *@param pass   !ToDo (Parameter description)
     ***************************************/
    public void set(int index, String url, String user, String pass)
    {
        Authorization auth = new Authorization(url, user, pass);
        if (index >= 0)
        {
            getAuthObjects().set(index, auth);
        } else
        {
            getAuthObjects().add(auth);
        }
    }

//	public TestElement addChildElement(TestElement el)
//	{
//		if(el.getProperty("password") != null &&
//				el.getProperty("username") != null &&
//				el.getProperty("url") != null)
//		{
//			addAuth(new Authorization(el.getPropertyAsString("url"),
//					el.getPropertyAsString("username"),
//					el.getPropertyAsString("password")));
//		}
//		else
//		{
//			super.addChildElement(el);
//		}
//	}


    public Set getValidSubelementTypes()
    {
        return super.getValidSubelementTypes();
    }


    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public List getAuthObjects()
    {
        return getAuthorizations();
    }


    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public int getColumnCount()
    {
        return columnCount;
    }


    /****************************************
     * !ToDoo (Method description)
     *
     *@param column  !ToDo (Parameter description)
     *@return        !ToDo (Return description)
     ***************************************/
    public String getColumnName(int column)
    {
        return columnNames[column];
    }


    /****************************************
     * !ToDoo (Method description)
     *
     *@param column  !ToDo (Parameter description)
     *@return        !ToDo (Return description)
     ***************************************/
    public Class getColumnClass(int column)
    {
        return columnNames[column].getClass();
    }


    /****************************************
     * !ToDoo (Method description)
     *
     *@param row  !ToDo (Parameter description)
     *@return     !ToDo (Return description)
     ***************************************/
    public Authorization getAuthObjectAt(int row)
    {
        return (Authorization)getAuthObjects().get(row);
    }


    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public boolean isEditable()
    {
        return true;
    }


    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public String getClassLabel()
    {
        return JMeterUtils.getResString("auth_manager_title");
    }


    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public Collection getAddList()
    {
        return null;
    }


    /****************************************
     * return the record at index i
     *
     *@param i  !ToDo (Parameter description)
     *@return   !ToDo (Return description)
     ***************************************/
    public Authorization get(int i)
    {
        return (Authorization)getAuthObjects().get(i);
    }


    /****************************************
     * !ToDoo (Method description)
     *
     *@param url  !ToDo (Parameter description)
     *@return     !ToDo (Return description)
     ***************************************/
    public String getAuthHeaderForURL(URL url)
    {
        if (isSupportedProtocol(url))
        {
            return null;
        }

        StringBuffer header = new StringBuffer();
        for (Iterator enum = getAuthObjects().iterator(); enum.hasNext();)
        {
            Authorization auth = (Authorization)enum.next();
            if (url.toString().startsWith(auth.getUrl()))
            {
                header.append("Basic " + Base64Encoder.encode(auth.getUsername() + ":" + auth.getPassword()));
                break;
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


    /****************************************
     * !ToDo
     *
     *@param config  !ToDo
     ***************************************/
    public void addConfigElement(ConfigElement config)
    {
    }


    /****************************************
     * !ToDo
     *
     *@param auth  !ToDo
     ***************************************/
    public void addAuth(Authorization auth)
    {
        getAuthObjects().add(auth);
    }


    /****************************************
     * !ToDo
     ***************************************/
    public void addAuth()
    {
        getAuthObjects().add(new Authorization());
    }


    /****************************************
     * !ToDo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public boolean expectsModification()
    {
        return false;
    }


    /****************************************
     * !ToDo (Method description)
     ***************************************/
    public void uncompile()
    {
    }


    /****************************************
     * remove an authentication record
     *
     *@param index  !ToDo (Parameter description)
     ***************************************/
    public void remove(int index)
    {
        getAuthObjects().remove(index);
    }


    /****************************************
     * return the number of records
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public int size()
    {
        return getAuthObjects().size();
    }


    private boolean isSupportedProtocol(URL url)
    {
        return !url.getProtocol().toUpperCase().equals("HTTP") &&
            !url.getProtocol().toUpperCase().equals("HTTPS");
    }


    /**
     * Save the given collection of Authorization objects to the given file.
     *
     * @param authorizations collection of {@link Authorization} instances
     * @param authFile  name of the file where to store the authorizations
     * @throws IOException
     */
    public static void saveAuthorizations(Collection authorizations, String authFile) throws IOException
    {
        File file = new File(authFile);

        if (!file.isAbsolute())
        {
            file = new File(System.getProperty("user.dir") + File.separator + authFile);
        }

        JMeterTabbedWriter writer = new JMeterTabbedWriter(new FileWriter(file));

        writer.writeLine("# JMeter generated Authorizations file");
        Iterator iterator = authorizations.iterator();

        while (iterator.hasNext())
        {
            Authorization auth = (Authorization)iterator.next();
            String[] tokens = new String[]{auth.getUrl(), auth.getUsername(), auth.getPassword()};

            writer.write(tokens);
        }

        writer.flush();
        writer.close();
    }


    /**
     * Load  Authorization objects from the given file.
     *
     * @param authFile  name of the file where to load the authorizations from
     * @return collection of {@link Authorization} instances
     * @throws IOException
     */
    public static List loadAuthorizations(String authFile) throws IOException
    {
        File file = new File(authFile);

        if (!file.isAbsolute())
        {
            file = new File(System.getProperty("user.dir") + File.separator + authFile);
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
                answer.add(new Authorization(tokens[0].trim(), tokens[1].trim(), tokens[2].trim()));
            } catch (Exception e)
            {
                throw new IOException("Error parsing auth line\n\t'" + tokens + "'\n\t" + e);
            }
        }

        reader.close();
        return answer;
    }

}

