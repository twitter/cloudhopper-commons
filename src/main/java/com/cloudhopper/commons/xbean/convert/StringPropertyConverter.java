
package com.cloudhopper.commons.xbean.convert;

import com.cloudhopper.commons.xbean.*;

/**
 * Converts a String to a String.
 * @author joelauer
 */
public class StringPropertyConverter implements PropertyConverter {

    public Object convert(String value) throws ConversionException {
        return value;
    }

}
