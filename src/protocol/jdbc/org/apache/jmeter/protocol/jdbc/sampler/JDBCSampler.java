/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 - 2003 The Apache Software Foundation.  All rights
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
package org.apache.jmeter.protocol.jdbc.sampler;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.protocol.jdbc.config.DbConfig;
import org.apache.jmeter.protocol.jdbc.config.PoolConfig;
import org.apache.jmeter.protocol.jdbc.config.SqlConfig;
import org.apache.jmeter.protocol.jdbc.util.DBConnectionManager;
import org.apache.jmeter.protocol.jdbc.util.DBKey;
import org.apache.jmeter.protocol.jdbc.control.gui.JdbcTestSampleGui;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.NamedTestElement;
import org.apache.jmeter.testelement.TestListener;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.apache.jorphan.collections.Data;


/************************************************************
 *  A sampler which understands JDBC database requests
 *
 *@author     mstover
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 *@created    $Date$
 *@version    $Revision$
 ***********************************************************/
public class JDBCSampler extends AbstractSampler implements TestListener
{

    transient private static Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor("jmeter.protocol.jdbc");

    public static final String URL = "url";
    public static final String DRIVER = "driver";
    public static final String CONNECTIONS = "connections";
    public static final String MAXUSE = "maxUse";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public final static String QUERY = "query";

    transient DBConnectionManager manager = DBConnectionManager.getManager();

    private static Map keyMap = new HashMap();
    private static boolean running = false;

    private String driver = "";
    private String url = "";
    private String username = "";
    private String password = "";
    private int connections = 1;
    private int maxUse = 1;
    private String query = "";


    public JDBCSampler()
    {
    }


    public JDBCSampler(String name)
    {
        super(name);
    }

//    public void addCustomTestElement(NamedTestElement element)
//    {
//        if (element instanceof SqlConfig
//            || element instanceof PoolConfig
//            || element instanceof DbConfig) {
//            this.mergeIn(element);
//        }
//    }


    public String getDriver()
    {
        return driver;
    }


    public void setDriver(String driver)
    {
        this.driver = driver;
    }


    public String getUrl()
    {
        return url;
    }


    public void setUrl(String url)
    {
        this.url = url;
    }


    public String getUsername()
    {
        return username;
    }


    public void setUsername(String username)
    {
        this.username = username;
    }


    public String getPassword()
    {
        return password;
    }


    public void setPassword(String password)
    {
        this.password = password;
    }


    public int getConnections()
    {
        return connections;
    }


    public void setConnections(int connections)
    {
        this.connections = connections;
    }


    public int getMaxUse()
    {
        return maxUse;
    }


    public void setMaxUse(int maxUse)
    {
        this.maxUse = maxUse;
    }


    public String getQuery()
    {
        return query;
    }


    public void setQuery(String query)
    {
        this.query = query;
    }


    public Set getValidSubelementTypes()
    {
        Set answer = super.getValidSubelementTypes();

        answer.add(SqlConfig.class);
        answer.add(PoolConfig.class);
        answer.add(DbConfig.class);
        return answer;
    }


    public void testStarted(String host)
    {
    }


    public void testEnded(String host)
    {
    }


    public synchronized void testStarted()
    {
        if (!running)
        {
            running = true;
        }
    }


    public synchronized void testEnded()
    {
        if (running)
        {
            manager.shutdown();
            keyMap.clear();
            running = false;
        }
    }


    /************************************************************
     *  !ToDo (Method description)
     *
     *@param  e  !ToDo (Parameter description)
     *@return    !ToDo (Return description)
     ***********************************************************/
    public SampleResult sample(Entry e)
    {
        DBKey key = getKey();
        long start;
        long end;
        long time;
        time = start = end = 0;
        SampleResult res = new SampleResult();
        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        Data data = new Data();
        res.setSampleLabel(getQuery());
        start = System.currentTimeMillis();
        try
        {
            int count = 0;
            while (count < 20 && (con = manager.getConnection(key)) == null)
            {
                try
                {
                    Thread.sleep(10);
                } catch (Exception err)
                {
                    count++;
                }
            }
            stmt = con.createStatement();
            // Execute database query
            boolean retVal = stmt.execute(getQuery());
            // Based on query return value, get results
            if (retVal)
            {
                rs = stmt.getResultSet();
                data = getDataFromResultSet(rs);
                rs.close();
            } else
            {
                int updateCount = stmt.getUpdateCount();
            }
            stmt.close();
            manager.releaseConnection(con);
            res.setResponseData(data.toString().getBytes());
            res.setDataType(res.TEXT);
            res.setSuccessful(true);
        } catch (Exception ex)
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                } catch (SQLException err)
                {
                    rs = null;
                }
            }
            if (stmt != null)
            {
                try
                {
                    stmt.close();
                } catch (SQLException err)
                {
                    stmt = null;
                }
            }
            manager.releaseConnection(con);
            log.error("", ex);
            res.setResponseData(new byte[0]);
            res.setSuccessful(false);
        }
        // Calculate response time
        end = System.currentTimeMillis();
        time += end - start;
        res.setTime(time);
        res.setSamplerData(this);
        return res;
    }


    private DBKey getKey()
    {
        DBKey key = (DBKey)keyMap.get(getUrl());
        if (key == null)
        {
            key =
                manager.getKey(
                    getUrl(),
                    getUsername(),
                    getPassword(),
                    getDriver(),
                    getMaxUse(),
                    getConnections());
            keyMap.put(getUrl(), key);
        }
        return key;
    }


    /************************************************************
     *  Gets a Data object from a ResultSet.
     *
     *@param  rs                      ResultSet passed in from a database query
     *@return                         A Data object (com.stover.utils)
     *@exception  SQLException        !ToDo (Exception description)
     *@throws  java.sql.SQLException
     ***********************************************************/
    private Data getDataFromResultSet(ResultSet rs) throws SQLException
    {
        ResultSetMetaData meta;
        Data data = new Data();
        meta = rs.getMetaData();
        int numColumns = meta.getColumnCount();
        String[] dbCols = new String[numColumns];
        for (int count = 1; count <= numColumns; count++)
        {
            dbCols[count - 1] = meta.getColumnName(count);
            data.addHeader(dbCols[count - 1]);
        }
        while (rs.next())
        {
            data.next();
            for (int count = 0; count < numColumns; count++)
            {
                Object o = rs.getObject(count + 1);
                if (o == null)
                {
                } else if (o instanceof byte[])
                {
                    o = new String((byte[])o);
                }
                data.addColumnValue(dbCols[count], o);
            }
        }
        return data;
    }


    public String toString()
    {
        return getUrl() + ", user: " + getUsername() + "\n" + getQuery();
    }


}
