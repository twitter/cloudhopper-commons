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
public class HasherTest {
    private static final Logger logger = LoggerFactory.getLogger(HasherTest.class);

    // md5 samples -- all generated from http://tools.benramsey.com/md5/
    private static final String MD5_SAMPLE1 = "test";
    private static final String MD5_SAMPLE1_HASH = "098f6bcd4621d373cade4e832627b4f6".toUpperCase();
    private static final String MD5_SAMPLE2 = "hello world";
    private static final String MD5_SAMPLE2_HASH = "5eb63bbbe01eeed093cb22bb8f5acdc3".toUpperCase();
    private static final String MD5_SAMPLE3 = "this is going to be a super long string that has spaces, etc. and ! I'm really happy that I'll be testing the MD5 hash to its fullest!!!!";
    private static final String MD5_SAMPLE3_HASH = "6c18b1eb40a59dc9ce43f62f4962219c".toUpperCase();

    // sha-1 samples -- all generated from http://people.eku.edu/styere/Encrypt/JS-SHA1.html
    private static final String SHA1_SAMPLE1 = "test";
    private static final String SHA1_SAMPLE1_HASH = "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3".toUpperCase();
    private static final String SHA1_SAMPLE2 = "th1s!typic@lpassWd";
    private static final String SHA1_SAMPLE2_HASH = "605fea3566504158c869ab98fb36858d68c490a6".toUpperCase();
    private static final String SHA1_SAMPLE3 = "this is going to be a super long string that has spaces, etc. and ! I'm really happy that I'll be testing the MD5 hash to its fullest!!!!";
    private static final String SHA1_SAMPLE3_HASH = "4a728cea6aec64cc6d7f022389019f1ecca85ed0".toUpperCase();

    // sha-256 samples -- all generated from http://www.johnmaguire.us/tools/hashcalc/index.php
    private static final String SHA256_SAMPLE1 = "test";
    private static final String SHA256_SAMPLE1_HASH = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08".toUpperCase();
    private static final String SHA256_SAMPLE2 = "th1s!typic@lpassWd";
    private static final String SHA256_SAMPLE2_HASH = "5178d956703d8a15c2d5516771f8398d446f56f40d1651a9278a178cb883dbeb".toUpperCase();
    private static final String SHA256_SAMPLE3 = "this is going to be a super long string that has spaces, etc. and ! I'm really happy that I'll be testing the MD5 hash to its fullest!!!!";
    private static final String SHA256_SAMPLE3_HASH = "614cf1ee45df9ce592fa1bfc3d9a9f9bc6ad887a882987e467f9a3002d779151".toUpperCase();

    // sha-512 samples -- all generated from http://www.johnmaguire.us/tools/hashcalc/index.php
    private static final String SHA512_SAMPLE1 = "test";
    private static final String SHA512_SAMPLE1_HASH = "ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff".toUpperCase();
    private static final String SHA512_SAMPLE2 = "th1s!typic@lpassWd";
    private static final String SHA512_SAMPLE2_HASH = "8085dbc8919e03ee7c093670e7bd86683b8fee8e0397998f4f74efe4ad6eae323af0bdaa3f3df0558c71f63b27057e1120d962e9c6fa7f86274347334f47b63f".toUpperCase();
    private static final String SHA512_SAMPLE3 = "this is going to be a super long string that has spaces, etc. and ! I'm really happy that I'll be testing the MD5 hash to its fullest!!!!";
    private static final String SHA512_SAMPLE3_HASH = "14a47a42148abd5a12a0cea35ada405025eff87fe713c7664afc62332f8d852ed9b6f61accd1e3782512a1447851412efea094c81ec61268704d3f2b198b65be".toUpperCase();


    @Test
    public void toHashedHexStringMD5() throws Exception {
        // MD5 is default, this will make sure we don't break that
        Hasher hasher = new Hasher();
        String sample1 = hasher.toHashedHexString(MD5_SAMPLE1);
        Assert.assertEquals(MD5_SAMPLE1_HASH, sample1);
        String sample2 = hasher.toHashedHexString(MD5_SAMPLE2);
        Assert.assertEquals(MD5_SAMPLE2_HASH, sample2);
        String sample3 = hasher.toHashedHexString(MD5_SAMPLE3);
        Assert.assertEquals(MD5_SAMPLE3_HASH, sample3);
        Assert.assertEquals(32, sample3.length());
    }

    @Test
    public void toHashedHexStringSHA1() throws Exception {
        // MD5 is default, this will make sure we don't break that
        Hasher hasher = new Hasher(Hasher.Algorithm.SHA1);
        String sample1 = hasher.toHashedHexString(SHA1_SAMPLE1);
        Assert.assertEquals(SHA1_SAMPLE1_HASH, sample1);
        String sample2 = hasher.toHashedHexString(SHA1_SAMPLE2);
        Assert.assertEquals(SHA1_SAMPLE2_HASH, sample2);
        String sample3 = hasher.toHashedHexString(SHA1_SAMPLE3);
        Assert.assertEquals(SHA1_SAMPLE3_HASH, sample3);
        Assert.assertEquals(40, sample3.length());
    }

    @Test
    public void toHashedHexStringSHA256() throws Exception {
        // MD5 is default, this will make sure we don't break that
        Hasher hasher = new Hasher(Hasher.Algorithm.SHA256);
        String sample1 = hasher.toHashedHexString(SHA256_SAMPLE1);
        Assert.assertEquals(SHA256_SAMPLE1_HASH, sample1);
        String sample2 = hasher.toHashedHexString(SHA256_SAMPLE2);
        Assert.assertEquals(SHA256_SAMPLE2_HASH, sample2);
        String sample3 = hasher.toHashedHexString(SHA256_SAMPLE3);
        Assert.assertEquals(SHA256_SAMPLE3_HASH, sample3);
        Assert.assertEquals(64, sample3.length());
    }

    @Test
    public void toHashedHexStringSHA512() throws Exception {
        // MD5 is default, this will make sure we don't break that
        Hasher hasher = new Hasher(Hasher.Algorithm.SHA512);
        String sample1 = hasher.toHashedHexString(SHA512_SAMPLE1);
        Assert.assertEquals(SHA512_SAMPLE1_HASH, sample1);
        String sample2 = hasher.toHashedHexString(SHA512_SAMPLE2);
        Assert.assertEquals(SHA512_SAMPLE2_HASH, sample2);
        String sample3 = hasher.toHashedHexString(SHA512_SAMPLE3);
        Assert.assertEquals(SHA512_SAMPLE3_HASH, sample3);
        Assert.assertEquals(128, sample3.length());
    }
}
