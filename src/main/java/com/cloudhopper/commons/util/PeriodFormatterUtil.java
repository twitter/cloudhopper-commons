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
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Utility class to create useful Joda PeriodFormatters.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class PeriodFormatterUtil {

    private static PeriodFormatter linuxUptimeStyle;
    private static PeriodFormatter standardUptimeStyle;
    private static PeriodType dayHourMinSecPeriodType;

    static {
        // create a standard period type that does not include years, months,
        // weeks, or millis -- this is required in order to properly print out
        // the uptime values where we only want a max value of days calculated
        dayHourMinSecPeriodType = PeriodType.standard().withYearsRemoved().withMonthsRemoved().withWeeksRemoved().withMillisRemoved();

        linuxUptimeStyle = new PeriodFormatterBuilder()
            .printZeroAlways()
            .appendDays()
            .appendSuffix(" day", " days")
            .appendSeparator(", ")
            .minimumPrintedDigits(2)
            .appendHours()
            .appendSeparator(":")
            .minimumPrintedDigits(2)
            .appendMinutes()
            .appendSeparator(":")
            .minimumPrintedDigits(2)
            .appendSeconds()
            .toFormatter();

        standardUptimeStyle = new PeriodFormatterBuilder()
            .printZeroAlways()
            .appendDays()
            .appendSuffix(" day ", " days ")
            .appendHours()
            .appendSuffix(" hours ")
            .appendMinutes()
            .appendSuffix(" mins ")
            .appendSeconds()
            .appendSuffix(" secs")
            .toFormatter();
    }

    /**
     * Gets a PeriodType that can be used to create a Period that is normalized
     * to only include the fields Day, Hour, Minute, and Second.  Basically,
     * this PeriodType would max out at days, so that someting like 40 days
     * does not get converted to 1 month, 1 week, and 3 days like it normally
     * would with the Joda library.
     * @return A PeriodType that only includes Days, Hours, Minutes, and Seconds.
     * @see PeriodFormatterUtil#createDayHourMinSecPeriod(long) 
     */
    public static PeriodType getDayHourMinSecPeriodType() {
        return dayHourMinSecPeriodType;
    }

    /**
     * Creates a new Period that can be used properly with the "Uptime" styles
     * returned by this utility class.  This period normalizes the internal
     * fields to only include days, hours, minutes, and seconds.
     * @param durationInMillis The length of the duration in milliseconds
     * @return A new normalized Period
     */
    public static Period createDayHourMinSecPeriod(long durationInMillis) {
        return new Period(durationInMillis).normalizedStandard(dayHourMinSecPeriodType);
    }

    /**
     * Gets a PeriodFormatter in the Linux OS style such as "0 days, 01:05:12"
     * or "2 days, 07:00:00".  NOTE: You need to be careful with the Period
     * used with this formatter object.  It needs to be normalized to max out
     * with the days fields (not include years, months, weeks, or millis).
     * @return The global PeriodFormatter instance
     * @see PeriodFormatterUtil#createDayHourMinSecPeriod(long) 
     */
    public static PeriodFormatter getLinuxUptimeStyle() {
        return linuxUptimeStyle;
    }

    /**
     * Gets a PeriodFormatter in the standard Uptime style such as "0 days 0 hours 1 min 50 secs"
     * or "2 days 7 hours 1 min 50 secs". NOTE: You need to be careful with the Period
     * used with this formatter object.  It needs to be normalized to max out
     * with the days fields (not include years, months, weeks, or millis).
     * @return The global PeriodFormatter instance
     * @see PeriodFormatterUtil#createDayHourMinSecPeriod(long) 
     */
    public static PeriodFormatter getStandardUptimeStyle() {
        return standardUptimeStyle;
    }

    /**
     * Helper method to create a "Linux" uptime styled string that represents
     * the duration (in milliseconds).  This method will only call other methods
     * publicly available on this class.  Converts the duration into a String
     * such as "0 days, 01:05:12" or "2 days, 07:00:00".
     * @param durationInMillis The duration (in millis)
     * @return A styled String of the duration
     */
    public static String toLinuxUptimeStyleString(long durationInMillis) {
        return getLinuxUptimeStyle().print(createDayHourMinSecPeriod(durationInMillis));
    }

    /**
     * Helper method to create a "Standard" uptime styled string that represents
     * the duration (in milliseconds).  This method will only call other methods
     * publicly available on this class.  Converts the duration into a String
     * such as "0 days 0 hours 1 min 50 secs" or "2 days 7 hours 1 min 50 secs".
     * @param durationInMillis The duration (in millis)
     * @return A styled String of the duration
     */
    public static String toStandardUptimeStyleString(long durationInMillis) {
        return getStandardUptimeStyle().print(createDayHourMinSecPeriod(durationInMillis));
    }

}
