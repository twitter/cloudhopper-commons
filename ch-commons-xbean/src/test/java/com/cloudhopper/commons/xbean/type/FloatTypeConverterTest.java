package com.cloudhopper.commons.xbean.type;

/*
 * #%L
 * ch-commons-xbean
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

import com.cloudhopper.commons.xbean.TypeConverter;
import com.cloudhopper.commons.xbean.TypeConverterUtil;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class FloatTypeConverterTest {
    private static final Logger logger = Logger.getLogger(FloatTypeConverterTest.class);

    private TypeConverter[] pcs = new TypeConverter[] {
        new FloatTypeConverter(),
        new FloatPrimitiveTypeConverter()
    };
    
    @Test
    public void validConversions() throws Exception {
        for (TypeConverter pc : pcs) {
            // check that the registry has this class
            Object obj = pc.convert("0");
            Assert.assertEquals(obj, TypeConverterUtil.convert("0", obj.getClass()));
            Assert.assertEquals(Float.valueOf(0), pc.convert("0"));
            Assert.assertEquals(Float.valueOf(0), pc.convert("0.0"));
            Assert.assertEquals(Float.valueOf(1.14f), pc.convert("1.14"));
            // min and max  value of an integer
            Assert.assertEquals(Float.valueOf(-2147483648), pc.convert("-2147483648"));
            Assert.assertEquals(Float.valueOf(2147483647), pc.convert("2147483647"));
        }
    }
}
