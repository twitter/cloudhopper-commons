/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

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
     * @return A managed DataSource
     * @throws SQLMissingDependencyException
     * @throws SQLConfigurationException
     */
    public ManagedDataSource create(DataSourceConfiguration config) throws SQLMissingDependencyException, SQLConfigurationException;

}
