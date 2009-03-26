
package com.cloudhopper.commons.sql;

import com.cloudhopper.commons.sql.adapter.DataSourceAdapter;
import javax.sql.DataSource;

/**
 * A DataSource wrapper that provides "managed" functionality such as JMX.
 * 
 * @author joelauer
 */
public abstract class ManagedDataSource implements ManagedDataSourceMBean {

    private DataSourceConfiguration config;
    private DataSource dataSource;
    private DataSourceAdapter adapter;

    public ManagedDataSource(DataSourceAdapter adapter, DataSourceConfiguration config, DataSource ds) {
        this.adapter = adapter;
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

    public Integer getMinPoolSize() {
        if (adapter.isPooled()) {
            return config.getMinPoolSize();
        } else {
            return null;
        }
    }

    public Integer getMaxPoolSize() {
        if (adapter.isPooled()) {
            return config.getMaxPoolSize();
        } else {
            return null;
        }
    }

    public Long getActiveConnectionTimeout() {
        if (adapter.isPooled()) {
            return config.getActiveConnectionTimeout();
        } else {
            return null;
        }
    }

    public Long getIdleConnectionTimeout() {
        if (adapter.isPooled()) {
            return config.getIdleConnectionTimeout();
        } else {
            return null;
        }
    }

    public Long getValidateIdleConnectionTimeout() {
        if (adapter.isPooled()) {
            return config.getValidateIdleConnectionTimeout();
        } else {
            return null;
        }
    }

    public Boolean getValidateOnCheckin() {
        if (adapter.isPooled()) {
            return config.getValidateOnCheckin();
        } else {
            return null;
        }
    }

    public Boolean getValidateOnCheckout() {
        if (adapter.isPooled()) {
            return config.getValidateOnCheckout();
        } else {
            return null;
        }
    }

}
