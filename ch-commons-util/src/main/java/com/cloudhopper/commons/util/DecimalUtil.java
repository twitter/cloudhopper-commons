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
import java.text.DecimalFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility classes for working with decimal values like a float or double.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DecimalUtil {
    private static Logger logger = LoggerFactory.getLogger(DecimalUtil.class);

    /**
     * Thread-safe version of converting a double value to a String when only
     * a certain number of digits are desired after a decimal point.
     * @param value
     * @param precision
     * @return
     */
    public static String toString(double value, int precision) {
        // create decimal format string
        StringBuilder buf = new StringBuilder("0");
        if (precision > 0) {
            buf.append(".");
        }
        for (int i = 0; i < precision; i++) {
            buf.append("0");
        }
        // create a decimal format
        DecimalFormat format = new DecimalFormat(buf.toString());
        return format.format(value);
        
        /**
        String temp = Double.toString(value);
        StringBuffer buf = new StringBuffer(temp.length()+precision);
        buf.append(temp);
        // find the '.' char
        int pos = buf.indexOf(".");
        // was the . found, if not, add it so we can use the same logic below
        if (pos < 0) {
            // if we want precision, add . and set the position correctly
            if (precision > 0) {
                buf.append('.');
                pos = buf.length()-1;   // set pos to last char
            } else {
                return buf.toString();
            }
        }
        // number of digits after
        int digitsAfter = buf.length() - pos - 1;

        // if precision is zero, then fake getting rid of the period by incrementing this
        if (precision == 0)
            digitsAfter++;

        // do we need to add, delete, or keep it?
        if (digitsAfter < precision) {
            // add zeoes
            int count = precision - digitsAfter;
            for (int i = 0; i < count; i++) {
                buf.append('0');
            }
        } else if (digitsAfter > precision) {
            // trim digits
            int count = digitsAfter - precision;
            buf.delete(buf.length()-count, buf.length());
        }
        return buf.toString();
         */
    }

}
