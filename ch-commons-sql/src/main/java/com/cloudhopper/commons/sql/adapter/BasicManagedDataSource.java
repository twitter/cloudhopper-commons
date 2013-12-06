package com.cloudhopper.commons.sql.adapter;

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

import com.cloudhopper.commons.sql.DataSourceConfiguration;
import com.cloudhopper.commons.sql.ManagedDataSource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic implementation of a "Managed" DataSource.
 * 
 * @author joelauer
 */
public class BasicManagedDataSource extends ManagedDataSource {

    private static final Logger logger = LoggerFactory.getLogger(BasicManagedDataSource.class);

    public BasicManagedDataSource(DataSourceAdapter adapter, DataSourceConfiguration config, DataSource ds) {
        super(adapter, config, ds);
    }

    /** Not supported by default */
    public Integer getIdleConnectionCount() {
        return null;
    }

    public Integer getConnectionCount() {
        return null;
    }

    public Integer getBusyConnectionCount() {
        return null;
    }

}
