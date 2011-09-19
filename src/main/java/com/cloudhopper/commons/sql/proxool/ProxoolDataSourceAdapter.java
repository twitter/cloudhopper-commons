/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.cloudhopper.commons.sql.proxool;

// java imports
import java.lang.management.ManagementFactory;
import java.util.Properties;

// third party imports
import org.logicalcobwebs.proxool.ProxoolDriver;
import org.logicalcobwebs.proxool.ProxoolFacade;

// my imports
import com.cloudhopper.commons.sql.*;
import com.cloudhopper.commons.sql.adapter.*;
import com.cloudhopper.commons.util.ManagementUtil;
import org.logicalcobwebs.proxool.ProxoolException;

/**
 * Adapter for a Proxool DataSource.
 * 
 * @author joelauer
 */
public class ProxoolDataSourceAdapter implements DataSourceAdapter {

    public boolean isPooled() {
        return true;
    }

    public boolean isJmxCapable() {
        return true;
    }

    public ManagedDataSource create(DataSourceConfiguration config) throws SQLMissingDependencyException, SQLConfigurationException {

        // verify proxool driver exists and register it
        try {
            // create a new instance
            ProxoolDriver driver = new ProxoolDriver();
        } catch (Exception e) {
            throw new SQLMissingDependencyException("Proxool driver '" + ProxoolDriver.class.getName() + "' failed to load. Perhaps missing proxool jar file?", e);
        }

        // create new set of proxool properties
        Properties info = new Properties();

        // setup properties to use
        info.setProperty("proxool.minimum-connection-count", Integer.toString(config.getMinPoolSize()));
        info.setProperty("proxool.maximum-connection-count", Integer.toString(config.getMaxPoolSize()));

        // set the validation query
        info.setProperty("proxool.house-keeping-test-sql", config.getValidationQuery());

        // amount of time (in ms) to wait for getConnection() to succeed
        // not supported by Proxool -- maybe driver dependant????
        //config.getCheckoutTimeout());

        // checkin/checkout validation
        info.setProperty("proxool.test-after-use", Boolean.toString(config.getValidateOnCheckin()));
        info.setProperty("proxool.test-before-use", Boolean.toString(config.getValidateOnCheckout()));

        // amount of time to wait to validate connections
        info.setProperty("proxool.house-keeping-sleep-time", Long.toString(config.getValidateIdleConnectionTimeout()));

        // set idleConnectionTimeout
        // not supported by prooxool -- this is implicitly done...

        // set activeConnectionTimeout
        info.setProperty("proxool.maximum-active-time", Long.toString(config.getActiveConnectionTimeout()));

        // should jmx be turned on/off?
        if (config.getJmx()) {
            // enable jmx for proxool (no support for renaming domain, object name)
            info.setProperty("proxool.jmx", "true");
            // create list of all mbean servers...
            String serverId = ManagementUtil.getMBeanServerId(ManagementFactory.getPlatformMBeanServer());
            // add agent-id property
            info.setProperty("proxool.jmx-agent-id", serverId);
        } else {
            info.setProperty("proxool.jmx", "false");
        }
        
        info.setProperty("user", config.getUsername());
        info.setProperty("password", config.getPassword());
        String alias = config.getName();
        String driverClass = config.getDriver();
        String driverUrl = config.getUrl();

        // create new url for proxool
        String proxoolUrl = "proxool." + alias + ":" + driverClass + ":" + driverUrl;

        // register this connection
        // normally, you'd do this ProxoolFacade.registerConnectionPool(url, info);
        try {
            ProxoolFacade.registerConnectionPool(proxoolUrl, info);
        } catch (ProxoolException e) {
            throw new SQLConfigurationException("Failed while registering proxool connection", e);
            //throw new SQLMissingDependencyException("Proxool '" + PROXOOL_FACADE_CLASS + "' failed to load. Perhaps missing proxool jar file?", e);
        }

        // create basic DataSource wrapper to handle calls to the DriverManager
        BasicDataSource ds = new BasicDataSource(proxoolUrl, config.getUsername(), config.getPassword());

        // done creating datasource wrapper, return it
        return new ProxoolManagedDataSource(this, config, ds);
    }

}
