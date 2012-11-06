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

import com.cloudhopper.commons.util.FastByteArrayOutputStream;
import java.io.IOException;

/**
 * The <b>TmobileNlGSMCharset</b> class handles a charset unique to the "T-Mobile Netherlands"
 * SMSC vendor.  
 * T-Mobile is basically GSM, but they only support one of the characters from the 
 * extended table, and they encode it differently (when encoded to hex string)
 * 
 * @author dhoffman
 */
public class TMobileNlGSMCharset extends GSMCharset {

    static final int TMO_EURO_BYTE = 0x80;
    static final char EURO_MARK = '\u20AC';

    /**
     * Verifies that this charset can represent every character in the Java
     * String (char sequence).
     * @param str0 The String to verify
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
            // simple range checks for most common characters (0x20 -> 0x5A) or (0x61 -> 0x7A)
            if ((c >= ' ' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                continue;
            } else {
                // 10X more efficient using a switch statement vs. a lookup table search
                switch (c) {
                    case '_':	
                    case '\u00A3':	// £
                    case '\u00A5':	// ¥
                    case '\u00E8':	// è
                    case '\u00E9':	// é
                    case '\u00F9':	// ù
                    case '\u00EC':	// ì
                    case '\u00F2':	// ò
                    case '\u00C7':	// Ç
                    case '\n':          // newline
                    case '\u00D8':	// Ø
                    case '\u00F8':	// ø
                    case '\r':          // carriage return
                    case '\u00C5':	// Å
                    case '\u00E5':	// å
                    case '\u0394':	// Δ
                    case '\u03A6':	// Φ
                    case '\u0393':	// Γ
                    case '\u039B':	// Λ
                    case '\u03A9':	// Ω
                    case '\u03A0':	// Π
                    case '\u03A8':	// Ψ
                    case '\u03A3':	// Σ
                    case '\u0398':	// Θ
                    case '\u039E':	// Ξ
                    case '\u00C6':	// Æ
                    case '\u00E6':	// æ
                    case '\u00DF':	// ß
                    case '\u00C9':	// É
                    case '\u00A4':	// ¤
                    case '\u00A1':	// ¡
                    case '\u00C4':	// Ä
                    case '\u00D6':	// Ö
                    case '\u00D1':	// Ñ
                    case '\u00DC':	// Ü
                    case '\u00A7':	// §
                    case '\u00BF':	// ¿
                    case '\u00E4':	// ä
                    case '\u00F6':	// ö
                    case '\u00F1':	// ñ
                    case '\u00FC':	// ü
                    case '\u00E0':	// à
                    case '\u20AC':	// €
                        continue;
                    default:
                        return false;
                }
            }
        }
        return true;
    }
        

    @Override
    public byte[] encode(CharSequence str0) {
        if (str0 == null) {
            return null;
        }

        // T-Mo-NL doesn't use multi-byte encoding, so encoded length = input length
        int estimatedByteLength = str0.length();
        FastByteArrayOutputStream baos = new FastByteArrayOutputStream(estimatedByteLength);

        try {
            int len = str0.length();
            for (int i = 0; i < len; i++) {
                int search = 0;
                char c = str0.charAt(i);

                // T-Mo-NL's one extended char is Euro mark, encoded as 0x80
                if (c == EURO_MARK)
                    baos.write(TMO_EURO_BYTE);
                else {
                    for (; search < CHAR_TABLE.length; search++) {
                        if (search == EXTENDED_ESCAPE) {
                            continue;
                        }

                        if (c == CHAR_TABLE[search]) {
                            baos.write(search);
                            break;
                        }
                    }
                    if (search == CHAR_TABLE.length) {
                        // A '?' character.
                        baos.write(0x3f);
                    }
                }
            }
        } catch (IOException e) {
            // should be an impossible error
            throw new RuntimeException("Impossible error with FastByteArrayOutputStream: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    @Override
    public void decode(byte[] bytes, StringBuilder buffer) {
        if (bytes == null) {
            // append nothing
            return;
        }

        char[] table = CHAR_TABLE;
        for (int i = 0; i < bytes.length; i++) {
            int code = (int)bytes[i] & 0x000000ff;
            if (code == TMO_EURO_BYTE) {
                buffer.append(EURO_MARK);
            } else {
                buffer.append((code >= table.length) ? '?' : table[code]);
            }
        }
    }

}
