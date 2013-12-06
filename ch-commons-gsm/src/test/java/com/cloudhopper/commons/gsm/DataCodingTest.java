package com.cloudhopper.commons.gsm;

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

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class DataCodingTest {
    private static final Logger logger = LoggerFactory.getLogger(DataCodingTest.class);

    @Test
    public void parse() {
        DataCoding dc = DataCoding.parse(DataCoding.CHAR_ENC_DEFAULT);
        Assert.assertEquals((byte)0x00, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_DEFAULT, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.CHARACTER_ENCODING, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_0, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        dc = DataCoding.parse(DataCoding.CHAR_ENC_8BIT);
        Assert.assertEquals((byte)0x04, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_8BIT, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.CHARACTER_ENCODING, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_0, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        dc = DataCoding.parse(DataCoding.CHAR_ENC_IA5);
        Assert.assertEquals((byte)0x01, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_IA5, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.CHARACTER_ENCODING, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_0, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        dc = DataCoding.parse(DataCoding.CHAR_ENC_UCS2);
        Assert.assertEquals((byte)0x08, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_UCS2, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.CHARACTER_ENCODING, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_0, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        // common to see a byte value of -15 used
        dc = DataCoding.parse((byte)-15);
        Assert.assertEquals((byte)0xF1, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_DEFAULT, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.MESSAGE_CLASS, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_1, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        // common to see a byte value of 16 used
        dc = DataCoding.parse((byte)16);
        Assert.assertEquals((byte)16, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_DEFAULT, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.GENERAL, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_0, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        // common to see a byte value of 17 used
        dc = DataCoding.parse((byte)17);
        Assert.assertEquals((byte)17, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_DEFAULT, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.GENERAL, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_1, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        // common to see a byte value of 20 used
        dc = DataCoding.parse((byte)20);
        Assert.assertEquals((byte)20, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_8BIT, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.GENERAL, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_0, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        // common to see a byte value of 21 used
        dc = DataCoding.parse((byte)21);
        Assert.assertEquals((byte)21, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_8BIT, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.GENERAL, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_1, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        // common to see a byte value of 21 used
        dc = DataCoding.parse((byte)21);
        Assert.assertEquals((byte)21, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_8BIT, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.GENERAL, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_1, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        // 1C is a reserved character encoding!
        dc = DataCoding.parse((byte)0x1C);
        Assert.assertEquals(DataCoding.CHAR_ENC_DEFAULT, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.RESERVED, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_0, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        // very complicated general data coding 111011
        dc = DataCoding.parse((byte)0x3B);
        Assert.assertEquals((byte)0x3B, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_UCS2, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.GENERAL, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_3, dc.getMessageClass());
        Assert.assertEquals(true, dc.isCompressed());

        // message waiting indication group
        dc = DataCoding.parse((byte)0xEA);
        Assert.assertEquals((byte)0xEA, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_UCS2, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.MESSAGE_WAITING_INDICATION, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_0, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());

        // 0xFD = 1111 11 01 -> should be UCS2
        /** NOTE: 0xFD is not a valid DCS value
        dc = DataCoding.parse((byte)0xFD);
        Assert.assertEquals((byte)0xFD, dc.getByteValue());
        Assert.assertEquals(DataCoding.CHAR_ENC_UCS2, dc.getCharacterEncoding());
        Assert.assertEquals(DataCoding.Group.MESSAGE_CLASS, dc.getCodingGroup());
        Assert.assertEquals(DataCoding.MESSAGE_CLASS_1, dc.getMessageClass());
        Assert.assertEquals(false, dc.isCompressed());
         */
    }

}
