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
package org.apache.jmeter.protocol.http.modifier;


import java.io.Serializable;

import junit.framework.TestCase;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.ResponseBasedModifier;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.AbstractNamedTestElement;
import org.apache.jmeter.testelement.category.ResponseBasedModifierCategory;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;


/**
 * @author mstover
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 *
 */
public class URLRewritingModifier extends AbstractNamedTestElement
    implements Serializable, ResponseBasedModifier, ResponseBasedModifierCategory
{

    transient private static Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor("jmeter.protocol.http");

    public final static String ARGUMENT_NAME = "argumentName";
    public final static String PATH_EXTENSION = "pathExtension";

    transient private static final Perl5Compiler compiler = new Perl5Compiler();

    private String argumentName = "";
    private boolean pathExtension = false;
    private Pattern case1, case2, case3;


    /**
     * @see ResponseBasedModifier#modifyEntry(Sampler, SampleResult)
     */
    public boolean modifyEntry(Sampler sampler, SampleResult responseText)
    {
        if (case1 == null)
        {
            initRegex(getArgumentName());
        }
        String text = new String(responseText.getResponseData());
        Perl5Matcher matcher = new Perl5Matcher();
        String value = "";
        if (matcher.contains(text, case1))
        {
            MatchResult result = matcher.getMatch();
            value = result.group(1);
        } else if (matcher.contains(text, case2))
        {
            MatchResult result = matcher.getMatch();
            value = result.group(1);
        } else if (matcher.contains(text, case3))
        {
            MatchResult result = matcher.getMatch();
            value = result.group(1);
        }
        modify((HTTPSampler)sampler, value);
        if (value.length() > 0)
        {
            return true;
        }
        return false;
    }


    private void modify(HTTPSampler sampler, String value)
    {
        if (isPathExtension())
        {
            sampler.setPath(
                sampler.getPath() + ";" + getArgumentName() + "=" + value);
        } else
        {
            sampler.getArguments().removeArgument(getArgumentName());
            sampler.getArguments().addArgument(
                new HTTPArgument(getArgumentName(), value, true));
        }
    }


    public void setArgumentName(String argName)
    {
        argumentName = argName;
        case1 = case2 = case3 = null;
    }


    private void initRegex(String argName)
    {
        try
        {
            case1 = compiler.compile(argName + "=([^\"'>& \n\r]*)[& \\n\\r\"'>]?$?");
            case2 =
                compiler.compile(
                    "[Nn][Aa][Mm][Ee]=\""
                    + argName
                    + "\"[^>]+[vV][Aa][Ll][Uu][Ee]=\"([^\"]*)\"");
            case3 =
                compiler.compile(
                    "[vV][Aa][Ll][Uu][Ee]=\"([^\"]*)\"[^>]+[Nn][Aa][Mm][Ee]=\""
                    + argName
                    + "\"");
        } catch (MalformedPatternException e)
        {
            log.error("", e);
        }
    }


    public String getArgumentName()
    {
        return argumentName;
    }


    public void setPathExtension(boolean pathExt)
    {
        pathExtension = pathExt;
    }


    public boolean isPathExtension()
    {
        return pathExtension;
    }


    public static class Test extends TestCase
    {

        SampleResult response;


        public Test(String name)
        {
            super(name);
        }


        public void setUp()
        {
        }


        public void testGrabSessionId() throws Exception
        {
            String html =
                "location: http://server.com/index.html?session_id=jfdkjdkf%jddkfdfjkdjfdf";
            response = new SampleResult();
            response.setResponseData(html.getBytes());
            URLRewritingModifier mod = new URLRewritingModifier();
            mod.setArgumentName("session_id");
            HTTPSampler sampler = new HTTPSampler();
            sampler.setDomain("server.com");
            sampler.setPath("index.html");
            sampler.setMethod(HTTPSampler.METHOD_GET);
            sampler.setProtocol("http");
            sampler.addArgument("session_id", "adfasdfdsafasdfasd");
            mod.modifyEntry(sampler, response);
            Arguments args = sampler.getArguments();
            assertEquals(
                "jfdkjdkf%jddkfdfjkdjfdf",
                ((Argument)args.getArguments().get(0)).getValue());
            assertEquals(
                "http://server.com:80/index.html?session_id=jfdkjdkf%jddkfdfjkdjfdf",
                sampler.toString());
        }


        public void testGrabSessionId2() throws Exception
        {
            String html =
                "<a href=\"http://server.com/index.html?session_id=jfdkjdkfjddkfdfjkdjfdf\">";
            response = new SampleResult();
            response.setResponseData(html.getBytes());
            URLRewritingModifier mod = new URLRewritingModifier();
            mod.setArgumentName("session_id");
            HTTPSampler sampler = new HTTPSampler();
            sampler.setDomain("server.com");
            sampler.setPath("index.html");
            sampler.setMethod(HTTPSampler.METHOD_GET);
            sampler.setProtocol("http");
            mod.modifyEntry(sampler, response);
            Arguments args = sampler.getArguments();
            assertEquals(
                "jfdkjdkfjddkfdfjkdjfdf",
                ((Argument)args.getArguments().get(0)).getValue());
        }


        public void testGrabSessionId3() throws Exception
        {
            String html =
                "href='index.html?session_id=jfdkjdkfjddkfdfjkdjfdf'";
            response = new SampleResult();
            response.setResponseData(html.getBytes());
            URLRewritingModifier mod = new URLRewritingModifier();
            mod.setArgumentName("session_id");
            HTTPSampler sampler = new HTTPSampler();
            sampler.setDomain("server.com");
            sampler.setPath("index.html");
            sampler.setMethod(HTTPSampler.METHOD_GET);
            sampler.setProtocol("http");
            mod.modifyEntry(sampler, response);
            Arguments args = sampler.getArguments();
            assertEquals(
                "jfdkjdkfjddkfdfjkdjfdf",
                ((Argument)args.getArguments().get(0)).getValue());
        }
    }
}
