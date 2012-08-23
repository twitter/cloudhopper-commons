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

import com.cloudhopper.commons.util.HexUtil;
import com.cloudhopper.sxmp.util.MobileAddressUtil;
import com.cloudhopper.commons.util.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author joelauer
 */
public class SxmpParser {
    private static final Logger logger = Logger.getLogger(SxmpParser.class);

    static protected DateTimeFormatter dateTimeFormat = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);
    
    /**
    public Node parse(InputSource source) throws IOException, SAXException {
//        _dtd=null;
        Handler handler = new Handler();
        XMLReader reader = _parser.getXMLReader();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.setEntityResolver(handler);
        if (logger.isDebugEnabled())
            logger.debug("parsing: sid=" + source.getSystemId() + ",pid=" + source.getPublicId());
        _parser.parse(source, handler);
        if (handler.error != null)
            throw handler.error;
        Node root = (Node)handler.root;
        handler.reset();
        return root;
    }

    public synchronized Node parse(String xml) throws IOException, SAXException {
        ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
        return parse(is);
    }

    public synchronized Node parse(File file) throws IOException, SAXException {
        return parse(new InputSource(file.toURI().toURL().toString()));
    }

    public synchronized Node parse(InputStream in) throws IOException, SAXException {
        //_dtd=null;
        Handler handler = new Handler();
        XMLReader reader = _parser.getXMLReader();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.setEntityResolver(handler);
        _parser.parse(new InputSource(in), handler);
        if (handler.error != null)
            throw handler.error;
        Node root = (Node)handler.root;
        handler.reset();
        return root;
    }
     */

    public Operation parse(InputStream in) throws SxmpParsingException, IOException, SAXException, ParserConfigurationException {
        // create a new SAX parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        //factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        
        //try {

            SAXParser parser = factory.newSAXParser();
            //_parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", validating);
            parser.getXMLReader().setFeature("http://xml.org/sax/features/namespaces", true);
            parser.getXMLReader().setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            parser.getXMLReader().setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);

        //} catch (Exception e) {
        //    logger.warn(e);
        //    throw new Error(e.toString());
        //}


        //_dtd=null;
        Handler handler = new Handler();
        XMLReader reader = parser.getXMLReader();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.setEntityResolver(handler);

        // try parsing (may throw an SxmpParsingException in the handler)
        parser.parse(new InputSource(in), handler);

        // check if there was an error
        if (handler.error != null) {
            throw handler.error;
        }

        // check to see if an operation was actually parsed
        if (handler.getOperation() == null) {
            throw new SxmpParsingException(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, "The operation type [" + handler.operationType.getValue() + "] requires a request element", new PartialOperation(handler.operationType));
        }

        // if we got here, an operation was parsed -- now we need to validate it
        // to make sure that it has all required elements
        try {
            handler.getOperation().validate();
        } catch (SxmpErrorException e) {
            throw new SxmpParsingException(e.getErrorCode(), e.getErrorMessage(), handler.getOperation());
        }

        return handler.getOperation();
    }

    private class Handler extends DefaultHandler {

        SAXParseException error;
        private int depth;
        Attributes currentAttrs;
        StringBuilder charBuffer;
        Operation.Type operationType;
        Account account;
        Application application;
        Operation operation;
        // we'll use this hashmap for duplicate element checks
        HashSet<String> elements;

        // utility class to hold error code and message
        private class ErrorCodeMessage {
            public int code;
            public String message;
        }

        Handler() {
            depth = -1;
            charBuffer = new StringBuilder(200);
            this.elements = new HashSet<String>();
        }

        /**
         * Gets the value from a required attribute.  This method will provide
         * the following error checking.  If the attribute is missing, it will
         * throw a MISSING_ATTRIBUTE exception.  If the attribute was included,
         * but the value was empty, it will throw an EMPTY_VALUE exception.
         * Otherwise, it will return the value.
         * @param attr The attributes to check
         * @param name The attribute name to find
         * @return The value
         * @throws SxmpErrorException See method overview
         */
        public String getOptionalAttributeValue(String tag, Attributes attrs, String name) throws SxmpParsingException {
            //
            // make sure the attribute exists
            //
            int size = attrs.getLength();
            String value = null;

            for (int i = 0; i < size; i++) {
                if (attrs.getQName(i).equals(name)) {
                    // is this the second time we found this attribute?
                    if (value != null) {
                        throw new SxmpParsingException(SxmpErrorCode.MULTIPLE_ATTRIBUTES_NOT_SUPPORTED, "Multiple [" + name + "] attributes within the [" + tag + "] element are not supported", (this.operation != null ? this.operation : new PartialOperation(this.operationType)));
                    }

                    value = attrs.getValue(i);

                    // is there some sort of value for it?
                    if (StringUtil.isEmpty(value)) {
                        throw new SxmpParsingException(SxmpErrorCode.EMPTY_VALUE, "The [" + name + "] attribute was empty in the [" + tag + "] element", (this.operation != null ? this.operation : new PartialOperation(this.operationType)));
                    }
                }
            }

            if (value != null) {
                return value;
            } else {
                return null;
            }
        }

        /**
         * Gets the value from a required attribute.  This method will provide
         * the following error checking.  If the attribute is missing, it will
         * throw a MISSING_ATTRIBUTE exception.  If the attribute was included,
         * but the value was empty, it will throw an EMPTY_VALUE exception.
         * Otherwise, it will return the value.
         * @param attr The attributes to check
         * @param name The attribute name to find
         * @return The value
         * @throws SxmpErrorException See method overview
         */
        public String getRequiredAttributeValue(String tag, Attributes attrs, String name) throws SxmpParsingException {
            //
            // same process as an optional element
            //
            String value = getOptionalAttributeValue(tag, attrs, name);

            if (value != null) {
                return value;
            } else {
                throw new SxmpParsingException(SxmpErrorCode.MISSING_REQUIRED_ATTRIBUTE, "The attribute [" + name + "] is required with the [" + tag + "] element", (this.operation != null ? this.operation : new PartialOperation(this.operationType)));
            }
        }

        public void parseRequestElement(String tag, Request request, Attributes attrs) throws SxmpParsingException {
            // should only occur once
            if (elements.contains(tag)) {
                // we already had the same operation before, so we'll include that in the error
                throw new SxmpParsingException(SxmpErrorCode.MULTIPLE_ELEMENTS_NOT_SUPPORTED, "Multiple [" + tag + "] elements are not supported", this.operation);
            }

            // add the element tag to something we visited
            elements.add(tag);

            // an account must ALWAYS come before a request
            if (this.account == null) {
                throw new SxmpParsingException(SxmpErrorCode.MISSING_REQUIRED_ELEMENT, "The [account] element is required before a [" + tag + "] element", new PartialOperation(this.operationType));
            }

            // check if the request type matches the operation type
            if (request.getType() != this.operationType) {
                throw new SxmpParsingException(SxmpErrorCode.OPTYPE_MISMATCH, "The operation type [" + operationType.getValue() + "] does not match the [" + tag + "] element", new PartialOperation(this.operationType));
            }

            // save the account in this request
            request.setAccount(this.account);
            request.setApplication(this.application);

            // save the request as our operation
            this.operation = request;

            // all requests have an optional referenceId attribute
            String referenceId = getOptionalAttributeValue("submitRequest", attrs, "referenceId");

            if (!StringUtil.isEmpty(referenceId)) {
                try {
                    request.setReferenceId(referenceId);
                } catch (SxmpErrorException e) {
                    throw new SxmpParsingException(SxmpErrorCode.INVALID_REFERENCE_ID, e.getErrorMessage(), this.operation);
                }
            }
        }

        public void parseResponseElement(String tag, Response response) throws SxmpParsingException {
            // should only occur once
            if (elements.contains(tag)) {
                // we already had the same operation before, so we'll include that in the error
                throw new SxmpParsingException(SxmpErrorCode.MULTIPLE_ELEMENTS_NOT_SUPPORTED, "Multiple [" + tag + "] elements are not supported", this.operation);
            }

            // add the element tag to something we visited
            elements.add(tag);

            // check if the request type matches the operation type
            if (response.getType() != this.operationType) {
                throw new SxmpParsingException(SxmpErrorCode.OPTYPE_MISMATCH, "The operation type [" + operationType.getValue() + "] does not match the [" + tag + "] element", new PartialOperation(this.operationType));
            }

            // save the request as our operation
            this.operation = response;
        }

        public String parseCharacterData(String tag, boolean required) throws SxmpParsingException {
            // should only occur once
            if (elements.contains(tag)) {
                throw new SxmpParsingException(SxmpErrorCode.MULTIPLE_ELEMENTS_NOT_SUPPORTED, "Multiple [" + tag + "] elements are not supported", this.operation);
            }

            // add the element tag to something we visisted
            elements.add(tag);

            // should be able to convert character data to an integer
            String text = charBuffer.toString();

            if (required && StringUtil.isEmpty(text)) {
                throw new SxmpParsingException(SxmpErrorCode.EMPTY_VALUE, "The element [" + tag + "] must contain a value", this.operation);
            }

            return text;
        }

        public Integer parseIntegerValue(String name, String value) throws SxmpParsingException {
            // convert to an integer
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                throw new SxmpParsingException(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, "Unable to convert [" + value + "] to an integer for [" + name + "]", this.operation);
            }
        }


        public Boolean parseBooleanValue(String name, String value) throws SxmpParsingException {
            // convert to a boolean
            if (value == null) {
                 throw new SxmpParsingException(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, "Unable to convert [null] to a boolean for [" + name + "]", this.operation);
            }

            if (value.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            } else if (value.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            } else {
                throw new SxmpParsingException(SxmpErrorCode.UNABLE_TO_CONVERT_VALUE, "Unable to convert [" + value + "] to a boolean for [" + name + "]", this.operation);
            }
        }

        public ErrorCodeMessage parseErrorElement(String tag, Attributes attrs) throws SxmpParsingException {
            // should only occur once at this level
            if (elements.contains(tag)) {
                throw new SxmpParsingException(SxmpErrorCode.MULTIPLE_ELEMENTS_NOT_SUPPORTED, "Multiple [" + tag + "] elements are not supported", this.operation);
            }

            // add the element tag to something we visisted
            elements.add(tag);

            ErrorCodeMessage ecm = new ErrorCodeMessage();
            String errorCodeString = this.getRequiredAttributeValue("error", currentAttrs, "code");
            ecm.message = this.getRequiredAttributeValue("error", currentAttrs, "message");
            ecm.code = this.parseIntegerValue("code", errorCodeString);
            return ecm;
        }

        public Operation getOperation() {
            return this.operation;
        }

        @Override
        public void processingInstruction( String target, String value ) {
            //logger.debug("processing instr!");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
            //
            // get the tag stripped of any namespace
            //
            String tag = (StringUtil.isEmpty(uri)) ? qName : localName;

            // debug
            //logger.debug("startElement: tag=" + tag);
//            logger.trace("startElement: uri=" + uri + ", localName=" + localName + ", qName=" + qName);

            // always increment our depth
            depth++;

            //
            // Depth: zero
            // Element(s): operation
            //
            if (depth == 0) {
                if (!tag.equals("operation")) {
                    throw new SxmpParsingException(SxmpErrorCode.INVALID_XML, "Root element must be an [operation]", null);
                }

                // must contain an attribute with the type
                String opType = getRequiredAttributeValue("operation", attrs, "type");

                // parse it via the enum
                this.operationType = Operation.Type.parse(opType);

                if (this.operationType == null) {
                    throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_OPERATION, "Unsupported operation type [" + opType + "]", null);
                }
            //
            // Depth: 1
            // Element(s): account, submitRequest
            //
            } else if (depth == 1) {
                
                if (tag.equals("account")) {
                    // should only occur once
                    if (elements.contains(tag)) {
                        throw new SxmpParsingException(SxmpErrorCode.MULTIPLE_ELEMENTS_NOT_SUPPORTED, "Multiple [account] elements are not supported", new PartialOperation(this.operationType));
                    }

                    elements.add(tag);
                    
                    // should have two attributes, username and password
                    String username = getRequiredAttributeValue("account", attrs, "username");
                    String password = getRequiredAttributeValue("account", attrs, "password");
                    this.account = new Account(username, password);
                } else if (tag.equals("application")) {
                    // should only occur once
                    if (elements.contains(tag)) {
                        throw new SxmpParsingException(SxmpErrorCode.MULTIPLE_ELEMENTS_NOT_SUPPORTED, "Multiple [application] elements are not supported", new PartialOperation(this.operationType));
                    }
                } else if (tag.equals("error")) {
                    // this is a top level error -- indicates this should be
                    // the only element to return
                    ErrorCodeMessage ecm = parseErrorElement(tag, currentAttrs);

                    ErrorResponse errorResponse = new ErrorResponse(this.operationType, ecm.code, ecm.message);
                    this.operation = errorResponse;
                } else if (tag.equals("submitRequest")) {
                    parseRequestElement(tag, new SubmitRequest(), attrs);
                } else if (tag.equals("deliverRequest")) {
                    parseRequestElement(tag, new DeliverRequest(), attrs);
                } else if (tag.equals("deliveryReportRequest")) {
                    parseRequestElement(tag, new DeliveryReportRequest(), attrs);
                } else if (tag.equals("submitResponse")) {
                    parseResponseElement(tag, new SubmitResponse());
                } else if (tag.equals("deliverResponse")) {
                    parseResponseElement(tag, new DeliverResponse());
                } else if (tag.equals("deliveryReportResponse")) {
                    parseResponseElement(tag, new DeliveryReportResponse());
                } else {
                    throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "Unsupported [" + tag + "] element found at depth [" + depth + "]", new PartialOperation(this.operationType));
                }

            //
            // Depth: 2
            // Element(s): operatorId, etc.
            //
            } else if (depth == 2) {
                // do nothing here, always process in endElement callback
            } else {
                // NOTE: no other depths are supported in SXMP
                throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "Unsupported [" + tag + "] element found at depth [" + depth + "]", this.operation);
            }

            // starting element means reset our character buffer
            charBuffer.setLength(0);
            // save reference to attributes for processing in endElement
            currentAttrs = attrs;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            //
            // get the tag stripped of any namespace
            //
            String tag = (StringUtil.isEmpty(uri)) ? qName : localName;
            
//            logger.trace("endElement: uri=" + uri + ", localName=" + localName + ", qName=" + qName);

            if (depth == 1) {
                if (tag.equals("application")) {
                    // parse the character data, check for duplicates
                    String applicationNameText = parseCharacterData(tag, true);
                    this.application = new Application(applicationNameText);
                }
            } else if (depth == 2) {
                if (tag.equals("operatorId")) {
                    // parse the character data, check for duplicates
                    String operatorIdText = parseCharacterData(tag, true);

                    // convert to an integer
                    Integer operatorId = parseIntegerValue(tag, operatorIdText);

                    try {
                        if (operation instanceof MessageRequest) {
                            ((MessageRequest)operation).setOperatorId(operatorId);
                        } else {
                            throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "The [operatorId] element is not supported for this operation type", this.operation);
                        }
                    } catch (SxmpErrorException e) {
                        throw new SxmpParsingException(e.getErrorCode(), e.getErrorMessage(), this.operation);
                    }
                } else if (tag.equals("error")) {
                    // parse the error element
                    ErrorCodeMessage ecm = parseErrorElement(tag, currentAttrs);

                    if (operation instanceof Response) {
                        ((Response)operation).setErrorCode(ecm.code);
                        ((Response)operation).setErrorMessage(ecm.message);
                    } else {
                        throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "The [" + tag + "] element is not supported for this operation type", this.operation);
                    }
                } else if (tag.equals("ticketId")) {
                    // parse the character data, check for duplicates
                    String ticketId = parseCharacterData(tag, true);

                    // we basically support a ticket element on anything (request or response)
                    operation.setTicketId(ticketId);
                } else if (tag.equals("status")) {
                    // should only occur once
                    if (elements.contains(tag)) {
                        throw new SxmpParsingException(SxmpErrorCode.MULTIPLE_ELEMENTS_NOT_SUPPORTED, "Multiple [status] elements are not supported", this.operation);
                    }

                    // add the element tag to something we visisted
                    elements.add(tag);

                    String statusCodeString = this.getRequiredAttributeValue("status", currentAttrs, "code");
                    String statusMessage = this.getRequiredAttributeValue("status", currentAttrs, "message");
                    Integer statusCode = this.parseIntegerValue("code", statusCodeString);

                    DeliveryStatus status = new DeliveryStatus(statusCode, statusMessage);

                    if (operation instanceof DeliveryReportRequest) {
                        ((DeliveryReportRequest)operation).setStatus(status);
                    } else {
                        throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "The status element is not supported for this operation type", this.operation);
                    }
                } else if (tag.equals("messageError")) {
                    // should only occur once
                    if (elements.contains(tag)) {
                        throw new SxmpParsingException(SxmpErrorCode.MULTIPLE_ELEMENTS_NOT_SUPPORTED, "Multiple [messageError] elements are not supported", this.operation);
                    }

                    // add the element tag to something we visisted
                    elements.add(tag);

                    String messageErrorCodeString = this.getRequiredAttributeValue("messageError", currentAttrs, "code");
                    Integer messageErrorCode = this.parseIntegerValue("code", messageErrorCodeString);

                    if (operation instanceof DeliveryReportRequest) {
                        ((DeliveryReportRequest)operation).setMessageErrorCode(messageErrorCode);
                    } else {
                        throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "The messageError element is not supported for this operation type", this.operation);
                    }
                } else if (tag.equals("createDate")) {
                    // should only occur once
                    if (elements.contains(tag)) {
                        throw new SxmpParsingException(SxmpErrorCode.MULTIPLE_ELEMENTS_NOT_SUPPORTED, "Multiple [createDate] elements are not supported", this.operation);
                    }

                    String createDateText = parseCharacterData(tag, true);

                    DateTime createDate = null;
                    try {
                        createDate = dateTimeFormat.parseDateTime(createDateText);
                    } catch (Exception e) {
                        throw new SxmpParsingException(SxmpErrorCode.INVALID_VALUE, "Unable to convert createDate [" + createDateText + "] into a DateTime", this.operation);
                    }

                    if (operation instanceof DeliveryReportRequest) {
                        ((DeliveryReportRequest)operation).setCreateDate(createDate);
                    } else {
                        throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "The createDate element is not supported for this operation type", this.operation);
                    }
                } else if (tag.equals("finalDate")) {
                    // should only occur once
                    if (elements.contains(tag)) {
                        throw new SxmpParsingException(SxmpErrorCode.MULTIPLE_ELEMENTS_NOT_SUPPORTED, "Multiple [finalDate] elements are not supported", this.operation);
                    }

                    String finalDateText = parseCharacterData(tag, true);

                    DateTime finalDate = null;
                    try {
                        finalDate = dateTimeFormat.parseDateTime(finalDateText);
                    } catch (Exception e) {
                        throw new SxmpParsingException(SxmpErrorCode.INVALID_VALUE, "Unable to convert finalDate [" + finalDateText + "] into a DateTime", this.operation);
                    }

                    if (operation instanceof DeliveryReportRequest) {
                        ((DeliveryReportRequest)operation).setFinalDate(finalDate);
                    } else {
                        throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "The finalDate element is not supported for this operation type", this.operation);
                    }
                } else if (tag.equals("deliveryReport")) {
                    // parse the character data, check for duplicates
                    String deliveryReportText = parseCharacterData(tag, true);

                    Boolean deliveryReport = parseBooleanValue(tag, deliveryReportText);

                    if (operation instanceof SubmitRequest) {
                        ((SubmitRequest)operation).setDeliveryReport(deliveryReport);
                    } else {
                        throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "The [deliveryReport] element is not supported for this operation type", this.operation);
                    }
                } else if (tag.equals("sourceAddress")) {
                    // parse the character data, check for duplicates
                    String addrString = parseCharacterData(tag, true);
                    
                    // a type attribute is required
                    String addrTypeString = getRequiredAttributeValue(tag, currentAttrs, "type");

                    try {
                        // parse and create address
                        MobileAddress srcAddr = MobileAddressUtil.parseAddress(addrTypeString, addrString);

                        if (operation instanceof MessageRequest) {
                            ((MessageRequest)operation).setSourceAddress(srcAddr);
                        } else {
                            throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "The [sourceAddress] element is not supported for this operation type", this.operation);
                        }
                    } catch (SxmpErrorException e) {
                        throw new SxmpParsingException(e.getErrorCode(), e.getErrorMessage(), this.operation);
                    }
                } else if (tag.equals("destinationAddress")) {
                    // parse the character data, check for duplicates
                    String addrString = parseCharacterData(tag, true);

                    // a type attribute is required
                    String addrTypeString = getRequiredAttributeValue(tag, currentAttrs, "type");

                    try {
                        // parse and create address
                        MobileAddress destAddr = MobileAddressUtil.parseAddress(addrTypeString, addrString);

                        if (operation instanceof MessageRequest) {
                            ((MessageRequest)operation).setDestinationAddress(destAddr);
                        } else {
                            throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "The [destinationAddress] element is not supported for this operation type", this.operation);
                        }
                    } catch (SxmpErrorException e) {
                        throw new SxmpParsingException(e.getErrorCode(), e.getErrorMessage(), this.operation);
                    }
                } else if (tag.equals("text")) {
                    // parse the character data, check for duplicates
                    // NOTE: the text element can contain no chars (empty message)
                    String encodedText = parseCharacterData(tag, false);

                    // an encoding MUST be included
                    String encoding = getRequiredAttributeValue(tag, currentAttrs, "encoding");

                    TextEncoding te = TextEncoding.valueOfCharset(encoding);

                    // if no encoding was found, then this is not supported
                    if (te == null) {
                        throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_TEXT_ENCODING, "Unsupported text encoding [" + encoding + "] found", this.operation);
                    }

                    // try to convert hex encoding
                    String text = null;
                    try {
                        if (logger.isTraceEnabled()) {
                            logger.trace("textBeforeDecoding: [" + encodedText + "]");
                        }

                        byte[] bytes = HexUtil.toByteArray(encodedText);

                        // convert these bytes into a string using the provided charset
                        text = new String(bytes, te.getCharset());

                        if (logger.isTraceEnabled()) {
                            logger.debug("textAfterDecoding: [" + text + "]");
                        }
                    } catch (Exception e) {
                        throw new SxmpParsingException(SxmpErrorCode.TEXT_HEX_DECODING_FAILED, "Unable to decode hex data into text", this.operation);
                    }

                    if (operation instanceof MessageRequest) {
                        ((MessageRequest)operation).setText(text, te);
                    } else {
                        throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "The [text] element is not supported for this operation type", this.operation);
                    }
                } else {
                    throw new SxmpParsingException(SxmpErrorCode.UNSUPPORTED_ELEMENT, "Unsupported [" + tag + "] element found at depth [" + depth + "]", this.operation);
                }
            }

            // always decrement our depth
            depth--;
        }

        @Override
        public void ignorableWhitespace(char buf[], int offset, int len) throws SAXException {
            // do nothing
            logger.debug("ignorable whitespace included!");
        }

        @Override
        public void characters(char buf[], int offset, int len) throws SAXException {
            if (buf == null || buf.length <= 0) {
                // do nothing
                return;
            }

            // convert to string
            String text = new String(buf, offset, len);

            // now, only set a text value if its not empty
            if (text != null && !text.isEmpty()) {
                charBuffer.append(text);
            }
        }

        @Override
        public void warning(SAXParseException ex) {
            logger.warn(ex);
            logger.warn("WARNING @ " + getLocationString(ex) + " : " + ex.toString());
        }

        @Override
        public void error(SAXParseException ex) throws SAXException {
            // Save error and continue to report other errors
            if (error == null)
                error = ex;
            //logger.debug(ex);
            logger.error("ERROR @ " + getLocationString(ex) + " : " + ex.toString());
        }

        @Override
        public void fatalError(SAXParseException ex) throws SAXException {
            error = ex;
            //logger.debug(ex);
            logger.error("FATAL @ " + getLocationString(ex) + " : " + ex.toString());
            throw ex;
        }

        private String getLocationString(SAXParseException ex) {
            return ex.getSystemId() + " line:" + ex.getLineNumber() + " col:" + ex.getColumnNumber();
        }

        @Override
        public InputSource resolveEntity(String pid, String sid) {
            //logger.debug("resolveEntity(" + pid + ", " + sid + ")");
            /**
            if (logger.isDebugEnabled())
                logger.debug("resolveEntity(" + pid + ", " + sid + ")");

            if (sid!=null && sid.endsWith(".dtd"))
                _dtd=sid;

            URL entity = null;
            if (pid != null)
                entity = (URL) _redirectMap.get(pid);
            if (entity == null)
                entity = (URL) _redirectMap.get(sid);
            if (entity == null)
            {
                String dtd = sid;
                if (dtd.lastIndexOf('/') >= 0)
                    dtd = dtd.substring(dtd.lastIndexOf('/') + 1);

                if (logger.isDebugEnabled())
                    logger.debug("Can't exact match entity in redirect map, trying " + dtd);
                entity = (URL) _redirectMap.get(dtd);
            }

            if (entity != null)
            {
                try
                {
                    InputStream in = entity.openStream();
                    if (logger.isDebugEnabled())
                        logger.debug("Redirected entity " + sid + " --> " + entity);
                    InputSource is = new InputSource(in);
                    is.setSystemId(sid);
                    return is;
                }
                catch (IOException e)
                {
                    logger.warn(e);
                }
            }

             */
            return null;
        }
    }


}
