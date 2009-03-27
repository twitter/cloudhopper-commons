
package com.cloudhopper.commons.sql;

/**
 * Defines the MBean properties and methods exposed for a ManagedDataSource.
 *
 * @author joelauer
 */
public interface ManagedDataSourceMBean {

    // Configuration Attributes

    public String getName();
    public String getUrl();
    public String getDriver();
    public String getDatabaseVendor();
    public String getProvider();
    public String getUsername();
    public String getValidationQuery();

    public Integer getMinPoolSize();
    public Integer getMaxPoolSize();
    public Long getActiveConnectionTimeout();
    public Long getIdleConnectionTimeout();
    public Long getValidateIdleConnectionTimeout();
    public Boolean getValidateOnCheckin();
    public Boolean getValidateOnCheckout();

    // Monitoring Attributes

    public Integer getConnectionCount();
    public Integer getIdleConnectionCount();
    public Integer getBusyConnectionCount();


}
