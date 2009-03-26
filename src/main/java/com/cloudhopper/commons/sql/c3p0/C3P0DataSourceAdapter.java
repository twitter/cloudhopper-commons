package com.cloudhopper.commons.sql.c3p0;

// java imports
import com.cloudhopper.commons.sql.adapter.*;
import com.cloudhopper.commons.sql.util.ReflectionUtil;
import java.beans.PropertyVetoException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.sql.DriverManager;

// third party imports
import com.mchange.v2.c3p0.ComboPooledDataSource;

// my imports
import com.cloudhopper.commons.sql.*;
import org.apache.log4j.Logger;

/**
 * Adapter for a c3p0 datasource.
 * 
 * @author joelauer
 */
public class C3P0DataSourceAdapter implements DataSourceAdapter {

    private static Logger logger = Logger.getLogger(C3P0DataSourceAdapter.class);

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
            // setup the validation query
            cpds.setPreferredTestQuery(config.getValidationQuery());

            // amount of time (in ms) to wait for getConnection() to succeed
            cpds.setCheckoutTimeout((int)config.getCheckoutTimeout());

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
        
        return new C3P0ManagedDataSource(config, cpds);
    }

}
