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
import org.junit.*;
import org.apache.log4j.Logger;

// my imports

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
