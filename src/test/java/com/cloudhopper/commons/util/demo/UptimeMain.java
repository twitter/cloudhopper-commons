package com.cloudhopper.commons.util.demo;

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

// java imports

import com.cloudhopper.commons.util.PeriodFormatterUtil;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;


/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class UptimeMain {

    public static long UPTIME_LESS_THAN_9_DAYS = 59*900*59*23*10;      // less than 10 days in millis
    public static long UPTIME_LESS_THAN_3_DAYS = 59*900*59*23*3;      // less than 3 days in millis
    public static long UPTIME_LESS_THAN_2_DAYS = 59*1000*59*24*2;      // less than 2 days in millis
    public static long UPTIME_LESS_THAN_1_DAY = 59*900*59*23;      // less than 1 day in millis
    public static long UPTIME_56_SECS = 59*900;      // less than 1 day in millis

    public static void main(String[] args) {

        //Period period = new Period(uptime, PeriodType.standard().withYearsRemoved().withWeeksRemoved().withMonthsRemoved().withMillisRemoved());
        //MutablePeriod period = new Duration(uptime).toPeriod().toMutablePeriod();

        long uptime = UPTIME_56_SECS;

        // ah, ha -- this is super important -- need to normalize the period!
        PeriodType periodType = PeriodType.standard().withYearsRemoved().withMonthsRemoved().withWeeksRemoved().withMillisRemoved();
        Period period = new Period(uptime).normalizedStandard(periodType);

        System.out.println("Uptime: " + uptime + " ms");
        System.out.println("Weeks: " + period.getWeeks());
        System.out.println("Days: " + period.getDays());
        System.out.println("Millis: " + period.getMillis() + " ms");

        // print out the uptime
        String uptimeStyleString = PeriodFormatterUtil.getStandardUptimeStyle().print(period);
        String linuxStyleString = PeriodFormatterUtil.getLinuxUptimeStyle().print(period);

        System.out.println(uptimeStyleString);
        System.out.println(linuxStyleString);

        PeriodFormatter fmt = new PeriodFormatterBuilder()
            .printZeroNever()
            .appendDays()
            .appendSuffix(" day ", " days ")
            .appendHours()
            .appendSuffix(" hours ")
            .appendMinutes()
            .appendSuffix(" mins ")
            .printZeroAlways()
            .appendSeconds()
            .appendSuffix(" secs ")
            .toFormatter();

        String str0 = fmt.print(period);
        System.out.println(str0);


        String str1 = PeriodFormat.getDefault().print(period);
        System.out.println(str1);
    }

}
