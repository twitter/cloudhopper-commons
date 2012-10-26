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
import com.cloudhopper.commons.util.StringUtil;
import java.util.TreeMap;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;

/**
 * Tests DateTimePeriod class.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DateTimePeriodDataUtilTest {

    @Test
    public void fill() throws Exception {
        //
        // create a period of 4 hours
        //
        DateTime start = new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        DateTimePeriod period = new DateTimeHour(start, start.plusHours(4));

        // fill an empty map
        TreeMap<DateTime, SampleData> emptyMap = new TreeMap<DateTime, SampleData>();
        DateTimePeriodDataUtil.fill(SampleData.class, emptyMap, period.toHours());

        SampleData[] result0 = emptyMap.values().toArray(new SampleData[0]);
        Assert.assertArrayEquals(new SampleData[] {
            new SampleData(DateTimePeriod.createHour(2009, 1, 1, 0, DateTimeZone.UTC), null),
            new SampleData(DateTimePeriod.createHour(2009, 1, 1, 1, DateTimeZone.UTC), null),
            new SampleData(DateTimePeriod.createHour(2009, 1, 1, 2, DateTimeZone.UTC), null),
            new SampleData(DateTimePeriod.createHour(2009, 1, 1, 3, DateTimeZone.UTC), null)
        }, result0);

        // fill a partially completed map
        SampleData hour0 = new SampleData(DateTimePeriod.createHour(2009, 1, 1, 0, DateTimeZone.UTC), "hour0");
        SampleData hour2 = new SampleData(DateTimePeriod.createHour(2009, 1, 1, 2, DateTimeZone.UTC), "hour2");
        TreeMap<DateTime, SampleData> partialMap = new TreeMap<DateTime, SampleData>();
        partialMap.put(hour0.getPeriod().getStart(), hour0);
        partialMap.put(hour2.getPeriod().getStart(), hour2);
        DateTimePeriodDataUtil.fill(SampleData.class, partialMap, period.toHours());

        result0 = partialMap.values().toArray(new SampleData[0]);
        Assert.assertArrayEquals(new SampleData[] {
            new SampleData(DateTimePeriod.createHour(2009, 1, 1, 0, DateTimeZone.UTC), "hour0"),
            new SampleData(DateTimePeriod.createHour(2009, 1, 1, 1, DateTimeZone.UTC), null),
            new SampleData(DateTimePeriod.createHour(2009, 1, 1, 2, DateTimeZone.UTC), "hour2"),
            new SampleData(DateTimePeriod.createHour(2009, 1, 1, 3, DateTimeZone.UTC), null)
        }, result0);

    }

    private static class SampleData extends DateTimePeriodData {
        private final String text;
        public SampleData(DateTimePeriod period) {
            super(period);
            this.text = null;
        }
        public SampleData(DateTimePeriod period, String text) {
            super(period);
            this.text = text;
        }
        public String getText() { return this.text; }

        @Override
        public boolean equals(Object object) {
            if (!super.equals(object)) {
                return false;
            }
            if (!(object instanceof SampleData)) {
                return false;
            }
            SampleData otherData = (SampleData)object;
            return StringUtil.isEqual(this.text, otherData.text);
        }
    }

}
