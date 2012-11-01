package com.cloudhopper.commons.xbean;

/*
 * #%L
 * ch-commons-xbean
 * %%
 * Copyright (C) 2012 Cloudhopper by Twitter
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

// third party imports
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.*;
import org.apache.log4j.Logger;

// my imports

/**
 * Tests that the XmlBean uses the runtime class of the object instead of the
 * declared type in the object.  Allows inherited properties to be automatically
 * available for configuration even if the type is a "super class" as defined
 * in the object.
 * 
 * @author joelauer
 */
public class XmlBeanRuntimeClassTest {

    private static final Logger logger = Logger.getLogger(XmlBeanRuntimeClassTest.class);

    @Test
    public void configureWithRuntimeClass() throws Exception {
        // build xml
        StringBuilder xml = new StringBuilder(200)
                // NOTE: leave out xml version on purpose since it should have
                // no effect on processing the configuration file
                //.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<root>\n")
                // the type actually uses a factory to create a default instance
                // of the configuration object so that those properties are supported!
                .append("   <type>complex</type>\n")
                .append("   <configuration>\n")
                .append("     <port>80</port>\n")
                .append("     <host>www.google.com</host>\n")
                .append("     <url>http://www.google.com/</url>\n")
                .append("   </configuration>\n")
                .append("</root>")
                .append("");

        // object we'll configure
        CombinedConfiguration config = new CombinedConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(xml.toString(), config);

        // confirm properties
        Assert.assertEquals(ComplexConfiguration.class, config.config.getClass());
        ComplexConfiguration complex = (ComplexConfiguration)config.config;
        Assert.assertEquals(80, complex.port);
        Assert.assertEquals("www.google.com", config.config.host);
        Assert.assertEquals("http://www.google.com/", config.config.url);
    }

    /**
    @Test
    public void configureWithTypeLastThrowsException() throws Exception {
        // build xml
        StringBuilder xml = new StringBuilder(200)
                // NOTE: leave out xml version on purpose since it should have
                // no effect on processing the configuration file
                //.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<root>\n")
                .append("   <configuration>\n")
                .append("     <port>80</port>\n")
                .append("     <host>www.google.com</host>\n")
                .append("     <url>http://www.google.com/</url>\n")
                .append("   </configuration>\n")
                // the type has to be defined before the configuration would be
                .append("   <type>complex</type>\n")
                .append("</root>")
                .append("");

        // object we'll configure
        CombinedConfiguration config = new CombinedConfiguration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(xml.toString(), config);
    }
     */

    public static class SimpleConfiguration {
        
        private String host;
        private String url;

        public SimpleConfiguration() {
            // do nothing
        }
        
        public void setHost(String value) throws MalformedURLException {
            this.host = value;
        }

        public void setUrl(String value) throws MalformedURLException {
            URL aURL = new URL(value);
            this.url = value;
        }
    }


    public static class ComplexConfiguration extends SimpleConfiguration {

        private int port;

        public ComplexConfiguration() {
            super();
        }

        public void setPort(int value) throws ConfigurationException {
            if (value <= 0) {
                throw new ConfigurationException("Port must be > 0");
            }
            this.port = value;
        }
    }

    public static class CombinedConfiguration {

        private String type;
        private SimpleConfiguration config;

        public CombinedConfiguration() {
            // do nothing
        }

        public void setType(String type) throws ConfigurationException {
            // based on the "type", create a default instance of the configuration
            // that will be used...
            if (type.equals("complex")) {
                this.config = new ComplexConfiguration();
            } else if (type.equals("simple")) {
                this.config = new SimpleConfiguration();
            } else {
                throw new ConfigurationException("Type '" + type + "' not supported");
            }
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public void setConfiguration(SimpleConfiguration config) throws ConfigurationException {
            // if "type" isn't set, then this can't be c
            if (this.type == null) {
                throw new ConfigurationException("Must set <type> before <configuration>");
            }
            this.config = config;
        }

        public SimpleConfiguration getConfiguration() {
            return this.config;
        }
    }
    

    public static class ConfigurationException extends Exception {
        public ConfigurationException(String msg) {
            super(msg);
        }
    }

}
