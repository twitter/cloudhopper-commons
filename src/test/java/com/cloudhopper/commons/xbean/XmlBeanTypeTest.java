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

public class XmlBeanTypeTest {
    private static final Logger logger = Logger.getLogger(XmlBeanTypeTest.class);

    public static class TypeTestConfig {
        private byte byte0;
        private Byte byte1;
    }

    @Test(expected=PropertyConversionException.class)
    public void unsignedPrimitiveByteTypeThrowsException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("   <byte0>255</byte0>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        // object we'll configure
        TypeTestConfig config0 = new TypeTestConfig();

        // configure it using default options
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, config0);
    }

    @Test(expected=PropertyConversionException.class)
    public void unsignedByteTypeThrowsException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("   <byte1>255</byte1>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        // object we'll configure
        TypeTestConfig config0 = new TypeTestConfig();

        // configure it using default options
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, config0);
    }

    @Ignore @Test
    public void byteTypeAsHex() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("   <byte0>0x34</byte0>\n")
                .append("   <byte1>0xFF</byte1>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        // object we'll configure
        TypeTestConfig config0 = new TypeTestConfig();

        // configure it using default options
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, config0);

        Assert.assertEquals((byte)0x34, config0.byte0);
        Assert.assertEquals(new Byte((byte)0xFF), config0.byte1);
    }

}
