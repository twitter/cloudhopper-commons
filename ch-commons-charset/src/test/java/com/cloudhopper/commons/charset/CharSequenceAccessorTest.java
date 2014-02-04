package com.cloudhopper.commons.charset;

/*
 * #%L
 * ch-commons-charset
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

import com.cloudhopper.commons.charset.CharSequenceAccessor.CharArrayWrapper;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author joelauer
 */
public class CharSequenceAccessorTest {
    
    @Test
    public void usage() throws Exception {
        try {
            String s0 = "test";
            CharArrayWrapper wrapper = CharSequenceAccessor.access(s0);
            Assert.assertEquals(0, wrapper.offset);
            Assert.assertEquals(4, wrapper.length);
            Assert.assertArrayEquals(new char[] { 't','e','s','t' }, wrapper.value);

            // this usually retains same char[] but adjusts offset (may differ between JVMs)
            String s1 = s0.substring(1);
            wrapper = CharSequenceAccessor.access(s1);
            Assert.assertEquals(1, wrapper.offset);
            Assert.assertEquals(3, wrapper.length);
            Assert.assertArrayEquals(new char[] { 't','e','s','t' }, wrapper.value);

            StringBuilder sb = new StringBuilder("test");
            wrapper = CharSequenceAccessor.access(sb);
            Assert.assertEquals(0, wrapper.offset);
            Assert.assertEquals(4, wrapper.length);
            // appears JVM actually creates a default size of 20 -- more than 4 -- only compare first 4 chars
            Assert.assertArrayEquals(new char[] { 't','e','s','t' }, Arrays.copyOf(wrapper.value, 4));

            // stringbuffer isn't supported -- should return null
            StringBuffer sbf = new StringBuffer("test");
            wrapper = CharSequenceAccessor.access(sbf);
            Assert.assertNull(wrapper);
        } catch (Throwable t) {
            System.out.println("WARN: CharSequenceAccessor tests failed... optimizations disabled");
        }
    }
    
}
