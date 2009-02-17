
package net.cloudhopper.commons.sql;

import org.apache.log4j.Logger;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.Validate;
import org.apache.commons.configuration.Configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Factory to create DataSource objects.
 *
 */
public class DataSourceFactory {
    private static final Logger logger = Logger.getLogger(DataSourceFactory.class);

    private static final String DEFAULT = "default";

    private static final String DEFAULT_JNDI_DATA_SOURCE = "loginConfiguration";

    private static DataSourceFactory instance;

    private Map<String, DataSource> dataSourceMap;

    private DataSourceFactory() {
        dataSourceMap = new HashMap<String, DataSource>();
    }

    /**
     * Gets a connection to the default dto source.
     * @return a connection to the default dto source
     */
    public Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException ex) {
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets the singleton instance of this factory.
     * @return  this dto source factory
     */
    public static DataSourceFactory getInstance() {
        if (instance == null) {
            instance = new DataSourceFactory();
        }
        return instance;
    }

    /**
     * Gets the default dto source.
     * @return  the default dto source
     */
    public DataSource getDataSource() {
        if (!dataSourceMap.containsKey(DEFAULT)) {
            dataSourceMap.put(DEFAULT, getDataSource(DEFAULT_JNDI_DATA_SOURCE));
        }
        return getDataSource(DEFAULT);
    }

    /**
     * Gets a dto source based on the specified JNDI name.  Appends "jdbc/"
     * in front of the name before doing a lookup.
     * @param name  JNDI name of desired dto source
     * @return DataSource to which the user is logged in
     */
    public DataSource getDataSource(String name) {
        if (dataSourceMap.containsKey(name)) {
            return dataSourceMap.get(name);
        }

        try {
            // grab jndi context for db connection
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource ds = (DataSource)envCtx.lookup("jdbc/" + name);
            dataSourceMap.put(name, ds);

            envCtx.close();
            initCtx.close();

            return ds;
        } catch (Exception ex) {
            String msg = "Cannot get datasource with name = " + name;
            logger.fatal(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    /**
     * Gets a dto source based on the credentials specified in the config properties.
     * Assumes the config object contains the following properties: db.driver, db.url,
     * db.username, db.password.
     * @param configProps properties containing database credentials
     * @return DataSource to which the user is logged in
     */
    public DataSource getDataSource(Properties configProps) {
        String driver = configProps.getProperty("db.driver");
        String url = configProps.getProperty("db.url");
        String username = configProps.getProperty("db.username");
        String password = configProps.getProperty("db.password");

        return getDataSource(driver, url, username, password);
    }

    /**
     * Gets a dto source based on the credentials specified in the config object.
     * Assumes the config object contains the following properties: driver, url,
     * username, password.  If values were read from a config file containing
     * properties such as db.driver, db.url, db.username, and db.password, then
     * the calling method should use config.subset("db") to get the desired config
     * parameter.
     * @param config Commons configuration containing database credentials
     * @return DataSource to which the user is logged in
     */
    public DataSource getDataSource(Configuration config) {
        String driver = (String) config.getProperty("db.driver");
        String url = (String) config.getProperty("db.url");
        String username = (String) config.getProperty("db.username");
        String password = (String) config.getProperty("db.password");

        return getDataSource(driver, url, username, password);
    }


    /**
     * Gets a dto source based on the specified credentials.
     * @param driver    class name of the database driver
     * @param url       database url
     * @param username  username to use
     * @param password  password to use
     * @return DataSource to which the user is logged in
     */
    public DataSource getDataSource(String driver, String url, String username, String password) {
        Validate.notEmpty(driver, "config value not found: db.driver");
        Validate.notEmpty(url, "config value not found: db.url");
        Validate.notEmpty(username, "config value not found: db.username");
        Validate.notNull(password, "config value not found: db.password");

        DataSource oldDataSource = dataSourceMap.get(url);
        if (oldDataSource != null) {
            return oldDataSource;
        }

        try {
            BasicDataSource newDataSource = new BasicDataSource();
            newDataSource.setDriverClassName(driver);
            newDataSource.setUrl(url);
            newDataSource.setUsername(username);
            newDataSource.setPassword(password);
            dataSourceMap.put(url, newDataSource);
            return newDataSource;
        } catch (Exception ex) {
            String message = "Cannot get datasource with url = " + url;
            logger.fatal(message, ex);
            throw new RuntimeException(message, ex);
        }
    }

    public void setDefaultDataSource(DataSource dataSource) {
        dataSourceMap.put(DEFAULT, dataSource);
    }
}
