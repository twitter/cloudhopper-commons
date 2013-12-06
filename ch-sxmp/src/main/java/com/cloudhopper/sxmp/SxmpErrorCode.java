package com.cloudhopper.sxmp;

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
 * @author joelauer
 */
public enum SxmpErrorCode {

    OK(0),

    /* 1001: Only one "XXXX" element supported */
    MULTIPLE_ELEMENTS_NOT_SUPPORTED(1001),
    /* 1002: Mandatory attribute in an element is missing */
    MISSING_REQUIRED_ATTRIBUTE(1002),
    /* 1003: Mandatory element is missing */
    MISSING_REQUIRED_ELEMENT(1003),
    /* 1004: Unsupported element found */
    UNSUPPORTED_ELEMENT(1004),
    /* 1005: Reference ID value is bad (e.g. unsupported characters) */
    INVALID_REFERENCE_ID(1005),
    /* 1006: Invalid value used */
    INVALID_VALUE(1006),
    /* 1007: Mismatch between operation type and embedded request element */
    OPTYPE_MISMATCH(1007),
    /* 1008: Invalid operation type attribute; a response when a request was expected */
    UNSUPPORTED_OPERATION(1008),
    /* 1009: Unsupported attribute in an element found */
    UNSUPPORTED_ATTRIBUTE(1009),
    /* 1010: Unable to convert an element or attribute value to a primitive type such as an integer */
    UNABLE_TO_CONVERT_VALUE(1010),
    /* 1011: The element or attribute exists, but the value was empty */
    EMPTY_VALUE(1011),
    /* 1012: Multiple attributes with the same name were found in the element */
    MULTIPLE_ATTRIBUTES_NOT_SUPPORTED(1012),
    /* 1013: Unsupported type of address */
    UNSUPPORTED_ADDRESS_TYPE(1013),
    /* 1014: Address format was valid, but its use is not permitted */
    ADDRESS_NOT_PERMITTED(1014),
    /* 1015: Address was not valid (e.g. bad syntax) */
    INVALID_ADDRESS_VALUE(1015),
    /* 1016: Unsupported text encoding */
    UNSUPPORTED_TEXT_ENCODING(1016),
    /* 1017: Unable to decode the hex characters into bytes */
    TEXT_HEX_DECODING_FAILED(1017),
    /* 1018: Message length was too long */
    MESSAGE_LENGTH_ERROR(1018),
    /* 1019: Authentication failed (e.g. username or password bad) */
    AUTHENTICATION_FAILURE(1019),
    /* 1020: Unable to parse XML */
    INVALID_XML(1020),
    /* 1021: Operation was not permitted */
    OPERATION_NOT_PERMITTED(1021),

    /* 1022: Internal server error */
    INTERNAL_SERVER_ERROR(1022),
    /* 1023: Routing system error (routing subsystem failure) */
    ROUTING_SYSTEM_ERROR(1023),
    /* 1024: Route not found (unable to lookup operator) */
    ROUTE_NOT_FOUND(1024),
    /* 1025: Insufficient funds (e.g. prepaid account balance too low) */
    INSUFFICIENT_FUNDS(1025),
    /* 1026: Account not found */
    ACCOUNT_NOT_FOUND(1026),
    /* 1027: Account disabled */
    ACCOUNT_DISABLED(1027),
    /* 1028: Source address error (required or not permitted) */
    SOURCE_ADDRESS_ERROR(1028),
    /* 1029: Destination address error (required or not permitted) */
    DESTINATION_ADDRESS_ERROR(1029),
    /* 1030: Application not found */
    APPLICATION_NOT_FOUND(1030),
    /* 1031: Operator not found */
    OPERATOR_NOT_FOUND(1031),

    /* 9999: Generic error (usually catch-all) */
    GENERIC(9999);

    private final int code;

    SxmpErrorCode(final int code) {
        this.code = code;
    }

    public int getIntValue() {
        return this.code;
    }
    
    public static SxmpErrorCode valueOf(final int code) {
        for (SxmpErrorCode e : SxmpErrorCode.values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }

}
