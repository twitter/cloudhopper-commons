
package com.cloudhopper.commons.rfs;

/**
 * Root exception for any interactions with a remote filesystem.
 * 
 * @author joelauer
 */
public class FileSystemException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an instance of <code>FileSystemException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FileSystemException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>FileSystemException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FileSystemException(String msg, Throwable t) {
        super(msg, t);
    }
}
