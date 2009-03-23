
package com.cloudhopper.commons.xbean.convert;

import com.cloudhopper.commons.xbean.*;

/**
 * Converts a String to an Integer.
 * @author joelauer
 */
public class IntegerPropertyConverter implements PropertyConverter {

    public Object convert(String value) throws ConversionException {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ConversionException(e.getMessage());
        }
    }
    
}
