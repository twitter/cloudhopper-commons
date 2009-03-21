
package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public class PropertyInvocationException extends BasePropertyException {

    public PropertyInvocationException(String propertyName, String xmlPath, String msg) {
        this(propertyName, xmlPath, msg, null);
    }

    public PropertyInvocationException(String propertyName, String xmlPath, String msg, Throwable t) {
        super(propertyName, xmlPath, msg, t);
    }
}
