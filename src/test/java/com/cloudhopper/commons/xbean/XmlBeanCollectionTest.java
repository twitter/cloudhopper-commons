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
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

// my imports

public class XmlBeanCollectionTest {
    private static final Logger logger = Logger.getLogger(XmlBeanCollectionTest.class);
    
    public static class SampleA {
        private ArrayList<String> hosts;
    }

    @Test
    public void createEmptyArrayList() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts />\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        SampleA a = new SampleA();
        
        Assert.assertNull(a.hosts);
        
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, a);
        
        Assert.assertNotNull(a.hosts);
    }
    
    @Test
    public void addElementToArrayList() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts>\n")
                .append("    <host>www.google.com</host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        SampleA a = new SampleA();
        
        Assert.assertNull(a.hosts);
        
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, a);
        
        Assert.assertNotNull(a.hosts);
    }
}
