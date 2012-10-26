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

// java imports
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.concurrent.locks.*;

/**
 * Helps maintain a running total of X number of longs.  Internally,
 * this class maintains a list of X number of last long values.  Adding a new value
 * will evict the oldest value and the new running total will be recomputed.
 * The toString() method will return the running total.  If an average is preferred
 * then please considering using the RunningAverage class.
 *
 * NOTE: This class is thread-safe since its typical multiple threads would want
 * to call this class.  Internally, this uses a ReadWrite lock to ensure
 * efficient usage of this class.
 *
 * NOTE: This class internally stores the running total as a long -- avoid
 * using large values, otherwise this class may cause an overflow.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class RunningTotal {
    
    private long total;
    private int size;
    private Deque<Long> values;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public RunningTotal(int size) {
        this.total = 0;
        this.size = size;
        this.values = new ArrayDeque<Long>(size);
    }

    /**
     * Adds a new value to our running total. If the running total has already
     * reached its max size, this method will evict the oldest value.
     * <br>
     * NOTE: This method will use a writeLock to ensure thread-safety.
     * @param value The new long value to add
     */
    public void add(long value) {
        writeLock.lock();
        try {
            // do we need to evict anything?
            if (this.values.size() >= this.size) {
                Long oldValue = this.values.pollFirst();
                // subtract from running total
                this.total -= oldValue.longValue();
            }
            // add this new value onto the end
            this.values.add(value);
            // add to our total
            this.total += value;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Returns current number of values in our running total.  This should always
     * max out at the size value provided in our constructor.
     * <br>
     * NOTE: This method will use a readLock for thread-safety.
     * @return Current number of values in our running total.
     */
    public long getSize() {
        readLock.lock();
        try {
            return this.values.size();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns current value of our running total.
     * <br>
     * NOTE: This method will use a readLock for thread-safety.
     * @return The current running total.
     */
    public long getTotal() {
        readLock.lock();
        try {
            return this.total;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns current average of our running total.
     * <br>
     * NOTE: This method will use a readLock for thread-safety.
     * @return The current average of our running total.
     */
    public double getAverage() {
        readLock.lock();
        try {
            // any values at all, needed to avoid a DivideByZero exception
            if (this.values.size() <= 0) {
                return 0;
            }
            return ((double)this.total / (double)this.values.size());
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Returns a string representing the current running total. The same value
     * the toString() value a Long class would return.
     * <br>
     * NOTE: Will use a readLock to get a thread-safe version.
     * @return
     */
    @Override
    public String toString() {
        return Long.toString(getTotal());
    }

}
