package com.cloudhopper.commons.util;

/*
 * #%L
 * ch-commons-util
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

// java imports
import java.lang.reflect.InvocationTargetException;

// third party imports
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// my imports
import com.cloudhopper.commons.util.sample.Person;
import com.cloudhopper.commons.util.sample.InternetPerson;
import com.cloudhopper.commons.util.sample.ServerConfig;
import com.cloudhopper.commons.util.sample.ServerConfigException;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class BeanUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtilTest.class);

    @Test
    public void getBeanProperty() throws Exception {
        InternetPerson person = new InternetPerson();
        
        // assert default properties
        Assert.assertNull(person.getFirstName());
        Assert.assertNull(person.getLastName());
        Assert.assertEquals(-1, person.getId());
        // use utility class to get the "bean" property
        BeanProperty beanProperty = BeanUtil.findBeanProperty(InternetPerson.class, "firstName", true);
        Assert.assertEquals(Person.class.getDeclaredField("firstName"), beanProperty.getField());
        //Assert.assertEquals(Person.class.getDeclaredMethod("getFirstName"), beanProperty.getGetMethod());
        //Assert.assertEquals(Person.class.getDeclaredMethod("setFirstName", String.class), beanProperty.getSetMethod());
        Assert.assertEquals(String.class, beanProperty.getType());
        Assert.assertEquals("firstName", beanProperty.getName());
        // use getter/setter (should use underlying getter and setter methods)
        beanProperty.set(person, "Joe");
        Assert.assertEquals("Joe", beanProperty.get(person));
        Assert.assertEquals("Joe", person.getFirstName());

        // now let's test the usage of underlying field for all access
        BeanProperty beanProperty2 = BeanUtil.findBeanProperty(InternetPerson.class, "extraInfo", true);
        Assert.assertEquals(null, beanProperty2.get(person));
        beanProperty2.set(person, "MoreInfoHere");
        Assert.assertEquals("MoreInfoHere", beanProperty2.get(person));

        // test properties that throw exceptions
        ServerConfig config = new ServerConfig();
        BeanProperty beanProperty3 = BeanUtil.findBeanProperty(config.getClass(), "port", true);
        
        // this an int property, let's try a string
        try {
            beanProperty3.set(config, "80");
            Assert.fail("String arg should have thrown exception");
        } catch (IllegalArgumentException e) {
            // this is expected behavior
        }

        try {
            beanProperty3.set(config, -80);
            Assert.fail("negative port arg should have thrown exception");
        } catch (InvocationTargetException e) {
            Assert.assertEquals(ServerConfigException.class, e.getCause().getClass());
        }

        // the "set" method for port throws an exception if <= 0
        beanProperty3.set(config, 80);
        Assert.assertEquals(80, beanProperty3.get(config));
    }

    private static class TestBeanA {
        public String getFirstName() {
            return "Joe";
        }
    }

    @Test
    public void getBeanPropertyOnlyGetter() throws Exception {
        TestBeanA bean = new TestBeanA();
        
        // use utility class to get the "bean" property
        BeanProperty property = BeanUtil.findBeanProperty(bean.getClass(), "firstName", true);

        Assert.assertEquals(String.class, property.getType());
        Assert.assertEquals("firstName", property.getName());
        Assert.assertEquals(true, property.canGet());
        Assert.assertEquals(false, property.canSet());
        Assert.assertEquals(false, property.canAdd());
        Assert.assertEquals(null, property.getField());
        Assert.assertEquals(TestBeanA.class.getDeclaredMethod("getFirstName"), property.getGetMethod());
        Assert.assertEquals(null, property.getSetMethod());
    }

    private static class TestBeanB {
        public void setFirstName(String value) {
            // do nothing
        }
    }

    @Test
    public void getBeanPropertyOnlySetter() throws Exception {
        TestBeanB bean = new TestBeanB();

        // use utility class to get the "bean" property
        BeanProperty property = BeanUtil.findBeanProperty(bean.getClass(), "firstName", true);

        Assert.assertEquals(String.class, property.getType());
        Assert.assertEquals("firstName", property.getName());
        Assert.assertEquals(false, property.canGet());
        Assert.assertEquals(true, property.canSet());
        Assert.assertEquals(false, property.canAdd());
        Assert.assertEquals(null, property.getField());
        Assert.assertEquals(TestBeanB.class.getDeclaredMethod("setFirstName", String.class), property.getSetMethod());
        Assert.assertEquals(null, property.getGetMethod());
    }

    private static class TestBeanC {
        // this method shouldn't be found
        public String getFirstName(String value0) {
            return "Joe";
        }
        public String getFirstName() {
            return "Joe";
        }
        // this method shouldn't be found
        public void setFirstName(String value0, String value1) {
            // do nothing
        }
        public void setFirstName(String value) {
            // do nothing
        }
    }

    @Test
    public void getBeanPropertyOnlyGetterAndSetter() throws Exception {
        TestBeanC bean = new TestBeanC();

        // use utility class to get the "bean" property
        BeanProperty property = BeanUtil.findBeanProperty(bean.getClass(), "firstName", true);

        Assert.assertEquals(String.class, property.getType());
        Assert.assertEquals("firstName", property.getName());
        Assert.assertEquals(true, property.canGet());
        Assert.assertEquals(true, property.canSet());
        Assert.assertEquals(false, property.canAdd());
        Assert.assertEquals(null, property.getField());
        Assert.assertEquals(TestBeanC.class.getDeclaredMethod("setFirstName", String.class), property.getSetMethod());
        Assert.assertEquals(TestBeanC.class.getDeclaredMethod("getFirstName"), property.getGetMethod());
    }

    private static class TestBeanD {
        // this method shouldn't be found
        public void addHost(String value0, String value1) {
            // do nothing
        }
        public void addHost(String string0) {
            // do nothing
        }
    }

    @Test
    public void getBeanPropertyOnlyAdder() throws Exception {
        TestBeanD bean = new TestBeanD();

        // use utility class to get the "bean" property
        BeanProperty property = BeanUtil.findBeanProperty(bean.getClass(), "host", true);

        Assert.assertEquals(String.class, property.getType());
        Assert.assertEquals("host", property.getName());
        Assert.assertEquals(false, property.canGet());
        Assert.assertEquals(false, property.canSet());
        Assert.assertEquals(true, property.canAdd());
        Assert.assertEquals(null, property.getField());
        Assert.assertEquals(TestBeanD.class.getDeclaredMethod("addHost", String.class), property.getAddMethod());
        Assert.assertEquals(null, property.getSetMethod());
        Assert.assertEquals(null, property.getGetMethod());
    }

    private static class TestBeanE {
        private String sethost;
        private String addhost;
        // this method shouldn't be found
        public void addHost(String value0, String value1) {
            // do nothing
        }
        public void addHost(String string0) {
            this.addhost = string0;
        }
        public void setHost(String string0) {
            this.sethost = string0;
        }
    }

    @Test
    public void getBeanPropertyAddOrSet() throws Exception {
        TestBeanE bean = new TestBeanE();

        // use utility class to get the "bean" property
        BeanProperty property = BeanUtil.findBeanProperty(bean.getClass(), "host", true);

        Assert.assertEquals(String.class, property.getType());
        Assert.assertEquals("host", property.getName());
        Assert.assertEquals(false, property.canGet());
        Assert.assertEquals(true, property.canSet());
        Assert.assertEquals(true, property.canAdd());
        Assert.assertEquals(true, property.canAddOrSet());
        Assert.assertEquals(null, property.getField());
        Assert.assertEquals(TestBeanE.class.getDeclaredMethod("addHost", String.class), property.getAddMethod());
        Assert.assertEquals(TestBeanE.class.getDeclaredMethod("setHost", String.class), property.getSetMethod());
        Assert.assertEquals(null, property.getGetMethod());

        // verify that the "addHost" method takes precedence over the "setHost"
        property.addOrSet(bean, "www.google.com");

        Assert.assertEquals("www.google.com", bean.addhost);
        Assert.assertEquals(null, bean.sethost);
    }

    
}
