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
//import net.cloudhopper.commons.util.ByteBuffer;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class EncryptUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(EncryptUtilTest.class);

    // 8 chars is all it seems to take
    private static final String KEY = "$%!*?t12";
    private static final String SAMPLE1 = "test";
    private static final String SAMPLE2 = "hello world";
    private static final String SAMPLE3 = "this is going to be a super long string that has spaces, etc. and ! I'm really happy that I'll be testing the MD5 hash to its fullest!!!!";
    private static final String SAMPLE1_ENC = "K5Ygd3D1N+8=";
    private static final String SAMPLE2_ENC = "TtJn7HMA9ww4xeJC7HCZhQ==";
    private static final String SAMPLE3_ENC = "weXM56gYEglEtqRI/oOnhtawCRZG00aEfGxTgjysBZ08SZTRXhwhA7LrCklQ3WXGFi0oYlz0ACI7xneN4/P35JKCxMj+MNJS4spazdWFuIrryRqaUk/87KY39ve56nxqN31jC058guUPk4BglDpLIq9uyywv9vAlKnuXCMmDMjuyjBAuPqEoJYTzP7ObGhAJ";


    @Test
    public void encrypt() throws Exception {
        String string0 = EncryptUtil.encrypt(KEY, SAMPLE1);
        Assert.assertEquals(SAMPLE1_ENC, string0);
        //logger.debug(string0);
        string0 = EncryptUtil.encrypt(KEY, SAMPLE2);
        Assert.assertEquals(SAMPLE2_ENC, string0);
        //logger.debug(string0);
        string0 = EncryptUtil.encrypt(KEY, SAMPLE3);
        Assert.assertEquals(SAMPLE3_ENC, string0);
        //logger.debug(string0);
    }

    @Test
    public void decrypt() throws Exception {
        String string0 = EncryptUtil.decrypt(KEY, SAMPLE1_ENC);
        Assert.assertEquals(SAMPLE1, string0);
        string0 = EncryptUtil.decrypt(KEY, SAMPLE2_ENC);
        Assert.assertEquals(SAMPLE2, string0);
        string0 = EncryptUtil.decrypt(KEY, SAMPLE3_ENC);
        Assert.assertEquals(SAMPLE3, string0);
    }

    
}
