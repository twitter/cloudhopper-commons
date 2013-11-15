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

import com.cloudhopper.commons.util.DecimalUtil;
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
	String url = "http://localhost:9080/api/sxmp/1.0";

        // create a submit request
        SubmitRequest submit = new SubmitRequest();

        submit.setAccount(new Account("customer1", "password1"));

        submit.setDeliveryReport(Boolean.TRUE);

        MobileAddress sourceAddr = new MobileAddress();
        sourceAddr.setAddress(MobileAddress.Type.NETWORK, "40404");
        submit.setSourceAddress(sourceAddr);

        submit.setOperatorId(24);

        MobileAddress destAddr = new MobileAddress();
        destAddr.setAddress(MobileAddress.Type.INTERNATIONAL, "+14155551212");

        submit.setDestinationAddress(destAddr);

        submit.setText("Test abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijjabcdefghijjabcdefghijjabcdefghijjabcdefghijjabcdefghijjabcdefghijjabcdefghijbcdefghij", TextEncoding.UTF_8);
        //submit.setText("Hello World");

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
