
package com.cloudhopper.commons.sql;

/**
 * Enumeration of DataSource providers.
 * @author joelauer
 */
public enum DataSourceProvider {

    /** c3p0 datasource pooling provider */
    C3P0,
    /** Proxool datasource pooling provider */
    PROXOOL,
    /** Simple datasource provider, no pooling */
    SIMPLE

}
