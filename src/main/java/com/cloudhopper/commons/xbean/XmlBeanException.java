
package com.cloudhopper.commons.xbean;

/**
 * Super class for any XmlBean exceptions.
 * @author joelauer
 */
public abstract class XmlBeanException extends Exception {

    public XmlBeanException(String msg) {
        super(msg);
    }

    public XmlBeanException(String msg, Throwable t) {
        super(msg, t);
    }
}
