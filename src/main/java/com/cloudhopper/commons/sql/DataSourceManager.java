
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

/**
 * Class for creating and destroying DataSources.
 * @author joelauer
 */
public class DataSourceManager {
    
    private static final Logger logger = Logger.getLogger(DataSourceManager.class);

    private DataSourceManager() {
        // do nothing
    }
    
    public static DataSource create(DataSourceConfiguration configuration) throws SQLMissingDependencyException, SQLConfigurationException {
        // verify all required properties are configured and set

        // clone the configuration so we can save the properties used to create the ds
        DataSourceConfiguration config = (DataSourceConfiguration)configuration.clone();

        // verify database driver exists and try to register the database driver
        try {
            // create a new instance
            Class.forName(config.getDriver()).newInstance();
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
            throw new SQLConfigurationException("Invalid DataSourceAdapter class specified. Should be impossible error?");
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
     
    /**
    <proxool>
    <alias>xml-test</alias>
    <driver-url>jdbc:hsqldb:.</driver-url>
    <driver-class>org.hsqldb.jdbcDriver</driver-class>
    <driver-properties>
      <property name="user" value="sa"/>
      <property name="password" value=""/>
    </driver-properties>
    <maximum-connection-count>10</maximum-connection-count>
    <house-keeping-test-sql>select CURRENT_DATE</house-keeping-test-sql>
    </proxool>
     */

    /**
     * <Resource name="jdbc/confluence" auth="Container" type="javax.sql.DataSource"
         username="yourusername"
         password="yourpassword"
         driverClassName="com.mysql.jdbc.Driver"
         url="jdbc:mysql://localhost:3306/confluence?autoReconnect=true"
         maxActive="15"
         maxIdle="7"
         validationQuery="Select 1" />

     */

    /**
    * driverClassName - Fully qualified Java class name of the JDBC driver to be used.
    * maxActive - The maximum number of active instances that can be allocated from this pool at the same time.
    * maxIdle - The maximum number of connections that can sit idle in this pool at the same time.
    * maxWait - The maximum number of milliseconds that the pool will wait (when there are no available connections) for a connection to be returned before throwing an exception.
    * password - Database password to be passed to our JDBC driver.
    * url - Connection URL to be passed to our JDBC driver. (For backwards compatibility, the property driverName is also recognized.)
    * user - Database username to be passed to our JDBC driver.
    * validationQuery - SQL query that can be used by the pool to validate connections before they are returned to the application. If specified, this query MUST be an SQL SELECT statement that returns at least one row.
     */
}
