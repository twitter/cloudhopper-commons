package com.cloudhopper.commons.xbean.util;

/*
 * #%L
 * ch-commons-xbean
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

import com.cloudhopper.commons.xbean.ConversionException;
import com.cloudhopper.commons.xbean.ConversionOverflowException;

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
    
    static public String normalizeLeadingHexZeroes(String v, int length) throws ConversionException {
        if (v == null || v.length() == 0) {
            throw new ConversionException("Empty or null value detected; unable to convert");
        } else if (v.length() == length) {
            return v;
        } else if (v.length() < length) {
            // add leading zeroes
            int prepend = (length - v.length());
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < prepend; i++) {
                sb.append('0');
            }
            sb.append(v);
            return sb.toString();
        } else {
            // remove leading zeroes (or error out if not zeroes)
            int remove = (v.length() - length);
            // check if any of these are non-zero
            for (int i = 0; i < remove; i++) {
                if (v.charAt(i) != '0') {
                    throw new ConversionOverflowException("Overflow of value detected; unable to trim value [" + v + "] to length " + length);
                }
            }
            return v.substring(remove);
        }
    }

}
