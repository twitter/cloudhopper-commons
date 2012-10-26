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
public class StringLookupMapTest {
    private static final Logger logger = LoggerFactory.getLogger(StringLookupMapTest.class);

    @Test
    public void usage() {
        StringLookupMap<String> map0 = new StringLookupMap<String>();

        try {
            // try a null key
            map0.put(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
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

        // only wildcards
        map0.put("1*", "1*");
        Assert.assertEquals(1, map0.size());
        Assert.assertEquals(null, map0.get("3"));
        Assert.assertEquals(null, map0.get("1"));
        Assert.assertEquals("1*", map0.get("13"));
        Assert.assertEquals("1*", map0.get("134"));

        map0.put("*", "*");
        Assert.assertEquals(2, map0.size());
        Assert.assertEquals("*", map0.get("3"));
        Assert.assertEquals("*", map0.get("1"));
        Assert.assertEquals("1*", map0.get("13"));
        Assert.assertEquals("1*", map0.get("134"));

        // only specific
        map0 = new StringLookupMap<String>();

        map0.put("1", "1");
        Assert.assertEquals(1, map0.size());
        Assert.assertEquals(null, map0.get("3"));
        Assert.assertEquals("1", map0.get("1"));
        Assert.assertEquals(null, map0.get("13"));

        map0.put("test", "test");
        Assert.assertEquals(2, map0.size());
        Assert.assertEquals(null, map0.get("3"));
        Assert.assertEquals("1", map0.get("1"));
        Assert.assertEquals("test", map0.get("test"));

        // only specific (case insensitive)
        map0 = new StringLookupMap<String>(false);

        map0.put("Test", "test");
        Assert.assertEquals(1, map0.size());
        Assert.assertEquals(null, map0.get("3"));
        Assert.assertEquals("test", map0.get("test"));
        Assert.assertEquals("test", map0.get("Test"));
        Assert.assertEquals("test", map0.get("TEST"));

        // wildcard and specific (case insensitive)
        map0 = new StringLookupMap<String>(false);

        map0.put("Test", "test");
        map0.put("@*", "@*");
        Assert.assertEquals(2, map0.size());
        Assert.assertEquals(null, map0.get("3"));
        Assert.assertEquals("test", map0.get("test"));
        Assert.assertEquals("test", map0.get("Test"));
        Assert.assertEquals("test", map0.get("TEST"));
        Assert.assertEquals("@*", map0.get("@j"));
        Assert.assertEquals("@*", map0.get("@J"));
        
        map0.put("*", "*");
        Assert.assertEquals(3, map0.size());
        // better match
        Assert.assertEquals("@*", map0.get("@J"));
        Assert.assertEquals("*", map0.get("J"));
    }

}
