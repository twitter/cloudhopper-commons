package com.cloudhopper.commons.util;

/*
 * #%L
 * ch-commons-util
 * %%
 * Copyright (C) 2012 Cloudhopper by Twitter
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

import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * A more simplified map interface than a java.util.Map where only a small subset
 * of functionality is supported.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class StringLookupMap<V> implements SimpleMap<V> {

    private final boolean isCaseSensitive;  // true by default
    private TreeMap<String,V> specificMap;
    private TreeMap<String,V> prefixMap;
    // special case for a key of "*" -- unable to match with a TreeMap
    private V rootPrefixValue;

    public StringLookupMap() {
        this(true);
    }

    public StringLookupMap(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    public int size() {
        // return the number of keys in each map
        return ((this.rootPrefixValue == null ? 0 : 1) + (this.specificMap == null ? 0 : this.specificMap.size()) + (this.prefixMap == null ? 0 : this.prefixMap.size()));
    }

    static protected void assertValidKey(String key) throws NullPointerException, IllegalArgumentException {
        // null keys are not permitted
        if (key == null) {
            throw new NullPointerException("A null key is not permitted");
        }
        // the address must be at least 1 char long
        if (key.length() <= 0) {
            throw new IllegalArgumentException("Illegal key [" + key + "]: must be a minimum length of 1");
        }
    }

    static protected void assertValidGetKey(String key) throws NullPointerException, IllegalArgumentException {
        assertValidKey(key);
        int len = key.length();
        for (int i = 0; i < len; i++) {
            char c = key.charAt(i);
            if (c == '*') {
                throw new IllegalArgumentException("Illegal key [" + key + "]: unsupported char [" + c + "] at index [" + i + "]");
            }
        }
    }

    static protected void assertValidPutKey(String key) throws NullPointerException, IllegalArgumentException {
        assertValidKey(key);
        int len = key.length();
        for (int i = 0; i < len; i++) {
            char c = key.charAt(i);
            if (c == '*' && (i+1) != len) {
                throw new IllegalArgumentException("Illegal key [" + key + "]: [*] can only be the last char in key");
            }
        }
    }

    public String toCorrectKeyCaseSensitivity(String key) {
        if (this.isCaseSensitive) {
            return key;
        } else {
            return key.toLowerCase();
        }
    }

    public V get(String key) throws IllegalArgumentException {
        assertValidGetKey(key);

        key = toCorrectKeyCaseSensitivity(key);

        // check the "specific" map first for an exact match
        if (this.specificMap != null) {
            V specificValue = this.specificMap.get(key);
            if (specificValue != null) {
                return specificValue;
            }
        }

        // if we got here, specific match wasn't found
        if (this.prefixMap != null) {
            // fallback to the "prefix" map and try to find the best match
            Entry<String,V> entry = this.prefixMap.floorEntry(key);
            if (entry != null) {
                // we need to check that the prefix key returned really is an actual prefix
                if (key.startsWith(entry.getKey())) {
                    // somewhat strange, but the key to lookup by > length of prefix key
                    if (key.length() > entry.getKey().length()) {
                        return entry.getValue();
                    }
                }
            }
        }

        // is there a final prefix value?
        if (this.rootPrefixValue != null) {
            return this.rootPrefixValue;
        }

        // nothing was found
        return null;
    }

    public V put(String key, V value) throws IllegalArgumentException {
        assertValidPutKey(key);

        // figure out which treemap to use based on whether a '*' is on the end
        char lastChar = key.charAt(key.length()-1);

        if (lastChar != '*') {
            // put on the "specific" string tree map
            if (this.specificMap == null) {
                this.specificMap = new TreeMap<String,V>();
            }
            return this.specificMap.put(toCorrectKeyCaseSensitivity(key), value);
        } else if (key.equals("*")) {
            V previousValue = this.rootPrefixValue;
            this.rootPrefixValue = value;
            return previousValue;
        } else {
            // chop off the '*', then add to prefix map
            key = key.substring(0, key.length()-1);
            if (this.prefixMap == null) {
                this.prefixMap = new TreeMap<String,V>();
            }
            return this.prefixMap.put(toCorrectKeyCaseSensitivity(key), value);
        }
    }

}
