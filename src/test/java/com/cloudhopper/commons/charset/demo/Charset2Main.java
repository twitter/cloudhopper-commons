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
import com.cloudhopper.commons.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class Charset2Main {
    private static final Logger logger = LoggerFactory.getLogger(Charset2Main.class);

    static public void main(String[] args) throws Exception {
        String str0 = "\u20AC";

        //Charset charset = new GSMCharset();
        //Charset charset = new PackedGSMCharset();
        //Charset charset = new ISO88591Charset();
        //Charset charset = new UCS2Charset();
        //Charset charset = new UTF8Charset();

        //Charset charset = CharsetUtil.map(CharsetUtil.NAME_PACKED_GSM);
        Charset charset = CharsetUtil.map(CharsetUtil.NAME_ISO_8859_15);

        byte[] encoded = CharsetUtil.encode(str0, charset);

        logger.info("str0: " + str0);
        logger.info("encoded: " + HexUtil.toHexString(encoded));
    }

}
