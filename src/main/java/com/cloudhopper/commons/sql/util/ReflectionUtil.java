
package com.cloudhopper.commons.sql.util;

// java imports
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// my imports
import com.cloudhopper.commons.sql.SQLConfigurationException;

/**
 *
 * @author joelauer
 */
public class ReflectionUtil {

    public static void call(String methodName, Object target, Object ... args) throws SQLConfigurationException {
        // create array of paramter types
        Class[] argTypes = new Class[args.length];
        int i = 0;
        for (Object param : args) {
            argTypes[i] = param.getClass();
            i++;
        }
        
        // find the method
        Method m = null;
        try {
            m = target.getClass().getMethod(methodName, argTypes);
        } catch (NoSuchMethodException e) {
            throw new SQLConfigurationException("Method '" + methodName + "' does not exist on class '" + target.getClass() + "'", e);
        }
        try {
            // invoke the method
            m.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new SQLConfigurationException("Not permitted to access method '" + methodName + "' on class '" + target.getClass() + "'");
        } catch (IllegalArgumentException e) {
            throw new SQLConfigurationException("Bad argument(s) used with method '" + methodName + "' on class '" + target.getClass() + "'");
        } catch (InvocationTargetException e) {
            throw new SQLConfigurationException("An exception was thrown while calling method '" + methodName + "' on class '" + target.getClass() + "'");
        }
    }

    public static void callStatic(String methodName, Class type, Object ... args) throws SQLConfigurationException {
        // create array of paramter types
        Class[] argTypes = new Class[args.length];
        int i = 0;
        for (Object param : args) {
            argTypes[i] = param.getClass();
            i++;
        }

        // find the method
        Method m = null;
        try {
            m = type.getMethod(methodName, argTypes);
        } catch (NoSuchMethodException e) {
            throw new SQLConfigurationException("Method '" + methodName + "' does not exist on class '" + type + "'");
        }
        try {
            // invoke the method
            m.invoke(null, args);
        } catch (IllegalAccessException e) {
            throw new SQLConfigurationException("Not permitted to access method '" + methodName + "' on class '" + type + "'");
        } catch (IllegalArgumentException e) {
            throw new SQLConfigurationException("Bad argument(s) used with method '" + methodName + "' on class '" + type + "'");
        } catch (InvocationTargetException e) {
            throw new SQLConfigurationException("An exception was thrown while calling method '" + methodName + "' on class '" + type + "'");
        }
    }

}
