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

import com.cloudhopper.commons.util.BeanProperty;
import com.cloudhopper.commons.util.BeanUtil;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author joelauer
 */
public class CollectionHelper {
    
    private Collection collectionObject;
    private Class valueClass;                   // used on maps or collections
    private BeanProperty valueProperty;         // used on maps or collections
    private Map mapObject;
    private Class keyClass;                     // only used on maps
    private BeanProperty keyProperty;           // only used on maps
    
    public boolean isCollectionType() {
        return (collectionObject != null);
    }
    
    public boolean isMapType() {
        return (mapObject != null);
    }

    public Collection getCollectionObject() {
        return collectionObject;
    }

    public Class getValueClass() {
        return valueClass;
    }
    
    public BeanProperty getValueProperty() {
        return valueProperty;
    }
    
    public Map getMapObject() {
        return mapObject;
    }

    public Class getKeyClass() {
        return keyClass;
    }

    public BeanProperty getKeyProperty() {
        return keyProperty;
    }

    static public CollectionHelper createCollectionType(Collection collection, String valuePropertyName, Class valueClass) {
        CollectionHelper ch = new CollectionHelper();
        ch.collectionObject = collection;
        ch.valueClass = valueClass;
        ch.valueProperty = new BeanProperty(valuePropertyName, valueClass, null);
        return ch;
    }
    
    static public CollectionHelper createMapType(Map map, String valuePropertyName, Class valueClass, String keyPropertyName, Class keyClass) throws XmlBeanClassException {
        CollectionHelper ch = new CollectionHelper();
        ch.mapObject = map;
        ch.valueClass = valueClass;
        ch.valueProperty = new BeanProperty(valuePropertyName, valueClass, null);
        
        // key properties
        ch.keyClass = keyClass;
        
        // if the key property name is set -- this is the value which will be
        // used for the key -- if its null then the xml MUST supply a key attribute
        if (keyPropertyName != null) {
            // find BeanProperty to "get" the key when we're ready to put
            try {
                ch.keyProperty = BeanUtil.findBeanProperty(valueClass, keyPropertyName, true);
            } catch (IllegalAccessException e) {
                throw new XmlBeanClassException("Unable to access class " + valueClass.getName() + " while searching for key", e);
            }
            if (ch.keyProperty == null) {
                throw new XmlBeanClassException("Unable to find key getter/setter for property [" + keyPropertyName + "] on class " + valueClass.getName());
            }
            // also need to check that the property type is compatible with the actual generic
            if (!keyClass.isAssignableFrom(ch.keyProperty.getType())) {
                throw new XmlBeanClassException("Incompatible class specified for key; class of map key [" + keyClass.getName() + "] and class of object key [" + ch.keyProperty.getType().getName() + "] for property [" + keyPropertyName + "]");
            }
        } else {
            // if no key property is explicity set -- then only strings in the 
            // key attribute on an element must be used.  Thereforce, the keyClass
            // must be of a simpleType
            if (!TypeConverterUtil.isSupported(keyClass)) {
                throw new XmlBeanClassException("If no key getter/setter property was set then only key attributes are allowed; these can only be simple types such as a String, Integer; but was actually class " + keyClass.getName());
            }
        }
        
        return ch;
    }
}
