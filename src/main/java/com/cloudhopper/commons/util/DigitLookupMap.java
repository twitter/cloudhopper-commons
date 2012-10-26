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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Trie based implementation of the SimpleMap interface used to map String keys
 * (containing only digits) to values.  This implementation supports
 * prefix and/or specific key matches.  The caller can explicitly permit
 * a key to match a prefix or specific value by appending a '*' character on the end.
 * This class guarantees that the map will be in numerically ascending key order,
 * sorted according to the natural order for the key's prefix and specific key
 * values.
 * <br>
 * A "specific" key contains only digits such as "13135551212".  Specific keys
 * will only match lookups that precisely match the key used.  A "prefix" key
 * contains zero or more digits, followed by a "*" character. Prefix keys will
 * match the best value.
 * <br>
 * Looking up a key of length (m) takes a worst case O(m) time.  This Trie
 * based implementation is a super efficient way of storing a large number of
 * digit-only Strings, especially when many of the Strings contain the same
 * prefix. The keys are not stored explicitly and nodes are shared between keys
 * with common initial subsequences.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DigitLookupMap<V> implements SimpleMap<V> {
    private static final Logger logger = LoggerFactory.getLogger(DigitLookupMap.class);

    // number of key-value mappings (both specific and prefix)
    private int size;
    private Node<V> root;

    public DigitLookupMap() {
        this.size = 0;
        this.root = null;
    }

    /**
     * Returns the number of key-value mappings in this map. This underlying
     * implementation includes both specific and prefix mappings are part
     * of the size.  A null value does not count as a key-value mapping.
     * @return The number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public V get(String key) throws IllegalArgumentException {
        assertValidGetKey(key);
        
        char c;
        int index;
        int keyLength = key.length();

        // if the "root" value is null, no keys have been put yet, always return null
        if (this.root == null) {
            return null;
        }

        Node<V> currentNode = this.root;

        // default the best matched value to the "root" node
        V bestMatchedValue = currentNode.getPrefixValue();

        // search through the key for the best match
        for (int i = 0; i < keyLength; i++) {
            c = key.charAt(i);
            index = toNodeArrayIndex(c);

            // assertValidPutKey should already check, but to be safe
            // check if it was a valid character
            if (index == -1 || index == 10) {
                throw new IllegalArgumentException("Illegal key [" + key + "]: unsupported char [" + c + "] at index [" + i + "]");
            }

            // if last char then see if there is a "specific" value
            if ((i+1) == keyLength) {
                // grab this specific index's node
                Node<V> specificNode = currentNode.getNode(index);
                // does a specific value exist?
                if (specificNode != null && specificNode.getSpecificValue() != null) {
                    // this is definitely the best value to return
                    return specificNode.getSpecificValue();
                }
            } else {
                // make the "current" node the "next" node
                currentNode = currentNode.getNode(index);
                // if the "next" node is null, then we already have the "best" value
                if (currentNode == null) {
                    return bestMatchedValue;
                } else {
                    // if a "prefix" value exists, this is actually the better value
                    if (currentNode.getPrefixValue() != null) {
                        bestMatchedValue = currentNode.getPrefixValue();
                    }
                }
            }
        }

        return bestMatchedValue;
    }

    /**
     * Associates the value with the key in this map. If the
     * map previously contained a mapping for this key, the old value is replaced
     * by the specified value. (A map m is said to contain a mapping for a key k
     * if and only if m.containsKey(k) would return true.)
     * @param key The key with which the specified value is to be associated.
     *      Null keys are not permitted and will throw a NullPointerException.
     * @param value The value to be associated with the specified key.
     * @return The previous value associated with specified key, or null if there
     *      was no mapping for key. A null return can also indicate that the map
     *      previously associated null with the specified key, if the
     *      implementation supports null values.
     * @throws IllegalArgumentException Thrown if the key or value is not valid
     */
    @Override
    public V put(String key, V value) throws IllegalArgumentException {
        assertValidPutKey(key);

        char c;
        int index;
        int keyLength = key.length();

        // if the "root" value is null, no keys have been put yet, and it's time
        // that we create the initial root value
        if (this.root == null) {
            // create a new "root" node by setting the parent to null
            this.root = new Node<V>(null);
        }

        Node<V> currentNode = this.root;
        Node<V> nextNode = null;
        V previousValue = null;

        // search through the address until we reach the end OR best match
        for (int i = 0; i < keyLength; i++) {
            c = key.charAt(i);
            index = toNodeArrayIndex(c);

            // assertValidPutKey should already check, but to be safe
            if (index == -1) {
                throw new IllegalArgumentException("Illegal key [" + key + "]: unsupported char [" + c + "] at index [" + i + "]");
            }

            // the '*' or index of 10, means we set the prefix value at the current node
            if (index == 10) {
                // assertValidPutKey should already check, but to be safe
                // this *must* be the last char in the key, otherwise, this is an invalid key
                if (i+1 != keyLength) {
                    throw new IllegalArgumentException("Illegal key [" + key + "]: [*] can only be the last char in key");
                }
                // set the prefix value (and get the previous value)
                previousValue = currentNode.setPrefixValue(value);
                // done processing, nothing further is required
                break;
            }

            // try to get the "next" node at this index
            nextNode = currentNode.getNode(index);

            // see if the "next" node exists
            if (nextNode == null) {
                // does not exist, create a new node and associate with current node as parent
                nextNode = new Node<V>(currentNode);
                // set this node to the index value
                currentNode.setNode(index, nextNode);
            }

            // "next" node becomes "current" node
            currentNode = nextNode;

            // is the final character in the key?
            if (i+1 == keyLength) {
                // set the specific value (and get the previous value)
                previousValue = currentNode.setSpecificValue(value);
            }
        }

        if (previousValue == null && value != null) {
            // added key-value mapping
            this.size++;
        } else if (previousValue != null && value == null) {
            // removed key-value mapping
            this.size--;
        } else {
            // key-value mapping wasn't removed or added -- do nothing
        }

        return previousValue;
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
            int index = toNodeArrayIndex(c);
            if (index < 0 || index >= 10) {
                throw new IllegalArgumentException("Illegal key [" + key + "]: unsupported char [" + c + "] at index [" + i + "]");
            }
        }
    }

    static protected void assertValidPutKey(String key) throws NullPointerException, IllegalArgumentException {
        assertValidKey(key);
        int len = key.length();
        for (int i = 0; i < len; i++) {
            char c = key.charAt(i);
            int index = toNodeArrayIndex(c);
            if (index < 0) {
                throw new IllegalArgumentException("Illegal key [" + key + "]: unsupported char [" + c + "] at index [" + i + "]");
            } else if (index == 10 && (i+1) != len) {
                throw new IllegalArgumentException("Illegal key [" + key + "]: [*] can only be the last char in key");
            }
        }
    }

    /**
     * Returns the node array index of the character for 0-9, or 10 for the '*'
     * character.  Returns -1 if no mapping exists.
     */
    static protected int toNodeArrayIndex(char c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case '*':
                return 10;
        }
        return -1;
    }


    /**
     * Prints out a dump of the hierarchy of this AddressTree
     * to the specified PrintStream.
     */
    public void debug(java.io.PrintStream out) {
        // start at parent node...
        dumpNode(root, 0, -2, out);
    }

    /**
     * Recursively dumps a node at a certain index.
     */
    private void dumpNode(Node node, int level, int idx, java.io.PrintStream out) {

        // if node null, then return
        if (node == null) {
            return;
        }

        // print out required spaces
        printSpaces(level, out);

        if (idx == -2) {
            out.print("ROOT -> ");
        } else {
            out.print(idx + " -> ");
        }

        // print out the values
        out.print("S: ");
        if (node.getSpecificValue() != null) {
            out.print(node.getSpecificValue().toString());
        } else {
            out.print("(null)");
        }
        out.print(", W: ");
        if (node.getPrefixValue() != null) {
            out.println(node.getPrefixValue().toString());
        } else {
            out.println("(null)");
        }

        // loop through each child node
        for (int index = 0; index < 10; index++) {
            // dump out this node...
            dumpNode(node.getNode(index), level + 1, index, out);
        }
    }

    /**
     * Prints out certain number of spaces.
     */
    private void printSpaces(int count, java.io.PrintStream out) {
        for (int i = 0; i < count; i++) {
            out.print(" ");
        }
    }

    private static class Node<V> {
        private Node<V> parent;
        private Node<V>[] children;
        private V specificValue;
        private V prefixValue;

        /**
         * Creates a new instance of a <code>Node</code> associated with a parent.
         */
        public Node(Node<V> parent) {
            this.parent = parent;
            this.specificValue = null;
            this.prefixValue = null;
            this.children = null;
        }

        public Node<V> getParent() {
            return this.parent;
        }

        /**
         * Returns the "specific" value of the node.
         * @return The current "specific" value of the node.
         */
        public V getSpecificValue() {
            return specificValue;
        }

        /**
         * Set the "specific" value of the node.
         * @return The previous "specific" value
         */
        public V setSpecificValue(V value) {
            V previousValue = this.specificValue;
            specificValue = value;
            return previousValue;
        }

        /**
         * Returns the "prefix" value of the node.
         * @return The current "prefix" value of the node.
         */
        public V getPrefixValue() {
            return prefixValue;
        }

        /**
         * Set the "prefix" value of the node.
         * @return Return the previous "prefix" value
         */
        public V setPrefixValue(V value) {
            V previousValue = this.prefixValue;
            prefixValue = value;
            return previousValue;
        }

        /**
         * Gets the ith child node.  Returns null if the node doesn't exist or
         * the underlying node array hasn't been created yet.  NOTE: The underlying
         * node array won't be created until at least one setNode() call has
         * been made.
         * @return The ith child node.
         */
        public Node<V> getNode(int i) {
            if (this.children == null) {
                return null;
            }
            return this.children[i];
        }

        /**
         * Sets the ith child node.
         * @param i The index of the node to set
         * @param node The node to set
         */
        @SuppressWarnings("unchecked")
        public void setNode(int i, Node<V> node) {
            if (node == null && this.children == null) {
                return; // do nothing
            }
            // time to finally lazily create the children node array
            if (this.children == null) {
                this.children = new Node[10];
            }
            this.children[i] = node;
        }
    }
}
