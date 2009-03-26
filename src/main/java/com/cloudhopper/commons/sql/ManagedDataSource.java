
package com.cloudhopper.commons.sql;

import javax.sql.DataSource;

/**
 * A wrapper around a DataSource that provides management features.
 * 
 * @author joelauer
 */
public abstract class ManagedDataSource implements ManagedDataSourceMBean {

    private DataSourceConfiguration config;
    private DataSource dataSource;

    public ManagedDataSource(DataSourceConfiguration config, DataSource ds) {
        this.config = config;
        this.dataSource = ds;
    }

    protected void setConfiguration(DataSourceConfiguration config) {
        this.config = config;
    }

    public DataSourceConfiguration getConfiguration() {
        return this.config;
    }

    protected void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public String getName() {
        return config.getName();
    }

    public String getUrl() {
        return config.getUrl();
    }

    public String getDriver() {
        return config.getDriver();
    }

    public String getDatabaseVendor() {
        return config.getVendor().toString();
    }

    public String getProvider() {
        return config.getProvider().toString();
    }

    public String getUsername() {
        return config.getUsername();
    }

    public String getValidationQuery() {
        return config.getValidationQuery();
    }

}
