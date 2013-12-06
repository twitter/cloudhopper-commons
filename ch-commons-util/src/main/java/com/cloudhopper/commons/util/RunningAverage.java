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

/**
 * Same as RunningTotal, but provides the average in its toString() method vs.
 * the running total. Also, this provides the precision of the average (Double)
 * to be set in the constructor that will be displayed in the toString() method.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class RunningAverage extends RunningTotal {

    private int precision;

    public RunningAverage(int size, int precision) {
        super(size);
        this.precision = precision;
    }

    /**
     * Returns a string representing the current running average. Will return
     * a String of the double with the precision set in the constructor.
     * <br>
     * NOTE: Will use a readLock to get a thread-safe version.
     * @return
     */
    @Override
    public String toString() {
        return DecimalUtil.toString(getAverage(), precision);
    }

    /**
     * Returns a string representing the current running average, but will use
     * the new precision rather than the one in the constructor.
     * <br>
     * NOTE: Will use a readLock to get a thread-safe version.
     * @return
     */
    public String toString(int temporaryPrecision) {
        return DecimalUtil.toString(getAverage(), temporaryPrecision);
    }

}
