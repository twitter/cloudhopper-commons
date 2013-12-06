package com.cloudhopper.commons.gsm.demo;

import com.cloudhopper.commons.gsm.DataCoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * #%L
 * ch-commons-gsm
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

/**
 *
 * @author joelauer
 */
public class DataCodingMain {
    private static final Logger logger = LoggerFactory.getLogger(DataCodingMain.class);

    static public void main(String[] args) throws Exception {
        // common DCSs I've seen: -15, 17, 11
        // -15 = 0xF1
        // 16 = 0x10
        // 17 = 0x11
        DataCoding dcs = DataCoding.parse((byte)0x11);
        
        // various settings that can be set purely from a dcs byte value
        logger.info("char encoding: " + dcs.getCharacterEncoding());
        logger.info("encoding group: " + dcs.getCodingGroup());
        logger.info("message class: " + dcs.getMessageClass());
        logger.info("is compressed: " + dcs.isCompressed());
    }

}
