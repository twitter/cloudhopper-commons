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

package com.cloudhopper.commons.sql;

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

/**
 * Enumeration of DataSource providers.
 * 
 * @author joelauer
 */
public enum DataSourceProvider {

    /** c3p0 datasource pooling provider */
    C3P0("com.cloudhopper.commons.sql.c3p0.C3P0DataSourceAdapter"),
    /** Proxool datasource pooling provider */
    PROXOOL("com.cloudhopper.commons.sql.proxool.ProxoolDataSourceAdapter"),
    /** Basic datasource provider, no pooling */
    BASIC("com.cloudhopper.commons.sql.adapter.BasicDataSourceAdapter");

    private final String adapter;

    DataSourceProvider(final String adapter) {
        this.adapter = adapter;
    }

    /**
     * Gets the adapter for this provider.
     */
    public String getAdapter() {
        return this.adapter;
    }

}
