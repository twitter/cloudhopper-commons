package com.cloudhopper.commons.sql;

/*
 * #%L
 * ch-commons-sql
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Enumeration of database vendors such as MySQL, MS SQL, Oracle, etc.
 * Every vendor has certain properties associated with them such as a valid
 * default driver, validation query, etc.
 * 
 * @author joelauer
 */
public enum DatabaseVendor {

    /** Microsoft SQLServer, default driver net.sourceforge.jtds.jdbc.Driver */
    MSSQL("net.sourceforge.jtds.jdbc.Driver", "SELECT GETDATE()"),
    /** PostgreSQL, default driver org.postgresql.Driver */
    POSTGRESQL("org.postgresql.Driver", "SELECT NOW()"),
    /** MySQL, default driver com.mysql.jdbc.Driver */
    MYSQL("com.mysql.jdbc.Driver", "SELECT NOW()"),
    /** Vertica, default driver com.vertica.jdbc.Driver */
    VERTICA("com.vertica.jdbc.Driver", "SELECT NOW()"),
    /** HSQLDB, default driver org.hsqldb.jdbcDriver */
    HSQLDB("org.hsqldb.jdbcDriver", "SELECT CURRENT_TIME AS now FROM (VALUES(0))");

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
