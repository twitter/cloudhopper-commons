
package com.cloudhopper.commons.xbean.convert;

import com.cloudhopper.commons.xbean.*;

/**
 * Converts a String to a Byte.
 * @author joelauer
 */
public class BytePropertyConverter implements PropertyConverter {

    public Object convert(String value) throws ConversionException {
        try {
            return Short.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ConversionException(e.getMessage());
        }
    }
    
}
