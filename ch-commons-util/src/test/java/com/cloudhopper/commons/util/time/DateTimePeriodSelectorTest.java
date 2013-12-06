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

// third party imports
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;

/**
 * Tests DateTimePeriod class.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DateTimePeriodSelectorTest {

    @Test
    public void thisMonth() throws Exception {
        DateTimePeriod period = DateTimePeriodSelector.thisMonth(DateTimeZone.UTC);
        DateTime now = new DateTime(DateTimeZone.UTC);
        DateTimePeriod expectedPeriod = DateTimePeriod.createMonth(now.getYear(), now.getMonthOfYear(), DateTimeZone.UTC);

        Assert.assertEquals(expectedPeriod, period);
    }

    @Test
    public void lastMonth() throws Exception {
        DateTimePeriod period = DateTimePeriodSelector.lastMonth(DateTimeZone.UTC);
        DateTime now = new DateTime(DateTimeZone.UTC);
        now = now.minusMonths(1);
        DateTimePeriod expectedPeriod = DateTimePeriod.createMonth(now.getYear(), now.getMonthOfYear(), DateTimeZone.UTC);

        Assert.assertEquals(expectedPeriod, period);
    }

    @Test
    public void last12Months() throws Exception {
        List<DateTimePeriod> periods = DateTimePeriodSelector.last12Months(DateTimeZone.UTC);
        DateTime now = new DateTime(DateTimeZone.UTC);
        
        // create our list for comparison
        DateTime startingMonth = now.minusMonths(12);
        DateTimePeriod startingPeriod = DateTimePeriod.createMonth(startingMonth.getYear(), startingMonth.getMonthOfYear(), DateTimeZone.UTC);
        ArrayList<DateTimePeriod> expectedPeriods = new ArrayList<DateTimePeriod>();
        expectedPeriods.add(startingPeriod);
        startingPeriod = startingPeriod.getNext();
        expectedPeriods.add(startingPeriod);
        startingPeriod = startingPeriod.getNext();
        expectedPeriods.add(startingPeriod);
        startingPeriod = startingPeriod.getNext();
        expectedPeriods.add(startingPeriod);
        startingPeriod = startingPeriod.getNext();
        expectedPeriods.add(startingPeriod);
        startingPeriod = startingPeriod.getNext();
        expectedPeriods.add(startingPeriod);
        startingPeriod = startingPeriod.getNext();
        expectedPeriods.add(startingPeriod);
        startingPeriod = startingPeriod.getNext();
        expectedPeriods.add(startingPeriod);
        startingPeriod = startingPeriod.getNext();
        expectedPeriods.add(startingPeriod);
        startingPeriod = startingPeriod.getNext();
        expectedPeriods.add(startingPeriod);
        startingPeriod = startingPeriod.getNext();
        expectedPeriods.add(startingPeriod);
        startingPeriod = startingPeriod.getNext();
        expectedPeriods.add(startingPeriod);

        Assert.assertArrayEquals(expectedPeriods.toArray(), periods.toArray());
    }
    
}
