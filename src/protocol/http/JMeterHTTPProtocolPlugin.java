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


import org.apache.jmeter.plugin.JMeterPlugin;
import org.apache.jmeter.protocol.http.control.gui.*;
import org.apache.jmeter.protocol.http.control.*;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFull;
import org.apache.jmeter.protocol.http.sampler.SoapSampler;
import org.apache.jmeter.protocol.http.gui.*;
import org.apache.jmeter.protocol.http.config.gui.HttpDefaultsGui;
import org.apache.jmeter.protocol.http.config.HTTPDefaults;
import org.apache.jmeter.protocol.http.modifier.AnchorModifier;
import org.apache.jmeter.protocol.http.modifier.URLRewritingModifier;
import org.apache.jmeter.protocol.http.modifier.gui.URLRewritingModifierGui;
import org.apache.jmeter.protocol.http.modifier.gui.AnchorModifierGui;


/**
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Revision$
 */
public class JMeterHTTPProtocolPlugin implements JMeterPlugin
{

    public String[][] getIconMappings()
    {
        return new String[][]{
        };
    }

    public Class[][] getGuiMappings()
    {
        return new Class[][]{
            {SoapSampler.class, SoapSamplerGui.class},
            {HTTPSamplerFull.class, HttpTestSampleGui.class},
            {HTTPDefaults.class, HttpDefaultsGui.class},
            {AuthManager.class, AuthPanel.class},
            {CookieManager.class, CookiePanel.class},
            {HeaderManager.class, HeaderPanel.class},
            {RecordingController.class, RecordController.class},
            {AnchorModifier.class, AnchorModifierGui.class},
            {URLRewritingModifier.class, URLRewritingModifierGui.class}
        };
    }

    public Class[] getElementClasses()
    {
        return new Class[]{
            SoapSampler.class,
            HTTPSamplerFull.class,
            HTTPDefaults.class,
            AuthManager.class,
            CookieManager.class,
            HeaderManager.class,
            RecordingController.class,
            AnchorModifier.class,
            URLRewritingModifier.class
        };
    }


    public Class[] getJavaSamplerClientClasses()
    {
        return new Class[0];
    }


    public String[][] getResourceBundles()
    {
        return new String[0][0];
    }

}
