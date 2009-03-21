
package com.cloudhopper.commons.xbean;

/**
 *
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
