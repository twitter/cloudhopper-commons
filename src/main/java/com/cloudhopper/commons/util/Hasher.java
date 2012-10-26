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

// java imports
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// my imports
import com.cloudhopper.commons.util.codec.Base64Codec;

/**
 * Utility class to hash strings (such as passwords).
 * <br>
 * <ul>
 *  <li>MD5 - hash is always 16 bytes, 32 char hex string, 24 char base-64
 *  <li>SHA1 - hash is always 20 bytes, 40 char hex string, 28 char base-64
 *  <li>SHA256 - hash is always 32 bytes, 64 char hex string, 44 char base-64
 *  <li>SHA512 - hash is always 64 bytes, 128 char hex string, 88 char base-64
 * </ul>
 * <br>
 * NOTE: These are only 1-way hashes, you won't be able to decrypt the string.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class Hasher {
    private static final String ALGORITHM_MD5 = "MD5"; // Message Digest Algorithm - 128 bit
    private static final String ALGORITHM_SHA1 = "SHA-1"; // Secure Hash Algorithm - 160 bit
    private static final String ALGORITHM_SHA256 = "SHA-256"; // 256 bit
    private static final String ALGORITHM_SHA512 = "SHA-512";  // 512 bit

    /**
     * Enum that represents each different algorithm supported by the Hasher.
     */
    public enum Algorithm {

        /** Message Digest Algorithm - 128 bit */
        MD5 (ALGORITHM_MD5),
        /** Secure Hash Algorithm - 160 bit */
        SHA1 (ALGORITHM_SHA1),
        /** Secure Hash Algorithm - 256 bit */
        SHA256 (ALGORITHM_SHA256),
        /** Secure Hash Algorithm - 512 bit */
        SHA512 (ALGORITHM_SHA512);

        private final String algorithm;

        Algorithm(final String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String toString() {
            return this.algorithm;
        }
    }

    private Algorithm algorithm;

    public Hasher() {
        this.algorithm = Algorithm.MD5;
    }

    public Hasher(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Hashes the input String into a hex encoded hashed String.
     * <br>
     * <ul>
     *  <li>MD5 - hash is always 32 char hex string
     *  <li>SHA1 - hash is always 40 char hex string
     *  <li>SHA256 - hash is always 64 char hex string
     *  <li>SHA512 - hash is always 128 char hex string
     * </ul>
     * @param string0 The String to hash. This assumes its an ASCII string.
     * @return The hex encoded hashed String
     * @throws NoSuchAlgorithmException
     */
    public String toHashedHexString(String string0) throws NoSuchAlgorithmException {
        return this.toHashedHexString(StringUtil.getAsciiBytes(string0));
    }


    /**
     * Hashes the input bytes into a hex encoded hashed String.
     * <ul>
     *  <li>MD5 - hash is always 32 char hex string
     *  <li>SHA1 - hash is always 40 char hex string
     *  <li>SHA256 - hash is always 64 char hex string
     *  <li>SHA512 - hash is always 128 char hex string
     * </ul>
     * @param bytes The bytes to hash
     * @return The hex encoded hashed String
     * @throws NoSuchAlgorithmException
     */
    public String toHashedHexString(byte[] bytes) throws NoSuchAlgorithmException {
        return HexUtil.toHexString(toHashedBytes(bytes));
        //return StringUtil.toHexString(toHashedBytes(bytes));
    }

    /**
     * Hashes the input String into a Base-64 encoded hashed String.
     * <ul>
     *  <li>MD5 - hash is always 24 char base-64
     *  <li>SHA1 - hash is always 28 char base-64
     *  <li>SHA256 - hash is always 44 char base-64
     *  <li>SHA512 - hash is always 88 char base-64
     * </ul>
     * @param string0 The String to hash. This assumes its an ASCII string.
     * @return The Base-64 encoded hashed String
     * @throws NoSuchAlgorithmException
     */
    public String toHashedBase64String(String string0) throws NoSuchAlgorithmException {
        return this.toHashedBase64String(StringUtil.getAsciiBytes(string0));
    }
    

    /**
     * Hashes the input bytes into a Base-64 encoded hashed String.
     * <ul>
     *  <li>MD5 - hash is always 24 char base-64
     *  <li>SHA1 - hash is always 28 char base-64
     *  <li>SHA256 - hash is always 44 char base-64
     *  <li>SHA512 - hash is always 88 char base-64
     * </ul>
     * @param bytes The bytes to hash
     * @return The Base-64 encoded hashed String
     * @throws NoSuchAlgorithmException
     */
    public String toHashedBase64String(byte[] bytes) throws NoSuchAlgorithmException {
        byte[] hashedBytes = toHashedBytes(bytes);
        return Base64Codec.encode(hashedBytes);
        //return StringUtil.getAsciiString(toHashedBase64Bytes(bytes));
    }

    /**
     * Hashes the input bytes into a Base-64 encoded hashed byte[].
     * <ul>
     *  <li>MD5 - hash is always 24 bytes base-64
     *  <li>SHA1 - hash is always 28 bytes base-64
     *  <li>SHA256 - hash is always 44 bytes base-64
     *  <li>SHA512 - hash is always 88 bytes base-64
     * </ul>
     * @param bytes The bytes to hash
     * @return The Base-64 encoded hashed byte[]
     * @throws NoSuchAlgorithmException
     */
    //public byte[] toHashedBase64Bytes(byte[] bytes) throws NoSuchAlgorithmException {
    //    return Base64.encodeBase64(toHashedBytes(bytes));
    //}

    /**
     * Hashes the input bytes into a hashed byte[] -- no special encoding.
     * <ul>
     *  <li>MD5 - hash is always 16 bytes
     *  <li>SHA1 - hash is always 20 bytes
     *  <li>SHA256 - hash is always 32 bytes
     *  <li>SHA512 - hash is always 64 bytes
     * </ul>
     * @param bytes The bytes to hash
     * @return The hashed byte[]
     * @throws NoSuchAlgorithmException
     */
    public byte[] toHashedBytes(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm.toString());
        return messageDigest.digest(bytes);
    }
}

