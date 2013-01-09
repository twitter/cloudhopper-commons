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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Utility class for a simple way of sending a request and returning a response.
 *
 * @author joelauer
 */
public class SxmpSender {
    private static final Logger logger = Logger.getLogger(SxmpSender.class);

    static public SubmitResponse submit(String url, SubmitRequest request) throws UnsupportedEncodingException, SxmpErrorException, IOException, SxmpParsingException, SAXException, ParserConfigurationException {
        // send request and get the response
        Response response = send(url, request, true);

        if (response == null) {
            return null;
        }

        if (!(response instanceof SubmitResponse)) {
            throw new SxmpErrorException(SxmpErrorCode.OPTYPE_MISMATCH, "Unexpected response class type parsed");
        }

        return (SubmitResponse)response;
    }

    static public DeliverResponse deliver(String url, DeliverRequest request, boolean shouldParseResponse) throws UnsupportedEncodingException, SxmpErrorException, IOException, SxmpParsingException, SAXException, ParserConfigurationException {
        // send request and get the response
        Response response = send(url, request, shouldParseResponse);

        if (response == null) {
            return null;
        }

        if (!(response instanceof DeliverResponse)) {
            throw new SxmpErrorException(SxmpErrorCode.OPTYPE_MISMATCH, "Unexpected response class type parsed");
        }

        return (DeliverResponse)response;
    }

    static public Response send(String url, Request request, boolean shouldParseResponse) throws UnsupportedEncodingException, SxmpErrorException, IOException, SxmpParsingException, SAXException, ParserConfigurationException {
        // convert request into xml
        String requestXml = SxmpWriter.createString(request);
        String responseXml = null;

        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        client.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);

        long start = System.currentTimeMillis();
        long stop = 0;

        logger.debug("Request XML:\n" + requestXml);

        // execute request
        try {
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(requestXml);
            // write old or new encoding?
            if (request.getVersion().equals(SxmpParser.VERSION_1_1)) {
                // v1.1 is utf-8
                entity.setContentType("text/xml; charset=\"utf-8\"");
            } else {
                // v1.0 was 8859-1, though that's technically wrong
                // unspecified XML must be encoded in UTF-8
                // maintained this way for backward compatibility
                entity.setContentType("text/xml; charset=\"iso-8859-1\"");
            }
            post.setEntity(entity);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            // execute request (will throw exception if fails)
            responseXml = client.execute(post, responseHandler);

            stop = System.currentTimeMillis();
        } finally {
            // clean up all resources
            client.getConnectionManager().shutdown();
        }

        logger.debug("Response XML:\n" + responseXml);
        logger.debug("Response Time: " + (stop - start) + " ms");

        // deliver responses sometimes aren't parseable since its acceptable
        // for delivery responses to merely return "OK" and an HTTP 200 error
        if (!shouldParseResponse) {
            return null;
        } else {
            // convert response xml into an object
            SxmpParser parser = new SxmpParser(SxmpParser.VERSION_1_0);
            // v1.0 data remains in ISO-8859-1, and responses are v1.0
            ByteArrayInputStream bais = new ByteArrayInputStream(responseXml.getBytes("ISO-8859-1"));
            Operation op = parser.parse(bais);

            if (!(op instanceof Response)) {
                throw new SxmpErrorException(SxmpErrorCode.OPTYPE_MISMATCH, "Unexpected response class type parsed");
            }

            return (Response)op;
        }
    }

}
