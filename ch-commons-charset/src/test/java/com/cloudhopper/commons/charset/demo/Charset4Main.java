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
public class Charset4Main {
    private static final Logger logger = LoggerFactory.getLogger(Charset4Main.class);

    static public void main(String[] args) throws Exception {
        byte[] bytes = HexUtil.toByteArray("E0A495E0A49AE0A4BE");

        Charset charset = CharsetUtil.map(CharsetUtil.NAME_UTF_8);

        String decoded = CharsetUtil.decode(bytes, charset);

        byte[] hexDecoded = decoded.getBytes("ISO-10646-UCS-2");

        logger.info("decoded: " + decoded);
        logger.info("decodedAsHex: " + HexUtil.toHexString(hexDecoded));

    }

}
