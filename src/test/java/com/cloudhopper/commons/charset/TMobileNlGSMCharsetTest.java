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
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author dhoffman
 */
public class TMobileNlGSMCharsetTest {
    private static final Logger logger = Logger.getLogger(TMobileNlGSMCharsetTest.class);

    @Test
    public void canRepresent() throws Exception {
        // nulls are always ok
        Assert.assertEquals(true, TMobileNlGSMCharset.canRepresent(null));
        Assert.assertEquals(true, TMobileNlGSMCharset.canRepresent(" "));
        Assert.assertEquals(true, TMobileNlGSMCharset.canRepresent("\n\r"));
        Assert.assertEquals(true, TMobileNlGSMCharset.canRepresent("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        Assert.assertEquals(true, TMobileNlGSMCharset.canRepresent("Hello @ World"));
        Assert.assertEquals(true, TMobileNlGSMCharset.canRepresent("$_"));
        // euro currency symbol is good
        Assert.assertEquals(true, TMobileNlGSMCharset.canRepresent("\u20ac"));
        // arabic char is not valid TMobileNlGSM char
        Assert.assertEquals(false, TMobileNlGSMCharset.canRepresent("\u0623"));
        // '`' char is NOT in the TMobileNlGSM charset
        Assert.assertEquals(false, TMobileNlGSMCharset.canRepresent("`"));
        // []{}^~|\ GSM extended table chars are not supported by T-Mo NL
        Assert.assertEquals(false, TMobileNlGSMCharset.canRepresent("{}[\\]^~|"));
        
        // create a fully correct string from lookup table
        // strings to decode/encode to/from UTF-8
        // build a string of every GSM base-table char
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < GSMCharset.CHAR_TABLE.length; i++) {
            char c = GSMCharset.CHAR_TABLE[i];
            if (c > 0) {
                s.append(c);
            }
        }
        s.append("\u20ac");  // and the euro-mark
        
        Assert.assertEquals(true, TMobileNlGSMCharset.canRepresent(s.toString()));
    }

    @Test
    public void testEncodeDecode() throws Exception {
        
        TMobileNlGSMCharset tmo = new TMobileNlGSMCharset();

        // test custom euro encode/decode
        String customEuro = "\u20ac";
        byte[] bytes = tmo.encode(customEuro);
        Assert.assertEquals(1, bytes.length);
        Assert.assertEquals(0x80, bytes[0] & 0x0ff);
        StringBuilder sb = new StringBuilder();
        tmo.decode(bytes, sb);
        Assert.assertEquals(1, sb.length());
        Assert.assertEquals(customEuro, sb.toString());

        // validate custom euro hex-encodes & decodes correctly also
        String hexEncoded = new String(Hex.encodeHex(bytes));
        Assert.assertEquals("80", hexEncoded);
        byte[] bytes2 = Hex.decodeHex(hexEncoded.toCharArray());
        sb = new StringBuilder();
        tmo.decode(bytes2, sb);
        Assert.assertEquals(1, sb.length());
        Assert.assertEquals(customEuro, sb.toString());

        // test invalid GSM chars; all should encode to '?'
        String invalidChars = "[]{}|^\\~";
        bytes = tmo.encode(invalidChars);
        for (byte b : bytes) 
            Assert.assertEquals(0x3f, b);

        // encode/decode test
        StringBuffer toEncode = new StringBuffer();
        for (int i=0; i < GSMCharset.CHAR_TABLE.length; i++) {
            if (i != GSMCharset.EXTENDED_ESCAPE)
                toEncode.append(GSMCharset.CHAR_TABLE[i]);
        }
        toEncode.append(customEuro);
        bytes = tmo.encode(toEncode.toString());
        sb = new StringBuilder();
        tmo.decode(bytes, sb);
        Assert.assertEquals(toEncode.toString(), sb.toString());

        // arabic char is not valid GSM char
        String otherUnicode = "\u0623";
        bytes = tmo.encode(otherUnicode);
        Assert.assertEquals(1, bytes.length);
        Assert.assertEquals(0x3f, bytes[0]);
    }
}
