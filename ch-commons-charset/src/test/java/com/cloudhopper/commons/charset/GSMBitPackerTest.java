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
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class GSMBitPackerTest {
private static final Logger logger = LoggerFactory.getLogger(GSMBitPackerTest.class);

    @Test
    public void unpackAndPack() throws Exception {
        byte[] packed, unpacked = null;

        // null returns null
        packed = null;
        unpacked = null;
        Assert.assertArrayEquals(unpacked, GSMBitPacker.unpack(packed));
        Assert.assertArrayEquals(packed, GSMBitPacker.pack(unpacked));

        // zero length returns zero length array
        packed = new byte[0];
        unpacked = new byte[0];
        Assert.assertArrayEquals(unpacked, GSMBitPacker.unpack(packed));
        Assert.assertArrayEquals(packed, GSMBitPacker.pack(unpacked));

        // 7-bit single byte is always the single byte
        packed = HexUtil.toByteArray("7F");
        unpacked = HexUtil.toByteArray("7F");
        Assert.assertArrayEquals(unpacked, GSMBitPacker.unpack(packed));
        Assert.assertArrayEquals(packed, GSMBitPacker.pack(unpacked));

        // 8-bit single byte ends up as 1 byte since the MSB of 1
        packed = HexUtil.toByteArray("FF");
        unpacked = HexUtil.toByteArray("7F");
        Assert.assertArrayEquals(unpacked, GSMBitPacker.unpack(packed));
        // NOT A 2-WAY TEST
        //Assert.assertArrayEquals(packed, GSMBitPacker.pack(unpacked));
        Assert.assertArrayEquals(HexUtil.toByteArray("7F"), GSMBitPacker.pack(unpacked));

        // these 2 bytes decoded
        packed = HexUtil.toByteArray("9B32");
        unpacked = HexUtil.toByteArray("1B65");
        Assert.assertArrayEquals(unpacked, GSMBitPacker.unpack(packed));
        Assert.assertArrayEquals(packed, GSMBitPacker.pack(unpacked));
        
        // "JOE" packed into 
        packed = HexUtil.toByteArray("CA7719");
        unpacked = "Joe".getBytes();
        Assert.assertArrayEquals(unpacked, GSMBitPacker.unpack(packed));
        Assert.assertArrayEquals(packed, GSMBitPacker.pack(unpacked));

        // "JOEY" packed into
        packed = HexUtil.toByteArray("CA77390F");
        unpacked = "Joey".getBytes();
        Assert.assertArrayEquals(unpacked, GSMBitPacker.unpack(packed));
        Assert.assertArrayEquals(packed, GSMBitPacker.pack(unpacked));
        
        // packed into 00c1285432bd74
        packed = HexUtil.toByteArray("00c1285432bd74");
        unpacked = HexUtil.toByteArray("0002232125262f3a");
        Assert.assertArrayEquals(unpacked, GSMBitPacker.unpack(packed));
        Assert.assertArrayEquals(packed, GSMBitPacker.pack(unpacked));

        // 8 bytes packed into 7 bytes
        packed = HexUtil.toByteArray("CA77392F64D7CB");
        unpacked = "JoeyBlue".getBytes();
        Assert.assertArrayEquals(unpacked, GSMBitPacker.unpack(packed));
        Assert.assertArrayEquals(packed, GSMBitPacker.pack(unpacked));

        // 160 character message in 140 bytes
        packed = HexUtil.toByteArray("d3b29b0c0abb414d2a68da9c82e8e8301d347ebbe9e1b47b0e9297cfe9b71b348797c769737a0c1aa3c3f239685e1fa341e139c8282fbbc76850783c2ebbe97316680a0fbbd37334283c1e97ddf4390b54a68f5d2072195e7693d3ee33e8ed06b1dfe3301b347ed7dd7479de050219df7250191f6ec3d96516286d060dc3ee3039cc02cdcb6e3268fe6e9763");
        unpacked = HexUtil.toByteArray("53656e6420616e204d5420534d53207468617420636f6e7461696e7320726567696f6e2073706563696669632063686172732073756368206173204672656e636820616363656e74732c205370616e69736820616363656e74732c206574632e20646570656e64696e67206f6e206c6f63616c20636f756e7472792e2020466f72206578616d706c652c2069662043616e6164612c2073656e6420736f6d6531");
        Assert.assertArrayEquals(unpacked, GSMBitPacker.unpack(packed));
        Assert.assertArrayEquals(packed, GSMBitPacker.pack(unpacked));

        // SPECIAL CASE -- last octet is zeros (AT character)
        // in 7-bit packed form, we don't know if its an AT char OR if its padding
        // basically 8 chars in 7 bytes with the last char being an @ character
        packed = HexUtil.toByteArray("CA77392F64D701");
        //unpacked = HexUtil.toByteArray("0002232125262f3a");
        //unpacked = "JoeyBlu\u0000".getBytes("ISO-8859-1");
        // a choice was made to strip off the trailing @ char
        unpacked = "JoeyBlu".getBytes("ISO-8859-1");
        Assert.assertArrayEquals(unpacked, GSMBitPacker.unpack(packed));
        Assert.assertArrayEquals(packed, GSMBitPacker.pack(unpacked));
    }
}
