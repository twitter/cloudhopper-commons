package com.cloudhopper.sxmp.servlet;

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

import com.cloudhopper.sxmp.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * SXMP processor used with servlets for delegating responsibility of handling
 * a request.  This processor automatically handles any version of an incoming
 * SXMP request.
 *
 * @author joelauer
 */
public class SxmpServletProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SxmpServletProcessor.class);
    private static final Marker fatal = MarkerFactory.getMarker("FATAL");

    public SxmpServletProcessor() {
        // do nothing
    }

    public void process(SxmpProcessor processor, HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        try {
            // first, we will assume the response will be correct and will be text/xml
            // responses are sent in SXMP v1.0, which is still ISO-8859-1
            response.setContentType("text/xml; charset=\"iso-8859-1\"");

            // process it -- if no exception thrown, then xml was already written to outputstream
            doProcess(processor, request.getInputStream(), out, request.getRequestURI(), request.getMethod(), request.getContentType());

            // if we get here, then return HTTP OK status
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (HttpStatusCodeException e) {
            logger.warn("Bad HTTP request, return non-200 status code: " + e.getMessage());
            response.setContentType("text/html");
            response.setStatus(e.getStatusCode());
            out.println(e.getMessage());
            return;
        } catch (Throwable t) {
            logger.error("Uncaught exception during SXMP doProcess", t);
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(t.getMessage());
            return;
        }
    }

    public static void doProcess(SxmpProcessor processor, InputStream in, PrintWriter out, String uri, String method, String contentType) throws IOException, HttpStatusCodeException {
        // validate the user went to the correct URL
        // E.g. /api/sxmp/1.0
        // find the next position of last / (to extract version)
        int posOfVersion = uri.lastIndexOf('/');
        if (posOfVersion < 0 || posOfVersion+1 >= uri.length()) {
            throw new HttpStatusCodeException(HttpServletResponse.SC_NOT_FOUND, "Bad URL used, could not extract version or no data for version");
        }

        String version = uri.substring(posOfVersion+1);
        //logger.debug("Parsed API Version: " + version);

        // check if the version is supported
        if (!version.equals(SxmpParser.VERSION_1_0) && !version.equals(SxmpParser.VERSION_1_1)) {
            throw new HttpStatusCodeException(HttpServletResponse.SC_BAD_REQUEST, "Unsupported API version in URL");
        }

        // validate the user did a POST
        if (!method.equalsIgnoreCase("POST")) {
            throw new HttpStatusCodeException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Only HTTP POST methods are acceptable");
        }

        // validate the user posted "text/xml"
        //logger.debug("Request contentType: " + contentType);
        if (contentType == null || !contentType.toLowerCase().startsWith("text/xml")) {
            throw new HttpStatusCodeException(HttpServletResponse.SC_BAD_REQUEST, "Unsupported Content-Type HTTP Header - Must Be text/xml");
        }

        // create a new session tied to this processor
        SxmpSession session = new SxmpSession(processor, version);

        Response response = null;
        try {
            // process request, get response
            response = session.process(in);
        } catch (Exception e) {
            // any exception thrown in process() should generate a non-200 HTTP status code
            // the exception also would have already been logged in the SxmpSession, so we
            // won't print it here and duplicate it -- any error during processing
            // would actually have returned an ErrorResponse
            throw new HttpStatusCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        // at this point, we should have a response
        if (response == null) {
            logger.error(fatal, "The response from SxmpSession.process() was null -- should be impossible");
            throw new HttpStatusCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Response was empty");
        }

        try {
            // if we get here, write a response
            SxmpWriter.write(out, response);
        } catch (SxmpErrorException e) {
            logger.error("Error while writing response", e);
            throw new HttpStatusCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to cleanly write response to OutputStream");
        }
    }
}
