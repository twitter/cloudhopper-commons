package com.cloudhopper.sxmp.util;

/*
 * #%L
 * ch-sxmp
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
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
 *
 * @author dhoffman
 */
public class XmlEscapeUtil {

    // (lt, gt, amp) only these need to be escaped in non-attribute text data
    private static final String[][] XML_TEXT_CHARS = {
        { "&", "&amp;"},
        { "<", "&lt;"},
        { ">", "&gt;"}
	/* why escape these?
	   { "\n", "&#10;"},
	   { "\r", "&#13;"},
	*/
    };

    // (gt, lt, quot, amp, apos) and then newline, carriage return
    private static final String[][] XML_ATTRIBUTE_CHARS = {
        { "&", "&amp;"},
        { "<", "&lt;"},
        { ">", "&gt;"},
        { "\"", "&quot;"},
        { "'", "&apos;"}
	/* why escape these?
	   { "\n", "&#10;"},
	   { "\r", "&#13;"},
	*/
    };

    /**
     * Escapes the characters in a String using XML entities.
     * For example: a&b < c > d => a&amp;b &lt; c &gt; d
     *
     * Supports only XML entities which must be escaped in non-attribute
     * text (gt, lt, amp)
     *
     * @param value The string to escape
     * @return The escaped String that can be used in an XML document.
     */
    public static String escapeTextXml(String value) {
        return escapeXml(value, XML_TEXT_CHARS);
    }

    /**
     * Escapes the characters in a String using XML entities.
     * For example: "bread" & "butter'ed" => &quot;bread&quot; &amp; &quot;butter&apos;ed&quot;
     *
     * Supports the five basic XML entities (gt, lt, quot, amp, apos)
     *
     * @param value The string to escape
     * @return The escaped String that can be used in an XML document.
     */
    public static String escapeAttributeXml(String value) {
        return escapeXml(value, XML_ATTRIBUTE_CHARS);
    }

    private static String escapeXml(String text, String[][] mapping) {
        // null to null
        if (text == null)
            return null;

        // assume the resulting string will be the same
        int len = text.length();
        StringBuilder buf = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            boolean entityFound = false;
            // is this a matching entity?
            for (int j = 0; j < mapping.length; j++) {
                // is this the matching character?
                if (c == mapping[j][0].charAt(0)) {
                    // append the entity
                    buf.append(mapping[j][1]);
                    entityFound = true;
                }
            }
            if (!entityFound) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

}
