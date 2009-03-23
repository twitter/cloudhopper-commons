
package com.cloudhopper.commons.xbean;

/**
 * Thrown when a property is not found on a Java object.
 * @author joelauer
 */
public class PropertyNotFoundException extends BasePropertyException {

    public PropertyNotFoundException(String propertyName, String xmlPath, Class targetClass, String msg) {
        this(propertyName, xmlPath, targetClass, msg, null);
    }

    public PropertyNotFoundException(String propertyName, String xmlPath, Class targetClass, String msg, Throwable t) {
        super(propertyName, xmlPath, targetClass, msg, t);
    }
}
