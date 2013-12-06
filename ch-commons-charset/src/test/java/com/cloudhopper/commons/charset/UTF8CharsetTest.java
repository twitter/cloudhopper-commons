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

// third party imports
import com.cloudhopper.commons.util.HexUtil;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class UTF8CharsetTest {
    private static final Logger logger = LoggerFactory.getLogger(UTF8CharsetTest.class);

    @Test
    public void emoticons() throws Exception {
        // great site: http://www.rishida.net/tools/conversion/
        // U+1F631 is a very high range example of an emoticon (something more people are using)
        // UTF-8 bytes look like this: F09F98B1
        // UTF-16 bytes look like this: D83DDE31
        // JavaScript escapes: \uD83D\uDE31
        byte[] bytes = HexUtil.toByteArray("F09F98B1");
        String str = CharsetUtil.CHARSET_UTF_8.decode(bytes);
        //logger.debug(str);
        //byte[] utf32 = str.getBytes("UTF-32");
        //logger.debug(HexUtil.toHexString(utf32));
        Assert.assertEquals("\uD83D\uDE31", str);   // UTF-16 used with JVM
    }
    
}
