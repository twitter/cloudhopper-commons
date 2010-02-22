
package com.cloudhopper.commons.xbean.convert;

import com.cloudhopper.commons.xbean.*;
import com.cloudhopper.commons.xbean.util.NumberRadixResult;
import com.cloudhopper.commons.xbean.util.NumberRadixUtil;

/**
 * Converts a String to a Byte.
 * @author joelauer
 */
public class BytePropertyConverter implements PropertyConverter {

    public Object convert(String value) throws ConversionException {
        NumberRadixResult result = NumberRadixUtil.parseNumberRadix(value);
        try {
            return Byte.valueOf(result.getNumber(), result.getRadix());
        } catch (NumberFormatException e) {
            throw new ConversionException(e.getMessage());
        }
    }
    
}
