/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudhopper.commons.sql;

/**
 * Enumeration of database vendors such as MySQL, MS SQL, Oracle, etc.
 * Every vendor has certain properties associated with them such as a valid
 * default driver, validation query, etc.
 * @author joelauer
 */
public enum DatabaseVendor {

    /** Microsoft SQLServer, default driver net.sourceforge.jtds.jdbc.Driver */
    MSSQL("net.sourceforge.jtds.jdbc.Driver", "SELECT GETDATE()"),
    /** PostgreSQL, default driver org.postgresql.Driver */
    //POSTGRESQL("org.postgresql.Driver"),
    /** MySQL, default driver com.mysql.jdbc.Driver */
    MYSQL("com.mysql.jdbc.Driver", "SELECT NOW()");

    private final String defaultDriver;
    private final String defaultValidationQuery;

    DatabaseVendor(final String driver, final String validationQuery) {
        this.defaultDriver = driver;
        this.defaultValidationQuery = validationQuery;
    }

    /**
     * Gets the default driver class name for the database vendor.
     * @return The default driver class name
     */
    public String getDefaultDriver() {
        return this.defaultDriver;
    }

    public String getDefaultValidationQuery() {
        return this.defaultValidationQuery;
    }
}