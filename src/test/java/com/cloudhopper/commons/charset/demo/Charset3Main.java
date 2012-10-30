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
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class Charset3Main {
    private static final Logger logger = LoggerFactory.getLogger(Charset3Main.class);

    static public void main(String[] args) throws Exception {
        Charset cs = CharsetUtil.map("GSM");

        for (int i = 0; i < 65536; i++) {
            String str = (char)i+"";
            byte[] sourceBytes = str.getBytes("ISO-10646-UCS-2");
            byte[] encodedBytes = cs.encode(str);

            // only print out exceptions between GSM & ISO-8859-1
            if (!Arrays.equals(sourceBytes, encodedBytes) && encodedBytes[0] != (byte)0x3F) {
                // { (byte)0x00, (byte)0x40 }, // @
                System.out.println("{ (byte)0x" + HexUtil.toHexString(encodedBytes) + ", (char)0x" + HexUtil.toHexString(sourceBytes) + " }, // " + str);
                //logger.debug(str + ": " + HexUtil.toHexString(sourceBytes) + "->" + HexUtil.toHexString(encodedBytes));
            }
        }
    }

}
