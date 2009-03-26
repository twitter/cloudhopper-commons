package com.cloudhopper.commons.sql.proxool;

// java imports
import com.cloudhopper.commons.sql.adapter.*;
import com.cloudhopper.commons.sql.util.ReflectionUtil;
import javax.sql.DataSource;
import java.util.Properties;

// my imports
import com.cloudhopper.commons.sql.*;
import com.cloudhopper.commons.sql.adapter.BasicDataSource;
import com.cloudhopper.commons.sql.util.JmxUtil;
import java.lang.management.ManagementFactory;

/**
 * Adapter for a Proxool datasource.
 * 
 * @author joelauer
 */
public class ProxoolDataSourceAdapter implements DataSourceAdapter {

    private static String PROXOOL_CLASS = "org.logicalcobwebs.proxool.ProxoolDriver";
    private static String PROXOOL_FACADE_CLASS = "org.logicalcobwebs.proxool.ProxoolFacade";

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
            Class.forName(PROXOOL_CLASS).newInstance();
        } catch (Exception e) {
            throw new SQLMissingDependencyException("Proxool driver '" + PROXOOL_CLASS + "' failed to load. Perhaps missing proxool jar file?", e);
        }

        // create new set of proxool properties
        Properties info = new Properties();

        //info.setProperty("proxool.maximum-connection-count", "10");
        //info.setProperty("proxool.house-keeping-test-sql", "select CURRENT_DATE");

        // should jmx be turned on/off?
        if (config.getJmx()) {
            // enable jmx for proxool (no support for renaming domain, object name)
            info.setProperty("proxool.jmx", "true");
            // create list of all mbean servers...
            String serverId = JmxUtil.getMBeanServerId(ManagementFactory.getPlatformMBeanServer());
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
        String url = "proxool." + alias + ":" + driverClass + ":" + driverUrl;

        // register this connection
        // normally, you'd do this ProxoolFacade.registerConnectionPool(url, info);
        try {
            Class facadeClass = Class.forName(PROXOOL_FACADE_CLASS);
            ReflectionUtil.callStatic("registerConnectionPool", facadeClass, url, info);
        } catch (ClassNotFoundException e) {
            throw new SQLMissingDependencyException("Proxool '" + PROXOOL_FACADE_CLASS + "' failed to load. Perhaps missing proxool jar file?", e);
        }

        // create basic DataSource wrapper to handle calls to the DriverManager
        BasicDataSource ds = new BasicDataSource(config.getUrl(), config.getUsername(), config.getPassword());

        // done creating datasource wrapper, return it
        return new BasicManagedDataSource(config, ds);
    }

}
