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
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// my imports
//import net.cloudhopper.commons.util.ByteBuffer;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class DecimalUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(DecimalUtilTest.class);

    @Test
    public void toStringWithPrecision() {
        String result = null;
        result = DecimalUtil.toString(0, 0);
        Assert.assertEquals("0", result);

        result = DecimalUtil.toString(1.12, 0);
        Assert.assertEquals("1", result);

        result = DecimalUtil.toString(1.1, 0);
        Assert.assertEquals("1", result);

        result = DecimalUtil.toString(1.12, 1);
        Assert.assertEquals("1.1", result);

        result = DecimalUtil.toString(1.12, 2);
        Assert.assertEquals("1.12", result);

        result = DecimalUtil.toString(1.12, 3);
        Assert.assertEquals("1.120", result);

        result = DecimalUtil.toString(1, 4);
        Assert.assertEquals("1.0000", result);

        // this causes an Exponent to be used in string representation
        double d0 = (double)1/(double)818393434343431121L;
        result = DecimalUtil.toString(d0, 2);
        Assert.assertEquals("0.00", result);
    }
}
