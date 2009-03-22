
package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public class PropertyPermissionException extends BasePropertyException {

    public PropertyPermissionException(String propertyName, String xmlPath, Class targetClass, String msg) {
        this(propertyName, xmlPath, null, msg, null);
    }

    public PropertyPermissionException(String propertyName, String xmlPath, Class targetClass, String msg, Throwable t) {
        super(propertyName, xmlPath, targetClass, msg, t);
    }
}
