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

// third party imports
import com.cloudhopper.commons.util.*;

/**
 * Utility methods for working with GSM mobile technologies.
 * 
 * @author joelauer
 */
public class GsmUtil {
    
    /**
     * Converts an address to a BCD-encoded byte array.  Any odd-length address
     * such as "10950" will have an 0xF byte added as the most signification
     * digit onto the end.  Used inside PDUs to encode addresses.  For example,
     * "10950" will look like this byte[] afterwards [0x01 0x59 0xf0].
     * @param address The address to encode such as "10950" or "13135554272"
     * @return A BCD-encoded byte array such as [0x01 0x59 0xf0] for "10950" or
     *      [0x31 0x31 0x55 0x45 0x72 0xf2] for "13135554272".  Will return a
     *      zero-length byte[] for an empty address.
     * @throws NullPointerException If the address is null
     * @throws IllegalArgumentException If the address does not contain only digits
     */
    public static byte[] toBcd(String address) throws NullPointerException, IllegalArgumentException {
        if (address == null) {
            throw new NullPointerException("Address cannot be null for BCD encoding");
        }

        if (!StringUtil.containsOnlyDigits(address)) {
            throw new IllegalArgumentException("Address must only contain digits");
        }

        // make sure this address is always an even length
        if (address.length() % 2 != 0) {
            // add an extra F onto the end
            address = address + "F";
        }

        // calculate length of final byte[]
        int bytes_len = address.length() / 2;
        byte[] bcd = new byte[bytes_len];

        for (int i = 0; i < bytes_len; i++) {
            StringBuilder chunk = new StringBuilder(address.substring(i * 2, (i * 2) + 2));
            // reverse the digits since its BCD encoded
            chunk.reverse();
            // get the byte
            byte[] bytes = HexUtil.toByteArray(chunk.toString());
            //byte[] bytes = ByteUtil.decodeHex(chunk.toString(), 2);
            // add this byte onto our total
            bcd[i] = bytes[0];
        }

        return bcd;
    }

    public static byte toSubmitInfo(boolean replyPath, boolean udhIndicator,
            boolean deliveryReceipt, int validityPeriodFormat, boolean rejectDuplicates)
            throws IllegalArgumentException {

        // generate proper submit info
        byte info = 0;

        // 7 0 TP-Reply -Path Reply path no set
        if (replyPath) {
            info |= (1 << 7);
        }

        // 6 0 TP-User-Data -header-indicator Indication that user data doesn't contain additional header.
        if (udhIndicator) {
            info |= (1 << 6);
        }

        // 5 0 TP-Status -Report-Request Not requested
        if (deliveryReceipt) {
            info |= (1 << 5);
        }

        // 4 1 TP-Validity -Period-Format Relative format (bits 4 and 3)
        // 3 0 TP-Validity -Period-Format Relative format (bits 4 and 3)
        //			4 3 (bits)
        //			0 0 TP-VP field not present
        //			1 0 TP-VP field present - relative format
        //			0 1 TP-VP field present - enhanced format
        //			1 1 TP-VP field present - absolute format
        if (validityPeriodFormat == GsmConstants.VP_FORMAT_RELATIVE) {
            info |= (1 << 4);
        } else if (validityPeriodFormat == GsmConstants.VP_FORMAT_ENHANCED) {
            info |= (1 << 3);
        } else if (validityPeriodFormat == GsmConstants.VP_FORMAT_ABSOLUTE) {
            info |= (1 << 4);
            info |= (1 << 3);
        } else if (validityPeriodFormat == GsmConstants.VP_FORMAT_NONE) {
            // zero by default so we don't add any bits
        } else {
            throw new IllegalArgumentException("Invalid validity period format");
        }

        // 2 0 TP-Rejected -Dublicates Do not reject duplicates in SC
        //		- 0 Instruct the SC to accept an SMS-SUBMIT for an SM still held in the
        //		SC which has the same TP-MR and the same TP-DA as a previously submitted SM from the
        //		same OA.
        //		- 1 Instruct the SC to reject an SMS-SUBMIT for an SM still held in the
        //		SC which has the same TP-MR and the same TP-DA as the previously submitted SM from the
        //		same OA. In this case an appropriate TP-FCS value will be returned in the
        //		SMS-SUBMIT-REPORT.
        if (rejectDuplicates) {
            info |= (1 << 2);
        }

        // 1 0 TP-Message -Type-Indicator type:SMS-SUBMIT (from phone to network), (bits 1 and 0)
        // 0 1 TP-Message -Type-Indicator type:SMS-SUBMIT (from phone to network), (bits 1 and 0)
        /**
        0 0 SMS-DELIVER (in the direction SC to MS)
        0 0 SMS-DELIVER REPORT (in the direction MS to SC)
        1 0 SMS-STATUS-REPORT (in the direction SC to MS)
        1 0 SMS-COMMAND (in the direction MS to SC)
        0 1 SMS-SUBMIT (in the direction MS to SC)
        0 1 SMS-SUBMIT-REPORT (in the direction SC to MS)
        1 1 Reserved
         */
        // set to SMS-SUBMIT
        info |= (1 << 0);

        return info;
    }

    /**
     * NOTE: Use getShortMessageUserData
     * @deprecated
     * @see #getShortMessageUserData(byte[]) 
     */
    static public byte[] removeUserDataHeader(byte[] shortMessage) {
        return getShortMessageUserData(shortMessage);
    }

    /**
     * Gets the "User Data" part of a short message byte array. Only call this
     * method if the short message contains a user data header. This method will
     * take the value of the first byte ("N") as the length of the user
     * data header, then remove ("N+1") bytes from the the short message and
     * return the remaining bytes as the "User Data".
     * @param shortMessage The byte array representing the entire message including
     *      a user data header and user data.  A null will return null.  An
     *      empty byte array will return an empty byte array.  A byte array
     *      not containing enough data will throw an IllegalArgumentException.
     * @return A byte array of the user data (minus the user data header)
     * @throws IllegalArgumentException If the byte array does not contain
     *      enough data to fit both the user data header and user data.
     */
    static public byte[] getShortMessageUserData(byte[] shortMessage) throws IllegalArgumentException {
        if (shortMessage == null) {
            return null;
        }

        if (shortMessage.length == 0) {
            return shortMessage;
        }

        // the entire length of UDH is the first byte + the length
        int userDataHeaderLength = ByteUtil.decodeUnsigned(shortMessage[0]) + 1;

        // is there enough data?
        if (userDataHeaderLength > shortMessage.length) {
            throw new IllegalArgumentException("User data header length exceeds short message length [shortMessageLength=" + shortMessage.length + ", userDataHeaderLength=" + userDataHeaderLength + "]");
        }

        // create a new message with the header removed
        int newShortMessageLength = shortMessage.length - userDataHeaderLength;
        byte[] newShortMessage = new byte[newShortMessageLength];

        System.arraycopy(shortMessage, userDataHeaderLength, newShortMessage, 0, newShortMessageLength);

        return newShortMessage;
    }

    /**
     * Gets the "User Data Header" part of a short message byte array. Only call this
     * method if the short message contains a user data header. This method will
     * take the value of the first byte ("N") as the length of the user
     * data header and return the first ("N+1") bytes from the the short message.
     * @param shortMessage The byte array representing the entire message including
     *      a user data header and user data.  A null will return null.  An
     *      empty byte array will return an empty byte array.  A byte array
     *      not containing enough data will throw an IllegalArgumentException.
     * @return A byte array of the user data header (minus the user data)
     * @throws IllegalArgumentException If the byte array does not contain
     *      enough data to fit both the user data header and user data.
     */
    static public byte[] getShortMessageUserDataHeader(byte[] shortMessage) throws IllegalArgumentException {
        if (shortMessage == null) {
            return null;
        }

        if (shortMessage.length == 0) {
            return shortMessage;
        }

        // the entire length of UDH is the first byte + the length
        int userDataHeaderLength = ByteUtil.decodeUnsigned(shortMessage[0]) + 1;

        // is there enough data?
        if (userDataHeaderLength > shortMessage.length) {
            throw new IllegalArgumentException("User data header length exceeds short message length [shortMessageLength=" + shortMessage.length + ", userDataHeaderLength=" + userDataHeaderLength + "]");
        }

        // is the user data header the only part of the payload (optimization)
        if (userDataHeaderLength == shortMessage.length) {
            return shortMessage;
        }

        // create a new message with just the header
        byte[] userDataHeader = new byte[userDataHeaderLength];
        System.arraycopy(shortMessage, 0, userDataHeader, 0, userDataHeaderLength);

        return userDataHeader;
    }

    /**
     * Creates multiple short messages (that include a user data header) by
     * splitting the binaryShortMessage data into 134 byte parts.  If the
     * binaryShortMessage does not need to be concatenated (less than or equal
     * to 140 bytes), this method will return NULL.
     * <br><br>
     * WARNING: This method only works on binary short messages that use 8-bit
     * bytes.  Short messages using 7-bit data or packed 7-bit data will not
     * be correctly handled by this method.
     * <br><br>
     * For example, will take a byte message (in hex, 138 bytes long)
     * <br>
     *   01020304...85&lt;byte 134&gt;87888990
     * <br><br>
     * Would be split into 2 parts as follows (in hex, with user data header)<br>
     *   050003CC020101020304...85&lt;byte 134&gt;<br>
     *   050003CC020287888990<br>
     * <br>
     * http://en.wikipedia.org/wiki/Concatenated_SMS
     *
     * @param binaryShortMessage The 8-bit binary short message to create the
     *      concatenated short messages from.
     * @param referenceNum The CSMS reference number that will be used in the
     *      user data header.
     * @return NULL if the binaryShortMessage does not need concatenated or
     *      an array of byte arrays representing each chunk (including UDH).
     * @throws IllegalArgumentException
     */
    static public byte[][] createConcatenatedBinaryShortMessages(byte[] binaryShortMessage, byte referenceNum) throws IllegalArgumentException {
        if (binaryShortMessage == null) {
            return null;
        }
        // if the short message does not need to be concatenated
        if (binaryShortMessage.length <= 140) {
            return null;
        }

        // since the UDH will be 6 bytes, we'll split the data into chunks of 134
        int numParts = (int) (binaryShortMessage.length / 134) + (binaryShortMessage.length % 134 != 0 ? 1 : 0);
        //logger.debug("numParts=" + numParts);

        byte[][] shortMessageParts = new byte[numParts][];

        for (int i = 0; i < numParts; i++) {
            // default this part length to max of 134
            int shortMessagePartLength = 134;
            if ((i + 1) == numParts) {
                // last part (only need to add remainder)
                shortMessagePartLength = binaryShortMessage.length - (i * 134);
            }

            //logger.debug("part " + i + " len: " + shortMessagePartLength);

            // part will be UDH (6 bytes) + length of part
            byte[] shortMessagePart = new byte[6 + shortMessagePartLength];
            // Field 1 (1 octet): Length of User Data Header, in this case 05.
            shortMessagePart[0] = (byte) 0x05;
            // Field 2 (1 octet): Information Element Identifier, equal to 00 (Concatenated short messages, 8-bit reference number)
            shortMessagePart[1] = (byte) 0x00;
            // Field 3 (1 octet): Length of the header, excluding the first two fields; equal to 03
            shortMessagePart[2] = (byte) 0x03;
            // Field 4 (1 octet): 00-FF, CSMS reference number, must be same for all the SMS parts in the CSMS
            shortMessagePart[3] = referenceNum;
            // Field 5 (1 octet): 00-FF, total number of parts. The value shall remain constant for every short message which makes up the concatenated short message. If the value is zero then the receiving entity shall ignore the whole information element
            shortMessagePart[4] = (byte) numParts;
            // Field 6 (1 octet): 00-FF, this part's number in the sequence. The value shall start at 1 and increment for every short message which makes up the concatenated short message. If the value is zero or greater than the value in Field 5 then the receiving entity shall ignore the whole information element. [ETSI Specification: GSM 03.40 Version 5.3.0: July 1996]
            shortMessagePart[5] = (byte) (i + 1);

            // copy this part's user data onto the end
            System.arraycopy(binaryShortMessage, (i * 134), shortMessagePart, 6, shortMessagePartLength);
            shortMessageParts[i] = shortMessagePart;
        }

        return shortMessageParts;
    }
}