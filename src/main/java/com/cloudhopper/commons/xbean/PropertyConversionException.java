
package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public class PropertyConversionException extends BasePropertyException {

    public PropertyConversionException(String propertyName, String xmlPath, String msg) {
        this(propertyName, xmlPath, msg, null);
    }

    public PropertyConversionException(String propertyName, String xmlPath, String msg, Throwable t) {
        super(propertyName, xmlPath, msg, t);
    }
}
