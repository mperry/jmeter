/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights 
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
 
package org.apache.jmeter.threads;

import java.io.Serializable;
import java.util.*;

import org.apache.jmeter.control.Controller;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.samplers.RemoteSampleListener;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.PerThreadClonable;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.assertions.Assertion;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;

/**
 * ThreadGroup
 *
 * @author Michael Stover
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @version $Id$
 */
public class ThreadGroup
    extends AbstractTestElement
    implements SampleListener, Serializable, Controller, PerThreadClonable
{
    private static Logger log =
        Hierarchy.getDefaultHierarchy().getLoggerFor("jmeter.elements");

    public final static String NUMBER_OF_THREADS = "numberOfThreads";
    public final static String RAMP_UP_PERIOD = "rampUpPeriod";
    public final static String LOOP_COUNT = "loopCount";
    public final static String LOOP_FOREVER = "loopForever";
    public final static String CONTROLLER = "controller";

    private final int DEFAULT_NUM_THREADS = 1;
    private final int DEFAULT_RAMP_UP = 0;
    private SampleQueue queue = null;
    private int threadsStarted = 0;
    private LinkedList listeners = new LinkedList();
    private LinkedList remoteListeners = new LinkedList();

    private boolean loopForever = true;
    private int loopCount = 1;
    private int numberOfThreads = 1;
    private int rampUpPeriod = 1;
    private LoopController controller;

    /**
     * No-arg constructor.
     */
    public ThreadGroup()
    {
    }



    public boolean isLoopForever()
    {
        return loopForever;
    }


    public void setLoopForever(boolean loopForever)
    {
        this.loopForever = loopForever;
    }


    public int getLoopCount()
    {
        return loopCount;
    }


    public void setLoopCount(int loopCount)
    {
        this.loopCount = loopCount;
    }


    public int getNumberOfThreads()
    {
        return numberOfThreads;
    }


    public void setNumberOfThreads(int numberOfThreads)
    {
        this.numberOfThreads = numberOfThreads;
    }


    public int getRampUpPeriod()
    {
        return rampUpPeriod;
    }


    public void setRampUpPeriod(int rampUpPeriod)
    {
        this.rampUpPeriod = rampUpPeriod;
    }


    public LoopController getController()
    {
        return null;
    }

    // required for legacy load
    public void setController(LoopController controller)
    {
        setLoopCount(controller.getLoopCount());
        setLoopForever(controller.getLoopForever());
    }


    /**
     * Set the nuber of threads.
     *
     * @param numThreads the number of threads.
     */
    public void setNumThreads(int numThreads)
    {
        setProperty(NUMBER_OF_THREADS, new Integer(numThreads));
    }

    public boolean isDone()
    {
        return getSamplerController().isDone();
    }

    public boolean hasNext()
    {
        return getSamplerController().hasNext();
    }

    public Sampler next()
    {
        return getSamplerController().next();
    }

    /**
     * Set the ramp-up value.
     *
     * @param rampUp the ramp-up value.
     */
    public void setRampUp(int rampUp)
    {
        setProperty(RAMP_UP_PERIOD, new Integer(rampUp));
    }

    public boolean isNextFirst()
    {
        return getSamplerController().isNextFirst();
    }

    /**
     * Get the ramp-up value.
     *
     * @return the ramp-up value.
     */
    public int getRampUp()
    {
        return getPropertyAsInt(ThreadGroup.RAMP_UP_PERIOD);
    }

    /**
     * Get the sampler controller.
     *
     * @return the sampler controller.
     */
    public Controller getSamplerController()
    {
        return (Controller) getProperty(CONTROLLER);
    }

    /**
     * Set the sampler controller.
     *
     * @param c the sampler controller.
     */
    public void setSamplerController(LoopController c)
    {
        // todo: uncomment
//        c.setLoopForever(false);
        setProperty(CONTROLLER, c);
    }

    /**
     * Get the number of threads.
     *
     * @return the number of threads.
     */
    public int getNumThreads()
    {
        return this.getPropertyAsInt(ThreadGroup.NUMBER_OF_THREADS);
    }

    /**
     * Get the default number of threads.
     *
     * @return the default number of threads.
     */
    public int getDefaultNumThreads()
    {
        return this.DEFAULT_NUM_THREADS;
    }

    /**
     * Get the default ramp-up value.
     *
     * @return the default ramp-up value (in seconds).
     */
    public int getDefaultRampUp()
    {
        return this.DEFAULT_RAMP_UP;
    }

    /**
     * Add a test element.
     *
     * @param child the test element to add.
     */
//    public void addTestElement(TestElement child)
//    {
//        getSamplerController().addTestElement(child);
//    }

    /**
     * A sample has occurred.
     *
     *@param e the sample event.
     */
    public void sampleOccurred(SampleEvent e)
    {
        if (queue == null)
        {
            queue = new SampleQueue();
            Thread thread = new Thread(queue);
            //thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        }
        queue.sampleOccurred(e);
    }

    /**
     * A sample has started.
     *
     *@param e the sample event.
     */
    public void sampleStarted(SampleEvent e)
    {
    }

    /**
     * A sample has stopped.
     *
     * @param e the sample event
     */
    public void sampleStopped(SampleEvent e)
    {
    }

    /**
     * Separate thread to deliver all SampleEvents. This ensures that sample
     * listeners will get sample events one at a time and can thus ignore thread
     * issues.
     *
     * @author Mike Stover
     * @version $Id$
     */
    private class SampleQueue implements Runnable, Serializable
    {
        List occurredQ = Collections.synchronizedList(new LinkedList());

        /**
         * No-arg constructor.
         */
        public SampleQueue()
        {
        }

        /**
         * A sample occurred.
         * 
         * @param e the sample event.
         */
        public synchronized void sampleOccurred(SampleEvent e)
        {
            occurredQ.add(e);
            this.notify();
        }

        /**
         * Run the thread.
         * 
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            SampleEvent event = null;
            while (true)
            {
                try
                {
                    event = (SampleEvent) occurredQ.remove(0);
                }
                catch (Exception ex)
                {
                    waitForSamples();
                    continue;
                }
                try
                {
                    if (event != null)
                    {
                        Iterator iter = listeners.iterator();
                        while (iter.hasNext())
                        {
                            ((SampleListener) iter.next()).sampleOccurred(
                                event);
                        }
                        iter = remoteListeners.iterator();
                        while (iter.hasNext())
                        {
                            try
                            {
                                (
                                    (RemoteSampleListener) iter
                                        .next())
                                        .sampleOccurred(
                                    event);
                            }
                            catch (Exception ex)
                            {
                                log.error("", ex);
                            }
                        }
                    }
                    else
                    {
                        waitForSamples();
                    }
                }
                catch (Throwable ex)
                {
                    log.error("", ex);
                }

            }
        }

        private synchronized void waitForSamples()
        {
            try
            {
                this.wait();
            }
            catch (Exception ex)
            {
                log.error("", ex);
            }
        }
    }

    public Set getValidSubelementTypes()
    {
        Set answer = super.getValidSubelementTypes();

        answer.add(Controller.class);
        answer.add(Sampler.class);
        answer.add(org.apache.jmeter.timers.Timer.class);
        answer.add(ConfigTestElement.class);
        answer.add(Assertion.class);

        return answer;
    }

}
