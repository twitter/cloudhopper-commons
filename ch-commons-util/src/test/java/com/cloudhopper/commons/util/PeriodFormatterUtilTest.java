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

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class PeriodFormatterUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(PeriodFormatterUtilTest.class);

    // 8 days 8 hours 9 mins 27 secs
    // 8 days 08:09:27
    public static long UPTIME_LESS_THAN_9_DAYS = 59*900*59*23*10;
    // 2 days 12 hours 2 mins 50 secs
    // 2 days 12:02:50
    public static long UPTIME_LESS_THAN_3_DAYS = 59*900*59*23*3;
    // 1 day 22 hours 24 mins 48 secs 
    // 1 day 22:24:48
    public static long UPTIME_LESS_THAN_2_DAYS = 59*1000*59*24*2;
    // 0 days 20 hours 0 mins 56 secs 
    // 0 days 20:00:56
    public static long UPTIME_LESS_THAN_1_DAY = 59*900*59*23;
    // 0 days 0 hours 0 mins 53 secs 
    // 0 days 00:00:53
    public static long UPTIME_53_SECS = 59*900;

    @Test
    public void linuxUptimeStyle() throws Exception {
        // calls a helper method, which actually uses all the other methods in the utility class
        Assert.assertEquals("8 days, 08:09:27", PeriodFormatterUtil.toLinuxUptimeStyleString(UPTIME_LESS_THAN_9_DAYS));
        Assert.assertEquals("2 days, 12:02:50", PeriodFormatterUtil.toLinuxUptimeStyleString(UPTIME_LESS_THAN_3_DAYS));
        Assert.assertEquals("1 day, 22:24:48", PeriodFormatterUtil.toLinuxUptimeStyleString(UPTIME_LESS_THAN_2_DAYS));
        Assert.assertEquals("0 days, 20:00:56", PeriodFormatterUtil.toLinuxUptimeStyleString(UPTIME_LESS_THAN_1_DAY));
        Assert.assertEquals("0 days, 00:00:53", PeriodFormatterUtil.toLinuxUptimeStyleString(UPTIME_53_SECS));
    }

    @Test
    public void standardUptimeStyle() throws Exception {
        // calls a helper method, which actually uses all the other methods in the utility class
        Assert.assertEquals("8 days 8 hours 9 mins 27 secs", PeriodFormatterUtil.toStandardUptimeStyleString(UPTIME_LESS_THAN_9_DAYS));
        Assert.assertEquals("2 days 12 hours 2 mins 50 secs", PeriodFormatterUtil.toStandardUptimeStyleString(UPTIME_LESS_THAN_3_DAYS));
        Assert.assertEquals("1 day 22 hours 24 mins 48 secs", PeriodFormatterUtil.toStandardUptimeStyleString(UPTIME_LESS_THAN_2_DAYS));
        Assert.assertEquals("0 days 20 hours 0 mins 56 secs", PeriodFormatterUtil.toStandardUptimeStyleString(UPTIME_LESS_THAN_1_DAY));
        Assert.assertEquals("0 days 0 hours 0 mins 53 secs", PeriodFormatterUtil.toStandardUptimeStyleString(UPTIME_53_SECS));
    }
}
