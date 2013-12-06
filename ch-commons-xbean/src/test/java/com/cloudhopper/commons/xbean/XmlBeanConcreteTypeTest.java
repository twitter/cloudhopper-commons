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
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

// my imports

public class XmlBeanConcreteTypeTest {
    private static final Logger logger = Logger.getLogger(XmlBeanConcreteTypeTest.class);

    public static interface InterfaceA {
        public void methodA();
    }
    
    public static class TypeB implements InterfaceA {
        private String host;
        private int port;
        
        @Override
        public void methodA() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
    
    public static class TypeC implements InterfaceA {
        private String host;
        private String comment;
        
        @Override
        public void methodA() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
    
    public static class TestConfigA {
        private ArrayList<InterfaceA> filters = new ArrayList<InterfaceA>();
        
        public void addFilter(InterfaceA f) {
            filters.add(f);
        }
    }
    
    public static class TestConfigB {
        private ArrayList<InterfaceA> filters = new ArrayList<InterfaceA>();
    }

    @Test
    public void createConcreteTypesBasedOnTypeAttribute() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("   <filter type=\"com.cloudhopper.commons.xbean.XmlBeanConcreteTypeTest$TypeB\">\n")        // typeB first
                .append("     <host>www.twitter.com</host>\n")
                .append("     <port>80</port>\n")
                .append("   </filter>\n")
                .append("   <filter type=\"com.cloudhopper.commons.xbean.XmlBeanConcreteTypeTest$TypeC\">\n")        // typeC second
                .append("     <host>www.google.com</host>\n")
                .append("     <comment>this is a comment</comment>\n")
                .append("   </filter>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        TestConfigA config0 = new TestConfigA();
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, config0);
        
        Assert.assertEquals(2, config0.filters.size());
        TypeB b = (TypeB)config0.filters.get(0);
        Assert.assertEquals("www.twitter.com", b.host);
        Assert.assertEquals(80, b.port);
        TypeC c = (TypeC)config0.filters.get(1);
        Assert.assertEquals("www.google.com", c.host);
        Assert.assertEquals("this is a comment", c.comment);
    }
    
    @Test(expected=PropertyInvalidTypeException.class)
    public void typeAttributeCompatabilityChecked() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <host>www.twitter.com</host>\n")
                .append("  <port type=\"java.lang.String\">80</port>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        TypeB b = new TypeB();
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, b);
        
        Assert.assertEquals("www.twitter.com", b.host);
        Assert.assertEquals(80, b.port);
    }
    
    // FIXME: this syntax would be really nice but need native support for
    // collections such as a Map or List
    @Test @Ignore
    public void createConcreteTypesBasedOnTypeAttributeForList() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("   <filters>\n")
                .append("     <add type=\"com.cloudhopper.commons.xbean.XmlBeanConcreteTypeTest$TypeB\">\n")        // typeB first
                .append("       <host>www.twitter.com</host>\n")
                .append("       <port>80</port>\n")
                .append("     </filter>\n")
                .append("     <add type=\"com.cloudhopper.commons.xbean.XmlBeanConcreteTypeTest$TypeC\">\n")        // typeC second
                .append("       <host>www.google.com</host>\n")
                .append("       <comment>this is a comment</comment>\n")
                .append("     </filter>\n")
                .append("   </filters>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        TestConfigA config0 = new TestConfigA();
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, config0);
        
        Assert.assertEquals(2, config0.filters.size());
        TypeB b = (TypeB)config0.filters.get(0);
        Assert.assertEquals("www.twitter.com", b.host);
        Assert.assertEquals(80, b.port);
        TypeC c = (TypeC)config0.filters.get(1);
        Assert.assertEquals("www.google.com", c.host);
        Assert.assertEquals("this is a comment", c.comment);
    }

}
