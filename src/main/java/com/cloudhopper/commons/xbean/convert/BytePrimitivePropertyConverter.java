
package com.cloudhopper.commons.xbean.convert;

import com.cloudhopper.commons.xbean.*;

/**
 * @author joelauer
 */
public class BytePrimitivePropertyConverter implements PropertyConverter {

    public Object convert(String value) throws ConversionException {
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            throw new ConversionException(e.getMessage());
        }
    }
    
}
