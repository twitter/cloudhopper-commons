
package com.cloudhopper.commons.sql;

// java imports
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.sql.DataSource;

// third party imports
import org.apache.log4j.Logger;

// my imports
import com.cloudhopper.commons.sql.adapter.DataSourceAdapter;
import java.sql.Driver;

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
