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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;

/**
 * This class implements utilities for working with classes.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 * @author john woolf (twitter: @jwoolf330 or <a href="http://twitter.com/jwoolf330" target=window>http://twitter.com/jwoolf330</a>)
 */
public class ClassUtil {

    /**
     * Finds an instance of an Enum constant on a class. Useful for safely
     * getting the value of an enum constant without an exception being thrown
     * like the Enum.valueOf() method causes. Searches for enum constant
     * where case is sensitive.
     */
    public static Object findEnumConstant(Class<?> type, String constantName) {
        return findEnumConstant(type, constantName, true);
    }


    /**
     * Finds an instance of an Enum constant on a class. Useful for safely
     * getting the value of an enum constant without an exception being thrown
     * like the Enum.valueOf() method causes. Also, this method optionally allows
     * the caller to choose whether case matters during the search.
     */
    public static Object findEnumConstant(Class<?> type, String constantName, boolean caseSensitive) {
        if (!type.isEnum()) {
            return null;
        }
        for (Object obj : type.getEnumConstants()) {
            String name = obj.toString();
            if ((caseSensitive && name.equals(constantName)) || (!caseSensitive && name.equalsIgnoreCase(constantName))) {
                return obj;
            }
        }
        // otherwise, it wasn't found
        return null;
    }


    /**
     * Returns an array of class objects representing the entire class hierarchy
     * with the most-super class as the first element followed by all subclasses
     * in the order they are declared. This method does not include the generic
     * Object type in its list. If this class represents the Object type, this
     * method will return a zero-size array.
     */
    public static Class<?>[] getClassHierarchy(Class<?> type) {
        ArrayDeque<Class<?>> classes = new ArrayDeque<Class<?>>();
        // class to start our search from, we'll loop thru the entire class hierarchy
        Class<?> classType = type;
        // keep searching up until we reach an Object class type
        while (classType != null && !classType.equals(Object.class)) {
            // keep adding onto front
            classes.addFirst(classType);
            classType = classType.getSuperclass();
        }
        return classes.toArray(new Class[0]);
    }


    /**
     * Checks if the class implements public "bean" methods (get and set) for the
     * name. For example, for a name such as "firstName", this method will
     * check if the class has both getFirstName() and setFirstName() methods.
     * Also, this method will validate the return type matches the paramType
     * on the getXXX() method and that the setXXX() method only accepts that
     * paramType.  The search is optionally case sensitive.
     * @param type The class to search
     * @param propertyName The property name to search for, if "firstName", then
     *      this method will internally add the "get" and "set" to the beginning.
     * @param propertyType The class type of the property
     * @param caseSensitive If the search is case sensitive or not
     * @return True if the "bean" methods are correct, otherwise false.
     */
    public static boolean hasBeanMethods(Class<?> type, String propertyName, Class<?> propertyType, boolean caseSensitive) {
        try {
            // if this succeeds without an exception, then the properties exist!
            @SuppressWarnings("unused")
            Method[] methods = getBeanMethods(type, propertyName, propertyType, caseSensitive);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    
    /**
     * Gets the methods within this class that implements public "bean" methods
     * (get and set) for the property name. Returns an array of methods where
     * index=0 is the get method and index=1 is the set method.
     * For example, for a name such as "firstName", this method will
     * check if the class has both getFirstName() and setFirstName() methods.
     * Also, this method will validate the return type matches the propertyType
     * on the getXXX() method and that the setXXX() method only accepts that
     * propertyType.  The search is optionally case sensitive.
     * @param type The class to search
     * @param propertyName The name to search for
     * @param paramType The class type of the property
     * @param caseSensitive If the search is case sensitive or not
     * @return An array of Methods were index=0 is the get method and index=1 is the set method
     * @throws java.lang.IllegalAccessException If the method was found, but is not public.
     * @throws java.lang.NoSuchMethodException If the method was not found
     */
    public static Method[] getBeanMethods(Class<?> type, String propertyName, Class<?> propertyType, boolean caseSensitive)
        throws IllegalAccessException, NoSuchMethodException {
        Method methods[] = new Method[2];
        // search for the "get"
        methods[0] = getMethod(type, "get"+propertyName, propertyType, null, caseSensitive);
        // search for the "set"
        methods[1] = getMethod(type, "set"+propertyName, null, propertyType, caseSensitive);
        return methods;
    }




    /**
     * Gets the public method within the type that matches the method name, return type,
     * and single parameter type. Optionally is a case sensitive search. Useful
     * for searching for "bean" methods on classes.
     * @param type The class to search for the method
     * @param name The name of the method to search for
     * @param returnType The expected return type or null if its expected to be a void method
     * @param paramType The expected parameter type or null if no parameters are expected
     * @param caseSensitive True if its a case sensitive search, otherwise false
     * @return The method matching the search criteria
     * @throws java.lang.IllegalAccessException If the method was found, but is not public.
     * @throws java.lang.NoSuchMethodException If the method was not found
     */
    public static Method getMethod(Class<?> type, String name, Class<?> returnType, Class<?> paramType, boolean caseSensitive)
        throws IllegalAccessException, NoSuchMethodException {
        
        // flag to help modify the exception to make it a little easier for debugging
        boolean methodNameFound = false;

        // start our search
        Class<?> classType = type;

        while (classType != null && !classType.equals(Object.class)) {

            for (Method m : classType.getDeclaredMethods()) {

                if ((!caseSensitive && m.getName().equalsIgnoreCase(name)) || (caseSensitive && m.getName().equals(name))) {

                    // we found the method name, but its possible the signature won't
                    // match below, we'll set this flag to help construct a better exception
                    // below
                    methodNameFound = true;

                    // should we validate the return type?
                    if (returnType != null) {
                        // if the return types don't match, then this must be invalid
                        // since the JVM doesn't allow the same return type
                        if (!m.getReturnType().equals(returnType)) {
                            throw new NoSuchMethodException("Method '" + name + "' was found in " + type.getSimpleName() + ".class"
                                    + ", but the returnType " + m.getReturnType().getSimpleName() + ".class did not match expected " + returnType.getSimpleName() + ".class");
                        }
                    // make sure the return type is VOID
                    } else {
                        if (!m.getReturnType().equals(void.class)) {
                            throw new NoSuchMethodException("Method '" + name + "' was found in " + type.getSimpleName() + ".class"
                                    + ", but the returnType " + m.getReturnType().getSimpleName() + ".class was expected to be void");
                        }
                    }

                    // return type was okay, check the parameters
                    Class<?>[] paramTypes = m.getParameterTypes();

                    // should we check the parameter type?
                    if (paramType != null) {
                        // must have exactly 1 parameter
                        if (paramTypes.length != 1) {
                            // this might not be the method we want, keep searching
                            continue;
                        } else {
                            // if the parameters don't match, keep searching
                            if (!paramTypes[0].equals(paramType)) {
                                continue;
                            }
                        }
                    // if paramType was null, then make sure no parameters are expected
                    } else {
                        if (paramTypes.length != 0) {
                            continue;
                        }
                    }

                    // if we got here, then everything matches so far
                    // now its time to check if the method is accessible
                    if (!Modifier.isPublic(m.getModifiers())) {
                        throw new IllegalAccessException("Method '" + name + "' was found in " + type.getSimpleName() + ".class "+
                                ", but its not accessible since its " + Modifier.toString(m.getModifiers()));
                    }

                    // everything was okay
                    return m;
                }
            }

            // move onto the superclass
            classType = classType.getSuperclass();
        }

        String signature = "public " + (returnType == null ? "void" : returnType.getName()) + " " + name + "(" + (paramType == null ? "" : paramType.getName()) + ")";
        
        if (methodNameFound) {
            throw new NoSuchMethodException("Method '" + signature + "' was found in " + type.getSimpleName() + ".class, but signature match failed");
        } else {
            throw new NoSuchMethodException("Method '" + signature + "' was not found in " + type.getSimpleName() + ".class");
        }
    }
}
