
package com.cloudhopper.commons.sql.adapter;

// java imports
import javax.sql.DataSource;

// third party imports
import org.apache.log4j.Logger;

// my imports
import com.cloudhopper.commons.sql.DataSourceConfiguration;
import com.cloudhopper.commons.sql.ManagedDataSource;

/**
 * A basic implementation of a "Managed" DataSource.
 * @author joelauer
 */
public class BasicManagedDataSource extends ManagedDataSource {

    private static final Logger logger = Logger.getLogger(BasicManagedDataSource.class);

    public BasicManagedDataSource(DataSourceConfiguration config, DataSource ds) {
        super(config, ds);
    }

    /** Not supported by default */
    public Integer getMinConnectionCount() {
        return null;
    }

    public Integer getMaxConnectionCount() {
        return null;
    }

    public Integer getIdleConnectionCount() {
        return null;
    }

}
