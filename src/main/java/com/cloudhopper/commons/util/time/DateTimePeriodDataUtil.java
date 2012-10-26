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

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DateTimePeriodDataUtil {

    private DateTimePeriodDataUtil() {
        // only static method
    }

    @SuppressWarnings("unchecked")
    public static <T extends DateTimePeriodData> void fill(Class<T> type, Map<DateTime,T> dataset, List<DateTimePeriod> periods) {
        // loop thru every period, check its it starting time exists in the dataset
        for (DateTimePeriod period : periods) {
            if (dataset.containsKey(period.getStart())) {
                // do nothing
            } else {
                // add a default entry
                try {
                    Constructor ctor = type.getConstructor(DateTimePeriod.class);
                    T data = (T)ctor.newInstance(period);
                    dataset.put(period.getStart(), data);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create default dataset entry", e);
                }
            }
        }
    }

}
