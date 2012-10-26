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

import com.cloudhopper.commons.util.LoadBalancedList.Node;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utilities for working with LoadBalancedList objects.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class LoadBalancedLists {

    /**
     * Creates a synchronized version of a LoadBalancedList by putting a lock
     * around any method that reads or writes to the internal data structure.
     * @param list The list to synchronize
     * @return A wrapper around the original list that provides synchronized access.
     */
    static public <E> LoadBalancedList<E> synchronizedList(LoadBalancedList<E> list) {
        return new ConcurrentLoadBalancedList<E>(list);
    }

    // implementing class
    private static class ConcurrentLoadBalancedList<E> implements LoadBalancedList<E> {

        // wrapped list
        private final LoadBalancedList<E> list;
        // underlying lock
        private final ReentrantLock lock;

        protected ConcurrentLoadBalancedList(LoadBalancedList<E> list) {
            this.list = list;
            this.lock = new ReentrantLock();
        }

        public List<Node<E>> getValues() {
            return this.list.getValues();
        }

        public void clear() {
            this.lock.lock();
            try {
                this.list.clear();
            } finally {
                this.lock.unlock();
            }
        }

        public boolean contains(E item) {
            this.lock.lock();
            try {
                return list.contains(item);
            } finally {
                this.lock.unlock();
            }
        }

        public E getNext() {
            this.lock.lock();
            try {
                return list.getNext();
            } finally {
                this.lock.unlock();
            }
        }

        public int getSize() {
            this.lock.lock();
            try {
                return list.getSize();
            } finally {
                this.lock.unlock();
            }
        }

        public void remove(E item) {
            this.lock.lock();
            try {
                list.remove(item);
            } finally {
                this.lock.unlock();
            }
        }

        public void set(E item, int weight) {
            this.lock.lock();
            try {
                list.set(item, weight);
            } finally {
                this.lock.unlock();
            }
        }
        
    }
    
}
