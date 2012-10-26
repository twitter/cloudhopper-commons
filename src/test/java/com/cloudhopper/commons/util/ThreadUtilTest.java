package com.cloudhopper.commons.util;

/*
 * #%L
 * ch-commons-util
 * %%
 * Copyright (C) 2012 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

// third party imports
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class ThreadUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(ThreadUtilTest.class);

    @Test
    public void getAllThreads() {
        Thread[] allThreads = ThreadUtil.getAllThreads();

        //for (Thread t : allThreads) {
        //    logger.info(t.getId() + ": " + t.getName());
        //}

        // sort of hard to predict how this test may be called, so we'll just
        // check that at least 4 threads were returned
        // 2: Reference Handler
        // 3: Finalizer
        // 4: Signal Dispatcher
        // 1: main
        Assert.assertEquals(true, allThreads.length >= 4);
    }

    @Test
    public void getAllThreadsMatching() {
        // try various regular expressions

        Thread[] allThreads0 = ThreadUtil.getAllThreadsMatching("main");
        Assert.assertEquals(1, allThreads0.length);

        Thread[] allThreads1 = ThreadUtil.getAllThreadsMatching("(?i).*Main.*");
        Assert.assertEquals(1, allThreads1.length);

        // the two previous should match
        Assert.assertEquals(allThreads0[0].getId(), allThreads1[0].getId());

        Thread[] allThreads2 = ThreadUtil.getAllThreadsMatching(".*main.*");
        Assert.assertEquals(1, allThreads2.length);

        // the two previous should match
        Assert.assertEquals(allThreads0[0].getId(), allThreads2[0].getId());
        
        // no matches
        Thread[] allThreads3 = ThreadUtil.getAllThreadsMatching(".*Main.*");
        Assert.assertEquals(0, allThreads3.length);
    }
}
