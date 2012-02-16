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

package com.cloudhopper.commons.xbean.type;

// third party imports
import com.cloudhopper.commons.xbean.type.ShortTypeConverter;
import com.cloudhopper.commons.xbean.type.ShortPrimitiveTypeConverter;
import com.cloudhopper.commons.xbean.ConversionException;
import com.cloudhopper.commons.xbean.ConversionOverflowException;
import com.cloudhopper.commons.xbean.TypeConverter;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

// my imports

public class ShortTypeConverterTest {
    private static final Logger logger = Logger.getLogger(ShortTypeConverterTest.class);

    private TypeConverter[] pcs = new TypeConverter[] {
        new ShortTypeConverter(),
        new ShortPrimitiveTypeConverter()
    };
    
    @Test
    public void validConversions() throws Exception {
        for (TypeConverter pc : pcs) {
            Assert.assertEquals(Short.valueOf((short)0), pc.convert("0"));
            // min and max value of a short
            Assert.assertEquals(Short.valueOf((short)-32768), pc.convert("-32768"));
            Assert.assertEquals(Short.valueOf((short)32767), pc.convert("32767"));
            // hex versions
            Assert.assertEquals(Short.valueOf((short)0), pc.convert("0x0"));
            Assert.assertEquals(Short.valueOf((short)0), pc.convert("0X0"));
            Assert.assertEquals(Short.valueOf((short)32767), pc.convert("0x7FFF"));
            Assert.assertEquals(Short.valueOf((short)0xFFFF), pc.convert("0xFFFF"));
        }
    }
    
    @Test
    public void overflow() throws Exception {
        for (TypeConverter pc : pcs) {
            try {
                pc.convert("0x10000");
            } catch (ConversionOverflowException e) {
                // valid
            }
        }
    }
    
    @Test
    public void negativeInHexNotAllowed() throws Exception {
        for (TypeConverter pc : pcs) {
            try {
                pc.convert("-0x01");
            } catch (ConversionException e) {
                // valid
            }
        }
    }
}
