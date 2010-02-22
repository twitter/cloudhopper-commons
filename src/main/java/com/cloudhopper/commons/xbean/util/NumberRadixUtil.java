
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
