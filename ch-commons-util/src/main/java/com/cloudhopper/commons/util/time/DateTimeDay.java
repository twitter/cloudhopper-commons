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

import org.joda.time.DateTime;

/**
 *
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DateTimeDay extends DateTimePeriod {

    protected DateTimeDay(DateTime start, DateTime end) {
        super(start, end, DateTimeDuration.DAY, "yyyy-MM-dd", "MMMM d, yyyy", "yyyy-MM-dd", "dd");
    }

    @Override
    public DateTimePeriod getNext() {
        DateTime next = getStart().plusDays(1);
        return DateTimePeriod.createDay(next);
    }

    @Override
    public DateTimePeriod getPrevious() {
        DateTime previous = getStart().minusDays(1);
        return DateTimePeriod.createDay(previous);
    }

    @Override
    public DateTimeDuration getDefaultSubDuration() {
        return DateTimeDuration.HOUR;
    }
    
}
