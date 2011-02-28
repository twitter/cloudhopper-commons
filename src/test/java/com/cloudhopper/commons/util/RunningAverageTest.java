/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.cloudhopper.commons.util;

// third party imports
import org.junit.*;
import org.apache.log4j.Logger;

// my imports
//import net.cloudhopper.commons.util.ByteBuffer;

/**
 *
 * @author joelauer
 */
public class RunningAverageTest {

    private static final Logger logger = Logger.getLogger(RunningAverageTest.class);

    @Test
    public void runningAverage() {
        RunningAverage avg = new RunningAverage(3, 1);
        Assert.assertEquals("0.0", avg.toString());
        avg.add(2);
        Assert.assertEquals("2.0", avg.toString());
        avg.add(3);
        Assert.assertEquals("2.5", avg.toString());
        avg.add(4);
        Assert.assertEquals("3.0", avg.toString());
        avg.add(5);
        // last value should have kicked out the 2 above
        Assert.assertEquals("4.0", avg.toString());
        Assert.assertEquals("4.000", avg.toString(3));
        Assert.assertEquals("4", avg.toString(0));
    }
}
