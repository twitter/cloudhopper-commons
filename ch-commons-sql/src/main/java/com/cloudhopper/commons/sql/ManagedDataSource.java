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
