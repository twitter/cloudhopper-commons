
package com.cloudhopper.commons.xbean.convert;

import com.cloudhopper.commons.xbean.*;

/**
 * Converts a String to a boolean.
 * @author joelauer
 */
public class BooleanPrimitivePropertyConverter implements PropertyConverter {

    public Object convert(String value) throws ConversionException {
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new ConversionException("A boolean value must either be 'true' or 'false' [actual='" + value + "']");
        }
    }

}
