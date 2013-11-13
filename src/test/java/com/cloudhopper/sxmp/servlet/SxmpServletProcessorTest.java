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

import com.cloudhopper.sxmp.Account;
import com.cloudhopper.sxmp.DeliverRequest;
import com.cloudhopper.sxmp.DeliverResponse;
import com.cloudhopper.sxmp.DeliveryReportRequest;
import com.cloudhopper.sxmp.DeliveryReportResponse;
import com.cloudhopper.sxmp.SubmitRequest;
import com.cloudhopper.sxmp.SubmitResponse;
import com.cloudhopper.sxmp.SxmpErrorCode;
import com.cloudhopper.sxmp.SxmpErrorException;
import com.cloudhopper.sxmp.SxmpProcessor;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.http.HttpStatus;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.hamcrest.Matcher;
import org.junit.*;
import org.junit.matchers.JUnitMatchers;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author joelauer
 */
public class SxmpServletProcessorTest {
    private static final Logger logger = LoggerFactory.getLogger(SxmpServletProcessorTest.class);

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void doProcessBadAPIVersion() throws Exception {
        SxmpServletProcessor processor = new SxmpServletProcessor();

        try {
            processor.doProcess(null, null, null, "/any/path/to/servlet/2.0", "POST", "text/xml");
        } catch (HttpStatusCodeException e) {
            // correct behavior
            Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, e.getStatusCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Unsupported API version"));
        }
    }

    @Test
    public void doProcessBadMethod() throws Exception {
        SxmpServletProcessor processor = new SxmpServletProcessor();
        try {
            processor.doProcess(null, null, null, "/api/sxmp/1.0", "GET", "text/xml");
        } catch (HttpStatusCodeException e) {
            // correct behavior
            Assert.assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getStatusCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Only HTTP POST method"));
        }
    }

    @Test
    public void doProcessBadURL() throws Exception {
        SxmpServletProcessor processor = new SxmpServletProcessor();
        try {
            processor.doProcess(null, null, null, "/", "POST", "text/xml");
        } catch (HttpStatusCodeException e) {
            // correct behavior
            Assert.assertEquals(HttpStatus.SC_NOT_FOUND, e.getStatusCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Bad URL used"));
        }
    }

    @Test
    public void doProcessNonXMLContentTypeHeader() throws Exception {
        SxmpServletProcessor processor = new SxmpServletProcessor();
        try {
            processor.doProcess(null, null, null, "/1.0", "POST", "text/html");
        } catch (HttpStatusCodeException e) {
            // correct behavior
            Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, e.getStatusCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Unsupported Content-Type"));
        }
    }

    @Test
    public void doProcessSubmitWithSxmpErrorThrown() throws Exception {
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
        StringWriter sw = new StringWriter();

        SxmpProcessor requestProcessor = new SxmpProcessor() {
            public boolean authenticate(Account account) throws SxmpErrorException {
                return true;
            }

            public SubmitResponse submit(Account account, SubmitRequest submitReq) throws SxmpErrorException {
                throw new SxmpErrorException(SxmpErrorCode.INVALID_VALUE, "Short code not allowed");
            }

            public DeliverResponse deliver(Account account, DeliverRequest deliverRequest) throws SxmpErrorException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public DeliveryReportResponse deliveryReport(Account account, DeliveryReportRequest deliveryRequest) throws SxmpErrorException {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        SxmpServletProcessor processor = new SxmpServletProcessor();
        processor.doProcess(requestProcessor, is, new PrintWriter(sw), "/1.0", "POST", "text/xml; charset=ISO-8859-1");

        logger.debug(sw.toString());
    }

    @Test
    public void doProcessDeliverWithUnsupportedSxmpErrorThrown() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliverRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>10</operatorId>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </deliverRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        StringWriter sw = new StringWriter();

        SxmpProcessor requestProcessor = new SxmpProcessor() {
            public boolean authenticate(Account account) throws SxmpErrorException {
                return true;
            }

            public SubmitResponse submit(Account account, SubmitRequest submitReq) throws SxmpErrorException {
                throw new SxmpErrorException(SxmpErrorCode.INVALID_VALUE, "Short code not allowed");
            }

            public DeliverResponse deliver(Account account, DeliverRequest deliverRequest) throws SxmpErrorException {
                throw new SxmpErrorException(SxmpErrorCode.INVALID_VALUE, "Short code not allowed");
            }

            public DeliveryReportResponse deliveryReport(Account account, DeliveryReportRequest deliveryRequest) throws SxmpErrorException {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        new SxmpServletProcessor().doProcess(requestProcessor, is, new PrintWriter(sw), "/1.0", "POST", "text/xml; charset=ISO-8859-1");

        // actual deliver request from Verizon
        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\" encoding=\"utf-8\"?><operation type=\"deliver\"><error code=\"1006\" message=\"Short code not allowed\"/></operation>");

        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("Deliver XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

}
