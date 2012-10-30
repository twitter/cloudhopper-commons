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

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class CharsetMain {
    private static final Logger logger = LoggerFactory.getLogger(CharsetMain.class);

    static public void main(String[] args) throws Exception {
        String str0 = "Hello @ World";

        //Charset charset = new GSMCharset();
        //Charset charset = new PackedGSMCharset();
        //Charset charset = new ISO88591Charset();
        //Charset charset = new UCS2Charset();
        //Charset charset = new UTF8Charset();

        //Charset charset = CharsetUtil.map(CharsetUtil.NAME_PACKED_GSM);
        Charset charset = CharsetUtil.map(CharsetUtil.NAME_GSM);

        int count = 100000;

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {

            byte[] encoded = CharsetUtil.encode(str0, charset);

            //byte[] encoded = charset.encode(str0);

            //byte[] encoded = str0.getBytes("ISO-8859-1");
        }
        long stopTime = System.currentTimeMillis();

        logger.info("To convert to bytes " + count + " times, took " + (stopTime-startTime) + " ms");

        //logger.info("gsm: " + HexUtil.toHexString(gsmEncoded));
    }

}
