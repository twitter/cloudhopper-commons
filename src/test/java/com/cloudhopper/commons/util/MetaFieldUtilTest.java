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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

// third party imports
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// my imports
import com.cloudhopper.commons.util.annotation.MetaField;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class MetaFieldUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(MetaFieldUtilTest.class);

    @Test
    public void toMetaFieldInfoArray() throws Exception {
        Administrator emp = new Administrator();
        MetaFieldInfo[] fields = MetaFieldUtil.toMetaFieldInfoArray(emp);
        Assert.assertEquals(12, fields.length);
        Assert.assertEquals("First Name", fields[0].name);
        Assert.assertEquals("Last Name", fields[1].name);
        Assert.assertEquals("email", fields[2].name);
        Assert.assertEquals("ID", fields[3].name);
        Assert.assertEquals("Active?", fields[4].name);
        Assert.assertEquals("loginCounter", fields[5].name);
        Assert.assertEquals("The first name of this user", fields[0].description);
        Assert.assertEquals("", fields[1].description);
        Assert.assertEquals("", fields[2].description);
        // set some values
        emp.setFirstName("John");
        emp.setLastName("Doe");
        fields = MetaFieldUtil.toMetaFieldInfoArray(emp, "EMPTY");
        //for (MetaFieldInfo field : fields) {
        //    logger.debug("field name=" + field.name + ", value=" + field.value + ", description=" + field.description);
        //}
        Assert.assertEquals("John", fields[0].value);
        Assert.assertEquals("Doe", fields[1].value);
        Assert.assertEquals("EMPTY", fields[2].value);
    }


    @Test
    public void toMetaFieldInfoString() throws Exception {
        Employee emp = new Employee();
        String string0 = MetaFieldUtil.toMetaFieldInfoString(emp);
        logger.debug(string0);

        emp = new Administrator();
        String string1 = MetaFieldUtil.toMetaFieldInfoString(emp);
        logger.debug(string1);

        /**
        Assert.assertEquals("username=null,firstName=null,lastName=null,email=null,id=-1,isActive=false,loginCounter=0", string0);
        emp.setFirstName("John");
        emp.setLastName("Doe");
        emp.setActive(true);
        emp.setLoginCounter(52);
        string0 = MetaFieldUtil.toMetaFieldInfoString(emp);
        Assert.assertEquals("username=null,firstName=\"John\",lastName=\"Doe\",email=null,id=-1,isActive=true,loginCounter=52", string0);
         */
    }


    @Test
    public void toMetaFieldInfoArrayUnwrappingAtomicReference() throws Exception {
        TestRef ref = new TestRef();
        MetaFieldInfo[] fields = MetaFieldUtil.toMetaFieldInfoArray(ref);
        // won't be able unwrap anything if the reference type is null
        Assert.assertEquals(1, fields.length);
        ref.setPerson(new Person());
        fields = MetaFieldUtil.toMetaFieldInfoArray(ref);
        // should be unwrapped now
        Assert.assertEquals(6, fields.length);

        //for (MetaFieldInfo field : fields) {
        //    logger.debug("field name=" + field.name + ", value=" + field.value + ", description=" + field.description);
        //}
    }

    /**
    @Test
    public void toMetaFieldInfoRecursive() throws Exception {
        Administrator emp = new Administrator();
        MetaFieldInfo[] fields = MetaFieldUtil.internalToMetaFieldInfoArray(Administrator.class, null, null, null, null, true, true);
        for (MetaFieldInfo field : fields) {
            logger.debug("field name=" + field.name + ", value=" + field.value + ", description=" + field.description);
        }
    }
     */
    
}

@Ignore
class TestRef {
    @MetaField
    private AtomicReference<Person> person;

    public void setPerson(Person p) { person = new AtomicReference<Person>(p); }
}

class Person {
    @MetaField(name="First Name", description="The first name of this user")
    private String firstName;
    @MetaField(name="Last Name", description="")
    private String lastName;
    @MetaField
    private String email;
    @MetaField(name="ID")
    private int id = -1;
    @MetaField(name="Active?", description="Is this person actually active")
    private AtomicBoolean isActive = new AtomicBoolean(false);
    @MetaField(description="Number of times logged in")
    private AtomicInteger loginCounter = new AtomicInteger(0);

    public void setFirstName(String value) { firstName = value; }
    public void setLastName(String value) { lastName = value; }
    public void setEmail(String value) { email = value; }
    public void setId(int value) { id = value; }
    public void setActive(boolean value) { isActive.set(value); }
    public void setLoginCounter(int value) { loginCounter.set(value); }
}

class Employee extends Person {
    @MetaField(name="Username", description="Unique username")
    private String username;

    public void setUsername(String value) { username = value; }
}

class Administrator extends Employee {
    @MetaField(name="Mailing Address", description="Address of administrator")
    private Address address;
    public void setAddress(Address value) { address = value; }
}

class Address {
    @MetaField(name="Line 1", description="First line of address")
    private String line1;
    @MetaField(name="Line 2", description="Second line of address")
    private String line2;
    @MetaField(name="Line 3")
    private CityStateZipCode line3;

    public void setLine1(String line) { this.line1 = line; }
    public void setLine2(String line) { this.line2 = line; }
    public void setLine3(CityStateZipCode line) { this.line3 = line; }
}

class CityStateZipCode {
    @MetaField(name="City")
    private String city;
    @MetaField(name="State")
    private String state;
    @MetaField(name="Zip Code")
    private int zipCode;

    public void setCity(String value) { this.city = value; }
    public void setState(String value) { this.state = value; }
    public void setZipCode(int value) { this.zipCode = value; }
}
