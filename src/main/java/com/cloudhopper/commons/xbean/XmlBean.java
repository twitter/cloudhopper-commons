/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

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
import com.cloudhopper.commons.util.StringUtil;
import com.cloudhopper.commons.xml.XPath;
import com.cloudhopper.commons.xml.XmlParser;
import com.cloudhopper.commons.xml.XmlParser.Attribute;
import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

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
        HashMap<String,String> properties = new HashMap<String,String>();

        doConfigure(rootNode, obj, properties, true, null);
    }

    /**
     * Internal method for handling the configuration of an object. This method
     * is recursively called for simple and complex properties.
     */
    private void doConfigure(XmlParser.Node rootNode, Object obj, HashMap<String,String> properties, boolean checkForDuplicates, CollectionHelper ch) throws XmlBeanException {
        // loop thru all child nodes
        if (rootNode.hasChildren()) {

            for (XmlParser.Node node : rootNode.getChildren()) {
                
                // tag name represents a property we're going to set
                String propertyName = node.getTag();

                // find the property if it exists
                BeanProperty property = null;
                
                if (ch != null) {
                    // make sure node tag name matches propertyName
                    if (!propertyName.equals(ch.getValueProperty().getName())) {
                        throw new PropertyNotFoundException(propertyName, node.getPath(), obj.getClass(), "Collection can only be configured with a property name of [" + ch.getValueProperty().getName() + "] but [" + propertyName + "] was used instead");
                    }
                    property = ch.getValueProperty();
                } else {
                    try {
                        property = BeanUtil.findBeanProperty(obj.getClass(), propertyName, true);
                    } catch (IllegalAccessException e) {
                        throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Illegal access while attempting to reflect property from class", e);
                    }
                }

                // if property is null, then this isn't a valid property on this object
                if (property == null) {
                    throw new PropertyNotFoundException(propertyName, node.getPath(), obj.getClass(), "Property [" + propertyName + "] not found");
                }

                // only some attributes are permitted if we're dealing with a
                // collection or map at this point
                boolean isCollection = (Collection.class.isAssignableFrom(property.getType()));
                boolean isMap = (Map.class.isAssignableFrom(property.getType()));
                
                // were any attributes included?
                String typeAttrString = null;
                String valueAttrString = null;
                String keyAttrString = null;
                
                // check if an annotation is present for the field
                if (property.getField() != null) {
                    XmlBeanProperty annotation = property.getField().getAnnotation(XmlBeanProperty.class);
                    if (annotation != null) {
                        if (!StringUtil.isEmpty(annotation.value())) {
                            valueAttrString = annotation.value();
                        }
                        if (!StringUtil.isEmpty(annotation.key())) {
                            keyAttrString = annotation.key();
                        }
                    }
                }
                
                // process attributes within the xml itself
                if (node.hasAttributes()) {
                    for (Attribute attr : node.getAttributes()) {
                        if (attr.getName().equals("type")) {
                            typeAttrString = attr.getValue();
                        } else if (attr.getName().equals("value") && (isCollection || isMap)) {
                            // only permitted on collections or map
                            valueAttrString = attr.getValue();
                        } else if (attr.getName().equals("key") && (isMap || (ch != null && ch.isMapType()))) {
                            // only permitted on map type OR on a map value
                            keyAttrString = attr.getValue();
                        } else {
                            throw new PropertyNoAttributesExpectedException(propertyName, node.getPath(), obj.getClass(), "One or more attributes not allowed for property [" + propertyName + "]");
                        }
                    }
                }
                
                //
                // otherwise, the property exists, attempt to set it
                //

                // is there actually a "setter" method -- we shouldn't let
                // user's be able to configure fields in this case
                // unless accessing private properties is allowed
                // unless a collection helper is also null
                if (ch == null && !this.accessPrivateProperties && property.getAddMethod() == null && property.getSetMethod() == null) {
                    throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Not permitted to add or set property [" + propertyName + "]");
                }

                // if we can "add" this property, then turn off checkForDuplicates
                // we also don't check for duplicates in the case of a collection
                if (ch != null || property.canAdd()) {
                    checkForDuplicates = false;
                }
                
                // was this property already previously set?
                // only use this check if an "add" method doesn't exist for the bean
                if (checkForDuplicates && properties.containsKey(node.getPath())) {
                    throw new PropertyAlreadySetException(propertyName, node.getPath(), obj.getClass(), "Property [" + propertyName + "] was already previously set in the xml");
                }
                // add this property to our hashmap
                properties.put(node.getPath(), null);

                
                // if a "type" attribute was included - check that it both exists
                // and is compatible with the type of the property it is being added/set to
                Class typeAttrClass = null;
                if (typeAttrString != null) {
                    try {
                        typeAttrClass = Class.forName(typeAttrString);
                    } catch (ClassNotFoundException e) {
                        throw new PropertyInvalidTypeException(propertyName, node.getPath(), obj.getClass(), "Unable to find class [" + typeAttrString + "] specified in type attribute of property '" + propertyName + "'");
                    }
                    
                    if (!property.getType().isAssignableFrom(typeAttrClass)) {
                        throw new PropertyInvalidTypeException(propertyName, node.getPath(), obj.getClass(), "Unable to assign a value of specified type [" + typeAttrString + "] to property [" + propertyName + "] which is a type [" + property.getType().getName() + "]");
                    }
                }
                
                // the object we'll eventually add or set
                Object value = null;
                // get the node's text value
                String nodeText = node.getText();
                
                // is this a simple conversion?
                if (TypeConverterUtil.isSupported(property.getType())) {
                    // was any text set?  if not, throw an exception
                    if (nodeText == null) {
                        throw new PropertyIsEmptyException(propertyName, node.getPath(), obj.getClass(), "Value for property [" + propertyName + "] was empty in xml");
                    }

                    // try to convert this to a Java object value
                    try {
                        value = TypeConverterUtil.convert(nodeText, property.getType());
                    } catch (ConversionException e) {
                        throw new PropertyConversionException(propertyName, node.getPath(), obj.getClass(), "The value [" + nodeText + "] for property [" + propertyName + "] could not be converted to a(n) " + property.getType().getSimpleName() + ". " + e.getMessage());
                    }

                // otherwise, this is a "complicated" type
                } else {

                    // only "get" the property if its possible -- e.g. if there
                    // is only an addXXXX method available, then this would throw
                    // an exception, so we'll check to see if getting the property
                    // is possible first
                    if (property.canGet()) {
                        try {
                            value = property.get(obj);
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
                            throw new PropertyInvocationException(propertyName, node.getPath(), obj.getClass(), "The existing value for property [" + propertyName + "] caused an exception during get", t.getMessage(), t);
                        }
                    }
                    
                    // if null, then we need to create a new instance of it
                    if (value == null) {
                        Class newType = property.getType();
                        // create a new instance of either the actual type OR the
                        // type specified in the "type" attribute
                        if (typeAttrClass != null) {
                            newType = typeAttrClass;
                        }
                        
                        try {
                            value = newType.newInstance();
                        } catch (InstantiationException e) {
                            throw new XmlBeanClassException("Failed while attempting to create object of type " + newType.getName(), e);
                        } catch (IllegalAccessException e) {
                            throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Illegal access while attempting to create new instance of " + newType.getName(), e);
                        }
                    }
                    
                    // special handling for "collections" -- required for handling
                    // the values in configuring of child objects
                    CollectionHelper newch = null;
                    if (value instanceof Collection || value instanceof Map) {
                        newch = createCollectionHelper(node, obj, value, propertyName, property, valueAttrString, keyAttrString);
                    }

                    // recursively configure the next object
                    doConfigure(node, value, properties, checkForDuplicates, newch);
                }
                
                // save this reference object back (since it was successfully configured)
                if (ch != null) {
                    if (ch.isCollectionType()) {
                        ch.getCollectionObject().add(value);
                    } else if (ch.isMapType()) {
                        // need to figure out the key value -- it may either be a value from the value OR a simple type
                        Object keyValue = null;
                        if (ch.getKeyProperty() == null) {
                            // a KEY must have been set!
                            if (StringUtil.isEmpty(keyAttrString)) {
                                throw new PropertyIsEmptyException(propertyName, node.getPath(), obj.getClass(), "The XML attribute [key] was null or empty and is required");
                            } else {
                                try {
                                    keyValue = TypeConverterUtil.convert(keyAttrString, ch.getKeyClass());
                                } catch (ConversionException e) {
                                    throw new PropertyConversionException(propertyName, node.getPath(), obj.getClass(), "Unable to cleanly convert key value [" + keyAttrString + "] into type [" + ch.getKeyClass().getName() + ": " + e.getMessage(), e);
                                }
                            }
                        } else {
                            try {
                                // extract the key value from the object
                                keyValue = ch.getKeyProperty().get(value);
                            } catch (Exception e) {
                                throw new PropertyPermissionException(propertyName, node.getPath(), value.getClass(), "Unable to access property to get the value of the key: " + e.getMessage(), e);
                            }
                            
                            if (keyValue == null) {
                                throw new PropertyIsEmptyException(propertyName, node.getPath(), obj.getClass(), "The value of the key [" + ch.getKeyProperty().getName() + "] was null; unable to put value onto the map");
                            }
                        }

                        ch.getMapObject().put(keyValue, value);
                    } else {
                        throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Unsupported collection/map type used");
                    }
                } else {
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
                        throw new PropertyInvocationException(propertyName, node.getPath(), obj.getClass(), "The value '" + nodeText + "' for property '" + propertyName + "' caused an exception", t.getMessage(), t);
                    } catch (IllegalAccessException e) {
                        throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Illegal access while setting property", e);
                    }
                }
            }
        }
    }
    
    public CollectionHelper createCollectionHelper(XmlParser.Node node, Object obj, Object value, String propertyName, BeanProperty property, String valueAttrString, String keyAttrString) throws PropertyPermissionException, XmlBeanClassException {
        // special handling for "collections"
        CollectionHelper newch = null;
        
        // determine "propertyName" for elements within the collection
        String valuePropertyName = (valueAttrString != null ? valueAttrString : "value");

        // determine type of objects to create by determining generic type
        if (property.getField() == null) {
            throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Unable to access field for collection type [" + value.getClass().getName() + "]");
        } else {
            Type type = property.getField().getGenericType();
            if (!(type instanceof ParameterizedType)) {
                throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Only collection types with a parameterized type are supported");
            } else {
                ParameterizedType pt = (ParameterizedType)type;
                Type[] pts = pt.getActualTypeArguments();
                
                if (value instanceof Map) {
                    // Maps must have 2 parameterized types
                    if (pts.length != 2) {
                        throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Only map types with 2 parameterized types are supported; actual [" + pts.length + "]");
                    }
                    Type keyType = pts[0];
                    Type valueType = pts[1];
                    if (!(keyType instanceof Class && valueType instanceof Class)) {
                        throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Only a map with paramertized types of concrete classes are supported (e.g. TreeMap<String,Integer> rather than TreeMap<String,ArrayList<String>>)");
                    }
                    Class keyClass = (Class)keyType;
                    Class valueClass = (Class)valueType;
                    Map m = (Map)value;
                    newch = CollectionHelper.createMapType(m, valuePropertyName, valueClass, keyAttrString, keyClass);
                } else {
                    // Collections must have 1 parameterized type
                    if (pts.length != 1) {
                        throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Only collection types with 1 parameterized type are supported; actual [" + pts.length + "]");
                    }
                    Type valueType = pts[0];
                    if (!(valueType instanceof Class)) {
                        throw new PropertyPermissionException(propertyName, node.getPath(), obj.getClass(), "Only a collection with a parameterized type of a concrete class is supported (e.g. ArrayList<String> rather than ArrayList<ArrayList<String>>)");
                    }
                    Class valueClass = (Class)valueType;
                    Collection c = (Collection)value;
                    newch = CollectionHelper.createCollectionType(c, valuePropertyName, valueClass);
                }
            }
        }
        
        return newch;
    }
}
