package com.cloudhopper.commons.charset;

/*
 * #%L
 * ch-commons-charset
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
public class GSMCharsetTest {
    private static final Logger logger = LoggerFactory.getLogger(GSMCharsetTest.class);

    @Test
    public void canRepresent() throws Exception {
        // nulls are always ok
        Assert.assertEquals(true, GSMCharset.canRepresent(null));
        Assert.assertEquals(true, GSMCharset.canRepresent(" "));
        Assert.assertEquals(true, GSMCharset.canRepresent("\n\r"));
        Assert.assertEquals(true, GSMCharset.canRepresent("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        Assert.assertEquals(true, GSMCharset.canRepresent("Hello @ World"));
        Assert.assertEquals(true, GSMCharset.canRepresent("{}[]$"));
        // euro currency symbol is good
        Assert.assertEquals(true, GSMCharset.canRepresent("\u20ac"));
        // arabic char is not valid GSM char
        Assert.assertEquals(false, GSMCharset.canRepresent("\u0623"));
        // bug found with A-z if statement in previous charset
        // 1 char in-between the upper-case and lower-case snuck in the
        // simple range check -- the '`' char is NOT in the GSM charset
        Assert.assertEquals(false, GSMCharset.canRepresent("`"));
        Assert.assertEquals(true, GSMCharset.canRepresent("[\\]^_"));
        // form feed: uncommon but technically part of the spec
        Assert.assertEquals(true, GSMCharset.canRepresent("\f"));
        
        // create a fully correct string from lookup tables
        // strings to decode/encode to/from UTF-8
        // build a string of every GSM char
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < GSMCharset.CHAR_TABLE.length; i++) {
            char c = GSMCharset.CHAR_TABLE[i];
            if (c > 0) {
                s.append(c);
            }
        }
        for (int i = 0; i < GSMCharset.EXT_CHAR_TABLE.length; i++) {
            char c = GSMCharset.EXT_CHAR_TABLE[i];
            if (c > 0) {
                s.append(c);
            }
        }
        
        Assert.assertEquals(true, GSMCharset.canRepresent(s.toString()));
    }
}
