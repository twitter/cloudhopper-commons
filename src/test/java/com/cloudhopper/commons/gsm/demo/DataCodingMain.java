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

package com.cloudhopper.commons.gsm.demo;

import org.apache.log4j.Logger;

/**
 *
 * @author joelauer
 */
public class DataCodingMain {
    static private final Logger logger = Logger.getLogger(DataCodingMain.class);

    static public void main(String[] args) throws Exception {
        // common DCSs I've seen: -15, 17, 11
        // -15 = 0xF1
        // 16 = 0x10
        // 17 = 0x11
        //DCS dcs = new DCS();
        //dcs.setValue((byte)0x11);

        //logger.info(dcs.toDebugString());

    }

}
