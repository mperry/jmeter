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
package org.apache.jmeter.assertions;


import java.io.*;
import java.util.*;

import org.apache.oro.text.MalformedCachePatternException;
import org.apache.oro.text.PatternCacheLRU;
import org.apache.oro.text.regex.*;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.AbstractNamedTestElement;
import org.apache.jmeter.testelement.category.AssertionCategory;


/************************************************************
 *  Title: Jakarta-JMeter Description: Copyright: Copyright (c) 2001 Company:
 *  Apache
 *
 * @author     Michael Stover
 * @author     <a href="mailto:jacarlco@katun.com">Jonathan Carlson</a>
 * @author     <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created    $Date$
 * @version    $Revision$
 ***********************************************************/

public class ResponseAssertion extends AbstractNamedTestElement implements Serializable, Assertion, AssertionCategory
{

    public final static String TEST_FIELD = "testField";
    public final static String TEST_MODE = "testMode";
    public final static String TEST_PATTERNS = "patterns";

    public static final int TEXT_RESPONSE = 0;
    public static final int URL = 1;

    public static final int CONTAINS = 0;  // bit mask 00
    public static final int CONTAINS_NOT = 1;  // bit mask 01
    public static final int MATCH = 2;  // bit mask 10
    public static final int MATCH_NOT = 3;  // bit mask 11
    public static final int NOT_MASK = 1;  // bit mask 01

    private String notMessage = "";
    private String failMessage = "to contain: ";
// todo: replace usage of old constants
//	public final static int MATCH = 1 << 0;
//	public final static int CONTAINS = 1 << 1;
    public final static int NOT = 1 << 2;

    private static ThreadLocal matcher =
        new ThreadLocal()
        {
            protected Object initialValue()
            {
                return new Perl5Matcher();
            }
        };
    private static PatternCacheLRU patternCache =
        new PatternCacheLRU(1000, new Perl5Compiler());


    private int testField = TEXT_RESPONSE;
    private int testMode = CONTAINS;
    private List patterns = new LinkedList();


    public ResponseAssertion()
    {
    }


    public ResponseAssertion(int field, int type, String string)
    {
        this();
        setTestField(field);
        setTestMode(type);
        getTestStrings().add(string);
    }


    public List getPatterns()
    {
        return patterns;
    }


    public void setPatterns(List patterns)
    {
        this.patterns = patterns;
    }


    public int getTestField()
    {
        return testField;
    }


    public void setTestField(int testField)
    {
        this.testField = testField;
    }


    public int getTestMode()
    {
        return testMode;
    }


    public void setTestMode(int testMode)
    {
        if (testMode < 0 || testMode > MATCH_NOT)
        {
            throw new IllegalArgumentException("Unknonw type constant " + testMode);
        }

        this.testMode = testMode;

        if ((testMode & NOT_MASK) > 0)
        {
            notMessage = "not ";
        } else
        {
            notMessage = "";
        }

        if ((testMode & MATCH) > 0)
        {
            failMessage = "to contain: ";
        } else
        {
            failMessage = "to match: ";
        }
    }


    /************************************************************
     *  !ToDo (Method description)
     *
     *@param  testString  !ToDo (Parameter description)
     ***********************************************************/
    public void addTestString(String testString)
    {
        getTestStrings().add(testString);
    }


    public void setTestString(String testString, int index)
    {
        getTestStrings().set(index, testString);
    }


    public void removeTestString(String testString)
    {
        getTestStrings().remove(testString);
    }


    public void removeTestString(int index)
    {
        getTestStrings().remove(index);
    }


    public void clearTestStrings()
    {
        getTestStrings().clear();
    }


    /************************************************************
     *  !ToDoo (Method description)
     *
     *@param  response  !ToDo (Parameter description)
     *@return           !ToDo (Return description)
     ***********************************************************/
    public AssertionResult getResult(SampleResult response)
    {
        AssertionResult result;
        if (!response.isSuccessful())
        {
            result = new AssertionResult();
            result.setError(true);
            result.setFailureMessage(new String(response.getResponseData()));
            return result;
        }
        result = evaluateResponse(response);
        return result;
    }


    /************************************************************
     *  !ToDoo (Method description)
     *
     *@return    !ToDo (Return description)
     ***********************************************************/
    public List getTestStrings()
    {
        return (List)getPropertyValue(TEST_PATTERNS);
    }


    public boolean isContainsType()
    {
        return (getTestMode() & CONTAINS) > 0;
    }


    public boolean isMatchType()
    {
        return (getTestMode() & MATCH) > 0;
    }


    public boolean isNotType()
    {
        return (getTestMode() & NOT) > 0;
    }


    public void setToContainsType()
    {
        setTestMode((getTestMode() | CONTAINS) & (MATCH ^ (CONTAINS | MATCH | NOT)));
        failMessage = "to contain: ";
    }


    public void setToMatchType()
    {
        setTestMode((getTestMode() | MATCH) & (CONTAINS ^ (CONTAINS | MATCH | NOT)));
        failMessage = "to match: ";
    }


    public void setToNotType()
    {
        setTestMode((getTestMode() | NOT));
    }


    public void unsetNotType()
    {
        setTestMode(getTestMode() & (NOT ^ (CONTAINS | MATCH | NOT)));
    }


    /**
     * Make sure the response satisfies the specified assertion requirements.
     *
     * @param response an instance of SampleResult
     * @return an instance of AssertionResult
     */
    private AssertionResult evaluateResponse(SampleResult response)
    {
        boolean pass = true;
        boolean not = (NOT & getTestMode()) > 0;
        AssertionResult result = new AssertionResult();
        String responseString = new String(response.getResponseData());

        try
        {
            // Get the Matcher for this thread
            Perl5Matcher localMatcher = (Perl5Matcher)matcher.get();

            Iterator iter = getTestStrings().iterator();
            while (iter.hasNext())
            {
                String stringPattern = (String)iter.next();
                Pattern pattern = patternCache.getPattern(stringPattern, Perl5Compiler.READ_ONLY_MASK);
                boolean found;
                if ((CONTAINS & getTestMode()) > 0)
                {
                    found = localMatcher.contains(responseString, pattern);
                } else
                {
                    found = localMatcher.matches(responseString, pattern);
                }
                pass = not ? !found : found;

                if (!pass)
                {
                    result.setFailure(true);
                    result.setFailureMessage("Test Failed, expected " +
                                             notMessage + failMessage + stringPattern);
                    break;
                }
            }
            if (pass)
            {
                result.setFailure(false);
            }
            result.setError(false);
        } catch (MalformedCachePatternException e)
        {
            result.setError(true);
            result.setFailure(false);
            result.setFailureMessage("Bad test configuration" + e);
        }

        return result;
    }

    public static class Test extends junit.framework.TestCase
    {

        int threadsRunning;
        int failed;

        public Test(String name)
        {
            super(name);
        }

        public void testThreadSafety() throws Exception
        {
            Thread[] threads = new Thread[100];
            for (int i = 0; i < threads.length; i++)
            {
                threads[i] = new TestThread();
            }
            failed = 0;
            for (int i = 0; i < threads.length; i++)
            {
                threads[i].start();
                threadsRunning++;
            }
            synchronized (this)
            {
                while (threadsRunning > 0) wait();
            }
            assertEquals(failed, 0);
        }

        class TestThread extends Thread
        {

            static final String TEST_STRING = "Dábale arroz a la zorra el abad.";
            static final String TEST_PATTERN = ".*á.*\\.";

            public void run()
            {
                ResponseAssertion assertion = new ResponseAssertion(
                    TEXT_RESPONSE, CONTAINS, TEST_PATTERN);
                SampleResult response = new SampleResult();
                response.setResponseData(TEST_STRING.getBytes());
                for (int i = 0; i < 100; i++)
                {
                    AssertionResult result;
                    result = assertion.evaluateResponse(response);
                    if (result.isFailure() || result.isError())
                    {
                        failed++;
                    }
                }
                synchronized (Test.this)
                {
                    threadsRunning--;
                    Test.this.notify();
                }
            }
        }
    }

}
