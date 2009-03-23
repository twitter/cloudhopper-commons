
package com.cloudhopper.commons.xbean.convert;

import com.cloudhopper.commons.xbean.*;

/**
 * Converts a String to a Long.
 * @author joelauer
 */
public class LongPropertyConverter implements PropertyConverter {

    public Object convert(String value) throws ConversionException {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ConversionException(e.getMessage());
        }
    }
    
}
