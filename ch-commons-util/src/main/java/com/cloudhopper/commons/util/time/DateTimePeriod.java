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
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Represents two DateTime values that represent the "start" and "end" of a
 * period of time.  Useful for selecting data from a database for records
 * between two DateTime values.  The "start" DateTime is inclusive for the
 * period, however the "end" DateTime is exclusive for the period.
 *
 * For example, a period of a month such as "January 2009" would have a start
 * DateTime of "2009-01-01 00:00:00.000" and an end DateTime of
 * "2009-02-01 00:00:00.000".  Since the start date is inclusive and the end
 * date is exclusive, the resulting SELECT statement would consist of:
 *
 *   <code>"DateCol" >= period.getStart() AND "DateCol" < period.getEnd() </code> .
 *
 * NOTE: This cannot be used with a BETWEEN statement since the end date should
 * not actually be included.  This is more precise since databases may have
 * different levels of precision such as no milliseconds, or perhaps nano
 * precision.  Also, this representation makes the internal calculations much
 * easier since the end date would always be start.plusMonths(1).
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public abstract class DateTimePeriod {

    private final DateTime start;
    private final DateTime end;
    private final DateTimeDuration duration;
    // unique key representing this period such as 2009-01 or 2009-06-24 or 2009-06-24-01
    private final String key;
    // long name of period such as "January 2009" or "January 1, 2009" or "January 1, 2009 13:00"
    private final String longName;
    // short name of period such as "2009" or "2009-01" or "2009-06-24" or "13:00" or "13:15"
    private final String shortName;
    // index name of period representing the most granular duration this period
    // supports such as "2009" for a year or "01" for a month or "24" for a day or "13" or "15"
    private final String indexName;

    protected DateTimePeriod(DateTime start, DateTime end, DateTimeDuration duration, String keyPattern,
            String longNamePattern, String shortNamePattern, String indexNamePattern) {
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.key = DateTimeFormat.forPattern(keyPattern).print(start);
        this.longName = DateTimeFormat.forPattern(longNamePattern).print(start);
        this.shortName = DateTimeFormat.forPattern(shortNamePattern).print(start);
        this.indexName = DateTimeFormat.forPattern(indexNamePattern).print(start);
    }

    /**
     * Gets the inclusive starting DateTime for this period.
     * @return The inclusive starting DateTime for this period.
     */
    public DateTime getStart() {
        return this.start;
    }

    /**
     * Gets the exclusive ending DateTime for this period.
     * @return The exclusive ending DateTime for this period.
     */
    public DateTime getEnd() {
        return this.end;
    }

    /**
     * Gets the duration this period represents such as a Month, Year, etc.
     * @return The duration for this period
     */
    public DateTimeDuration getDuration() {
        return this.duration;
    }

    /**
     * Gets a key that is unique to this period.  This key is unique and could
     * be used in a HashTable, "select" box on a webpage, etc.  A period can
     * be recreated back from a key.  For example:
     * <ul>
     *      <li>The year 2009 would have a key of "2009"
     *      <li>The month January 2009 would have key of "2009-01"
     *      <li>The day January 24, 2009 would have a key of "2009-01-24"
     *      <li>The hour January 24, 2009 at 7 AM would have a key of "2009-01-24-07"
     *      <li>The hour January 24, 2009 at 2 PM would have a key of "2009-01-24-14"
     *      <li>The five minutes at January 24, 2009 at 2:15 PM would have a key of "2009-01-24-14-15"
     * </ul>
     * @return A key to this period such as "2009-01" to represent a period for
     *        January 2009.
     */
    public String getKey() {
        return this.key;
    }


    /**
     * Gets a long name for this period appropriate for display as the title
     * for a report such as "January 2009". For example:
     * <ul>
     *      <li>The year 2009 would have a long name of "2009"
     *      <li>The month January 2009 would have a long name of "January 2009"
     *      <li>The day January 24, 2009 would have a long name of "January 24, 2009"
     *      <li>The hour January 24, 2009 at 7 AM would have a long name of "January 24, 2009 07:00"
     *      <li>The hour January 24, 2009 at 2 PM would have a long name of "January 24, 2009 14:00"
     *      <li>The five minutes at January 24, 2009 at 2:15 PM would have a long name of "January 24, 2009 14:15"
     * </ul>
     * @return The long name of the period such as "January 2009"
     * @see #getShortName()
     * @see #getIndexName() 
     */
    public String getLongName() {
        return this.longName;
    }

    /**
     * Gets a short name for this period appropriate for display within a table
     * of data to identify a row.  A short name will be no longer than 10 characters.
     * For example:
     * <ul>
     *      <li>The year 2009 would have a short name of "2009"
     *      <li>The month January 2009 would have a short name of "2009-01"
     *      <li>The day January 24, 2009 would have a short name of "2009-01-24"
     *      <li>The hour January 24, 2009 at 7 AM would have a short name of "07:00"
     *      <li>The hour January 24, 2009 at 2 PM would have a short name of "14:00"
     *      <li>The five minutes at January 24, 2009 at 2:15 PM would have a short name of "14:15"
     * </ul>
     * @return The short name of the period such as "2009-01-24"
     * @see #getLongName()
     * @see #getIndexName()
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Gets an index name for this period appropriate for display to only identify
     * the particular row by the smallest possible identifer.  An index name will
     * be no longer than 4 characters.
     * For example:
     * <ul>
     *      <li>The year 2009 would have an index name of "2009"
     *      <li>The month January 2009 would have an index name of "01"
     *      <li>The day January 24, 2009 would have an index name of "24"
     *      <li>The hour January 24, 2009 at 7 AM would have an index name of "07"
     *      <li>The hour January 24, 2009 at 2 PM would have an index name of "14"
     *      <li>The five minutes at January 24, 2009 at 2:15 PM would have an index name of "15"
     * </ul>
     * @return The index name of the period such as "01" for January
     * @see #getLongName()
     * @see #getShortName()
     */
    public String getIndexName() {
        return this.indexName;
    }
    
    /**
     * Gets the next period based on the duration. For example, if this period
     * represents a Month, this will return a period for the next Month.
     * @return The next period
     */
    public abstract DateTimePeriod getNext();

    /**
     * Gets the previous period based on the duration. For example, if this period
     * represents a Month, this will return a period for the previous Month.
     * @return The previous period
     */
    public abstract DateTimePeriod getPrevious();

    /**
     * Gets the default "Group By" enumeration based on the duration for this
     * period.  The default "Group By" is usually the period "slice" one level
     * below this one.  For example, if this period is a month, this would
     * return a grouping by year-month-day.  If this period is a year,
     * this would return a grouping by year-quarter.
     * @return A default group by determined by this period's duration
     */
    public abstract DateTimeDuration getDefaultSubDuration();


    /**
     * Converts this period to a list of periods for the given duration.  This is
     * an alternative way to create a list of periods instead of calling each
     * specific duration method such as toHours() or toDays(). Partial periods will not be
     * included.
     * @return A list of periods contained within this period for the specified duration
     * @see #toFiveMinutes()
     * @see #toHours()
     * @see #toDays()
     * @see #toMonths()
     * @see #toYears() 
     */
    public List<DateTimePeriod> toPeriods(DateTimeDuration duration) {
        if (duration == DateTimeDuration.YEAR) {
            return toYears();
        } else if (duration == DateTimeDuration.MONTH) {
            return toMonths();
        } else if (duration == DateTimeDuration.DAY) {
            return toDays();
        } else if (duration == DateTimeDuration.HOUR) {
            return toHours();
        } else if (duration == DateTimeDuration.FIVE_MINUTES) {
            return toFiveMinutes();
        } else {
            throw new UnsupportedOperationException("Duration " + duration + " not supported yet");
        }
    }


    /**
     * Converts this period to a list of five minute periods.  Partial five minute periods will not be
     * included.  For example, a period of "January 20, 2009 7 to 8 AM" will return a list
     * of 12 five minute periods - one for each 5 minute block of time 0, 5, 10, 15, etc.
     * On the other hand, a period of "January 20, 2009 at 13:04" would return an empty list since partial
     * five minute periods are not included.
     * @return A list of five minute periods contained within this period
     */
    public List<DateTimePeriod> toFiveMinutes() {
        ArrayList<DateTimePeriod> list = new ArrayList<DateTimePeriod>();

        // default "current" five minutes to start datetime
        DateTime currentStart = getStart();
        // calculate "next" five minutes
        DateTime nextStart = currentStart.plusMinutes(5);
        // continue adding until we've reached the end
        while (nextStart.isBefore(getEnd()) || nextStart.isEqual(getEnd())) {
            // its okay to add the current
            list.add(new DateTimeFiveMinutes(currentStart, nextStart));
            // increment both
            currentStart = nextStart;
            nextStart = currentStart.plusMinutes(5);
        }

        return list;
    }


    /**
     * Converts this period to a list of hour periods.  Partial hours will not be
     * included.  For example, a period of "January 20, 2009" will return a list
     * of 24 hours - one for each hour on January 20, 2009.  On the other hand,
     * a period of "January 20, 2009 13:10" would return an empty list since partial
     * hours are not included.
     * @return A list of hour periods contained within this period
     */
    public List<DateTimePeriod> toHours() {
        ArrayList<DateTimePeriod> list = new ArrayList<DateTimePeriod>();

        // default "current" hour to start datetime
        DateTime currentStart = getStart();
        // calculate "next" hour
        DateTime nextStart = currentStart.plusHours(1);
        // continue adding until we've reached the end
        while (nextStart.isBefore(getEnd()) || nextStart.isEqual(getEnd())) {
            // its okay to add the current
            list.add(new DateTimeHour(currentStart, nextStart));
            // increment both
            currentStart = nextStart;
            nextStart = currentStart.plusHours(1);
        }

        return list;
    }
    

    /**
     * Converts this period to a list of day periods.  Partial days will not be
     * included.  For example, a period of "January 2009" will return a list
     * of 31 days - one for each day in January.  On the other hand, a period
     * of "January 20, 2009 13:00-59" would return an empty list since partial
     * days are not included.
     * @return A list of day periods contained within this period
     */
    public List<DateTimePeriod> toDays() {
        ArrayList<DateTimePeriod> list = new ArrayList<DateTimePeriod>();

        // default "current" day to start datetime
        DateTime currentStart = getStart();
        // calculate "next" day
        DateTime nextStart = currentStart.plusDays(1);
        // continue adding until we've reached the end
        while (nextStart.isBefore(getEnd()) || nextStart.isEqual(getEnd())) {
            // its okay to add the current
            list.add(new DateTimeDay(currentStart, nextStart));
            // increment both
            currentStart = nextStart;
            nextStart = currentStart.plusDays(1);
        }
        
        return list;
    }


    /**
     * Converts this period to a list of month periods.  Partial months will not be
     * included.  For example, a period of "2009" will return a list
     * of 12 months - one for each month in 2009.  On the other hand, a period
     * of "January 20, 2009" would return an empty list since partial
     * months are not included.
     * @return A list of month periods contained within this period
     */
    public List<DateTimePeriod> toMonths() {
        ArrayList<DateTimePeriod> list = new ArrayList<DateTimePeriod>();

        // default "current" month to start datetime
        DateTime currentStart = getStart();
        // calculate "next" month
        DateTime nextStart = currentStart.plusMonths(1);
        // continue adding until we've reached the end
        while (nextStart.isBefore(getEnd()) || nextStart.isEqual(getEnd())) {
            // its okay to add the current
            list.add(new DateTimeMonth(currentStart, nextStart));
            // increment both
            currentStart = nextStart;
            nextStart = currentStart.plusMonths(1);
        }

        return list;
    }

    /**
     * Converts this period to a list of year periods.  Partial years will not be
     * included.  For example, a period of "2009-2010" will return a list
     * of 2 years - one for 2009 and one for 2010.  On the other hand, a period
     * of "January 2009" would return an empty list since partial
     * years are not included.
     * @return A list of year periods contained within this period
     */
    public List<DateTimePeriod> toYears() {
        ArrayList<DateTimePeriod> list = new ArrayList<DateTimePeriod>();

        // default "current" year to start datetime
        DateTime currentStart = getStart();
        // calculate "next" year
        DateTime nextStart = currentStart.plusYears(1);
        // continue adding until we've reached the end
        while (nextStart.isBefore(getEnd()) || nextStart.isEqual(getEnd())) {
            // its okay to add the current
            list.add(new DateTimeYear(currentStart, nextStart));
            // increment both
            currentStart = nextStart;
            nextStart = currentStart.plusYears(1);
        }

        return list;
    }

    /**
     * Compares another DateTimePeriod for equality if the internal start
     * and end DateTimes equal this period.
     * @param object The other DateTimePeriod
     * @return True if the internal start and end DateTimes equal this period.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DateTimePeriod)) {
            throw new ClassCastException("Can only compare with other DateTimePeriod instances");
        }

        DateTimePeriod otherPeriod = (DateTimePeriod)object;

        if (this.start == null && otherPeriod.start != null) {
            return false;
        }

        if (this.end == null && otherPeriod.end != null) {
            return false;
        }

        if (this.start.equals(otherPeriod.start) && this.end.equals(otherPeriod.end)) {
            return true;
        }

        return true;
    }

    /**
     * Hashcode computed based on the internal start and end DateTime hashCode
     * values.
     * @return A hashCode based on the hashCode of the internal start and end
     *      DateTime values.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.start != null ? this.start.hashCode() : 0);
        hash = 59 * hash + (this.end != null ? this.end.hashCode() : 0);
        return hash;
    }

    /**
     * Creates a new period for the specified duration starting from the
     * provided DateTime.
     * @param duration The duration for this period such as Month, Hour, etc.
     * @param start The DateTime to start this period from
     * @return A new period
     */
    static public DateTimePeriod create(DateTimeDuration duration, DateTime start) {
        if (duration == DateTimeDuration.YEAR) {
            return createYear(start);
        } else if (duration == DateTimeDuration.MONTH) {
            return createMonth(start);
        } else if (duration == DateTimeDuration.DAY) {
            return createDay(start);
        } else if (duration == DateTimeDuration.HOUR) {
            return createHour(start);
        } else if (duration == DateTimeDuration.FIVE_MINUTES) {
            return createFiveMinutes(start);
        } else {
            throw new UnsupportedOperationException("Duration " + duration + " not yet supported");
        }
    }


    static public DateTimePeriod createYear(int year, DateTimeZone zone) {
        // year-01-01 00:00:00.000
        DateTime start = new DateTime(year, 1, 1, 0, 0, 0, 0, zone);
        return createYear(start);
    }

    static public DateTimePeriod createYear(DateTime start) {
        DateTime end = start.plusYears(1);
        return new DateTimeYear(start, end);
    }

    static public DateTimePeriod createMonth(int year, int month, DateTimeZone zone) {
        // year-month-01 00:00:00.000
        DateTime start = new DateTime(year, month, 1, 0, 0, 0, 0, zone);
        return createMonth(start);
    }

    static public DateTimePeriod createMonth(DateTime start) {
        DateTime end = start.plusMonths(1);
        return new DateTimeMonth(start, end);
    }

    static public DateTimePeriod createDay(int year, int month, int day, DateTimeZone zone) {
        // year-month-day 00:00:00.000
        DateTime start = new DateTime(year, month, day, 0, 0, 0, 0, zone);
        return createDay(start);
    }

    static public DateTimePeriod createDay(DateTime start) {
        DateTime end = start.plusDays(1);
        return new DateTimeDay(start, end);
    }

    static public DateTimePeriod createHour(int year, int month, int day, int hour, DateTimeZone zone) {
        // year-month-day hour:00:00.000
        DateTime start = new DateTime(year, month, day, hour, 0, 0, 0, zone);
        return createHour(start);
    }

    static public DateTimePeriod createHour(DateTime start) {
        DateTime end = start.plusHours(1);
        return new DateTimeHour(start, end);
    }

    static public DateTimePeriod createFiveMinutes(int year, int month, int day, int hour, int minute, DateTimeZone zone) {
        // year-month-day hour:00:00.000
        DateTime start = new DateTime(year, month, day, hour, minute, 0, 0, zone);
        return createFiveMinutes(start);
    }

    static public DateTimePeriod createFiveMinutes(DateTime start) {
        DateTime end = start.plusMinutes(5);
        return new DateTimeFiveMinutes(start, end);
    }

    
    /**
     * Create a list of DateTimePeriods that represent the last year of
     * YearMonth periods.  For example, if its currently January 2009, this
     * would return periods representing "January 2009, December 2008, ... February 2008"
     * @param zone
     * @return
     */
    static public List<DateTimePeriod> createLastYearMonths(DateTimeZone zone) {
        ArrayList<DateTimePeriod> periods = new ArrayList<DateTimePeriod>();

        // get today's date
        DateTime now = new DateTime(zone);

        // start with today's current month and 11 others (last 12 months)
        for (int i = 0; i < 12; i++) {
            // create a new period
            DateTimePeriod period = createMonth(now.getYear(), now.getMonthOfYear(), zone);
            periods.add(period);
            // subtract 1 month
            now = now.minusMonths(1);
        }

        return periods;
    }
    
}
