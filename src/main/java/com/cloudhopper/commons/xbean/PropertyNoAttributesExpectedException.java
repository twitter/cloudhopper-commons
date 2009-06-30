
package com.cloudhopper.commons.xbean;

/**
 * Thrown if attributes are included for an xml element when no attributes
 * are expected.
 * @author joelauer
 */
public class PropertyNoAttributesExpectedException extends BasePropertyException {
    private static final long serialVersionUID = 1L;

    public PropertyNoAttributesExpectedException(String propertyName, String xmlPath, Class targetClass, String msg) {
        this(propertyName, xmlPath, null, msg, null);
    }

    public PropertyNoAttributesExpectedException(String propertyName, String xmlPath, Class targetClass, String msg, Throwable t) {
        super(propertyName, xmlPath, targetClass, msg, t);
    }
}
