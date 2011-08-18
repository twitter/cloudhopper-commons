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

import com.cloudhopper.commons.util.FastByteArrayOutputStream;
import java.io.IOException;

/**
 * This class encodes and decodes Java Strings to and from the SMS default
 * alphabet. It also supports the default extension table. The default alphabet
 * and it's extension table is defined in GSM 03.38.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class GSMCharset extends BaseCharset {

    public static final int EXTENDED_ESCAPE = 0x1b;

    /** Page break (extended table). */
    public static final int PAGE_BREAK = 0x0a;

    public static final char[] CHAR_TABLE = {
        '@', '\u00a3', '$', '\u00a5', '\u00e8', '\u00e9', '\u00f9', '\u00ec',
        '\u00f2', '\u00c7', '\n', '\u00d8', '\u00f8', '\r', '\u00c5', '\u00e5',
        '\u0394', '_', '\u03a6', '\u0393', '\u039b', '\u03a9', '\u03a0', '\u03a8',
        '\u03a3', '\u0398', '\u039e', ' ', '\u00c6', '\u00e6', '\u00df', '\u00c9',
        ' ', '!', '"', '#', '\u00a4', '%', '&', '\'',
        '(', ')', '*', '+', ',', '-', '.', '/',
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', ':', ';', '<', '=', '>', '?',
        '\u00a1', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
        'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
        'X', 'Y', 'Z', '\u00c4', '\u00d6', '\u00d1', '\u00dc', '\u00a7',
        '\u00bf', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
        'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
        'x', 'y', 'z', '\u00e4', '\u00f6', '\u00f1', '\u00fc', '\u00e0',
    };

    /**
     * Extended character table. Characters in this table are accessed by the
     * 'escape' character in the base table. It is important that none of the
     * 'inactive' characters ever be matchable with a valid base-table
     * character as this breaks the encoding loop.
     *
     * @see #EXTENDED_ESCAPE
     */
    public static final char[] EXT_CHAR_TABLE = {
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, '^', 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            '{', '}', 0, 0, 0, 0, 0, '\\',
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, '[', '~', ']', 0,
            '|', 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, '\u20ac', 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
    };


    /**
     * Verifies that this charset can represent every character in the Java
     * String (char sequence).
     * @param str0 The String to verfiy
     * @return True if the charset can represent every character in the Java
     *      String, otherwise false.
     */
    static public boolean canRepresent(CharSequence str0) {
        if (str0 == null) {
            return true;
        }

        int len = str0.length();
        for (int i = 0; i < len; i++) {
            // get the char in this string
            char c = str0.charAt(i);
            // a very easy check a-z, A-Z (and [\]^_ chars too), and 0-9 are always valid
            if ((c >= 'A' && c <= '_') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                continue;
            } else {
                // search both charmaps (if char is in either, we're good!)
                boolean found = false;
                for (int j = 0; j < CHAR_TABLE.length; j++) {
                    if (c == CHAR_TABLE[j]) {
                        found = true;
                        break;          // stop searching thru char table!
                    } else if (c == EXT_CHAR_TABLE[j]) {
                        found = true;
                        break;          // stop searching thru char table!
                    }
                }
                // if we searched both charmaps and didn't find it, then its bad
                if (!found) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int estimateEncodeByteLength(CharSequence str0) {
        if (str0 == null) {
            return 0;
        }
        // only a couple chars are expected to be "double" bytes
        return str0.length() + 10;
    }

    @Override
    public byte[] encode(CharSequence str0) {
        if (str0 == null) {
            return null;
        }

        // estimate the length of the dynamic byte array
        int estimatedByteLength = estimateEncodeByteLength(str0);
        FastByteArrayOutputStream baos = new FastByteArrayOutputStream(estimatedByteLength);

        try {
            int len = str0.length();
            for (int i = 0; i < len; i++) {
                int search = 0;
                char c = str0.charAt(i);
                for (; search < CHAR_TABLE.length; search++) {
                    if (search == EXTENDED_ESCAPE) {
                        continue;
                    }

                    if (c == CHAR_TABLE[search]) {
                        baos.write(search);
                        break;
                    }

                    if (c == EXT_CHAR_TABLE[search]) {
                        baos.write(EXTENDED_ESCAPE);
                        baos.write(search);
                        break;
                    }
                }
                if (search == CHAR_TABLE.length) {
                    // A '?' character.
                    baos.write(0x3f);
                }
            }
        } catch (IOException e) {
            // should be an impossible error
            throw new RuntimeException("Impossible error with FastByteArrayOutputStream: " + e.getMessage(), e);
        }

        return baos.toByteArray();

    }

    @Override
    public int estimateDecodeCharLength(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        if (bytes.length < 2) {
            return bytes.length;
        }
        // only a couple chars are expected to be "double" bytes
        return bytes.length + 10;
    }

    /**
     * Decode an SMS default alphabet-encoded octet string into a Java String.
     */
    @Override
    public void decode(byte[] bytes, StringBuilder buffer) {
        if (bytes == null) {
            // append nothing
            return;
        }

        char[] table = CHAR_TABLE;
        for (int i = 0; i < bytes.length; i++) {
            int code = (int)bytes[i] & 0x000000ff;
            if (code == EXTENDED_ESCAPE) {
                // take next char from extension table
                table = EXT_CHAR_TABLE;
            } else {
                buffer.append((code >= table.length) ? '?' : table[code]);
                // go back to the default table
                table = CHAR_TABLE;
            }
        }
    }
}
