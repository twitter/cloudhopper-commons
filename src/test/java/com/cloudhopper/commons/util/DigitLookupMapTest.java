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

// my imports

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DigitLookupMapTest {
    private static final Logger logger = LoggerFactory.getLogger(DigitLookupMapTest.class);

    @Test
    public void usage() {
        DigitLookupMap<String> map0 = new DigitLookupMap<String>();

        try {
            // try a null key
            map0.put(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // correct behavior
        }

        try {
            // try an invalid key
            map0.put("a", "a");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }

        try {
            // try an invalid wildcard key
            map0.put("*1", "*1");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }

        Assert.assertEquals(0, map0.size());
        
        map0.put("1*", "1*");
        Assert.assertEquals(1, map0.size());

        map0.put("234*", "234*");
        Assert.assertEquals(2, map0.size());

        map0.put("234", "234");
        Assert.assertEquals(3, map0.size());

        map0.put("234555", "234555");
        Assert.assertEquals(4, map0.size());

        Assert.assertEquals(null, map0.get("3"));
        Assert.assertEquals(null, map0.get("34"));
        Assert.assertEquals("1*", map0.get("13"));
        Assert.assertEquals("234*", map0.get("2345"));
        Assert.assertEquals("234", map0.get("234"));
        Assert.assertEquals("234555", map0.get("234555"));
        Assert.assertEquals("234*", map0.get("2345555"));

        String pv = null;

        // test the replacement of a key with a new value
        pv = map0.put("1*", "new*");
        Assert.assertEquals("1*", pv);

        // size should not have changed (key/value mappings stays the same)
        Assert.assertEquals(4, map0.size());

        map0.put("*", "*");
        Assert.assertEquals("*", map0.get("3"));
        Assert.assertEquals("*", map0.get("34"));
        Assert.assertEquals(5, map0.size());

        // remove a node
        map0.put("*", null);
        Assert.assertEquals(null, map0.get("3"));
        Assert.assertEquals(null, map0.get("34"));
        Assert.assertEquals(4, map0.size());

        // set to null again, but shouldn't do anything bad
        map0.put("*", null);
        Assert.assertEquals(null, map0.get("3"));
        Assert.assertEquals(null, map0.get("34"));
        Assert.assertEquals(4, map0.size());
    }

}
