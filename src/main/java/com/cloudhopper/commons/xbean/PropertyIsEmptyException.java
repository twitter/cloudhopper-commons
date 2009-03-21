
package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public class PropertyIsEmptyException extends BasePropertyException {

    public PropertyIsEmptyException(String propertyName, String xmlPath, String msg) {
        this(propertyName, xmlPath, msg, null);
    }

    public PropertyIsEmptyException(String propertyName, String xmlPath, String msg, Throwable t) {
        super(propertyName, xmlPath, msg, t);
    }
}
