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

/**
 * Utility class for methods to handle bytes.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class ByteUtil {
	
	   /**
     * BCD encodes a String into a byte array.
     */
    public static byte[] encodeBcd(String data, int num_bytes) {
        // allocate buffer
        byte buf[] = new byte[num_bytes];

        // if length of addr is odd, then add an F onto the end of it
        if ((data.length() % 2) != 0) {
            data += "F";
        }

        int index = 0;
        for (int i = 0; i < data.length(); i += 2) {
            StringBuilder x = new StringBuilder(data.substring(i, i + 2));
            x.reverse();
            buf[index] = ByteUtil.decodeHex(x.toString(), 2)[0];
            index++;
        }

        return buf;
    }

    /**
     * BCD decodes a byte array into a String. For example, addresses inside
     * PDUs for SMS are usually BCD encoded.  An address of "13134434272" will
     * actually look like this inside the byte[] "31314443722F"
     *
     * len is the number of bytes
     * to read.
     */
    public static String decodeBcd(byte[] data, int offset, int len) {
        StringBuilder result = new StringBuilder();

        // loop through byte array until we get to the end or an F
        for (int i = offset; i < (offset + len); i++) {
            // get buffer
            StringBuilder x = new StringBuilder(encodeHex(data[i]));
            // reverse it
            x.reverse();
            // add it to the data
            if (x.charAt(1) == 'F' || x.charAt(1) == 'f') {
                result.append(x.substring(0, 1));
            } else {
                result.append(x.toString());
            }
        }
        return result.toString();
    }


    /**
     * Decodes the string data (in hex) into a byte array.
     * @param width The width of the hex encoding (2 or 4)
     * @deprecated Please see class HexUtil
     */
    @Deprecated
    public static byte[] decodeHex(String hexString, int width) {
        return HexUtil.toByteArray(hexString);
        /**
        // the resulting string
        StringBuffer result = new StringBuffer();

        // just cycle through the whole string two chars at a time
        for (int i = 0; i < data.length(); i += width) {
            result.append((char) Integer.parseInt(data.substring(i, i + width), 16));
        }

        // return whats rightfully the user's
        try {
            return result.toString().getBytes("ISO-8859-1");
        } catch (Exception e) {
            // should never happen
        }
        return null;
         */
    }

    /**
     * Encodes a byte into a hex string (hex dump).
     * @deprecated Please see class HexUtil
     */
    @Deprecated
    public static String encodeHex(byte value) {
        return HexUtil.toHexString(value);
        /**
        // the result
        StringBuffer result = new StringBuffer();

        short val = decodeUnsigned(data);

        result.append(padLeading(Integer.toHexString((int) val), 2));

        // return encoded text
        return result.toString();
         */
    }

    /**
     * Encodes a byte array into a hex string (hex dump).
     * @deprecated Please see class HexUtil
     */
    @Deprecated
    public static String encodeHex(byte[] bytes) {
        return HexUtil.toHexString(bytes);
        /**
        // the result
        StringBuffer result = new StringBuffer();

        short val = 0;

        // encode each byte into a hex dump
        for (int i = 0; i < data.length; i++) {
            val = decodeUnsigned(data[i]);
            result.append(padLeading(Integer.toHexString((int) val), 2));
        }

        // return encoded text
        return result.toString();
         */
    }

    /**
     * Encodes a byte array into a hex string (hex dump).
     * @deprecated Please see class HexUtil
     */
    @Deprecated
    public static String encodeHex(byte[] data, char delimiter) {
        // the result
        StringBuilder result = new StringBuilder();

        short val = 0;

        // encode each byte into a hex dump
        for (int i = 0; i < data.length; i++) {
            val = decodeUnsigned(data[i]);
            result.append(padLeading(Integer.toHexString((int) val), 2));
            result.append(delimiter);
        }

        // return encoded text
        return result.toString();
    }

    public static String padLeading(String data, int width) {
        // the number of zeros to add
        int num = width - data.length();
        // check if we're looking at a stupid programmer
        if (0 >= num) {
            return data;
        }
        // a temp string
        StringBuilder result = new StringBuilder();
        // add all the zeros
        for (int i = 0; i < num; i++) {
            result.append('0');
        }
        // add the original string
        result.append(data);
        // return the damn string
        return result.toString();
    }
	

    /**
     * Allow a variable of type <code>byte</code> to carry lengths or sizes up to 255.
     * The integral types are signed in Java, so if there is necessary to store
     * an unsigned type into signed of the same size, the value can be stored
     * as negative number even if it would be positive in unsigned case.
     * For example message length can be 0 to 254 and is carried by 1 octet
     * i.e. unsigned 8 bits. We use a byte variable to read the value from octet stream.
     * If the length is >127, it is interpreted as negative byte using negative
     * complement notation.
     * So length 150 would be interpreted as Java byte -106. If we want to know
     * the actual value (*length) we need to make a correction to a bigger integral type,
     * in case of byte it's short. The correction from (negative) byte to short is<br>
     * <code>(short)(256+(short)length)</code><br>
     * This converts the negative byte value representing positive length into positive
     * short value.
     * @see #encodeUnsigned(short)
     */
    public static short decodeUnsigned(byte signed)
    {
        if (signed >= 0) {
            return signed;
        } else {
            return (short)(256+(short)signed);
        }
    }
}
