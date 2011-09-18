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

package com.cloudhopper.commons.xbean.util;

/**
 * Utility for parsing strings and returning the String to actually attempt
 * to parse along with a radix.  For example, any integer starting with 0x
 * will be treated as hex.
 *
 * @author joelauer
 */
public class NumberRadixUtil {

    static public NumberRadixResult parseNumberRadix(String number) {
        if (number == null) {
            return null;
        }
        if (number.startsWith("0x") || number.startsWith("0X")) {
            return new NumberRadixResult(number.substring(2), 16);
        } else {
            return new NumberRadixResult(number, 10);
        }
    }

}
