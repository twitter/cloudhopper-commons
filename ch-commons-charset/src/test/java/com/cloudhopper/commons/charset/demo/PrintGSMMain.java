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

import com.cloudhopper.commons.charset.GSMCharset;
import com.cloudhopper.commons.util.HexUtil;

/**
 *
 * @author joelauer
 */
public class PrintGSMMain {
    
    static public void main(String[] args) throws Exception {
        // utility class for converting CHAR_TABLE and EXT_CHAR_TABLEs into
        // switch statements to be used in several methods
        printTable(GSMCharset.CHAR_TABLE);
        printTable(GSMCharset.EXT_CHAR_TABLE);
    }
    
    static public void printTable(char[] t) {
        for (int i = 0; i < t.length; i++) {
            char c = t[i];
            if (c > 0) {
                if ((c >= ' ' && c <= '_') || (c >= 'a' && c <= '~')) {
                    // do not print out this!
                } else {
                    System.out.println("case '\\u" + HexUtil.toHexString((short)c) + "':" + "\t// " + c);
                }
            }
        }
    }
    
}
