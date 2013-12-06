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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Utility class for creating lists of periods for users to select from.  For
 * example, a user might want to select from a period covering the "Last Year"
 * as months.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DateTimePeriodSelector {

    private DateTimePeriodSelector() {
        // only static methods
    }

    static public DateTimePeriod thisMonth(DateTimeZone zone) {
        DateTime now = new DateTime(zone);
        return DateTimePeriod.createMonth(now.getYear(), now.getMonthOfYear(), zone);
    }

    static public DateTimePeriod lastMonth(DateTimeZone zone) {
        return thisMonth(zone).getPrevious();
    }
    
    /**
     * Create a list of DateTimePeriods that represent the last 12 month periods
     * based on the current date.  The list will be arranged in ascending order
     * from earliest to latest date. For example, if its currently January 2009,
     * a list containing "February 2008, March 2008, ... , January 2009" would
     * be returned.  If you need this list in reverse order to show the most
     * recent month first, just call Collections.reverse() on the returned list.
     * @param zone The time zone used for calculations
     * @return A list of the last 12 months
     */
    static public List<DateTimePeriod> last12Months(DateTimeZone zone) {
        ArrayList<DateTimePeriod> periods = new ArrayList<DateTimePeriod>();

        // get today's date
        DateTime now = new DateTime(zone);

        // start with today's current month and 11 others (last 12 months)
        for (int i = 0; i < 12; i++) {
            // create a new period
            DateTimePeriod period = DateTimePeriod.createMonth(now.getYear(), now.getMonthOfYear(), zone);
            periods.add(period);
            // subtract 1 month
            now = now.minusMonths(1);
        }

        Collections.reverse(periods);

        return periods;
    }
    
}
