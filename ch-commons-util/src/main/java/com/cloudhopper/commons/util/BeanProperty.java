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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Represents a "bean" property for a class.  A property in this case follows
 * a coding convention of how the field is named and how it matches getter
 * and setter methods with the same Class type.  For example, a field may
 * be declared such as "firstName" and have access methods such as
 * "getFirstName()" and "setFirstName()".  This class abstracts that association
 * and provides more useful methods of "get" and "set" which may fall back
 * to direct access of the underlying field if no getter or setter methods
 * exist.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class BeanProperty {

    private String name;
    private Class type;
    private Field field;
    Method getMethod;
    Method setMethod;
    Method addMethod;

    public BeanProperty(String name, Class type, Field field) {
        this.name = name;
        this.type = type;
        this.field = field;
    }

    /**
     * Gets the name of the property such as "firstName".  Usually is the
     * name of the underlying field of a property.
     * @return The name of the property such as "firstName"
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the Class type of the property such as "java.lang.String".
     * @return The Class type of the property
     */
    public Class getType() {
        return type;
    }

    /**
     * Returns whether the "add" method would succeed or throw an exception.
     * @return True if an "add" would succeed, otherwise false.
     */
    public boolean canAdd() {
        return (addMethod != null);
    }

    /**
     * Returns whether the "addOrSet" method would succeed or throw an exception.
     * @return True if an "addOrSet" would succeed, otherwise false.
     */
    public boolean canAddOrSet() {
        return (canAdd() || canSet());
    }

    /**
     * Returns whether the "get" method would succeed or throw an exception.
     * @return True if an "get" would succeed, otherwise false.
     */
    public boolean canGet() {
        return (getMethod != null || field != null);
    }

    /**
     * Returns whether the "set" method would succeed or throw an exception.
     * @return True if an "set" would succeed, otherwise false.
     */
    public boolean canSet() {
        return (setMethod != null || field != null);
    }

    /**
     * Gets the underlying declared field within the class for this property.
     * Its possible the underlying field may be null, since all access may
     * specifically be required to go thru getter and setter methods.
     * @return The declared field for this property
     */
    public Field getField() {
        return field;
    }

    /**
     * Gets the underlying declared getter method for this property.
     * Its possible the underlying method may be null, since it may not exist
     * for this property.  In that case, direct access to the field may be
     * required to obtain its value.
     * @return The declared getter method for this property such as getFirstName()
     */
    public Method getGetMethod() {
        return getMethod;
    }

    /**
     * Gets the underlying declared setter method for this property.
     * Its possible the underlying method may be null, since it may not exist
     * for this property.  In that case, direct access to the field may be
     * required to obtain its value.
     * @return The declared setter method for this property such as setFirstName()
     */
    public Method getSetMethod() {
        return setMethod;
    }

    /**
     * Gets the underlying declared adder method for this property.
     * Its possible the underlying method may be null, since it may not exist
     * for this property.
     * @return The declared adder method for this property such as addFirstName()
     */
    public Method getAddMethod() {
        return addMethod;
    }

    /**
     * Sets the property value on the object.  Attempts to first set the
     * property via its setter method such as "setFirstName()".  If a setter
     * method doesn't exist, this will then attempt to set the property value
     * directly on the underlying field within the class.
     * <br>
     * NOTE: If the setter method throws an exception during execution, the
     * exception will be accessible in the getCause() method of the
     * InvocationTargetException.
     * @param obj The object to set the property on
     * @param value The value of the property
     * @throws java.lang.IllegalAccessException Thrown if an access exception
     *      occurs while attempting to set the property value.
     * @throws java.lang.reflect.InvocationTargetException Thrown if there
     *      is an exception thrown while calling the underlying method.
     */
    public void set(Object obj, Object value) throws IllegalAccessException, InvocationTargetException {
        // always try the "setMethod" first
        if (setMethod != null) {
            setMethod.invoke(obj, value);
        // fall back to setting the field directly
        } else if (field != null) {
            field.set(obj, value);
        } else {
            throw new IllegalAccessException("Cannot set property value");
        }
    }

    /**
     * Gets the property value on the object.  Attempts to first get the
     * property via its getter method such as "getFirstName()".  If a getter
     * method doesn't exist, this will then attempt to get the property value
     * directly from the underlying field within the class.
     * <br>
     * NOTE: If the getter method throws an exception during execution, the
     * exception will be accessible in the getCause() method of the
     * InvocationTargetException.
     * @param obj The object to get the property from
     * @return The value of the property
     * @throws java.lang.IllegalAccessException Thrown if an access exception
     *      occurs while attempting to get the property value.
     * @throws java.lang.IllegalArgumentException Thrown if an illegal argument
     *      is used while attempting to get the property value.
     * @throws java.lang.reflect.InvocationTargetException Thrown if there
     *      is an exception thrown while calling the underlying method.
     */
    public Object get(Object obj) throws IllegalAccessException, InvocationTargetException {
        // always try the "getMethod" first
        if (getMethod != null) {
            return getMethod.invoke(obj);
        // fall back to getting the field directly
        } else if (field != null) {
            return field.get(obj);
        } else {
            throw new IllegalAccessException("Cannot get property value");
        }
    }

    /**
     * Adds the property value on the object.  Attempts to call "addFirstName()".
     * If the adder method doesn't exist, this will throw an exception.
     * <br>
     * NOTE: If the adder method throws an exception during execution, the
     * exception will be accessible in the getCause() method of the
     * InvocationTargetException.
     * @param obj The object to add the property on
     * @param value The value to add
     * @throws java.lang.IllegalAccessException Thrown if an access exception
     *      occurs while attempting to set the property value.
     * @throws java.lang.IllegalArgumentException Thrown if an illegal argument
     *      is used while attempting to set the property value.
     * @throws java.lang.reflect.InvocationTargetException Thrown if there
     *      is an exception thrown while calling the underlying method.
     */
    public void add(Object obj, Object value) throws IllegalAccessException, InvocationTargetException {
        if (addMethod != null) {
            addMethod.invoke(obj, value);
        } else {
            throw new IllegalAccessException("Cannot add property value");
        }
    }

    /**
     * Adds or Sets the property value on the object.  Attempts to first add
     * the property and falls back to setting the property.
     * <br>
     * NOTE: If the adder or setter method throws an exception during execution, the
     * exception will be accessible in the getCause() method of the InvocationTargetException.
     * @param obj The object to add or set the property on
     * @param value The value of the property
     * @throws java.lang.IllegalAccessException Thrown if an access exception
     *      occurs while attempting to set the property value.
     * @throws java.lang.reflect.InvocationTargetException Thrown if there
     *      is an exception thrown while calling the underlying method.
     */
    public void addOrSet(Object obj, Object value) throws IllegalAccessException, InvocationTargetException {
        // always try the "addMethod" first
        if (addMethod != null) {
            addMethod.invoke(obj, value);
        // try the "setMethod" second
        } else if (setMethod != null) {
            setMethod.invoke(obj, value);
        // fall back to setting the field directly
        } else if (field != null) {
            field.set(obj, value);
        } else {
            throw new IllegalAccessException("Cannot add or set the property");
        }
    }
}
