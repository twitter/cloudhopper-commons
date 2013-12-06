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
public class TypeOfAddressTest {
    private static final Logger logger = LoggerFactory.getLogger(TypeOfAddressTest.class);

    @Test
    public void valueOf() {
        TypeOfAddress toa = TypeOfAddress.valueOf((byte)0x91);
        Assert.assertEquals(Ton.INTERNATIONAL, toa.getTon());
        Assert.assertEquals(Npi.ISDN, toa.getNpi());
        TypeOfAddress toa2 = TypeOfAddress.valueOf((byte)0x81);
        Assert.assertEquals(Ton.INTERNATIONAL, toa2.getTon());
        Assert.assertEquals(Npi.UNKNOWN, toa2.getNpi());
    }

    @Test
    public void valueOf2() {
        TypeOfAddress toa = TypeOfAddress.valueOf((byte)161);
        Assert.assertEquals(Ton.INTERNATIONAL, toa.getTon());
        //Assert.assertEquals(Npi.ISDN, toa.getNpi());
    }

    @Test
    public void toByte() {
        TypeOfAddress toa = new TypeOfAddress(Ton.INTERNATIONAL, Npi.ISDN);
        Assert.assertEquals((byte)0x91, toa.toByte());
    }

    @Test
    public void toByte2() {
        TypeOfAddress toa = new TypeOfAddress(Ton.UNKNOWN, Npi.UNKNOWN);
        Assert.assertEquals((byte)0x80, toa.toByte());
    }

}
