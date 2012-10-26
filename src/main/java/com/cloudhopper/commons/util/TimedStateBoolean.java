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
import java.util.concurrent.atomic.AtomicBoolean;

// third party imports
import org.joda.time.DateTime;
import org.joda.time.Period;

// my imports

/**
 * Wrapper around Java AtomicBoolean to include the DateTime of when the state last
 * changed, effectively providing a "time" of the current state.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class TimedStateBoolean {

    private AtomicBoolean value;
    private long valueTime;
    private final Boolean expectedValue;

    /**
     * Creates a new instance with the initalValue. This constructor does not
     * setup an expectedValue, so either value will be ok.
     * @param initialValue The initial value of the boolean.
     */
    public TimedStateBoolean(boolean initialValue) {
        this.value = new AtomicBoolean(initialValue);
        this.valueTime = System.currentTimeMillis();
        this.expectedValue = null;
    }

    /**
     * Creates a new instance with the initalValue. The expectedValue is the
     * value we would expect if this is in a good state.
     * @param initialValue The initial value of the boolean.
     * @param expectedValue The expected value of the boolean if this was in
     *      a good state.
     */
    public TimedStateBoolean(boolean initialValue, boolean expectedValue) {
        this.value = new AtomicBoolean(initialValue);
        this.valueTime = System.currentTimeMillis();
        this.expectedValue = expectedValue;
    }

    /**
     * Returns the expected value of this state boolean. If this instance does
     * not have an expectedValue set, this will always return the current value.
     * @return
     */
    public boolean getExpected() {
        if (expectedValue == null) {
            return get();
        } else {
            return this.expectedValue.booleanValue();
        }
    }

    /**
     * Tests whether this state boolean is what we would expect it to be. If
     * no expectedValue is actually set, this will always return true.
     * @return
     */
    public boolean isExpected() {
        // if no expectedValue is set, this is always true
        if (this.expectedValue == null)
            return true;
        return (this.expectedValue.booleanValue() == get());
    }

    /**
     * Sets to the given value and returns the previous value. If the previous
     * value is different than the new value, the internal "valueTime" will
     * be updated.
     * @param newValue The new boolean value
     * @return
     */
    public boolean getAndSet(boolean newValue) {
        boolean oldValue = value.getAndSet(newValue);
        // did the state change?
        if (oldValue != newValue)
            valueTime = System.currentTimeMillis();
        return oldValue;
    }

    /**
     * Unconditionally sets to the given value.
     * @param newValue The new boolean value
     */
    public void set(boolean newValue) {
        // call internal method in order to track the time
        getAndSet(newValue);
    }

    /**
     * Returns the current value.
     * @return Returns the current value.
     */
    public boolean get() {
        return value.get();
    }

    /**
     * Returns the timestamp (num of milliseconds via System.currentTimeMillis) of the
     * last time this boolean changed state.  In order to calculate the total
     * number of milliseconds this boolean has retained this value, you'll need
     * to do your own call to System.currentTimeMillis() and substract this value.
     */
    public long getValueTimeMillis() {
        return valueTime;
    }

    /**
     * Returns a DateTime that represents the last time this boolean changed state.
     */
    public DateTime getValueDateTime() {
        return new DateTime(valueTime);
    }

    /**
     * Based on the time this method is called (System.currentTimeMillis), this
     * method returns a Period that represents the duration of time this value
     * has retained this value.
     * @see #getValueDurationInMills() 
     */
    public Period getValueDuration() {
        long now = System.currentTimeMillis();
        return new Period(now-valueTime);
    }

    /**
     * Based on the time this method is called (System.currentTimeMillis), this
     * method returns a long that represents the duration of time this value
     * has retained this value.
     * @see #getValueDuration()
     */
    public long getValueDurationInMills() {
        return (System.currentTimeMillis() - valueTime);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
