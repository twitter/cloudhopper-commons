
package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public class PropertyNotFoundException extends BasePropertyException {

    public PropertyNotFoundException(String propertyName, String xmlPath, String msg) {
        this(propertyName, xmlPath, msg, null);
    }

    public PropertyNotFoundException(String propertyName, String xmlPath, String msg, Throwable t) {
        super(propertyName, xmlPath, msg, t);
    }
}
