package org.apache.jmeter.protocol.http.sampler;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.jmeter.protocol.http.control.gui.SoapSamplerGui;


/**
 * Sampler to handle SOAP Requests
 *
 *
 * @author Jordi Salvat i Alabart
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Id$
 */
public class SoapSampler extends HTTPSampler
{

    public static final String XML_DATA = "xmlData";
    public static final String URL = "url";


    private String url = "";
    private String xmlData = "";


    public String getUrl()
    {
        return url;
    }


    public void setUrl(String url)
    {
        this.url = url;
    }


    public URL getRequestUrl() throws MalformedURLException
    {
        return new URL(getUrl());
    }


    public String getXmlData()
    {
        return xmlData;
    }


    public void setXmlData(String xmlData)
    {
        this.xmlData = xmlData;
    }


    /****************************************
     * Set the HTTP request headers in preparation to open the connection
     * and sending the METHOD_POST data:
     *
     *@param connection       <code>URLConnection</code> to set headers on
     *@exception IOException  if an I/O exception occurs
     ***************************************/
    public void setHeaders(URLConnection connection)
        throws IOException
    {
        ((HttpURLConnection)connection).setRequestMethod("METHOD_POST");
        connection.setRequestProperty("Content-length", "" + getXmlData().length());
        connection.setRequestProperty("Content-type", "text/xml");
        connection.setDoOutput(true);
    }

    /****************************************
     * Send METHOD_POST data from <code>Entry</code> to the open connection.
     *
     *@param connection       <code>URLConnection</code> of where METHOD_POST data should
     *      be sent
     *@exception IOException  if an I/O exception occurs
     ***************************************/
    public void sendPostData(URLConnection connection)
        throws IOException
    {
        PrintWriter out = new PrintWriter(connection.getOutputStream());
        out.print(getXmlData());
        out.close();
    }
}

