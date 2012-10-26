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
import java.net.MalformedURLException;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests URLParser class.
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class URLParserTest {
    private static final Logger logger = LoggerFactory.getLogger(URLParserTest.class);

    @Test(expected=MalformedURLException.class)
    public void parseNoProtocol() throws Exception {
        URL url0 = URLParser.parse("/");
    }

    @Test
    public void parse0() throws Exception {
        URL url0 = URLParser.parse("http://www.google.com/");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertEquals(null, url0.getUsername());
        Assert.assertEquals(null, url0.getPassword());
        Assert.assertEquals("www.google.com", url0.getHost());
        Assert.assertEquals(null, url0.getPort());
        Assert.assertEquals("/", url0.getPath());
    }

    @Test
    public void parse1() throws Exception {
        URL url0 = URLParser.parse("file:///");
        Assert.assertEquals("file", url0.getProtocol());
        Assert.assertEquals(null, url0.getUsername());
        Assert.assertEquals(null, url0.getPassword());
        Assert.assertEquals(null, url0.getHost());
        Assert.assertEquals(null, url0.getPort());
        Assert.assertEquals("/", url0.getPath());
    }

    @Test
    public void parse2() throws Exception {
        URL url0 = URLParser.parse("http://www.google.com");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertEquals(null, url0.getUsername());
        Assert.assertEquals(null, url0.getPassword());
        Assert.assertEquals("www.google.com", url0.getHost());
        Assert.assertEquals(null, url0.getPort());
        Assert.assertEquals(null, url0.getPath());
    }

    @Test
    public void parse3() throws Exception {
        URL url0 = URLParser.parse("http://www.google.com:80");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertEquals(null, url0.getUsername());
        Assert.assertEquals(null, url0.getPassword());
        Assert.assertEquals("www.google.com", url0.getHost());
        Assert.assertEquals(new Integer(80), url0.getPort());
        Assert.assertEquals(null, url0.getPath());
    }

    @Test
    public void parse4() throws Exception {
        URL url0 = URLParser.parse("http://www.google.com:80/");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertEquals(null, url0.getUsername());
        Assert.assertEquals(null, url0.getPassword());
        Assert.assertEquals("www.google.com", url0.getHost());
        Assert.assertEquals(new Integer(80), url0.getPort());
        Assert.assertEquals("/", url0.getPath());
    }

    @Test(expected=MalformedURLException.class)
    public void parseBadPort() throws Exception {
        URL url0 = URLParser.parse("http://www.google.com:80j");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertEquals(null, url0.getUsername());
        Assert.assertEquals(null, url0.getPassword());
        Assert.assertEquals("www.google.com", url0.getHost());
        Assert.assertEquals(new Integer(80), url0.getPort());
        Assert.assertEquals("/", url0.getPath());
    }

    @Test
    public void parse5() throws Exception {
        URL url0 = URLParser.parse("http://u@h");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertEquals("u", url0.getUsername());
        Assert.assertEquals(null, url0.getPassword());
        Assert.assertEquals("h", url0.getHost());
        Assert.assertEquals(null, url0.getPort());
        Assert.assertEquals(null, url0.getPath());
    }

    @Test
    public void parse6() throws Exception {
        URL url0 = URLParser.parse("http://u:p@h");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertEquals("u", url0.getUsername());
        Assert.assertEquals("p", url0.getPassword());
        Assert.assertEquals("h", url0.getHost());
        Assert.assertEquals(null, url0.getPort());
        Assert.assertEquals(null, url0.getPath());
    }

    @Test
    public void parse7() throws Exception {
        URL url0 = URLParser.parse("http://@");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertEquals(null, url0.getUsername());
        Assert.assertEquals(null, url0.getPassword());
        Assert.assertEquals(null, url0.getHost());
        Assert.assertEquals(null, url0.getPort());
        Assert.assertEquals(null, url0.getPath());
    }

    @Test
    public void parse8() throws Exception {
        URL url0 = URLParser.parse("http://:@");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertEquals(null, url0.getUsername());
        Assert.assertEquals(null, url0.getPassword());
        Assert.assertEquals(null, url0.getHost());
        Assert.assertEquals(null, url0.getPort());
        Assert.assertEquals(null, url0.getPath());
    }

    @Test(expected=MalformedURLException.class)
    public void parsePortSepButNoPort() throws Exception {
        URL url0 = URLParser.parse("http://:@:");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertEquals(null, url0.getUsername());
        Assert.assertEquals(null, url0.getPassword());
        Assert.assertEquals(null, url0.getHost());
        Assert.assertEquals(null, url0.getPort());
        Assert.assertEquals(null, url0.getPath());
    }

    @Test
    public void parseEncodedDifficult() throws Exception {
        URL url0 = URLParser.parse("http://%40:%40@h:80/+/+/+/+?");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertEquals("@", url0.getUsername());
        Assert.assertEquals("@", url0.getPassword());
        Assert.assertEquals("h", url0.getHost());
        Assert.assertEquals(new Integer(80), url0.getPort());
        Assert.assertEquals("/ / / / ", url0.getPath());
    }

    @Test
    public void parseQueryString() throws Exception {
        URL url0 = URLParser.parse("http://www.google.com?");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertNull(url0.getUsername());
        Assert.assertNull(url0.getPassword());
        Assert.assertEquals("www.google.com", url0.getHost());
        Assert.assertNull(url0.getPort());
        Assert.assertNull(url0.getPath());

        url0 = URLParser.parse("http://www.google.com/?");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertNull(url0.getUsername());
        Assert.assertNull(url0.getPassword());
        Assert.assertEquals("www.google.com", url0.getHost());
        Assert.assertNull(url0.getPort());
        Assert.assertEquals("/", url0.getPath());

        url0 = URLParser.parse("http://www.google.com/?x=1");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertNull(url0.getUsername());
        Assert.assertNull(url0.getPassword());
        Assert.assertEquals("www.google.com", url0.getHost());
        Assert.assertNull(url0.getPort());
        Assert.assertEquals("/", url0.getPath());
        Assert.assertEquals("1", url0.getQueryProperty("x"));

        url0 = URLParser.parse("http://www.google.com/?x=1&y=2");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertNull(url0.getUsername());
        Assert.assertNull(url0.getPassword());
        Assert.assertEquals("www.google.com", url0.getHost());
        Assert.assertNull(url0.getPort());
        Assert.assertEquals("/", url0.getPath());
        Assert.assertEquals("1", url0.getQueryProperty("x"));
        Assert.assertEquals("2", url0.getQueryProperty("y"));

        url0 = URLParser.parse("http://www.google.com/?x=%40&y=2");
        Assert.assertEquals("http", url0.getProtocol());
        Assert.assertNull(url0.getUsername());
        Assert.assertNull(url0.getPassword());
        Assert.assertEquals("www.google.com", url0.getHost());
        Assert.assertNull(url0.getPort());
        Assert.assertEquals("/", url0.getPath());
        Assert.assertEquals("@", url0.getQueryProperty("x"));
        Assert.assertEquals("2", url0.getQueryProperty("y"));
    }
    
}
