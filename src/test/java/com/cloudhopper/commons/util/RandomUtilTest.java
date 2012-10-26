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

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class RandomUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(RandomUtilTest.class);

    @Test
    public void generateString() {
        // can really only test the length since its a random num
        String result = RandomUtil.generateString(2);
        Assert.assertEquals(2, result.length());

        result = RandomUtil.generateString(20);
        Assert.assertEquals(20, result.length());

        result = RandomUtil.generateString(8);
        Assert.assertEquals(8, result.length());
    }

}
