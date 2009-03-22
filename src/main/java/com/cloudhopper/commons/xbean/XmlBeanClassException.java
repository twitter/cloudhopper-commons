
package com.cloudhopper.commons.xbean;

/**
 * Thrown if there is an exception during reflection or instantiation of a class.
 * 
 * @author joelauer
 */
public class XmlBeanClassException extends XmlBeanException {

    public XmlBeanClassException(String msg) {
        this(msg, null);
    }

    public XmlBeanClassException(String msg, Throwable t) {
        super(msg, t);
    }
}
