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
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

// my imports

public class XmlBeanMapTest {
    private static final Logger logger = Logger.getLogger(XmlBeanMapTest.class);
    
    public static class Host {
        private String name;
        private String ip;
        private Integer id;
    }
    
    public static class Host2 {
        private String name;
        private ArrayList<String> ips;
    }
    
    public static class SampleA {
        private HashMap<String,String> hosts;
    }
    
    public static class SampleB {
        private HashMap<String,Host> hosts;
    }
    
    public static class SampleBWithAnnotation {
        @XmlBeanProperty(value="host", key="name")
        private HashMap<String,Host> hosts;
    }
    
    public static class SampleC {
        private HashMap<Integer,String> hosts;
    }
    
    public static class SampleD {
        private HashMap<String,Host2> hosts;
    }
    
    public static class SampleDWithAnnotation {
        @XmlBeanProperty(value="host", key="name")
        private HashMap<String,Host2> hosts;
    }

    @Test
    public void createEmptyHashMap() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts />\n")
                .append("</configuration>")
                .append("");

        SampleA a = XmlBeanFactory.create(string0.toString(), SampleA.class);
        
        Assert.assertNotNull(a.hosts);
    }
    
    @Test
    public void putSimpleTypesToHashMap() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host\">\n")
                .append("    <host key=\"www.google.com\">10.10.1.1</host>\n")
                .append("    <host key=\"www.twitter.com\">10.10.1.2</host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleA a = XmlBeanFactory.create(string0.toString(), SampleA.class);
        
        Assert.assertNotNull(a.hosts);
        Assert.assertEquals(2, a.hosts.size());
        Assert.assertEquals("10.10.1.1", a.hosts.get("www.google.com"));
        Assert.assertEquals("10.10.1.2", a.hosts.get("www.twitter.com"));
        Assert.assertNull(a.hosts.get("www.twitter2.com"));
    }
    
    @Test(expected=PropertyIsEmptyException.class)
    public void putSimpleTypesToHashMapWithoutKeyThrowsException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host\">\n")
                .append("    <host key=\"www.google.com\">10.10.1.1</host>\n")
                .append("    <host key=\"www.twitter.com\">10.10.1.2</host>\n")
                .append("    <host>10.10.1.3</host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleA a = XmlBeanFactory.create(string0.toString(), SampleA.class);
    }
    
    @Test
    public void putSimpleAndComplexTypesToHashMap() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host\" key=\"name\">\n")
                .append("    <host>\n")
                .append("      <name>www.google.com</name>\n")
                .append("      <ip>10.10.1.1</ip>\n")
                .append("    </host>\n")
                .append("    <host>\n")
                .append("      <name>www.twitter.com</name>\n")
                .append("      <ip>10.10.1.2</ip>\n")
                .append("    </host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleB b = XmlBeanFactory.create(string0.toString(), SampleB.class);
        
        Assert.assertNotNull(b.hosts);
        Assert.assertEquals(2, b.hosts.size());
        Assert.assertEquals("10.10.1.1", b.hosts.get("www.google.com").ip);
        Assert.assertEquals("10.10.1.2", b.hosts.get("www.twitter.com").ip);
        Assert.assertNull(b.hosts.get("www.twitter2.com"));
    }
    
    @Test(expected=XmlBeanClassException.class)
    public void putSimpleAndComplexTypesToHashMapThrowsExceptionIfKeyPropertyDoesNotExist() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host\" key=\"name2\">\n")
                .append("    <host>\n")
                .append("      <name>www.google.com</name>\n")
                .append("      <ip>10.10.1.1</ip>\n")
                .append("    </host>\n")
                .append("    <host>\n")
                .append("      <name>www.twitter.com</name>\n")
                .append("      <ip>10.10.1.2</ip>\n")
                .append("    </host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleB b = XmlBeanFactory.create(string0.toString(), SampleB.class);
    }
    
    @Test
    public void putSimpleAndComplexTypesToHashMapViaAnnotations() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts>\n")
                .append("    <host>\n")
                .append("      <name>www.google.com</name>\n")
                .append("      <ip>10.10.1.1</ip>\n")
                .append("    </host>\n")
                .append("    <host>\n")
                .append("      <name>www.twitter.com</name>\n")
                .append("      <ip>10.10.1.2</ip>\n")
                .append("    </host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleBWithAnnotation b = XmlBeanFactory.create(string0.toString(), SampleBWithAnnotation.class);
        
        Assert.assertNotNull(b.hosts);
        Assert.assertEquals(2, b.hosts.size());
        Assert.assertEquals("10.10.1.1", b.hosts.get("www.google.com").ip);
        Assert.assertEquals("10.10.1.2", b.hosts.get("www.twitter.com").ip);
        Assert.assertNull(b.hosts.get("www.twitter2.com"));
    }
    
    @Test
    public void putSimpleAndComplexTypesToHashMapOverrideAnnotations() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host2\" key=\"ip\">\n")
                .append("    <host2>\n")
                .append("      <name>www.google.com</name>\n")
                .append("      <ip>10.10.1.1</ip>\n")
                .append("    </host2>\n")
                .append("    <host2>\n")
                .append("      <name>www.twitter.com</name>\n")
                .append("      <ip>10.10.1.2</ip>\n")
                .append("    </host2>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleBWithAnnotation b = XmlBeanFactory.create(string0.toString(), SampleBWithAnnotation.class);
        
        Assert.assertNotNull(b.hosts);
        Assert.assertEquals(2, b.hosts.size());
        Assert.assertEquals("www.google.com", b.hosts.get("10.10.1.1").name);
        Assert.assertEquals("www.twitter.com", b.hosts.get("10.10.1.2").name);
        Assert.assertNull(b.hosts.get("10.10.1.3"));
    }
    
    @Test
    public void putSimpleTypesToHashMapWithDifferentTypes() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host\">\n")
                .append("    <host key=\"1\">www.google.com</host>\n")
                .append("    <host key=\"2\">www.twitter.com</host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleC c = XmlBeanFactory.create(string0.toString(), SampleC.class);
        
        Assert.assertNotNull(c.hosts);
        Assert.assertEquals(2, c.hosts.size());
        Assert.assertEquals("www.google.com", c.hosts.get(1));
        Assert.assertEquals("www.twitter.com", c.hosts.get(2));
        Assert.assertNull(c.hosts.get(3));
    }
    
    @Test
    public void putValueWithSubCollection() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host\" key=\"name\">\n")
                .append("    <host>\n")
                .append("      <name>www.google.com</name>\n")
                .append("      <ips value=\"ip\">\n")
                .append("        <ip>10.10.1.1</ip>\n")
                .append("        <ip>10.10.1.2</ip>\n")
                .append("      </ips>\n")
                .append("    </host>\n")
                .append("    <host>\n")
                .append("      <name>www.twitter.com</name>\n")
                .append("    </host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleD d = XmlBeanFactory.create(string0.toString(), SampleD.class);
        
        Assert.assertNotNull(d.hosts);
        Assert.assertEquals(2, d.hosts.size());
        Host2 h0 = d.hosts.get("www.google.com");
        Assert.assertNotNull(h0);
        Assert.assertEquals(2, h0.ips.size());
        Assert.assertEquals("10.10.1.1", h0.ips.get(0));
        Assert.assertEquals("10.10.1.2", h0.ips.get(1));
        Host2 h1 = d.hosts.get("www.twitter.com");
        Assert.assertNotNull(h1);
        Assert.assertNull(h1.ips);
    }
    
    @Test
    public void putValueWithSubCollectionViaAnnotation() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts>\n")
                .append("    <host>\n")
                .append("      <name>www.google.com</name>\n")
                .append("      <ips value=\"ip\">\n")
                .append("        <ip>10.10.1.1</ip>\n")
                .append("        <ip>10.10.1.2</ip>\n")
                .append("      </ips>\n")
                .append("    </host>\n")
                .append("    <host>\n")
                .append("      <name>www.twitter.com</name>\n")
                .append("    </host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleDWithAnnotation d = XmlBeanFactory.create(string0.toString(), SampleDWithAnnotation.class);
        
        Assert.assertNotNull(d.hosts);
        Assert.assertEquals(2, d.hosts.size());
        Host2 h0 = d.hosts.get("www.google.com");
        Assert.assertNotNull(h0);
        Assert.assertEquals(2, h0.ips.size());
        Assert.assertEquals("10.10.1.1", h0.ips.get(0));
        Assert.assertEquals("10.10.1.2", h0.ips.get(1));
        Host2 h1 = d.hosts.get("www.twitter.com");
        Assert.assertNotNull(h1);
        Assert.assertNull(h1.ips);
    }
    
    @Test(expected=XmlBeanClassException.class)
    public void putWithKeyClassNotMatchingHashMapKeyClassThrowsException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host\" key=\"id\">\n")
                .append("    <host>\n")
                .append("      <name>www.google.com</name>\n")
                .append("      <ip>10.10.1.1</ip>\n")
                .append("      <id>1</id>\n")
                .append("    </host>\n")
                .append("    <host>\n")
                .append("      <name>www.twitter.com</name>\n")
                .append("      <ip>10.10.1.2</ip>\n")
                .append("      <id>2</id>\n")
                .append("    </host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleB b = XmlBeanFactory.create(string0.toString(), SampleB.class);
        // this should have failed!
        Assert.fail();
    }
    
    @Test(expected=PropertyIsEmptyException.class)
    public void putNullKeyHashMapThrowsException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host\" key=\"name\">\n")
                .append("    <host>\n")
                .append("      <name>www.google.com</name>\n")
                .append("      <ip>10.10.1.1</ip>\n")
                .append("    </host>\n")
                .append("    <host>\n")
                //.append("      <name>www.twitter.com</name>\n")   // missing name - null should throw exception
                .append("      <ip>10.10.1.2</ip>\n")
                .append("    </host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleB b = XmlBeanFactory.create(string0.toString(), SampleB.class);
        // this should have failed!
        Assert.fail();
    }
}
