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

package com.cloudhopper.commons.xbean;

// third party imports
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.*;
import org.apache.log4j.Logger;

// my imports
import com.cloudhopper.commons.xml.*;

public class XmlBeanTest {

    private static final Logger logger = Logger.getLogger(XmlBeanTest.class);

    @Test
    public void configureSimpleProperties() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                // NOTE: leave out xml version on purpose since it should have
                // no effect on processing the configuration file
                //.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <port>80</port>\n")
                .append("   <host>www.google.com</host>\n")
                .append("   <url>http://www.google.com/</url>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        SimpleConfiguration config = new SimpleConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);

        // confirm properties
        Assert.assertNull(config.size);
        Assert.assertEquals(80, config.port);
        Assert.assertEquals("www.google.com", config.host);
        Assert.assertEquals("http://www.google.com/", config.url);
    }

    @Test(expected=RootTagMismatchException.class)
    public void configureThrowRootTagMismatchException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        SimpleConfiguration config = new SimpleConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        // this should cause a check of what the root tag actually is
        bean.setRootTag("configuration2");
        bean.configure(rootNode, config);
    }

    @Test(expected=PropertyNotFoundException.class)
    public void configureThrowPropertyNotFoundException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <port2>80</port2>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        SimpleConfiguration config = new SimpleConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);
    }

    @Test(expected=PropertyPermissionException.class)
    public void configureThrowPropertyPermissionException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <size>80</size>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        SimpleConfiguration config = new SimpleConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);
    }

    @Test(expected=PropertyIsEmptyException.class)
    public void configureThrowPropertyIsEmptyException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <port></port>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        SimpleConfiguration config = new SimpleConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);
    }

    @Test(expected=PropertyConversionException.class)
    public void configureThrowPropertyConversionException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <port>joe</port>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        SimpleConfiguration config = new SimpleConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);
    }

    @Test(expected=PropertyInvocationException.class)
    public void configureThrowPropertyInvocationException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <port>-1</port>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        SimpleConfiguration config = new SimpleConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);
    }

    @Test
    public void configureWithAccessPrivateProperties() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <size>80</size>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        SimpleConfiguration config = new SimpleConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        // setting this to true allows access to internal private properties
        // that normally aren't allowed to be set
        bean.setAccessPrivateProperties(true);
        bean.configure(rootNode, config);

        // confirm properties
        Assert.assertEquals(new Integer(80), config.size);
        Assert.assertEquals(0, config.port);
        Assert.assertNull(config.host);
        Assert.assertNull(config.url);
    }


    @Test
    public void configureComplex() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <server>\n")
                .append("     <port>80</port>\n")
                .append("     <host>www.google.com</host>\n")
                .append("   </server>\n")
                .append("   <url>http://www.google.com/</url>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        ComplexConfiguration config = new ComplexConfiguration();

        Assert.assertNull(config.getServer());
        Assert.assertNull(config.url);

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);

        // confirm properties
        Assert.assertNotNull(config.getServer());
        Assert.assertEquals("http://www.google.com/", config.url);
        Assert.assertEquals(80, config.getServer().port);
        Assert.assertEquals("www.google.com", config.getServer().host);
    }
    

    @Test
    public void configureComplexWithProvidedInstance() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <server>\n")
                .append("     <host>www.google.com</host>\n")
                .append("   </server>\n")
                .append("   <url>http://www.google.com/</url>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        ComplexConfiguration config = new ComplexConfiguration();
        // create our own server instance though, configure() method should try
        // try to "get" it first to configure it
        Server server = new Server();
        server.setPort(80);
        config.setServer(server);
        config.setUrl("http://www.abc.com/");

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);

        // confirm properties
        Assert.assertSame(server, config.getServer());
        Assert.assertEquals("http://www.google.com/", config.url);
        Assert.assertEquals(80, config.getServer().port);
        Assert.assertEquals("www.google.com", config.getServer().host);
    }

    @Test
    public void configureComplexNoChildProperties() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <server />\n")
                .append("   <url>http://www.google.com/</url>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        ComplexConfiguration config = new ComplexConfiguration();

        Assert.assertNull(config.getServer());
        Assert.assertNull(config.url);

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);

        // confirm properties
        Assert.assertNotNull(config.getServer());
        Assert.assertEquals("http://www.google.com/", config.url);
        Assert.assertEquals(0, config.getServer().port);
        Assert.assertEquals(null, config.getServer().host);
    }

    @Test(expected=PropertyNotFoundException.class)
    public void configureComplexWithPropertyNotFoundException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <server>\n")
                .append("     <port2>80</port2>\n")
                .append("   </server>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        ComplexConfiguration config = new ComplexConfiguration();
       
        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);
    }

    /**
     * Xbean attempts to create a new instance of a class that doesn't have
     * any empty constructor.  Causes an instantiation exception to be thrown.
     */
    @Test(expected=XmlBeanClassException.class)
    public void configureComplexWithBadConstructor() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <complexServer />\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        ComplexConfiguration config = new ComplexConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.setAccessPrivateProperties(true);
        bean.configure(rootNode, config);
    }

    /**
     * Tests whether failure to successfully configure a child complex object
     * ensures that it isn't set on a property in the root object. Basically,
     * ensures that configuring child objects are sort of "atomic".
     */
    @Test
    public void configureComplexWithPropertyNotFoundExceptionEnsureOnlyOnePropertyChanged() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <url>http://www.google.com/</url>\n")
                .append("   <server>\n")
                .append("     <host>www2.google.com</host>\n")      // this should be changed since server reference already existed
                .append("     <port2>80</port2>\n")                 // this should cause an error to be thrown
                .append("   </server>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        ComplexConfiguration config = new ComplexConfiguration();
        Server server = new Server();
        config.setServer(server);
        server.setHost("www.google.com");
        server.setPort(443);

        // configure it using default options
        XmlBean bean = new XmlBean();

        try {
            bean.configure(rootNode, config);
            Assert.fail("Configure should have failed");
        } catch (XmlBeanException e) {
            // ignore it
        }

        // server should remain the same
        Assert.assertSame(server, config.getServer());
        Assert.assertEquals("http://www.google.com/", config.url);
        Assert.assertEquals(443, config.getServer().port);
        // this property should have changed since "server" was already stored in main config object
        Assert.assertEquals("www2.google.com", config.getServer().host);
    }


    /**
     * Tests if failing to configure a child complex object causes a new reference
     * to never be saved on the parent object.  Different from previous test
     * since a new instance was created and configured and then NOT saved.
     */
    @Test
    public void configureComplexWithPropertyNotFoundExceptionEnsureNoPropertiesChanged() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <url>http://www.google.com/</url>\n")
                .append("   <server>\n")
                .append("     <host>www2.google.com</host>\n")      // this should be changed
                .append("     <port2>80</port2>\n")                 // this should cause an error to be thrown and caused "server" to not be saved
                .append("   </server>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        ComplexConfiguration config = new ComplexConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();

        try {
            bean.configure(rootNode, config);
            Assert.fail("Configure should have failed");
        } catch (XmlBeanException e) {
            // ignore it
        }

        // server should remain the same
        Assert.assertNull(config.getServer());
        Assert.assertEquals("http://www.google.com/", config.url);
    }

    @Test(expected=PropertyNoAttributesExpectedException.class)
    public void configureNoAttributesExpectedInRootNode() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration id=\"stratus\">\n")       // should cause exception
                .append("   <port>80</port>\n")
                .append("   <host>www.google.com</host>\n")
                .append("   <url>http://www.google.com/</url>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        SimpleConfiguration config = new SimpleConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);
    }

    @Test(expected=PropertyNoAttributesExpectedException.class)
    public void configureNoAttributesExpectedInProperty() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <port>80</port>\n")
                .append("   <host id=\"1\">www.google.com</host>\n")    // should cause exception
                .append("   <url>http://www.google.com/</url>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        SimpleConfiguration config = new SimpleConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);
    }


    @Test
    public void configureComplexTwice() throws Exception {
        // build xml once
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <server>\n")
                .append("     <port>80</port>\n")
                .append("     <host>www.google.com</host>\n")
                .append("   </server>\n")
                .append("   <url>http://www.google.com/</url>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        ComplexConfiguration config = new ComplexConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);

        // confirm properties
        Assert.assertNotNull(config.getServer());
        Assert.assertEquals("http://www.google.com/", config.url);
        Assert.assertEquals(80, config.getServer().port);
        Assert.assertEquals("www.google.com", config.getServer().host);


        // build xml override some properties
        StringBuilder string1 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <server>\n")
                .append("     <port>8080</port>\n")
                .append("   </server>\n")
                .append("   <url>http://www2.google.com/</url>\n")
                .append("</configuration>");

        // parse new xml
        is = new ByteArrayInputStream(string1.toString().getBytes());
        rootNode = parser.parse(is);

        // configure it again
        bean.configure(rootNode, config);

        // confirm properties
        Assert.assertNotNull(config.getServer());
        Assert.assertEquals("http://www2.google.com/", config.url);
        Assert.assertEquals(8080, config.getServer().port);
        Assert.assertEquals("www.google.com", config.getServer().host);
    }


    @Test(expected=PropertyAlreadySetException.class)
    public void configureSamePropertyTwiceCausesException() throws Exception {
        // build xml once
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("  <server>\n")
                .append("    <port>80</port>\n")
                .append("    <port>8080</port>\n")             // this should cause an exception
                .append("  </server>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        ComplexConfiguration config = new ComplexConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);
    }

    
    public static class ComplexConfiguration {
        private Server server;
        private ComplexServer complexServer;
        private String url;

        public void setServer(Server value) {
            this.server = value;
        }

        public Server getServer() {
            return this.server;
        }

        public void setUrl(String value) throws MalformedURLException {
            URL aURL = new URL(value);
            this.url = value;
        }
    }


    public static class ComplexServer {
        private int port;
        public ComplexServer(int port) {
            this.port = port;
        }

    }


    public static class Server {
        private int port;
        private String host;

        public void setPort(int value) throws ConfigurationException {
            if (value <= 0) {
                throw new ConfigurationException("Port must be > 0");
            }
            this.port = value;
        }

        public void setHost(String value) throws MalformedURLException {
            this.host = value;
        }
    }

    
    public static class SimpleConfiguration {

        private Integer size;
        private int port;
        private String host;
        private String url;

        public void setPort(int value) throws ConfigurationException {
            if (value <= 0) {
                throw new ConfigurationException("Port must be > 0");
            }
            this.port = value;
        }

        public void setHost(String value) throws MalformedURLException {
            //URL aURL = new URL(value);
            /**
            System.out.println("protocol = " + aURL.getProtocol());
            System.out.println("authority = " + aURL.getAuthority());
            System.out.println("host = " + aURL.getHost());
            System.out.println("port = " + aURL.getPort());
            System.out.println("path = " + aURL.getPath());
            System.out.println("query = " + aURL.getQuery());
            System.out.println("filename = " + aURL.getFile());
            System.out.println("ref = " + aURL.getRef());
             */
            this.host = value;
        }

        public void setUrl(String value) throws MalformedURLException {
            URL aURL = new URL(value);
            /**
            System.out.println("protocol = " + aURL.getProtocol());
            System.out.println("authority = " + aURL.getAuthority());
            System.out.println("host = " + aURL.getHost());
            System.out.println("port = " + aURL.getPort());
            System.out.println("path = " + aURL.getPath());
            System.out.println("query = " + aURL.getQuery());
            System.out.println("filename = " + aURL.getFile());
            System.out.println("ref = " + aURL.getRef());
             */
            this.url = value;
        }
    }

    public static class ConfigurationException extends Exception {
        public ConfigurationException(String msg) {
            super(msg);
        }
    }

}
