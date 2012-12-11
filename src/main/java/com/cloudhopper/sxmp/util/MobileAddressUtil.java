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

package com.cloudhopper.sxmp.util;

import com.cloudhopper.sxmp.MobileAddress;
import com.cloudhopper.sxmp.SxmpErrorCode;
import com.cloudhopper.sxmp.SxmpErrorException;

/**
 *
 * @author joelauer
 */
public class MobileAddressUtil {

    /**
     * Parses string into a MobileAddress type.  Case insensitive.  Returns null
     * if no match was found.
     */
    static public MobileAddress.Type parseType(String type) {
        if (type.equalsIgnoreCase("network")) {
            return MobileAddress.Type.NETWORK;
        } else if (type.equalsIgnoreCase("national")) {
            return MobileAddress.Type.NATIONAL;
        } else if (type.equalsIgnoreCase("alphanumeric")) {
            return MobileAddress.Type.ALPHANUMERIC;
        } else if (type.equalsIgnoreCase("international")) {
            return MobileAddress.Type.INTERNATIONAL;
        } else if (type.equalsIgnoreCase("push_destination")) {
            return MobileAddress.Type.PUSH_DESTINATION;
        } else {
            return null;
        }
    }

    static public MobileAddress parseAddress(String type, String address) throws SxmpErrorException {
        // try to parse the type first
        MobileAddress.Type addrType = MobileAddressUtil.parseType(type);

        // check if the type was valid.
        if (addrType == null) {
            throw new SxmpErrorException(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, "The address type [" + type + "] is not valid");
        }

        // creates a new mobile address, also validates syntax of them too
        return new MobileAddress(addrType, address);
    }

    static public void validateNetworkAddress(String value) throws SxmpErrorException {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                throw new SxmpErrorException(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, "Network address contained an invalid character [" + value.charAt(i) + "]");
            }
        }
    }

    static public void validateInternationalAddress(String value) throws SxmpErrorException {
        // make sure address is at least 2 digits long
        if (value.length() < 2) {
            throw new SxmpErrorException(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, "International address must be at least 2 characters in length");
        }

        // first character must be +
        if (value.charAt(0) != '+') {
            throw new SxmpErrorException(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, "International address must start with '+' character followed by country dialing code and national number");
        }

        for (int i = 1; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                throw new SxmpErrorException(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, "International address contained an invalid character [" + value.charAt(i) + "]");
            }
        }
    }

}
