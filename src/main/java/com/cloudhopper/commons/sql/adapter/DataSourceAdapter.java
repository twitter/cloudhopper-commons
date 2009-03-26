
package com.cloudhopper.commons.sql.adapter;

// java imports
import javax.sql.DataSource;

// third party imports

// my imports
import com.cloudhopper.commons.sql.*;

/**
 * Adapter to dynamically create a new instance of a DataSource.  Adapters
 * must be able to "late bind" to any necessary third party jars by using
 * reflection, etc. in order to only require a runtime requirement instead of
 * compile-time.
 * 
 * @author joelauer
 */
public interface DataSourceAdapter {

    /**
     * Whether this adapter provides a pooled DataSource.
     * @return True if this DataSource is pooled, otherwise false.
     */
    public boolean isPooled();

    /**
     * Whether this adapter is capable of providing a JMX interface for management
     * @return True if this adapter can provide JMX management, otherwise false.
     */
    public boolean isJmxCapable();

    /**
     * Creates a new DataSource using the configuration. 
     * @param config The configuration to use when creating the DataSource.  There
     *      are certain properties that will be pre-checked before this method
     *      gets called to verify properties are correct.
     * @return
     * @throws SQLMissingDependencyException
     * @throws SQLConfigurationException
     */
    public ManagedDataSource create(DataSourceConfiguration config) throws SQLMissingDependencyException, SQLConfigurationException;

}
