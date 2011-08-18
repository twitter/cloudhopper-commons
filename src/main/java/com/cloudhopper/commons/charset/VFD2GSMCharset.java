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

import java.util.Arrays;

/**
 * The <b>VFD2GSMCharset</b> class is mostly based on standard GSM charset, but
 * just a few chars (@, German chars) use different byte values.
 *
 * The SMSC uses the German National changes Replacement Codes (NRCs, s. ISO 21
 * German) for the representation of the characters ä, Ä, ö, Ö, ü, Ü, ß, §.
 * Also, the @ char is encoded strange.
 *
 * NOTE: This charset was only for MT (application -> mobile).  Vodafone-D2 has
 * an entirely different charset for MO (mobile -> application).
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class VFD2GSMCharset extends GSMCharset {
    
    private static final int GSM_COL = 0;
    private static final int VFD2_COL = 1;
    private static final byte[][] VFD2_OVERRIDE_TABLE = {
        { (byte)0x00, (byte)0x40 }, // @
        { (byte)0x02, (byte)0x24 }, // $
        { (byte)0x1E, (byte)0x7E }, // ß
        { (byte)0x24, (byte)0x02 }, // ¤
        { (byte)0x40, (byte)0xA1 }, // ¡
        { (byte)0x5D, (byte)0x5F }, // Ñ
        { (byte)0x5E, (byte)0x5D }, // Ü
        { (byte)0x5F, (byte)0x5E }, // §
        { (byte)0x7D, (byte)0x1E }, // ñ
        { (byte)0x7E, (byte)0x7D }, // ü
    };

    @Override
    public byte[] encode(CharSequence str0) {
        // encoding of unicode to "VFD2-GSM" is nearly the same as "GSM", but
        // a few byte values need to be converted -- we'll first convert to GSM
        byte[] gsmBytes = super.encode(str0);

        //logger.debug(HexUtil.toHexString(gsmBytes));

        // a little slow to run thru this again, but SMS are so tiny its not worth optimizing
        MAIN_LOOP:
        for (int i = 0; i < gsmBytes.length; i++) {
            OVERRIDE_LOOP:
            for (int j = 0; j < VFD2_OVERRIDE_TABLE.length; j++) {
                // if we find a GSM byte value in our override GSM column
                // we need to swap its value with a replacement value
                if (gsmBytes[i] == VFD2_OVERRIDE_TABLE[j][GSM_COL]) {
                    gsmBytes[i] = VFD2_OVERRIDE_TABLE[j][VFD2_COL];
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

        // decoding "VFD2-GSM" to unicode is nearly same process as "GSM", but
        // a few byte values need to be converted -- we'll first convert from VFD2-GSM to GSM
        // a little slow to run thru this again, but SMS are so tiny its not worth optimizing
        MAIN_LOOP:
        for (int i = 0; i < length; i++) {
            OVERRIDE_LOOP:
            for (int j = 0; j < VFD2_OVERRIDE_TABLE.length; j++) {
                // if we find a VFD2-GSM byte value in our VFD2 column
                // we need to swap its value with a replacement value
                if (bytes[i] == VFD2_OVERRIDE_TABLE[j][VFD2_COL]) {
                    // we found a special byte value, check if we need to copy
                    // the byte array now for a "lazy" copy
                    if (bytes2 == null) {
                        bytes2 = Arrays.copyOf(bytes, bytes.length);
                    }
                    bytes2[i] = VFD2_OVERRIDE_TABLE[j][GSM_COL];
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
