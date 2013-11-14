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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class SenderMain {
    private static final Logger logger = LoggerFactory.getLogger(SenderMain.class);

    static public void main(String[] args) throws Exception {


        /**
        String url = "http://lyn-twtr-gw.cloudhopper.com/api/sxmp/1.0";

        SubmitRequest request = new SubmitRequest();

        request.setAccount(new Account("twitter", "4jd6781uy"));
        request.setDeliveryReport(Boolean.TRUE);
        request.setReferenceId("TESTREF");
        
        MobileAddress sourceAddr = new MobileAddress(MobileAddress.Type.NETWORK, "40404");
        request.setSourceAddress(sourceAddr);

        MobileAddress destAddr = new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+16502931589");
        request.setDestinationAddress(destAddr);

        request.setOperatorId(20);

        request.setText("Hello World2");
        //submit.setText("Test from Twitter 2");

        logger.debug("Request: " + request);

        SubmitResponse response = SxmpSender.submit(url, request);

        logger.debug("Response: " + response);
         */

        String url = "https://sms.twitter.com/receive/cloudhopper";

        DeliverRequest request = new DeliverRequest();

        request.setAccount(new Account("twitter", "4jd6781uy"));

        //request.setReferenceId("TESTREF");
        request.setTicketId(System.currentTimeMillis()+"");

        MobileAddress sourceAddr = new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+14159129228");
        request.setSourceAddress(sourceAddr);

        MobileAddress destAddr = new MobileAddress(MobileAddress.Type.NETWORK, "40404");
        request.setDestinationAddress(destAddr);

        request.setOperatorId(23);

        request.setText("Hello 2");
        //submit.setText("Test from Twitter 2");

        logger.debug("Request: " + request);

        DeliverResponse response = SxmpSender.deliver(url, request, false);

        logger.debug("Response: " + response);
    }
}
