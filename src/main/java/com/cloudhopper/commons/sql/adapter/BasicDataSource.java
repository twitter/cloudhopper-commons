package com.cloudhopper.commons.sql.adapter;

// java imports
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.sql.DriverManager;

// my imports

/**
 * Basic DataSource implementation to provide a thin layer to obtain
 * connections directly from the DriverManager.
 * 
 * @author joelauer
 */
public class BasicDataSource implements DataSource {

    private String url;
    private String username;
    private String password;

    public BasicDataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        // driver is already registered by SimpleAdapter
        // create the connection
        return DriverManager.getConnection(url, username, password);
    }

    public Connection getConnection(String user, String pass) throws SQLException {
        // driver is already registered by SimpleAdapter
        // create the connection
        return DriverManager.getConnection(url, user, pass);
    }

    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        // do nothing
    }

    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
