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
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;

/**
 * Post class to send a HTTP POST request.
 * Optimized for better resource management and performance.
 */
public class Post {
    private static final Logger logger = LoggerFactory.getLogger(Post.class);

    public static void main(String[] args) {
        // Message to be sent
        String message = "Test With @ Character";

        // Build XML payload with message encoded in ISO-8859-1
        String xmlPayload = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"password1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>75</operatorId>\n")
            .append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+13135551234</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">" + HexUtil.toHexString(message.getBytes(StandardCharsets.ISO_8859_1)) + "</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .toString();

        // Target URL for the POST request
        String strURL = "http://localhost:9080/api/sxmp/1.0";

        // Use try-with-resources to ensure HttpClient is closed properly
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            long totalStart = System.currentTimeMillis();  // Track total start time

            // Create and configure HttpPost object
            HttpPost post = new HttpPost(strURL);
            StringEntity entity = new StringEntity(xmlPayload, StandardCharsets.ISO_8859_1);
            entity.setContentType("text/xml; charset=ISO-8859-1");
            post.setEntity(entity);

            long start = System.currentTimeMillis();  // Track request start time

            // Execute HTTP POST request and measure the response time
            try (CloseableHttpResponse response = client.execute(post)) {
                long stop = System.currentTimeMillis();  // Track request stop time

                // Log response details
                logger.debug("----------------------------------------");
                logger.debug("Response took " + (stop - start) + " ms");
                logger.debug("Response: {}", response.getEntity().getContent().toString());
                logger.debug("----------------------------------------");
            }

            long totalEnd = System.currentTimeMillis();  // Track total end time
            logger.debug("Total response time: " + (totalEnd - totalStart) + " ms");

        } catch (Exception e) {
            // Log any exceptions that occur during the request
            logger.error("Error occurred while sending HTTP POST request", e);
        }
    }
}
