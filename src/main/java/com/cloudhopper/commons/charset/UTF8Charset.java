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

/**
 * Charset for UTF-8.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class UTF8Charset extends JavaCharset {
    
    public UTF8Charset() {
        super("UTF8");
    }

    @Override
    public int estimateEncodeByteLength(CharSequence str0) {
        if (str0 == null) {
            return 0;
        }
        // let's double the estimate
        return str0.length() * 2;
    }

    @Override
    public int estimateDecodeCharLength(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        // best guess would be 8-bit chars
        return bytes.length;
    }

    /**
     * Highly efficient and performant method for calculating the byte length of
     * a String if it was encoded as UTF-8 bytes. Since no byte array is allocated
     * just for getting the byte length, this method is proven to speed up
     * checks by 90% vs. something like s.getBytes("UTF8").length.
     * @param s The String to calculate the UTF-8 byte length from
     * @return The number of bytes required to represent the String
     * @see http://mail-archives.apache.org/mod_mbox/incubator-thrift-commits/201004.mbox/%3C20100425152011.3DFA123888FE@eris.apache.org%3E
     */
    public static int calculateByteLength(final String s) {
        if (s == null) {
            return 0;
        }
        int byteLength = 0;
        int c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c <= 0x007F) {
                byteLength++;
            } else if (c > 0x07FF) {
                byteLength += 3;
            } else {
                byteLength += 2;
            }
        }
        return byteLength;
    }


/** THESE METHODS MAY ALL PROVE TO BE FASTER THAN DEFAULT JVM IMPL
 * http://mail-archives.apache.org/mod_mbox/incubator-thrift-commits/201004.mbox/%3C20100425152011.3DFA123888FE@eris.apache.org%3E
 *
 * tested this out -- definitely cuts down conversions by 50%
    
  public static byte[] encode(String s) {
    byte[] buf = new byte[calculateByteLength(s)];
    encode(s, buf, 0);
    return buf;
  }

  public static void encode(String s, byte[] buf, int offset) {
    int nextByte = 0;
    int c;
    for (int i = 0; i < s.length(); i++) {
      c = s.charAt(i);
      if (c <= 0x007F) {
        buf[offset + nextByte] = (byte)c;
        nextByte++;
      } else if (c > 0x07FF) {
        buf[offset + nextByte    ] = (byte)(0xE0 | c >> 12 & 0x0F);
        buf[offset + nextByte + 1] = (byte)(0x80 | c >>  6 & 0x3F);
        buf[offset + nextByte + 2] = (byte)(0x80 | c       & 0x3F);
        nextByte+=3;
      } else {
        buf[offset + nextByte    ] = (byte)(0xC0 | c >> 6 & 0x1F);
        buf[offset + nextByte + 1] = (byte)(0x80 | c      & 0x3F);
        nextByte+=2;
      }
    }
  }

  /**
  public static String decode(byte[] buf) {
    return decode(buf, 0, buf.length);
  }

  public static String decode(byte[] buf, int offset, int byteLength) {
    int charCount = 0;
    char[] chars = new char[byteLength];
    int c;
    int byteIndex = offset;
    int charIndex = 0;
    while (byteIndex < offset + byteLength) {
      c = buf[byteIndex++] & 0xFF;
      switch (c >> 4) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
          chars[charIndex++] = (char) c;
          break;
        case 12:
        case 13:
          chars[charIndex++] = (char) ((c & 0x1F) << 6 | (buf[byteIndex++] & 0x3F));
          break;
        case 14:
          chars[charIndex++] = (char) ((c & 0x0F) << 12 | (buf[byteIndex++] & 0x3F) << 6 | (buf[byteIndex++] & 0x3F) << 0);
          break;
      }
      charCount++;
    }
    return new String(chars, 0, charCount);
  }
 */

}
