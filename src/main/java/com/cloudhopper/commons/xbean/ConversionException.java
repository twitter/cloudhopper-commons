
package com.cloudhopper.commons.xbean;

/**
 * Thrown by PropertyConverter classes when a String fails conversion to a
 * Java object.
 * @author joelauer
 */
public class ConversionException extends Exception {
    private static final long serialVersionUID = 1L;

    public ConversionException(String msg) {
        super(msg);
    }

    public ConversionException(String msg, Throwable t) {
        super(msg, t);
    }
}
