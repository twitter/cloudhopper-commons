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

import com.cloudhopper.commons.util.HexUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class PostUTF8MO {
    private static final Logger logger = LoggerFactory.getLogger(PostUTF8MO.class);

    static public void main(String[] args) throws Exception {

        String URL = "https://sms-staging.twitter.com/receive/cloudhopper";
        
        
        // this is a Euro currency symbol
        //String text = "\u20AC";

        // shorter arabic
        //String text = "\u0623\u0647\u0644\u0627";

        // even longer arabic
        //String text = "\u0623\u0647\u0644\u0627\u0020\u0647\u0630\u0647\u0020\u0627\u0644\u062a\u062c\u0631\u0628\u0629\u0020\u0627\u0644\u0623\u0648\u0644\u0649";

        String text = "";
        for (int i = 0; i < 140; i++) {
            text += "\u0623";
        }

        String srcAddr = "+14159129228";

        String ticketId = System.currentTimeMillis()+"";
        String operatorId = "23";

        //text += " " + ticketId;

        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
            //.append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"twitter\" password=\"4jd6781uy\"/>\n")
            .append(" <deliverRequest>\n")
            .append("  <ticketId>" + ticketId + "</ticketId>\n")
            .append("  <operatorId>" + operatorId + "</operatorId>\n")
            .append("  <sourceAddress type=\"international\">" + srcAddr  + "</sourceAddress>\n")
            .append("  <destinationAddress type=\"network\">40404</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">" + HexUtil.toHexString(text.getBytes("UTF-8")) + "</text>\n")
            .append(" </deliverRequest>\n")
            .append("</operation>\n")
            .append("");
        
        // Get target URL
        //String strURL = "http://68.64.54.17/api/sxmp/1.0";

        // Get file to be posted
        //String strXMLFilename = args[1];
        //File input = new File(strXMLFilename);

        HttpClient client = new DefaultHttpClient();
        client.getParams().setBooleanParameter("http.protocol.expect-continue", false);

        long start = System.currentTimeMillis();

        // execute request
        try {
            HttpPost post = new HttpPost(URL);

            StringEntity entity = new StringEntity(string0.toString(), "ISO-8859-1");
            entity.setContentType("text/xml; charset=\"ISO-8859-1\"");
            post.setEntity(entity);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            String responseBody = client.execute(post, responseHandler);

            logger.debug("----------------------------------------");
            logger.debug(responseBody);
            logger.debug("----------------------------------------");
        } finally {
            // do nothing
        }

        long end = System.currentTimeMillis();

        logger.debug("Response took " + (end-start) + " ms");

    }
}
