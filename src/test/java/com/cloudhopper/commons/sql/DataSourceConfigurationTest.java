
package com.cloudhopper.commons.sql;

// third party imports
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.*;
import org.apache.log4j.Logger;

// my imports
import com.cloudhopper.commons.xbean.*;

public class DataSourceConfigurationTest {

    private static final Logger logger = Logger.getLogger(DataSourceConfigurationTest.class);

    @Test
    public void configureNoProperties() throws Exception {
        String xml = new StringBuilder(200)
            .append("<configuration>")
            .append(" <datasource>")
            .append(" </datasource>")
            .append("</configuration>")
            .toString();

        DataSourceConfiguration config = new DataSourceConfiguration();

        XmlBean xbean = new XmlBean();
        xbean.configure(xml, config, "/configuration/datasource");

        // make sure certain properties are null
        Assert.assertNull(config.getUsername());
        Assert.assertNull(config.getPassword());
        Assert.assertNull(config.getUrl());
        Assert.assertNull(config.getName());
    }

    @Test
    public void configureUrlAutomaticallySelectsProperties() throws Exception {
        String xml = new StringBuilder(200)
            .append("<configuration>")
            .append(" <datasource>")
            .append("  <url>jdbc:mysql://localhost:3306/stratus001</url>")
            .append(" </datasource>")
            .append("</configuration>")
            .toString();

        DataSourceConfiguration config = new DataSourceConfiguration();

        XmlBean xbean = new XmlBean();
        xbean.configure(xml, config, "/configuration/datasource");

        // make sure certain properties are null
        Assert.assertEquals("jdbc:mysql://localhost:3306/stratus001", config.getUrl());
        Assert.assertEquals("com.mysql.jdbc.Driver", config.getDriver());
        Assert.assertEquals(DatabaseVendor.MYSQL, config.getVendor());
        // should have picked default validation query
        Assert.assertEquals("SELECT NOW()", config.getValidationQuery());
        // c3p0 should be default
        Assert.assertEquals(DataSourceProvider.C3P0, config.getProvider());
    }

}
