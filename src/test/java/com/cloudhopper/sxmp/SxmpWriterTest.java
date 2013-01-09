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

// third party imports
import com.cloudhopper.commons.util.HexUtil;
import com.cloudhopper.stratus.type.OptionalParamMap;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.*;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

// my imports

/**
 *
 * @author joelauer
 */
public class SxmpWriterTest {
    private static final Logger logger = Logger.getLogger(SxmpWriterTest.class);

    @Test(expected=NullPointerException.class)
    public void writeNullOperation() throws Exception {
        SxmpWriter.write(new StringWriter(), (Operation)null);
    }

    @Test
    public void writeSubmitResponse() throws Exception {
        SubmitResponse submitResponse = new SubmitResponse();
        submitResponse.setErrorCode(5);
        submitResponse.setErrorMessage("Success");
        submitResponse.setTicketId("THISISTICKET");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, submitResponse);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <submitResponse>\n")
            .append("   <error code=\"5\" message=\"Success\"/>\n")
            .append("   <ticketId>THISISTICKET</ticketId>\n")
            .append(" </submitResponse>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeDeliverResponse() throws Exception {
        DeliverResponse deliverResponse = new DeliverResponse();
        deliverResponse.setErrorCode(5);
        deliverResponse.setErrorMessage("Success");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, deliverResponse);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <deliverResponse>\n")
            .append("   <error code=\"5\" message=\"Success\"/>\n")
            .append(" </deliverResponse>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeDeliveryReportResponse() throws Exception {
        DeliveryReportResponse deliveryResponse = new DeliveryReportResponse();
        deliveryResponse.setErrorCode(5);
        deliveryResponse.setErrorMessage("Success");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, deliveryResponse);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliveryReport\">\n")
            .append(" <deliveryReportResponse>\n")
            .append("   <error code=\"5\" message=\"Success\"/>\n")
            .append(" </deliveryReportResponse>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeSubmitRequestWithMissingAccount() throws Exception {
        SubmitRequest request = new SubmitRequest();
        //request.setAccount(new Account("customer1", "test1"));
        request.setOperatorId(20);
        request.setReferenceId("TESTREF");
        request.setSourceAddress(new MobileAddress(MobileAddress.Type.NETWORK, "40404"));
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+13135551212"));
        request.setText("Hello World");

        StringWriter sw = new StringWriter();

        try {
            SxmpWriter.write(sw, request);
            Assert.fail();
        } catch (SxmpErrorException e) {
            Assert.assertEquals(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, e.getErrorCode());
        }
    }

    @Test
    public void writeSubmitRequest0() throws Exception {
        SubmitRequest request = new SubmitRequest();
        request.setAccount(new Account("customer1", "test1"));
        request.setOperatorId(20);
        request.setReferenceId("TESTREF");
        request.setSourceAddress(new MobileAddress(MobileAddress.Type.NETWORK, "40404"));
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+13135551212"));
        request.setText("Hello World");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"TESTREF\">\n")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <deliveryReport>false</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+13135551212</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">48656C6C6F20576F726C64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeSubmitRequestWithLatin1() throws Exception {
        SubmitRequest request = new SubmitRequest();
        request.setAccount(new Account("customer1", "test1"));
        request.setOperatorId(20);
        request.setReferenceId("TESTREF");
        request.setSourceAddress(new MobileAddress(MobileAddress.Type.NETWORK, "40404"));
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+13135551212"));
        request.setText("Hello World", TextEncoding.ISO_8859_1);

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"TESTREF\">\n")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <deliveryReport>false</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+13135551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656C6C6F20576F726C64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeSubmitRequestWithUnicodeCharsInUTF8() throws Exception {
        SubmitRequest request = new SubmitRequest();
        request.setAccount(new Account("customer1", "test1"));
        request.setOperatorId(20);
        request.setReferenceId("TESTREF");
        request.setSourceAddress(new MobileAddress(MobileAddress.Type.NETWORK, "40404"));
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+13135551212"));
        request.setTextEncoding(TextEncoding.UTF_8);
        String text = "\u20AC\u0623\u0647\u0644\u0627\u0020\u0647\u0630\u0647\u0020\u0627\u0644\u062a\u062c\u0631\u0628\u0629\u0020\u0627\u0644\u0623\u0648\u0644\u0649";
        request.setText(text);

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"TESTREF\">\n")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <deliveryReport>false</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+13135551212</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">E282ACD8A3D987D984D8A720D987D8B0D98720D8A7D984D8AAD8ACD8B1D8A8D8A920D8A7D984D8A3D988D984D989</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeDeliverRequest0() throws Exception {
        DeliverRequest request = new DeliverRequest();
        request.setAccount(new Account("customer1", "test1"));
        request.setOperatorId(20);
        request.setReferenceId("TESTREF");
        request.setSourceAddress(new MobileAddress(MobileAddress.Type.NETWORK, "40404"));
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+13135551212"));
        request.setText("Hello World");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliverRequest referenceId=\"TESTREF\">\n")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+13135551212</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">48656C6C6F20576F726C64</text>\n")
            .append(" </deliverRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeDeliverRequestWithTicketId0() throws Exception {
        DeliverRequest request = new DeliverRequest();
        request.setAccount(new Account("customer1", "test1"));
        request.setOperatorId(20);
        request.setTicketId("THISISATESTTICKETID");
        request.setReferenceId("TESTREF");
        request.setSourceAddress(new MobileAddress(MobileAddress.Type.NETWORK, "40404"));
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+13135551212"));
        request.setText("Hello World");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliverRequest referenceId=\"TESTREF\">\n")
            .append("  <ticketId>THISISATESTTICKETID</ticketId>")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+13135551212</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">48656C6C6F20576F726C64</text>\n")
            .append(" </deliverRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeDeliveryReportRequest0() throws Exception {
        DeliveryReportRequest request = new DeliveryReportRequest();
        request.setAccount(new Account("customer1", "test1"));
        //request.setOperatorId(20);
        request.setReferenceId("TESTREF");
        request.setStatus(new DeliveryStatus(5, "Expired"));
        request.setTicketId("000:20090118002220948:000");
        //request.setSourceAddress(new MobileAddress(MobileAddress.Type.NETWORK, "40404"));
        //request.setDestinationAddress(new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+13135551212"));
        //request.setText("Hello World");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliveryReport\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliveryReportRequest referenceId=\"TESTREF\">\n")
            .append("   <status code=\"5\" message=\"Expired\"/>\n")
            .append("   <ticketId>000:20090118002220948:000</ticketId>\n")
            .append(" </deliveryReportRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeDeliveryReportRequestWithCreateFinalMessageErrorCode() throws Exception {
        DeliveryReportRequest request = new DeliveryReportRequest();
        request.setAccount(new Account("customer1", "test1"));
        request.setReferenceId("TESTREF");
        request.setStatus(new DeliveryStatus(5, "EXPIRED"));
        request.setTicketId("000:20090118002220948:000");
        // new properties in version 1.2
        request.setCreateDate(new DateTime(2010,5,30,9,30,10,0, DateTimeZone.UTC));
        request.setFinalDate(new DateTime(2010,5,30,9,30,15,314, DateTimeZone.UTC));
        request.setMessageErrorCode(new Integer(102));

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliveryReport\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliveryReportRequest referenceId=\"TESTREF\">\n")
            .append("   <status code=\"5\" message=\"EXPIRED\"/>\n")
            .append("   <ticketId>000:20090118002220948:000</ticketId>\n")
            .append("   <messageError code=\"102\"/>\n")
            .append("   <createDate>2010-05-30T09:30:10.000Z</createDate>\n")
            .append("   <finalDate>2010-05-30T09:30:15.314Z</finalDate>\n")
            .append(" </deliveryReportRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeSubmitRequestWithAlphanumeric() throws Exception {
        SubmitRequest request = new SubmitRequest();
        request.setAccount(new Account("customer1", "test1"));
        request.setOperatorId(20);
        request.setSourceAddress(new MobileAddress(MobileAddress.Type.ALPHANUMERIC, "TestAlpha"));
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+13135551212"));
        request.setText("Hello World");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest>\n")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <deliveryReport>false</deliveryReport>\n")
            .append("  <sourceAddress type=\"alphanumeric\">TestAlpha</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+13135551212</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">48656C6C6F20576F726C64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeSubmitRequestWithLongPushDestination() throws Exception {
        SubmitRequest request = new SubmitRequest(SxmpParser.VERSION_1_1);
        request.setAccount(new Account("customer1", "test1"));
        request.setOperatorId(20);
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.PUSH_DESTINATION, "abcd1234fghi 012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"));
        request.setText("Hello World");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest>\n")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <deliveryReport>false</deliveryReport>\n")
            .append("  <destinationAddress type=\"push_destination\">abcd1234fghi 012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">48656C6C6F20576F726C64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
        // guarantee there's no optionalParams entry
        Assert.assertTrue(!expectedXML.toString().contains("optionalParams"));
    }

    @Test
    public void writeSubmitRequestWithUTF8PushDestination() throws Exception {
        SubmitRequest request = new SubmitRequest(SxmpParser.VERSION_1_1);
        request.setAccount(new Account("customer1", "test1"));
        request.setOperatorId(20);
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.PUSH_DESTINATION, "abcd\n1234\rfghi-€£æ_\u20AC\u0623\u0647\u0644"));
        request.setText("Hello World");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug("UTF8 PUSH DEST: "+sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest>\n")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <deliveryReport>false</deliveryReport>\n")
            .append("  <destinationAddress type=\"push_destination\">abcd&#10;1234&#13;fghi-€£æ_\u20AC\u0623\u0647\u0644</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">48656C6C6F20576F726C64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
        // verify there's no optionalParams entry
        Assert.assertTrue(!expectedXML.toString().contains("optionalParams"));
    }

    @Test
    public void writeSubmitRequestWithOptionalParams() throws Exception {
        SubmitRequest request = new SubmitRequest(SxmpParser.VERSION_1_1);
        request.setAccount(new Account("customer1", "test1"));
        request.setOperatorId(20);
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.PUSH_DESTINATION, "abcd1234fghi"));
        request.setText("Hello World");
        // use a tree map to get guaranteed key order
        OptionalParamMap optParams = new OptionalParamMap(OptionalParamMap.TREE_MAP);
        optParams.put("A", new Integer(42));
        optParams.put("b", "value with unicode and UTF8 extended chars: € £ æ - \u20AC \u0623 \u0647 \u0644 ");
        optParams.put("c", "'sample' with XML-excaping: \n&\r<>'\"");
        optParams.put("e", new Integer(-42));
        optParams.put("f", new Double(3.14159));
        optParams.put("g", new Integer(33445566));
        optParams.put("h", new Long(123456789123456l));
        request.setOptionalParams(optParams);

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug("UTF8 OPTIONAL PARAMS: "+sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest>\n")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <deliveryReport>false</deliveryReport>\n")
            .append("  <destinationAddress type=\"push_destination\">abcd1234fghi</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">48656C6C6F20576F726C64</text>\n")
            // for some unknown reason, JSONObject writes the unicode-encoding of € (\u20ac), but keeps the others as single unicode chars
            .append("  <optionalParams>{\"A\":42,\"b\":\"value with unicode and UTF8 extended chars: \\u20ac £ æ - \\u20ac \u0623 \u0647 \u0644 \",\"c\":\"'sample' with XML-excaping: \\n&amp;\\r&lt;&gt;'\\\"\",\"e\":-42,\"f\":3.14159,\"g\":33445566,\"h\":123456789123456}</optionalParams>")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeDeliverRequestWithAlphanumeric() throws Exception {
        DeliverRequest request = new DeliverRequest();
        request.setAccount(new Account("customer1", "test1"));
        request.setOperatorId(20);
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.ALPHANUMERIC, "TestAlpha"));
        request.setSourceAddress(new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+13135551212"));
        request.setText("Hello World");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliverRequest>\n")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <sourceAddress type=\"international\">+13135551212</sourceAddress>\n")
            .append("  <destinationAddress type=\"alphanumeric\">TestAlpha</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">48656C6C6F20576F726C64</text>\n")
            .append(" </deliverRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeSubmitRequestWithNationalAndApplication() throws Exception {
        SubmitRequest request = new SubmitRequest();
        request.setAccount(new Account("customer1", "test1"));
        request.setApplication(new Application("TestApp"));
        request.setOperatorId(20);
        request.setSourceAddress(new MobileAddress(MobileAddress.Type.NATIONAL, "0123456789"));
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.INTERNATIONAL, "+13135551212"));
        request.setText("Hello World");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <application>TestApp</application>\n")
            .append(" <submitRequest>\n")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <deliveryReport>false</deliveryReport>\n")
            .append("  <sourceAddress type=\"national\">0123456789</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+13135551212</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">48656C6C6F20576F726C64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

    @Test
    public void writeDeliverRequestWithNationalAndApplication() throws Exception {
        DeliverRequest request = new DeliverRequest();
        request.setAccount(new Account("customer1", "test1"));
        request.setApplication(new Application("TestApp"));
        request.setOperatorId(20);
        request.setDestinationAddress(new MobileAddress(MobileAddress.Type.ALPHANUMERIC, "TestAlpha"));
        request.setSourceAddress(new MobileAddress(MobileAddress.Type.NATIONAL, "0123456789"));
        request.setText("Hello World");

        StringWriter sw = new StringWriter();
        SxmpWriter.write(sw, request);

        logger.debug(sw.toString());

        StringBuilder expectedXML = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <application>TestApp</application>\n")
            .append(" <deliverRequest>\n")
            .append("  <operatorId>20</operatorId>\n")
            .append("  <sourceAddress type=\"national\">0123456789</sourceAddress>\n")
            .append("  <destinationAddress type=\"alphanumeric\">TestAlpha</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">48656C6C6F20576F726C64</text>\n")
            .append(" </deliverRequest>\n")
            .append("</operation>\n")
            .append("");

        // compare to actual correct submit response
        XMLUnit.setIgnoreWhitespace(true);
        Diff myDiff = new Diff(expectedXML.toString(), sw.toString());
        DetailedDiff myDetailedDiff = new DetailedDiff(myDiff);
        Assert.assertTrue("XML are similar " + myDetailedDiff, myDetailedDiff.similar());
    }

}
