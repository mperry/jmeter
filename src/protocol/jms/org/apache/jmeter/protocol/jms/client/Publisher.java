/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jmeter.protocol.jms.client;

import java.io.Closeable;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.jmeter.protocol.jms.Utils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class Publisher implements Closeable {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private final Connection connection;

    private final Session session;

    private final  MessageProducer producer;
    
    private final Context ctx;
    
    private final boolean staticDest;

    /**
     * Create a publisher using either the jndi.properties file or the provided parameters.
     * Uses a static destination (for backward compatibility)
     * 
     * @param useProps true if a jndi.properties file is to be used
     * @param initialContextFactory the (ignored if useProps is true)
     * @param providerUrl (ignored if useProps is true)
     * @param connfactory
     * @param destinationName
     * @param useAuth (ignored if useProps is true)
     * @param securityPrincipal (ignored if useProps is true)
     * @param securityCredentials (ignored if useProps is true)
     * @throws JMSException if the context could not be initialised, or there was some other error
     * @throws NamingException 
     */
    public Publisher(boolean useProps, String initialContextFactory, String providerUrl, 
            String connfactory, String destinationName, boolean useAuth,
            String securityPrincipal, String securityCredentials) throws JMSException, NamingException {
        this(useProps, initialContextFactory, providerUrl, connfactory,
                destinationName, useAuth, securityPrincipal,
                securityCredentials, true);
    }
    
    /**
     * Create a publisher using either the jndi.properties file or the provided parameters
     * @param useProps true if a jndi.properties file is to be used
     * @param initialContextFactory the (ignored if useProps is true)
     * @param providerUrl (ignored if useProps is true)
     * @param connfactory
     * @param destinationName
     * @param useAuth (ignored if useProps is true)
     * @param securityPrincipal (ignored if useProps is true)
     * @param securityCredentials (ignored if useProps is true)
     * @param staticDestination true is the destination is not to change between loops
     * @throws JMSException if the context could not be initialised, or there was some other error
     * @throws NamingException 
     */
    public Publisher(boolean useProps, String initialContextFactory, String providerUrl, 
            String connfactory, String destinationName, boolean useAuth,
            String securityPrincipal, String securityCredentials,
            boolean staticDestination) throws JMSException, NamingException {
        super();
        ctx = InitialContextFactory.getContext(useProps, initialContextFactory, 
                providerUrl, useAuth, securityPrincipal, securityCredentials);
        connection = Utils.getConnection(ctx, connfactory);
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        staticDest = staticDestination;
        if (staticDest) {
            Destination dest = Utils.lookupDestination(ctx, destinationName);
            producer = session.createProducer(dest);
        } else {
            producer = session.createProducer(null);
        }
    }

    public TextMessage publish(String text) throws JMSException,
            NamingException {
        return publish(text, null);
    }
    
    public TextMessage publish(String text, String destinationName)
            throws JMSException, NamingException {
        TextMessage msg = session.createTextMessage(text);
        if (staticDest || destinationName == null) {
            producer.send(msg);
        } else {
            Destination dest = Utils.lookupDestination(ctx, destinationName);
            producer.send(dest, msg);
        }
        return msg;
    }

    public ObjectMessage publish(Serializable contents) throws JMSException,
            NamingException {
        return publish(contents, null);
    }
    
    public ObjectMessage publish(Serializable contents, String destinationName)
            throws JMSException, NamingException {
        ObjectMessage msg = session.createObjectMessage(contents);
        if (staticDest || destinationName == null) {
            producer.send(msg);
        } else {
            Destination dest = Utils.lookupDestination(ctx, destinationName);
            producer.send(dest, msg);
        }
        return msg;
    }

    public MapMessage publish(Map<String, Object> map) throws JMSException,
            NamingException {
        return publish(map, null);
    }
    
    public MapMessage publish(Map<String, Object> map, String destinationName)
            throws JMSException, NamingException {
        MapMessage msg = session.createMapMessage();
        for (Entry<String, Object> me : map.entrySet()) {
            msg.setObject(me.getKey(), me.getValue());
        }
        if (staticDest || destinationName == null) {
            producer.send(msg);
        } else {
            Destination dest = Utils.lookupDestination(ctx, destinationName);
            producer.send(dest, msg);
        }
        return msg;
    }

    /**
     * Close will close the session
     */
    public void close() {
        Utils.close(producer, log);
        Utils.close(session, log);
        Utils.close(connection, log);
    }
}
