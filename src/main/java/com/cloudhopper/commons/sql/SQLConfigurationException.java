package com.cloudhopper.commons.sql;

import java.sql.SQLException;

/**
 * Thrown if an error occurs while configuring a DataSourceFactory or DataSource.
 * @author joelauer
 */
public class SQLConfigurationException extends SQLException {

    /**
     * Creates a new instance of <code>SQLConfigurationException</code> without detail message.
     */
    public SQLConfigurationException() {
    }


    /**
     * Constructs an instance of <code>SQLConfigurationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SQLConfigurationException(String msg) {
        super(msg);
    }
}
