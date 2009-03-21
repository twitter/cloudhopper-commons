
package com.cloudhopper.commons.xbean.convert;

import com.cloudhopper.commons.xbean.*;

/**
 * @author joelauer
 */
public class ShortPropertyConverter implements PropertyConverter {

    public Object convert(String value) throws ConversionException {
        try {
            return Byte.valueOf(value);
        } catch (NumberFormatException e) {
            throw new ConversionException(e.getMessage());
        }
    }
    
}
