/*
 * Copyright 2012 Twitter, Inc..
 *
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
 */
package com.cloudhopper.commons.xbean;

import com.cloudhopper.commons.util.BeanProperty;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author joelauer
 */
public class CollectionHelper {
    
    private Collection collectionObject;
    private Map mapObject;
    private Class valueType;
    private BeanProperty property;
    
    public boolean isCollectionType() {
        return (collectionObject != null);
    }
    
    public boolean isMapType() {
        return (mapObject != null);
    }

    public Collection getCollectionObject() {
        return collectionObject;
    }

    public Map getMapObject() {
        return mapObject;
    }

    public Class getValueType() {
        return valueType;
    }
    
    public BeanProperty getProperty() {
        return property;
    }
    
    static public CollectionHelper createCollectionType(Collection collection, Class valueType, String propertyName) {
        CollectionHelper ch = new CollectionHelper();
        ch.collectionObject = collection;
        ch.valueType = valueType;
        ch.property = new BeanProperty(propertyName, valueType, null);
        return ch;
    }
    
}
