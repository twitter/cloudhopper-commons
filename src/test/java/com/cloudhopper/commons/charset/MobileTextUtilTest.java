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
public class MobileTextUtilTest {
    private static final Logger logger = Logger.getLogger(MobileTextUtilTest.class);

    @Test
    public void replaceSafeUnicodeChars() throws Exception {
        String source = null;
        StringBuilder buffer = null;
        int replaced = -1;

        source = "hello";
        buffer = new StringBuilder(source);
        replaced = MobileTextUtil.replaceSafeUnicodeChars(buffer);
        Assert.assertEquals(0, replaced);
        Assert.assertEquals(source, buffer.toString());


        source = "\u201chello\u201d \u201cworld\u201d \u201cthis\u201d";
        buffer = new StringBuilder(source);
        replaced = MobileTextUtil.replaceSafeUnicodeChars(buffer);
        Assert.assertEquals(6, replaced);
        Assert.assertEquals("\"hello\" \"world\" \"this\"", buffer.toString());


        source = "\u201chello\u201d \u201cworld\u201d \u201cthis\u201d";
        buffer = new StringBuilder(source);
        replaced = MobileTextUtil.replaceSafeUnicodeChars(buffer);
        Assert.assertEquals(6, replaced);
        Assert.assertEquals("\"hello\" \"world\" \"this\"", buffer.toString());


        source = "\u2018hello\u2019 \u2018world\u2019 don\u2019t";
        buffer = new StringBuilder(source);
        replaced = MobileTextUtil.replaceSafeUnicodeChars(buffer);
        Assert.assertEquals(5, replaced);
        Assert.assertEquals("\'hello\' \'world\' don\'t", buffer.toString());
    }

    @Test
    public void replaceAccentedChars() throws Exception {
        String source = null;
        StringBuilder buffer = null;
        int replaced = -1;

        source = "hello";
        buffer = new StringBuilder(source);
        replaced = MobileTextUtil.replaceAccentedChars(buffer);
        Assert.assertEquals(0, replaced);
        Assert.assertEquals(source, buffer.toString());

        source = "h\u00E9llo";
        buffer = new StringBuilder(source);
        replaced = MobileTextUtil.replaceAccentedChars(buffer);
        Assert.assertEquals(1, replaced);
        Assert.assertEquals("hello", buffer.toString());

        source = "\u00E8\u00E9\u00EA\u00EB\u00EF\u00F1\u00F2";
        buffer = new StringBuilder(source);
        replaced = MobileTextUtil.replaceAccentedChars(buffer);
        Assert.assertEquals(7, replaced);
        Assert.assertEquals("eeeeino", buffer.toString());

        source = "\u20AC";
        buffer = new StringBuilder(source);
        replaced = MobileTextUtil.replaceAccentedChars(buffer);
        Assert.assertEquals(0, replaced);
        Assert.assertEquals("\u20AC", buffer.toString());
    }
}
