/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public abstract class XmlConfigurationException extends Exception {

    public XmlConfigurationException(String msg) {
        super(msg);
    }

    public XmlConfigurationException(String msg, Throwable t) {
        super(msg, t);
    }
}
