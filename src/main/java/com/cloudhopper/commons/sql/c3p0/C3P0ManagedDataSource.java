
package com.cloudhopper.commons.sql.c3p0;

// third party imports
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

// my imports
import com.cloudhopper.commons.sql.DataSourceConfiguration;
import com.cloudhopper.commons.sql.ManagedDataSource;

/**
 * An implementation of a C3P0 "Managed" DataSource.
 * @author joelauer
 */
public class C3P0ManagedDataSource extends ManagedDataSource {

    private static final Logger logger = Logger.getLogger(C3P0ManagedDataSource.class);

    // additional reference to the actual C3P0 type -- this is required
    // to monitor and track certain values for this particular datasource
    private ComboPooledDataSource cpds;

    public C3P0ManagedDataSource(DataSourceConfiguration config, ComboPooledDataSource cpds) {
        super(config, cpds);
        this.cpds = cpds;
    }

    public Integer getMinConnectionCount() {
        return cpds.getMinPoolSize();
    }

    public Integer getMaxConnectionCount() {
        return cpds.getMaxPoolSize();
    }

    public Integer getIdleConnectionCount() {
        try {
            return cpds.getNumIdleConnectionsAllUsers();
        } catch (Exception e) {
            logger.error("Error while getting idle connection count", e);
            return null;
        }
    }

}
