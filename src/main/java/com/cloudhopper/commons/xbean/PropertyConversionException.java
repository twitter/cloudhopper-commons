
package com.cloudhopper.commons.xbean;

/**
 * Thrown when a property fails during conversion to a Java object.
 * @author joelauer
 */
public class PropertyConversionException extends BasePropertyException {

    public PropertyConversionException(String propertyName, String xmlPath, Class targetClass, String msg) {
        this(propertyName, xmlPath, null, msg, null);
    }

    public PropertyConversionException(String propertyName, String xmlPath, Class targetClass, String msg, Throwable t) {
        super(propertyName, xmlPath, targetClass, msg, t);
    }
}
