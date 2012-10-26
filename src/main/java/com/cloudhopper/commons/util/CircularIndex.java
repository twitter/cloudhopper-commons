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
 * Utility class for maintaining a circular index of a fixed capacity.  Useful
 * for implementing a "queue" using a fixed-length array where the head and tail
 * keep looping around the fixed capacity.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class CircularIndex {
    private static Logger logger = LoggerFactory.getLogger(CircularIndex.class);

    // should we reset the sequence if size = 0 (automatically resetting)
    private boolean resetOnZero;
    // fixed capacity of queue - index range of 0 to (capacity-1)
    private long capacity;
    // current size of queue - makes calculations easier
    private long size;
    // first item in queue
    private long first;
    // points to the next available space
    private long next;

    /**
     * Creates a circular index based on a fixed capacity of elements.  This
     * capacity should represent the actual length of an array, etc.  The max
     * size of elements permitted in the index is exactly one less than the
     * capacity (maxSize = capacity - 1).  This max size is driven by the
     * requirement that 1 empty space exists to keep track of where the first
     * and last elements are in the index.
     * @param capacity The fixed capacity of elements.  If this index is to be
     *      used with an array, the capacity should match the capacity of the
     *      array.  Must be > 0.
     */
    public CircularIndex(long capacity) {
        this(capacity, 0, 0);
    }

    public CircularIndex(long capacity, boolean resetOnZero) {
        this(capacity, 0, 0, resetOnZero);
    }

    public CircularIndex(long capacity, long first, long size) {
        this(capacity, first, size, false);
    }

    /**
     * Creates a circular index based on a fixed capacity of elements.  This
     * capacity should represent the actual length of an array, etc.  The max
     * size of elements permitted in the index is exactly one less than the
     * capacity (maxSize = capacity - 1).  This max size is driven by the
     * requirement that 1 empty space exists to keep track of where the first
     * and last elements are in the index.
     * @param capacity The fixed capacity of elements.  If this index is to be
     *      used with an array, the capacity should match the capacity of the
     *      array.  Must be > 0.
     * @param first The initial index of the first element. This must be within
     *      the range based on the capacity.
     * @param size The number of elements contained within the index. Used in
     *      conjunction with the first element index to calculate the "next"
     *      index used internally.  Can be set to zero in order to control
     *      which will be the index of the first element added.
     * @param resetOnZero Controls whether this index should automatically reset
     *      when the size hits zero.  A reset sets the first and next indexes
     *      back to their defaults.
     */
    public CircularIndex(long capacity, long first, long size, boolean resetOnZero) {
        // verify capacity is valid
        if (capacity <= 0) {
            throw new IllegalArgumentException("Index capacity must be > 0");
        }
        this.capacity = capacity;

        // verify the "first" parameter
        if (first < 0) {
            throw new IllegalArgumentException("Index first element must be >= 0 [first=" + first + "]");
        } else if (first >= capacity) {
            throw new IllegalArgumentException("Index first element must be < capacity [first=" + first + ", capacity=" + capacity + "]");
        }
        this.first = first;

        // verify the "size" parameter
        if (size < 0) {
            throw new IllegalArgumentException("Index size element must be >= 0 [size=" + size + "]");
        } else if (size > getMaxSize()) {
            throw new IllegalArgumentException("Index size element must be < getMaxSize [size=" + size + ", getMaxSize=" + getMaxSize() + "]");
        }
        this.size = size;

        // calculate "next" based on size
        this.next = this.calculateNewIndex(first, size);

        this.resetOnZero = resetOnZero;

        checkReset();
    }

    /**
     * Resets index back to defaults with a zero size and no elements.  The
     * first index will start from zero.  The capacity remains the same as when
     * the index was constructed.
     */
    public void reset() {
        this.size = 0;
        this.first = 0;
        this.next = 0;
    }

    private void checkReset() {
        if (this.resetOnZero && this.size == 0) {
            reset();
        }
    }

    /**
     * Gets the underlying capacity of the index.  NOTE: This does not equal
     * the maximum number of elements the index can support.  The capacity is
     * always 1 size larger than the maximum size since one empty space is used
     * to track the first and last items in the index.
     * @return The underlying capacity of the index.
     */
    public long getCapacity() {
        return this.capacity;
    }

    /**
     * Gets the maximum number of elements this index can support.  Always
     * one less than the set capacity.
     * @return The maximum number of elements this index can support.
     */
    public long getMaxSize() {
        // max size is always 1 less than capacity
        return (this.capacity - 1);
    }

    /**
     * Gets the current number of elements in this index.
     * @return The number of elements in this index.
     */
    public long getSize() {
        return this.size;
    }

    /**
     * Checks if the index is currently empty (contains no elements).
     * @return True if the index is empty, otherwise false.
     */
    public boolean isEmpty() {
        return (first == next);
    }

    /**
     * Checks if the index is currently full (contains all elements).
     * @return True if the index is full, otherwise false.
     */
    public boolean isFull() {
        // would one more item would make next == first
        return ((next + 1) % capacity == first);
    }

    protected long calculateNewIndex(long index, long offset) {
        // capacity is added on LHS to handle negative offsets
        // Java does not handle negative modulous operands as I thought, so
        // this is a workaround to correctly calculate it
        return (index + capacity + offset) % capacity;
    }

    /**
     * Adds an element to the back of the index if its not full.
     * @return True if the element was added or false if the index was full.
     */
    public boolean addLast() {
        // check if queue is full
        if (isFull()) {
            return false;
        }

        // increment next available space, rollover to beginning if needed
        next = calculateNewIndex(next, 1);
        //next = (next + 1) % capacity;

        // always increment size
        size++;
        
        return true;
    }

    /**
     * Adds an element to the front of the index if its not full.
     * @return True if the element was added or false if the index was full.
     */
    public boolean addFirst() {
        // check if queue is full
        if (isFull()) {
            return false;
        }

        // increment next available space, rollover to beginning if needed
        first = calculateNewIndex(first, -1);
        //first = (first - 1) % capacity;

        // always increment size
        size++;

        return true;
    }

    /**
     * Removes the last element from the back of the index if its not empty.
     * @return True if the element was removed or false if the index was empty.
     */
    public boolean removeLast() {
        // check if queue is empty
        if (isEmpty()) {
            return false;
        }

        // increment first item, rollver to beginning if needed
        next = calculateNewIndex(next, -1);
        //next = (next - 1) % capacity;

        // always decrement size
        size--;

        // check if we should reset
        checkReset();

        return true;
    }

    /**
     * Removes the first element from the front of the index if its not empty.
     * @return True if the element was removed or false if the index was empty.
     */
    public boolean removeFirst() {
        // check if queue is empty
        if (isEmpty()) {
            return false;
        }

        // increment first item, rollver to beginning if needed
        first = calculateNewIndex(first, 1);
        //first = (first + 1) % capacity;

        // always decrement size
        size--;

        // check if we should reset
        checkReset();

        return true;
    }
    
    /**
     * Gets the index to the first element if it exists.
     * @return The index of the first element (head) or -1 if no elements exist.
     */
    public long getFirst() {
        if (isEmpty()) {
            return -1;
        }
        return this.first;
    }

    /**
     * Gets the index to the last element if it exists.
     * @return The index of the last element (tail) or -1 if no elements exist.
     */
    public long getLast() {
        if (isEmpty()) {
            return -1;
        }
        // next points to current free space, so the one directly before it
        // would actually be the last item
        // NOTE: -1 mod 4 = 3 (isn't the modulus operator schweet?)
        //return ((next - 1) % capacity);
        return calculateNewIndex(next, -1);
    }


    /**
     * Gets the index where the next last element will be put.  This is the index
     * where the next "addLast" operation would put the element. Will return -1 if
     * the index is full (i.e. no next space available).
     * @return The index of where the next last element will be put or -1 if the
     *      the index is full.
     */
    public long getNextLast() {
        if (isFull()) {
            return -1;
        } else if (isEmpty()) {
            // interesting case, always the same as the first
            return first;
        } else {
            return next;
        }
    }

    /**
     * Gets the index where the next first element will be put.  This is the index
     * where the next "addFirst" operation would put the element. Will return -1 if
     * the index is full (i.e. no next space available).
     * @return The index of where the next first element will be put or -1 if the
     *      the index is full.
     */
    public long getNextFirst() {
        if (isFull()) {
            return -1;
        } else if (isEmpty()) {
            // interesting case, always the current first
            return first;
        } else {
            return calculateNewIndex(first, -1);
        }
    }

    /** protected methods just for testing purposes **/
    protected long getInternalFirst() {
        return this.first;
    }

    protected long getInternalNext() {
        return this.next;
    }

    @Override
    public String toString() {
        return new StringBuilder(50)
            .append("[size=")
            .append(size)
            .append(", first=")
            .append(first)
            .append(", next=")
            .append(next)
            .append("]")
            .toString();
    }

}
