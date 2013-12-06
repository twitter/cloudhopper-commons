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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Interface for simple handling of XML.
 * 
 * @author joelauer
 */
public interface SimpleHandler {

    public void startElement(int depth, String uri, String tag, Attributes attrs) throws SAXException;

    public void endElement(int depth, String uri, String tag, Attributes attrs, StringBuilder charBuffer) throws SAXException;

}
