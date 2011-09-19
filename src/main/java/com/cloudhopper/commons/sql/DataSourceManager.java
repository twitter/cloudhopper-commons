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

package com.cloudhopper.commons.sql;

// java imports
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.sql.DataSource;

// third party imports
import org.apache.log4j.Logger;

// my imports
import com.cloudhopper.commons.sql.adapter.DataSourceAdapter;

/**
 * Class for creating, managing, and monitoring of the life-cycle of DataSources.
 * 
 * @author joelauer
 */
public class DataSourceManager {
    
    private static final Logger logger = Logger.getLogger(DataSourceManager.class);

    private DataSourceManager() {
        // do nothing
    }

    /**
     * Verifies a DataSource is functional.  This method will call getConnection()
     * on the DataSource and if that fails a SQLException will be thrown.
     * @param ds The DataSource to verify
     * @throws java.sql.SQLException Thrown if the call to getConnection() fails
     *      to obtain a SQL connection.
     */
    public static void verify(DataSource ds) throws SQLException {
        Connection conn = null;
        try {
            // get a connection -- this will test the connection works
            conn = ds.getConnection();
        } finally {
            JdbcUtil.close(conn);
        }
    }

    /**
     * Creates a new DataSource from the DataSourceConfiguration.
     * @param configuration The configuration of the DataSource
     * @return A new DataSource tied to the configuration
     * @throws SQLMissingDependencyException Thrown if a dependency (external jar)
     *      is missing from the configuration.
     * @throws SQLConfigurationException Thrown if there was an error while
     *      configuring the DataSource.
     */
    public static DataSource create(DataSourceConfiguration configuration) throws SQLMissingDependencyException, SQLConfigurationException {
        // verify all required properties are configured and set
        configuration.validate();

        // clone the configuration so we can save the properties used to create the ds
        DataSourceConfiguration config = (DataSourceConfiguration)configuration.clone();

        // verify database driver exists and try to register the database driver
        try {
            // create a new instance
            Driver driver = (Driver)Class.forName(config.getDriver()).newInstance();
            // put out some properties to save 'em?
        } catch (Exception e) {
            throw new SQLMissingDependencyException("Database driver '" + config.getDriver() + "' failed to load. Perhaps missing jar file?", e);
        }

        // get the class responsible for creating the datasource
        String adapterClass = config.getProvider().getAdapter();

        // create a new instance of the adapter
        DataSourceAdapter adapter = null;
        try {
            adapter = (DataSourceAdapter)Class.forName(adapterClass).newInstance();
        } catch (Exception e) {
            //throw new SQLConfigurationException("Invalid DataSourceAdapter class specified. Should be impossible error?");
            throw new SQLMissingDependencyException("DataSourceAdapter '" + adapterClass + "' failed to load. Perhaps missing jar file?", e);
        }

        // delegate creating the new datasource to the adapter
        ManagedDataSource mds = adapter.create(config);

        // if the user requested this datasource to be added to jmx
        if (config.getJmx()) {
            // hmm... if jmx is turned on, let's register the MBean
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            try {
                ObjectName name = new ObjectName(config.getJmxDomain() + ":type=ManagedDataSource,name=" + config.getName());
                mbs.registerMBean(mds, name);
            } catch (Exception e) {
                // log the error, but don't throw an exception for this datasource
                logger.error("Error while attempting to register ManagedDataSourceMBean '" + config.getName() + "'", e);
            }
        }

        // return the datasource
        return mds.getDataSource();
    }
}
