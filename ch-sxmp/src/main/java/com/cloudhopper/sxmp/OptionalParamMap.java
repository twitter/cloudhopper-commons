package com.cloudhopper.sxmp;

/*
 * #%L
 * ch-sxmp
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
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

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.charset.ModifiedUTF8Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Map<String, Object> for StratusRequest optional params.
 * Keys are limited to 255 UTF8 bytes max.
 * Objects can be: short, int, long, double, string (32767 UTF8 bytes max)
 *
 * @author dhoffman
 */
public class OptionalParamMap implements Map<String, Object> {

    static public final int HASH_MAP = 1;
    static public final int TREE_MAP = 2;

    static private final ModifiedUTF8Charset MODIFIED_UTF8_CHARSET = (ModifiedUTF8Charset)CharsetUtil.CHARSET_MODIFIED_UTF8;

    private final Map<String, Object> map;

    /**
     * Create an optional param map based on the specified map type
     * @param type
     * @throws IllegalArgumentException
     */
    public OptionalParamMap(int type) throws IllegalArgumentException {
        switch (type) {
            case HASH_MAP:
                map = new HashMap<String, Object>();
                break;
            case TREE_MAP:
                map = new TreeMap<String, Object>();
                break;
            default:
                throw new IllegalArgumentException("Invalid map type");
        }
    }

    @Override
    public Object put(String key, Object value) {
        if (map.size() + 1 > 255)
            throw new IllegalArgumentException("more than 255 entries");

        validateEntry(key, value);
        return map.put(key, value);
    }

    @Override
    public void putAll(Map map) {
        if (this.map.size() + map.size() > 255)
            throw new IllegalArgumentException("more than 255 entries");

        for (Object s : map.keySet()) {
            if (!(s instanceof String)) {
                throw new IllegalArgumentException("keys must be strings");
            }
            put((String) s, map.get(s));
        }
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    public String getString(String key, String defaultValue) {
        Object o = get(key);
        if (o != null && o instanceof String)
            return (String) o;
        return defaultValue;
    }

    public Integer getInteger(String key, Integer defaultValue) {
        Object o = get(key);
        if (o != null && o instanceof Integer)
            return (Integer) o;
        return defaultValue;
    }

    /*
     * getLong will successfully return for both Long values and Integer values
     * it will convert the Integer to a Long
     */
    public Long getLong(String key, Long defaultValue) {
        Object o = get(key);
        if (o != null) {
            if (o instanceof Long) {
                return (Long) o;
            } else if (o instanceof Integer) {
                return new Long((Integer)o);
            }
        }
        return defaultValue;
    }

    public Double getDouble(String key, Double defaultValue) {
        Object o = get(key);
        if (o != null && o instanceof Double)
            return (Double) o;
        return defaultValue;
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return map.containsValue(o);
    }

    @Override
    public Object remove(Object o) {
        return map.remove(o);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (String key : keySet()) {
            sb.append(key).append("=");
            Object o = get(key);
            if (o instanceof Integer)
                sb.append("i");
            else if (o instanceof Long)
                sb.append("l");
            else if (o instanceof Double)
                sb.append("d");
            sb.append(o).append(", ");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof OptionalParamMap)
            return map.equals(((OptionalParamMap) o).map);
        return false;
    }

    /**
     * validation:
     * enforces key length less than 256 UTF8 bytes
     * enforces valid value type (cannot be null)
     * enforces valid String value length less than 32767 UTF8 bytes
     */
    private void validateEntry(String key, Object value) {
        if (isBlank(key)) {
            throw new IllegalArgumentException("key cannot be null/blank");
        }
        if (ModifiedUTF8Charset.calculateByteLength(key) > 255) {
            throw new IllegalArgumentException("key length > 255 bytes");
        }
        else if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        else if (value instanceof String) {
            if (ModifiedUTF8Charset.calculateByteLength((String) value) > 32767) {
                throw new IllegalArgumentException("string value length > 34767 bytes");
            }
        }
        else if (value instanceof Integer ||
                 value instanceof Long ||
                 value instanceof Double) {
            // valid
        } else {
            throw new IllegalArgumentException("Illegal value type: "+value.getClass().toString());
        }
    }

    // Replaces org.apache.commons.lang.StringUtils.isBlank
    private static boolean isBlank(String str) {
	int strLen;
	if (str == null || (strLen = str.length()) == 0) {
	    return true;
	}
	for (int i = 0; i < strLen; i++) {
	    if ((Character.isWhitespace(str.charAt(i)) == false)) {
		return false;
	    }
	}
	return true;
    }

}
