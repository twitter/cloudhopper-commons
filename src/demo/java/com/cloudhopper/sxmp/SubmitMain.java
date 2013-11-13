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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

/**
 *
 * @author joelauer
 */
public class SubmitMain {
    private static final Logger logger = LoggerFactory.getLogger(SubmitMain.class);

    static public void main(String[] args) throws Exception {

        // target url
        //String url = "http://sfd-stratus-003.cloudhopper.lan/api/sxmp/1.0";
        //String url = "http://localhost:9080/api/sxmp/1.0";

        //String url = "http://lyn-stratus-lab4/api/sxmp/1.0";
        //String url = "http://lyn-stratus-005/api/sxmp/1.0";

        //String url = "http://lyn-stratus-001/api/sxmp/1.0";

        String url = "http://68.64.52.45/api/sxmp/1.0";

        //String host = "lyn-twtr-gw.cloudhopper.com";
        //String host = "lyn-stratus-006";

        //String url = "http://lw-stratus-100.vanso.lan/api/sxmp/1.0";

        //String url = "http://lyn-stratus-006/api/sxmp/1.0";

        //String url = "http://localhost:9080/api/sxmp/1.0";
        //String strURL = "http://sfd-twtr-gw.cloudhopper.com/api/sxmp/1.0";

        //String url = "http://" + host + "/api/sxmp/1.0";


        //String url = "http://68.64.54.78/api/sxmp/1.0";


        // create a submit request
        SubmitRequest submit = new SubmitRequest();

        //submit.setAccount(new Account("chdev", "aeljjl99"));
        //submit.setAccount(new Account("customer2", "password2"));
        //submit.setAccount(new Account("customer2", "password2"));

        submit.setAccount(new Account("twitter", "4jd6781uy"));
        //submit.setApplication(new Application("twitter2"));

        //submit.setAccount(new  Account("chdev", "aeljjl99"));
        //submit.setApplication(new Application("TestApp1"));

        submit.setDeliveryReport(Boolean.TRUE);
        // OLD STRATUS PROXY REQUIRES REFERENCE ID
        //submit.setReferenceId("TESTREF");
        
        MobileAddress sourceAddr = new MobileAddress();
        //sourceAddr.setAddress(MobileAddress.Type.NETWORK, "21212");
        //sourceAddr.setAddress(MobileAddress.Type.NETWORK, "88850900003728");
        //sourceAddr.setAddress(MobileAddress.Type.NETWORK, "55555");
        //sourceAddr.setAddress(MobileAddress.Type.ALPHANUMERIC, "Vanso");
        sourceAddr.setAddress(MobileAddress.Type.NETWORK, "40404");
        submit.setSourceAddress(sourceAddr);

        submit.setOperatorId(24);

        MobileAddress destAddr = new MobileAddress();
        destAddr.setAddress(MobileAddress.Type.INTERNATIONAL, "+13134434272");
        //destAddr.setAddress(MobileAddress.Type.INTERNATIONAL, "+17788961678");
        //destAddr.setAddress(MobileAddress.Type.INTERNATIONAL, "+180721600000");

        submit.setDestinationAddress(destAddr);

        submit.setText("Test abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijjabcdefghijjabcdefghijjabcdefghijjabcdefghijjabcdefghijjabcdefghijjabcdefghijbcdefghij", TextEncoding.UTF_8);
        //submit.setText("Hello World");

        // Get file to be posted
        HttpClient client = new DefaultHttpClient();

        client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        client.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);

        // convert request into a byte array
        //byte[] data = SxmpWriter.createByteArray(submit);


        //logger.debug(StringUtil.toHexString(data));

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
