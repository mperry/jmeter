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
package org.apache.jmeter.util;


import java.io.*;
import java.util.*;


/**
 * Writes String values separated by a single tab character.
 *
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Revision$
 */
public class JMeterTabbedReader
{

    private BufferedReader in;


    public JMeterTabbedReader(Reader in)
    {
        if (in instanceof BufferedReader) {
            this.in = (BufferedReader)in;
        } else {
            this.in = new BufferedReader(in);
        }
    }


    /**
     * Read the next line from this reader and split it into tokens
     *
     * @return array of tokens or null, if there are no more lines available
     * @throws IOException
     */
    public String[] readLine() throws IOException
    {
        String line = in.readLine();

        if (line == null) {
            return null;
        }

        try {
            // consume comments and empty lines
            while (line.startsWith("#") || line.trim().length() == 0) {
                line = in.readLine();
                if (line == null) {
                    return null;
                }
            }
            return split(line, "\t", " ");

        } catch (Exception e) {
            throw new IOException("Error parsing cookie line\n\t'" + line + "'\n\t" + e);
        }
    }


    public void close() throws IOException
    {
        in.close();
    }


    /******************************************************
     * Takes a String and a tokenizer character, and returns
     a new array of strings of the string split by the tokenizer
     character.
     @param string String to be split
     @param delimiters Character to split the string on
     @param defaultValue Default value to place between two split chars that have nothing between them
     @return Array of all the tokens.
     ******************************************************/
    private String[] split(String string, String delimiters, String defaultValue)
    {
        // if default = "", the loop will run forever
        if (string == null || delimiters == null || defaultValue == null || defaultValue.length() == 0) {
            return new String[0];
        }

        int spot;
        String doubleDelimiters = delimiters + delimiters;

        while ((spot = string.indexOf(doubleDelimiters)) != -1) {
            // fill empty token a with defaultValue
            string = string.substring(0, spot + delimiters.length()) + defaultValue + string.substring(spot + delimiters.length(), string.length());
        }
        if (string.endsWith(delimiters)) {
            // fill empty token at the end with defaultValue
            string = string + defaultValue;
        }
        if (string.startsWith(delimiters)) {
            // fill empty token at the start with defaultValue
            string = defaultValue + string;
        }

        StringTokenizer tokenizer = new StringTokenizer(string, delimiters);
        String[] values = new String[tokenizer.countTokens()];
        int i = 0;

        while (tokenizer.hasMoreTokens()) {
            values[i++] = tokenizer.nextToken();
        }
        return values;
    }

}
