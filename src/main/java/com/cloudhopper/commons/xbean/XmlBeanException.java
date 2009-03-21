/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudhopper.commons.xbean;

/**
 *
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
