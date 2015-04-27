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

import java.io.ByteArrayInputStream;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author joelauer
 */
public class SxmpSessionTest {
    private static final Logger logger = LoggerFactory.getLogger(SxmpSessionTest.class);

    private static class MockSxmpRequestProcessor implements SxmpProcessor {
        public int authenticates = 0;
        public int submits = 0;
        public int delivers = 0;
        public int deliveryReports = 0;
        public boolean authenticate(Account account) throws SxmpErrorException {
            authenticates++;
            return true;
        }

        public SubmitResponse submit(Account account, SubmitRequest submitRequest) throws SxmpErrorException {
            submits++;
            SubmitResponse submitResponse = submitRequest.createResponse();
            submitResponse.setTicketId("THISISATICKET");
            return submitResponse;
        }

        public DeliverResponse deliver(Account account, DeliverRequest deliverRequest) throws SxmpErrorException {
            delivers++;
            return deliverRequest.createResponse();
        }

        public DeliveryReportResponse deliveryReport(Account account, DeliveryReportRequest deliveryRequest) throws SxmpErrorException {
            deliveryReports++;
            return deliveryRequest.createResponse();
        }
    }

    @Test
    public void processThrowsSAXParsingException() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append("</operation2>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        MockSxmpRequestProcessor processor = new MockSxmpRequestProcessor();
        SxmpSession session = new SxmpSession(processor, SxmpParser.VERSION_1_0);

        Response response = null;
        try {
            response = session.process(is);
            Assert.fail();
        } catch (SAXParseException e) {
            // correct behavior
        }
    }

    @Test
    public void processThrowsSxmpParsingExceptionWithNoOperationType() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation2 type=\"submit\">\n")
            .append("</operation2>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        MockSxmpRequestProcessor processor = new MockSxmpRequestProcessor();
        SxmpSession session = new SxmpSession(processor, SxmpParser.VERSION_1_0);

        Response response = null;
        try {
            response = session.process(is);
            Assert.fail();
        } catch (SAXException e) {
            // correct behavior
            //Assert.assertEquals(SxmpErrorCode.INVALID_XML, e.getErrorCode());
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Root element must be an [operation]"));
            //Assert.assertNull(e.getOperation());
        }
    }

    @Test
    public void processResponseThrowsUnsupportedOperation() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <submitResponse>\n")
            .append("   <error code=\"0\" message=\"OK\"/>\n")
            .append("   <ticketId>000:20090118002220948:000</ticketId>\n")
            .append(" </submitResponse>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        MockSxmpRequestProcessor processor = new MockSxmpRequestProcessor();
        SxmpSession session = new SxmpSession(processor, SxmpParser.VERSION_1_0);

        // this should actually not throw an error and should be an error response
        ErrorResponse response = (ErrorResponse)session.process(is);

        Assert.assertEquals(Operation.Type.SUBMIT, response.getType());
        Assert.assertEquals(SxmpErrorCode.UNSUPPORTED_OPERATION.getIntValue(), response.getErrorCode().intValue());
        //Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Root element must be an [operation]"));
    }

    @Test
    public void processResponseAuthenticationReturnsFalse() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>10</operatorId>\n")
            .append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        SxmpProcessor processor = new SxmpProcessor() {
            public boolean authenticate(Account account) throws SxmpErrorException {
                return false;
            }

            public SubmitResponse submit(Account account, SubmitRequest submitRequest) throws SxmpErrorException {
                return null;
            }

            public DeliverResponse deliver(Account account, DeliverRequest deliverRequest) throws SxmpErrorException {
                return null;
            }

            public DeliveryReportResponse deliveryReport(Account account, DeliveryReportRequest deliveryRequest) throws SxmpErrorException {
                return null;
            }
        };

        SxmpSession session = new SxmpSession(processor, SxmpParser.VERSION_1_0);

        // this should actually not throw an error and should be an error response
        ErrorResponse response = (ErrorResponse)session.process(is);

        Assert.assertEquals(Operation.Type.SUBMIT, response.getType());
        Assert.assertEquals(SxmpErrorCode.AUTHENTICATION_FAILURE.getIntValue(), response.getErrorCode().intValue());
        //Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Root element must be an [operation]"));
    }

    @Test
    public void processResponseAuthenticationThrowsException() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>10</operatorId>\n")
            .append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        SxmpProcessor processor = new SxmpProcessor() {
            public boolean authenticate(Account account) throws SxmpErrorException {
                throw new SxmpErrorException(SxmpErrorCode.EMPTY_VALUE, "Unable to correctly get database connection");
            }

            public SubmitResponse submit(Account account, SubmitRequest submitRequest) throws SxmpErrorException {
                return null;
            }

            public DeliverResponse deliver(Account account, DeliverRequest deliverRequest) throws SxmpErrorException {
                return null;
            }

            public DeliveryReportResponse deliveryReport(Account account, DeliveryReportRequest deliveryRequest) throws SxmpErrorException {
                return null;
            }
        };

        SxmpSession session = new SxmpSession(processor, SxmpParser.VERSION_1_0);

        // this should actually not throw an error and should be an error response
        ErrorResponse response = (ErrorResponse)session.process(is);

        Assert.assertEquals(Operation.Type.SUBMIT, response.getType());
        Assert.assertEquals(SxmpErrorCode.EMPTY_VALUE.getIntValue(), response.getErrorCode().intValue());
        //Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Root element must be an [operation]"));
    }

    @Test
    public void processSubmitThrowsUncaughtException() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>10</operatorId>\n")
            .append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        SxmpProcessor processor = new SxmpProcessor() {
            public boolean authenticate(Account account) throws SxmpErrorException {
                return true;
            }

            public SubmitResponse submit(Account account, SubmitRequest submitRequest) throws SxmpErrorException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public DeliverResponse deliver(Account account, DeliverRequest deliverRequest) throws SxmpErrorException {
                return null;
            }

            public DeliveryReportResponse deliveryReport(Account account, DeliveryReportRequest deliveryRequest) throws SxmpErrorException {
                return null;
            }


        };

        SxmpSession session = new SxmpSession(processor, SxmpParser.VERSION_1_0);

        // this should actually not throw an error and should be an error response
        ErrorResponse response = (ErrorResponse)session.process(is);
        Assert.assertEquals(Operation.Type.SUBMIT, response.getType());
        Assert.assertEquals(SxmpErrorCode.GENERIC.getIntValue(), response.getErrorCode().intValue());
    }

    @Test
    public void processSubmitOK() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>10</operatorId>\n")
            .append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        MockSxmpRequestProcessor processor = new MockSxmpRequestProcessor();
        SxmpSession session = new SxmpSession(processor, SxmpParser.VERSION_1_0);

        // this should actually not throw an error and should be an error response
        SubmitResponse response = (SubmitResponse)session.process(is);

        Assert.assertEquals(Operation.Type.SUBMIT, response.getType());
        Assert.assertEquals(0, response.getErrorCode().intValue());
        Assert.assertEquals("OK", response.getErrorMessage());
        Assert.assertEquals("THISISATICKET", response.getTicketId());
    }
}
