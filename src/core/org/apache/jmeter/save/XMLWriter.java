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
package org.apache.jmeter.save;


import java.io.*;
import java.util.*;


/**
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Revision$
 */
public class XMLWriter {

    private Writer writer;
    private boolean indent = true;
    private String encoding = "UTF-8";
    private Stack openTags = new Stack();
    private boolean startTagClosed = true;
    private String indentSpaces;
    private int indentLevel = -1;

    public XMLWriter(Writer writer)
    {
        this.writer = writer;
    }

    public XMLWriter(Writer writer, boolean indent, int indentSpaces)
    {
        this.writer = writer;
        setIndent(indent);
        setIndentSpaces(indentSpaces);
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public void setIndent(boolean indent)
    {
        this.indent = indent;
    }

    public void setIndentSpaces(int indentSpaces)
    {
        StringBuffer buffer = new StringBuffer(indentSpaces);

        for (int i = 0; i < indentSpaces; i++) {
            buffer.append(' ');
        }
        this.indentSpaces = buffer.toString();
    }

    public void declaration() throws IOException
    {
        writer.write("<?xml version=\"1.0\" encoding=\"");
        writer.write(encoding);
        writer.write("\"?>\n\n");
    }

    public void startTag(String tag) throws IOException
    {
        if (! startTagClosed) {
            writer.write(">\n");
        }
        indentLevel++;
        indent();
        writer.write("<");
        writer.write(tag);
        openTags.push(tag);
        startTagClosed = false;
    }

    public void attribute(String name, String value) throws IOException
    {
        if (startTagClosed) {
            throw new IllegalStateException("No open start tag");
        }
        writer.write(' ');
        writer.write(name);
        writer.write("=\"");
        writer.write(XMLUtils.escapeAttributeValue(value));
        writer.write('"');
    }

    public void body(String value) throws IOException
    {
        if (openTags.isEmpty()) {
            throw new IllegalStateException("No open tag");
        }
        if (! startTagClosed) {
            writer.write('>');
        }
        writer.write(XMLUtils.escapeBodyValue(value));
    }

    public void end() throws IOException
    {
        String tag = (String)openTags.pop();

        if (startTagClosed) {
            indent();
            writer.write("</");
            writer.write(tag);
            writer.write(">\n");
        } else {
            writer.write("/>\n");
        }
        indentLevel--;
        startTagClosed = true;
    }

    public void close() throws IOException
    {
        while(! openTags.isEmpty()) {
            end();
        }
        writer.flush();
    }

    private void indent() throws IOException
    {
        if (indent) {
            for (int i = 0; i < indentLevel; i++) {
                writer.write(indentSpaces);
            }
        }
    }
}
