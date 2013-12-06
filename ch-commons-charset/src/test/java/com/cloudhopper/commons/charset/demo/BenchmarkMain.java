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
import com.cloudhopper.commons.charset.UTF8Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class BenchmarkMain {
    private static final Logger logger = LoggerFactory.getLogger(BenchmarkMain.class);

    static public void main(String[] args) throws Exception {
        String in = "\u20ACABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuv\u6025";

        int count = 500000;
        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            //String out = CharsetUtil.normalize(in, CharsetUtil.CHARSET_GSM);
            //GSMCharset.canRepresent(in);
            //int length = UTF8Charset.calculateByteLength(in);
            //int length = in.getBytes("UTF8").length;

            //byte[] a = UTF8Charset.encode(in);
            byte[] b = CharsetUtil.encode(in, CharsetUtil.CHARSET_UTF_8);
        }

        long stop = System.currentTimeMillis();

        logger.info("Took " + (stop-start) + " ms to run " + count + " times");
    }

}
