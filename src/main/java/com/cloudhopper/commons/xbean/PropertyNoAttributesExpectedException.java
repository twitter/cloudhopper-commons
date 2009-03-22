
package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public class PropertyNoAttributesExpectedException extends BasePropertyException {

    public PropertyNoAttributesExpectedException(String propertyName, String xmlPath, Class targetClass, String msg) {
        this(propertyName, xmlPath, null, msg, null);
    }

    public PropertyNoAttributesExpectedException(String propertyName, String xmlPath, Class targetClass, String msg, Throwable t) {
        super(propertyName, xmlPath, targetClass, msg, t);
    }
}
