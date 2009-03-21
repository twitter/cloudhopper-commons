
package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public class PropertyPermissionException extends BasePropertyException {

    public PropertyPermissionException(String propertyName, String xmlPath, String msg) {
        this(propertyName, xmlPath, msg, null);
    }

    public PropertyPermissionException(String propertyName, String xmlPath, String msg, Throwable t) {
        super(propertyName, xmlPath, msg, t);
    }
}
