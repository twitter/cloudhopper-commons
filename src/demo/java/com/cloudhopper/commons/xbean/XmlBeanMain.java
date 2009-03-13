package com.cloudhopper.commons.xbean;

// java imports
import java.io.*;

import org.apache.log4j.Logger;

/**
 *
 * @author joelauer
 */
public class XmlBeanMain {

    private static final Logger logger = Logger.getLogger(XmlBeanMain.class);
    
    public static void main(String[] args) throws Exception {

        StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
                .append("<configuration>\n")
                .append("   <port>joe</port>\n")
                .append("   <host><![CDATA[http://www.google.com/]]></host>\n")
                .append("</configuration>")
                .append("");

        SampleConfiguration config = new SampleConfiguration();

        
        XmlParser parser = new XmlParser();
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        logger.debug("Trying to parse...");
        XmlParser.Node rootNode = parser.parse(is);

        // now configure it
        XmlConfiguration xmlBean = new XmlConfiguration();
        xmlBean.configure(rootNode, config);

        logger.debug("port: " + config.port);
        logger.debug("host: " + config.host);

    }

    public static class SampleConfiguration {
        private int port;
        private String host;
    }

}
