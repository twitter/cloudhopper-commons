
package com.cloudhopper.commons.sql;

import com.cloudhopper.commons.sql.c3p0.C3P0DataSourceAdapter;
import com.cloudhopper.commons.sql.proxool.ProxoolDataSourceAdapter;
import com.cloudhopper.commons.sql.adapter.BasicDataSourceAdapter;
import com.cloudhopper.commons.sql.adapter.*;

/**
 * Enumeration of DataSource providers.
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
