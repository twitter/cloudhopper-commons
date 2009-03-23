
package com.cloudhopper.commons.xbean.convert;

import com.cloudhopper.commons.xbean.*;

/**
 * Converts a String to a Short.
 * @author joelauer
 */
public class ShortPropertyConverter implements PropertyConverter {

    public Object convert(String value) throws ConversionException {
        try {
            return Short.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ConversionException(e.getMessage());
        }
    }
    
}
