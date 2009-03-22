
package com.cloudhopper.commons.xbean;

// java imports
import java.util.HashMap;

// my imports
import com.cloudhopper.commons.util.ClassUtil;
import com.cloudhopper.commons.xbean.convert.*;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joelauer
 */
public class XmlBean {

    // registry of converters
    private static HashMap<Class,PropertyConverter> converterRegistry;

    // statically create registry
    static {
        converterRegistry = new HashMap<Class,PropertyConverter>();
        converterRegistry.put(String.class, new StringPropertyConverter());
        converterRegistry.put(boolean.class, new BooleanPrimitivePropertyConverter());
        converterRegistry.put(Boolean.class, new BooleanPropertyConverter());
        converterRegistry.put(byte.class, new BytePrimitivePropertyConverter());
        converterRegistry.put(Byte.class, new BytePropertyConverter());
        converterRegistry.put(short.class, new ShortPrimitivePropertyConverter());
        converterRegistry.put(Short.class, new ShortPropertyConverter());
        converterRegistry.put(int.class, new IntegerPrimitivePropertyConverter());
        converterRegistry.put(Integer.class, new IntegerPropertyConverter());
        converterRegistry.put(long.class, new LongPrimitivePropertyConverter());
        converterRegistry.put(Long.class, new LongPropertyConverter());
    }

    private String rootTag;
    private boolean accessPrivateProperties;

    public XmlBean() {
        rootTag = null;
        accessPrivateProperties = false;
    }

    /**
     * If non-null, will check that the root tag's value matches this value.
     * @param value The root node tag's value to match such as "Configuration"
     */
    public void setRootTag(String value) {
        this.rootTag = value;
    }

    /**
     * Whether private properties (without associated public getter/setter method)
     * are okay to set on a bean.  If false, then a specific permission exception
     * will be thrown.
     * @param value
     */
    public void setAccessPrivateProperties(boolean value) {
        this.accessPrivateProperties = value;
    }

    public void configure(XmlParser.Node rootNode, Object obj) throws XmlBeanException, Exception {

        // root node doesn't matter -- unless we're going to validate its name
        if (this.rootTag != null) {
            if (!this.rootTag.equals(rootNode.getTag())) {
                throw new RootTagMismatchException("Root tag mismatch [expected=" + this.rootTag + ", actual=" + rootNode.getTag() + "]");
            }
        }

        doConfigure(rootNode, obj);
    }

    private void doConfigure(XmlParser.Node rootNode, Object obj) throws XmlBeanException {

        // FIXME: do we do anything with attributes of a node?

        // loop thru all child nodes
        if (rootNode.hasChildren()) {

            for (XmlParser.Node node : rootNode.getChildren()) {
                
                // tag name represents a property we're going to set
                String propertyName = node.getTag();

                // find the property if it exists
                ClassUtil.BeanProperty property = null;
                
                try {
                    property = ClassUtil.getBeanProperty(obj.getClass(), propertyName, true);
                } catch (IllegalAccessException e) {
                    throw new PropertyPermissionException(propertyName, node.getPath(), "Illegal access while attempting to reflect property from class", e);
                } catch (NoSuchMethodException e) {
                    throw new PropertyPermissionException(propertyName, node.getPath(), "No such method while attempting to reflect property from class", e);
                }

                // if property is null, then this isn't a valid property on this object
                if (property == null) {
                    throw new PropertyNotFoundException(propertyName, node.getPath(), "Object '" + obj.getClass().getSimpleName() + "' does not contain property");
                }

                //
                // otherwise, the property exists, attempt to set it
                //

                // is there actually a "setter" method -- we shouldn't let
                // user's be able to configure fields in this case
                // unless accessing private properties is allowed
                if (!this.accessPrivateProperties && property.getSetMethod() == null) {
                    throw new PropertyPermissionException(propertyName, node.getPath(), "Object '" + obj.getClass().getSimpleName() + "' contains this property, but not permitted to set its value");
                }

                // is this a simple conversion?
                if (isSimpleType(property.getType())) {
                    
                    // get the node's text value
                    String string0 = node.getText();

                    // was any text set?  if not, throw an exception
                    if (string0 == null) {
                        throw new PropertyIsEmptyException(propertyName, node.getPath(), "Property value was empty in xml file");
                    }

                    // try to convert this to a Java object value
                    Object value = null;
                    try {
                        value = convertType(string0, property.getType());
                    } catch (ConversionException e) {
                        throw new PropertyConversionException(propertyName, node.getPath(), "Property value '" + string0 + "' failed during conversion to a " + property.getType().getSimpleName());
                    }

                    // k, time to try to set the value
                    try {
                        property.set(obj, value);
                    } catch (InvocationTargetException e) {
                        // this generally means the setXXXX method on the object
                        // threw an exception -- we want to unwrap that and just
                        // return that exception instead
                        if (e.getCause() != null) {
                            throw new PropertyInvocationException(propertyName, node.getPath(), e.getCause().getMessage(), e.getCause());
                        }
                        // otherwise, just throw this
                        throw new PropertyInvocationException(propertyName, node.getPath(), e.getMessage(), e);
                    } catch (IllegalAccessException e) {
                        throw new PropertyPermissionException(propertyName, node.getPath(), "Illegal access while setting property", e);
                    }

                // otherwise, this is a "complicated" type
                } else {

                    // try to "get" an instance of this variable if it exists
                    Object targetObj = null;

                    try {
                        targetObj = property.get(obj);
                    } catch (IllegalAccessException e) {
                        throw new PropertyPermissionException(propertyName, node.getPath(), "Illegal access while attempting to get property value from object", e);
                    } catch (InvocationTargetException e) {
                        throw new PropertyInvocationException(propertyName, node.getPath(), e.getCause().getMessage(), e.getCause());
                    }
                    
                    // if null, then we need to create a new instance of it
                    if (targetObj == null) {
                        try {
                            // create a new instance of this type
                            targetObj = property.getType().newInstance();
                        } catch (InstantiationException e) {
                            throw new XmlBeanClassException("Failed while attempting to create object of type " + property.getType().getName(), e);
                        } catch (IllegalAccessException e) {
                            throw new PropertyPermissionException(propertyName, node.getPath(), "Illegal access while attempting to create new instance of " + property.getType().getName(), e);
                        }
                    }

                    // recursively configure it
                    doConfigure(node, targetObj);

                    try {
                        // save this reference object back, but only if successfully configured
                        property.set(obj, targetObj);
                    } catch (IllegalAccessException e) {
                        throw new PropertyPermissionException(propertyName, node.getPath(), "Illegal access while attempting to set property", e);
                    } catch (InvocationTargetException e) {
                        // this generally means the setXXXX method on the object
                        // threw an exception -- we want to unwrap that and just
                        // return that exception instead
                        if (e.getCause() != null) {
                            throw new PropertyInvocationException(propertyName, node.getPath(), e.getCause().getMessage(), e.getCause());
                        }
                        // otherwise, just throw this
                        throw new PropertyInvocationException(propertyName, node.getPath(), e.getMessage(), e);
                    }
                }
            }
        }

    }

    /**
     * Returns whether or not this property type is supported by a simple
     * conversion of a String to a Java object.
     * @param type The property type
     * @return True if its a simple conversion, false otherwise.
     */
    private boolean isSimpleType(Class type) {
        return converterRegistry.containsKey(type);
    }

    /**
     * Converts the string value into an Object of the Class type.
     * @param string0 The string value to convert
     * @param type The Class type to convert it into
     * @return A new Object converted from the String value
     */
    private Object convertType(String string0, Class type) throws ConversionException {
        PropertyConverter converter = converterRegistry.get(type);
        
        if (converter == null) {
            throw new ConversionException("Unable to convert '" + string0 + "' to a " + type.getSimpleName() + " type");
        }

        return converter.convert(string0);
    }

}
