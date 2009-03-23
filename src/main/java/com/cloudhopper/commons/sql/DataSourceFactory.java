
package com.cloudhopper.commons.sql;

import javax.sql.DataSource;

/**
 * Class for configuring a provider for obtaining a JDBC connection.
 * 
 * @author joelauer
 */
public class DataSourceFactory {

    private DataSourceProvider provider;
    
    private DatabaseVendor vendor;
    private String driver;

    private String name;
    private String url;
    private String username;
    private String password;
    
    private String validationQuery;

    public DataSourceFactory() {
        // default provider is c3p0
        provider = DataSourceProvider.C3P0;
    }

    public void setProvider(DataSourceProvider provider) {
        this.provider = provider;
    }

    /**
     * Sets the database vendor associated with this datasource.  For example,
     * connecting to a MySQL database would have this property set to MYSQL.
     * This property is automatically set by the "url" property.
     * @param vendor The database vendor
     */
    public void setVendor(DatabaseVendor vendor) {
        // attempt to automatically determine the database driver if it hasn't been set
        setDriverIfNotSet(vendor.getDefaultDriver());
        // set the database vendor
        this.vendor = vendor;
    }

    /**
     * Internal method to set the database vendor if it hasn't already been set.
     * @param vendor
     */
    private void setVendorIfNotSet(DatabaseVendor vendor) {
        if (this.vendor == null) {
            setVendor(vendor);
        }
    }

    /**
     * Gets the database vendor associated with this datasource. For example,
     * connecting to a MySQL database would have this property set to MYSQL.
     * This property is automatically set by the "url" property.
     * @return The database vendor
     */
    public DatabaseVendor getVendor() {
        return this.vendor;
    }

    /**
     * Sets the name of this datasource such as "main" or "dbname".
     * @param name The name associated with this datasource
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the JDBC url to connect to the database. In JDBC, all url's begin
     * with jdbc:protocol: This is the standard. After this is driver specific,
     * and no two drivers are the same. This method will partially parse the
     * URL to extract out various properties to assist in configuring this factory.
     * 
     * HSQL: jdbc:hsqldb:mem:
     *      jdbc:hsqldb:file:
     * 
     * MySQL: jdbc:mysql://[host:port],[host:port].../[database][?propertyName1][=propertyValue1][&propertyName2][=propertyValue2]
     *      jdbc:mysql://localhost:3306/stratus001?useTimezone=true&useLegacyDatetimeCode=false&serverTimezone=UTC
     *
     * PostgreSQL: jdbc:postgresql:database
     *      jdbc:postgresql://host/database
     *      jdbc:postgresql://host:port/database
     *      jdbc:postgresql://localhost:5432/testdb?charSet=LATIN1
     *
     * JTDS: jdbc:jtds:<server_type>://<server>[:<port>][/<database>][;<property>=<value>[;...]]
     *      jdbc:jtds:sqlserver://localhost/dbname
     *      jdbc:jtds:sybase://localhost/dbname
     *
     * Microsoft: jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
     *      jdbc:sqlserver://localhost;databaseName=AdventureWorks;integratedSecurity=true;applicationName=MyApp;
     *      jdbc:sqlserver://localhost:1433;instanceName=instance1;databaseName=AdventureWorks;integratedSecurity=true;applicationName=MyApp;
     *
     * @param url
     * @throws com.cloudhopper.commons.sql.SQLConfigurationException
     */
    public void setUrl(String url) throws SQLConfigurationException {
        // make sure the url starts with "jdbc:"
        if (!url.startsWith("jdbc:")) {
            throw new SQLConfigurationException("Invalid JDBC URL. Does not start with 'jdbc:'");
        }

        // attempt to parse the protocol from the JDBC url -- this is the portion
        // after the initial "jdbc:protocol:" -- try to find the next ':' char after the first 5
        int protocolPos = url.indexOf(":", 5);
        if (protocolPos <= 0) {
            throw new SQLConfigurationException("Invalid JDBC URL. Does not start with a protocol that ends in ':' such as 'jdbc:protocol:'");
        }
        String protocol = url.substring(5, protocolPos);

        // attempt to parse the next protocol token
        int subProtocolPos = url.indexOf(":", protocolPos+1);
        String subProtocol = "";
        if (subProtocolPos > 0) {
            subProtocol = url.substring(protocolPos+1, subProtocolPos);
        }

        // attempt to map JDBC url to set the vendor -- this will set the vendor
        // and also the driver if those properties have not yet been set
        if (protocol.equals("mysql")) {
            setVendorIfNotSet(DatabaseVendor.MYSQL);
        } else if (protocol.equals("jtds") && subProtocol.equals("sqlserver")) {
            setVendorIfNotSet(DatabaseVendor.MSSQL);
        } else {
            throw new SQLConfigurationException("Unsupported protocol '" + protocol + (!subProtocol.equals("") ? ":" + subProtocol : "") + "' in JDBC URL. Add mapping to DataSourceFactory?");
        }

        // everything went ok, save the url
        this.url = url;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * Internal method to set the database driver if it hasn't already been set.
     * @param vendor
     */
    private void setDriverIfNotSet(String driver) {
        if (this.driver == null) {
            setDriver(driver);
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DataSource createDataSource() throws SQLConfigurationException {
        // FIXME: verify database driver exists?

        return null;
    }

    // pooling properties...
    /**
    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }
     */

    /**
    <proxool>
    <alias>xml-test</alias>
    <driver-url>jdbc:hsqldb:.</driver-url>
    <driver-class>org.hsqldb.jdbcDriver</driver-class>
    <driver-properties>
      <property name="user" value="sa"/>
      <property name="password" value=""/>
    </driver-properties>
    <maximum-connection-count>10</maximum-connection-count>
    <house-keeping-test-sql>select CURRENT_DATE</house-keeping-test-sql>
    </proxool>
     */

    /**
     * <Resource name="jdbc/confluence" auth="Container" type="javax.sql.DataSource"
         username="yourusername"
         password="yourpassword"
         driverClassName="com.mysql.jdbc.Driver"
         url="jdbc:mysql://localhost:3306/confluence?autoReconnect=true"
         maxActive="15"
         maxIdle="7"
         validationQuery="Select 1" />

     */

    /**
    * driverClassName - Fully qualified Java class name of the JDBC driver to be used.
    * maxActive - The maximum number of active instances that can be allocated from this pool at the same time.
    * maxIdle - The maximum number of connections that can sit idle in this pool at the same time.
    * maxWait - The maximum number of milliseconds that the pool will wait (when there are no available connections) for a connection to be returned before throwing an exception.
    * password - Database password to be passed to our JDBC driver.
    * url - Connection URL to be passed to our JDBC driver. (For backwards compatibility, the property driverName is also recognized.)
    * user - Database username to be passed to our JDBC driver.
    * validationQuery - SQL query that can be used by the pool to validate connections before they are returned to the application. If specified, this query MUST be an SQL SELECT statement that returns at least one row.
     */

    @Override
    public String toString() {
        return new StringBuilder(200)
                .append("[name=")
                .append(this.name)
                .append(", provider=")
                .append(this.provider)
                .append(", vendor=")
                .append(this.vendor)
                .append(", driver=")
                .append(this.driver)
                .append(", url={")
                .append(this.url)
                .append("}, username=")
                .append(this.username)
                .append(", password=")
                .append((this.username == null ? "null" : "*****"))
                .append("]")
                .toString();
    }
}
