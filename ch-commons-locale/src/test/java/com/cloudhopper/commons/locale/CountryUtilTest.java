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
public class CountryUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(CountryUtilTest.class);


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

    @Test
    public void countryByName() throws Exception {
        List<Country> countries = CountryUtil.getCountriesByName();
        // first couple element check, name isn't in Country's comparison
        /**
         * AF AFG 004 Afghanistan
AL ALB 008 Albania, People's Socialist Republic of
DZ DZA 012 Algeria, People's Democratic Republic of
AS ASM 016 American Samoa
AD AND 020 Andorra, Principality of
         */
        Assert.assertEquals(countries.get(0), new Country("AF", "", ""));
        Assert.assertEquals(countries.get(1), new Country("AL", "", ""));
        Assert.assertEquals(countries.get(2), new Country("DZ", "", ""));
        Assert.assertEquals(countries.get(3), new Country("AS", "", ""));
        Assert.assertEquals(countries.get(4), new Country("AD", "", ""));

        Country c0 = countries.get(4);
        Assert.assertEquals("Andorra", c0.getName());
        Assert.assertEquals("Andorra, Principality of", c0.getLongName());
    }

    @Test
    public void lookupByIso2() throws Exception {
        Country result0 = CountryUtil.lookupByIso2("US");
        Assert.assertEquals(result0.getCode(), "US");
        result0 = CountryUtil.lookupByIso2("GB");
        Assert.assertEquals(result0.getCode(), "GB");
    }

    
}
