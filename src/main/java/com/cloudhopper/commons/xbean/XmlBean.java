
package com.cloudhopper.commons.xbean;

// java imports
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.io.InputStream;
import org.xml.sax.SAXException;

// my imports
import com.cloudhopper.commons.util.BeanProperty;
import com.cloudhopper.commons.util.BeanUtil;
import com.cloudhopper.commons.util.ClassUtil;
import com.cloudhopper.commons.xbean.convert.*;
import com.cloudhopper.commons.xml.XPath;
import com.cloudhopper.commons.xml.XmlParser;
import java.io.File;

/**
 * Represents a Java bean configured via XML. This class essentially is a much
 * more simple version of Spring. Java objects are loosely configured with this
 * class using reflection and method/field names matching elements in an xml
 * document.
 * <br><br>
 * An element in an xml document represents a property that will be set on a
 * Java object.  This class supports nested Java objects and their configuration
 * as well. Nested objects are first retrieved via a getter. If they are null,
 * this class will create a new instance with the default empty constructor.
 * <br><br>
 * Java objects can first be programmatically configured via Java code followed
 * by overrides in an xml document. Or, Java objects can be configured over and
 * over again by multiple xml documents.  Also, a subset of "xpath" is supported
 * to allow elements inside an xml document to become the "root" of the xml document.
 * <br><br>
 * Also, a Java object is permitted to throw exceptions when a property is set
 * and that exception will be rethrown inside a PropertyInvocationException.
 * Properties are converted to their Java equivalents based on the Class type
 * of the property of the Java object.
 * <br><br>
 * <b>Sample Java Classes:</b>
 * <pre>{@code
 * public class Server {
 *   public void setPort(int port) { this.port = value; }
 *   public void setHost(String value) { this.host = value; }
 * }
 *
 * public class Config {
 *   public void setServer(Server value) { this.server = value; }
 * }}</pre>
 *
 * <b>Sample XML:</b>
 * <pre>{@code
 * <configuration>
 *   <server>
 *     <host>www.google.com</host>
 *     <port>80</port>
 *   </server>
 * </configuration>
 * }</pre>
 *
 * <b>Sample Client Java Code:</b>
 * <pre>{@code
 * XmlBean bean = new XmlBean();
 * Config config = new Config();
 * bean.configure(xml, config);
 * }</pre>
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
     * Controls if private properties (without associated public getter/setter method)
     * are okay to set on a bean.  If false, then a specific permission exception
     * will be thrown. This option is false by default.
     * @param value True if underlying fields should be exposed, otherwise set to false.
     */
    public void setAccessPrivateProperties(boolean value) {
        this.accessPrivateProperties = value;
    }

    /**
     * Configures the object by setting the value of its properties with the
     * values from the xml document.  Each element of the xml document represents
     * a named property of the object.  For example, if "setFirstName" is a property
     * of the object, then an element of "<firstName>Joe</firstName>" would set
     * that value to "Joe".
     * @param xml A string representation of the xml document
     * @param obj The object to configure
     * @throws com.cloudhopper.commons.xbean.XmlBeanException Thrown if an exception
     *      occurs while configuring the object.
     * @throws java.io.IOException Thrown if an exception occurs while attempting
     *      to parse the input for the xml document.
     * @throws org.xml.sax.SAXException Thrown if an exception occurs while parsing
     *      the xml document.
     */
    public void configure(String xml, Object obj) throws XmlBeanException, IOException, SAXException {
        configure(xml, obj, null);
    }

    /**
     * Parses the xml document and configures the object by setting the value of its properties with the
     * values from the xml document.  Each element of the xml document represents
     * a named property of the object.  For example, if "setFirstName" is a property
     * of the object, then an element of "<firstName>Joe</firstName>" would set
     * that value to "Joe".
     * @param xml A string representation of the xml document
     * @param obj The object to configure
     * @param xpath The xpath used to select a new "root" node when configuring
     *      the object. For example, "/nodeA/nodeB" would effectively configure
     *      the object starting as though nodeB was the root node. Set to null
     *      to ignore using the xpath.
     * @throws com.cloudhopper.commons.xbean.XPathNotFoundException Thrown if the
     *      provided xpath isn't found in the xml document.
     * @throws com.cloudhopper.commons.xbean.XmlBeanException Thrown if an exception
     *      occurs while configuring the object.
     * @throws java.io.IOException Thrown if an exception occurs while attempting
     *      to parse the input for the xml document.
     * @throws org.xml.sax.SAXException Thrown if an exception occurs while parsing
     *      the xml document.
     */
    public void configure(String xml, Object obj, String xpath) throws XPathNotFoundException, XmlBeanException, IOException, SAXException {
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(xml);
        configure(rootNode, obj, xpath);
    }

    /**
     * Parses the xml document and configures the object by setting the value of its properties with the
     * values from the xml document.  Each element of the xml document represents
     * a named property of the object.  For example, if "setFirstName" is a property
     * of the object, then an element of "<firstName>Joe</firstName>" would set
     * that value to "Joe".
     * @param in An inputstream that contains the xml document
     * @param obj The object to configure
     * @throws com.cloudhopper.commons.xbean.XPathNotFoundException Thrown if the
     *      provided xpath isn't found in the xml document.
     * @throws com.cloudhopper.commons.xbean.XmlBeanException Thrown if an exception
     *      occurs while configuring the object.
     * @throws java.io.IOException Thrown if an exception occurs while attempting
     *      to parse the input for the xml document.
     * @throws org.xml.sax.SAXException Thrown if an exception occurs while parsing
     *      the xml document.
     */
    public void configure(InputStream in, Object obj) throws XmlBeanException, IOException, SAXException {
        configure(in, obj, null);
    }

    /**
     * Parses the xml document and configures the object by setting the value of its properties with the
     * values from the xml document.  Each element of the xml document represents
     * a named property of the object.  For example, if "setFirstName" is a property
     * of the object, then an element of "<firstName>Joe</firstName>" would set
     * that value to "Joe".
     * @param in An inputstream that contains the xml document
     * @param obj The object to configure
     * @param xpath The xpath used to select a new "root" node when configuring
     *      the object. For example, "/nodeA/nodeB" would effectively configure
     *      the object starting as though nodeB was the root node. Set to null
     *      to ignore using the xpath.
     * @throws com.cloudhopper.commons.xbean.XPathNotFoundException Thrown if the
     *      provided xpath isn't found in the xml document.
     * @throws com.cloudhopper.commons.xbean.XmlBeanException Thrown if an exception
     *      occurs while configuring the object.
     * @throws java.io.IOException Thrown if an exception occurs while attempting
     *      to parse the input for the xml document.
     * @throws org.xml.sax.SAXException Thrown if an exception occurs while parsing
     *      the xml document.
     */
    public void configure(InputStream in, Object obj, String xpath) throws XPathNotFoundException, XmlBeanException, IOException, SAXException {
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(in);
        configure(rootNode, obj);
    }

    /**
     * Parses the xml document and configures the object by setting the value of its properties with the
     * values from the xml document.  Each element of the xml document represents
     * a named property of the object.  For example, if "setFirstName" is a property
     * of the object, then an element of "<firstName>Joe</firstName>" would set
     * that value to "Joe".
     * @param file The file object containing the xml document
     * @param obj The object to configure
     * @throws com.cloudhopper.commons.xbean.XPathNotFoundException Thrown if the
     *      provided xpath isn't found in the xml document.
     * @throws com.cloudhopper.commons.xbean.XmlBeanException Thrown if an exception
     *      occurs while configuring the object.
     * @throws java.io.IOException Thrown if an exception occurs while attempting
     *      to parse the input for the xml document.
     * @throws org.xml.sax.SAXException Thrown if an exception occurs while parsing
     *      the xml document.
     */
    public void configure(File file, Object obj) throws XmlBeanException, IOException, SAXException {
        configure(file, obj, null);
    }

    /**
     * Parses the xml document and configures the object by setting the value of its properties with the
     * values from the xml document.  Each element of the xml document represents
     * a named property of the object.  For example, if "setFirstName" is a property
     * of the object, then an element of "<firstName>Joe</firstName>" would set
     * that value to "Joe".
     * @param file The file object containing the xml document
     * @param obj The object to configure
     * @param xpath The xpath used to select a new "root" node when configuring
     *      the object. For example, "/nodeA/nodeB" would effectively configure
     *      the object starting as though nodeB was the root node. Set to null
     *      to ignore using the xpath.
     * @throws com.cloudhopper.commons.xbean.XPathNotFoundException Thrown if the
     *      provided xpath isn't found in the xml document.
     * @throws com.cloudhopper.commons.xbean.XmlBeanException Thrown if an exception
     *      occurs while configuring the object.
     * @throws java.io.IOException Thrown if an exception occurs while attempting
     *      to parse the input for the xml document.
     * @throws org.xml.sax.SAXException Thrown if an exception occurs while parsing
     *      the xml document.
     */
    public void configure(File file, Object obj, String xpath) throws XPathNotFoundException, XmlBeanException, IOException, SAXException {
        XmlParser parser = new XmlParser();
        XmlParser.Node rootNode = parser.parse(file);
        configure(rootNode, obj);
    }

    /**
     * Configures the object by setting the value of its properties with the
     * values from the xml document.  Each element of the xml document represents
     * a named property of the object.  For example, if "setFirstName" is a property
     * of the object, then an element of "<firstName>Joe</firstName>" would set
     * that value to "Joe".
     * @param rootNode The root node of the xml document
     * @param obj The object to configure
     * @param xpath The xpath used to select a new "root" node when configuring
     *      the object. For example, "/nodeA/nodeB" would effectively configure
     *      the object starting as though nodeB was the root node. Set to null
     *      to ignore using the xpath.
     * @throws com.cloudhopper.commons.xbean.XPathNotFoundException Thrown if the
     *      provided xpath isn't found in the xml document.
     * @throws com.cloudhopper.commons.xbean.XmlBeanException Thrown if an exception
     *      occurs while configuring the object.
     */
    public void configure(XmlParser.Node rootNode, Object obj, String xpath) throws XPathNotFoundException, XmlBeanException {
        // if xpath isn't null
        if (xpath != null) {
            // attempt to select a new rootNode using the xpath
            rootNode = XPath.select(rootNode, xpath);

            // if node is null, then this xpath wasn't found
            if (rootNode == null) {
                throw new XPathNotFoundException("Xpath '" + xpath + "' not found from root node");
            }
        }
        // delegate to configure method
        configure(rootNode, obj);
    }

    /**
     * Configures the object by setting the value of its properties with the
     * values from the xml document.  Each element of the xml document represents
     * a named property of the object.  For example, if "setFirstName" is a property
     * of the object, then an element of "<firstName>Joe</firstName>" would set
     * that value to "Joe".
     * @param rootNode The root node of the xml document
     * @param obj The object to configure
     * @throws com.cloudhopper.commons.xbean.XmlBeanException Thrown if an exception
     *      occurs while configuring the object.
     */
    public void configure(XmlParser.Node rootNode, Object obj) throws XmlBeanException {
        // root node doesn't matter -- unless we're going to validate its name
        if (this.rootTag != null) {
            if (!this.rootTag.equals(rootNode.getTag())) {
                throw new RootTagMismatchException("Root tag mismatch [expected=" + this.rootTag + ", actual=" + rootNode.getTag() + "]");
            }
        }

        // were any attributes included in the root?
        if (rootNode.hasAttributes()) {
            throw new PropertyNoAttributesExpectedException("[root node]", rootNode.getPath(), obj.getClass(), "No xml attributes expected for root node");
        }

        // create a hashmap to track properties
        HashMap properties = new HashMap();

        doConfigure(rootNode, obj, properties, true);
    }

    /**
     * Internal method for handling the configuration of an object. This method
     * is recursively called for simple and complex properties.
     */
    private void doConfigure(XmlParser.Node rootNode, Object obj, HashMap properties, boolean checkForDuplicates) throws XmlBeanException {

        // FIXME: do we do anything with attributes of a node?

        // loop thru all child nodes
        if (rootNode.hasChildren()) {

            for (XmlParser.Node node : rootNode.getChildren()) {
                
                // tag name represents a property we're going to set
                String propertyName = node.getTag();

                // find the property if it exists
                BeanProperty property = null;
                
                try {
                    property = BeanUtil.findBeanProperty(obj.getClass(), propertyName, true);
                } catch (IllegalAccessException e) {
                    throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Illegal access while attempting to reflect property from class", e);
                }

                // if property is null, then this isn't a valid property on this object
                if (property == null) {
                    throw new PropertyNotFoundException(propertyName, node.getPath(), obj.getClass(), "Property '" + propertyName + "' not found");
                }

                // were any attributes included?
                if (node.hasAttributes()) {
                    throw new PropertyNoAttributesExpectedException(propertyName, node.getPath(), obj.getClass(), "No xml attributes expected for property '" + propertyName + "'");
                }

                //
                // otherwise, the property exists, attempt to set it
                //

                // is there actually a "setter" method -- we shouldn't let
                // user's be able to configure fields in this case
                // unless accessing private properties is allowed
                if (!this.accessPrivateProperties && property.getAddMethod() == null && property.getSetMethod() == null) {
                    throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Not permitted to add or set property '" + propertyName + "'");
                }

                // if we can "add" this property, then turn off checkForDuplicates
                if (property.canAdd()) {
                    checkForDuplicates = false;
                }
                
                // was this property already previously set?
                // only use this check if an "add" method doesn't exist for the bean
                if (checkForDuplicates && properties.containsKey(node.getPath())) {
                    throw new PropertyAlreadySetException(propertyName, node.getPath(), obj.getClass(), "Property '" + propertyName + "' was already previously set in the xml");
                }
                // add this property to our hashmap
                properties.put(node.getPath(), null);


                // is this a simple conversion?
                if (isSimpleType(property.getType())) {
                    
                    // get the node's text value
                    String string0 = node.getText();

                    // was any text set?  if not, throw an exception
                    if (string0 == null) {
                        throw new PropertyIsEmptyException(propertyName, node.getPath(), obj.getClass(), "Value for property '" + propertyName + "' was empty in xml");
                    }

                    // try to convert this to a Java object value
                    Object value = null;
                    try {
                        value = convertType(string0, property.getType());
                    } catch (ConversionException e) {
                        throw new PropertyConversionException(propertyName, node.getPath(), obj.getClass(), "The value '" + string0 + "' for property '" + propertyName + "' could not be converted to a(n) " + property.getType().getSimpleName() + ". " + e.getMessage());
                    }

                    // k, time to try to add or set the value
                    try {
                        property.addOrSet(obj, value);
                    } catch (InvocationTargetException e) {
                        Throwable t = e;
                        // this generally means the setXXXX method on the object
                        // threw an exception -- we want to unwrap that and just
                        // return that exception instead
                        if (e.getCause() != null) {
                            t = e.getCause();
                        }
                        throw new PropertyInvocationException(propertyName, node.getPath(), obj.getClass(), "The value '" + string0 + "' for property '" + propertyName + "' caused an exception", t.getMessage(), t);
                    } catch (IllegalAccessException e) {
                        throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Illegal access while setting property", e);
                    }

                // otherwise, this is a "complicated" type
                } else {

                    // try to "get" an instance of this variable if it exists
                    Object targetObj = null;

                    // only "get" the property if its possible -- e.g. if there
                    // is only an addXXXX method available, then this would throw
                    // an exception, so we'll check to see if getting the property
                    // is possible first
                    if (property.canGet()) {
                        try {
                            targetObj = property.get(obj);
                        } catch (IllegalAccessException e) {
                            throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Illegal access while attempting to get property value from object", e);
                        } catch (InvocationTargetException e) {
                            Throwable t = e;
                            // this generally means the setXXXX method on the object
                            // threw an exception -- we want to unwrap that and just
                            // return that exception instead
                            if (e.getCause() != null) {
                                t = e.getCause();
                            }
                            throw new PropertyInvocationException(propertyName, node.getPath(), obj.getClass(), "The existing value for property '" + propertyName + "' caused an exception during get", t.getMessage(), t);
                        }
                    }
                    
                    // if null, then we need to create a new instance of it
                    if (targetObj == null) {
                        try {
                            // create a new instance of this type
                            targetObj = property.getType().newInstance();
                        } catch (InstantiationException e) {
                            throw new XmlBeanClassException("Failed while attempting to create object of type " + property.getType().getName(), e);
                        } catch (IllegalAccessException e) {
                            throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Illegal access while attempting to create new instance of " + property.getType().getName(), e);
                        }
                    }

                    // recursively configure it
                    doConfigure(node, targetObj, properties, checkForDuplicates);

                    try {
                        // save this reference object back, but only if successfully configured
                        property.addOrSet(obj, targetObj);
                    } catch (IllegalAccessException e) {
                        throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Illegal access while attempting to set property", e);
                    } catch (InvocationTargetException e) {
                        Throwable t = e;
                        // this generally means the setXXXX method on the object
                        // threw an exception -- we want to unwrap that and just
                        // return that exception instead
                        if (e.getCause() != null) {
                            t = e.getCause();
                        }
                        throw new PropertyInvocationException(propertyName, node.getPath(), obj.getClass(), "The value(s) for property '" + propertyName + "' caused an exception", t.getMessage(), t);
                    }
                }
            }
        }

    }

    /**
     * Returns whether or not this property type is supported by a simple
     * conversion of a String to a Java object. This method checks if the type
     * is registered in the converter registry or if it represents an Enum.
     * 
     * @param type The property type
     * @return True if its a simple conversion, false otherwise.
     */
    private boolean isSimpleType(Class type) {
        return (converterRegistry.containsKey(type) || type.isEnum());
    }

    /**
     * Converts the string value into an Object of the Class type. Will either
     * delegate conversion to a PropertyConverter or will handle creating enums
     * directly.
     * 
     * @param string0 The string value to convert
     * @param type The Class type to convert it into
     * @return A new Object converted from the String value
     */
    private Object convertType(String string0, Class type) throws ConversionException {
        // if enum, handle differently
        if (type.isEnum()) {
            Object obj = ClassUtil.findEnumConstant(type, string0);
            if (obj == null) {
                throw new ConversionException("Invalid constant '" + string0 + "' used, valid values [" + toListString(type.getEnumConstants()) + "]");
            }
            return obj;
        // else, handle normally
        } else {
            PropertyConverter converter = converterRegistry.get(type);

            if (converter == null) {
                throw new ConversionException("The type " + type.getSimpleName() + " is not supported");
            }

            return converter.convert(string0);
        }
    }

    private String toListString(Object[] list) {
        StringBuilder buf = new StringBuilder(200);
        int i = 0;
        for (Object obj : list) {
            if (i != 0) {
                buf.append(",");
            }
            buf.append(obj.toString());
            i++;
        }
        return buf.toString();
    }

}
