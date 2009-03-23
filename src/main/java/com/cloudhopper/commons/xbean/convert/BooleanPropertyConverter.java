
package com.cloudhopper.commons.xbean.convert;

import com.cloudhopper.commons.xbean.*;

/**
 * Converts a String to a Boolean.
 * @author joelauer
 */
public class BooleanPropertyConverter implements PropertyConverter {

    public Object convert(String value) throws ConversionException {
        if (value.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        } else if (value.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        } else {
            throw new ConversionException("A boolean value must either be 'true' or 'false' [actual='" + value + "']");
        }
    }

}
