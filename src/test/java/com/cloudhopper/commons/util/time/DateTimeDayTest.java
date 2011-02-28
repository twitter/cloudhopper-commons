/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.cloudhopper.commons.util.time;

// third party imports
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;

/**
 * Tests DateTimePeriod class.
 * 
 * @author joelauer
 */
public class DateTimeDayTest {

    @Test
    public void getNext() throws Exception {
        DateTimePeriod period0 = DateTimePeriod.createDay(2009, 12, 31, DateTimeZone.UTC);

        DateTimePeriod period1 = period0.getNext();

        DateTime correctStart = new DateTime(2010,1,1,0,0,0,0,DateTimeZone.UTC);
        DateTime correctEnd = new DateTime(2010,1,2,0,0,0,0,DateTimeZone.UTC);
        Assert.assertEquals(correctStart, period1.getStart());
        Assert.assertEquals(correctEnd, period1.getEnd());
    }

    @Test
    public void getPrevious() throws Exception {
        DateTimePeriod period0 = DateTimePeriod.createDay(2009, 1, 1, DateTimeZone.UTC);

        DateTimePeriod period1 = period0.getPrevious();

        DateTime correctStart = new DateTime(2008,12,31,0,0,0,0,DateTimeZone.UTC);
        DateTime correctEnd = new DateTime(2009,1,1,0,0,0,0,DateTimeZone.UTC);
        Assert.assertEquals(correctStart, period1.getStart());
        Assert.assertEquals(correctEnd, period1.getEnd());
    }

    @Test
    public void getDefaultSubDuration() throws Exception {
        DateTimePeriod period0 = DateTimePeriod.createDay(2009, 1, 1, DateTimeZone.UTC);
        Assert.assertEquals(DateTimeDuration.HOUR, period0.getDefaultSubDuration());
    }
    
}
