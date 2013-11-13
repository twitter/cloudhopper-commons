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
            //.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"twitter\" password=\"4jd6781uy\"/>\n")
            .append(" <deliveryReportRequest>\n")
            .append("  <ticketId>" + ticketId + "</ticketId>\n")
            .append(" </deliveryReportRequest>\n")
            .append("</operation>\n")
            .append("");

            /**
            //.append("<!DOCTYPE chapter PUBLIC \"-//OASIS//DTD DocBook XML//EN\" \"../dtds/docbookx.dtd\">")
            //.append("<!DOCTYPE chapter PUBLIC \"-//OASIS//DTD DocBook XML//EN\">")
            .append("<submitRequest sequenceId=\"1000\">\n")
            .append("   <!-- this is a comment -->\n")
            .append("   <account username=\"testaccount\" password=\"testpassword\"/>\n")
            .append("   <option />\n")
            .append("   <messageRequest referenceId=\"MYMESSREF\">\n")
            //.append("       <sourceAddress>+13135551212</sourceAddress>\n")
            .append("       <destinationAddress>+13135551200</destinationAddress>\n")
            .append("       <text><![CDATA[Hello World]]></text>\n")
            .append("   </messageRequest>\n")
            .append("</submitRequest>")
            .append("");
             */
        
        // Get target URL
        //String strURL = "http://68.64.54.17/api/sxmp/1.0";

        // Get file to be posted
        //String strXMLFilename = args[1];
        //File input = new File(strXMLFilename);

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
