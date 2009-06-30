
package com.cloudhopper.commons.xbean;

/**
 * Thrown when access to a property or class is denied.
 * @author joelauer
 */
public class PropertyPermissionException extends BasePropertyException {
    private static final long serialVersionUID = 1L;

    public PropertyPermissionException(String propertyName, String xmlPath, Class targetClass, String msg) {
        this(propertyName, xmlPath, null, msg, null);
    }

    public PropertyPermissionException(String propertyName, String xmlPath, Class targetClass, String msg, Throwable t) {
        super(propertyName, xmlPath, targetClass, msg, t);
    }
}
