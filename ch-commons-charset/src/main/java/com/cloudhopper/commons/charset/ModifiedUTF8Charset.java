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

import com.cloudhopper.commons.charset.CharSequenceAccessor.CharArrayWrapper;

/**
 * <p>Charset representing "Modified UTF-8".  Java originally used 2 byte char
 * primitives to store characters in its Strings.  These were originally encoded
 * in UCS2 -- which let Java natively support ~65K characters in Unicode.  In
 * Java 5, UCS2 is no longer used -- UTF-16 is now used.  This let's Java 
 * natively support the entire range of Unicode characters which can be > 65K.
 * For higher range UTF-16 characters with a Java char value of (> 0x7FFF), this
 * charset does NOT correctly encode these values to the correct UTF-8 byte 
 * sequence.</p>
 * 
 * <p>Its usually quite uncommon in most situations to actually use a character
 * value > 0x7FFF.  This is why this charset exists -- it takes advantage of this
 * property to speed up UTF-8 encoding/decoding of byte arrays.  If you decide
 * to solely use this charset for serialization, you also don't risk any issues
 * with encoding/decoding since the resulting Java String will always be the same
 * as if you actually used UTF-8.</p>
 * 
 * <p>This charset turns out to be very useful for directly encoding/decoding from
 * byte arrays (especially if the byte array is already allocated), where the 
 * default Java classes would force you to create a new byte array.  It also 
 * is ~30% faster than Java at decoding/encoding in most cases.  In some cases
 * it's a little slower.  On average though it usually matches Java and has
 * a good chance at being much faster during decoding.</p>
 * 
 * <p>This charset is originally based on much of the work in DataOuputStream.java
 * and DataInputStream.java with a few notable tweaks:
 *   <ul>
 *      <li>0x0000 is encoded as 0x00 rather than DataOutputStreams 2 byte version.
 *          This is identical to how UTF-8 is actually supposed to work</li>
 *      <li>CharSequenceAccessor utility class uses reflection to access the
 *          private fields in String.java to reduce copying of char arrays.</li>
 *      <li>CharSequenceAccessor utility class uses reflection to access the
 *          private fields in String.java to directly access the underlying
 *          char array vs. ever calling charAt.</li>
 *  </ul>
 *
 * </p>
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class ModifiedUTF8Charset extends BaseCharset {
    
    public ModifiedUTF8Charset() {
        // do nothing
    }

    @Override
    public int estimateEncodeByteLength(CharSequence str0) {
        return calculateByteLength(str0);
    }
    
    @Override
    public int estimateDecodeCharLength(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        return bytes.length;            // decoding will never be more than 1 char = 1 byte
    }

    @Override
    public byte[] encode(CharSequence charSeq) {
        if (charSeq == null) {
            return null;
        }
        CharArrayWrapper wrapper = CharSequenceAccessor.access(charSeq);
        if (wrapper != null) {
            // use more efficient direct access to char array using the wrapper
            int utf8len = calculateByteLength(null, wrapper.value, wrapper.offset, wrapper.length);
            byte[] buf = new byte[utf8len];
            encodeToByteArray(null, wrapper.value, wrapper.offset, wrapper.length, buf, 0);
            return buf;
        } else {
            // encode based on charAt() method (slightly less efficient)
            int utf8len = calculateByteLength(charSeq, null, 0, 0);
            byte[] buf = new byte[utf8len];
            encodeToByteArray(charSeq, null, 0, 0, buf, 0);
            return buf;
        }
    }

    @Override
    public void decode(byte[] bytes, StringBuilder buffer) {
        if (bytes == null) {
            return;
        }
        // expand buffer as necessary to support all possible UTF-8 bytes
        buffer.ensureCapacity(buffer.length()+bytes.length);
        CharArrayWrapper wrapper = CharSequenceAccessor.access(buffer);
        // since we want to mimic an "append", the "length" of the existing char
        // array represents how much data is currently contained inside it
        // we'll start our "append" at that offset
        int charLength = decodeToCharArray(bytes, 0, bytes.length, wrapper.value, wrapper.length);
        // the "wrapper" is merely prepped for reading
        // we'll need to do a final update of it's values
        CharSequenceAccessor.updateStringBuilder(buffer, charLength+wrapper.length);
    }
    
    @Override
    public String decode(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] charBuffer = new char[bytes.length];
        int charLength = decodeToCharArray(bytes, 0, bytes.length, charBuffer, 0);
        // this prevents re-duplicating a char[] that we know will never change
        // its performance impact is more pronounced when decoding larger strings
        return CharSequenceAccessor.createOptimizedString(charBuffer, 0, charLength);
        //return new String(charBuffer, 0, charLength);
    }
    
    public String decode(byte[] bytes, int offset, int length) {
        if (bytes == null) {
            return null;
        }
        char[] charBuffer = new char[bytes.length];
        int charLength = decodeToCharArray(bytes, offset, length, charBuffer, 0);
        // this prevents re-duplicating a char[] that we know will never change
        // its performance impact is more pronounced when decoding larger strings
        return CharSequenceAccessor.createOptimizedString(charBuffer, 0, charLength);
        //return new String(charBuffer, 0, charLength);
    }
    
    /**
     * Highly efficient method for calculating the byte length of
     * a String if it was encoded as modified UTF-8 bytes. Since no byte array
     * is allocated just for calculating the byte length, this method can speed up
     * checks by 90% vs. something like s.getBytes("UTF8").length. This method
     * is adapted from JDK source code for DataOutputStream.java.
     * @param charSeq The character sequence to use for encoding.
     * @return The number of bytes required to represent the String as modified
     *      UTF-8 encoded bytes.
     */
    static public int calculateByteLength(CharSequence charSeq) {
        return calculateByteLength(charSeq, null, 0, 0);
    }
    
    /**
     * Highly efficient method for calculating the byte length of
     * a String if it was encoded as modified UTF-8 bytes. Since no byte array
     * is allocated just for calculating the byte length, this method can speed up
     * checks by 90% vs. something like s.getBytes("UTF8").length. This method
     * is adapted from JDK source code for DataOutputStream.java.
     * @param charSeq The optional character sequence to use for encoding rather
     *      than the provided character buffer. It is always higher performance
     *      to supply a char array vs. use a CharSequence.  Set to null if the
     *      character array is supplied.
     * @param charBuffer The source char array to encode
     * @param charOffset The offset in the source char array to start encode from
     * @param charLength The length from the offset in the source char array to encode
     * @return The number of bytes required to represent the String as modified
     *      UTF-8 encoded bytes.
     */
    static public int calculateByteLength(CharSequence charSeq, char[] charBuffer, int charOffset, int charLength) {
        int c = 0;
        int byteLength = 0;
        int charPos = charOffset;       // start at char offset
        int charAbsLength = charPos + charLength;
        
        if (charBuffer == null) {
            if (charSeq == null) {
                return 0;
            }
            // use charSequence rather than charBuffer
            charOffset = 0;
            charAbsLength = charSeq.length();
        }
        
        for (; charPos < charAbsLength; charPos++) {
            // optimized method for getting char to encode
            if (charBuffer != null) {
                c = charBuffer[charPos];
            } else {
                c = charSeq.charAt(charPos);
            }
            if ((c >= 0x0000) && (c <= 0x007F)) {
                byteLength++;
            } else if (c > 0x07FF) {
                byteLength += 3;
            } else {
                byteLength += 2;
            }
        }
        return byteLength;
    }
    
    /**
     * Encode the string to an array of UTF-8 bytes.  The buffer must be pre-allocated
     * and have enough space to hold the encoded string.
     * @param charSeq The optional character sequence to use for encoding rather
     *      than the provided character buffer. It is always higher performance
     *      to supply a char array vs. use a CharSequence.  Set to null if the
     *      character array is supplied.
     * @param charBuffer The source char array to encode
     * @param charOffset The offset in the source char array to start encode from
     * @param charLength The length from the offset in the source char array to encode
     * @param byteBuffer The destination byte array to encode to
     * @param byteOffset The offset in the destination byte array to start encode to
     * @return The number of bytes written to the destination byte array
     * @see #calculateByteLength(java.lang.CharSequence) 
     */
    static public int encodeToByteArray(CharSequence charSeq, char[] charBuffer, int charOffset, int charLength, byte[] byteBuffer, int byteOffset) {
        int c = 0;
        int bytePos = byteOffset;       // start at byte offset
        int charPos = charOffset;       // start at char offset
        int charAbsLength = charPos + charLength;
        
        if (charBuffer == null) {
            if (charSeq == null) {
                throw new IllegalArgumentException("Both charSeq and charBuffer cannot be null");
            }
            // use charSequence rather than charBuffer
            charOffset = 0;
            charAbsLength = charSeq.length();
        }
        
        // optimized method is only ascii chars used
        for (; charPos < charAbsLength; charPos++) {
            // optimized method for getting char to encode
            if (charBuffer != null) {
                c = charBuffer[charPos];
            } else {
                c = charSeq.charAt(charPos);
            }
            if (!((c >= 0x0000) && (c <= 0x007F)))
                break;
            byteBuffer[bytePos++] = (byte) c;
        }

        for (; charPos < charAbsLength; charPos++) {
            // optimized method for getting char to encode
            if (charBuffer != null) {
                c = charBuffer[charPos];
            } else {
                c = charSeq.charAt(charPos);
            }
            if ((c >= 0x0000) && (c <= 0x007F)) {
                byteBuffer[bytePos++] = (byte) c;
            } else if (c > 0x07FF) {
                byteBuffer[bytePos++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                byteBuffer[bytePos++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                byteBuffer[bytePos++] = (byte) (0x80 | (c & 0x3F));
            } else {
                byteBuffer[bytePos++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                byteBuffer[bytePos++] = (byte) (0x80 | (c & 0x3F));
            }
        }
        
        return (bytePos-byteOffset);
    }
    
    
    static public int decodeToCharArray(byte[] byteBuffer, int byteOffset, int byteLength, char[] charBuffer, int charOffset) {
        int c = 0, char2 = 0, char3 = 0;
        int bytePos = byteOffset;
        int byteAbsLength = byteOffset + byteLength;
        int charPos = charOffset;

        // optimization - do simple conversion of ascii-only chars 
        while (bytePos < byteAbsLength) {
            c = (int) byteBuffer[bytePos] & 0xff;
            if (c > 127)
                break;
            bytePos++;
            charBuffer[charPos++] = (char)c;
        }

        while (bytePos < byteAbsLength) {
            c = (int) byteBuffer[bytePos] & 0xff;
            switch (c >> 4) {
            // cases 0000 thru 0111
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                // 0xxxxxxx
                bytePos++;
                charBuffer[charPos++] = (char)c;
                break;
            // why not case 8, 9, 10, or 11? (are those invalid UTF-8 sequences?
            case 12:
            case 13:
                // 110x xxxx then 10xx xxxx
                bytePos += 2;
                if (bytePos > byteAbsLength)
                    throw new IllegalArgumentException("malformed input: partial character at end");
                char2 = (int) byteBuffer[bytePos - 1];
                if ((char2 & 0xC0) != 0x80)
                    throw new IllegalArgumentException("malformed input around byte " + bytePos);
                charBuffer[charPos++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
                break;
            case 14:
                // 1110 xxxx then 10xx xxxx then 10xx xxxx
                bytePos += 3;
                if (bytePos > byteAbsLength)
                    throw new IllegalArgumentException("malformed input: partial character at end");
                char2 = (int) byteBuffer[bytePos - 2];
                char3 = (int) byteBuffer[bytePos - 1];
                if (((char2 & 0xC0) != 0x80)
                        || ((char3 & 0xC0) != 0x80))
                    throw new IllegalArgumentException("malformed input around byte " + (bytePos - 1));
                charBuffer[charPos++] = (char) (((c & 0x0F) << 12)
                        | ((char2 & 0x3F) << 6) | (char3 & 0x3F));
                break;
            default:
                // 10xx xxxx,  1111 xxxx
                throw new IllegalArgumentException("malformed input around byte " + bytePos);
            }
        }
        
        return (charPos - charOffset);
    }
}
