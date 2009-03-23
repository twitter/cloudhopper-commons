/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudhopper.commons.sql;

/**
 * Enumeration of database vendors such as MySQL, MS SQL, Oracle, etc.
 * @author joelauer
 */
public enum DatabaseVendor {

    /** Microsoft SQLServer, default driver net.sourceforge.jtds.jdbc.Driver */
    MSSQL("net.sourceforge.jtds.jdbc.Driver"),
    /** PostgreSQL, default driver org.postgresql.Driver */
    //POSTGRESQL("org.postgresql.Driver"),
    /** MySQL, default driver com.mysql.jdbc.Driver */
    MYSQL("com.mysql.jdbc.Driver");

    private final String defaultDriver;

    DatabaseVendor(final String driver) {
        this.defaultDriver = driver;
    }

    /**
     * Gets the default driver class name for the database vendor.
     * @return The default driver class name
     */
    public String getDefaultDriver() {
        return this.defaultDriver;
    }
}