
package com.cloudhopper.commons.xbean;

/**
 * Thrown when a property was already set in an xml document once before.
 * @author joelauer
 */
public class PropertyAlreadySetException extends BasePropertyException {

    public PropertyAlreadySetException(String propertyName, String xmlPath, Class targetClass, String msg) {
        this(propertyName, xmlPath, null, msg, null);
    }

    public PropertyAlreadySetException(String propertyName, String xmlPath, Class targetClass, String msg, Throwable t) {
        super(propertyName, xmlPath, targetClass, msg, t);
    }
}
