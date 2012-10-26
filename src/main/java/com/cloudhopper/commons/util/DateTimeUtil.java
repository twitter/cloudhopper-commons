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

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DateTimeUtil {

    /**
     * Null-safe method of converting a SQL Timestamp into a DateTime that
     * it set specifically to be in UTC.
     * <br>
     * NOTE: The timestamp also should be in UTC.
     * @return A UTC DateTime
     */
    public static DateTime toDateTime(Timestamp value) {
        if (value == null) {
            return null;
        } else {
            return new DateTime(value.getTime(), DateTimeZone.UTC);
        }
    }

    /**
     * Null-safe method of copying a DateTime
     * <br>
     * NOTE: The timestamp also should be in UTC.
     * @return A UTC DateTime
     */
    public static DateTime copy(DateTime value) {
        if (value == null) {
            return null;
        } else {
            return new DateTime(value.getMillis(), DateTimeZone.UTC);
        }
    }

    /**
     * Null-safe method of converting a DateTime to a Timestamp.
     * <br>
     * NOTE: The timestamp also should be in UTC.
     * @return A UTC DateTime
     */
    public static Timestamp toTimestamp(DateTime dt) {
        if (dt == null) {
            return null;
        } else {
            return new Timestamp(dt.getMillis());
        }
    }

    /**
     * Get the current time as a DateTime.
     * 
     * @return A UTC DateTime
     */
    public static DateTime now() {
        return new DateTime(DateTimeZone.UTC);
    }


    /**
     * Parses a string for an embedded date and/or time contained within a
     * string such as "app.2008-05-01.log". This method expects the string
     * to contain a pattern of "yyyy-MM-dd".  All dates will be interpreted to
     * be in the UTC timezone.
     * @param string0 The string to parse
     * @return The parsed DateTime value
     * @throws IllegalArgumentException Thrown if the string did not contain
     *      an embedded date.
     */
    public static DateTime parseEmbedded(String string0) throws IllegalArgumentException {
        return parseEmbedded(string0, "yyyy-MM-dd", DateTimeZone.UTC);
    }



    /**
     * Parses a string for an embedded date and/or time contained within a
     * string such as "app.2008-05-01.log" or "app-20090624-051112.log.gz".
     * This method accepts a variety of date and time patterns that are valid
     * within the Joda DateTime library.  For example, the string "app.2008-05-01.log"
     * would be a pattern of "yyyy-MM-dd" and the string "app-20090624-151112.log.gz"
     * would be a pattern of "yyyy-MM-dd-HHmmss".
     * @param string0 The string to parse
     * @param pattern The DateTime pattern embedded in the string such as
     *      "yyyy-MM-dd".  The pattern must be a valid DateTime Joda pattern.
     * @param zone The timezone the parsed date will be in
     * @return The parsed DateTime value
     * @throws IllegalArgumentException Thrown if the pattern is invalid or if
     *      string did not contain an embedded date.
     */
    public static DateTime parseEmbedded(String string0, String pattern, DateTimeZone zone) throws IllegalArgumentException {
        // compile a date time formatter -- which also will check that pattern
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern).withZone(zone);

        //
        // use the pattern to generate a regular expression now -- we'll do a
        // simple replacement of ascii characters with \\d
        //
        StringBuilder regex = new StringBuilder(pattern.length()*2);
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (Character.isLetter(c)) {
                regex.append("\\d");
            } else {
                regex.append(c);
            }
        }

        // extract the date from the string
        Pattern p = Pattern.compile(regex.toString()); // Compiles regular expression into Pattern.
        Matcher m = p.matcher(string0); // Creates Matcher with subject s and Pattern p.

        if (!m.find()) {
            // if we get here, then no valid grouping was found
            throw new IllegalArgumentException("String '" + string0 + "' did not contain an embedded date [regexPattern='" + regex.toString() + "', datePattern='" + pattern + "']");
        }

        //logger.debug("Matching date: " + m.group());
        // this group represents a string in the format YYYY-MM-DD
        String dateString = m.group();
        // parse the string and return the Date
        return fmt.parseDateTime(dateString);
    }

    /**
     * Null-safe method that returns a new instance of a DateTime object rounded
     * downwards to the nearest year. Note that the nearest valid year is actually
     * the first of that given year (Jan 1). The time zone of the returned DateTime
     * instance will be the same as the argument. Similar to a floor() function
     * on a float.<br>
     * Examples:
     * <ul>
     *      <li>null -> null
     *      <li>"2009-06-24 13:24:51.476 -8:00" -> "2009-01-01 00:00:00.000 -8:00"
     * </ul>
     * @param value The DateTime value to round downward
     * @return Null if the argument is null or a new instance of the DateTime
     *      value rounded downwards to the nearest year.
     */
    public static DateTime floorToYear(DateTime value) {
        if (value == null) {
            return null;
        }
        return new DateTime(value.getYear(), 1, 1, 0, 0, 0, 0, value.getZone());
    }

    /**
     * Null-safe method that returns a new instance of a DateTime object rounded
     * downwards to the nearest month. Note that the nearest valid month is actually
     * the first of that given month (1st day of month). The time zone of the returned DateTime
     * instance will be the same as the argument. Similar to a floor() function
     * on a float.<br>
     * Examples:
     * <ul>
     *      <li>null -> null
     *      <li>"2009-06-24 13:24:51.476 -8:00" -> "2009-06-01 00:00:00.000 -8:00"
     * </ul>
     * @param value The DateTime value to round downward
     * @return Null if the argument is null or a new instance of the DateTime
     *      value rounded downwards to the nearest month.
     */
    public static DateTime floorToMonth(DateTime value) {
        if (value == null) {
            return null;
        }
        return new DateTime(value.getYear(), value.getMonthOfYear(), 1, 0, 0, 0, 0, value.getZone());
    }

    /**
     * Null-safe method that returns a new instance of a DateTime object rounded
     * downwards to the nearest day. The time zone of the returned DateTime
     * instance will be the same as the argument. Similar to a floor() function
     * on a float.<br>
     * Examples:
     * <ul>
     *      <li>null -> null
     *      <li>"2009-06-24 13:24:51.476 -8:00" -> "2009-06-24 00:00:00.000 -8:00"
     * </ul>
     * @param value The DateTime value to round downward
     * @return Null if the argument is null or a new instance of the DateTime
     *      value rounded downwards to the nearest day.
     */
    public static DateTime floorToDay(DateTime value) {
        if (value == null) {
            return null;
        }
        return new DateTime(value.getYear(), value.getMonthOfYear(), value.getDayOfMonth(), 0, 0, 0, 0, value.getZone());
    }

    /**
     * Null-safe method that returns a new instance of a DateTime object rounded
     * downwards to the nearest hour. The time zone of the returned DateTime
     * instance will be the same as the argument. Similar to a floor() function
     * on a float.<br>
     * Examples:
     * <ul>
     *      <li>null -> null
     *      <li>"2009-06-24 13:24:51.476 -8:00" -> "2009-06-24 13:00:00.000 -8:00"
     * </ul>
     * @param value The DateTime value to round downward
     * @return Null if the argument is null or a new instance of the DateTime
     *      value rounded downwards to the nearest hour.
     */
    public static DateTime floorToHour(DateTime value) {
        if (value == null) {
            return null;
        }
        return new DateTime(value.getYear(), value.getMonthOfYear(), value.getDayOfMonth(), value.getHourOfDay(), 0, 0, 0, value.getZone());
    }
    
    /**
     * Null-safe method that returns a new instance of a DateTime object rounded
     * downwards to the nearest half hour (30 minutes). The time zone of the
     * returned DateTime instance will be the same as the argument. Similar to a
     * floor() function on a float.<br>
     * Examples:
     * <ul>
     *      <li>null -> null
     *      <li>"2009-06-24 13:29:51.476 -8:00" -> "2009-06-24 13:00:00.000 -8:00"
     * </ul>
     * @param value The DateTime value to round downward
     * @return Null if the argument is null or a new instance of the DateTime
     *      value rounded downwards to the nearest 30 minutes.
     */
    public static DateTime floorToHalfHour(DateTime value) {
        return floorToMinutePeriod(value, 30);
    }
    
    /**
     * Null-safe method that returns a new instance of a DateTime object rounded
     * downwards to the nearest 15 minutes. The time zone of the returned DateTime
     * instance will be the same as the argument. Similar to a floor() function
     * on a float.<br>
     * Examples:
     * <ul>
     *      <li>null -> null
     *      <li>"2009-06-24 13:29:51.476 -8:00" -> "2009-06-24 13:15:00.000 -8:00"
     * </ul>
     * @param value The DateTime value to round downward
     * @return Null if the argument is null or a new instance of the DateTime
     *      value rounded downwards to the nearest 15 minutes.
     */
    public static DateTime floorToQuarterHour(DateTime value) {
        return floorToMinutePeriod(value, 15);
    }
    
    /**
     * Null-safe method that returns a new instance of a DateTime object rounded
     * downwards to the nearest 10 minutes. The time zone of the returned DateTime
     * instance will be the same as the argument. Similar to a floor() function
     * on a float.<br>
     * Examples:
     * <ul>
     *      <li>null -> null
     *      <li>"2009-06-24 13:29:51.476 -8:00" -> "2009-06-24 13:20:00.000 -8:00"
     * </ul>
     * @param value The DateTime value to round downward
     * @return Null if the argument is null or a new instance of the DateTime
     *      value rounded downwards to the nearest 10 minutes.
     */
    public static DateTime floorToTenMinutes(DateTime value) {
        return floorToMinutePeriod(value, 10);
    }

    /**
     * Null-safe method that returns a new instance of a DateTime object rounded
     * downwards to the nearest 5 minutes. The time zone of the returned DateTime
     * instance will be the same as the argument. Similar to a floor() function
     * on a float.<br>
     * Examples:
     * <ul>
     *      <li>null -> null
     *      <li>"2009-06-24 13:24:51.476 -8:00" -> "2009-06-24 13:20:00.000 -8:00"
     * </ul>
     * @param value The DateTime value to round downward
     * @return Null if the argument is null or a new instance of the DateTime
     *      value rounded downwards to the nearest 5 minutes.
     */
    public static DateTime floorToFiveMinutes(DateTime value) {
        return floorToMinutePeriod(value, 5);
    }
    
    /**
     * Null-safe method that returns a new instance of a DateTime object rounded
     * downwards to the nearest specified period in minutes. For example, if
     * a period of 5 minutes is requested, a time of "2009-06-24 13:24:51.476 -8:00"
     * would return a datetime of "2009-06-24 13:20:00.000 -8:00". The time zone of the
     * returned DateTime instance will be the same as the argument. Similar to a
     * floor() function on a float.<br>
     * NOTE: While any period in minutes between 1 and 59 can be passed into this
     * method, its only useful if the value can be evenly divided into 60 to make
     * it as useful as possible.<br>
     * Examples:
     * <ul>
     *      <li>null -> null
     *      <li>5: "2009-06-24 13:39:51.476 -8:00" -> "2009-06-24 13:35:00.000 -8:00"
     *      <li>10: "2009-06-24 13:39:51.476 -8:00" -> "2009-06-24 13:30:00.000 -8:00"
     *      <li>15: "2009-06-24 13:39:51.476 -8:00" -> "2009-06-24 13:30:00.000 -8:00"
     *      <li>20: "2009-06-24 13:39:51.476 UTC" -> "2009-06-24 13:20:00.000 UTC"
     * </ul>
     * @param value The DateTime value to round downward
     * @return Null if the argument is null or a new instance of the DateTime
     *      value rounded downwards to the nearest period in minutes.
     */
    public static DateTime floorToMinutePeriod(DateTime value, int periodInMinutes) {
        if (value == null) {
            return null;
        }
        if (periodInMinutes <= 0 || periodInMinutes > 59) {
            throw new IllegalArgumentException("period in minutes must be > 0 and <= 59");
        }
        int min = value.getMinuteOfHour();
        min = (min / periodInMinutes) * periodInMinutes;
        return new DateTime(value.getYear(), value.getMonthOfYear(), value.getDayOfMonth(), value.getHourOfDay(), min, 0, 0, value.getZone());
    }

    /**
     * Null-safe method that returns a new instance of a DateTime object rounded
     * downwards to the nearest minute. The time zone of the returned DateTime
     * instance will be the same as the argument. Similar to a floor() function
     * on a float.<br>
     * Examples:
     * <ul>
     *      <li>null -> null
     *      <li>"2009-06-24 13:24:51.476 -8:00" -> "2009-06-24 13:24:00.000 -8:00"
     * </ul>
     * @param value The DateTime value to round downward
     * @return Null if the argument is null or a new instance of the DateTime
     *      value rounded downwards to the nearest minute.
     */
    public static DateTime floorToMinute(DateTime value) {
        if (value == null) {
            return null;
        }
        return new DateTime(value.getYear(), value.getMonthOfYear(), value.getDayOfMonth(), value.getHourOfDay(), value.getMinuteOfHour(), 0, 0, value.getZone());
    }

    /**
     * Null-safe method that returns a new instance of a DateTime object rounded
     * downwards to the nearest second. The time zone of the returned DateTime
     * instance will be the same as the argument. Similar to a floor() function
     * on a float.<br>
     * Examples:
     * <ul>
     *      <li>null -> null
     *      <li>"2009-06-24 13:24:51.476 -8:00" -> "2009-06-24 13:24:51.000 -8:00"
     * </ul>
     * @param value The DateTime value to round downward
     * @return Null if the argument is null or a new instance of the DateTime
     *      value rounded downwards to the nearest second.
     */
    public static DateTime floorToSecond(DateTime value) {
        if (value == null) {
            return null;
        }
        return new DateTime(value.getYear(), value.getMonthOfYear(), value.getDayOfMonth(), value.getHourOfDay(), value.getMinuteOfHour(), value.getSecondOfMinute(), 0, value.getZone());
    }
}
