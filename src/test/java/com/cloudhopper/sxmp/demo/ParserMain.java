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

package com.cloudhopper.sxmp.demo;

import com.cloudhopper.sxmp.*;
import com.cloudhopper.commons.util.DecimalUtil;
import java.io.ByteArrayInputStream;
import org.apache.http.HttpVersion;
import org.apache.log4j.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

/**
 *
 * @author joelauer
 */
public class ParserMain {
    private static final Logger logger = Logger.getLogger(ParserMain.class);

    static public void main(String[] args) throws Exception {
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
            .append("<ticketId>&x20;</ticketId>\n")
            .append("</deliverRequest>    \n")
            .append("</operation >\n")
            .append("");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        SxmpParser parser = new SxmpParser();

        //try {
            Operation operation = parser.parse(is);
            //Assert.fail();
        //} catch (SxmpParsingException e) {
            // correct behavior
            //Assert.assertEquals(SxmpErrorCode.MISSING_REQUIRED_ATTRIBUTE, e.getErrorCode());
            //Assert.assertThat(e.getMessage(), JUnitMatchers.containsString("The attribute [username] is required with the [account] element"));
            //Assert.assertNotNull(e.getOperation());
         //   PartialOperation partial = (PartialOperation)e.getOperation();
            //Assert.assertEquals(Operation.Type.SUBMIT, partial.getType());
        //}
            
        logger.debug("done!");
    }
}
