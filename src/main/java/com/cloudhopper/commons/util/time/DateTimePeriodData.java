package com.cloudhopper.commons.util.time;

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
 * Utility class for modeling datasets keyed on a DateTimePeriod.  Classes
 * that extend this class can then use the methods in DateTimePeriodDataUtil
 * such as fill() which will populate a dataset to ensure that every period
 * has at least a default value.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public abstract class DateTimePeriodData {

    private final DateTimePeriod period;

    public DateTimePeriodData(DateTimePeriod period) {
        this.period = period;
    }

    public DateTimePeriod getPeriod() {
        return this.period;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DateTimePeriodData)) {
            throw new ClassCastException("Can only compare equality with other DateTimePeriodData objects");
        }
        return this.period.equals(((DateTimePeriodData)object).period);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.period != null ? this.period.hashCode() : 0);
        return hash;
    }

}
