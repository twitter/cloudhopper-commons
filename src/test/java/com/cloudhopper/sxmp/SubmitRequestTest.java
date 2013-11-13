package com.cloudhopper.sxmp;

/*
 * #%L
 * ch-sxmp
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
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
public class SubmitRequestTest {
    private static final Logger logger = LoggerFactory.getLogger(SubmitRequestTest.class);

    @Test
    public void createResponseWithoutReferenceId() throws Exception {
        // create a submit request
        SubmitRequest submit = new SubmitRequest();
        submit.setAccount(new Account("customer1", "password1"));
        submit.setOperatorId(1);
        submit.setDeliveryReport(Boolean.TRUE);
        MobileAddress sourceAddr = new MobileAddress();
        sourceAddr.setAddress(MobileAddress.Type.NETWORK, "40404");
        submit.setSourceAddress(sourceAddr);
        MobileAddress destAddr = new MobileAddress();
        destAddr.setAddress(MobileAddress.Type.INTERNATIONAL, "+13135551212");
        submit.setDestinationAddress(destAddr);
        submit.setText("Hello World");

        SubmitResponse submitResponse = submit.createResponse();

        Assert.assertEquals(null, submitResponse.getReferenceId());
        // make sure defaults are 0 and "OK"
        Assert.assertEquals(new Integer(0), submitResponse.getErrorCode());
        Assert.assertEquals("OK", submitResponse.getErrorMessage());
    }

    @Test
    public void createResponseWithoutOnlyDefaults() throws Exception {
        // create a submit request
        SubmitRequest submit = new SubmitRequest();
        SubmitResponse submitResponse = submit.createResponse();

        Assert.assertEquals(null, submitResponse.getReferenceId());
        // make sure defaults are 0 and "OK"
        Assert.assertEquals(new Integer(0), submitResponse.getErrorCode());
        Assert.assertEquals("OK", submitResponse.getErrorMessage());
    }
}
