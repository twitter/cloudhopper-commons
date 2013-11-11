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
package com.cloudhopper.commons.sql.c3p0;

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

// java imports
import com.cloudhopper.commons.sql.adapter.*;
import java.beans.PropertyVetoException;

// third party imports
import com.mchange.v2.c3p0.ComboPooledDataSource;

// my imports
import com.cloudhopper.commons.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter for a c3p0 DataSource.
 * 
 * @author joelauer
 */
public class C3P0DataSourceAdapter implements DataSourceAdapter {

    private static Logger logger = LoggerFactory.getLogger(C3P0DataSourceAdapter.class);

    public boolean isPooled() {
        return true;
    }

    public boolean isJmxCapable() {
        return true;
    }

    public ManagedDataSource create(DataSourceConfiguration config) throws SQLMissingDependencyException, SQLConfigurationException {

        //
        // http://www.mchange.com/projects/c3p0/index.html#configuration_properties
        //

        //
        // these system properties need turned off prior to creating our first
        // instance of the ComboPooledDataSource, otherwise, they are ignored

        // turn off VMID stuff (causes long ugly names for datasources)
        System.setProperty("com.mchange.v2.c3p0.VMID", "NONE");

        // jmx is off by default
        if (!config.getJmx()) {
            // apparently, c3p0 does this with a system-wide property
            // com.mchange.v2.c3p0.management.ManagementCoordinator=com.mchange.v2.c3p0.management.NullManagementCoordinator
            System.setProperty("com.mchange.v2.c3p0.management.ManagementCoordinator", "com.mchange.v2.c3p0.management.NullManagementCoordinator");
        } else {
            System.setProperty("com.mchange.v2.c3p0.management.ManagementCoordinator", "com.cloudhopper.commons.sql.c3p0.C3P0CustomManagementCoordinator");
        }

        // set the JMX domain for the C3P0
        C3P0CustomManagementCoordinator.setJmxDomainOnce(config.getJmxDomain());

        // create a new instance of the c3p0 datasource
        ComboPooledDataSource cpds = new ComboPooledDataSource(true);

        // set properties
        try {
            // set required properties
            cpds.setDriverClass(config.getDriver());
            cpds.setUser(config.getUsername());
            cpds.setPassword(config.getPassword());
            cpds.setJdbcUrl(config.getUrl());

            // set optional properties
            cpds.setDataSourceName(config.getName());
            cpds.setMinPoolSize(config.getMinPoolSize());
            cpds.setMaxPoolSize(config.getMaxPoolSize());
            // we'll set the initial pool size to the minimum size
            cpds.setInitialPoolSize(config.getMinPoolSize());

            // set the validation query
            cpds.setPreferredTestQuery(config.getValidationQuery());

            // amount of time (in ms) to wait for getConnection() to succeed
            cpds.setCheckoutTimeout((int)config.getCheckoutTimeout());

            // checkin validation
            cpds.setTestConnectionOnCheckin(config.getValidateOnCheckin());

            // checkout validation
            cpds.setTestConnectionOnCheckout(config.getValidateOnCheckout());

            // amount of time to wait to validate connections
            // NOTE: in seconds
            int seconds = (int)(config.getValidateIdleConnectionTimeout()/1000);
            cpds.setIdleConnectionTestPeriod(seconds);

            // set idleConnectionTimeout
            // NOTE: in seconds
            seconds = (int)(config.getIdleConnectionTimeout()/1000);
            cpds.setMaxIdleTimeExcessConnections(seconds);

            // set activeConnectionTimeout
            seconds = (int)(config.getActiveConnectionTimeout()/1000);
            cpds.setUnreturnedConnectionTimeout(seconds);

            if (config.getDebug()) {
                cpds.setDebugUnreturnedConnectionStackTraces(true);
            } else {
                cpds.setDebugUnreturnedConnectionStackTraces(false);
            }

            // properties I think aren't valid for c3p0
            // defines how many times c3p0 will try to acquire a new Connection from the database before giving up.
            cpds.setAcquireRetryAttempts(10);

        } catch (PropertyVetoException e) {
            throw new SQLConfigurationException("Property was vetoed during configuration", e);
        }

        /**
        // configure c3p0 defaults that seem to make more sense
        /**
         *  c3p0.acquireIncrement	hibernate.c3p0.acquire_increment
            c3p0.idleConnectionTestPeriod	hibernate.c3p0.idle_test_period
            c3p0.initialPoolSize	not available -- uses minimum size
            c3p0.maxIdleTime	hibernate.c3p0.timeout
            c3p0.maxPoolSize	hibernate.c3p0.max_size
            c3p0.maxStatements	hibernate.c3p0.max_statements
            c3p0.minPoolSize	hibernate.c3p0.min_size
            c3p0.testConnectionsOnCheckout 	hibernate.c3p0.validate hibernate 2.x only!
         */
        
        return new C3P0ManagedDataSource(this, config, cpds);
    }

}
