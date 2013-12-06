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
public class PostReport {
    private static final Logger logger = LoggerFactory.getLogger(PostReport.class);

    static public void main(String[] args) throws Exception {

        String URL = "https://sms.twitter.com/receive/cloudhopper";

        String ticketId = "testticket";

        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"password1\"/>\n")
            .append(" <deliveryReportRequest>\n")
            .append("  <ticketId>" + ticketId + "</ticketId>\n")
            .append(" </deliveryReportRequest>\n")
            .append("</operation>\n")
            .append("");

        /**
        // Prepare HTTP post
        PostMethod post = new PostMethod(URL);

        // Request content will be retrieved directly
        // from the input stream
        RequestEntity entity = new StringRequestEntity(string0.toString(), "text/xml", "ISO-8859-1");
        
        post.setRequestEntity(entity);

        // Get HTTP client
        HttpClient httpclient = new HttpClient();

        // Execute request
        try {
            int result = httpclient.executeMethod(post);
            // Display status code
            System.out.println("Response status code: " + result);
            // Display response
            System.out.println("Response body: ");
            System.out.println(post.getResponseBodyAsString());
        } finally {
            // Release current connection to the connection pool once you are done
            post.releaseConnection();
        }
         */

    }
}
