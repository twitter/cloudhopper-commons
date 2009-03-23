
package com.cloudhopper.commons.xbean;

/**
 * Thrown when an xpath is not found.
 * 
 * @author joelauer
 */
public class XPathNotFoundException extends XmlBeanException {

    public XPathNotFoundException(String msg) {
        this(msg, null);
    }

    public XPathNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
