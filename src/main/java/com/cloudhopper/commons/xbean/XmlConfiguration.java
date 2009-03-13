
package com.cloudhopper.commons.xbean;

import com.cloudhopper.commons.util.ClassUtil;

/**
 *
 * @author joelauer
 */
public class XmlConfiguration {

    public void configure(XmlParser.Node rootNode, Object obj) throws BasePropertyException, Exception {
        // root node doesn't matter -- unless we're going to validate its name

        // FIXME: do we do anything with attributes of a node?

        // loop thru all child nodes
        if (rootNode.hasChildren()) {
            for (XmlParser.Node node : rootNode.getChildren()) {
                // tag name represents a property we're going to set
                String propertyName = node.getTag();

                // find the property if it exists
                ClassUtil.BeanProperty property = ClassUtil.getBeanProperty(obj.getClass(), propertyName, true);

                // if property is null, then this isn't a valid property on this object
                if (property == null) {
                    throw new PropertyNotFoundException(propertyName, node.getPath(), "Object '" + obj.getClass().getSimpleName() + "' does not contain property");
                }

                //
                // otherwise, the property exists, attempt to set it
                //

                // initially, we'll assume its a simple property using a standard
                // java primitive like an int or Integer
                // attempt to convert the string value into the correct object type
                Object value = convertType(node.getText(), property.getType());

                // is this property of a type we can handle?


                
                
                // second, attempt to set this value on the object
                property.set(obj, value);
            }
        }

    }

    public Object convertType(String string0, Class type) throws IllegalArgumentException {

        // convert the type from a String value into an actual object
        if (type.equals(String.class)) {
            return string0;

        } else if (type.equals(boolean.class)) {
            return Boolean.parseBoolean(string0);
        } else if (type.equals(Boolean.class)) {
            return Boolean.valueOf(string0);

        } else if (type.equals(byte.class)) {
            return Byte.parseByte(string0);
        } else if (type.equals(Byte.class)) {
            return Byte.valueOf(string0);

        } else if (type.equals(short.class)) {
            return Short.parseShort(string0);
        } else if (type.equals(Short.class)) {
            return Short.valueOf(string0);

        } else if (type.equals(int.class)) {
            return Integer.parseInt(string0);
        } else if (type.equals(Integer.class)) {
            return Integer.valueOf(string0);

        } else if (type.equals(long.class)) {
            return Long.parseLong(string0);
        } else if (type.equals(Long.class)) {
            return Long.valueOf(string0);
        }

        return null;
    }

}
