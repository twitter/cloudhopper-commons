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
import com.cloudhopper.commons.util.HexUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class CharsetUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(CharsetUtilTest.class);

    @Test
    public void encode() throws Exception {
        // euro currency symbol
        String str0 = "\u20ac";
        byte[] bytes = null;

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("1B65"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_PACKED_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("9B32"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UCS_2);
        Assert.assertArrayEquals(HexUtil.toByteArray("20AC"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-16BE"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UCS_2LE);
        Assert.assertArrayEquals(HexUtil.toByteArray("AC20"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-16LE"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UTF_8);
        Assert.assertArrayEquals(HexUtil.toByteArray("E282AC"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-8"), bytes);

        // latin-1 doesn't contain the euro symbol - replace with '?'
        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_ISO_8859_1);
        Assert.assertArrayEquals(HexUtil.toByteArray("3F"), bytes);
        Assert.assertArrayEquals(str0.getBytes("ISO-8859-1"), bytes);

        // latin-9 does contain the euro symbol
        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_ISO_8859_15);
        Assert.assertArrayEquals(HexUtil.toByteArray("A4"), bytes);
        Assert.assertArrayEquals(str0.getBytes("ISO-8859-15"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_AIRWIDE_IA5);
        Assert.assertArrayEquals(HexUtil.toByteArray("1B65"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_VFD2_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("1B65"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_VFTR_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("1B65"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_TMOBILENL_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("80"), bytes);


        // longer string with @ symbol in-between
        str0 = "Hello @ World";

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_GSM);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("48656C6C6F200020576F726C64"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_PACKED_GSM);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("C8329BFD060140D7B79C4D06"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UCS_2);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("00480065006C006C006F0020004000200057006F0072006C0064"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-16BE"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UCS_2LE);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("480065006C006C006F0020004000200057006F0072006C006400"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-16LE"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UTF_8);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("48656C6C6F204020576F726C64"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-8"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_ISO_8859_1);
        Assert.assertArrayEquals(HexUtil.toByteArray("48656C6C6F204020576F726C64"), bytes);
        Assert.assertArrayEquals(str0.getBytes("ISO-8859-1"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_ISO_8859_15);
        Assert.assertArrayEquals(HexUtil.toByteArray("48656C6C6F204020576F726C64"), bytes);
        Assert.assertArrayEquals(str0.getBytes("ISO-8859-15"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_AIRWIDE_IA5);
        Assert.assertArrayEquals(HexUtil.toByteArray("48656C6C6F200020576F726C64"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_VFD2_GSM);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("48656C6C6F204020576F726C64"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_VFTR_GSM);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("48656C6C6F204020576F726C64"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_TMOBILENL_GSM);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("48656C6C6F200020576F726C64"), bytes);


        // longer string with @ symbol in-between
        str0 = "JoeyBlue";

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_GSM);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("4A6F6579426C7565"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_PACKED_GSM);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("CA77392F64D7CB"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UCS_2);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("004A006F006500790042006C00750065"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-16BE"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UCS_2LE);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("4A006F006500790042006C0075006500"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-16LE"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UTF_8);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("4A6F6579426C7565"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-8"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_ISO_8859_1);
        Assert.assertArrayEquals(HexUtil.toByteArray("4A6F6579426C7565"), bytes);
        Assert.assertArrayEquals(str0.getBytes("ISO-8859-1"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_ISO_8859_15);
        Assert.assertArrayEquals(HexUtil.toByteArray("4A6F6579426C7565"), bytes);
        Assert.assertArrayEquals(str0.getBytes("ISO-8859-15"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_AIRWIDE_IA5);
        Assert.assertArrayEquals(HexUtil.toByteArray("4A6F6579426C7565"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_VFD2_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("4A6F6579426C7565"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_VFTR_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("4A6F6579426C7565"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_TMOBILENL_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("4A6F6579426C7565"), bytes);


        // longer string with @ symbol in-between
        str0 = "{}[]$";

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_GSM);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("1B281B291B3C1B3E02"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_PACKED_GSM);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("1BD426B5E16D7C02"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UCS_2);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("007B007D005B005D0024"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-16BE"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UCS_2LE);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("7B007D005B005D002400"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-16LE"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_UTF_8);
        //logger.debug(HexUtil.toHexString(bytes));
        Assert.assertArrayEquals(HexUtil.toByteArray("7B7D5B5D24"), bytes);
        Assert.assertArrayEquals(str0.getBytes("UTF-8"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_ISO_8859_1);
        Assert.assertArrayEquals(HexUtil.toByteArray("7B7D5B5D24"), bytes);
        Assert.assertArrayEquals(str0.getBytes("ISO-8859-1"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_ISO_8859_15);
        Assert.assertArrayEquals(HexUtil.toByteArray("7B7D5B5D24"), bytes);
        Assert.assertArrayEquals(str0.getBytes("ISO-8859-15"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_AIRWIDE_IA5);
        Assert.assertArrayEquals(HexUtil.toByteArray("1B281B291B3C1B3E02"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_VFD2_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("1B281B291B3C1B3E24"), bytes);

        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_VFTR_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("1B281B291B3C1B3E24"), bytes);

        // {}[] not supported
        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_TMOBILENL_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("3F3F3F3F02"), bytes);

        // chars specifically to vodafone-turkey
        //str0 = "$@£¤¥§ÄÅßñΓΔΘΩ€";
        str0 = "$@\u00a3\u00a4\u00a5\u00a7\u00c4\u00c5\u00df\u00f1\u0393\u0394\u0398\u03a9\u20ac";
        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_VFTR_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("2440A3A4A5A7C4C5DFF1137F19151B65"), bytes);

        // form feed is an escape code in GSM
        str0 = "\f\f";
        bytes = CharsetUtil.encode(str0, CharsetUtil.CHARSET_GSM);
        Assert.assertArrayEquals(HexUtil.toByteArray("1B0A1B0A"), bytes);
    }

    @Test
    public void decode() throws Exception {
        // euro currency symbol
        String str0 = "\u20ac";
        String str1 = null;

        str1 = CharsetUtil.decode(HexUtil.toByteArray("1B65"), CharsetUtil.CHARSET_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("9B32"), CharsetUtil.CHARSET_PACKED_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("20AC"), CharsetUtil.CHARSET_UCS_2);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("E282AC"), CharsetUtil.CHARSET_UTF_8);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("1B65"), CharsetUtil.CHARSET_AIRWIDE_IA5);
        Assert.assertEquals(str0, str1);

        // latin-1 doesn't contain the euro symbol - replace with '?'
        //str1 = CharsetUtil.decode(HexUtil.toByteArray("3F"), CharsetUtil.CHARSET_ISO_8859_1);
        //Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("A4"), CharsetUtil.CHARSET_ISO_8859_15);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("1B65"), CharsetUtil.CHARSET_VFD2_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("1B65"), CharsetUtil.CHARSET_VFTR_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("80"), CharsetUtil.CHARSET_TMOBILENL_GSM);
        Assert.assertEquals(str0, str1);


        // longer string with @ symbol in-between
        str0 = "Hello @ World";

        str1 = CharsetUtil.decode(HexUtil.toByteArray("48656C6C6F200020576F726C64"), CharsetUtil.CHARSET_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("C8329BFD060140D7B79C4D06"), CharsetUtil.CHARSET_PACKED_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("00480065006C006C006F0020004000200057006F0072006C0064"), CharsetUtil.CHARSET_UCS_2);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("48656C6C6F204020576F726C64"), CharsetUtil.CHARSET_UTF_8);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("48656C6C6F204020576F726C64"), CharsetUtil.CHARSET_ISO_8859_1);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("48656C6C6F204020576F726C64"), CharsetUtil.CHARSET_ISO_8859_15);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("48656C6C6F204020576F726C64"), CharsetUtil.CHARSET_AIRWIDE_IA5);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("48656C6C6F204020576F726C64"), CharsetUtil.CHARSET_VFD2_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("48656C6C6F204020576F726C64"), CharsetUtil.CHARSET_VFTR_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("48656C6C6F200020576F726C64"), CharsetUtil.CHARSET_TMOBILENL_GSM);
        Assert.assertEquals(str0, str1);


        // longer string with @ symbol in-between
        str0 = "JoeyBlue";

        str1 = CharsetUtil.decode(HexUtil.toByteArray("4A6F6579426C7565"), CharsetUtil.CHARSET_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("CA77392F64D7CB"), CharsetUtil.CHARSET_PACKED_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("004A006F006500790042006C00750065"), CharsetUtil.CHARSET_UCS_2);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("4A6F6579426C7565"), CharsetUtil.CHARSET_UTF_8);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("4A6F6579426C7565"), CharsetUtil.CHARSET_ISO_8859_1);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("4A6F6579426C7565"), CharsetUtil.CHARSET_ISO_8859_15);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("4A6F6579426C7565"), CharsetUtil.CHARSET_AIRWIDE_IA5);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("4A6F6579426C7565"), CharsetUtil.CHARSET_VFD2_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("4A6F6579426C7565"), CharsetUtil.CHARSET_VFTR_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("4A6F6579426C7565"), CharsetUtil.CHARSET_TMOBILENL_GSM);
        Assert.assertEquals(str0, str1);


        // longer string with @ symbol in-between
        str0 = "{}[]$";

        str1 = CharsetUtil.decode(HexUtil.toByteArray("1B281B291B3C1B3E02"), CharsetUtil.CHARSET_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("1BD426B5E16D7C02"), CharsetUtil.CHARSET_PACKED_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("007B007D005B005D0024"), CharsetUtil.CHARSET_UCS_2);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("7B7D5B5D24"), CharsetUtil.CHARSET_UTF_8);
        Assert.assertEquals(str0, str1);

        // airwide is close to GSM, $ is 0x24 rather than 0x02 though
        str1 = CharsetUtil.decode(HexUtil.toByteArray("1B281B291B3C1B3E24"), CharsetUtil.CHARSET_AIRWIDE_IA5);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("7B7D5B5D24"), CharsetUtil.CHARSET_ISO_8859_1);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("7B7D5B5D24"), CharsetUtil.CHARSET_ISO_8859_15);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("1B281B291B3C1B3E24"), CharsetUtil.CHARSET_VFD2_GSM);
        Assert.assertEquals(str0, str1);

        str1 = CharsetUtil.decode(HexUtil.toByteArray("1B281B291B3C1B3E24"), CharsetUtil.CHARSET_VFTR_GSM);
        Assert.assertEquals(str0, str1);

        // skip TMOBILENL_GSM - can't encode {}[]

        // had problem passing these tests on linux vs. mac os x -- issue with
        // byte encoding on differnet platforms, replaced tests with source strings
        // that use Java unicode escapes
        // helpful URL: http://www.greywyvern.com/code/php/utf8_html
        // decode a string with every char in VFD2-GSM
        // str0 = "@$ß¤¡ÑÜ§ñü_";
        str0 = "@$\u00df\u00a4\u00a1\u00d1\u00dc\u00a7\u00f1\u00fc_";
        str1 = CharsetUtil.decode(HexUtil.toByteArray("40247E02A15F5D5E1E7D11"), CharsetUtil.CHARSET_VFD2_GSM);
        Assert.assertEquals(str0, str1);

        //str0 = "@$ß$@ÑÜ_ñü_";
        str0 = "@$\u00df$@\u00d1\u00dc_\u00f1\u00fc_";
        str1 = CharsetUtil.decode(HexUtil.toByteArray("40241E24405D5E5F7D7E5F"), CharsetUtil.CHARSET_AIRWIDE_IA5);
        Assert.assertEquals(str0, str1);

        // chars specifically to vodafone-turkey
        //str0 = "$@£¤¥§ÄÅßñΓΔΘΩ€";
        str0 = "$@\u00a3\u00a4\u00a5\u00a7\u00c4\u00c5\u00df\u00f1\u0393\u0394\u0398\u03a9\u20ac";
        str1 = CharsetUtil.decode(HexUtil.toByteArray("2440A3A4A5A7C4C5DFF1137F19151B65"), CharsetUtil.CHARSET_VFTR_GSM);
        Assert.assertEquals(str0, str1);

        // form feed GSM escape sequence
        str0 = "\f\f";
        str1 = CharsetUtil.decode(HexUtil.toByteArray("1B0A1B0A"), CharsetUtil.CHARSET_GSM);
        Assert.assertEquals(str0, str1);
    }

    @Test
    public void verifyDecodeDoesNotChangeByteArray() throws Exception {
        for (Map.Entry<String,Charset> entry : CharsetUtil.getCharsetMap().entrySet()) {
            byte[] bytes = new byte[] { (byte)0x40, (byte)0x5F, (byte)0x24, (byte)0x78, (byte)0x02, (byte)0x02};
            byte[] expectedBytes = Arrays.copyOf(bytes, bytes.length);
            String str0 = CharsetUtil.decode(bytes, entry.getValue());
            // test that the byte array wasn't changed
            Assert.assertArrayEquals("Charset " + entry.getKey() + " impl bad -- modified byte array parameter", expectedBytes, bytes);
        }
    }

    @Test
    public void verifyNullByteArray() throws Exception {
        for (Map.Entry<String,Charset> entry : CharsetUtil.getCharsetMap().entrySet()) {
            // test that the byte array wasn't changed
            Assert.assertEquals("Charset " + entry.getKey() + " impl bad -- did not return null", null, CharsetUtil.decode(null, entry.getValue()));
        }
    }

    @Test
    public void decodeToStringBuilderAllCharsets() throws Exception {
        // try every charset with simple A-Z, a-z, and 0-9 which should work in all charsets
        String expectedString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefjhijklmnopqrstuvwxyz01234567890";
        // test decode to stringBuilder
        for (Map.Entry<String,Charset> entry : CharsetUtil.getCharsetMap().entrySet()) {
            // make this a harder test where we actually test this was appended!
            byte[] expectedBytes = CharsetUtil.encode(expectedString, entry.getKey());
            StringBuilder sb = new StringBuilder("T");
            CharsetUtil.decode(expectedBytes, sb, entry.getValue());
            Assert.assertEquals("Charset " + entry.getKey() + " impl broken", "T"+expectedString, sb.toString());
        }
    }

    @Test
    public void decodeToStringAllCharsets() throws Exception {
        // try every charset with simple A-Z, a-z, and 0-9 which should work in all charsets
        String expectedString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefjhijklmnopqrstuvwxyz01234567890";
        // test decode to stringBuilder
        for (Map.Entry<String,Charset> entry : CharsetUtil.getCharsetMap().entrySet()) {
            // make this a harder test where we actually test this was appended!
            byte[] expectedBytes = CharsetUtil.encode(expectedString, entry.getValue());
            String actualString = CharsetUtil.decode(expectedBytes, entry.getKey());
            Assert.assertEquals("Charset " + entry.getKey() + " impl broken", expectedString, actualString);
        }
    }

    @Test
    public void normalize() throws Exception {
        String in = null;

        // try every charset with simple A-Z, a-z, and 0-9 which should work in all charsets
        in = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefjhijklmnopqrstuvwxyz01234567890?&@";
        for (Map.Entry<String,Charset> entry : CharsetUtil.getCharsetMap().entrySet()) {
            Assert.assertEquals("Charset " + entry.getKey() + " implementation broken", in, CharsetUtil.normalize(in, entry.getValue()));
        }

        in = "\u20AC";  // euro currency char (only supported in a couple charsets)
        Assert.assertEquals("\u20AC", CharsetUtil.normalize(in, CharsetUtil.CHARSET_GSM));
        Assert.assertEquals("\u20AC", CharsetUtil.normalize(in, CharsetUtil.CHARSET_PACKED_GSM));
        Assert.assertEquals("\u20AC", CharsetUtil.normalize(in, CharsetUtil.CHARSET_AIRWIDE_GSM));
        Assert.assertEquals("\u20AC", CharsetUtil.normalize(in, CharsetUtil.CHARSET_VFD2_GSM));
        Assert.assertEquals("\u20AC", CharsetUtil.normalize(in, CharsetUtil.CHARSET_VFTR_GSM));
        Assert.assertEquals("?", CharsetUtil.normalize(in, CharsetUtil.CHARSET_ISO_8859_1));
        Assert.assertEquals("\u20AC", CharsetUtil.normalize(in, CharsetUtil.CHARSET_ISO_8859_15));
        Assert.assertEquals("\u20AC", CharsetUtil.normalize(in, CharsetUtil.CHARSET_UCS_2));
        Assert.assertEquals("\u20AC", CharsetUtil.normalize(in, CharsetUtil.CHARSET_UCS_2LE));
        Assert.assertEquals("\u20AC", CharsetUtil.normalize(in, CharsetUtil.CHARSET_UTF_8));
        Assert.assertEquals("\u20AC", CharsetUtil.normalize(in, CharsetUtil.CHARSET_TMOBILENL_GSM));

        in = "\u6025";  // arabic char (only supported in a couple charsets)
        Assert.assertEquals("?", CharsetUtil.normalize(in, CharsetUtil.CHARSET_GSM));
        Assert.assertEquals("?", CharsetUtil.normalize(in, CharsetUtil.CHARSET_PACKED_GSM));
        Assert.assertEquals("?", CharsetUtil.normalize(in, CharsetUtil.CHARSET_AIRWIDE_GSM));
        Assert.assertEquals("?", CharsetUtil.normalize(in, CharsetUtil.CHARSET_VFD2_GSM));
        Assert.assertEquals("?", CharsetUtil.normalize(in, CharsetUtil.CHARSET_VFTR_GSM));
        Assert.assertEquals("?", CharsetUtil.normalize(in, CharsetUtil.CHARSET_ISO_8859_1));
        Assert.assertEquals("?", CharsetUtil.normalize(in, CharsetUtil.CHARSET_ISO_8859_15));
        Assert.assertEquals("\u6025", CharsetUtil.normalize(in, CharsetUtil.CHARSET_UCS_2));
        Assert.assertEquals("\u6025", CharsetUtil.normalize(in, CharsetUtil.CHARSET_UCS_2LE));
        Assert.assertEquals("\u6025", CharsetUtil.normalize(in, CharsetUtil.CHARSET_UTF_8));
        Assert.assertEquals("?", CharsetUtil.normalize(in, CharsetUtil.CHARSET_TMOBILENL_GSM));
    }
}
