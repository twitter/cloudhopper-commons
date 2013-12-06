package com.cloudhopper.commons.charset.demo;

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

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class Charset6Main {
    private static final Logger logger = LoggerFactory.getLogger(Charset6Main.class);

    static public void main(String[] args) throws Exception {
        byte[] vmpbytes = HexUtil.toByteArray("c3a2c282c2ac");
        
        // i suspect vmp is double utf-8 encoding this
        String decoded1 = CharsetUtil.decode(vmpbytes, CharsetUtil.CHARSET_UTF_8);
        
        // now get raw bytes
        logger.info("decode #1 length: " + decoded1.length());
        
        byte[] nextbytes = decoded1.getBytes("ISO-8859-1");
        logger.info("decode #1 bytes: " + HexUtil.toHexString(nextbytes));
    }

}
