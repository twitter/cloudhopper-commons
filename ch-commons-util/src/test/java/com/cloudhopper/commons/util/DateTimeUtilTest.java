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

// third party imports
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DateTimeUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtilTest.class);

    @Test
    public void parseEmbedded() throws Exception {

        // parse just dates
        DateTime dt0 = DateTimeUtil.parseEmbedded("app-2009-06-24.log.gz", "yyyy-MM-dd", DateTimeZone.UTC);
        logger.debug("dt = " + dt0);
        Assert.assertEquals(new DateTime(2009,6,24,0,0,0,0,DateTimeZone.UTC), dt0);

        // parse a date and time
        dt0 = DateTimeUtil.parseEmbedded("app-2009-06-24-051112.log.gz", "yyyy-MM-dd-hhmmss", DateTimeZone.UTC);
        logger.debug("dt = " + dt0);
        Assert.assertEquals(new DateTime(2009,6,24,5,11,12,0,DateTimeZone.UTC), dt0);

        // parse different date format
        dt0 = DateTimeUtil.parseEmbedded("app-20090624-051112.log.gz", "yyyyMMdd-hhmmss", DateTimeZone.UTC);
        logger.debug("dt = " + dt0);
        Assert.assertEquals(new DateTime(2009,6,24,5,11,12,0,DateTimeZone.UTC), dt0);

        // parse just year and month
        dt0 = DateTimeUtil.parseEmbedded("app-200906.log.gz", "yyyyMM", DateTimeZone.UTC);
        logger.debug("dt = " + dt0);
        Assert.assertEquals(new DateTime(2009,6,1,0,0,0,0,DateTimeZone.UTC), dt0);

        try {
            // filename is missing a day
            dt0 = DateTimeUtil.parseEmbedded("app-200906.log.gz", "yyyyMMdd", DateTimeZone.UTC);
            Assert.fail("parse should have failed");
        } catch (IllegalArgumentException e) {
            // correct behavior
        }

        try {
            // june only has 30 days
            dt0 = DateTimeUtil.parseEmbedded("app-20090631.log.gz", "yyyyMMdd", DateTimeZone.UTC);
            Assert.fail("parse should have failed");
        } catch (IllegalArgumentException e) {
            // correct behavior
        }

        try {
            // tt isn't a valid pattern
            dt0 = DateTimeUtil.parseEmbedded("app-20090631.log.gz", "yyyyMMtt", DateTimeZone.UTC);
            Assert.fail("parse should have failed");
        } catch (IllegalArgumentException e) {
            // correct behavior
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseEmbeddedThrowsException0() throws Exception {
        DateTimeUtil.parseEmbedded("app.2008-05-0.log");
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseEmbeddedThrowsException1() throws Exception {
        DateTimeUtil.parseEmbedded("app.2008-0-01.log");
    }

    @Test(expected=IllegalArgumentException.class)
    public void parseEmbeddedThrowsException2() throws Exception {
        DateTimeUtil.parseEmbedded("app.208-05-01.log");
    }

    /**
    @Test
    public void toMidnightUTCDateTime() throws Exception {
        // create a DateTime in the pacific timezone for June 24, 2009 at 11 PM
        DateTime dt = new DateTime(2009,6,24,23,30,30,0,DateTimeZone.forID("America/Los_Angeles"));
        logger.info("Local DateTime: " + dt);

        // just for making sure we're creating something interesting, let's
        // just convert this to UTC without using our utility function
        DateTime utcdt = dt.toDateTime(DateTimeZone.UTC);
        logger.info("DateTime -> UTC: " + utcdt);

        // convert this to be in the UTC timezone -- reset to midnight
        DateTime newdt = DateTimeUtil.toYearMonthDayUTC(dt);
        logger.debug("DateTime -> UTC (but with util): " + newdt);

        Assert.assertEquals(2009, newdt.getYear());
        Assert.assertEquals(6, newdt.getMonthOfYear());
        Assert.assertEquals(24, newdt.getDayOfMonth());
        Assert.assertEquals(0, newdt.getHourOfDay());
        Assert.assertEquals(0, newdt.getMinuteOfHour());
        Assert.assertEquals(0, newdt.getSecondOfMinute());
        Assert.assertEquals(0, newdt.getMillisOfSecond());
    }
     */

    @Test
    public void floorToYear() throws Exception {
        // create a reference datetime
        DateTime dt0 = new DateTime(2009,6,24,23,30,30,789,DateTimeZone.forID("America/Los_Angeles"));

        //
        // floor to nearest year
        //
        DateTime dt1 = DateTimeUtil.floorToYear(dt0);

        Assert.assertEquals(2009, dt1.getYear());
        Assert.assertEquals(1, dt1.getMonthOfYear());
        Assert.assertEquals(1, dt1.getDayOfMonth());
        Assert.assertEquals(0, dt1.getHourOfDay());
        Assert.assertEquals(0, dt1.getMinuteOfHour());
        Assert.assertEquals(0, dt1.getSecondOfMinute());
        Assert.assertEquals(0, dt1.getMillisOfSecond());
        Assert.assertEquals(DateTimeZone.forID("America/Los_Angeles"), dt1.getZone());

        //
        // floor null
        //
        DateTime dt2 = DateTimeUtil.floorToYear(null);
        Assert.assertNull(dt2);
    }

    @Test
    public void floorToMonth() throws Exception {
        // create a reference datetime
        DateTime dt0 = new DateTime(2009,6,24,23,30,30,789,DateTimeZone.forID("America/Los_Angeles"));

        //
        // floor to nearest month
        //
        DateTime dt1 = DateTimeUtil.floorToMonth(dt0);

        Assert.assertEquals(2009, dt1.getYear());
        Assert.assertEquals(6, dt1.getMonthOfYear());
        Assert.assertEquals(1, dt1.getDayOfMonth());
        Assert.assertEquals(0, dt1.getHourOfDay());
        Assert.assertEquals(0, dt1.getMinuteOfHour());
        Assert.assertEquals(0, dt1.getSecondOfMinute());
        Assert.assertEquals(0, dt1.getMillisOfSecond());
        Assert.assertEquals(DateTimeZone.forID("America/Los_Angeles"), dt1.getZone());

        //
        // floor null
        //
        DateTime dt2 = DateTimeUtil.floorToMonth(null);
        Assert.assertNull(dt2);
    }

    @Test
    public void floorToDay() throws Exception {
        // create a reference datetime
        DateTime dt0 = new DateTime(2009,6,24,23,30,30,789,DateTimeZone.forID("America/Los_Angeles"));

        //
        // floor to nearest day
        //
        DateTime dt1 = DateTimeUtil.floorToDay(dt0);

        Assert.assertEquals(2009, dt1.getYear());
        Assert.assertEquals(6, dt1.getMonthOfYear());
        Assert.assertEquals(24, dt1.getDayOfMonth());
        Assert.assertEquals(0, dt1.getHourOfDay());
        Assert.assertEquals(0, dt1.getMinuteOfHour());
        Assert.assertEquals(0, dt1.getSecondOfMinute());
        Assert.assertEquals(0, dt1.getMillisOfSecond());
        Assert.assertEquals(DateTimeZone.forID("America/Los_Angeles"), dt1.getZone());

        //
        // floor null
        //
        DateTime dt2 = DateTimeUtil.floorToDay(null);
        Assert.assertNull(dt2);
    }

    @Test
    public void floorToHour() throws Exception {
        // create a reference datetime
        DateTime dt0 = new DateTime(2009,6,24,23,30,30,789,DateTimeZone.forID("America/Los_Angeles"));

        //
        // floor to nearest hour
        //
        DateTime dt1 = DateTimeUtil.floorToHour(dt0);

        Assert.assertEquals(2009, dt1.getYear());
        Assert.assertEquals(6, dt1.getMonthOfYear());
        Assert.assertEquals(24, dt1.getDayOfMonth());
        Assert.assertEquals(23, dt1.getHourOfDay());
        Assert.assertEquals(0, dt1.getMinuteOfHour());
        Assert.assertEquals(0, dt1.getSecondOfMinute());
        Assert.assertEquals(0, dt1.getMillisOfSecond());
        Assert.assertEquals(DateTimeZone.forID("America/Los_Angeles"), dt1.getZone());

        //
        // floor null
        //
        DateTime dt2 = DateTimeUtil.floorToHour(null);
        Assert.assertNull(dt2);
    }
    
    @Test
    public void floorToHalfHour() throws Exception {
        // create a reference datetime
        DateTime dt0 = new DateTime(2009,6,24,23,29,30,789,DateTimeZone.UTC);

        Assert.assertNull(DateTimeUtil.floorToHalfHour(null));
        
        // floor to nearest half hour
        DateTime dt1 = DateTimeUtil.floorToHalfHour(dt0);

        Assert.assertEquals(2009, dt1.getYear());
        Assert.assertEquals(6, dt1.getMonthOfYear());
        Assert.assertEquals(24, dt1.getDayOfMonth());
        Assert.assertEquals(23, dt1.getHourOfDay());
        Assert.assertEquals(0, dt1.getMinuteOfHour());
        Assert.assertEquals(0, dt1.getSecondOfMinute());
        Assert.assertEquals(0, dt1.getMillisOfSecond());
        Assert.assertEquals(DateTimeZone.UTC, dt1.getZone());

        DateTime dt3 = DateTimeUtil.floorToHalfHour(new DateTime(2009,6,24,10,0,0,0));
        Assert.assertEquals(new DateTime(2009,6,24,10,0,0,0), dt3);

        dt3 = DateTimeUtil.floorToHalfHour(new DateTime(2009,6,24,10,1,23,456));
        Assert.assertEquals(new DateTime(2009,6,24,10,0,0,0), dt3);

        dt3 = DateTimeUtil.floorToHalfHour(new DateTime(2009,6,24,10,30,12,56));
        Assert.assertEquals(new DateTime(2009,6,24,10,30,0,0), dt3);

        dt3 = DateTimeUtil.floorToHalfHour(new DateTime(2009,6,24,10,59,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,30,0,0), dt3);

        dt3 = DateTimeUtil.floorToHalfHour(new DateTime(2009,6,24,10,55,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,30,0,0), dt3);

        dt3 = DateTimeUtil.floorToHalfHour(new DateTime(2009,6,24,10,46,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,30,0,0), dt3);
    }
    
    @Test
    public void floorToQuarterHour() throws Exception {
        // create a reference datetime
        DateTime dt0 = new DateTime(2009,6,24,23,29,30,789,DateTimeZone.UTC);

        Assert.assertNull(DateTimeUtil.floorToQuarterHour(null));
        
        // floor to nearest half hour
        DateTime dt1 = DateTimeUtil.floorToQuarterHour(dt0);

        Assert.assertEquals(2009, dt1.getYear());
        Assert.assertEquals(6, dt1.getMonthOfYear());
        Assert.assertEquals(24, dt1.getDayOfMonth());
        Assert.assertEquals(23, dt1.getHourOfDay());
        Assert.assertEquals(15, dt1.getMinuteOfHour());
        Assert.assertEquals(0, dt1.getSecondOfMinute());
        Assert.assertEquals(0, dt1.getMillisOfSecond());
        Assert.assertEquals(DateTimeZone.UTC, dt1.getZone());

        DateTime dt3 = DateTimeUtil.floorToQuarterHour(new DateTime(2009,6,24,10,0,0,0));
        Assert.assertEquals(new DateTime(2009,6,24,10,0,0,0), dt3);

        dt3 = DateTimeUtil.floorToQuarterHour(new DateTime(2009,6,24,10,1,23,456));
        Assert.assertEquals(new DateTime(2009,6,24,10,0,0,0), dt3);

        dt3 = DateTimeUtil.floorToQuarterHour(new DateTime(2009,6,24,10,30,12,56));
        Assert.assertEquals(new DateTime(2009,6,24,10,30,0,0), dt3);

        dt3 = DateTimeUtil.floorToQuarterHour(new DateTime(2009,6,24,10,59,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,45,0,0), dt3);

        dt3 = DateTimeUtil.floorToQuarterHour(new DateTime(2009,6,24,10,55,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,45,0,0), dt3);

        dt3 = DateTimeUtil.floorToQuarterHour(new DateTime(2009,6,24,10,46,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,45,0,0), dt3);
    }
    
    @Test
    public void floorToTenMinutes() throws Exception {
        // create a reference datetime
        DateTime dt0 = new DateTime(2009,6,24,23,29,30,789,DateTimeZone.UTC);

        Assert.assertNull(DateTimeUtil.floorToTenMinutes(null));
        
        // floor to nearest half hour
        DateTime dt1 = DateTimeUtil.floorToTenMinutes(dt0);

        Assert.assertEquals(2009, dt1.getYear());
        Assert.assertEquals(6, dt1.getMonthOfYear());
        Assert.assertEquals(24, dt1.getDayOfMonth());
        Assert.assertEquals(23, dt1.getHourOfDay());
        Assert.assertEquals(20, dt1.getMinuteOfHour());
        Assert.assertEquals(0, dt1.getSecondOfMinute());
        Assert.assertEquals(0, dt1.getMillisOfSecond());
        Assert.assertEquals(DateTimeZone.UTC, dt1.getZone());

        DateTime dt3 = DateTimeUtil.floorToTenMinutes(new DateTime(2009,6,24,10,0,0,0));
        Assert.assertEquals(new DateTime(2009,6,24,10,0,0,0), dt3);

        dt3 = DateTimeUtil.floorToTenMinutes(new DateTime(2009,6,24,10,1,23,456));
        Assert.assertEquals(new DateTime(2009,6,24,10,0,0,0), dt3);

        dt3 = DateTimeUtil.floorToTenMinutes(new DateTime(2009,6,24,10,30,12,56));
        Assert.assertEquals(new DateTime(2009,6,24,10,30,0,0), dt3);

        dt3 = DateTimeUtil.floorToTenMinutes(new DateTime(2009,6,24,10,59,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,50,0,0), dt3);

        dt3 = DateTimeUtil.floorToTenMinutes(new DateTime(2009,6,24,10,55,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,50,0,0), dt3);

        dt3 = DateTimeUtil.floorToTenMinutes(new DateTime(2009,6,24,10,46,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,40,0,0), dt3);
    }

    @Test
    public void floorToFiveMinutes() throws Exception {
        // create a reference datetime
        DateTime dt0 = new DateTime(2009,6,24,23,30,30,789,DateTimeZone.forID("America/Los_Angeles"));

        //
        // floor to nearest five minutes
        //
        DateTime dt1 = DateTimeUtil.floorToFiveMinutes(dt0);

        Assert.assertEquals(2009, dt1.getYear());
        Assert.assertEquals(6, dt1.getMonthOfYear());
        Assert.assertEquals(24, dt1.getDayOfMonth());
        Assert.assertEquals(23, dt1.getHourOfDay());
        Assert.assertEquals(30, dt1.getMinuteOfHour());
        Assert.assertEquals(0, dt1.getSecondOfMinute());
        Assert.assertEquals(0, dt1.getMillisOfSecond());
        Assert.assertEquals(DateTimeZone.forID("America/Los_Angeles"), dt1.getZone());

        //
        // floor null
        //
        DateTime dt2 = DateTimeUtil.floorToFiveMinutes(null);
        Assert.assertNull(dt2);

        //
        // various tests since rounding five minutes is more complicated
        //
        DateTime dt3 = DateTimeUtil.floorToFiveMinutes(new DateTime(2009,6,24,10,0,0,0));
        Assert.assertEquals(new DateTime(2009,6,24,10,0,0,0), dt3);

        dt3 = DateTimeUtil.floorToFiveMinutes(new DateTime(2009,6,24,10,1,23,456));
        Assert.assertEquals(new DateTime(2009,6,24,10,0,0,0), dt3);

        dt3 = DateTimeUtil.floorToFiveMinutes(new DateTime(2009,6,24,10,2,12,56));
        Assert.assertEquals(new DateTime(2009,6,24,10,0,0,0), dt3);

        dt3 = DateTimeUtil.floorToFiveMinutes(new DateTime(2009,6,24,10,59,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,55,0,0), dt3);

        dt3 = DateTimeUtil.floorToFiveMinutes(new DateTime(2009,6,24,10,55,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,55,0,0), dt3);

        dt3 = DateTimeUtil.floorToFiveMinutes(new DateTime(2009,6,24,10,46,59,999));
        Assert.assertEquals(new DateTime(2009,6,24,10,45,0,0), dt3);
    }

    @Test
    public void floorToMinute() throws Exception {
        // create a reference datetime
        DateTime dt0 = new DateTime(2009,6,24,23,30,30,789,DateTimeZone.forID("America/Los_Angeles"));

        //
        // floor to nearest minute
        //
        DateTime dt1 = DateTimeUtil.floorToMinute(dt0);

        Assert.assertEquals(2009, dt1.getYear());
        Assert.assertEquals(6, dt1.getMonthOfYear());
        Assert.assertEquals(24, dt1.getDayOfMonth());
        Assert.assertEquals(23, dt1.getHourOfDay());
        Assert.assertEquals(30, dt1.getMinuteOfHour());
        Assert.assertEquals(0, dt1.getSecondOfMinute());
        Assert.assertEquals(0, dt1.getMillisOfSecond());
        Assert.assertEquals(DateTimeZone.forID("America/Los_Angeles"), dt1.getZone());

        //
        // floor null
        //
        DateTime dt2 = DateTimeUtil.floorToMinute(null);
        Assert.assertNull(dt2);
    }

    @Test
    public void floorToSecond() throws Exception {
        // create a reference datetime
        DateTime dt0 = new DateTime(2009,6,24,23,30,31,789,DateTimeZone.forID("America/Los_Angeles"));

        //
        // floor to nearest second
        //
        DateTime dt1 = DateTimeUtil.floorToSecond(dt0);

        Assert.assertEquals(2009, dt1.getYear());
        Assert.assertEquals(6, dt1.getMonthOfYear());
        Assert.assertEquals(24, dt1.getDayOfMonth());
        Assert.assertEquals(23, dt1.getHourOfDay());
        Assert.assertEquals(30, dt1.getMinuteOfHour());
        Assert.assertEquals(31, dt1.getSecondOfMinute());
        Assert.assertEquals(0, dt1.getMillisOfSecond());
        Assert.assertEquals(DateTimeZone.forID("America/Los_Angeles"), dt1.getZone());

        //
        // floor null
        //
        DateTime dt2 = DateTimeUtil.floorToSecond(null);
        Assert.assertNull(dt2);
    }
}
