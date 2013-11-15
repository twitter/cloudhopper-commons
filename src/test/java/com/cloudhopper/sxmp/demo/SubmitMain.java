package com.cloudhopper.sxmp.demo;

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

import com.cloudhopper.commons.util.DecimalUtil;
import com.cloudhopper.sxmp.*;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class SubmitMain {
    private static final Logger logger = LoggerFactory.getLogger(SubmitMain.class);

    static public void main(String[] args) throws Exception {

	String url = "http://127.0.0.1:8080/api/sxmp/1.0";
	String phone = "+14155551212";
	int operator = 1;
	if (args.length > 0) url = args[0];
	if (args.length > 1) phone = args[1];
	if (args.length > 2) operator = Integer.parseInt(args[2]);

        // create a submit request
        SubmitRequest submit = new SubmitRequest();
        submit.setAccount(new Account("customer1", "password1"));
        submit.setDeliveryReport(Boolean.TRUE);
        
        MobileAddress sourceAddr = new MobileAddress();
        sourceAddr.setAddress(MobileAddress.Type.NETWORK, "40404");
        submit.setSourceAddress(sourceAddr);
        submit.setOperatorId(operator);

	submit.setPriority(Priority.URGENT);

        MobileAddress destAddr = new MobileAddress();
        destAddr.setAddress(MobileAddress.Type.INTERNATIONAL, phone);

        submit.setDestinationAddress(destAddr);
        submit.setText("Hello World");

        // Get file to be posted
        HttpClient client = new DefaultHttpClient();

        client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        client.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);

        long totalStart = System.currentTimeMillis();
        int count = 1;

        for (int i = 0; i < count; i++) {
            long start = System.currentTimeMillis();

            // execute request
            try {
                HttpPost post = new HttpPost(url);

                //ByteArrayEntity entity = new ByteArrayEntity(data);
                StringEntity entity = new StringEntity(SxmpWriter.createString(submit));
                entity.setContentType("text/xml; charset=\"iso-8859-1\"");
                post.setEntity(entity);

                ResponseHandler<String> responseHandler = new BasicResponseHandler();

                String responseBody = client.execute(post, responseHandler);
                long stop = System.currentTimeMillis();

                logger.debug("----------------------------------------");
                logger.debug("Response took " + (stop-start) + " ms");
                logger.debug(responseBody);
                logger.debug("----------------------------------------");
            } finally {
                // do nothing
            }
        }

        long totalEnd = System.currentTimeMillis();

        logger.debug("Response took " + (totalEnd - totalStart) + " ms for " + count + " requests");

        double seconds = ((double)(totalEnd - totalStart))/1000;
        double smspersec = ((double)count)/seconds;
        logger.debug("SMS / Sec: " + DecimalUtil.toString(smspersec, 2));

    }
}
