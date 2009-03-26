package com.cloudhopper.commons.sql.adapter;

// java imports
import javax.sql.DataSource;

// my imports
import com.cloudhopper.commons.sql.*;

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
