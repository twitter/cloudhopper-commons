
package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public class PropertyIsEmptyException extends BasePropertyException {

    public PropertyIsEmptyException(String propertyName, String xmlPath, Class targetClass, String msg) {
        this(propertyName, xmlPath, null, msg, null);
    }

    public PropertyIsEmptyException(String propertyName, String xmlPath, Class targetClass, String msg, Throwable t) {
        super(propertyName, xmlPath, targetClass, msg, t);
    }
}
