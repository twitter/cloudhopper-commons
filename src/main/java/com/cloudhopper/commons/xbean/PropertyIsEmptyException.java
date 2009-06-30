
package com.cloudhopper.commons.xbean;

/**
 * Thrown if a property was included in an xml document, but not value was
 * assigned to it.
 * @author joelauer
 */
public class PropertyIsEmptyException extends BasePropertyException {
    private static final long serialVersionUID = 1L;

    public PropertyIsEmptyException(String propertyName, String xmlPath, Class targetClass, String msg) {
        this(propertyName, xmlPath, null, msg, null);
    }

    public PropertyIsEmptyException(String propertyName, String xmlPath, Class targetClass, String msg, Throwable t) {
        super(propertyName, xmlPath, targetClass, msg, t);
    }
}
