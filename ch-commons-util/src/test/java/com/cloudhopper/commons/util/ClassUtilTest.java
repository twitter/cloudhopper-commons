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
import java.lang.reflect.Method;

// third party imports
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// my imports
import com.cloudhopper.commons.util.sample.Person;
import com.cloudhopper.commons.util.sample.InternetPerson;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class ClassUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(ClassUtilTest.class);


    @Test
    public void getMethodCaseSensitiveGet() throws Exception {
        Method m = ClassUtil.getMethod(InternetPerson.class, "getFirstName", String.class, null, true);
        Method expectedMethod = Person.class.getMethod("getFirstName");
        Assert.assertEquals(expectedMethod, m);
    }

    @Test
    public void getMethodCaseSensitiveSet() throws Exception {
        Method m = ClassUtil.getMethod(InternetPerson.class, "setFirstName", null, String.class, true);
        Method expectedMethod = Person.class.getMethod("setFirstName", String.class);
        Assert.assertEquals(expectedMethod, m);
    }
    
    @Test(expected=NoSuchMethodException.class)
    public void getMethodCaseSensitiveInvalidMethod() throws Exception {
        Method m = ClassUtil.getMethod(InternetPerson.class, "getfirstname", String.class, null, true);
    }

    @Test(expected=IllegalAccessException.class)
    public void getMethodCaseSensitiveProtected() throws Exception {
        Method m = ClassUtil.getMethod(InternetPerson.class, "getLastName2", String.class, null, true);
    }

    @Test(expected=NoSuchMethodException.class)
    public void getMethodCaseSensitiveBadReturnType() throws Exception {
        Method m = ClassUtil.getMethod(InternetPerson.class, "getLastName", Object.class, null, true);
    }

    @Test(expected=NoSuchMethodException.class)
    public void getMethodCaseSensitiveBadParameterType() throws Exception {
        Method m = ClassUtil.getMethod(InternetPerson.class, "getLastName", String.class, String.class, true);
    }

    @Test
    public void getMethodCaseSensitiveNoReturnOrParam() throws Exception {
        try {
            Method m = ClassUtil.getMethod(InternetPerson.class, "setLastName", null, null, true);
        } catch (NoSuchMethodException e) {
            if (e.getMessage().indexOf("signature") < 0) {
                Assert.fail("Expected exception was correct, but 'signature match failed' was not found in its message!");
            }
        }
    }

    @Test
    public void getMethodCaseSensitiveNoReturnBadParam() throws Exception {
        try {
            Method m = ClassUtil.getMethod(InternetPerson.class, "setLastName", null, Object.class, true);
        } catch (NoSuchMethodException e) {
            if (e.getMessage().indexOf("signature") < 0) {
                Assert.fail("Expected exception was correct, but 'signature match failed' was not found in its message!");
            }
        }
    }

    @Test
    public void getMethodCaseInsensitiveGet() throws Exception {
        Method m = ClassUtil.getMethod(InternetPerson.class, "getfirstName", String.class, null, false);
        Method expectedMethod = Person.class.getMethod("getFirstName");
        Assert.assertEquals(expectedMethod, m);
    }

    @Test
    public void getMethodCaseInsensitiveSet() throws Exception {
        Method m = ClassUtil.getMethod(InternetPerson.class, "setfirstname", null, String.class, false);
        Method expectedMethod = Person.class.getMethod("setFirstName", String.class);
        Assert.assertEquals(expectedMethod, m);
    }

    @Test
    public void getBeanMethods() throws Exception {
        Method expectedGetMethod = Person.class.getMethod("getFirstName");
        Method expectedSetMethod = Person.class.getMethod("setFirstName", String.class);

        // case insensitive
        Method[] methods = ClassUtil.getBeanMethods(InternetPerson.class, "firstname", String.class, false);
        Assert.assertEquals(expectedGetMethod, methods[0]);
        Assert.assertEquals(expectedSetMethod, methods[1]);

        // case sensitive
        methods = ClassUtil.getBeanMethods(InternetPerson.class, "FirstName", String.class, true);
        Assert.assertEquals(expectedGetMethod, methods[0]);
        Assert.assertEquals(expectedSetMethod, methods[1]);
    }

    @Test(expected=NoSuchMethodException.class)
    public void getBeanMethodsOnlyGet() throws Exception {
        Method[] methods = ClassUtil.getBeanMethods(InternetPerson.class, "email2", String.class, false);
    }

    @Test
    public void hasBeanMethods() {
        Assert.assertEquals(false, ClassUtil.hasBeanMethods(InternetPerson.class, "email2", String.class, false));
        Assert.assertEquals(false, ClassUtil.hasBeanMethods(InternetPerson.class, "Email2", String.class, true));
        Assert.assertEquals(false, ClassUtil.hasBeanMethods(InternetPerson.class, "blash", String.class, true));
        Assert.assertEquals(false, ClassUtil.hasBeanMethods(InternetPerson.class, "blash", String.class, false));
        Assert.assertEquals(true, ClassUtil.hasBeanMethods(InternetPerson.class, "LastName", String.class, true));
        Assert.assertEquals(true, ClassUtil.hasBeanMethods(InternetPerson.class, "FirstName", String.class, true));
        Assert.assertEquals(true, ClassUtil.hasBeanMethods(InternetPerson.class, "Email", String.class, true));
    }

    @Test
    public void getClassHierarchy() throws Exception {
        Class[] hierarchy = ClassUtil.getClassHierarchy(InternetPerson.class);
        Assert.assertEquals(2, hierarchy.length);
        Assert.assertEquals(Person.class, hierarchy[0]);
        Assert.assertEquals(InternetPerson.class, hierarchy[1]);
    }

    /**
     * Used for any enum tests.
     */
    public enum TestEnum {
        ONE,
        Two
    }

    @Test
    public void findEnumConstant() throws Exception {
        // non-enum classes should just return null
        Assert.assertEquals(null, ClassUtil.findEnumConstant(this.getClass(), "One", true));
        Assert.assertEquals(TestEnum.ONE, ClassUtil.findEnumConstant(TestEnum.class, "ONE", true));
        Assert.assertEquals(TestEnum.ONE, ClassUtil.findEnumConstant(TestEnum.class, "ONE", false));
        // case sensitive search should fail
        Assert.assertEquals(null, ClassUtil.findEnumConstant(TestEnum.class, "One", true));
        Assert.assertEquals(TestEnum.ONE, ClassUtil.findEnumConstant(TestEnum.class, "one", false));
        Assert.assertEquals(TestEnum.Two, ClassUtil.findEnumConstant(TestEnum.class, "Two", true));
        Assert.assertEquals(TestEnum.Two, ClassUtil.findEnumConstant(TestEnum.class, "two", false));
        Assert.assertEquals(null, ClassUtil.findEnumConstant(TestEnum.class, "two", true));
    }
    
}
