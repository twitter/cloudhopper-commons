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
