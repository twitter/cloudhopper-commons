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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * A set of String utilities.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class StringUtil {
    
    private static final String PRINTABLE = ": ~`!@#$%^&*()-_+=/\\,.[]{}|?<>\"'";
    private static final String SAFE = ":~!@#$%^&*()-_+=/\\,.[]{}|?<>";

    // (gt, lt, quot, amp, apos) and then newline, carriage return
    private static final String[][] XML_CHARS = {
        { "&", "&amp;"},
        { "<", "&lt;"},
        { ">", "&gt;"},
        { "\"", "&quot;"},
        { "'", "&apos;"},
        { "\n", "&#10;"},
        { "\r", "&#13;"},
    };

    /**
     * Searches the string for occurrences of the pattern $ENV{key} and
     * attempts to replace this pattern with a value from the System environment
     * obtained using the 'key'.  For example, including "$ENV{USERNAME}" in
     * a string and calling this method would then attempt to replace the entire
     * pattern with the value of the environment variable "USERNAME". The System
     * environment is obtained in Java with a call to System.getenv().  An environment variable is typically
     * defined in the Linux shell or Windows property tabs.  NOTE: A Java System
     * property is not the same as an environment variable.
     * @param string0 The string to perform substitution on such as "Hello $ENV{USERNAME}".
     *      This string may be null, empty, or contain one or more substitutions.
     * @return A string with all occurrences of keys substituted with their
     *      values obtained from the System environment.  Can be null if the
     *      original string was null.
     * @throws SubstitutionException Thrown if a starting string was found, but the
     *      ending string was not.  Also, thrown if a key value was empty such
     *      as using "$ENV{}".  Finally, thrown if the property key was not
     *      found in the properties object (could not be replaced).
     * @see #substituteWithProperties(java.lang.String, java.lang.String, java.lang.String, java.util.Properties) 
     */
    public static String substituteWithEnvironment(String string0) throws SubstitutionException {
        // turn environment into properties
        Properties envProps = new Properties();
        // add all system environment vars to the properties
        envProps.putAll(System.getenv());
        // delegate to other method using the default syntax $ENV{<key>}
        return substituteWithProperties(string0, "$ENV{", "}", envProps);
    }


    /**
     * Searches string for occurrences of a pattern, extracts out a key name
     * between the startStr and endStr tokens, then attempts to replace the
     * property value into the string.  This method is useful for merging
     * property values into configuration strings/settings.  For examle, the
     * system environment Map could be converted into a Properties object then
     * have its values merged into a String so that users can access environment
     * variables.
     * @param string0 The string to perform substitution on such as "Hello $ENV{TEST}".
     *      This string may be null, empty, or contain one or more substitutions.
     * @param startStr The string that marks the start of a replacement key such
     *      as "$ENV{" if the final search pattern you wanted was "$ENV{<key>}".
     * @param endStr The string that marks the end of a replacement key such
     *      as "}" if the final search pattern you wanted was "$ENV{<key>}".
     * @param properties The property keys and associated values to use for
     *      replacement.
     * @return A string with all occurrences of keys substituted with their
     *      values obtained from the properties object.  Can be null if the
     *      original string was null.
     * @throws SubstitutionException Thrown if a starting string was found, but the
     *      ending string was not.  Also, thrown if a key value was empty such
     *      as using "$ENV{}".  Finally, thrown if the property key was not
     *      found in the properties object (could not be replaced).
     * @see #substituteWithEnvironment(java.lang.String) 
     */
    public static String substituteWithProperties(String string0, String startStr, String endStr, Properties properties) throws SubstitutionException {
        // a null source string will always return the same -- a null result
        if (string0 == null) {
            return null;
        }

        // create a builder for the resulting string
        StringBuilder result = new StringBuilder(string0.length());

        // attempt to find the first occurrence of the starting string
        int end = -1;
        int pos = string0.indexOf(startStr);
        
        // keep looping while we keep finding more occurrences
        while (pos >= 0) {
            // is there string data before the position that we should append to the result?
            if (pos > end+1) {
                result.append(string0.substring(end+1, pos));
            }

            // search for endStr starting from the end of the startStr
            end = string0.indexOf(endStr, pos+startStr.length());

            // was the end found?
            if (end < 0) {
                throw new SubstitutionException("End of substitution pattern '" + endStr + "' not found [@position=" + pos + "]");
            }

            // extract the part in the middle of the start and end strings
            String key = string0.substring(pos+startStr.length(), end);
            // NOTE: don't trim the key, whitespace technically matters...

            // was there anything left?
            if (key == null || key.equals("")) {
                throw new SubstitutionException("Property key was empty in string with an occurrence of '" + startStr + endStr + "' [@position=" + pos + "]");
            }

            // attempt to get this property
            String value = properties.getProperty(key);
            // was the property found
            if (value == null) {
                throw new SubstitutionException("A property value for '" + startStr + key + endStr + "' was not found (property missing?)");
            }

            // append this value to our result
            result.append(value);

            // find next occurrence after last end
            pos = string0.indexOf(startStr, end+1);
        }

        // is there any string data we missed in the loop above?
        if (end+1 < string0.length()) {
            // append the remaining part of the string
            result.append(string0.substring(end+1));
        }

        return result.toString();
    }


    /**
     * Returns true if the String is considered a "safe" string where only specific
     * characters are allowed to be used.  Useful for checking passwords or other
     * information you don't want a user to be able to type just anything in.
     * This method does not allow any whitespace characters, newlines, carriage returns.
     * Primarily allows [a-z] [A-Z] [0-9] and a few other useful ASCII characters
     * such as ":~!@#$%^*()-_+=/\\,.[]{}|?<>" (but not the quote chars)
    */
    public static boolean isSafeString(String string0) {
        for (int i = 0; i < string0.length(); i++) {
            if (!isSafeChar(string0.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the char is considered a "safe" char. Please see
     * documentation for isSafeString().
     * @see #isSafeString(java.lang.String) 
    */
    public static boolean isSafeChar(char ch) {
        if (ch >= 'a' && ch <= 'z')
            return true;
        if (ch >= 'A' && ch <= 'Z')
          return true;
        if (ch >= '0' && ch <= '9')
          return true;
        // loop thru our PRINTABLE string
        for (int i = 0; i < SAFE.length(); i++) {
            if (ch == SAFE.charAt(i))
                return true;
        }
        return false;
    }

    /**
     * Safely capitalizes a string by converting the first character to upper
     * case. Handles null, empty, and strings of length of 1 or greater.  For
     * example, this will convert "joe" to "Joe".  If the string is null, this
     * will return null.  If the string is empty such as "", then it'll just
     * return an empty string such as "".
     * @param string0 The string to capitalize
     * @return A new string with the first character converted to upper case
     */
    static public String capitalize(String string0) {
        if (string0 == null) {
            return null;
        }
        int length = string0.length();
        // if empty string, just return it
        if (length == 0) {
            return string0;
        } else if (length == 1) {
            return string0.toUpperCase();
        } else {
            StringBuilder buf = new StringBuilder(length);
            buf.append(string0.substring(0, 1).toUpperCase());
            buf.append(string0.substring(1));
            return buf.toString();
        }
    }

    /**
     * Safely uncapitalizes a string by converting the first character to lower
     * case. Handles null, empty, and strings of length of 1 or greater.  For
     * example, this will convert "Joe" to "joe".  If the string is null, this
     * will return null.  If the string is empty such as "", then it'll just
     * return an empty string such as "".
     * @param string0 The string to uncapitalize
     * @return A new string with the first character converted to lower case
     */
    static public String uncapitalize(String string0) {
        if (string0 == null) {
            return null;
        }
        int length = string0.length();
        // if empty string, just return it
        if (length == 0) {
            return string0;
        } else if (length == 1) {
            return string0.toLowerCase();
        } else {
            StringBuilder buf = new StringBuilder(length);
            buf.append(string0.substring(0, 1).toLowerCase());
            buf.append(string0.substring(1));
            return buf.toString();
        }
    }

    /**
     * Checks if the targetString is contained within the array of strings. This
     * method will return true if a "null" is contained in the array and the
     * targetString is also null.
     * @param strings The array of strings to search.
     * @param targetString The string to search for
     * @return True if the string is contained within, otherwise false. Also
     *      returns false if the strings array is null.
     */
    static public boolean contains(String[] strings, String targetString) {
        return (indexOf(strings, targetString) != -1);
    }

    /**
     * Finds the first occurrence of the targetString in the array of strings.
     * Returns -1 if an occurrence wasn't found. This
     * method will return true if a "null" is contained in the array and the
     * targetString is also null.
     * @param strings The array of strings to search.
     * @param targetString The string to search for
     * @return The index of the first occurrence, or -1 if not found. If strings
     *      array is null, will return -1;
     */
    static public int indexOf(String[] strings, String targetString) {
        if (strings == null)
            return -1;
        for (int i = 0; i < strings.length; i++) {
            if (strings[i] == null) {
                if (targetString == null) {
                    return i;
                }
            } else {
                if (targetString != null) {
                    if (strings[i].equals(targetString)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }


    /**
     * If present, this method will strip off the leading and trailing "
     * character in the string parameter.  For example, "10958" will
     * becomes just 10958.
     */
    static public String stripQuotes(String string0) {
        // if an empty string, return it
        if (string0.length() == 0) {
            return string0;
        }
        // if the first and last characters are quotes, just do 1 substring
        if (string0.length() > 1 && string0.charAt(0) == '"' && string0.charAt(string0.length() - 1) == '"') {
            return string0.substring(1, string0.length() - 1);
        } else if (string0.charAt(0) == '"') {
            string0 = string0.substring(1);
        } else if (string0.charAt(string0.length() - 1) == '"') {
            string0 = string0.substring(0, string0.length() - 1);
        }
        return string0;
    }


    /**
     * Checks if a string contains only digits.
     * @return True if the string0 only contains digits, or false otherwise.
     */
    static public boolean containsOnlyDigits(String string0) {
        // are they all digits?
        for (int i = 0; i < string0.length(); i++) {
            if (!Character.isDigit(string0.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    
    /**
     * Splits a string around matches of the given delimiter character.
     *
     * Where applicable, this method can be used as a substitute for
     * <code>String.split(String regex)</code>, which is not available
     * on a JSR169/Java ME platform.
     *
     * @param str the string to be split
     * @param delim the delimiter
     * @throws NullPointerException if str is null
     */
    static public String[] split(String str, char delim) {
        if (str == null) {
            throw new NullPointerException("str can't be null");
        }

        // Note the javadoc on StringTokenizer:
        //     StringTokenizer is a legacy class that is retained for
        //     compatibility reasons although its use is discouraged in
        //     new code.
        // In other words, if StringTokenizer is ever removed from the JDK,
        // we need to have a look at String.split() (or java.util.regex)
        // if it is supported on a JSR169/Java ME platform by then.
        StringTokenizer st = new StringTokenizer(str, String.valueOf(delim));
        int n = st.countTokens();
        String[] s = new String[n];
        for (int i = 0; i < n; i++) {
            s[i] = st.nextToken();
        }
        return s;
    }

    /**
     * Used to print out a string for error messages,
     * chops is off at 60 chars for historical reasons.
     */
    public final static String formatForPrint(String input) {
        if (input.length() > 60) {
            StringBuffer tmp = new StringBuffer(input.substring(0, 60));
            tmp.append("&");
            input = tmp.toString();
        }
        return input;
    }

    /**
     * A method that receive an array of Objects and return a
     * String array representation of that array.
     */
    public static String[] toStringArray(Object[] objArray) {
        int idx;
        int len = objArray.length;
        String[] strArray = new String[len];

        for (idx = 0; idx < len; idx++) {
            strArray[idx] = objArray[idx].toString();
        }

        return strArray;
    }

	/**
		Get 7-bit ASCII character array from input String.
		The lower 7 bits of each character in the input string is assumed to be
		the ASCII character value.

     Hexadecimal - Character

     | 00 NUL| 01 SOH| 02 STX| 03 ETX| 04 EOT| 05 ENQ| 06 ACK| 07 BEL|
     | 08 BS | 09 HT | 0A NL | 0B VT | 0C NP | 0D CR | 0E SO | 0F SI |
     | 10 DLE| 11 DC1| 12 DC2| 13 DC3| 14 DC4| 15 NAK| 16 SYN| 17 ETB|
     | 18 CAN| 19 EM | 1A SUB| 1B ESC| 1C FS | 1D GS | 1E RS | 1F US |
     | 20 SP | 21  ! | 22  " | 23  # | 24  $ | 25  % | 26  & | 27  ' |
     | 28  ( | 29  ) | 2A  * | 2B  + | 2C  , | 2D  - | 2E  . | 2F  / |
     | 30  0 | 31  1 | 32  2 | 33  3 | 34  4 | 35  5 | 36  6 | 37  7 |
     | 38  8 | 39  9 | 3A  : | 3B  ; | 3C  < | 3D  = | 3E  > | 3F  ? |
     | 40  @ | 41  A | 42  B | 43  C | 44  D | 45  E | 46  F | 47  G |
     | 48  H | 49  I | 4A  J | 4B  K | 4C  L | 4D  M | 4E  N | 4F  O |
     | 50  P | 51  Q | 52  R | 53  S | 54  T | 55  U | 56  V | 57  W |
     | 58  X | 59  Y | 5A  Z | 5B  [ | 5C  \ | 5D  ] | 5E  ^ | 5F  _ |
     | 60  ` | 61  a | 62  b | 63  c | 64  d | 65  e | 66  f | 67  g |
     | 68  h | 69  i | 6A  j | 6B  k | 6C  l | 6D  m | 6E  n | 6F  o |
     | 70  p | 71  q | 72  r | 73  s | 74  t | 75  u | 76  v | 77  w |
     | 78  x | 79  y | 7A  z | 7B  { | 7C  | | 7D  } | 7E  ~ | 7F DEL|

	 */
    public static byte[] getAsciiBytes(String input) {
        char[] c = input.toCharArray();
        byte[] b = new byte[c.length];
        for (int i = 0; i < c.length; i++) {
            b[i] = (byte) (c[i] & 0x007F);
        }

        return b;
    }

    public static String getAsciiString(byte[] input) {
        StringBuffer buf = new StringBuffer(input.length);
        for (byte b : input) {
            buf.append((char) b);
        }
        return buf.toString();
    }

    /**
     * Trim off trailing blanks but not leading blanks
     * @param str
     * @return The input with trailing blanks stipped off
     */
    public static String trimTrailing(String str) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        for (; len > 0; len--) {
            if (!Character.isWhitespace(str.charAt(len - 1))) {
                break;
            }
        }
        return str.substring(0, len);
    }

    /**
    Truncate a String to the given length with no warnings
    or error raised if it is bigger.

    @param	value String to be truncated
    @param	length	Maximum length of string

    @return Returns value if value is null or value.length() is less or equal to than length, otherwise a String representing
    value truncated to length.
     */
    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }

    /**
     * Return a slice (substring) of the passed in value, optionally trimmed.
     * WARNING - endOffset is inclusive for historical reasons, unlike
     * String.substring() which has an exclusive ending offset.
     * @param value Value to slice, must be non-null.
     * @param beginOffset Inclusive start character
     * @param endOffset Inclusive end character
     * @param trim To trim or not to trim
     * @return Sliceed value.
     */
    public static String slice(String value,
            int beginOffset, int endOffset,
            boolean trim) {
        String retval = value.substring(beginOffset, endOffset + 1);

        if (trim) {
            retval = retval.trim();
        }

        return retval;
    }

    public static char[] HEX_TABLE = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * @deprecated Please use new utility class HexUtil
     * @see HexUtil#toHexString(byte[])
     */
    @Deprecated
    public static String toHexString(byte[] bytes) {
        return HexUtil.toHexString(bytes);
    }

    /**
     * @deprecated Please use new utility class HexUtil
     * @see HexUtil#toHexString(byte[], int, int)
     */
    @Deprecated
    public static String toHexString(byte[] bytes, int offset, int length) {
        return HexUtil.toHexString(bytes, offset, length);
    }

    /**
     * @deprecated Please use new utility class HexUtil
     * @see HexUtil#toByteArray(java.lang.CharSequence, int, int)
     */
    @Deprecated
    public static byte[] toHexByte(String hexString, int offset, int length) {
        return HexUtil.toByteArray(hexString, offset, length);
    }

    /**
     * Converts from a hex string like "FF" to a byte[].
     * @param string0 The string containing the hex-encoded data.
     * @return
     * @deprecated Please use new utility class HexUtil
     * @see HexUtil#toByteArray(java.lang.CharSequence) 
     */
    @Deprecated
    public static byte[] fromHexString(String hexString) {
        return HexUtil.toByteArray(hexString);
    }

    /**
     * Convert a hexidecimal string generated by toHexString() back into a byte array.
     * @param s String to convert
     * @param offset starting character (zero based) to convert.
     * @param length number of characters to convert.
     * @return the converted byte array. Returns null if the length is not a multiple of 2.
     * @deprecated Please use new utility class HexUtil
     * @see HexUtil#toByteArray(java.lang.CharSequence, int, int) 
     */
    @Deprecated
    public static byte[] fromHexString(String hexString, int offset, int length) {
        return HexUtil.toByteArray(hexString, offset, length);
    }


    /**
     * Return true if the character is printable in ASCII. Not using
     * Character.isLetterOrDigit(); applies to all unicode ranges.
     */
    public static boolean isPrintableChar(char ch) {
        if (ch >= 'a' && ch <= 'z') {
            return true;
        }
        if (ch >= 'A' && ch <= 'Z') {
            return true;
        }
        if (ch >= '0' && ch <= '9') {
            return true;
        }

        // loop thru our PRINTABLE string
        for (int i = 0; i < PRINTABLE.length(); i++) {
            if (ch == PRINTABLE.charAt(i)) {
                return true;
            }
        }

        return false;
    }
    
	   /**
    Convert a byte array to a human-readable String for debugging purposes.
     */
    public static String hexDump(String prefix, byte[] data) {
        byte byte_value;

        StringBuffer str = new StringBuffer(data.length * 3);

        str.append(prefix);

        for (int i = 0; i < data.length; i += 16) {
            // dump the header: 00000000:
            String offset = Integer.toHexString(i);

            // "0" left pad offset field so it is always 8 char's long.
            str.append("  ");
            for (int offlen = offset.length(); offlen < 8; offlen++) {
                str.append("0");
            }
            str.append(offset);
            str.append(":");

            // dump hex version of 16 bytes per line.
            for (int j = 0; (j < 16) && ((i + j) < data.length); j++) {
                byte_value = data[i + j];

                // add spaces between every 2 bytes.
                if ((j % 2) == 0) {
                    str.append(" ");
                }

                // dump a single byte.
                byte high_nibble = (byte) ((byte_value & 0xf0) >>> 4);
                byte low_nibble = (byte) (byte_value & 0x0f);

                str.append(HEX_TABLE[high_nibble]);
                str.append(HEX_TABLE[low_nibble]);
            }

            // IF THIS IS THE LAST LINE OF HEX, THEN ADD THIS
            if (i + 16 > data.length) {
                // for debugging purposes, I want the last bytes always padded
                // over so that the ascii portion is correctly positioned
                int last_row_byte_count = data.length % 16;
                int num_bytes_short = 16 - last_row_byte_count;
                // number of spaces to add = (num bytes remaining * 2 spaces per byte) + (7 - (num bytes % 2))
                int num_spaces = (num_bytes_short * 2) + (7 - (last_row_byte_count / 2));
                for (int v = 0; v < num_spaces; v++) {
                    str.append(" ");
                }
            }

            // dump ascii version of 16 bytes
            str.append("  ");

            for (int j = 0; (j < 16) && ((i + j) < data.length); j++) {
                char char_value = (char) data[i + j];

                // RESOLVE (really want isAscii() or isPrintable())
                //if (Character.isLetterOrDigit(char_value))
                if (isPrintableChar(char_value)) {
                    str.append(String.valueOf(char_value));
                } else {
                    str.append(".");
                }
            }

            // new line
            str.append("\n");
        }

        // always trim off the last newline
        str.deleteCharAt(str.length() - 1);

        return (str.toString());
    }

	   // The functions below are used for uppercasing SQL in a consistent manner.
    // Derby will uppercase Turkish to the English locale to avoid i
    // uppercasing to an uppercase dotted i. In future versions, all
    // casing will be done in English.   The result will be that we will get
    // only the 1:1 mappings  in
    // http://www.unicode.org/Public/3.0-Update1/UnicodeData-3.0.1.txt
    // and avoid the 1:n mappings in
    //http://www.unicode.org/Public/3.0-Update1/SpecialCasing-3.txt
    //
    // Any SQL casing should use these functions
    /** Convert string to uppercase
     * Always use the java.util.ENGLISH locale
     * @param s   string to uppercase
     * @return uppercased string
     */
    public static String SQLToUpperCase(String s) {
        return s.toUpperCase(Locale.ENGLISH);
    }

    /** Compares two strings
     * Strings will be uppercased in english and compared
     * equivalent to s1.equalsIgnoreCase(s2)
     * throws NPE if s1 is null
     *
     * @param s1  first string to compare
     * @param s2  second string to compare
     *
     * @return   true if the two upppercased ENGLISH values are equal
     *           return false if s2 is null
     */
    public static boolean SQLEqualsIgnoreCase(String s1, String s2) {
        if (s2 == null) {
            return false;
        } else {
            return SQLToUpperCase(s1).equals(SQLToUpperCase(s2));
        }

    }

    /**
     * Normalize a SQL identifer, up-casing if <regular identifer>,
     * and handling of <delimited identifer> (SQL 2003, section 5.2).
     * The normal form is used internally in Derby.
     *
     * @param id syntacically correct SQL identifier
     */
    public static String normalizeSQLIdentifier(String id) {
        if (id.length() == 0) {
            return id;
        }

        if (id.charAt(0) == '"'
                && id.length() >= 3
                && id.charAt(id.length() - 1) == '"') {
            // assume syntax is OK, thats is, any quotes inside are doubled:

            return StringUtil.compressQuotes(
                    id.substring(1, id.length() - 1), "\"\"");

        } else {
            return StringUtil.SQLToUpperCase(id);
        }
    }

    /**
     * Compress 2 adjacent (single or double) quotes into a single (s or d)
     * quote when found in the middle of a String.
     *
     * NOTE:  """" or '''' will be compressed into "" or ''.
     * This function assumes that the leading and trailing quote from a
     * string or delimited identifier have already been removed.
     * @param source string to be compressed
     * @param quotes string containing two single or double quotes.
     * @return String where quotes have been compressed
     */
    public static String compressQuotes(String source, String quotes) {
        String result = source;
        int index;

        /* Find the first occurrence of adjacent quotes. */
        index = result.indexOf(quotes);

        /* Replace each occurrence with a single quote and begin the
         * search for the next occurrence from where we left off.
         */
        while (index != -1) {
            result = result.substring(0, index + 1)
                    + result.substring(index + 2);
            index = result.indexOf(quotes, index + 1);
        }

        return result;
    }

    /**
     * Quote a string so that it can be used as an identifier or a string
     * literal in SQL statements. Identifiers are surrounded by double quotes
     * and string literals are surrounded by single quotes. If the string
     * contains quote characters, they are escaped.
     *
     * @param source the string to quote
     * @param quote the character to quote the string with (' or &quot;)
     * @return a string quoted with the specified quote character
     * @see #quoteStringLiteral(String)
     * @see IdUtil#normalToDelimited(String)
     */
    static String quoteString(String source, char quote) {
        // Normally, the quoted string is two characters longer than the source
        // string (because of start quote and end quote).
        StringBuffer quoted = new StringBuffer(source.length() + 2);
        quoted.append(quote);
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            // if the character is a quote, escape it with an extra quote
            if (c == quote) quoted.append(quote);
            quoted.append(c);
        }
        quoted.append(quote);
        return quoted.toString();
    }

    /**
     * Quote a string so that it can be used as a string literal in an
     * SQL statement.
     *
     * @param string the string to quote
     * @return the string surrounded by single quotes and with proper escaping
     * of any single quotes inside the string
     */
    public static String quoteStringLiteral(String string) {
        return quoteString(string, '\'');
    }

    /**
     * Turn an array of ints into a printable string. Returns what's returned
     * in Java 5 by java.util.Arrays.toString(int[]).
     */
    public static String stringify(int[] raw) {
        if (raw == null) {
            return "null";
        }

        StringBuffer buffer = new StringBuffer();
        int count = raw.length;

        buffer.append("[ ");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(raw[i]);
        }
        buffer.append(" ]");

        return buffer.toString();
    }

    /**
     * Checks if the string is an empty value which is true if the string is
     * null or if the string represents an empty string of "".  Please note that
     * a string with just a space " " would not be considered empty.
     * @param string0 The string to check
     * @return True if null or "", otherwise false.
     */
    public static boolean isEmpty(String string0) {
        if (string0 == null || string0.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEqual(String string0, String string1) {
        return isEqual(string0, string1, true);
    }

    /**
     * Returns the value from calling "toString()" on the object, but is a safe
     * version that gracefully handles NULL objects by returning a String of "".
     * @param obj The object to call toString() on. Safely handles a null object.
     * @return The value from obj.toString() or "" if the object is null.
     * @see #toStringWithNullAsNull(java.lang.Object) 
     */
    static public String toStringWithNullAsEmpty(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return obj.toString();
        }
    }

    /**
     * Returns the value from calling "toString()" on the object, but is a safe
     * version that gracefully handles NULL objects by returning a String of "<NULL>".
     * @param obj The object to call toString() on. Safely handles a null object.
     * @return The value from obj.toString() or "<NULL>" if the object is null.
     * @see #toStringWithNullAsEmpty(java.lang.Object)
     */
    static public String toStringWithNullAsReplaced(Object obj) {
        if (obj == null) {
            return "<NULL>";
        } else {
            return obj.toString();
        }
    }

    /**
     * Returns the value from calling "toString()" on the object, but is a safe
     * version that gracefully handles NULL objects by returning null (rather
     * than causing a NullPointerException).
     * @param obj The object to call toString() on. Safely handles a null object.
     * @return The value from obj.toString() or null if the object is null.
     * @see #toStringWithNullAsEmpty(java.lang.Object)
     */
    static public String toStringWithNullAsNull(Object obj) {
        if (obj == null) {
            return null;
        } else {
            return obj.toString();
        }
    }

    /**
     * Checks if both strings are equal to each other.  Safely handles the case
     * where either string may be null.  The strings are evaluated as equal if
     * they are both null or if they actually equal each other.  One string
     * that is null while the other one isn't (even if its an empty string) will
     * be considered as NOT equal.  Case sensitive comparisons are optional.
     * @param string0 The string to compare
     * @param string1 The other string to compare with
     * @param caseSensitive If true a case sensitive comparison will be made,
     *      otherwise equalsIgnoreCase will be used.
     * @return True if the strings are both null or equal to each other, otherwise
     *      false.
     */
    public static boolean isEqual(String string0, String string1, boolean caseSensitive) {
        if (string0 == null && string1 == null) {
            return true;
        }
        if (string0 == null && string1 != null) {
            return false;
        }
        if (string0 != null && string1 == null) {
            return false;
        }
        if (caseSensitive) {
            return string0.equals(string1);
        } else {
            return string0.equalsIgnoreCase(string1);
        }
    }


    public static String readToString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    /**
     * Escapes the characters in a String using XML entities.
     * For example: "bread" & "butter'ed" => &quot;bread&quot; &amp; &quot;butter&apos;ed&quot;
     * 
     * Supports the five basic XML entities (gt, lt, quot, amp, apos) and also
     * supports a newline and carriage return character.  A newline is escaped
     * to &#10; and a carriage return to &#13;
     * 
     * @param value The string to escape
     * @return The escaped String that can be used in an XML document.
     */
    public static String escapeXml(String value) {
        // null to null
        if (value == null)
            return null;

        // assume the resulting string will be the same
        int len = value.length();
        StringBuilder buf = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            boolean entityFound = false;
            // is this a matching entity?
            for (int j = 0; j < XML_CHARS.length; j++) {
                // is this the matching character?
                if (c == XML_CHARS[j][0].charAt(0)) {
                    // append the entity
                    buf.append(XML_CHARS[j][1]);
                    entityFound = true;
                }
            }
            if (!entityFound) {
                buf.append(c);
            }
        }
        return buf.toString();
    }


    /**
     * Removes all other characters from a string except digits.  A good way
     * of cleaing up something like a phone number.
     * @param str0 The string to clean up
     * @return A new String that has all characters except digits removed
     */
    static public String removeAllCharsExceptDigits(String str0) {
        if (str0 == null) {
            return null;
        }
        if (str0.length() == 0) {
            return str0;
        }
        StringBuilder buf = new StringBuilder(str0.length());
        int length = str0.length();
        for (int i = 0; i < length; i++) {
            char c = str0.charAt(i);
            if (Character.isDigit(c)) {
                // append this character to our string
                buf.append(c);
            }
        }
        return buf.toString();
    }

}

