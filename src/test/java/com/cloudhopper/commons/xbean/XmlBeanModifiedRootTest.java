
package com.cloudhopper.commons.xbean;

// third party imports
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.*;
import org.apache.log4j.Logger;

// my imports
import com.cloudhopper.commons.xml.*;
import java.util.ArrayList;

public class XmlBeanModifiedRootTest {

    private static final Logger logger = Logger.getLogger(XmlBeanModifiedRootTest.class);

    private static class TestConfig {
        private String name;
        public void setName(String value) { this.name = value; }
    }

    /**
     * Modifies what the "root" element is in the xml document when we call
     * the configure() method.
     */
    @Test
    public void configureModifiedRoot() throws Exception {
        String xml = new StringBuilder(200)
            .append("<configuration>")
            .append(" <datasource>")
            .append("  <name>main</name>")
            .append(" </datasource>")
            .append("</configuration>")
            .toString();

        // object we'll configure
        TestConfig config = new TestConfig();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(xml, config, "/configuration/datasource");

        // confirm properties
        Assert.assertEquals("main", config.name);
    }

    @Test(expected=XPathNotFoundException.class)
    public void configureModifiedRootThrowsXPathNotFoundException() throws Exception {
        String xml = new StringBuilder(200)
            .append("<configuration>")
            .append(" <datasource>")
            .append("  <name>main</name>")
            .append(" </datasource>")
            .append("</configuration>")
            .toString();

        // object we'll configure
        TestConfig config = new TestConfig();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(xml, config, "/configuration/datasource2");
    }

}
