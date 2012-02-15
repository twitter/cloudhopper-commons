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

package com.cloudhopper.commons.xbean.convert;

// third party imports
import com.cloudhopper.commons.xbean.ConversionException;
import com.cloudhopper.commons.xbean.ConversionOverflowException;
import com.cloudhopper.commons.xbean.PropertyConverter;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

// my imports

public class LongPropertyConverterTest {
    private static final Logger logger = Logger.getLogger(LongPropertyConverterTest.class);

    private PropertyConverter[] pcs = new PropertyConverter[] {
        new LongPropertyConverter(),
        new LongPrimitivePropertyConverter()
    };
    
    @Test
    public void validConversions() throws Exception {
        for (PropertyConverter pc : pcs) {
            Assert.assertEquals(Long.valueOf(0), pc.convert("0"));
            // min and max value of a long
            Assert.assertEquals(Long.valueOf(-9223372036854775808L), pc.convert("-9223372036854775808"));
            Assert.assertEquals(Long.valueOf(9223372036854775807L), pc.convert("9223372036854775807"));
            // hex versions
            Assert.assertEquals(Long.valueOf(0), pc.convert("0x0"));
            Assert.assertEquals(Long.valueOf(0), pc.convert("0X0"));
            Assert.assertEquals(Long.valueOf(9223372036854775807L), pc.convert("0x7FFFFFFFFFFFFFFF"));
            Assert.assertEquals(Long.valueOf(0xFFFFFFFFFFFFFFFFL), pc.convert("0xFFFFFFFFFFFFFFFF"));
        }
    }
    
    @Test
    public void overflow() throws Exception {
        for (PropertyConverter pc : pcs) {
            try {
                pc.convert("0x10000000000000000");
            } catch (ConversionOverflowException e) {
                // valid
            }
        }
    }
    
    @Test
    public void negativeInHexNotAllowed() throws Exception {
        for (PropertyConverter pc : pcs) {
            try {
                pc.convert("-0x01");
            } catch (ConversionException e) {
                // valid
            }
        }
    }
}
