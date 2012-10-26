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
public class ByteArrayUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(ByteArrayUtilTest.class);

    @Test
    public void toByte() throws Exception {
        try {
            ByteArrayUtil.toByte(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
        Assert.assertEquals((byte)0x12, ByteArrayUtil.toByte(new byte[] { (byte)0x12 }));
        Assert.assertEquals((byte)0xFF, ByteArrayUtil.toByte(new byte[] { (byte)0x12, (byte)0xFF, (byte)0x56 }, 1, 1));
    }

    @Test
    public void toUnsignedByte() throws Exception {
        try {
            ByteArrayUtil.toUnsignedByte(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
        Assert.assertEquals((short)0x12, ByteArrayUtil.toUnsignedByte(new byte[] { (byte)0x12 }));
        Assert.assertEquals((short)0xFF, ByteArrayUtil.toUnsignedByte(new byte[] { (byte)0x12, (byte)0xFF, (byte)0x56 }, 1, 1));
        Assert.assertEquals((short)240, ByteArrayUtil.toUnsignedByte(new byte[] { (byte)0xF0 }));
        Assert.assertEquals((short)240, ByteArrayUtil.toUnsignedByte(new byte[] { (byte)0x12, (byte)0xF0 }, 1, 1));
    }

    @Test
    public void toShort() throws Exception {
        try {
            ByteArrayUtil.toShort(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
        try {
            ByteArrayUtil.toShort(new byte[] { (byte)0x12, (byte)0x34 }, 1, 3);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
        try {
            ByteArrayUtil.toShort(new byte[] { (byte)0x12, (byte)0x34 }, 1, 2);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
        Assert.assertEquals((short)0x1234, ByteArrayUtil.toShort(new byte[] { (byte)0x12, (byte)0x34 }));
        Assert.assertEquals((short)0x3456, ByteArrayUtil.toShort(new byte[] { (byte)0x12, (byte)0x34, (byte)0x56 }, 1, 2));
    }

    @Test
    public void toUnsignedShort() throws Exception {
        try {
            ByteArrayUtil.toUnsignedShort(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
        Assert.assertEquals((int)0x1234, ByteArrayUtil.toUnsignedShort(new byte[] { (byte)0x12, (byte)0x34 }));
        Assert.assertEquals((int)0xFF56, ByteArrayUtil.toUnsignedShort(new byte[] { (byte)0x12, (byte)0xFF, (byte)0x56 }, 1, 2));
        Assert.assertEquals((int)61458, ByteArrayUtil.toUnsignedShort(new byte[] { (byte)0xF0, (byte)0x12 }));
        Assert.assertEquals((int)61458, ByteArrayUtil.toUnsignedShort(new byte[] { (byte)0x56, (byte)0xF0, (byte)0x12 }, 1, 2));
    }

    @Test
    public void toInt() throws Exception {
        try {
            ByteArrayUtil.toInt(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
        Assert.assertEquals((int)0x12345678, ByteArrayUtil.toInt(new byte[] { (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78 }));
        Assert.assertEquals((int)0x34567890, ByteArrayUtil.toInt(new byte[] { (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x90 }, 1, 4));
    }

    @Test
    public void toUnsignedInt() throws Exception {
        try {
            ByteArrayUtil.toUnsignedInt(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
        Assert.assertEquals((long)0x12345678, ByteArrayUtil.toUnsignedInt(new byte[] { (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78 }));
        Assert.assertEquals((long)0x34567890, ByteArrayUtil.toUnsignedInt(new byte[] { (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x90 }, 1, 4));
        Assert.assertEquals((long)4027733624L, ByteArrayUtil.toUnsignedInt(new byte[] { (byte)0xF0, (byte)0x12, (byte)0x56, (byte)0x78 }));
        Assert.assertEquals((long)4027733624L, ByteArrayUtil.toUnsignedInt(new byte[] { (byte)0x12, (byte)0xF0, (byte)0x12, (byte)0x56, (byte)0x78 }, 1, 4));
    }

    @Test
    public void toLong() throws Exception {
        try {
            ByteArrayUtil.toLong(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
        Assert.assertEquals(0x1234567812345678L, ByteArrayUtil.toLong(new byte[] { (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78 }));
        Assert.assertEquals(0x3456789012345678L, ByteArrayUtil.toLong(new byte[] { (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x90, (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78 }, 1, 8));
    }

    @Test
    public void toByteArray() throws Exception {
        Assert.assertArrayEquals(new byte[] { (byte)0x12 }, ByteArrayUtil.toByteArray((byte)0x12));
        Assert.assertArrayEquals(new byte[] { (byte)0x12, (byte)0x34 }, ByteArrayUtil.toByteArray((short)0x1234));
        Assert.assertArrayEquals(new byte[] { (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78 }, ByteArrayUtil.toByteArray((int)0x12345678));
        Assert.assertArrayEquals(new byte[] { (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78 }, ByteArrayUtil.toByteArray(0x1234567812345678L));
    }
    
}
