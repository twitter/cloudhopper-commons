
package com.cloudhopper.commons.xbean.util;

/**
 * Result when using the NumberRadixUtil class.
 * 
 * @author joelauer
 */
public class NumberRadixResult {

    private final String number;
    private final int radix;

    public NumberRadixResult(String number, int radix) {
        this.number = number;
        this.radix = radix;
    }

    public String getNumber() {
        return this.number;
    }

    public int getRadix() {
        return this.radix;
    }
    
}
