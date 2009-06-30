
package com.cloudhopper.commons.xbean;

/**
 * Thrown when the underlying Java object throws an exception while attempting
 * to either get or set a property value.
 * @author joelauer
 */
public class PropertyInvocationException extends BasePropertyException {
    private static final long serialVersionUID = 1L;

    public PropertyInvocationException(String propertyName, String xmlPath, Class targetClass, String msg, String errorMessage) {
        this(propertyName, xmlPath, null, msg, errorMessage, null);
    }

    public PropertyInvocationException(String propertyName, String xmlPath, Class targetClass, String msg, String errorMessage, Throwable t) {
        super(propertyName, xmlPath, targetClass, msg + " [error='" + errorMessage + "']", t);
    }
}
