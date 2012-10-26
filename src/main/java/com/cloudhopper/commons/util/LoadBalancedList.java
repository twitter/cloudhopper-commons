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

import java.util.List;

/**
 * A list that can act like a load balancer by internally tracking
 * which item to return next.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public interface LoadBalancedList<E> {

    // each "item" in the list to keep track of their weight, etc.
    public static class Node<E> {
        final private E value;
        private int weight;
        private int count;

        public Node(E value) {
            this(value, 0);
        }

        public Node(E value, int weight) {
            this.value = value;
            this.weight = weight;
            this.count = 0;
        }

        public E getValue() {
            return this.value;
        }

        public int getWeight() {
            return this.weight;
        }

        public void setWeight(int value) {
            this.weight = value;
        }

        public int getCount() {
            return this.count;
        }

        public void incrementCount() {
            this.count++;
        }

        public void setCount(int value) {
            this.count = value;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Node)) {
                return false;
            }
            Node otherNode = (Node)other;
            return this.value.equals(otherNode.value);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return new StringBuilder(50)
                .append(value)
                .append(" {weight=")
                .append(weight)
                .append(" count=")
                .append(count)
                .append("}")
                .toString();
        }
    }

    /**
     * Gets a list of all nodes.
     * @return A list of all nodes.
     */
    public List<Node<E>> getValues();

    /**
     * Clears all entries from the list.
     */
    public void clear();

    /**
     * Checks if the item is already contained in this list.
     * @param item The item to check for in list
     * @return True if in list, or false if its not found.
     */
    public boolean contains(E item);

    /**
     * Gets the next item to use from the list.
     * @return The next item to use or null if no items were available.
     */
    public E getNext();

    /**
     * Gets the total number of items (size) of the list.
     * @return The size of the list (number of items)
     */
    public int getSize();

    /**
     * Removes an item from the list. If the item doesn't exist, this method
     * will do nothing.
     * @param item The item to remove.
     */
    public void remove(E item);

    /**
     * Adds the item to the list if it doesn't yet exist.  If the item is already
     * in the list, then this method will update the weight of the item.
     * @param item The item to add or set
     * @param weight The new weight of the item
     */
    public void set(E item, int weight);

}
