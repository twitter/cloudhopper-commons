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

/**
 * A more simplified map interface than a java.util.Map where only a small subset
 * of functionality is supported.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public interface SimpleMap<V> {

    /**
     * Returns the value to which this map maps the specified key. Returns null
     * if the map contains no mapping for this key. A return value of null does
     * not necessarily indicate that the map contains no mapping for the key;
     * it's also possible that the map explicitly maps the key to null. The
     * containsKey operation may be used to distinguish these two cases.
     * <br>
     * More formally, if this map contains a mapping from a key k to a value v
     * such that (key==null ? k==null : key.equals(k)), then this method returns
     * v; otherwise it returns null. (There can be at most one such mapping.)
     * @param key The key whose associated value is to be returned.
     * @return The value to which this map maps the specified key, or null if
     *      the map contains no mapping for this key.
     * @throws IllegalArgumentException Thrown if the key or value is not valid
     */
    public V get(String key) throws IllegalArgumentException;

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for this key, the old value is replaced
     * by the specified value. (A map m is said to contain a mapping for a key k
     * if and only if m.containsKey(k) would return true.)
     * @param key The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @return The previous value associated with specified key, or null if there
     *      was no mapping for key. A null return can also indicate that the map
     *      previously associated null with the specified key, if the
     *      implementation supports null values.
     * @throws IllegalArgumentException Thrown if the key or value is not valid
     */
    public V put(String key, V value) throws IllegalArgumentException;

    /**
     * Returns the number of key-value mappings in this map.
     * @return The number of key-value mappings in this map.
     */
    int size();

}
