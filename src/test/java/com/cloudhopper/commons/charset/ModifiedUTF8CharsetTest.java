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
import com.cloudhopper.commons.util.HexUtil;
import org.junit.*;
import org.apache.log4j.Logger;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class ModifiedUTF8CharsetTest {
    private static final Logger logger = Logger.getLogger(ModifiedUTF8CharsetTest.class);

    String nullString = "\u0000";
    String controlCharsString = createStringWithCharRange('\u0001', 0x20);
    String asciiOnlyString = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    String iso88591CharsString = createStringWithCharRange('\u0080', 128);
    String first7EFFString = createStringWithCharRange('\u0100', 0x7EFF);
    String entireString = createStringWithCharRange('\u0000', 0x7FFF);
    String upperRangeString = createStringWithCharRange('\u7FFF', 0x8000);
    
    static public String createStringWithCharRange(char start, int length) {
        StringBuilder buf = new StringBuilder(length);
        int end = start+length;
        for (int i = start; i < end; i++) {
            buf.append((char)i);
        }
        return buf.toString();
    }
    
    @Test
    public void compareAgainstJVM() throws Exception {
        byte[] expected = null;
        byte[] actual = null;
        String actualString = null;
        
        String[] strings = new String[] {
            nullString, controlCharsString, asciiOnlyString, iso88591CharsString, first7EFFString, entireString
        };
        
        int i = 0;
        for (String s : strings) {
            expected = s.getBytes("UTF-8");
            actual = CharsetUtil.CHARSET_MODIFIED_UTF8.encode(s);
            //logger.info("  string: " + s);
            //logger.info("expected: " + HexUtil.toHexString(expected));
            //logger.info("  actual: " + HexUtil.toHexString(actual));
            // verify our length calculator is correct
            Assert.assertEquals(expected.length, ModifiedUTF8Charset.calculateByteLength(s));
            Assert.assertArrayEquals("string: " + s, expected, actual);
            // try to decode the byte array and make sure it matches the expected string
            actualString = CharsetUtil.CHARSET_MODIFIED_UTF8.decode(expected);
            Assert.assertEquals(s, actualString);
            // verify a decode to a stringbuffer works as expected
            StringBuilder actualStringBuffer = new StringBuilder();
            CharsetUtil.decode(expected, actualStringBuffer, CharsetUtil.CHARSET_MODIFIED_UTF8);
            Assert.assertEquals(s, actualStringBuffer.toString());
            i++;
        }
        
        // upper range of java values are where modified UTF-8 falls on its face
        // its still safe to use as long as modified UTF-8 bytes are used to decode
        // the values as well -- verify the entire range decodes back to the same values
        byte[] encoded = CharsetUtil.CHARSET_MODIFIED_UTF8.encode(upperRangeString);
        String decoded = CharsetUtil.decode(encoded, CharsetUtil.CHARSET_MODIFIED_UTF8);
        Assert.assertEquals(upperRangeString, decoded);
    }
}
