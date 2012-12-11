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

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author joelauer
 */
public class SxmpSession {
    private static final Logger logger = Logger.getLogger(SxmpSession.class);

    private final SxmpProcessor processor;
    private final String version;

    // backwards-compatible version
    /*
    public SxmpSession(final SxmpProcessor processor) {
        this.processor = processor;
        this.version = SxmpParser.VERSION_1_0;
    }
    */
    public SxmpSession(final SxmpProcessor processor, final String version) {
        this.processor = processor;
        this.version = version;
    }

    /**
     * Processes an InputStream that contains a request.  Does its best to
     * only produce a Response that can be written to an OutputStream. Any
     * exception this method throws should be treated as fatal and no attempt
     * should be made to print out valid XML as a response.
     * @param is The InputStream to read the Request from
     * @return A Response that can be written to an OutputStream via an SxmpWriter
     * @throws IOException Thrown if there is an error while reading the InputStream
     * @throws SAXException Thrown if there is an error with parsing the XML document
     * @throws ParserConfigurationException Thrown if there is an error with loading
     *      the XML parser.
     */
    public Response process(InputStream is) throws IOException, SAXException, ParserConfigurationException {
        // create a new XML parser
        SxmpParser parser = new SxmpParser(version);

        // an instance of an operation we'll be processing as a request
        Operation operation = null;

        try {
            // parse input stream into an operation (this may
            operation = parser.parse(is);
        } catch (SxmpParsingException e) {
            // major issue parsing the request into something valid -- this
            // exception may contain a partially parsed operation -- if it does
            // then we want to return valid XML back to the caller of this session
            if (e.getOperation() != null && e.getOperation().getType() != null) {
                logger.warn("Unable to fully parse XML into a request, returning ErrorResponse", e);
                // we'll actually return a generic ErrorResponse back
                return new ErrorResponse(e.getOperation().getType(), e.getErrorCode().getIntValue(), e.getErrorMessage());
            } else {
                // otherwise, we should just return a generic error since nothing
                // really was parsed in the XML document
                throw new SAXException(e.getMessage(), e);
            }
        }

        // at this point, we'll catch any SxmpErrorExceptions and make sure they
        // are always converted into an ErrorResponse object, rather than
        // the exception ever being thrown
        try {
            // can only handle requests
            if (!(operation instanceof Request)) {
                throw new SxmpErrorException(SxmpErrorCode.UNSUPPORTED_OPERATION, "A session can only process requests");
            }

            // convert to a request
            Request req = (Request)operation;

            // was an account included?
            if (req.getAccount() == null) {
                throw new SxmpErrorException(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, "A request must include account credentials");
            }

            // authenticate the request
            if (!processor.authenticate(req.getAccount())) {
                throw new SxmpErrorException(SxmpErrorCode.AUTHENTICATION_FAILURE, "Authentication failure");
            }

            // handle request type
            if (operation instanceof SubmitRequest) {
                return processor.submit(req.getAccount(), (SubmitRequest)operation);
            } else if (operation instanceof DeliverRequest) {
                return processor.deliver(req.getAccount(), (DeliverRequest)operation);
            } else if (operation instanceof DeliveryReportRequest) {
                return processor.deliveryReport(req.getAccount(), (DeliveryReportRequest)operation);
            } else {
                // if we got here, then a request we don't support occurred
                throw new SxmpErrorException(SxmpErrorCode.UNSUPPORTED_OPERATION, "Unsupported operation request type");
            }
        } catch (SxmpErrorException e) {
            // because this is a mostly normal error in the course of processing a message
            // we don't want to print the full stacktrace -- we just want to print the message
            logger.warn(e.getMessage());
            // we'll actually return a generic ErrorResponse back
            return new ErrorResponse(operation.getType(), e.getErrorCode().getIntValue(), e.getErrorMessage());
        } catch (Throwable t) {
            logger.error("Major uncaught throwable while processing request, generating an ErrorResponse", t);
            // we'll actually return a generic ErrorResponse back
            return new ErrorResponse(operation.getType(), SxmpErrorCode.GENERIC.getIntValue(), "Generic error while processing request");
        }
    }

}
