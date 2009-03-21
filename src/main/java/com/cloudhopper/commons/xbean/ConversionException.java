/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudhopper.commons.xbean;

/**
 *
 * @author joelauer
 */
public class ConversionException extends Exception {

    public ConversionException(String msg) {
        super(msg);
    }

    public ConversionException(String msg, Throwable t) {
        super(msg, t);
    }
}
