package com.cloudhopper.commons.sql.adapter;

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

import com.cloudhopper.commons.sql.*;
import javax.sql.DataSource;

/**
 * Adapter for creating a BasicDataSource which is a simple wrapper that
 * obtains connections from the DriverManager.
 * <br><br>
 * <b>NOTE:</b> This adapter does not provide pooled connections.
 * 
 * @author joelauer
 */
public class BasicDataSourceAdapter implements DataSourceAdapter {

    public boolean isPooled() {
        return false;
    }

    public boolean isJmxCapable() {
        return false;
    }

    public ManagedDataSource create(DataSourceConfiguration config) throws SQLMissingDependencyException, SQLConfigurationException {
        // try to register the database driver
        try {
            // create a new instance
            Class.forName(config.getDriver()).newInstance();
        } catch (Exception e) {
            throw new SQLMissingDependencyException("Database driver '" + config.getDriver() + "' failed to load. Perhaps missing jar file?", e);
        }

        // create basic DataSource wrapper to handle calls to the DriverManager
        BasicDataSource ds = new BasicDataSource(config.getUrl(), config.getUsername(), config.getPassword());

        // done creating datasource wrapper, return it
        return new BasicManagedDataSource(this, config, ds);
    }
}
