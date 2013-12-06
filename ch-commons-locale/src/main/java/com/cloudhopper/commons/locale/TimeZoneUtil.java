package com.cloudhopper.commons.locale;

/*
 * #%L
 * ch-commons-locale
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
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
import java.util.Set;
import java.util.TreeMap;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements utilities for working with timezones.
 *
 * @author Joe Lauer
 */
public class TimeZoneUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeZoneUtil.class);

    /** All Timezones */
    private static TreeMap<String,TimeZone> timezones;
    /** Only "continental" Timezones for Africa, America, Antarctica, Arctic, Asia,
     *  Atlantic, Australia, Europe, Indian, Mideast, Pacific */
    private static ArrayList<TimeZone> continentalTimezones;

    static {
        // build sorted list of timezones first
        timezones = new TreeMap<String,TimeZone>();
        // load all timezone ids in alphabetical order
        Set tzs = DateTimeZone.getAvailableIDs();
        long now = System.currentTimeMillis();
        for (Object tz : tzs) {
            String tzId = (String)tz;
            // get the actual timezone...
            DateTimeZone dtz = DateTimeZone.forID(tzId);
            // get today's timezone standard offset
            long standardOffset = dtz.getStandardOffset(now);
            long standardOffsetPositive = (standardOffset < 0 ? standardOffset*-1 : standardOffset);
            int hours = (int)(standardOffsetPositive/1000/60/60);
            int mins = (int)((standardOffsetPositive-(hours*60*60*1000))/1000/60);
            if (mins < 0) {
                mins = 0;
            }
            // conver this into hours...
            String standardOffsetTime = String.format((standardOffset < 0 ? "-" : "+") + "%02d:%02d", hours, mins);
            //logger.debug("Adding " + tzId + ", " + standardOffsetTime);
            timezones.put(tzId, new TimeZone(tzId, standardOffsetTime));
        }

        // build our "regional" list of timezones
        // Africa, America, Antarctica, Arctic, Asia, Atlantic, Australia, Europe, Indian, Mideast, Pacific
        continentalTimezones = new ArrayList<TimeZone>();
        // always add UTC first
        continentalTimezones.add(timezones.get("UTC"));
        for (TimeZone tz : timezones.values()) {
            String tempid = tz.getId().toLowerCase();
            if (tempid.startsWith("africa") || tempid.startsWith("america") || tempid.startsWith("antarctica")
                    || tempid.startsWith("arctic") || tempid.startsWith("asia") || tempid.startsWith("atlantic")
                    || tempid.startsWith("australia") || tempid.startsWith("europe") || tempid.startsWith("indian")
                    || tempid.startsWith("mideast") || tempid.startsWith("pacific")) {
                continentalTimezones.add(tz);
            }
        }
    }

    private TimeZoneUtil() {
        // only static
    }

    /**
     * Gets a timezone by id such as "America/Los_Angeles".
     * @param id
     * @return
     */
    public static TimeZone getTimeZone(String id) {
        return timezones.get(id);
    }

    /**
     * Gets an alphabetical list of "continental" timezones.  A continental timezone excludes
     * any misc. timezones and only includes UTC first, followed by any timezone
     * that starts with Africa, America, Antarctica, Arctic, Asia, Atlantic, Australia,
     * Europe, Indian, Mideast, Pacific.
     * @return A list of "continental" timezones.
     */
    public static List<TimeZone> getContinentalTimezones() {
        return continentalTimezones;
    }
    
}
