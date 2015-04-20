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

import com.cloudhopper.commons.util.HexUtil;
import com.cloudhopper.commons.util.StringUtil;
import com.cloudhopper.sxmp.util.XmlEscapeUtil;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author joelauer
 */
public class SxmpWriter {

    static private void writeXmlHeader(Writer out, Operation operation) throws IOException {
        // v1.1 needs to be UTF-8; v1.0 was unspecified
        if (operation.getVersion().equals(SxmpParser.VERSION_1_0))
            out.write("<?xml version=\"1.0\"?>\n");
        else
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    }

    static private void writeOperationStartTag(Writer out, Operation.Type type) throws IOException {
        if (type == null) {
            throw new NullPointerException("Operation type cannot be null");
        }

        out.write("<operation type=\"" + type.getValue() + "\">\n");
    }

    static private void writeOperationEndTag(Writer out) throws IOException {
        out.write("</operation>\n");
    }

    static private void writeRequestResponseTag(Writer out, Operation operation) throws IOException {
        out.write(operation.getType().getValue());
        if (operation.isRequest()) {
            out.write("Request");
        } else {
            out.write("Response");
        }
    }

    static private void writeRequestResponseStartTag(Writer out, Operation operation) throws IOException {
        out.write(" <");
        writeRequestResponseTag(out, operation);

        if (operation.getReferenceId() != null) {
            out.write(" referenceId=\"");
            out.write(operation.getReferenceId());
            out.write("\"");
        }

        out.write(">\n");
    }

    static private void writeRequestResponseEndTag(Writer out, Operation operation) throws IOException {
        out.write(" </");
        writeRequestResponseTag(out, operation);
        out.write(">\n");
    }

    static private void writeErrorElement(Writer out, Response response) throws IOException {
        out.write("  <error code=\"");
        out.write(response.getErrorCode().toString());
        out.write("\" message=\"");
        out.write(StringUtil.escapeXml(response.getErrorMessage()));
        out.write("\"/>\n");
    }

    /**
    static public byte[] createByteArray(Operation operation) throws SxmpErrorException, IOException {
        // most requests will be ~1000 bytes
        //FastByteArrayOutputStream baos = new FastByteArrayOutputStream(1000);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
        OutputStreamWriter out = new OutputStreamWriter(baos, "ISO-8859-1");
        SxmpWriter.write(out, operation);
        byte[] data = baos.toByteArray();
        //out.close();
        //baos.close();
        return data;
    }
     */
    static public String createString(Operation operation) throws SxmpErrorException, IOException {
        StringWriter sw = new StringWriter(1000);
        SxmpWriter.write(sw, operation);
        return sw.toString();
    }

    static public void write(Writer out, Operation operation) throws SxmpErrorException, IOException {
	write(out, operation, true);
    }
    
    static public void write(Writer out, Operation operation, boolean withXmlHeader) throws SxmpErrorException, IOException {
        if (withXmlHeader) writeXmlHeader(out, operation);
        writeOperationStartTag(out, operation.getType());

        // let's validate this operation is valid
        operation.validate();

        if (operation instanceof ErrorResponse) {
            // write out just an ErrorResponse
            writeErrorElement(out, (ErrorResponse)operation);

        } else if (operation instanceof Request) {
            // any request requires an account to be printed out
            Request request = (Request)operation;

            if (request.getAccount() != null) {
                out.write(" <account username=\"");
                out.write(StringUtil.escapeXml(request.getAccount().getUsername()));
                out.write("\" password=\"");
                out.write(StringUtil.escapeXml(request.getAccount().getPassword()));
                out.write("\"/>\n");
            }

            if (request.getApplication() != null) {
                out.write(" <application>");
                out.write(StringUtil.escapeXml(request.getApplication().getName()));
                out.write("</application>\n");
            }

            writeRequestResponseStartTag(out, operation);

            // tickets are now supported any any request
            if (request.getTicketId() != null) {
                out.write("  <ticketId>");
                out.write(StringUtil.escapeXml(request.getTicketId()));
                out.write("</ticketId>\n");
            }

            if (operation instanceof DeliveryReportRequest) {
                DeliveryReportRequest deliveryRequest = (DeliveryReportRequest)operation;

                if (deliveryRequest.getStatus() != null) {
                    out.write("  <status code=\"");
                    out.write(deliveryRequest.getStatus().getCode().toString());
                    out.write("\" message=\"");
                    out.write(StringUtil.escapeXml(deliveryRequest.getStatus().getMessage()));
                    out.write("\"/>\n");
                }

                if (deliveryRequest.getMessageErrorCode() != null) {
                    out.write("  <messageError code=\"");
                    out.write(deliveryRequest.getMessageErrorCode().toString());
                    out.write("\"/>\n");
                }

                if (deliveryRequest.getCreateDate() != null) {
                    out.write("  <createDate>");
                    out.write(SxmpParser.dateTimeFormat.print(deliveryRequest.getCreateDate()));
                    out.write("</createDate>\n");
                }

                if (deliveryRequest.getFinalDate() != null) {
                    out.write("  <finalDate>");
                    out.write(SxmpParser.dateTimeFormat.print(deliveryRequest.getFinalDate()));
                    out.write("</finalDate>\n");
                }

            } else if (operation instanceof MessageRequest) {
                MessageRequest messageRequest = (MessageRequest)operation;

                if (messageRequest.getOperatorId() != null) {
                    out.write("  <operatorId>");
                    out.write(messageRequest.getOperatorId().toString());
                    out.write("</operatorId>\n");
                }

                if (messageRequest.getPriority() != null) {
                    out.write("  <priority>");
                    out.write(messageRequest.getPriority().getPriorityFlag().toString());
                    out.write("</priority>\n");
                }

                if (operation instanceof SubmitRequest) {
                    SubmitRequest submitRequest = (SubmitRequest)operation;
                    if (submitRequest.getDeliveryReport() != null) {
                        out.write("  <deliveryReport>");
                        out.write(submitRequest.getDeliveryReport().toString().toLowerCase());
                        out.write("</deliveryReport>\n");
                    }
                }

                if (messageRequest.getSourceAddress() != null) {
                    out.write("  <sourceAddress type=\"");
                    out.write(messageRequest.getSourceAddress().getType().toString().toLowerCase());
                    out.write("\">");
                    out.write(messageRequest.getSourceAddress().getAddress());
                    out.write("</sourceAddress>\n");
                }

                if (messageRequest.getDestinationAddress() != null) {
                    out.write("  <destinationAddress type=\"");
                    out.write(messageRequest.getDestinationAddress().getType().toString().toLowerCase());
                    out.write("\">");
                    // push dest address is not guaranteed to be XML-safe
                    // do minimum-necessary escaping? or better to stay standardized & escape everything?
                    if (messageRequest.getDestinationAddress().getType() == MobileAddress.Type.PUSH_DESTINATION) {
                        out.write(XmlEscapeUtil.escapeTextXml(messageRequest.getDestinationAddress().getAddress()));
                    } else {
                        out.write(messageRequest.getDestinationAddress().getAddress());
                    }
                    out.write("</destinationAddress>\n");
                }

                if (messageRequest.getText() != null) {
                    String charset = messageRequest.getTextEncoding().getCharset();
                    out.write("  <text encoding=\"");
                    out.write(charset);
                    out.write("\">");
                    out.write(HexUtil.toHexString(messageRequest.getText().getBytes(charset)));
                    out.write("</text>\n");
                }

                // this is a v1.1 option; only write if version == 1.1
                if (messageRequest.getOptionalParams() != null &&
                        messageRequest.getVersion().equals(SxmpParser.VERSION_1_1)) {

                    //String charset = messageRequest.getTextEncoding().getCharset();
                    out.write("  <optionalParams>");
                    JSONObject jsonObj = new JSONObject(messageRequest.getOptionalParams());
                    // JSON encoding is not XML-safe
                    // do minimum-necessary escaping? or better to stay standardized & escape everything?
                    // tradeoff: all " and ' become &quot; and &apos; and all json strings are quoted.
                    out.write(XmlEscapeUtil.escapeTextXml(jsonObj.toString()));
                    out.write("</optionalParams>\n");
                }
            }

            writeRequestResponseEndTag(out, operation);
        } else {
            writeRequestResponseStartTag(out, operation);

            writeErrorElement(out, (Response)operation);

            // tickets on responses are now supported for submits or delivers
            if (operation instanceof SubmitResponse || operation instanceof DeliverResponse) {
                if (operation.getTicketId() != null) {
                    out.write("  <ticketId>");
                    out.write(StringUtil.escapeXml(operation.getTicketId()));
                    out.write("</ticketId>\n");
                }
            }

            writeRequestResponseEndTag(out, operation);
        }

        writeOperationEndTag(out);
    }
}
