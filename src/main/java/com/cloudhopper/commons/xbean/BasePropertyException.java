/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public abstract class BasePropertyException extends XmlBeanException {

    private String propertyName;
    private String xmlPath;

    public BasePropertyException(String propertyName, String xmlPath, String msg) {
        this(propertyName, xmlPath, msg, null);
    }

    public BasePropertyException(String propertyName, String xmlPath, String msg, Throwable t) {
        super(msg + " [property=" + propertyName + ", xmlPath=" + xmlPath + "]", t);
        this.propertyName = propertyName;
        this.xmlPath = xmlPath;
    }
}
