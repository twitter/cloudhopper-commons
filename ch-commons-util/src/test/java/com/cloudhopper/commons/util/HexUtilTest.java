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
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class HexUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(HexUtilTest.class);

    @Test
    public void toHexStringWithByteArray() throws Exception {
        String s0 = null;

        // a NULL byte array returns an empty string (safe choice)
        s0 = HexUtil.toHexString(null);
        Assert.assertEquals("", s0);

        s0 = HexUtil.toHexString(new byte[0]);
        Assert.assertEquals("", s0);

        s0 = HexUtil.toHexString(new byte[] { 0x34 });
        Assert.assertEquals("34", s0);

        s0 = HexUtil.toHexString(new byte[] { 0x34, 0x35 });
        Assert.assertEquals("3435", s0);

        s0 = HexUtil.toHexString(new byte[] { (byte)0xFF, (byte)0xAB });
        Assert.assertEquals("FFAB", s0);
    }

    @Test
    public void toHexStringWithByteArrayAndOffsetLength() throws Exception {
        String s0 = null;

        // a NULL byte array returns an empty string (safe choice)
        s0 = HexUtil.toHexString(null, 0, 0);
        Assert.assertEquals("", s0);

        s0 = HexUtil.toHexString(new byte[0], 0, 0);
        Assert.assertEquals("", s0);

        s0 = HexUtil.toHexString(new byte[] { 0x34 }, 0, 1);
        Assert.assertEquals("34", s0);

        s0 = HexUtil.toHexString(new byte[] { 0x34, 0x35 }, 0, 2);
        Assert.assertEquals("3435", s0);

        s0 = HexUtil.toHexString(new byte[] { (byte)0xFF, (byte)0xAB }, 0, 2);
        Assert.assertEquals("FFAB", s0);

        s0 = HexUtil.toHexString(new byte[] { (byte)0xFF, (byte)0xAB }, 1, 1);
        Assert.assertEquals("AB", s0);

        try {
            HexUtil.toHexString(new byte[0], -1, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }

        try {
            HexUtil.toHexString(new byte[0], 0, -1);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }

        try {
            HexUtil.toHexString(new byte[0], 1, 0);
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException e) {
            // correct behavior
        }
    }

    @Test
    public void appendHexStringWithByteArray() throws Exception {
        StringBuilder s0 = new StringBuilder(100);

        s0.setLength(0);
        try {
            HexUtil.appendHexString(null, null);
            Assert.fail();
        } catch (NullPointerException e) {
            // correct behavior
        }
        Assert.assertEquals("", s0.toString());

        // a NULL byte array appends nothing (a noop)
        s0.setLength(0);
        HexUtil.appendHexString(s0, null);
        Assert.assertEquals("", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, new byte[0]);
        Assert.assertEquals("", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, new byte[] { 0x34 });
        Assert.assertEquals("34", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, new byte[] { 0x34, 0x35 });
        Assert.assertEquals("3435", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, new byte[] { (byte)0xFF, (byte)0xAB });
        Assert.assertEquals("FFAB", s0.toString());
    }

    @Test
    public void appendHexStringWithByteArrayAndOffsetLength() throws Exception {
        StringBuilder s0 = new StringBuilder(100);

        s0.setLength(0);
        try {
            // a null buffer throws an exception
            HexUtil.appendHexString(null, new byte[0], 0, 0);
            Assert.fail();
        } catch (NullPointerException e) {
            // correct behavior
            //logger.error(e);
        }
        Assert.assertEquals("", s0.toString());

        // a NULL byte array appends nothing (a noop)
        s0.setLength(0);
        HexUtil.appendHexString(s0, null, 0, 0);
        Assert.assertEquals("", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, new byte[0], 0, 0);
        Assert.assertEquals("", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, new byte[] { 0x34 }, 0, 1);
        Assert.assertEquals("34", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, new byte[] { 0x34 }, 1, 0);
        Assert.assertEquals("", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, new byte[] { 0x34, 0x35 }, 1, 1);
        Assert.assertEquals("35", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, new byte[] { 0x34, 0x35 }, 0, 2);
        Assert.assertEquals("3435", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, new byte[] { (byte)0xFF, (byte)0xAB }, 0, 2);
        Assert.assertEquals("FFAB", s0.toString());

        try {
            HexUtil.appendHexString(s0, new byte[0], -1, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }

        try {
            HexUtil.appendHexString(s0, new byte[0], 0, -1);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }

        try {
            HexUtil.appendHexString(s0, new byte[0], 1, 0);
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException e) {
            // correct behavior
        }
    }

    @Test
    public void toHexStringWithByte() throws Exception {
        String s0 = null;

        s0 = HexUtil.toHexString((byte)0x65);
        Assert.assertEquals("65", s0);

        s0 = HexUtil.toHexString((byte)0x34);
        Assert.assertEquals("34", s0);

        s0 = HexUtil.toHexString((byte)0x00);
        Assert.assertEquals("00", s0);

        s0 = HexUtil.toHexString((byte)-1);
        Assert.assertEquals("FF", s0);
    }

    @Test
    public void appendHexStringWithByte() throws Exception {
        StringBuilder s0 = new StringBuilder(2);

        HexUtil.appendHexString(s0, (byte)0x65);
        Assert.assertEquals("65", s0.toString());

        // this appends the next byte
        HexUtil.appendHexString(s0, (byte)0x34);
        Assert.assertEquals("6534", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, (byte)0x00);
        Assert.assertEquals("00", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, (byte)-1);
        Assert.assertEquals("FF", s0.toString());
    }

    @Test
    public void toHexStringWithShort() throws Exception {
        String s0 = null;

        s0 = HexUtil.toHexString((short)0x65);
        Assert.assertEquals("0065", s0.toString());

        s0 = HexUtil.toHexString((short)0x6534);
        Assert.assertEquals("6534", s0.toString());

        s0 = HexUtil.toHexString((short)0x00);
        Assert.assertEquals("0000", s0.toString());

        s0 = HexUtil.toHexString((short)-1);
        Assert.assertEquals("FFFF", s0.toString());
    }

    @Test
    public void appendHexStringWithShort() throws Exception {
        StringBuilder s0 = new StringBuilder(4);

        HexUtil.appendHexString(s0, (short)0x65);
        Assert.assertEquals("0065", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, (short)0x6534);
        Assert.assertEquals("6534", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, (short)0x00);
        Assert.assertEquals("0000", s0.toString());

        s0.setLength(0);
        // java is signed, so this should be a large hex representation
        HexUtil.appendHexString(s0, (short)-1);
        Assert.assertEquals("FFFF", s0.toString());
    }

    @Test
    public void toHexStringWithInt() throws Exception {
        String s0 = null;

        s0 = HexUtil.toHexString((int)0x65);
        Assert.assertEquals("00000065", s0.toString());

        s0 = HexUtil.toHexString((int)0x6534);
        Assert.assertEquals("00006534", s0.toString());

        s0 = HexUtil.toHexString((int)0x00);
        Assert.assertEquals("00000000", s0.toString());

        s0 = HexUtil.toHexString(-34000);
        Assert.assertEquals("FFFF7B30", s0.toString());

        s0 = HexUtil.toHexString(-1);
        Assert.assertEquals("FFFFFFFF", s0.toString());
    }

    @Test
    public void appendHexStringWithInt() throws Exception {
        StringBuilder s0 = new StringBuilder(8);

        HexUtil.appendHexString(s0, (int)0x65);
        Assert.assertEquals("00000065", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, (int)0x6534);
        Assert.assertEquals("00006534", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, (int)0x00);
        Assert.assertEquals("00000000", s0.toString());

        s0.setLength(0);
        // java is signed, so this should be a large hex representation
        HexUtil.appendHexString(s0, -34000);
        Assert.assertEquals("FFFF7B30", s0.toString());

        s0.setLength(0);
        // java is signed, so this should be a large hex representation
        HexUtil.appendHexString(s0, -1);
        Assert.assertEquals("FFFFFFFF", s0.toString());
    }

    @Test
    public void toHexStringWithLong() throws Exception {
        String s0 = null;

        s0 = HexUtil.toHexString(0x65L);
        Assert.assertEquals("0000000000000065", s0.toString());

        s0 = HexUtil.toHexString(0x6534L);
        Assert.assertEquals("0000000000006534", s0.toString());

        s0 = HexUtil.toHexString(0x00L);
        Assert.assertEquals("0000000000000000", s0.toString());

        s0 = HexUtil.toHexString(-34000L);
        Assert.assertEquals("FFFFFFFFFFFF7B30", s0.toString());

        s0 = HexUtil.toHexString(-1L);
        Assert.assertEquals("FFFFFFFFFFFFFFFF", s0.toString());
    }

    @Test
    public void appendHexStringWithLong() throws Exception {
        StringBuilder s0 = new StringBuilder(16);

        HexUtil.appendHexString(s0, 0x65L);
        Assert.assertEquals("0000000000000065", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, 0x6534L);
        Assert.assertEquals("0000000000006534", s0.toString());

        s0.setLength(0);
        HexUtil.appendHexString(s0, 0x00L);
        Assert.assertEquals("0000000000000000", s0.toString());

        s0.setLength(0);
        // java is signed, so this should be a large hex representation
        HexUtil.appendHexString(s0, -34000L);
        Assert.assertEquals("FFFFFFFFFFFF7B30", s0.toString());

        s0.setLength(0);
        // java is signed, so this should be a large hex representation
        HexUtil.appendHexString(s0, -1L);
        Assert.assertEquals("FFFFFFFFFFFFFFFF", s0.toString());
    }

    @Test
    public void hexCharToIntValue() throws Exception {
        Assert.assertEquals(0, HexUtil.hexCharToIntValue('0'));
        Assert.assertEquals(1, HexUtil.hexCharToIntValue('1'));
        Assert.assertEquals(2, HexUtil.hexCharToIntValue('2'));
        Assert.assertEquals(3, HexUtil.hexCharToIntValue('3'));
        Assert.assertEquals(4, HexUtil.hexCharToIntValue('4'));
        Assert.assertEquals(5, HexUtil.hexCharToIntValue('5'));
        Assert.assertEquals(6, HexUtil.hexCharToIntValue('6'));
        Assert.assertEquals(7, HexUtil.hexCharToIntValue('7'));
        Assert.assertEquals(8, HexUtil.hexCharToIntValue('8'));
        Assert.assertEquals(9, HexUtil.hexCharToIntValue('9'));
        Assert.assertEquals(10, HexUtil.hexCharToIntValue('A'));
        Assert.assertEquals(11, HexUtil.hexCharToIntValue('B'));
        Assert.assertEquals(12, HexUtil.hexCharToIntValue('C'));
        Assert.assertEquals(13, HexUtil.hexCharToIntValue('D'));
        Assert.assertEquals(14, HexUtil.hexCharToIntValue('E'));
        Assert.assertEquals(15, HexUtil.hexCharToIntValue('F'));
        Assert.assertEquals(10, HexUtil.hexCharToIntValue('a'));
        Assert.assertEquals(11, HexUtil.hexCharToIntValue('b'));
        Assert.assertEquals(12, HexUtil.hexCharToIntValue('c'));
        Assert.assertEquals(13, HexUtil.hexCharToIntValue('d'));
        Assert.assertEquals(14, HexUtil.hexCharToIntValue('e'));
        Assert.assertEquals(15, HexUtil.hexCharToIntValue('f'));

        try {
            Assert.assertEquals(15, HexUtil.hexCharToIntValue('g'));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
    }

    @Test
    public void toByteArray() throws Exception {
        byte[] bytes = null;

        bytes = HexUtil.toByteArray(null);
        Assert.assertArrayEquals(null, bytes);

        bytes = HexUtil.toByteArray("");
        Assert.assertArrayEquals(new byte[] { }, bytes);

        bytes = HexUtil.toByteArray("65");
        Assert.assertArrayEquals(new byte[] { 0x65 }, bytes);

        bytes = HexUtil.toByteArray("fF");
        Assert.assertArrayEquals(new byte[] { (byte)0xFF }, bytes);

        bytes = HexUtil.toByteArray("BbCc");
        Assert.assertArrayEquals(new byte[] { (byte)0xbb, (byte)0xcc }, bytes);

        bytes = HexUtil.toByteArray("AaBbCcDdEefF");
        Assert.assertArrayEquals(new byte[] { (byte)0xaa, (byte)0xbb, (byte)0xcc, (byte)0xdd, (byte)0xee, (byte)0xff }, bytes);
    }

    @Test
    public void toByteArrayWithOffsetAndLength() throws Exception {
        byte[] bytes = null;

        bytes = HexUtil.toByteArray(null, 0, 2);
        Assert.assertArrayEquals(null, bytes);

        bytes = HexUtil.toByteArray("", 0, 0);
        Assert.assertArrayEquals(new byte[] { }, bytes);

        bytes = HexUtil.toByteArray("65", 0, 2);
        Assert.assertArrayEquals(new byte[] { 0x65 }, bytes);

        bytes = HexUtil.toByteArray("fF", 0, 2);
        Assert.assertArrayEquals(new byte[] { (byte)0xFF }, bytes);

        bytes = HexUtil.toByteArray("AaBbCcDdEefF", 2, 4);
        Assert.assertArrayEquals(new byte[] { (byte)0xbb, (byte)0xcc }, bytes);

        bytes = HexUtil.toByteArray("AaBbCcDdEefF", 0, 12);
        Assert.assertArrayEquals(new byte[] { (byte)0xaa, (byte)0xbb, (byte)0xcc, (byte)0xdd, (byte)0xee, (byte)0xff }, bytes);
    }
}
