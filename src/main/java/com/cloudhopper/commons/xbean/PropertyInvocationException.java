
package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public class PropertyInvocationException extends BasePropertyException {

    public PropertyInvocationException(String propertyName, String xmlPath, Class targetClass, String msg, String errorMessage) {
        this(propertyName, xmlPath, null, msg, errorMessage, null);
    }

    public PropertyInvocationException(String propertyName, String xmlPath, Class targetClass, String msg, String errorMessage, Throwable t) {
        super(propertyName, xmlPath, targetClass, msg + " [error='" + errorMessage + "']", t);
    }
}
