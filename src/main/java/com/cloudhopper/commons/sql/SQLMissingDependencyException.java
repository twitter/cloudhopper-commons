package com.cloudhopper.commons.sql;

import java.sql.SQLException;

/**
 * Thrown if an error occurs while configuring a DataSourceFactory or DataSource.
 * 
 * @author joelauer
 */
public class SQLMissingDependencyException extends SQLException {

    /**
     * Constructs an instance of <code>SQLConfigurationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SQLMissingDependencyException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>SQLConfigurationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SQLMissingDependencyException(String msg, Throwable t) {
        super(msg, t);
    }
}
