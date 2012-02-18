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

import java.text.Normalizer;

/**
 * Utility class for working with text used on mobile phones (primarily SMS).
 * Helpful methods for converting unicode characters into their ascii equivalents
 * such as smart quotes to dumb quotes.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class MobileTextUtil {
    
    // source-char, replace-char
    // http://en.wikipedia.org/wiki/Quotation_mark_glyphs
    static public final char[][] CHAR_TABLE = {
        { '\u2013', '-' },
        { '\u2014', '-' },
        { '\u2018', '\'' },
        { '\u2019', '\'' },
        { '\u201A', '\'' },
        { '\u201B', '\'' }, // U+201B ‛​ single high-reversed-9 quotation mark (HTML: &#8219; ), also called single reversed comma, quotation mark 
        { '\u201C', '"' },
        { '\u201D', '"' },
        { '\u201E', '"' },
        { '\u201F', '"' }, // U+201F ‟​ double high-reversed-9 quotation mark (HTML: &#8223; ), also called double reversed comma, quotation mark
        { '\u2020', '+' },
        { '\u2022', '.' },
        { '\u2026', '.' }, // actually "...", but just replacing with "."
        { '\u2039', '<' },
        { '\u203A', '>' },
        /** deprecated at recommendation by Turkcell - these replacements changed meaning too much */
        //{ '\u0131', '1' }, // U+0131 is a lower case letter dotless i (ı)
        //{ '\u0130', 'i' }, // U+0130 (İ) is capital i with dot
    };


    /**
     * Replace unicode characters with their ascii equivalents, limiting
     * replacement to "safe" characters such as smart quotes to dumb quotes.
     * "Safe" is subjective, but generally the agreement is that these character
     * replacements should not change the meaning of the string in any meaninful
     * way.
     *
     * @param buffer The buffer containing the characters to analyze and replace
     *      if necessary.
     * @return The number of characters replaced
     */
    static public int replaceSafeUnicodeChars(StringBuilder buffer) {
        int replaced = 0;
        for (int i = 0; i < buffer.length(); i++) {
            char c = buffer.charAt(i);
            for (int j = 0; j < CHAR_TABLE.length; j++) {
                if (c == CHAR_TABLE[j][0]) {
                    replaced++;
                    buffer.setCharAt(i, CHAR_TABLE[j][1]);
                }
            }
        }
        return replaced;
    }

    /**
     * Replace accented characters with their ascii equivalents.  For example,
     * convert é to e.<br><br>
     * NOTE: This method is not very efficient.  The String will be copied
     * twice during conversion, so you'll likely only want to run this against
     * small strings.
     *
     * @param buffer The buffer containing the characters to analyze and replace
     *      if necessary.
     * @return The number of characters replaced
     */
    public static int replaceAccentedChars(StringBuilder buffer) {
        // save the size before we strip out the accents
        int sizeBefore = buffer.length();
        // each accented char will be converted into 2 chars -- the ascii version
        // followed by the accent character
        String s = Normalizer.normalize(buffer, Normalizer.Form.NFD);
        // new size will include accented chars
        int sizeAfter = s.length();
        // efficiency check #1 - if the length hasn't changed, do nothing
        int replaced = sizeAfter - sizeBefore;
        if (replaced <= 0) {
            return 0;
        }

        // replace the accents with nothing
        s = s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        buffer.setLength(0);
        buffer.append(s);
        
        return replaced;
    }

}
