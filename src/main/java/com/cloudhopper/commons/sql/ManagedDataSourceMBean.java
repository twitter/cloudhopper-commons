
package com.cloudhopper.commons.sql;

/**
 * Defines the MBean properties and methods exposed for a ManagedDataSource.
 *
 * @author joelauer
 */
public interface ManagedDataSourceMBean {

    /** Configuration Attributes */
    public String getName();
    public String getUrl();
    public String getDriver();
    public String getDatabaseVendor();
    public String getProvider();
    public String getUsername();

    /** Monitoring Attributes */
    public Integer getMinConnectionCount();
    public Integer getMaxConnectionCount();
    public Integer getIdleConnectionCount();

}
