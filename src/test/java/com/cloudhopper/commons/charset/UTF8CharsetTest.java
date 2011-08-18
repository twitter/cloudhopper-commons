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

package com.cloudhopper.commons.charset;

// third party imports
import org.junit.*;
import org.apache.log4j.Logger;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class UTF8CharsetTest {
    private static final Logger logger = Logger.getLogger(UTF8CharsetTest.class);

    @Test
    public void calculateByteLength() throws Exception {
        String sample = null;
        // test the incredibly fast method for calculating a Java strings UTF-8 byte length
        Assert.assertEquals(0, UTF8Charset.calculateByteLength(null));
        Assert.assertEquals(0, UTF8Charset.calculateByteLength(""));
        Assert.assertEquals(1, UTF8Charset.calculateByteLength("a"));
        Assert.assertEquals(2, UTF8Charset.calculateByteLength("\n\r"));
        sample = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Assert.assertEquals(sample.getBytes("UTF8").length, UTF8Charset.calculateByteLength(sample));
        sample = "\u20ac";
        Assert.assertEquals(sample.getBytes("UTF8").length, UTF8Charset.calculateByteLength(sample));
        sample = "\u20ac\u0623";
        Assert.assertEquals(sample.getBytes("UTF8").length, UTF8Charset.calculateByteLength(sample));
        sample = "\u00A7\u00E5\uFFFF";
        Assert.assertEquals(sample.getBytes("UTF8").length, UTF8Charset.calculateByteLength(sample));
    }
}
