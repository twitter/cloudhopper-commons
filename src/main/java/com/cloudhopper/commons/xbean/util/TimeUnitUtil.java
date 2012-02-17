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
package com.cloudhopper.commons.xbean.util;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author jlauer
 */
public class TimeUnitUtil {
    
    public static class Result {
        private String number;
        private TimeUnit timeUnit;
        
        public Result(String number, TimeUnit timeUnit) {
            this.number = number;
            this.timeUnit = timeUnit;
        }

        public String getNumber() {
            return number;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }
    }
    
    static public Result parse(String v) {
        int pos = v.indexOf('.');
        if (pos > 0) {
            String number = v.substring(0, pos);
            String suffix = v.substring(pos+1).toLowerCase();
            if (suffix.equals("second") || suffix.equals("seconds")) {
                return new Result(number, TimeUnit.SECONDS);
            } else if (suffix.equals("millisecond") || suffix.equals("milliseconds") || suffix.equals("millis")) {
                return new Result(number, TimeUnit.MILLISECONDS);
            } else if (suffix.equals("minute") || suffix.equals("minutes")) {
                return new Result(number, TimeUnit.MINUTES);
            } else if (suffix.equals("hour") || suffix.equals("hours")) {
                return new Result(number, TimeUnit.HOURS);
            } else if (suffix.equals("day") || suffix.equals("days")) {
                return new Result(number, TimeUnit.DAYS);
            }
        }
        return null;
    }
    
}
