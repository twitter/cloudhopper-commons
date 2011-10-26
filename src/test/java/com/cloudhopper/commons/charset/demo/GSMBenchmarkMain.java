/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.cloudhopper.commons.charset.demo;

import com.cloudhopper.commons.charset.GSMCharset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Benchmark for GSM canRepresent().
 * 
 * @author joelauer
 */
public class GSMBenchmarkMain {
    private static final Logger logger = LoggerFactory.getLogger(GSMBenchmarkMain.class);
    
    static public void main(String[] args) throws Exception {
        // strings to decode/encode to/from UTF-8
        // build a string of every GSM char
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < GSMCharset.CHAR_TABLE.length; i++) {
            char c = GSMCharset.CHAR_TABLE[i];
            if (c > 0) {
                s.append(c);
            }
        }
        for (int i = 0; i < GSMCharset.EXT_CHAR_TABLE.length; i++) {
            char c = GSMCharset.EXT_CHAR_TABLE[i];
            if (c > 0) {
                s.append(c);
            }
        }
        
        String gsmString = s.toString();
        System.out.println("gsm string: " + gsmString);
        
        int count = 1000000;
        long encodeStart = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            GSMCharset.canRepresent(gsmString);
        }

        long encodeStop = System.currentTimeMillis();

        System.out.println("took " + (encodeStop-encodeStart) + " ms to run " + count + " times");
    }
    
}
