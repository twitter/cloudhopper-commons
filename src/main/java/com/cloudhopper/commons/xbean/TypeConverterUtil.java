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

import com.cloudhopper.commons.util.ClassUtil;
import com.cloudhopper.commons.xbean.type.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

/**
 * Utility class for converting values to/from Strings and Java types.
 * 
 * @author joelauer
 */
public class TypeConverterUtil {
    
    // registry of converters
    private static final HashMap<Class,TypeConverter> REGISTRY = new HashMap<Class,TypeConverter>();

    // statically create registry
    static {
        REGISTRY.put(String.class, new StringTypeConverter());
        REGISTRY.put(boolean.class, new BooleanPrimitiveTypeConverter());
        REGISTRY.put(Boolean.class, new BooleanTypeConverter());
        REGISTRY.put(byte.class, new BytePrimitiveTypeConverter());
        REGISTRY.put(Byte.class, new ByteTypeConverter());
        REGISTRY.put(short.class, new ShortPrimitiveTypeConverter());
        REGISTRY.put(Short.class, new ShortTypeConverter());
        REGISTRY.put(int.class, new IntegerPrimitiveTypeConverter());
        REGISTRY.put(Integer.class, new IntegerTypeConverter());
        REGISTRY.put(long.class, new LongPrimitiveTypeConverter());
        REGISTRY.put(Long.class, new LongTypeConverter());
        REGISTRY.put(double.class, new URLTypeConverter());
        REGISTRY.put(Double.class, new DoubleTypeConverter());
        REGISTRY.put(float.class, new FloatPrimitiveTypeConverter());
        REGISTRY.put(Float.class, new FloatTypeConverter());
        REGISTRY.put(URL.class, new URLTypeConverter());
        REGISTRY.put(File.class, new FileTypeConverter());
    }
    
    /**
     * Returns whether or not this property type is supported by a simple
     * conversion of a String to a Java object. This method checks if the type
     * is registered in the converter registry or if it represents an enum.
     * 
     * @param type The property type
     * @return True if its a simple conversion, false otherwise.
     */
    static public boolean isSupported(Class type) {
        return (REGISTRY.containsKey(type) || type.isEnum());
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
    static public <E> E convert(String s, Class<E> type) throws ConversionException {
        // if enum, handle differently
        if (type.isEnum()) {
            Object obj = ClassUtil.findEnumConstant(type, s);
            if (obj == null) {
                throw new ConversionException("Invalid constant [" + s + "] used, supported values [" + toListString(type.getEnumConstants()) + "]");
            }
            return (E)obj;
        // else, handle normally
        } else {
            TypeConverter converter = REGISTRY.get(type);

            if (converter == null) {
                throw new ConversionException("The type [" + type.getSimpleName() + "] is not supported");
            }

            return (E)converter.convert(s);
        }
    }
    
    static private String toListString(Object[] list) {
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
