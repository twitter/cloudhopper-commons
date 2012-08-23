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
import java.io.ByteArrayInputStream;
import org.junit.*;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.matchers.JUnitMatchers;
import org.xml.sax.SAXParseException;

// my imports

/**
 * 
 * @author joelauer
 */
public class SxmpParserTest {
    private static final Logger logger = Logger.getLogger(SxmpParserTest.class);

    @Test
    public void parseInvalidRootElement() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation2 type=\"submit\">\n")
            .append("</operation2>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
            // correct behavior
            Assert.assertEquals(SxmpErrorCode.INVALID_XML, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Root element must be an [operation]"));
            Assert.assertNull(e.getOperation());
        }
    }

    
    @Test
    public void parseUnsupportedOperationType() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit2\">\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
            // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNSUPPORTED_OPERATION, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Unsupported operation type"));
            Assert.assertNull(e.getOperation());
        }
    }


    @Test
    public void parseUnsupportedChildElement() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append("  <test></test>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
            // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNSUPPORTED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Unsupported [test] element"));
            // this should also contain an operation since the root element was parsed
            Assert.assertNotNull(e.getOperation());
            PartialOperation partial = (PartialOperation)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, partial.getType());
        }
    }


    @Test
    public void parseTwoAccounts() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <account username=\"customer2\" password=\"test2\"/>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
            // correct behavior
            Assert.assertEquals(SxmpErrorCode.MULTIPLE_ELEMENTS_NOT_SUPPORTED, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Multiple [account] elements are not supported"));
            // this should also contain an operation since the root element was parsed
            Assert.assertNotNull(e.getOperation());
            PartialOperation partial = (PartialOperation)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, partial.getType());
        }
    }


    @Test
    public void parseAccountOnly() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
            // correct behavior
            Assert.assertEquals(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The operation type [submit] requires a request element"));
            Assert.assertNotNull(e.getOperation());
            PartialOperation partial = (PartialOperation)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, partial.getType());
        }
    }


    @Test
    public void parseAccountWithMissingUsernameAttribute() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account password=\"test1\"/>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
            // correct behavior
            Assert.assertEquals(SxmpErrorCode.MISSING_REQUIRED_ATTRIBUTE, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The attribute [username] is required with the [account] element"));
            Assert.assertNotNull(e.getOperation());
            PartialOperation partial = (PartialOperation)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, partial.getType());
        }
    }


    @Test
    public void parseAccountWithMissingPasswordAttribute() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"test1\"/>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
            // correct behavior
            Assert.assertEquals(SxmpErrorCode.MISSING_REQUIRED_ATTRIBUTE, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The attribute [password] is required with the [account] element"));
            Assert.assertNotNull(e.getOperation());
            PartialOperation partial = (PartialOperation)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, partial.getType());
        }
    }


    @Test
    public void parseAccountWithEmptyUsernameAttribute() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"\" password=\"test1\"/>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
            // correct behavior
            Assert.assertEquals(SxmpErrorCode.EMPTY_VALUE, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The [username] attribute was empty in the [account] element"));
            Assert.assertNotNull(e.getOperation());
            PartialOperation partial = (PartialOperation)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, partial.getType());
        }
    }


    @Test
    public void parseOperationOnly() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
            // correct behavior
            Assert.assertEquals(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The operation type [submit] requires a request element"));
            Assert.assertNotNull(e.getOperation());
            PartialOperation partial = (PartialOperation)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, partial.getType());
        }
    }


    @Test
    public void parseOperationRequestMismatch() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>10</operatorId>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
            // correct behavior
            Assert.assertEquals(SxmpErrorCode.OPTYPE_MISMATCH, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The operation type [deliver] does not match the [submitRequest] element"));
            Assert.assertNotNull(e.getOperation());
            PartialOperation partial = (PartialOperation)e.getOperation();
            Assert.assertEquals(Operation.Type.DELIVER, partial.getType());
        }
    }


    @Test
    public void parseStartEndTagMismatch() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliverRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>10</operatorId>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SAXParseException e) {
            // correct behavior
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("must be terminated by the matching end-tag"));
        }
    }


    @Test
    public void parseSubmitMissingAccount() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
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
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
            // correct behavior
            Assert.assertEquals(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The [account] element is required before a [submitRequest] element"));
            Assert.assertNotNull(e.getOperation());
            PartialOperation partial = (PartialOperation)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, partial.getType());
        }
    }


    @Test
    public void parseSubmitEmptyOperatorId() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId></operatorId>\n")
            .append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40f404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.EMPTY_VALUE, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The element [operatorId] must contain a value"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
        }
    }

    @Test
    public void parseSubmitBadOperatorIdValue() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>f</operatorId>\n")
            .append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40f404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Unable to convert [f] to an integer for [operatorId]"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
        }
    }

    @Test
    public void parseSubmitNegativeOperatorIdValue() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>-1</operatorId>\n")
            .append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40f404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.INVALID_VALUE, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The [operatorId] must be greater than or equal to 0"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
        }
    }

    @Test
    public void parseSubmitBadDeliveryReportValue() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>1</operatorId>\n")
            .append("  <deliveryReport>blah</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40f404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Unable to convert [blah] to a boolean for [deliveryReport]"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
            // we should have partially parsed the submit request
            Assert.assertEquals(new Integer(1), submitRequest.getOperatorId());
        }
    }

    @Test
    public void parseSubmitBadSourceAddressValue() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>1</operatorId>\n")
            .append("  <deliveryReport>TrUe</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40f404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Network address contained an invalid character"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
            // we should have partially parsed the submit request
            Assert.assertEquals(new Integer(1), submitRequest.getOperatorId());
            Assert.assertEquals(Boolean.TRUE, submitRequest.getDeliveryReport());
        }
    }

    @Test
    public void parseSubmitBadSourceTypeValue() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>1</operatorId>\n")
            .append("  <deliveryReport>TrUe</deliveryReport>\n")
            .append("  <sourceAddress type=\"network2\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The address type [network2] is not valid"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
            // we should have partially parsed the submit request
            Assert.assertEquals(new Integer(1), submitRequest.getOperatorId());
            Assert.assertEquals(Boolean.TRUE, submitRequest.getDeliveryReport());
        }
    }

    @Test
    public void parseSubmitBadDestinationAddressValue() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>1</operatorId>\n")
            .append("  <deliveryReport>TrUe</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("International address must start with"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
            // we should have partially parsed the submit request
            Assert.assertEquals(new Integer(1), submitRequest.getOperatorId());
            Assert.assertEquals(Boolean.TRUE, submitRequest.getDeliveryReport());
            Assert.assertEquals(MobileAddress.Type.NETWORK, submitRequest.getSourceAddress().getType());
            Assert.assertEquals("40404", submitRequest.getSourceAddress().getAddress());
        }
    }

    @Test
    public void parseSubmitBadDestinationTypeValue() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>1</operatorId>\n")
            .append("  <deliveryReport>TrUe</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international2\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The address type [international2] is not valid"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
            // we should have partially parsed the submit request
            Assert.assertEquals(new Integer(1), submitRequest.getOperatorId());
            Assert.assertEquals(Boolean.TRUE, submitRequest.getDeliveryReport());
            Assert.assertEquals(MobileAddress.Type.NETWORK, submitRequest.getSourceAddress().getType());
            Assert.assertEquals("40404", submitRequest.getSourceAddress().getAddress());
        }
    }


    @Test
    public void parseSubmitInvalidTextEncodingValue() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>1</operatorId>\n")
            .append("  <deliveryReport>TrUe</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO2-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNSUPPORTED_TEXT_ENCODING, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Unsupported text encoding [ISO2-8859-1] found"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
            // we should have partially parsed the submit request
            Assert.assertEquals(new Integer(1), submitRequest.getOperatorId());
            Assert.assertEquals(Boolean.TRUE, submitRequest.getDeliveryReport());
            Assert.assertEquals(MobileAddress.Type.NETWORK, submitRequest.getSourceAddress().getType());
            Assert.assertEquals("40404", submitRequest.getSourceAddress().getAddress());
        }
    }


    @Test
    public void parseSubmitInvalidTextHexValue() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>1</operatorId>\n")
            .append("  <deliveryReport>TrUe</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">4</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.TEXT_HEX_DECODING_FAILED, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Unable to decode hex data into text"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
            // we should have partially parsed the submit request
            Assert.assertEquals(new Integer(1), submitRequest.getOperatorId());
            Assert.assertEquals(Boolean.TRUE, submitRequest.getDeliveryReport());
            Assert.assertEquals(MobileAddress.Type.NETWORK, submitRequest.getSourceAddress().getType());
            Assert.assertEquals("40404", submitRequest.getSourceAddress().getAddress());
            Assert.assertEquals(MobileAddress.Type.INTERNATIONAL, submitRequest.getDestinationAddress().getType());
            Assert.assertEquals("+12065551212", submitRequest.getDestinationAddress().getAddress());
        }
    }


    @Test
    public void parseSubmitUnsupportedElement() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>1</operatorId>\n")
            .append("  <status code=\"0\" message=\"Expired\" />\n")       // NOTE: A status ISN'T VALID HERE
            .append("  <deliveryReport>TrUe</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">4</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNSUPPORTED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The status element is not supported for this operation type"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
            // we should have partially parsed the submit request
            Assert.assertEquals(new Integer(1), submitRequest.getOperatorId());
        }
    }

    @Test
    public void parseSubmitMissingDestinationAddress() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            //.append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("A destinationAddress value is mandatory"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
        }
    }

    @Test
    public void parseSubmitMissingText() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            //.append("  <text encoding=\"ISO-8859-1\">48</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("A text value is mandatory"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
        }
    }

    @Test
    public void parseSubmitElementsAtWrongDepth() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\" />\n")
            .append(" <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append(" <text encoding=\"ISO-8859-1\">48</text>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNSUPPORTED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Unsupported [destinationAddress] element found at depth"));
            Assert.assertNotNull(e.getOperation());
            PartialOperation partial = (PartialOperation)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, partial.getType());
        }
    }

    @Test
    public void parseSubmitUnsupportedChildElement() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48</text>\n")
            .append("  <badelement />\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNSUPPORTED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Unsupported [badelement] element found at depth"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
        }
    }

    @Test
    public void parseSubmitUnsupportedChildElement1() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"MYREF102020022\">\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48")
            .append("<badelement />\n")
            .append("</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNSUPPORTED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("Unsupported [badelement] element found at depth"));
            Assert.assertNotNull(e.getOperation());
            SubmitRequest submitRequest = (SubmitRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.SUBMIT, submitRequest.getType());
        }
    }

    @Test
    public void parseSubmitRequest0() throws Exception {
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
        SxmpParser parser = new SxmpParser();
        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.SUBMIT, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        SubmitRequest submitReq = (SubmitRequest)operation;
        Assert.assertEquals("customer1", submitReq.getAccount().getUsername());
        Assert.assertEquals("test1", submitReq.getAccount().getPassword());
        Assert.assertNull(submitReq.getApplication());
        Assert.assertEquals("MYREF102020022", submitReq.getReferenceId());
        Assert.assertEquals(new Integer(10), submitReq.getOperatorId());
        Assert.assertEquals(new Boolean(true), submitReq.getDeliveryReport());
        Assert.assertEquals(MobileAddress.Type.NETWORK, submitReq.getSourceAddress().getType());
        Assert.assertEquals("40404", submitReq.getSourceAddress().getAddress());
        Assert.assertEquals(MobileAddress.Type.INTERNATIONAL, submitReq.getDestinationAddress().getType());
        Assert.assertEquals("+12065551212", submitReq.getDestinationAddress().getAddress());
        Assert.assertEquals("Hello World", submitReq.getText());
    }

    @Test
    public void parseSubmitRequestWithNoReferenceId() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest>\n")
            .append("  <operatorId>10</operatorId>\n")
            .append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();
        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.SUBMIT, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        SubmitRequest submitReq = (SubmitRequest)operation;
        Assert.assertEquals("customer1", submitReq.getAccount().getUsername());
        Assert.assertEquals("test1", submitReq.getAccount().getPassword());
        Assert.assertEquals(null, submitReq.getReferenceId());
        Assert.assertEquals(new Integer(10), submitReq.getOperatorId());
        Assert.assertEquals(new Boolean(true), submitReq.getDeliveryReport());
        Assert.assertEquals(MobileAddress.Type.NETWORK, submitReq.getSourceAddress().getType());
        Assert.assertEquals("40404", submitReq.getSourceAddress().getAddress());
        Assert.assertEquals(MobileAddress.Type.INTERNATIONAL, submitReq.getDestinationAddress().getType());
        Assert.assertEquals("+12065551212", submitReq.getDestinationAddress().getAddress());
        Assert.assertEquals("Hello World", submitReq.getText());
    }

    /**
    @Test
    public void parseUnicode() throws Exception {

        // utf-8
        // d8a3d987d984d8a720d987d8b0d98720d8a7d984d8aad8acd8b1d8a8d8a920d8a7d984d8a3d988d984d989
        // utf-16
        // 06230647064406270020064706300647002006270644062a062c0631062806290020062706440623064806440649
        // utf-16BE (big endian)
        // d8a3d987d984d8a720d987d8b0d98720d8a7d984d8aad8acd8b1d8a8d8a920d8a7d984d8a3d988d984d989

        // utf-16
        // 0623
        // into utf-8
        // d8a3

        // create byte array of arabic characters
        //byte[] bytes = StringUtil.fromHexString("06230647064406270020064706300647002006270644062a062c0631062806290020062706440623064806440649");
        byte[] bytes = StringUtil.fromHexString("0623");

        // try to encode this into a string -- its UCS2, but UTF-16 is a superset and upwards compatible
        String message = new String(bytes, "UTF-16BE");

        // now, convert this into UTF-8
        byte[] bytes2 = message.getBytes("UTF-8");

        System.out.println(StringUtil.toHexString(bytes2));
    }
     */

    @Test
    public void parseDeliverRequest0() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliverRequest referenceId=\"MYREF102020022\">\n")
            //.append("  <operatorId>10</operatorId>\n")
            //.append("  <sourceAddress type=\"international\">+12065551212</sourceAddress>\n")
            .append("  <destinationAddress type=\"network\">40404</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </deliverRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();
        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.DELIVER, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        DeliverRequest deliverReq = (DeliverRequest)operation;
        Assert.assertEquals("customer1", deliverReq.getAccount().getUsername());
        Assert.assertEquals("test1", deliverReq.getAccount().getPassword());
        Assert.assertNull(deliverReq.getApplication());
        Assert.assertEquals("MYREF102020022", deliverReq.getReferenceId());
        Assert.assertEquals(null, deliverReq.getOperatorId());
        Assert.assertEquals(MobileAddress.Type.NETWORK, deliverReq.getDestinationAddress().getType());
        Assert.assertEquals("40404", deliverReq.getDestinationAddress().getAddress());
        Assert.assertEquals(null, deliverReq.getSourceAddress());
        Assert.assertEquals("Hello World", deliverReq.getText());
        Assert.assertEquals(null, deliverReq.getTicketId());
    }

    @Test
    public void parseDeliverRequest1() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliverRequest referenceId=\"MYREF102020022\">\n")
            .append("  <operatorId>10</operatorId>\n")
            //.append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"international\">+12065551212</sourceAddress>\n")
            .append("  <destinationAddress type=\"network\">40404</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </deliverRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();
        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.DELIVER, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        DeliverRequest deliverReq = (DeliverRequest)operation;
        Assert.assertEquals("customer1", deliverReq.getAccount().getUsername());
        Assert.assertEquals("test1", deliverReq.getAccount().getPassword());
        Assert.assertEquals("MYREF102020022", deliverReq.getReferenceId());
        Assert.assertEquals(new Integer(10), deliverReq.getOperatorId());
        Assert.assertEquals(MobileAddress.Type.NETWORK, deliverReq.getDestinationAddress().getType());
        Assert.assertEquals("40404", deliverReq.getDestinationAddress().getAddress());
        Assert.assertEquals(MobileAddress.Type.INTERNATIONAL, deliverReq.getSourceAddress().getType());
        Assert.assertEquals("+12065551212", deliverReq.getSourceAddress().getAddress());
        Assert.assertEquals("Hello World", deliverReq.getText());
        Assert.assertEquals(null, deliverReq.getTicketId());
    }

    @Test
    public void parseDeliverRequestWithTicket() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliverRequest referenceId=\"MYREF102020022\">\n")
            .append("  <ticketId>THISISATICKETID</ticketId>")
            .append("  <operatorId>10</operatorId>\n")
            //.append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"international\">+12065551212</sourceAddress>\n")
            .append("  <destinationAddress type=\"network\">40404</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </deliverRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();
        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.DELIVER, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        DeliverRequest deliverReq = (DeliverRequest)operation;
        Assert.assertEquals("customer1", deliverReq.getAccount().getUsername());
        Assert.assertEquals("test1", deliverReq.getAccount().getPassword());
        Assert.assertEquals("MYREF102020022", deliverReq.getReferenceId());
        Assert.assertEquals(new Integer(10), deliverReq.getOperatorId());
        Assert.assertEquals(MobileAddress.Type.NETWORK, deliverReq.getDestinationAddress().getType());
        Assert.assertEquals("40404", deliverReq.getDestinationAddress().getAddress());
        Assert.assertEquals(MobileAddress.Type.INTERNATIONAL, deliverReq.getSourceAddress().getType());
        Assert.assertEquals("+12065551212", deliverReq.getSourceAddress().getAddress());
        Assert.assertEquals("Hello World", deliverReq.getText());
        Assert.assertEquals("THISISATICKETID", deliverReq.getTicketId());
    }

    @Test
    public void parseDeliveryReportMissingStatus() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliveryReport\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliveryReportRequest>\n")
            //.append("   <status code=\"5\" message=\"Expired\"/>\n")
            .append("   <ticketId>000:20090118002220948:000</ticketId>\n")
            .append(" </deliveryReportRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("A status value is mandatory with a deliveryReportRequest"));
            Assert.assertNotNull(e.getOperation());
            DeliveryReportRequest deliveryRequest = (DeliveryReportRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.DELIVERY_REPORT, deliveryRequest.getType());
        }
    }

    @Test
    public void parseDeliveryReportMissingTicket() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliveryReport\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliveryReportRequest>\n")
            .append("   <status code=\"5\" message=\"Expired\"/>\n")
            //.append("   <ticketId>000:20090118002220948:000</ticketId>\n")
            .append(" </deliveryReportRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("A ticketId value is mandatory with a deliveryReportRequest"));
            Assert.assertNotNull(e.getOperation());
            DeliveryReportRequest deliveryRequest = (DeliveryReportRequest)e.getOperation();
            Assert.assertEquals(Operation.Type.DELIVERY_REPORT, deliveryRequest.getType());
        }
    }

    @Test
    public void parseDeliveryReportRequest0() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliveryReport\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <deliveryReportRequest>\n")
            .append("   <status code=\"5\" message=\"Expired\"/>\n")
            .append("   <ticketId>000:20090118002220948:000</ticketId>\n")
            .append(" </deliveryReportRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();
        
        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.DELIVERY_REPORT, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        DeliveryReportRequest reportReq = (DeliveryReportRequest)operation;
        Assert.assertEquals("customer1", reportReq.getAccount().getUsername());
        Assert.assertEquals("test1", reportReq.getAccount().getPassword());
        Assert.assertNull(reportReq.getApplication());
        Assert.assertEquals("000:20090118002220948:000", reportReq.getTicketId());
        Assert.assertEquals(new Integer(5), reportReq.getStatus().getCode());
        Assert.assertEquals("Expired", reportReq.getStatus().getMessage());
    }

    @Test
    public void parseErrorResponse() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <error code=\"1001\" message=\"This is a test message\" />\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        Operation operation = parser.parse(is);

        Assert.assertEquals(Operation.Type.SUBMIT, operation.getType());
        Assert.assertEquals(true, operation.isResponse());
        ErrorResponse errorResponse = (ErrorResponse)operation;
        Assert.assertEquals(new Integer(1001), errorResponse.getErrorCode());
        Assert.assertEquals("This is a test message", errorResponse.getErrorMessage());
    }

    @Test
    public void parseSubmitResponse() throws Exception {
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
        SxmpParser parser = new SxmpParser();

        Operation operation = parser.parse(is);

        //logger.debug(operation);

        Assert.assertEquals(Operation.Type.SUBMIT, operation.getType());
        Assert.assertEquals(true, operation.isResponse());
        SubmitResponse submitResponse = (SubmitResponse)operation;
        Assert.assertEquals("000:20090118002220948:000", submitResponse.getTicketId());
        Assert.assertEquals(new Integer(0), submitResponse.getErrorCode());
        Assert.assertEquals("OK", submitResponse.getErrorMessage());
    }

    /** THIS TEST IS NO LONGER VALID SINCE WE SUPPORT TICKETS IN ANYTHING NOW
    @Test
    public void parseDeliverResponseNoTicketSupported() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <deliverResponse>\n")
            .append("   <error code=\"0\" message=\"OK\"/>\n")
            .append("   <ticketId>000:20090118002220948:000</ticketId>\n")
            .append(" </deliverResponse>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail();
        } catch (SxmpParsingException e) {
             // correct behavior
            Assert.assertEquals(SxmpErrorCode.UNSUPPORTED_ELEMENT, e.getErrorCode());
            Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The [ticketId] element is not supported for this operation type"));
            Assert.assertNotNull(e.getOperation());
            DeliverResponse deliverResponse = (DeliverResponse)e.getOperation();
            Assert.assertEquals(Operation.Type.DELIVER, deliverResponse.getType());
        }
    }
     */

    @Test
    public void parseDeliverResponse0() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <deliverResponse>\n")
            .append("   <error code=\"0\" message=\"OK\"/>\n")
            .append(" </deliverResponse>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        Operation operation = parser.parse(is);

        DeliverResponse deliverResponse = (DeliverResponse)operation;

        Assert.assertEquals(new Integer(0), deliverResponse.getErrorCode());
        Assert.assertEquals("OK", deliverResponse.getErrorMessage());
    }

    @Test
    public void parseDeliveryReportResponse0() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliveryReport\">\n")
            .append(" <deliveryReportResponse>\n")
            .append("   <error code=\"0\" message=\"OK\"/>\n")
            .append(" </deliveryReportResponse>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        Operation operation = parser.parse(is);

        DeliveryReportResponse deliveryResponse = (DeliveryReportResponse)operation;

        Assert.assertEquals(new Integer(0), deliveryResponse.getErrorCode());
        Assert.assertEquals("OK", deliveryResponse.getErrorMessage());
    }



    @Test
    public void parseSubmitRequestWithUnicodeCharsInUTF8() throws Exception {
        StringBuilder xml = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest referenceId=\"TESTREF\">\n")
            .append("  <operatorId>20</operatorId>\n")
            //.append("  <deliveryReport>false</deliveryReport>\n")
            .append("  <sourceAddress type=\"network\">40404</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+13135551212</destinationAddress>\n")
            .append("  <text encoding=\"UTF-8\">E282ACD8A3D987D984D8A720D987D8B0D98720D8A7D984D8AAD8ACD8B1D8A8D8A920D8A7D984D8A3D988D984D989</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(xml.toString().getBytes());
        SxmpParser parser = new SxmpParser();
        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.SUBMIT, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        SubmitRequest submitReq = (SubmitRequest)operation;
        Assert.assertEquals("customer1", submitReq.getAccount().getUsername());
        Assert.assertEquals("test1", submitReq.getAccount().getPassword());
        Assert.assertEquals("TESTREF", submitReq.getReferenceId());
        Assert.assertEquals(new Integer(20), submitReq.getOperatorId());
        Assert.assertEquals(new Boolean(false), submitReq.getDeliveryReport());
        Assert.assertEquals(MobileAddress.Type.NETWORK, submitReq.getSourceAddress().getType());
        Assert.assertEquals("40404", submitReq.getSourceAddress().getAddress());
        Assert.assertEquals(MobileAddress.Type.INTERNATIONAL, submitReq.getDestinationAddress().getType());
        Assert.assertEquals("+13135551212", submitReq.getDestinationAddress().getAddress());
        Assert.assertEquals("UTF-8", submitReq.getTextEncoding().getCharset());
        Assert.assertEquals("\u20AC\u0623\u0647\u0644\u0627\u0020\u0647\u0630\u0647\u0020\u0627\u0644\u062a\u062c\u0631\u0628\u0629\u0020\u0627\u0644\u0623\u0648\u0644\u0649", submitReq.getText());
    }

    @Test
    public void parseSubmitRequestWithAlphanumeric() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <submitRequest>\n")
            .append("  <operatorId>10</operatorId>\n")
            .append("  <deliveryReport>true</deliveryReport>\n")
            .append("  <sourceAddress type=\"alphanumeric\">TestAlpha</sourceAddress>\n")
            .append("  <destinationAddress type=\"international\">+12065551212</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append(" </submitRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();
        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.SUBMIT, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        SubmitRequest submitReq = (SubmitRequest)operation;
        Assert.assertEquals("customer1", submitReq.getAccount().getUsername());
        Assert.assertEquals("test1", submitReq.getAccount().getPassword());
        Assert.assertNull(submitReq.getReferenceId());
        Assert.assertEquals(new Integer(10), submitReq.getOperatorId());
        Assert.assertEquals(new Boolean(true), submitReq.getDeliveryReport());
        Assert.assertEquals(MobileAddress.Type.ALPHANUMERIC, submitReq.getSourceAddress().getType());
        Assert.assertEquals("TestAlpha", submitReq.getSourceAddress().getAddress());
        Assert.assertEquals(MobileAddress.Type.INTERNATIONAL, submitReq.getDestinationAddress().getType());
        Assert.assertEquals("+12065551212", submitReq.getDestinationAddress().getAddress());
        Assert.assertEquals("Hello World", submitReq.getText());
    }


    @Test
    public void parseSubmitRequestWithApplication() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"submit\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <application>TestApp1</application>\n")
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
        SxmpParser parser = new SxmpParser();
        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.SUBMIT, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        SubmitRequest submitReq = (SubmitRequest)operation;
        Assert.assertEquals("customer1", submitReq.getAccount().getUsername());
        Assert.assertEquals("test1", submitReq.getAccount().getPassword());
        Assert.assertEquals("TestApp1", submitReq.getApplication().getName());
        Assert.assertEquals("MYREF102020022", submitReq.getReferenceId());
        Assert.assertEquals(new Integer(10), submitReq.getOperatorId());
        Assert.assertEquals(new Boolean(true), submitReq.getDeliveryReport());
        Assert.assertEquals(MobileAddress.Type.NETWORK, submitReq.getSourceAddress().getType());
        Assert.assertEquals("40404", submitReq.getSourceAddress().getAddress());
        Assert.assertEquals(MobileAddress.Type.INTERNATIONAL, submitReq.getDestinationAddress().getType());
        Assert.assertEquals("+12065551212", submitReq.getDestinationAddress().getAddress());
        Assert.assertEquals("Hello World", submitReq.getText());
    }

    @Test
    public void parseDeliverRequestWithApplicationAndTicketId() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliver\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <application>TestApp1</application>\n")
            .append(" <deliverRequest>\n")
            .append("  <sourceAddress type=\"international\">+12065551212</sourceAddress>\n")
            .append("  <destinationAddress type=\"network\">40404</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append("  <ticketId>THISISATICKETID</ticketId>")
            .append(" </deliverRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();
        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.DELIVER, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        DeliverRequest deliverReq = (DeliverRequest)operation;
        Assert.assertEquals("customer1", deliverReq.getAccount().getUsername());
        Assert.assertEquals("test1", deliverReq.getAccount().getPassword());
        Assert.assertEquals("TestApp1", deliverReq.getApplication().getName());
        Assert.assertNull(deliverReq.getReferenceId());
        Assert.assertEquals(null, deliverReq.getOperatorId());
        Assert.assertEquals(MobileAddress.Type.NETWORK, deliverReq.getDestinationAddress().getType());
        Assert.assertEquals("40404", deliverReq.getDestinationAddress().getAddress());
        Assert.assertEquals(MobileAddress.Type.INTERNATIONAL, deliverReq.getSourceAddress().getType());
        Assert.assertEquals("+12065551212", deliverReq.getSourceAddress().getAddress());
        Assert.assertEquals("Hello World", deliverReq.getText());
        Assert.assertEquals("THISISATICKETID", deliverReq.getTicketId());
    }

    @Test
    public void parseDeliveryReportRequestWithApplication() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliveryReport\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <application>TestApp1</application>\n")
            .append(" <deliveryReportRequest>\n")
            .append("   <status code=\"5\" message=\"EXPIRED\"/>\n")
            .append("   <ticketId>000:20090118002220948:000</ticketId>\n")
            .append(" </deliveryReportRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.DELIVERY_REPORT, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        DeliveryReportRequest reportReq = (DeliveryReportRequest)operation;
        Assert.assertEquals("customer1", reportReq.getAccount().getUsername());
        Assert.assertEquals("test1", reportReq.getAccount().getPassword());
        Assert.assertEquals("TestApp1", reportReq.getApplication().getName());
        Assert.assertEquals("000:20090118002220948:000", reportReq.getTicketId());
        Assert.assertEquals(new Integer(5), reportReq.getStatus().getCode());
        Assert.assertEquals("EXPIRED", reportReq.getStatus().getMessage());
    }

    @Test
    public void parseDeliveryReportRequestWithDates() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\"?>\n")
            .append("<operation type=\"deliveryReport\">\n")
            .append(" <account username=\"customer1\" password=\"test1\"/>\n")
            .append(" <application>TestApp1</application>\n")
            .append(" <deliveryReportRequest>\n")
            .append("   <status code=\"5\" message=\"EXPIRED\"/>\n")
            .append("   <ticketId>000:20090118002220948:000</ticketId>\n")
            .append("   <messageError code=\"102\"/>\n")
            .append("   <createDate>2010-05-30T09:30:10.000Z</createDate>\n")
            .append("   <finalDate>2010-05-30T09:30:15.314Z</finalDate>\n")
            .append(" </deliveryReportRequest>\n")
            .append("</operation>\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        Operation operation = parser.parse(is);

        logger.debug(operation);

        Assert.assertEquals(Operation.Type.DELIVERY_REPORT, operation.getType());
        Assert.assertEquals(true, operation.isRequest());
        DeliveryReportRequest reportReq = (DeliveryReportRequest)operation;
        Assert.assertEquals("customer1", reportReq.getAccount().getUsername());
        Assert.assertEquals("test1", reportReq.getAccount().getPassword());
        Assert.assertEquals("TestApp1", reportReq.getApplication().getName());
        Assert.assertEquals("000:20090118002220948:000", reportReq.getTicketId());
        Assert.assertEquals(new Integer(5), reportReq.getStatus().getCode());
        Assert.assertEquals("EXPIRED", reportReq.getStatus().getMessage());
        Assert.assertEquals(new Integer(102), reportReq.getMessageErrorCode());
        Assert.assertEquals(new DateTime(2010,5,30,9,30,10,0, DateTimeZone.UTC), reportReq.getCreateDate());
        Assert.assertEquals(new DateTime(2010,5,30,9,30,15,314, DateTimeZone.UTC), reportReq.getFinalDate());
    }
    
    @Test
    public void parseEntityExpansionAttack() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version='1.0' encoding='iso-8859-1'?>\n")
            .append("<!DOCTYPE malicious [\n")
            .append("    <!ENTITY x0 \"\">\n")
            .append("    <!ENTITY x1 \"&x0;&x0;\">\n")
            .append("    <!ENTITY x2 \"&x1;&x1;\">\n")
            .append("    <!ENTITY x3 \"&x2;&x2;\">\n")
            .append("    <!ENTITY x4 \"&x3;&x3;\">\n")
            .append("    <!ENTITY x5 \"&x4;&x4;\">\n")
            .append("    <!ENTITY x6 \"&x5;&x5;\">\n")
            .append("    <!ENTITY x7 \"&x6;&x6;\">\n")
            .append("    <!ENTITY x8 \"&x7;&x7;\">\n")
            .append("    <!ENTITY x9 \"&x8;&x8;\">\n")
            .append("    <!ENTITY x10 \"&x9;&x9;\">\n")
            .append("    <!ENTITY x11 \"&x10;&x10;\">\n")
            .append("    <!ENTITY x12 \"&x11;&x11;\">\n")
            .append("    <!ENTITY x13 \"&x12;&x12;\">\n")
            .append("    <!ENTITY x14 \"&x13;&x13;\">\n")
            .append("    <!ENTITY x15 \"&x14;&x14;\">\n")
            .append("    <!ENTITY x16 \"&x15;&x15;\">\n")
            .append("    <!ENTITY x17 \"&x16;&x16;\">\n")
            .append("    <!ENTITY x18 \"&x17;&x17;\">\n")
            .append("    <!ENTITY x19 \"&x18;&x18;\">\n")
            .append("    <!ENTITY x20 \"&x19;&x19;\">\n")
            .append("]>\n")
            .append("<operation type=\"deliver\">\n")
            .append("<account username=\"test\" password=\"test\"/>\n")
            .append("<deliverRequest>\n")
            .append("<ticketId>&x20;test</ticketId>\n")
            .append("  <sourceAddress type=\"international\">+12065551212</sourceAddress>\n")
            .append("  <destinationAddress type=\"network\">40404</destinationAddress>\n")
            .append("  <text encoding=\"ISO-8859-1\">48656c6c6f20576f726c64</text>\n")
            .append("</deliverRequest>    \n")
            .append("</operation >\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        try {
            Operation operation = parser.parse(is);
            Assert.fail("should have thrown an exception");
        } catch (SAXParseException e) {
            // correct behavior -- only thrown if secure parsing is turned on
            // otherwise an OutOfMemoryException is thrown
        }
    }
}
