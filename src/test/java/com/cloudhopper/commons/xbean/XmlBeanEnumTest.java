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
import org.junit.*;
import org.apache.log4j.Logger;

// my imports
import com.cloudhopper.commons.xml.*;

public class XmlBeanEnumTest {

    private static final Logger logger = Logger.getLogger(XmlBeanEnumTest.class);

    @Test
    public void configureWithEnum() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("  <destinationAddress>")
                .append("    <toa>NATIONAL</toa>\n")
                .append("    <address>+13135551212</address>\n")
                .append("  </destinationAddress>")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        Configuration config = new Configuration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);

        // confirm properties
        Assert.assertEquals(TypeOfAddress.NATIONAL, config.destinationAddress.toa);
        Assert.assertEquals("+13135551212", config.destinationAddress.address);
    }

    @Test(expected=PropertyConversionException.class)
    public void configureWithBadEnum() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("  <destinationAddress>")
                .append("    <toa>National</toa>\n")
                .append("    <address>+13135551212</address>\n")
                .append("  </destinationAddress>")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(is);

        // object we'll configure
        Configuration config = new Configuration();

        // configure it using default options
        XmlBean bean = new XmlBean();
        bean.configure(rootNode, config);
    }
    
    public static class Configuration {
        private Address destinationAddress;

        public void setDestinationAddress(Address value) {
            this.destinationAddress = value;
        }
    }

    public static class Address {
        private TypeOfAddress toa;
        private String address;

        public void setToa(TypeOfAddress value) {
            this.toa = value;
        }

        public void setAddress(String value) throws ConfigurationException {
            if (value.length() < 2 || value.length() > 20) {
                throw new ConfigurationException("Address invalid length (min=2, max=20)");
            }
            this.address = value;
        }
    }

    public enum TypeOfAddress {
        NATIONAL,
        INTERNATIONAL
    }

    public static class ConfigurationException extends Exception {
        public ConfigurationException(String msg) {
            super(msg);
        }
    }

}
