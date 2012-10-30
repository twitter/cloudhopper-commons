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

import java.util.Arrays;

/**
 * The <b>VFTRGSMCharset</b> class is mostly based on Latin-1, but mixed almost
 * equally with the GSM default charset.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class VFTRGSMCharset extends GSMCharset {
    
    private static final int GSM_COL = 0;
    private static final int VFTR_COL = 1;
    // mapping GSM byte value to VFTR byte value
    private static final byte[][] VFTR_OVERRIDE_TABLE = {
        { (byte)0x02, (byte)0x24 }, // $
        { (byte)0x00, (byte)0x40 }, // @
        { (byte)0x11, (byte)0x5F }, // _
        { (byte)0x40, (byte)0xA1 }, // ¡
        { (byte)0x01, (byte)0xA3 }, // £
        { (byte)0x24, (byte)0xA4 }, // ¤
        { (byte)0x03, (byte)0xA5 }, // ¥
        { (byte)0x5F, (byte)0xA7 }, // §
        { (byte)0x60, (byte)0xBF }, // ¿
        { (byte)0x5B, (byte)0xC4 }, // Ä
        { (byte)0x0E, (byte)0xC5 }, // Å
        { (byte)0x1C, (byte)0xC6 }, // Æ
        { (byte)0x09, (byte)0xC7 }, // Ç
        { (byte)0x1F, (byte)0xC9 }, // É
        { (byte)0x5D, (byte)0xD1 }, // Ñ
        { (byte)0x5C, (byte)0xD6 }, // Ö
        { (byte)0x0B, (byte)0xD8 }, // Ø
        { (byte)0x5E, (byte)0xDC }, // Ü
        { (byte)0x1E, (byte)0xDF }, // ß
        { (byte)0x7F, (byte)0xE0 }, // à
        { (byte)0x7B, (byte)0xE4 }, // ä
        { (byte)0x0F, (byte)0xE5 }, // å
        { (byte)0x1D, (byte)0xE6 }, // æ
        { (byte)0x04, (byte)0xE8 }, // è
        { (byte)0x05, (byte)0xE9 }, // é
        { (byte)0x07, (byte)0xEC }, // ì
        { (byte)0x7D, (byte)0xF1 }, // ñ
        { (byte)0x08, (byte)0xF2 }, // ò
        { (byte)0x7C, (byte)0xF6 }, // ö
        { (byte)0x0C, (byte)0xF8 }, // ø
        { (byte)0x06, (byte)0xF9 }, // ù
        { (byte)0x7E, (byte)0xFC }, // ü
        { (byte)0x10, (byte)0x7F }, // Δ
        /** SAME MAPPINGS AS GSM
        { (char)0x13, (char)0x13 }, // Γ
        { (char)0x19, (char)0x19 }, // Θ
        { (char)0x14, (char)0x14 }, // Λ
        { (char)0x1A, (char)0x1A }, // Ξ
        { (char)0x16, (char)0x16 }, // Π
        { (char)0x18, (char)0x18 }, // Σ
        { (char)0x12, (char)0x12 }, // Φ
        { (char)0x17, (char)0x17 }, // Ψ
        { (char)0x15, (char)0x15 }, // Ω
         */
    };

    @Override
    public byte[] encode(CharSequence str0) {
        // first, convert UNICODE to GSM
        byte[] gsmBytes = super.encode(str0);

        // second, convert GSM to VF-TR for select chars
        // a little slow to run thru this again, but SMS are so tiny its not worth optimizing
        MAIN_LOOP:
        for (int i = 0; i < gsmBytes.length; i++) {
            OVERRIDE_LOOP:
            for (int j = 0; j < VFTR_OVERRIDE_TABLE.length; j++) {
                // if we find a GSM byte value in our override GSM column
                // we need to swap its value with a replacement value
                if (gsmBytes[i] == VFTR_OVERRIDE_TABLE[j][GSM_COL]) {
                    gsmBytes[i] = VFTR_OVERRIDE_TABLE[j][VFTR_COL];
                    // need to immediately exit search so that we don't run into
                    // the bug where 0x00 -> 0x40 and then 0x40 -> 0xA1
                    break OVERRIDE_LOOP;
                }
            }
        }

        return gsmBytes;
    }

    @Override
    public void decode(byte[] bytes, StringBuilder buffer) {
        int length = (bytes == null ? 0 : bytes.length);
        
        // we promise to not change any of the bytes -- an optimization is a
        // lazy "copy" of the byte array in case we don't encounter any bytes
        // that need to be converted
        byte[] bytes2 = null;

        // decoding "VFTR" to unicode is nearly same process as "GSM", but
        // a few byte values need to be converted -- we'll first convert from VFD2-GSM to GSM
        // a little slow to run thru this again, but SMS are so tiny its not worth optimizing
        MAIN_LOOP:
        for (int i = 0; i < length; i++) {
            OVERRIDE_LOOP:
            for (int j = 0; j < VFTR_OVERRIDE_TABLE.length; j++) {
                // if we find a VFD2-GSM byte value in our VFD2 column
                // we need to swap its value with a replacement value
                if (bytes[i] == VFTR_OVERRIDE_TABLE[j][VFTR_COL]) {
                    // we found a special byte value, check if we need to copy
                    // the byte array now for a "lazy" copy
                    if (bytes2 == null) {
                        bytes2 = Arrays.copyOf(bytes, bytes.length);
                    }
                    bytes2[i] = VFTR_OVERRIDE_TABLE[j][GSM_COL];
                    // need to immediately exit search so that we don't run into
                    // the bug where 0x00 -> 0x40 and then 0x40 -> 0xA1
                    break OVERRIDE_LOOP;
                }
            }
        }

        // delegate to parent (pick which byte array is correct)
        super.decode((bytes2 == null ? bytes : bytes2), buffer);
    }

}
