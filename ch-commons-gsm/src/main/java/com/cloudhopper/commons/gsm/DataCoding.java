package com.cloudhopper.commons.gsm;

/*
 * #%L
 * ch-commons-gsm
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

import com.cloudhopper.commons.util.HexUtil;

/**
 * A class that represents an immutable data coding scheme.  This class makes it
 * easier to work with a Data Coding Scheme specifications in ETSI GSM 3.38.
 * <br/>
 * Bit 7 6 5 4 3 2 1 0<br/>
 * <br/>
 * Bits 7..4 contain the "Coding Group Bits" which control what values
 * are contained in bits 3..0 OR even 5..0<br/>
 * <br/>
 * <b>0000: Character Encoding Group</b>
 * <ul>
 *      <li>Bits 0,1,2,3 Represent 16 Language Encodings</li>
 * </ul>
 *
 * <b>00xx: General Data Coding Group</b>
 * <ul>
 *      <li>If Bits 4 & 5 are 0 & 0 then see above "0000 Character Encoding Group"</li>
 *      <li>Message Class (Bit 0 & 1) (default 0, 0)</li>
 *      <li>Alphabet (Bit 2 & 3) (default 0, 0)</li>
 *      <li>Message Class Present (Bit 4) (whether bits 0 & 1 have meaning)</li>
 *      <li>Compression Flag (Bit 5) (0 - uncompressed, 1 - compressed)</li>
 * </ul>
 *
 * <b>0100-1011: Reserved Groups</b>
 * <ul>
 *      <li>Should throw an error during parsing</li>
 * </ul>
 *
 * <b>1100: Message Waiting Indication Group: Discard Message (Default Alphabet)</b>
 * <ul>
 *      <li>Bit 0 & 1 (Indication Type)</li>
 *      <li>Bit 2 (Reserved)</li>
 *      <li>Bit 3 (0 - Set Indication Inactive, 1 - Set Indication Active)</li>
 * </ul>
 *
 * <b>1101: Message Waiting Indication Group: Store Message (Default Alphabet)</b>
 * <ul>
 *      <li>Bit 0 & 1 (Indication Type)</li>
 *      <li>Bit 2 (Reserved)</li>
 *      <li>Bit 3 (0 - Set Indication Inactive, 1 - Set Indication Active)</li>
 * </ul>
 *
 * <b>1110: Message Waiting Indication Group: Store Message (UCS2 Alphabet)</b>
 * <ul>
 *      <li>Bit 0 & 1 (Indication Type)</li>
 *      <li>Bit 2 (Reserved)</li>
 *      <li>Bit 3 (0 - Set Indication Inactive, 1 - Set Indication Active)</li>
 * </ul>
 *
 * <b>1111: Message Class Group</b>
 * <ul>
 *      <li>Message Class (Bit 0 & 1)</li>
 *      <li>Alphabet (Bit 2 & 3) (Default, 8 Bit, USC2)</li>
 * </ul>
 *
 * @author joelauer
 */
public class DataCoding {

    public enum Group {
        /** Character encoding group (default).  Basically, represents the
          16 language encodings possible. */
        CHARACTER_ENCODING,
        /** General data coding group.  Represents a message class, a couple
          different character sets, and compression true/false. */
        GENERAL,
        /** A message waiting indicator was set along with just two possible
         * languages.  Either DEFAULT or UCS2. */
        MESSAGE_WAITING_INDICATION,
        /** A message class was set along with 3 possible languages. */
        MESSAGE_CLASS,
        /** Represents a reserved data coding scheme was used and likely was
         * something custom. */
        RESERVED
    }

    // message class constants
    public static final byte MESSAGE_CLASS_0 = 0;
    public static final byte MESSAGE_CLASS_1 = 1;
    public static final byte MESSAGE_CLASS_2 = 2;
    public static final byte MESSAGE_CLASS_3 = 3;

    // character encoding constants
    /** SMSC Default Alphabet (default) */
    public static final byte CHAR_ENC_DEFAULT = 0x00;
    /** IA5 (CCITT T.50)/ASCII (ANSI X3.4) */
    public static final byte CHAR_ENC_IA5 = 0x01;
    /** Octet unspecified (8-bit binary) defined for TDMA and/ or CDMA but not defined for GSM */
    public static final byte CHAR_ENC_8BITA = 0x02;
    /** Latin 1 (ISO-8859-1) */
    public static final byte CHAR_ENC_LATIN1 = 0x03;
    /** Octet unspecified (8-bit binary) ALL TECHNOLOGIES */
    public static final byte CHAR_ENC_8BIT = 0x04;
    /** JIS (X 0208-1990) */
    public static final byte CHAR_ENC_JIS = 0x05;
    /** Cyrllic (ISO-8859-5) */
    public static final byte CHAR_ENC_CYRLLIC = 0x06;
    /** Latin/Hebrew (ISO-8859-8) */
    public static final byte CHAR_ENC_HEBREW = 0x07;
    /** UCS2 (ISO/IEC-10646) */
    public static final byte CHAR_ENC_UCS2 = 0x08;
    /** Pictogram Encoding */
    public static final byte CHAR_ENC_PICTO = 0x09;
    /** ISO-2022-JP (Music Codes) */
    public static final byte CHAR_ENC_MUSIC = 0x0A;
    /** Reserved: 0x0B */
    public static final byte CHAR_ENC_RSRVD = 0x0B;
    /** Reserved: 0x0C */
    public static final byte CHAR_ENC_RSRVD2 = 0x0C;
    /** Extended Kanji JIS(X 0212-1990) */
    public static final byte CHAR_ENC_EXKANJI = 0x0D;
    /** KS C 5601 */
    public static final byte CHAR_ENC_KSC5601 = 0x0E;
    /** Reserved: 0x0F */
    public static final byte CHAR_ENC_RSRVD3 = 0x0F;

    /** NOT SUPPORTED YET
    // message waiting indication store/discard constants
    public static final byte MESSAGE_WAITING_COMMAND_DISCARD = 0;
    public static final byte MESSAGE_WAITING_COMMAND_STORE = 1;

    // message waiting indicator constants
    public static final byte MESSAGE_WAITING_IND_INACTIVE	= 0;
    public static final byte MESSAGE_WAITING_IND_ACTIVE		= 1;

    // message waiting indication type constants
    public static final byte MESSAGE_WAITING_TYPE_VOICEMAIL	= 0;
    public static final byte MESSAGE_WAITING_TYPE_FAX		= 1;
    public static final byte MESSAGE_WAITING_TYPE_EMAIL		= 2;
    public static final byte MESSAGE_WAITING_TYPE_OTHER		= 3;
     */

    // actual byte value
    private final byte dcs;
    // "group" this value falls under
    private final Group codingGroup;
    private final byte characterEncoding;
    private final byte messageClass;
    private final boolean compressed;
    // NOTE: leave out message waiting stuff for now...

    protected DataCoding(byte dcs, Group codingGroup, byte characterEncoding, byte messageClass, boolean compressed) {
        this.dcs = dcs;
        this.codingGroup = codingGroup;
        this.characterEncoding = characterEncoding;
        this.messageClass = messageClass;
        this.compressed = compressed;
    }

    public byte getByteValue() {
        return this.dcs;
    }

    public Group getCodingGroup() {
        return this.codingGroup;
    }

    public byte getCharacterEncoding() {
        return this.characterEncoding;
    }

    public byte getMessageClass() {
        return this.messageClass;
    }

    public boolean isCompressed() {
        return this.compressed;
    }

    /**
     * Creates a "Character Encoding" group data coding scheme where 16 different
     * languages are supported.  This method validates the range and
     * sets the message class to 0 and compression flags to false.
     * @param characterEncoding The different possible character encodings
     * @return A new immutable DataCoding instance representing this data coding scheme
     * @throws IllegalArgumentException Thrown if the range is not supported.
     */
    static public DataCoding createCharacterEncodingGroup(byte characterEncoding) throws IllegalArgumentException {
        // bits 3 thru 0 of the encoding represent 16 languages
        // make sure the top bits 7 thru 4 are not set
        if ((characterEncoding & 0xF0) != 0) {
            throw new IllegalArgumentException("Invalid characterEncoding [0x" + HexUtil.toHexString(characterEncoding) + "] value used: only 16 possible for char encoding group");
        }
        return new DataCoding(characterEncoding, Group.CHARACTER_ENCODING, characterEncoding, MESSAGE_CLASS_0, false);
    }

    /**
     * Creates a "Message Class" group data coding scheme where 2 different
     * languages are supported (8BIT or DEFAULT).  This method validates the
     * message class.
     * @param characterEncoding Either CHAR_ENC_DEFAULT or CHAR_ENC_8BIT
     * @param messageClass The 4 different possible message classes (0-3)
     * @return A new immutable DataCoding instance representing this data coding scheme
     * @throws IllegalArgumentException Thrown if the range is not supported.
     */
    static public DataCoding createMessageClassGroup(byte characterEncoding, byte messageClass) throws IllegalArgumentException {
        // only default or 8bit are valid
        if (!(characterEncoding == CHAR_ENC_DEFAULT || characterEncoding == CHAR_ENC_8BIT)) {
            throw new IllegalArgumentException("Invalid characterEncoding [0x" + HexUtil.toHexString(characterEncoding) + "] value used: only default or 8bit supported for message class group");
        }
        // validate the message class
        if (messageClass < 0 || messageClass > 3) {
            throw new IllegalArgumentException("Invalid messageClass [0x" + HexUtil.toHexString(messageClass) + "] value used: 0x00-0x03 only valid range");
        }

        // need to build this dcs value (top 4 bits are 1, start with 0xF0)
        byte dcs = (byte)0xF0;

        // 8bit encoding means bit 2 goes on
        if (characterEncoding == CHAR_ENC_8BIT) {
            dcs |= (byte)0x04;
        }

        // merge in the message class (bottom 2 bits)
        dcs |= messageClass;

        return new DataCoding(dcs, Group.MESSAGE_CLASS, characterEncoding, messageClass, false);
    }

    /**
     * Creates a "General" group data coding scheme where 3 different
     * languages are supported (8BIT, UCS2, or DEFAULT).  This method validates the
     * message class.
     * @param characterEncoding Either CHAR_ENC_DEFAULT, CHAR_ENC_8BIT, or CHAR_ENC_UCS2
     * @param messageClass The 4 different possible message classes (0-3).  If null,
     *      the "message class" not active flag will not be set.
     * @param compressed If true, the message is compressed.  False if unpacked.
     * @return A new immutable DataCoding instance representing this data coding scheme
     * @throws IllegalArgumentException Thrown if the range is not supported.
     */
    static public DataCoding createGeneralGroup(byte characterEncoding, Byte messageClass, boolean compressed) throws IllegalArgumentException {
        // only default, 8bit, or UCS2 are valid
        if (!(characterEncoding == CHAR_ENC_DEFAULT || characterEncoding == CHAR_ENC_8BIT || characterEncoding == CHAR_ENC_UCS2)) {
            throw new IllegalArgumentException("Invalid characterEncoding [0x" + HexUtil.toHexString(characterEncoding) + "] value used: only default, 8bit, or UCS2 supported for general group");
        }

        // validate the message class (only if non-null)
        if (messageClass != null && (messageClass.byteValue() < 0 || messageClass.byteValue() > 3)) {
            throw new IllegalArgumentException("Invalid messageClass [0x" + HexUtil.toHexString(messageClass) + "] value used: 0x00-0x03 only valid range");
        }

        // need to build this dcs value (top 2 bits are 0, start with 0x00)
        byte dcs = 0;
        if (compressed) {
            dcs |= (byte)0x20;  // turn bit 5 on
        }

        // if a message class is present turn bit 4 on
        if (messageClass != null) {
            dcs |= (byte)0x10;  // turn bit 4 on

            // bits 1 thru 0 is the message class
            // merge in the message class (bottom 2 bits)
            dcs |= messageClass;
        }

        // merge in language encodings (they nicely merge in since only default, 8-bit or UCS2)
        dcs |= characterEncoding;

        return new DataCoding(dcs, Group.GENERAL, characterEncoding, (messageClass == null ? MESSAGE_CLASS_0 : messageClass.byteValue()), compressed);
    }

    static public DataCoding createMessageWaitingIndicationGroup(byte characterEncoding, boolean store, boolean active, byte indicatorType) throws IllegalArgumentException {
        // only default or UCS2 are valid
        if (!(characterEncoding == CHAR_ENC_DEFAULT || characterEncoding == CHAR_ENC_UCS2)) {
            throw new IllegalArgumentException("Invalid characterEncoding [0x" + HexUtil.toHexString(characterEncoding) + "] value used: only default or UCS2 supported for MWI group");
        }

        // validate the indicatorType
        if (indicatorType < 0 || indicatorType > 3) {
            throw new IllegalArgumentException("Invalid indicatorType [0x" + HexUtil.toHexString(indicatorType) + "] value used: 0x00-0x03 only valid range");
        }

        // need to build this dcs value (top 2 bits are 1, start with 0xC0)
        byte dcs = (byte)0xC0;


        // bit 5: 0=default, 1=UCS2
        if (characterEncoding == CHAR_ENC_UCS2) {
            dcs |= (byte)0x20;
        }
        
        // bit 4: 0=discard, 1=store
        if (store) {
            dcs |= (byte)0x10;
        }

        // bit 3: indicator active
        if (active) {
            dcs |= (byte)0x08;
        }

        // bit 2: means nothing

        // bit 1 thru 0 is the type of indicator
        dcs |= indicatorType;

        return new DataCoding(dcs, Group.MESSAGE_WAITING_INDICATION, characterEncoding, MESSAGE_CLASS_0, false);
    }

    /**
     * Creates a "Reserved" group data coding scheme.  NOTE: this method does
     * not actually check if the byte value is reserved, its assumed the caller
     * just wants to store the byte value.
     * @param dcs The data coding scheme byte value
     * @return A new immutable DataCoding instance representing this data coding scheme
     */
    static public DataCoding createReservedGroup(byte dcs) {
        return new DataCoding(dcs, Group.RESERVED, CHAR_ENC_DEFAULT, MESSAGE_CLASS_0, false);
    }

    /**
     * Parse the data coding scheme value into something usable and makes various
     * values easy to use.  If a "reserved" value is used, returns a data coding
     * representing the same byte value, but all properties to their defaults.
     * @param dcs The byte value of the data coding scheme
     * @return A new immutable DataCoding instance representing this data coding scheme
     */
    static public DataCoding parse(byte dcs) {
        try {
            // if bits 7 thru 4 are all off, this represents a simple character encoding group
            if ((dcs & (byte)0xF0) == 0x00) {
                // the raw dcs value is exactly our 16 possible languaged
                return createCharacterEncodingGroup(dcs);

            // if bits 7 thru 4 are all on, this represents a message class group
            } else if ((dcs & (byte)0xF0) == (byte)0xF0) {
                // bit 2 is the language encoding
                byte characterEncoding = CHAR_ENC_DEFAULT;
                if ((dcs & (byte)0x04) == (byte)0x04) {
                    characterEncoding = CHAR_ENC_8BIT;
                }

                // bits 1 thru 0 is the message class
                byte messageClass = (byte)(dcs & (byte)0x03);

                return createMessageClassGroup(characterEncoding, messageClass);

            // at this point if bits 7 and 6 are off, then general data coding group
            } else if ((dcs & (byte)0xC0) == (byte)0x00) {
                // bit 5 represents "compression"
                boolean compressed = false;
                if ((dcs & (byte)0x20) == (byte)0x20) {
                    compressed = true;
                }

                // bits 1 thru 0 is the message class
                byte tempMessageClass = (byte)(dcs & (byte)0x03);

                Byte messageClass = null;
                // bit 4 on means the message class becomes used
                if ((dcs & (byte)0x10) == (byte)0x10) {
                    messageClass = new Byte(tempMessageClass);
                }

                // bits 3 and 2 represent the language encodings (nicely match default, 8-bit, or UCS2)
                byte characterEncoding = (byte)(dcs & (byte)0x0C);

                return createGeneralGroup(characterEncoding, messageClass, compressed);

            // at this point if bits 7 and 6 are on, then bits 5 and 4 determine MWI
            } else if ((dcs & (byte)0xC0) == (byte)0xC0) {
                // bit 5: 0=default, 1=UCS2
                byte characterEncoding = CHAR_ENC_DEFAULT;
                if ((byte)(dcs & (byte)0x20) == 0x20) {
                    characterEncoding = CHAR_ENC_UCS2;
                }

                // bit 4: 0=discard, 1=store
                boolean store = false;
                if ((byte)(dcs & (byte)0x10) == 0x10) {
                    store = true;
                }

                // bit 3: indicator active
                boolean indicatorActive = false;
                if ((byte)(dcs & (byte)0x08) == 0x08) {
                    indicatorActive = true;
                }

                // bit 2: means nothing

                // bit 1 thru 0 is the type of indicator
                byte indicatorType = (byte)(dcs & (byte)0x03);

                return createMessageWaitingIndicationGroup(characterEncoding, store, indicatorActive, indicatorType);

            } else {
                return createReservedGroup(dcs);
            }
        } catch (IllegalArgumentException e) {
            return createReservedGroup(dcs);
        }
    }

}
