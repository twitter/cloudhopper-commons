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
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;

/**
 * Tests DateTimePeriod class.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DateTimePeriodTest {

    @Test
    public void createYear() throws Exception {
        // test all 3 ways to create a year
        DateTimePeriod period0 = DateTimePeriod.createYear(2009, DateTimeZone.UTC);
        DateTimePeriod period1 = DateTimePeriod.createYear(new DateTime(2009,1,1,0,0,0,0,DateTimeZone.UTC));
        DateTimePeriod period2 = DateTimePeriod.create(DateTimeDuration.YEAR, new DateTime(2009,1,1,0,0,0,0,DateTimeZone.UTC));

        DateTime correctStart = new DateTime(2009,1,1,0,0,0,0,DateTimeZone.UTC);
        DateTime correctEnd = new DateTime(2010,1,1,0,0,0,0,DateTimeZone.UTC);
        Assert.assertEquals(correctStart, period0.getStart());
        Assert.assertEquals(correctEnd, period0.getEnd());
        Assert.assertEquals(DateTimeDuration.YEAR, period0.getDuration());
        Assert.assertEquals("2009", period0.getKey());
        Assert.assertEquals("2009", period0.getLongName());
        Assert.assertEquals("2009", period0.getShortName());
        Assert.assertEquals("2009", period0.getIndexName());

        Assert.assertEquals(correctStart, period1.getStart());
        Assert.assertEquals(correctEnd, period1.getEnd());
        Assert.assertEquals(DateTimeDuration.YEAR, period1.getDuration());
        Assert.assertEquals("2009", period1.getKey());
        Assert.assertEquals("2009", period0.getLongName());
        Assert.assertEquals("2009", period0.getShortName());
        Assert.assertEquals("2009", period0.getIndexName());

        Assert.assertEquals(correctStart, period2.getStart());
        Assert.assertEquals(correctEnd, period2.getEnd());
        Assert.assertEquals(DateTimeDuration.YEAR, period2.getDuration());
        Assert.assertEquals("2009", period2.getKey());
        Assert.assertEquals("2009", period0.getLongName());
        Assert.assertEquals("2009", period0.getShortName());
        Assert.assertEquals("2009", period0.getIndexName());
    }
    
    @Test
    public void createMonth() throws Exception {
        // test all 3 ways to create a month
        DateTimePeriod period0 = DateTimePeriod.createMonth(2009, 2, DateTimeZone.UTC);
        DateTimePeriod period1 = DateTimePeriod.createMonth(new DateTime(2009,2,1,0,0,0,0,DateTimeZone.UTC));
        DateTimePeriod period2 = DateTimePeriod.create(DateTimeDuration.MONTH, new DateTime(2009,2,1,0,0,0,0,DateTimeZone.UTC));

        DateTime correctStart = new DateTime(2009,2,1,0,0,0,0,DateTimeZone.UTC);
        DateTime correctEnd = new DateTime(2009,3,1,0,0,0,0,DateTimeZone.UTC);
        Assert.assertEquals(correctStart, period0.getStart());
        Assert.assertEquals(correctEnd, period0.getEnd());
        Assert.assertEquals(DateTimeDuration.MONTH, period0.getDuration());
        Assert.assertEquals("2009-02", period0.getKey());
        Assert.assertEquals("February 2009", period0.getLongName());
        Assert.assertEquals("2009-02", period0.getShortName());
        Assert.assertEquals("02", period0.getIndexName());

        Assert.assertEquals(correctStart, period1.getStart());
        Assert.assertEquals(correctEnd, period1.getEnd());
        Assert.assertEquals(DateTimeDuration.MONTH, period1.getDuration());
        Assert.assertEquals("2009-02", period1.getKey());
        Assert.assertEquals("February 2009", period0.getLongName());
        Assert.assertEquals("2009-02", period0.getShortName());
        Assert.assertEquals("02", period0.getIndexName());

        Assert.assertEquals(correctStart, period2.getStart());
        Assert.assertEquals(correctEnd, period2.getEnd());
        Assert.assertEquals(DateTimeDuration.MONTH, period2.getDuration());
        Assert.assertEquals("2009-02", period2.getKey());
        Assert.assertEquals("February 2009", period0.getLongName());
        Assert.assertEquals("2009-02", period0.getShortName());
        Assert.assertEquals("02", period0.getIndexName());
    }

    @Test
    public void createDay() throws Exception {
        // test all 3 ways to create a day
        DateTimePeriod period0 = DateTimePeriod.createDay(2009, 2, 1, DateTimeZone.UTC);
        DateTimePeriod period1 = DateTimePeriod.createDay(new DateTime(2009,2,1,0,0,0,0,DateTimeZone.UTC));
        DateTimePeriod period2 = DateTimePeriod.create(DateTimeDuration.DAY, new DateTime(2009,2,1,0,0,0,0,DateTimeZone.UTC));

        DateTime correctStart = new DateTime(2009,2,1,0,0,0,0,DateTimeZone.UTC);
        DateTime correctEnd = new DateTime(2009,2,2,0,0,0,0,DateTimeZone.UTC);
        Assert.assertEquals(correctStart, period0.getStart());
        Assert.assertEquals(correctEnd, period0.getEnd());
        Assert.assertEquals(DateTimeDuration.DAY, period0.getDuration());
        Assert.assertEquals("2009-02-01", period0.getKey());
        Assert.assertEquals("February 1, 2009", period0.getLongName());
        Assert.assertEquals("2009-02-01", period0.getShortName());
        Assert.assertEquals("01", period0.getIndexName());

        Assert.assertEquals(correctStart, period1.getStart());
        Assert.assertEquals(correctEnd, period1.getEnd());
        Assert.assertEquals(DateTimeDuration.DAY, period1.getDuration());
        Assert.assertEquals("2009-02-01", period1.getKey());
        Assert.assertEquals("February 1, 2009", period0.getLongName());
        Assert.assertEquals("2009-02-01", period0.getShortName());
        Assert.assertEquals("01", period0.getIndexName());

        Assert.assertEquals(correctStart, period2.getStart());
        Assert.assertEquals(correctEnd, period2.getEnd());
        Assert.assertEquals(DateTimeDuration.DAY, period2.getDuration());
        Assert.assertEquals("2009-02-01", period2.getKey());
        Assert.assertEquals("February 1, 2009", period0.getLongName());
        Assert.assertEquals("2009-02-01", period0.getShortName());
        Assert.assertEquals("01", period0.getIndexName());
    }

    @Test
    public void createHour() throws Exception {
        // test all 3 ways to create an hour
        DateTimePeriod period0 = DateTimePeriod.createHour(2009, 2, 1, 9, DateTimeZone.UTC);
        DateTimePeriod period1 = DateTimePeriod.createHour(new DateTime(2009,2,1,9,0,0,0,DateTimeZone.UTC));
        DateTimePeriod period2 = DateTimePeriod.create(DateTimeDuration.HOUR, new DateTime(2009,2,1,9,0,0,0,DateTimeZone.UTC));

        DateTime correctStart = new DateTime(2009,2,1,9,0,0,0,DateTimeZone.UTC);
        DateTime correctEnd = new DateTime(2009,2,1,10,0,0,0,DateTimeZone.UTC);
        Assert.assertEquals(correctStart, period0.getStart());
        Assert.assertEquals(correctEnd, period0.getEnd());
        Assert.assertEquals(DateTimeDuration.HOUR, period0.getDuration());
        Assert.assertEquals("2009-02-01-09", period0.getKey());
        Assert.assertEquals("February 1, 2009 09:00", period0.getLongName());
        Assert.assertEquals("09:00", period0.getShortName());
        Assert.assertEquals("09", period0.getIndexName());

        Assert.assertEquals(correctStart, period1.getStart());
        Assert.assertEquals(correctEnd, period1.getEnd());
        Assert.assertEquals(DateTimeDuration.HOUR, period1.getDuration());
        Assert.assertEquals("2009-02-01-09", period1.getKey());
        Assert.assertEquals("February 1, 2009 09:00", period0.getLongName());
        Assert.assertEquals("09:00", period0.getShortName());
        Assert.assertEquals("09", period0.getIndexName());

        Assert.assertEquals(correctStart, period2.getStart());
        Assert.assertEquals(correctEnd, period2.getEnd());
        Assert.assertEquals(DateTimeDuration.HOUR, period2.getDuration());
        Assert.assertEquals("2009-02-01-09", period2.getKey());
        Assert.assertEquals("February 1, 2009 09:00", period0.getLongName());
        Assert.assertEquals("09:00", period0.getShortName());
        Assert.assertEquals("09", period0.getIndexName());
    }

    @Test
    public void createFiveMinutes() throws Exception {
        // test all 3 ways to create five minutes
        DateTimePeriod period0 = DateTimePeriod.createFiveMinutes(2009, 2, 1, 9, 55, DateTimeZone.UTC);
        DateTimePeriod period1 = DateTimePeriod.createFiveMinutes(new DateTime(2009,2,1,9,55,0,0,DateTimeZone.UTC));
        DateTimePeriod period2 = DateTimePeriod.create(DateTimeDuration.FIVE_MINUTES, new DateTime(2009,2,1,9,55,0,0,DateTimeZone.UTC));

        DateTime correctStart = new DateTime(2009,2,1,9,55,0,0,DateTimeZone.UTC);
        DateTime correctEnd = new DateTime(2009,2,1,10,0,0,0,DateTimeZone.UTC);
        Assert.assertEquals(correctStart, period0.getStart());
        Assert.assertEquals(correctEnd, period0.getEnd());
        Assert.assertEquals(DateTimeDuration.FIVE_MINUTES, period0.getDuration());
        Assert.assertEquals("2009-02-01-09-55", period0.getKey());
        Assert.assertEquals("February 1, 2009 09:55", period0.getLongName());
        Assert.assertEquals("09:55", period0.getShortName());
        Assert.assertEquals("55", period0.getIndexName());

        Assert.assertEquals(correctStart, period1.getStart());
        Assert.assertEquals(correctEnd, period1.getEnd());
        Assert.assertEquals(DateTimeDuration.FIVE_MINUTES, period1.getDuration());
        Assert.assertEquals("2009-02-01-09-55", period1.getKey());
        Assert.assertEquals("February 1, 2009 09:55", period0.getLongName());
        Assert.assertEquals("09:55", period0.getShortName());
        Assert.assertEquals("55", period0.getIndexName());

        Assert.assertEquals(correctStart, period2.getStart());
        Assert.assertEquals(correctEnd, period2.getEnd());
        Assert.assertEquals(DateTimeDuration.FIVE_MINUTES, period2.getDuration());
        Assert.assertEquals("2009-02-01-09-55", period2.getKey());
        Assert.assertEquals("February 1, 2009 09:55", period0.getLongName());
        Assert.assertEquals("09:55", period0.getShortName());
        Assert.assertEquals("55", period0.getIndexName());
    }

    @Test
    public void toYears() throws Exception {
        // 1 year
        DateTimePeriod period0 = DateTimePeriod.createYear(2009, DateTimeZone.UTC);
        List<DateTimePeriod> periods = period0.toYears();
        Assert.assertArrayEquals(new DateTimePeriod[] {
                DateTimePeriod.createYear(2009, DateTimeZone.UTC)
            }, periods.toArray(new DateTimePeriod[0]));

        // multiple years (a little more than 1 more year)
        period0 = new DateTimeYear(new DateTime(2009,1,1,0,0,0,0,DateTimeZone.UTC), new DateTime(2011,2,1,0,0,0,0,DateTimeZone.UTC));
        periods = period0.toPeriods(DateTimeDuration.YEAR);
        Assert.assertArrayEquals(new DateTimePeriod[] {
                DateTimePeriod.createYear(2009, DateTimeZone.UTC),
                DateTimePeriod.createYear(2010, DateTimeZone.UTC)
            }, periods.toArray(new DateTimePeriod[0]));

        // no years (partial year such as a month)
        period0 = DateTimePeriod.createMonth(2009, 1, DateTimeZone.UTC);
        periods = period0.toYears();
        Assert.assertArrayEquals(new DateTimePeriod[] {}, periods.toArray(new DateTimePeriod[0]));
    }

    @Test
    public void toMonths() throws Exception {
        // 1 month
        DateTimePeriod period0 = DateTimePeriod.createMonth(2009, 1, DateTimeZone.UTC);
        List<DateTimePeriod> periods = period0.toMonths();
        Assert.assertArrayEquals(new DateTimePeriod[] {
                DateTimePeriod.createMonth(2009, 1, DateTimeZone.UTC)
            }, periods.toArray(new DateTimePeriod[0]));

        // multiple months (a little more than 1 more month)
        period0 = new DateTimeMonth(new DateTime(2009,1,1,0,0,0,0,DateTimeZone.UTC), new DateTime(2009,3,1,1,0,0,0,DateTimeZone.UTC));
        periods = period0.toPeriods(DateTimeDuration.MONTH);
        Assert.assertArrayEquals(new DateTimePeriod[] {
                DateTimePeriod.createMonth(2009, 1, DateTimeZone.UTC),
                DateTimePeriod.createMonth(2009, 2, DateTimeZone.UTC)
            }, periods.toArray(new DateTimePeriod[0]));

        // no months (partial month such as a day)
        period0 = DateTimePeriod.createDay(2009, 1, 1, DateTimeZone.UTC);
        periods = period0.toMonths();
        Assert.assertArrayEquals(new DateTimePeriod[] {}, periods.toArray(new DateTimePeriod[0]));
    }

    @Test
    public void toDays() throws Exception {
        // 1 day
        DateTimePeriod period0 = DateTimePeriod.createDay(2009, 1, 1, DateTimeZone.UTC);
        List<DateTimePeriod> periods = period0.toDays();
        Assert.assertArrayEquals(new DateTimePeriod[] {
                DateTimePeriod.createDay(2009, 1, 1, DateTimeZone.UTC)
            }, periods.toArray(new DateTimePeriod[0]));

        // multiple days (a little more than a day)
        period0 = new DateTimeDay(new DateTime(2009,1,1,0,0,0,0,DateTimeZone.UTC), new DateTime(2009,1,3,1,0,0,0,DateTimeZone.UTC));
        periods = period0.toPeriods(DateTimeDuration.DAY);
        Assert.assertArrayEquals(new DateTimePeriod[] {
                DateTimePeriod.createDay(2009, 1, 1, DateTimeZone.UTC),
                DateTimePeriod.createDay(2009, 1, 2, DateTimeZone.UTC)
            }, periods.toArray(new DateTimePeriod[0]));

        // no days (partial day such as a hour)
        period0 = DateTimePeriod.createHour(2009, 1, 1, 0, DateTimeZone.UTC);
        periods = period0.toDays();
        Assert.assertArrayEquals(new DateTimePeriod[] {}, periods.toArray(new DateTimePeriod[0]));
    }

    @Test
    public void toHours() throws Exception {
        // 1 hour
        DateTimePeriod period0 = DateTimePeriod.createHour(2009, 1, 1, 0, DateTimeZone.UTC);
        List<DateTimePeriod> periods = period0.toHours();
        Assert.assertArrayEquals(new DateTimePeriod[] {
                DateTimePeriod.createHour(2009, 1, 1, 0, DateTimeZone.UTC)
            }, periods.toArray(new DateTimePeriod[0]));

        // multiple hours (a little more than an hour)
        period0 = new DateTimeHour(new DateTime(2009,1,1,0,0,0,0,DateTimeZone.UTC), new DateTime(2009,1,1,2,0,0,0,DateTimeZone.UTC));
        periods = period0.toPeriods(DateTimeDuration.HOUR);
        Assert.assertArrayEquals(new DateTimePeriod[] {
                DateTimePeriod.createHour(2009, 1, 1, 0, DateTimeZone.UTC),
                DateTimePeriod.createHour(2009, 1, 1, 1, DateTimeZone.UTC)
            }, periods.toArray(new DateTimePeriod[0]));

        // no hours (partial hour such as 5 minutes)
        period0 = DateTimePeriod.createFiveMinutes(2009, 1, 1, 0, 0, DateTimeZone.UTC);
        periods = period0.toHours();
        Assert.assertArrayEquals(new DateTimePeriod[] {}, periods.toArray(new DateTimePeriod[0]));
    }

    @Test
    public void toFiveMinutes() throws Exception {
        // 1 five minute block
        DateTimePeriod period0 = DateTimePeriod.createFiveMinutes(2009, 1, 1, 0, 0, DateTimeZone.UTC);
        List<DateTimePeriod> periods = period0.toFiveMinutes();
        Assert.assertArrayEquals(new DateTimePeriod[] {
                DateTimePeriod.createFiveMinutes(2009, 1, 1, 0, 0, DateTimeZone.UTC)
            }, periods.toArray(new DateTimePeriod[0]));

        // multiple 5 minutes span (a little more than 5 mins)
        period0 = new DateTimeFiveMinutes(new DateTime(2009,1,1,0,0,0,0,DateTimeZone.UTC), new DateTime(2009,1,1,0,10,0,0,DateTimeZone.UTC));
        periods = period0.toPeriods(DateTimeDuration.FIVE_MINUTES);
        Assert.assertArrayEquals(new DateTimePeriod[] {
                DateTimePeriod.createFiveMinutes(2009, 1, 1, 0, 0, DateTimeZone.UTC),
                DateTimePeriod.createFiveMinutes(2009, 1, 1, 1, 5, DateTimeZone.UTC)
            }, periods.toArray(new DateTimePeriod[0]));

        // no five minutes (partial 5 minutes such as 1 minute)
        period0 = new DateTimeFiveMinutes(new DateTime(2009,1,1,0,0,0,0,DateTimeZone.UTC), new DateTime(2009,1,1,0,4,59,999,DateTimeZone.UTC));
        periods = period0.toFiveMinutes();
        Assert.assertArrayEquals(new DateTimePeriod[] {}, periods.toArray(new DateTimePeriod[0]));
    }

}
