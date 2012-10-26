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
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

// my imports
import com.cloudhopper.commons.util.codec.Base64Codec;

/**
 * Utility class for 2-way encryption.
 *
 * Originally copied from http://www.informit.com/guides/content.aspx?g=java&seqNum=31
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class EncryptUtil {

    public static String generateKey() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("DES");
            SecretKey desKey = keygen.generateKey();
            byte[] bytes = desKey.getEncoded();
            return StringUtil.getAsciiString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Uses the key to encrypt the source string into a base-64
     * encoded string using a DES/ECB/PKCS5Padding cipher.
     * @param key The key to use for encryption.  Must be at least 8 characters
     *      in length.
     * @param source The plain-text to encrypt.
     * @return A Base-64 encoded encypted string.  Its safe to assume that the
     *      encrypted string will be at least twice the size of the plain-text.
     */
    public static String encrypt(String key, String source) {
        try {
            // Get our secret key
            Key key0 = getKey(key);
            // Create the cipher
            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, key0);
            // Our cleartext as bytes
            byte[] cleartext = source.getBytes();
            // Encrypt the cleartext
            byte[] ciphertext = desCipher.doFinal(cleartext);
            // convert to Base64
            //byte[] b64cipherText = Base64.encodeBase64(ciphertext);
            // Return a String representation of the cipher text
            //return StringUtil.getAsciiString(b64cipherText);
            // replaced Jakarta Base64 with custom one
            return Base64Codec.encode(ciphertext);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * Decodes a base-64 encoded string using the key with the DES/ECB/PKCS5Padding
     * cipher.
     * @param key The key to use for encryption. Must be at least 8 characters
     *      in length.
     * @param source The Base-64 encoded encypted string.
     * @return The plain text
     */
    public static String decrypt(String key, String source) {
        try {
            // Get our secret key
            Key key0 = getKey(key);
            // Create the cipher
            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            //byte[] b64cipherText = StringUtil.getAsciiBytes(source);
            // convert source string into base 64 bytes
            //byte[] ciphertext = Base64.decodeBase64(b64cipherText);
            // replaced Jakarta Base64 with custom one
            byte[] ciphertext = Base64Codec.decode(source);

            // Initialize the same cipher for decryption
            desCipher.init(Cipher.DECRYPT_MODE, key0);
            // Decrypt the ciphertext
            byte[] cleartext = desCipher.doFinal(ciphertext);
            // Return the clear text
            return new String(cleartext);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static Key getKey(String key) {
        try {
            byte[] bytes = StringUtil.getAsciiBytes(key);
            DESKeySpec pass = new DESKeySpec(bytes);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            SecretKey s = skf.generateSecret(pass);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}