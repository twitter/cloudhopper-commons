package com.cloudhopper.commons.xbean;

// java imports
import java.io.*;

import org.apache.log4j.Logger;

/**
 *
 * @author joelauer
 */
public class XmlParserMain {

    private static final Logger logger = Logger.getLogger(XmlParserMain.class);
    
    public static void main(String[] args) throws Exception {

        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                //.append("<!DOCTYPE chapter PUBLIC \"-//OASIS//DTD DocBook XML//EN\" \"../dtds/docbookx.dtd\">")
		//.append("<!DOCTYPE chapter PUBLIC \"-//OASIS//DTD DocBook XML//EN\">")
                .append("<submitRequest sequenceId=\"1000\">\n")
                .append("   <!-- this is a comment -->\n")
                .append("   <account username=\"testaccount\" password=\"testpassword\"/>\n")
                .append("   <option />\n")
                .append("   <messageRequest referenceId=\"MYMESSREF\">\n")
                //.append("       <sourceAddress>+13135551212</sourceAddress>\n")
                .append("       <destinationAddress>+13135551200</destinationAddress>\n")
                .append("       <text><![CDATA[Hello World]]></text>\n")
                .append("   </messageRequest>\n")
                .append("</submitRequest>")
                .append("");

        XmlParser parser = new XmlParser();

        parser.addIncludeXPath("/submitRequest/messageRequest/*");
        //parser.addIncludeXPath("/submitRequest/account");
        //parser.addIncludeXPath("/submitRequest/option");

        //parser.setTrimText(false);
        //parser.setValidating(false);
        //parser.setXpath("messageRequest");

        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());

        logger.debug("Trying to parse...");
        XmlParser.Node root = parser.parse(is);

        dump(root, 0);

        //logger.debug(node.getPath());

        //String sequenceIdVal = node.getAttribute("sequenceId");


        //logger.debug(node.getPath());

    }

    private static void dump(XmlParser.Node node, int indent) {

        //logger.debug(node.getPath());

        StringBuilder string0 = new StringBuilder(200);

        // create indent string
        for (int i = 0; i < indent; i++) {
            string0.append(" ");
        }
        String indentString = string0.toString();
        string0.setLength(0);

        // create element
        string0.append("<");
        string0.append(node.getTag());

        // add attributes
        if (node.hasAttributes()) {
            for (XmlParser.Attribute attr : node.getAttributes()) {
                string0.append(" ");
                string0.append(attr.getName());
                string0.append("=\"");
                string0.append(attr.getValue());
                string0.append("\"");
            }
        }

        // if no children and no text value, then we should add the closing tag "/>"
        if (!node.hasChildren() && !node.hasText()) {
            string0.append("/>");
        } else {


            string0.append(">");
        }

        // add "text" value
        if (node.hasText()) {
            string0.append(node.getText());
            // if no children, make sure to add final tag
            if (!node.hasChildren()) {
                string0.append("</");
                string0.append(node.getTag());
                string0.append(">");
            }
        }

        logger.debug(indentString + string0.toString());

        // now recursively add all children
        for (int i = 0; i < node.getChildrenSize(); i++) {
            dump(node.getChild(i), indent+1);
        }

        // do we need to add the final tag?
        if (node.hasChildren()) {
            logger.debug(indentString + "</" + node.getTag() + ">");
        }
    }

}
