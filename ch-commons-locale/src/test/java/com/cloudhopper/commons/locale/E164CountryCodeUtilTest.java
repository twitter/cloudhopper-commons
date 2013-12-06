package com.cloudhopper.commons.locale;

/*
 * #%L
 * ch-commons-locale
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
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

import com.cloudhopper.commons.locale.Country;
import com.cloudhopper.commons.locale.CountryUtil;
import java.io.IOException;
import java.util.List;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class E164CountryCodeUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(E164CountryCodeUtilTest.class);

/**
    @Test(expected=IOException.class)
    public void parse() throws Exception {
        Country c = Country.parse("");
    }

    @Test(expected=IOException.class)
    public void parse2() throws Exception {
        Country c = Country.parse("dffsdfasdf");
    }

    @Test(expected=IOException.class)
    public void parse3() throws Exception {
        Country c = Country.parse("AA AAA 123");
    }

    @Test(expected=IOException.class)
    public void parse4() throws Exception {
        Country c = Country.parse("AA AAA 123 ");
    }

    @Test(expected=IOException.class)
    public void parse5() throws Exception {
        Country c = Country.parse(null);
    }

    @Test
    public void parseOK() throws Exception {
        Country c = Country.parse("AA AAA 123 J");
    }
 */

    @Test
    public void lookup() throws Exception {
        E164CountryCode result0 = E164CountryCodeUtil.lookup("1");
        Assert.assertEquals("US", result0.getIso());
        result0 = E164CountryCodeUtil.lookup("1313");
        Assert.assertEquals("US", result0.getIso());
        result0 = E164CountryCodeUtil.lookup("");
        Assert.assertEquals(null, result0);
        result0 = E164CountryCodeUtil.lookup("1416");
        Assert.assertEquals("CA", result0.getIso());
        result0 = E164CountryCodeUtil.lookup("18765551212");
        Assert.assertEquals("JM", result0.getIso());
        result0 = E164CountryCodeUtil.lookup("448765551212");
        Assert.assertEquals("GB", result0.getIso());
    }

    
}
