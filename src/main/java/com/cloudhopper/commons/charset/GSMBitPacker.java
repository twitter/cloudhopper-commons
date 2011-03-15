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
 * Utility for packing and unpacking 8-bit to/from 7-bit byte arrays.
 *
 * @author joelauer
 */
public class GSMBitPacker {

    /**
     * Pack a byte array according to the GSM bit-packing algorithm.
     * The GSM specification defines a simple compression mechanism for its
     * default alphabet to pack more message characters into a smaller space.
     * Since the alphabet only contains 128 symbols, each one can be represented
     * in 7 bits. The packing algorithm squeezes the bits for each symbol
     * "down" into the preceeding byte (so bit 7 of the first byte actually
     * contains bit 0 of the second symbol in a default alphabet string, bits
     * 6 and 7 in the second byte contain bits 0 and 1 of the third symbol etc.)
     * Since the maximum short message length is 140 <b>bytes</b>, you save
     * one bit per byte using the default alphabet giving you a total of
     * 140 + (140 / 8) = 160 characters to use. This is where the 160 character
     * limit comes from in SMPP packets.
     * <p>
     * Having said all that, most SMSCs do <b>NOT</b> use the packing
     * algorithm when communicating over TCP/IP. They either use a full
     * 8-bit alphabet such as ASCII or Latin-1, or they accept the default
     * alphabet in its unpacked form. As such, you will be unlikely to
     * need this method.
     * </o>
     * @param unpacked The unpacked byte array.
     * @return A new byte array containing the bytes in their packed form.
     */
    static public byte[] pack(byte[] unpacked) {
        if (unpacked == null) {
            return null;
        }

        int packedLen = unpacked.length - (unpacked.length / 8);
        //byte[] out = new byte[(int)Math.ceil((unpacked.length * 7) / 8f)];
        byte[] packed = new byte[packedLen];

        int len = unpacked.length;
        int current = 0;
        int bitpos = 0;
        for (int i = 0; i < len; i++) {
            byte b = (byte)(unpacked[i] & 0x7F); // remove top bit
            // assign first half of partial bits
            packed[current] |= (byte) ((b & 0xFF) << bitpos);
            // assign second half of partial bits (if exist)
            if (bitpos >= 2)
                packed[++current] |= (b >> (8 - bitpos));
            bitpos = (bitpos + 7) % 8;
            if (bitpos == 0)
                current++;
        }
        return packed;
    }

    /**
    static public byte[] pack(byte[] unpacked) {
        if (unpacked == null) {
            return null;
        }
        int packedLen = unpacked.length - (unpacked.length / 8);
        byte[] packed = new byte[packedLen];
        int pos = 0;
        int i = 0;
        while (i < unpacked.length) {
            int jmax = (i + 7) > unpacked.length ? unpacked.length - i : 7;
            int mask = 0x1;
            for (int j = 0; j < jmax; j++) {
                int b1 = (int) unpacked[i + j] & 0xff;
                int b2 = 0x0;
                try {
                    b2 = (int) unpacked[i + j + 1] & mask;
                } catch (ArrayIndexOutOfBoundsException x) {
                }
                packed[pos++] = (byte) ((b1 >>> j) | (b2 << (8 - (j + 1))));
                mask = (mask << 1) | 1;
            }
            i += 8;
        }
        return packed;
    }
     */

    /**
     * Unpack a byte array according to the GSM bit-packing algorithm.
     * Read the full description in the documentation of the
     * <code>pack</code> method.
     * @see #pack(byte[])
     * @param packed The packed byte array.
     * @return A new byte array containing the unpacked bytes.
     */
    /**
    static public byte[] unpack2(byte[] packed) {
        if (packed == null) {
            return null;
        }
        int unpackedLen = (packed.length * 8) / 7;
        byte[] unpacked = new byte[unpackedLen];
        int pos = 0;
        int i = 0;
        while (i < packed.length) {
            int mask = 0x7f;
            int jmax = (i + 8) > packed.length ? (packed.length - i) : 8;

            for (int j = 0; j < jmax; j++) {
                int b1 = (int) packed[i + j] & mask;
                int b2 = 0x0;
                try {
                    b2 = (int) packed[(i + j) - 1] & 0x00ff;
                } catch (ArrayIndexOutOfBoundsException x) {
                }
                unpacked[pos++] = (byte) ((b1 << j) | (b2 >>> (8 - j)));
                mask >>= 1;
            }
            i += 7;
        }
        return unpacked;
    }
     */

    /**
     * Unpack a byte array according to the GSM bit-packing algorithm.
     * Read the full description in the documentation of the
     * <code>pack</code> method.
     * @see #pack(byte[])
     * @param packed The packed byte array.
     * @return A new byte array containing the unpacked bytes.
     */
    static public byte[] unpack(byte[] packed) {
        if (packed == null) {
            return null;
        }

        int unpackedLen = (packed.length * 8) / 7;
        byte[] unpacked = new byte[unpackedLen];
        int len = unpacked.length;
        int current = 0;
        int bitpos = 0;
        for (int i = 0; i < len; i++) {
            // remove top bit and assign first half of partial bits
            unpacked[i] = (byte)(((packed[current] & 0xFF) >> bitpos) & 0x7F);
            // remove top bit and assign second half of partial bits (if exist)
            if (bitpos >= 2)
                unpacked[i] |= (byte)((packed[++current] << (8 - bitpos)) & 0x7F);
            bitpos = (bitpos + 7) % 8;
            if (bitpos == 0)
                current++;
        }
        // this fixes an ambiguity bug in the specs
        // where the last of 8 packed bytes is 0
        // and it's impossible to distinguish whether it is a
        // trailing '@' character (which is mapped to 0)
        // or extra zero-bit padding for 7 actual data bytes.
        //
        // we opt for the latter, since it's far more likely,
        // at the cost of losing a trailing '@' character
        // in strings whose unpacked size modulo 8 is 0,
        // and whose last character is '@'.
        //
        // an application that wishes to handle this rare case
        // properly must disambiguate this case externally, such
        // as by obtaining the original string length, and
        // appending the trailing '@' if the length
        // shows that there is one character missing.
        if (len % 8 == 0 && len > 0 && unpacked[len-1] == 0) {
            //System.err.println("Hit special case...");
            byte[] fixed = new byte[len-1];
            System.arraycopy(unpacked, 0, fixed, 0, len-1);
            unpacked = fixed;
        }
        return unpacked;
    }

}
