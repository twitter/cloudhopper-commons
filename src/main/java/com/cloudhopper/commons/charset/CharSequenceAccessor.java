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
package com.cloudhopper.commons.charset;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Utility class for accessing private fields in common CharSequence classes
 * such as String, StringBuilder, etc.
 * 
 * @author joelauer
 */
public class CharSequenceAccessor {
    
    // tests prove accessing raw char[] value from various common types of
    // of CharSequence implementations like String, StringBuilder, etc. speed
    // up calculations vs. using charAt()
    static private final Constructor<String> stringConstructor;
    static private final Field stringValueField;
    static private final Field stringOffsetField;
    static private final Field stringCountField;
    static private final boolean hasStringFields;
    static private final Field stringBuilderValueField;
    static private final Field stringBuilderCountField;
    static private final boolean hasStringBuilderFields;
    
    static {
        Constructor<String> stringConstructorTemp = null;
        Field stringValueFieldTemp = null, stringOffsetFieldTemp = null, stringCountFieldTemp = null;
        Field stringBuilderValueFieldTemp = null, stringBuilderCountFieldTemp = null;
        boolean hasStringFieldsTemp = false, hasStringBuilderFieldsTemp = false;
        
        try {
            stringValueFieldTemp = String.class.getDeclaredField("value");
            stringValueFieldTemp.setAccessible(true);
            stringOffsetFieldTemp = String.class.getDeclaredField("offset");
            stringOffsetFieldTemp.setAccessible(true);
            stringCountFieldTemp = String.class.getDeclaredField("count");
            stringCountFieldTemp.setAccessible(true);
            hasStringFieldsTemp = true;
            stringConstructorTemp = String.class.getDeclaredConstructor(int.class, int.class, char[].class);
        } catch (Throwable t) {
            // do nothing -- these optimizations just won't be used
            //throw new RuntimeException("Unable to get [count] field from String.class", t);
        }
        
        try {
            // string builder defines value[] in its parent class
            stringBuilderValueFieldTemp = StringBuilder.class.getSuperclass().getDeclaredField("value");
            stringBuilderValueFieldTemp.setAccessible(true);
            // string builder defines value[] in its parent class
            stringBuilderCountFieldTemp = StringBuilder.class.getSuperclass().getDeclaredField("count");
            stringBuilderCountFieldTemp.setAccessible(true);
            hasStringBuilderFieldsTemp = true;
        } catch (Throwable t) {
            // do nothing -- these optimizations just won't be used
            //throw new RuntimeException("Unable to get [count] field from StringBuilder.class", t);
        }
        
        stringValueField = stringValueFieldTemp;
        stringOffsetField = stringOffsetFieldTemp;
        stringCountField = stringCountFieldTemp;
        hasStringFields = hasStringFieldsTemp;
        stringConstructor = stringConstructorTemp;
        stringBuilderValueField = stringBuilderValueFieldTemp;
        stringBuilderCountField = stringBuilderCountFieldTemp;
        hasStringBuilderFields = hasStringBuilderFieldsTemp;
    }
    
    public static class CharArrayWrapper {
        public char[] value;
        public int offset;
        public int length;
    }
    
    static public CharArrayWrapper access(CharSequence str) {
        if (str == null) {
            return null;
        }
        try {
            if (str instanceof String) {
                if (hasStringFields) {
                    CharArrayWrapper w = new CharArrayWrapper();
                    w.value = (char[])stringValueField.get(str);
                    w.offset = stringOffsetField.getInt(str);
                    w.length = stringCountField.getInt(str);
                    return w;
                }
            } else if (str instanceof StringBuilder) {
                if (hasStringBuilderFields) {
                    CharArrayWrapper w = new CharArrayWrapper();
                    w.value = (char[])stringBuilderValueField.get(str);
                    w.length = stringBuilderCountField.getInt(str);
                    return w;
                }
            }
        } catch (Throwable t) {
            // do nothing
        }
        return null;
    }
    
    static public String createOptimizedString(char[] buffer, int offset, int length) {
        try {
            if (hasStringFields) {
                // create a string
                String s = new String();
                stringValueField.set(s, buffer);
                if (offset != 0)
                    stringOffsetField.setInt(s, offset);
                stringCountField.setInt(s, length);
                return s;
            }
        } catch (Throwable t) {
            // do nothing
        }
        return new String(buffer, offset, length);
    }
    
    static public void updateStringBuilder(StringBuilder sb, int newLength) {
        try {
            if (hasStringBuilderFields) {
                stringBuilderCountField.setInt(sb, newLength);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update count field for StringBuilder");
        }
    }
    
}
