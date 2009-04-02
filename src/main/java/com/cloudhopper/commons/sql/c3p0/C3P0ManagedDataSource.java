
package com.cloudhopper.commons.sql.c3p0;

// third party imports
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

// my imports
import com.cloudhopper.commons.sql.DataSourceConfiguration;
import com.cloudhopper.commons.sql.ManagedDataSource;
import com.cloudhopper.commons.sql.adapter.DataSourceAdapter;

/**
 * An implementation of a C3P0 "Managed" DataSource.
 * @author joelauer
 */
public class C3P0ManagedDataSource extends ManagedDataSource {

    private static final Logger logger = Logger.getLogger(C3P0ManagedDataSource.class);

    // additional reference to the actual C3P0 type -- this is required
    // to monitor and track certain values for this particular datasource
    private ComboPooledDataSource cpds;

    public C3P0ManagedDataSource(DataSourceAdapter adapter, DataSourceConfiguration config, ComboPooledDataSource cpds) {
        super(adapter, config, cpds);
        this.cpds = cpds;
    }

    public Integer getIdleConnectionCount() {
        try {
            return cpds.getNumIdleConnectionsAllUsers();
        } catch (Exception e) {
            logger.error("Error while getting idle connection count", e);
            return null;
        }
    }

    public Integer getBusyConnectionCount() {
        try {
            return cpds.getNumBusyConnectionsAllUsers();
        } catch (Exception e) {
            logger.error("Error while getting busy connection count", e);
            return null;
        }
    }

    public Integer getConnectionCount() {
        try {
            return cpds.getNumConnectionsAllUsers();
        } catch (Exception e) {
            logger.error("Error while getting connection count", e);
            return null;
        }
    }

}
