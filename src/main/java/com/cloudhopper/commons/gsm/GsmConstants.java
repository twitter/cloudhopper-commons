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

package com.cloudhopper.commons.gsm;

/**
 * Constants specified in GSM network specifications.
 * 
 * @author joelauer
 */
public class GsmConstants {

    /** Address Type of Number */

    /** Type of Number - Unknown */
    public static final int TON_UNKNOWN = 0x00;
    /** Type of Number - International number */
    public static final int TON_INTERNATIONAL = 0x01;
    /** Type of Number - National number */
    public static final int TON_NATIONAL = 0x02;
    /** Type of Number - Network specific number */
    public static final int TON_NETWORK = 0x03;
    /** Type of Number - Subscriber number */
    public static final int TON_SUBSCRIBER = 0x04;
    /** Type of Number - Alphanumeric, (coded according to 3G TS 23.038 [9] GSM 7-bit default alphabet) */
    public static final int TON_ALPHANUMERIC = 0x05;
    /** Type of Number - Abbreviated number */
    public static final int TON_ABBREVIATED = 0x06;

    /** Address Numbering Plan Indicator */

    /** NPI - Uknown **/
    public static final int NPI_UNKNOWN = 0x00;
    /** NPI - ISDN/telephone numbering plan (E.164 [17]/E.163[18]) */
    public static final int NPI_ISDN = 0x01;
    /** NPI - Data numbering plan (X.121) */
    public static final int NPI_DATA = 0x03;
    /** NPI - Telex numbering plan */
    public static final int NPI_TELEX = 0x04;
    /** NPI - Service Centre Specific plan */
    public static final int NPI_SERVICE = 0x05;
    /** NPI - National numbering plan */
    public static final int NPI_NATIONAL = 0x08;
    /** NPI - Private numbering plan */
    public static final int NPI_PRIVATE = 0x09;
    /** NPI - ERMES numbering plan (ETSI DE/PS 3 01-3) */
    public static final int NPI_ERMES = 0x0A;

    /** Validity Period Formats */
    
    /** No Validity Period Used */
    public static final int VP_FORMAT_NONE = 0x00;
    /** Validity Field uses a relative format */
    public static final int VP_FORMAT_RELATIVE = 0x01;
    /** Validity Period uses an enhanced format */
    public static final int VP_FORMAT_ENHANCED = 0x02;
    /** Validity Period uses an absolute format */
    public static final int VP_FORMAT_ABSOLUTE = 0x03;

}
