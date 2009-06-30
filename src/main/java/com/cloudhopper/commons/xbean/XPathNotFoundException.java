
package com.cloudhopper.commons.xbean;

/**
 * Thrown if an xpath is not found in an xml document.
 * @author joelauer
 */
public class XPathNotFoundException extends XmlBeanException {
    private static final long serialVersionUID = 1L;

    public XPathNotFoundException(String msg) {
        this(msg, null);
    }

    public XPathNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
