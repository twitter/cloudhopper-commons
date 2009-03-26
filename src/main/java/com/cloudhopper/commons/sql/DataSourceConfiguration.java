
package com.cloudhopper.commons.sql;

/**
 * Configuration of a DataSource to be used with a DataSourceManager.
 * @author joelauer
 */
public class DataSourceConfiguration implements Cloneable {

    private DataSourceProvider provider;
    private DatabaseVendor vendor;
    private String driver;
    private String name;
    private String url;
    private String username;
    private String password;
    private int minPoolSize;
    private int maxPoolSize;
    private boolean jmx;
    private String jmxDomain;

    private String validationQuery;

    public DataSourceConfiguration() {
        // default provider is c3p0
        provider = DataSourceProvider.C3P0;
        // by default, jmx is turned on
        jmx = true;
        // by default, jmx domain is "com.cloudhopper"
        jmxDomain = "com.cloudhopper";
        // by default, pool size is initially 1 up to 10
        minPoolSize = 1;
        maxPoolSize = 10;
    }

    public void setProvider(DataSourceProvider provider) {
        this.provider = provider;
    }

    public DataSourceProvider getProvider() {
        return this.provider;
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
     * Sets the name of this DataSource such as "main" or "dbname".  This name
     * is used by various implementations for naming a DataSource.  This name is
     * also used in JMX registrations.
     * @param name The name associated with this DataSource
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
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

    public String getUrl() {
        return this.url;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return this.driver;
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

    public String getUsername() {
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setJmx(boolean jmx) {
        this.jmx = jmx;
    }

    public boolean getJmx() {
        return this.jmx;
    }

    /**
     * Sets the domain to use for registering this connection to a JMX MBean
     * server. (e.g. "com.cloudhopper")
     * @param domain The domain to use for registering this DataSource
     */
    public void setJmxDomain(String domain) {
        this.jmxDomain = domain;
    }

    public String getJmxDomain() {
        return this.jmxDomain;
    }

    public void setMinPoolSize(int size) throws SQLConfigurationException {
        // must be > 0
        if (size <= 0) {
            throw new SQLConfigurationException("Minimum pool size must be > 0");
        }
        this.minPoolSize = size;
    }

    public int getMinPoolSize() {
        return this.minPoolSize;
    }

    public void setMaxPoolSize(int size) throws SQLConfigurationException {
        // must be > 0
        if (size <= 0) {
            throw new SQLConfigurationException("Maximum pool size must be > 0");
        }
        this.maxPoolSize = size;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

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

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
    }

}
