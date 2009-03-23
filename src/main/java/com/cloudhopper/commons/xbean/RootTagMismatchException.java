
package com.cloudhopper.commons.xbean;

/**
 * Thrown if a root tag does not match a tag name provided.
 * @author joelauer
 */
public class RootTagMismatchException extends XmlBeanException {

    public RootTagMismatchException(String msg) {
        this(msg, null);
    }

    public RootTagMismatchException(String msg, Throwable t) {
        super(msg, t);
    }
}
