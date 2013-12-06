package com.cloudhopper.commons.xbean.util;

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

import com.cloudhopper.commons.xbean.ConversionException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author joelauer
 */
public class NumberRadixUtilTest {
    
    @Test(expected=ConversionException.class)
    public void normalizeLeadingHexZeroesNullValue() throws Exception {
        NumberRadixUtil.normalizeLeadingHexZeroes(null, 2);
    }
    
    @Test(expected=ConversionException.class)
    public void normalizeLeadingHexZeroesEmptyValue() throws Exception {
        NumberRadixUtil.normalizeLeadingHexZeroes("", 2);
    }
    
    @Test
    public void normalizeLeadingHexZeroes() throws Exception {
        // appending digits or equal
        Assert.assertEquals("00", NumberRadixUtil.normalizeLeadingHexZeroes("0", 2));
        Assert.assertEquals("00", NumberRadixUtil.normalizeLeadingHexZeroes("00", 2));
        Assert.assertEquals("0F", NumberRadixUtil.normalizeLeadingHexZeroes("F", 2));
        Assert.assertEquals("FF", NumberRadixUtil.normalizeLeadingHexZeroes("FF", 2));
        Assert.assertEquals("0000", NumberRadixUtil.normalizeLeadingHexZeroes("0", 4));
        Assert.assertEquals("0000", NumberRadixUtil.normalizeLeadingHexZeroes("00", 4));
        Assert.assertEquals("000F", NumberRadixUtil.normalizeLeadingHexZeroes("F", 4));
        Assert.assertEquals("00FF", NumberRadixUtil.normalizeLeadingHexZeroes("FF", 4));
        Assert.assertEquals("0FFF", NumberRadixUtil.normalizeLeadingHexZeroes("FFF", 4));
        Assert.assertEquals("FFFF", NumberRadixUtil.normalizeLeadingHexZeroes("FFFF", 4));
        Assert.assertEquals("00000000", NumberRadixUtil.normalizeLeadingHexZeroes("0", 8));
        Assert.assertEquals("00000000", NumberRadixUtil.normalizeLeadingHexZeroes("00", 8));
        Assert.assertEquals("0000000F", NumberRadixUtil.normalizeLeadingHexZeroes("F", 8));
        Assert.assertEquals("000000FF", NumberRadixUtil.normalizeLeadingHexZeroes("FF", 8));
        Assert.assertEquals("00000FFF", NumberRadixUtil.normalizeLeadingHexZeroes("FFF", 8));
        Assert.assertEquals("0000FFFF", NumberRadixUtil.normalizeLeadingHexZeroes("FFFF", 8));
        Assert.assertEquals("FFFFFFFF", NumberRadixUtil.normalizeLeadingHexZeroes("FFFFFFFF", 8));
        // trimming digits
        Assert.assertEquals("00", NumberRadixUtil.normalizeLeadingHexZeroes("000", 2));
        Assert.assertEquals("00", NumberRadixUtil.normalizeLeadingHexZeroes("0000", 2));
        Assert.assertEquals("0F", NumberRadixUtil.normalizeLeadingHexZeroes("00F", 2));
        Assert.assertEquals("FF", NumberRadixUtil.normalizeLeadingHexZeroes("000000000FF", 2));
    }
    
    @Test(expected=ConversionException.class)
    public void normalizeLeadingHexZeroesThrowsOverflow() throws Exception {
        // appending digits or equal
        NumberRadixUtil.normalizeLeadingHexZeroes("100", 2);
    }
    
}
