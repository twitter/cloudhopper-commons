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
 * Tests URL class
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class URLTest {
    private static final Logger logger = LoggerFactory.getLogger(URLTest.class);

    @Test
    public void testToString() throws Exception {
        URL url = new URL();
        
        url.setProtocol("p");
        Assert.assertEquals("p://", url.toString());

        url.setHost("h");
        Assert.assertEquals("p://h", url.toString());

        url.setPort(80);
        Assert.assertEquals("p://h:80", url.toString());

        url.setPath("/");
        Assert.assertEquals("p://h:80/", url.toString());

        url.setUsername("u");
        Assert.assertEquals("p://u@h:80/", url.toString());

        url.setPassword("p");
        Assert.assertEquals("p://u:p@h:80/", url.toString());

        url.setPort(null);
        Assert.assertEquals("p://u:p@h/", url.toString());

        url.setUsername(null);
        Assert.assertEquals("p://h/", url.toString());

        url.setPath(null);
        Assert.assertEquals("p://h", url.toString());
    }

    @Test
    public void testEquals() throws Exception {
        URL url0 = URLParser.parse("http://www.google.com/");
        URL url1 = URLParser.parse("http://www.google.com/");

        Assert.assertEquals(url0, url1);
    }
    
}
