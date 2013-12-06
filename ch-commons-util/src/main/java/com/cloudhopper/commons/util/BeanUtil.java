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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements utilities for working with classes.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class BeanUtil {
    private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    /**
     * Finds a BeanProperty for the class type. A BeanProperty may have a getter
     * method such as "getFirstName()", a setter such as "setFirstName", an adder
     * such as "addFirstName", or may just be the underlying field with wrapped
     * getter and setter methods available via the BeanProperty class. The BeanProperty
     * class hides getter and setter access by allowing direct access to set the underlying
     * field in case a public setter or getter don't exist.  This method initially
     * searches for a field matching the propertyName.  If a field isn't found,
     * then this method falls back to searching for a getXXXXX or setXXXXX method matching
     * the propertyName.
     * @param type The class to search for the method
     * @param propertyName The name of the property to search for
     * @param caseSensitive True if its a case sensitive search, otherwise false
     * @return The new BeanProperty
     * @throws java.lang.IllegalAccessException If there is an access exception
     *      while attempting to turn "private" fields or methods to accessible.
     */
    public static BeanProperty findBeanProperty(Class type, String propertyName, boolean caseSensitive) throws IllegalAccessException {
        // bean property we'd return if found
        BeanProperty beanProperty = null;

        // class we'll start our search from
        Class classType = type;

        //
        // search for field first - loop thru all classes
        //
        while (classType != null && !classType.equals(Object.class)) {
            // search all declared fields
            for (Field f : classType.getDeclaredFields()) {
                // grab this field's name
                String fieldName = f.getName();
                // check if its the property we're searching for
                if ((!caseSensitive && fieldName.equalsIgnoreCase(propertyName)) || (caseSensitive && fieldName.equals(propertyName))) {
                    // success, we found the method we're looking for
                    Class fieldType = f.getType();
                    // make sure its accessible
                    f.setAccessible(true);
                    // create the BeanProperty we're going to use
                    beanProperty = new BeanProperty(propertyName, fieldType, f);
                    // done with this loop, break out of it
                    break;
                }
            }
            // move onto the superclass
            classType = classType.getSuperclass();
        }

        String capitalizedName = StringUtil.capitalize(propertyName);
        String getMethodName = "get" + capitalizedName;
        String setMethodName = "set" + capitalizedName;
        String addMethodName = "add" + capitalizedName;

        
        // if we get here and the beanProperty does not exist, then we need
        // to search thru the hierarchy of classes for a setter or getter
        // method that matches the name
        if (beanProperty == null) {
            // class we'll start our search from
            classType = type;
            while (classType != null && !classType.equals(Object.class)) {
                // search all declared fields
                for (Method m : classType.getDeclaredMethods()) {
                    // grab this methods's name
                    String methodName = m.getName();
                    // is this a getter we've been looking for?
                    if ((!caseSensitive && methodName.equalsIgnoreCase(getMethodName)) || (caseSensitive && methodName.equals(getMethodName))) {
                        // check to make sure this accepts no arguments
                        if (m.getParameterTypes().length == 0) {
                            // success, we found the getter method
                            beanProperty = new BeanProperty(propertyName, m.getReturnType(), null);
                            m.setAccessible(true);
                            beanProperty.getMethod = m;
                            break;
                        }
                    // is this a setter we've been looking for?
                    } else if ((!caseSensitive && methodName.equalsIgnoreCase(setMethodName)) || (caseSensitive && methodName.equals(setMethodName))) {
                        // check to make sure this accepts 1 argument
                        if (m.getParameterTypes().length == 1) {
                            // success, we found the setter method
                            beanProperty = new BeanProperty(propertyName, m.getParameterTypes()[0], null);
                            m.setAccessible(true);
                            beanProperty.setMethod = m;
                            break;
                        }
                    // is this a adder we've been looking for?
                    } else if ((!caseSensitive && methodName.equalsIgnoreCase(addMethodName)) || (caseSensitive && methodName.equals(addMethodName))) {
                        // check to make sure this accepts 1 argument
                        if (m.getParameterTypes().length == 1) {
                            // success, we found the adder method
                            beanProperty = new BeanProperty(propertyName, m.getParameterTypes()[0], null);
                            m.setAccessible(true);
                            beanProperty.addMethod = m;
                            break;
                        }
                    }
                }
                // move onto the superclass
                classType = classType.getSuperclass();
            }
        }

        // if we get here and beanProperty exists, then we need to search for
        // whether its getter/setter methods also exist
        if (beanProperty != null) {
            if (beanProperty.getMethod == null) {
                try {
                    // look for a "getter" method
                    Method getMethod = ClassUtil.getMethod(type, getMethodName, beanProperty.getType(), null, caseSensitive);
                    // set to accessible?
                    getMethod.setAccessible(true);
                    beanProperty.getMethod = getMethod;
                } catch (NoSuchMethodException e) {
                    // its okay if this happens, ignore
                }
            }

            if (beanProperty.setMethod == null) {
                try {
                    // look for a "setter" method
                    Method setMethod = ClassUtil.getMethod(type, setMethodName, null, beanProperty.getType(), caseSensitive);
                    // set to accessible?
                    setMethod.setAccessible(true);
                    beanProperty.setMethod = setMethod;
                } catch (NoSuchMethodException e) {
                    // its okay if this happens, ignore
                }
            }

            if (beanProperty.addMethod == null) {
                try {
                    // look for a "adder" method
                    Method addMethod = ClassUtil.getMethod(type, addMethodName, null, beanProperty.getType(), caseSensitive);
                    // set to accessible?
                    addMethod.setAccessible(true);
                    beanProperty.addMethod = addMethod;
                } catch (NoSuchMethodException e) {
                    // its okay if this happens, ignore
                }
            }
        }

        return beanProperty;
    }
}
