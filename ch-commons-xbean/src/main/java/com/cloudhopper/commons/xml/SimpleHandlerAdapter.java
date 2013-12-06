package com.cloudhopper.commons.xml;

/*
 * #%L
 * ch-commons-xbean
 * %%
 * Copyright (C) 2012 Cloudhopper by Twitter
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author joelauer
 */
public class SimpleHandlerAdapter extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(SimpleHandlerAdapter.class);

    private SimpleHandler handler;
    private SAXParseException error;
    private int currentDepth;
    private Attributes currentAttrs;
    private StringBuilder charBuffer;

    public SimpleHandlerAdapter(SimpleHandler handler) {
        this(handler, 200);
    }

    public SimpleHandlerAdapter(SimpleHandler handler, int charBufferSize) {
        this.handler = handler;
        this.currentDepth = -1;
        this.charBuffer = new StringBuilder(charBufferSize);
    }

    public SAXParseException getError() {
        return this.error;
    }

    @Override
    public void processingInstruction(String target, String value) {
        logger.trace("processingInstruction target=[" + target + "], value=[" + value + "]");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
        //
        // get the tag stripped of any namespace
        //
        String tag = (uri == null || uri.length() == 0) ? qName : localName;

        //
        // always increment the current depth
        //
        this.currentDepth++;

        //
        // reset character buffer at start of new element
        //
        charBuffer.setLength(0);

        //
        // reference to current attributes -- will be used during an endElement
        //
        this.currentAttrs = attrs;

        logger.trace("startElement depth=[" + currentDepth + "], tag=[" + tag + "], uri=[" + uri + "], attrs.length=[" + currentAttrs.getLength() + "]");

        handler.startElement(currentDepth, uri, tag, currentAttrs);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //
        // get the tag stripped of any namespace
        //
        String tag = (uri == null || uri.length() == 0) ? qName : localName;

        logger.trace("endElement depth=[" + currentDepth + "], tag=[" + tag + "], uri=[" + uri + "], attrs.length=[" + currentAttrs.getLength() + "], charBuffer.length=[" + charBuffer.length() + "]");

        handler.endElement(currentDepth, uri, tag, currentAttrs, charBuffer);

        //
        // always decrement the current depth
        //
        this.currentDepth--;
    }

    @Override
    public void ignorableWhitespace(char buf[], int offset, int len) throws SAXException {
        logger.trace("ignorableWhitespace buf.offset=[" + offset + "], buf.len=[" + len + "]");
    }

    @Override
    public void characters(char buf[], int offset, int len) throws SAXException {
        if (buf == null || buf.length <= 0) {
            // do nothing
            return;
        }

        charBuffer.append(buf, offset, len);
    }

    @Override
    public void warning(SAXParseException ex) {
        logger.warn("WARNING @ " + getLocationString(ex) + " : " + ex.toString(), ex);
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
        // Save error and continue to report other errors
        if (error == null)
            error = ex;
        logger.error("ERROR @ " + getLocationString(ex) + " : " + ex.toString(), ex);
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        error = ex;
        logger.error("FATAL @ " + getLocationString(ex) + " : " + ex.toString(), ex);
        throw ex;
    }

    private String getLocationString(SAXParseException ex) {
        return ex.getSystemId() + " line:" + ex.getLineNumber() + " col:" + ex.getColumnNumber();
    }

    @Override
    public InputSource resolveEntity(String pid, String sid) {
        logger.trace("resolveEntity pid=[" + pid + "], sid=[" + sid + "]");
        return null;
    }
}
