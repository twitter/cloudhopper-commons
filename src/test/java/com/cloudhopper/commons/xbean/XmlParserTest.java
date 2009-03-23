
package com.cloudhopper.commons.xbean;

// third party imports
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.*;
import org.apache.log4j.Logger;

// my imports

public class XmlParserTest {

    private static final Logger logger = Logger.getLogger(XmlParserTest.class);

    @Test
    public void includesXPath() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
            .append("<submitRequest sequenceId=\"1000\">\n")
            .append("   <!-- this is a comment -->\n")
            .append("   <account username=\"testaccount\" password=\"testpassword\"/>\n")
            .append("   <option />\n")
            .append("   <messageRequest referenceId=\"MYMESSREF\">\n")
            .append("       <sourceAddress>+13135551212</sourceAddress>\n")
            .append("       <destinationAddress>+13135551200</destinationAddress>\n")
            .append("       <text><![CDATA[Hello World]]></text>\n")
            .append("   </messageRequest>\n")
            .append("</submitRequest>")
            .append("");

        XmlParser parser = new XmlParser();
        parser.addIncludeXPath("/submitRequest/messageRequest/*");

        XmlParser.Node root = parser.parse(string0.toString());

        Assert.assertEquals(1, root.getChildrenSize());
        Assert.assertEquals("submitRequest", root.getTag());
        Assert.assertEquals("messageRequest", root.getChild(0).getTag());
        Assert.assertEquals("sourceAddress", root.getChild(0).getChild(0).getTag());
        Assert.assertEquals("destinationAddress", root.getChild(0).getChild(1).getTag());
        Assert.assertEquals("text", root.getChild(0).getChild(2).getTag());

        parser = new XmlParser();
        parser.addIncludeXPath("/submitRequest/messageRequest/destinationAddress");
        root = parser.parse(string0.toString());

        Assert.assertEquals(1, root.getChildrenSize());
        Assert.assertEquals("submitRequest", root.getTag());
        Assert.assertEquals("messageRequest", root.getChild(0).getTag());
        Assert.assertEquals("destinationAddress", root.getChild(0).getChild(0).getTag());

        parser = new XmlParser();
        parser.addIncludeXPath("/submitRequest/option");
        parser.addIncludeXPath("/submitRequest/messageRequest/destinationAddress");
        root = parser.parse(string0.toString());

        Assert.assertEquals(2, root.getChildrenSize());
        Assert.assertEquals("submitRequest", root.getTag());
        Assert.assertEquals("option", root.getChild(0).getTag());
        Assert.assertEquals("messageRequest", root.getChild(1).getTag());
        Assert.assertEquals("destinationAddress", root.getChild(1).getChild(0).getTag());
    }

    @Test
    public void excludesXPath() throws Exception {
        StringBuilder string0 = new StringBuilder(200)
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
            .append("<submitRequest sequenceId=\"1000\">\n")
            .append("   <!-- this is a comment -->\n")
            .append("   <account username=\"testaccount\" password=\"testpassword\"/>\n")
            .append("   <option />\n")
            .append("   <messageRequest referenceId=\"MYMESSREF\">\n")
            .append("       <sourceAddress>+13135551212</sourceAddress>\n")
            .append("       <destinationAddress>+13135551200</destinationAddress>\n")
            .append("       <text><![CDATA[Hello World]]></text>\n")
            .append("   </messageRequest>\n")
            .append("</submitRequest>")
            .append("");

        XmlParser parser = new XmlParser();
        parser.addExcludeXPath("/submitRequest/account");
        parser.addExcludeXPath("/submitRequest/option");

        XmlParser.Node root = parser.parse(string0.toString());

        Assert.assertEquals(1, root.getChildrenSize());
        Assert.assertEquals("submitRequest", root.getTag());
        Assert.assertEquals("messageRequest", root.getChild(0).getTag());
        Assert.assertEquals("sourceAddress", root.getChild(0).getChild(0).getTag());
        Assert.assertEquals("destinationAddress", root.getChild(0).getChild(1).getTag());
        Assert.assertEquals("text", root.getChild(0).getChild(2).getTag());

        parser = new XmlParser();
        parser.addExcludeXPath("/submitRequest/account");
        parser.addExcludeXPath("/submitRequest/option");
        parser.addExcludeXPath("/submitRequest/messageRequest/sourceAddress");
        parser.addExcludeXPath("/submitRequest/messageRequest/text");
        root = parser.parse(string0.toString());

        Assert.assertEquals(1, root.getChildrenSize());
        Assert.assertEquals("submitRequest", root.getTag());
        Assert.assertEquals("messageRequest", root.getChild(0).getTag());
        Assert.assertEquals("destinationAddress", root.getChild(0).getChild(0).getTag());

        parser = new XmlParser();
        parser.addExcludeXPath("/submitRequest/account");
        parser.addExcludeXPath("/submitRequest/messageRequest/sourceAddress");
        parser.addExcludeXPath("/submitRequest/messageRequest/text");
        root = parser.parse(string0.toString());

        Assert.assertEquals(2, root.getChildrenSize());
        Assert.assertEquals("submitRequest", root.getTag());
        Assert.assertEquals("option", root.getChild(0).getTag());
        Assert.assertEquals("messageRequest", root.getChild(1).getTag());
        Assert.assertEquals("destinationAddress", root.getChild(1).getChild(0).getTag());
    }
}
