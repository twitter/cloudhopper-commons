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

package com.cloudhopper.commons.sql.c3p0;

/*
 * #%L
 * ch-commons-sql
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

// third party imports
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// my imports
import com.cloudhopper.commons.sql.DataSourceConfiguration;
import com.cloudhopper.commons.sql.ManagedDataSource;
import com.cloudhopper.commons.sql.adapter.DataSourceAdapter;

/**
 * An implementation of a C3P0 "Managed" DataSource.
 * @author joelauer
 */
public class C3P0ManagedDataSource extends ManagedDataSource {

    private static final Logger logger = LoggerFactory.getLogger(C3P0ManagedDataSource.class);

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
