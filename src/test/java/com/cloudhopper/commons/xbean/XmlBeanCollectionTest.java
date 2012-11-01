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
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

// my imports

public class XmlBeanCollectionTest {
    private static final Logger logger = Logger.getLogger(XmlBeanCollectionTest.class);
    
    public static class Sample {
        private String name;
        private String comment;
    }
    
    public static class SampleA {
        private ArrayList<String> hosts;
    }
    
    public static class SampleAWithAnnotation {
        @XmlBeanProperty(value="host")
        private ArrayList<String> hosts;
    }
    
    public static class SampleB {
        private HashSet<String> hosts;
        private TreeSet<Integer> ids;
    }
    
    public static class SampleC {
        private ArrayList<Sample> samples;
    }
    
    public static class SampleD {
        private ArrayList<ArrayList<String>> hosts;
    }
    
    public static class SampleE {
        private List<String> hosts;
    }
    
    public static class SampleF {
        private ArrayList<String> hostList;
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
    public void addSimpleTypesToArrayList() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host\">\n")
                .append("    <host>www.google.com</host>\n")
                .append("    <host>www.twitter.com</host>\n")
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
        Assert.assertEquals(2, a.hosts.size());
        Assert.assertEquals("www.google.com", a.hosts.get(0));
        Assert.assertEquals("www.twitter.com", a.hosts.get(1));
    }
    
    @Test(expected=PropertyNotFoundException.class)
    public void addSimpleTypesToArrayListWithInvalidPropertyName() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts>\n")
                .append("    <host2>www.google.com</host2>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        SampleA a = new SampleA();
        
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, a);
    }
    
    @Test
    public void addSimpleTypesToMultipleSets() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host\">\n")
                .append("    <host>www.google.com</host>\n")
                .append("    <host>www.twitter.com</host>\n")
                .append("    <host>www.twitter.com</host>\n")   // duplicate in set should be ignored
                .append("  </hosts>\n")
                .append("  <ids value=\"id\">\n")
                .append("    <id>1</id>\n")
                .append("    <id>20</id>\n")
                .append("    <id>3</id>\n")
                .append("  </ids>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        SampleB b = new SampleB();
        
        Assert.assertNull(b.hosts);
        Assert.assertNull(b.ids);
        
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, b);
        
        Assert.assertNotNull(b.hosts);
        Assert.assertEquals(2, b.hosts.size());
        Assert.assertEquals(true, b.hosts.contains("www.google.com"));
        Assert.assertEquals(true, b.hosts.contains("www.twitter.com"));
        Assert.assertEquals(false, b.hosts.contains("www.google2.com"));
        
        Assert.assertNotNull(b.ids);
        Assert.assertEquals(3, b.ids.size());
        Assert.assertEquals(true, b.ids.contains(1));
        Assert.assertEquals(true, b.ids.contains(20));
        Assert.assertEquals(true, b.ids.contains(3));
        Assert.assertEquals(false, b.ids.contains(4));
    }
    
    @Test
    public void addComplexTypeToArrayList() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <samples value=\"sample\">\n")
                .append("    <sample>\n")
                .append("      <name>name1</name>\n")
                .append("      <comment>comment1</comment>\n")
                .append("    </sample>\n")
                .append("    <sample>\n")
                .append("      <name>name2</name>\n")
                .append("      <comment>comment2</comment>\n")
                .append("    </sample>\n")
                .append("  </samples>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        SampleC c = new SampleC();
        
        Assert.assertNull(c.samples);
        
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, c);
        
        Assert.assertNotNull(c.samples);
        Assert.assertEquals(2, c.samples.size());
        Sample s = c.samples.get(0);
        Assert.assertEquals("name1", s.name);
        Assert.assertEquals("comment1", s.comment);
        s = c.samples.get(1);
        Assert.assertEquals("name2", s.name);
        Assert.assertEquals("comment2", s.comment);
    }
    
    @Test(expected=PropertyPermissionException.class)
    public void addArrayListToArrayListThrowsException() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts value=\"host\">\n")
                .append("    <host>\n")
                .append("      <value>www.google.com</value>\n")
                .append("      <value>www.google2.com</value>\n")
                .append("    </host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        SampleD d = new SampleD();
        
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, d);
    }
    
    @Test
    public void addSimpleTypesToListBySpecifiedType() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts type=\"java.util.ArrayList\" value=\"host2\">\n")
                .append("    <host2>www.google.com</host2>\n")
                .append("    <host2>www.twitter.com</host2>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        // parse xml
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        SampleE e = new SampleE();
        
        Assert.assertNull(e.hosts);
        
        XmlBean xbean = new XmlBean();
        xbean.setAccessPrivateProperties(true);
        xbean.configure(is, e);
        
        Assert.assertNotNull(e.hosts);
        Assert.assertEquals(2, e.hosts.size());
        Assert.assertEquals("www.google.com", e.hosts.get(0));
        Assert.assertEquals("www.twitter.com", e.hosts.get(1));
    }
    
    @Test
    public void addSimpleTypesToArrayListNotNamedEndingInS() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hostList>\n")
                .append("    <value>www.google.com</value>\n")
                .append("    <value>www.twitter.com</value>\n")
                .append("  </hostList>\n")
                .append("</configuration>")
                .append("");
        
        SampleF a = XmlBeanFactory.create(string0.toString(), SampleF.class);
        
        Assert.assertNotNull(a.hostList);
        Assert.assertEquals(2, a.hostList.size());
        Assert.assertEquals("www.google.com", a.hostList.get(0));
        Assert.assertEquals("www.twitter.com", a.hostList.get(1));
    }
    
    @Test
    public void addValuesViaAnnotationToArrayList() throws Exception {
        // build xml
        StringBuilder string0 = new StringBuilder(200)
                .append("<configuration>\n")
                .append("  <hosts>\n")
                .append("    <host>www.google.com</host>\n")
                .append("    <host>www.twitter.com</host>\n")
                .append("  </hosts>\n")
                .append("</configuration>")
                .append("");

        SampleAWithAnnotation a = XmlBeanFactory.create(string0.toString(), SampleAWithAnnotation.class);
        
        Assert.assertNotNull(a.hosts);
        Assert.assertEquals(2, a.hosts.size());
        Assert.assertEquals("www.google.com", a.hosts.get(0));
        Assert.assertEquals("www.twitter.com", a.hosts.get(1));
    }
}
