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
public class DateTimeHour extends DateTimePeriod {

    protected DateTimeHour(DateTime start, DateTime end) {
        super(start, end, DateTimeDuration.HOUR, "yyyy-MM-dd-HH", "MMMM d, yyyy HH:00", "HH:00", "HH");
    }

    @Override
    public DateTimePeriod getNext() {
        DateTime next = getStart().plusHours(1);
        return DateTimePeriod.createHour(next);
    }

    @Override
    public DateTimePeriod getPrevious() {
        DateTime previous = getStart().minusHours(1);
        return DateTimePeriod.createHour(previous);
    }

    @Override
    public DateTimeDuration getDefaultSubDuration() {
        return DateTimeDuration.FIVE_MINUTES;
    }
    
}
