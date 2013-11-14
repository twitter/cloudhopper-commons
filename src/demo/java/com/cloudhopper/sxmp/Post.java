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
public class Post {
    private static final Logger logger = LoggerFactory.getLogger(Post.class);

    static public void main(String[] args) throws Exception {

        String message = "Test With @ Character";
	//String message = "Tell Twitter what you're doing!\nStd msg charges apply. Send 'stop' to quit.\nVisit twitter.com or email help@twitter.com for help.";

        StringBuilder string0 = new StringBuilder(200)
            //.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"twitter\" password=\"4jd6781uy\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            //.append(" <submitRequest>\n")
            .append("  <operatorId>75</operatorId>\n")
            .append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+131344434272</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">" + HexUtil.toHexString(message.getBytes("ISO-8859-1")) + "</text>\n")
            //.append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c6448656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
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
        //String strURL = "http://localhost:9080/api/sxmp/1.0";
        //String strURL = "http://68.64.54.17/api/sxmp/1.0";
        //String strURL = "http://localhost:9080/api/sxmp/1.0";
        //String strURL = "http://lyn-stratus-001/api/sxmp/1.0";
        //String strURL = "http://sfd-twtr-gw.cloudhopper.com/api/sxmp/1.0";
        String strURL = "http://lyn-twtr-gw.cloudhopper.com/api/sxmp/1.0";

        // Get file to be posted
        //String strXMLFilename = args[1];
        //File input = new File(strXMLFilename);

        HttpClient client = new DefaultHttpClient();

        long totalStart = System.currentTimeMillis();

        for (int i = 0; i < 1; i++) {
            long start = System.currentTimeMillis();

            // execute request
            try {
                HttpPost post = new HttpPost(strURL);

                StringEntity entity = new StringEntity(string0.toString(), "ISO-8859-1");
                entity.setContentType("text/xml; charset=\"ISO-8859-1\"");
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

        logger.debug("Response took " + (totalEnd-totalStart) + " ms");

    }
}
