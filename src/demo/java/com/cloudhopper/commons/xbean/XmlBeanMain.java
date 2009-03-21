package com.cloudhopper.commons.xbean;

// java imports
import java.io.*;

import java.net.MalformedURLException;
import java.net.URL;
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
                .append("   <port>80</port>\n")
                .append("   <port2>0</port2>\n")
                .append("   <host>https://www.google.com</host>\n")
                .append("</configuration>")
                .append("");

        SampleConfiguration config = new SampleConfiguration();

        
        XmlParser parser = new XmlParser();
        ByteArrayInputStream is = new ByteArrayInputStream(string0.toString().getBytes());
        logger.debug("Trying to parse...");
        XmlParser.Node rootNode = parser.parse(is);

        // now configure it
        XmlBean bean = new XmlBean();
        
        // set various options for this particular bean
        bean.setRootTag("configuration");
        bean.setAccessPrivateProperties(false);

        bean.configure(rootNode, config);

        logger.debug("port: " + config.port);
        logger.debug("host: " + config.host);

    }

    public static class SampleConfiguration {
        
        private int port;
        private String host;

        public void setPort(int value) throws ConfigurationException {
            if (value <= 0) {
                throw new ConfigurationException("Port must be > 0");
            }
            this.port = value;
        }

        public void setHost(String value) throws MalformedURLException {
            URL aURL = new URL(value);
            /**
            System.out.println("protocol = " + aURL.getProtocol());
            System.out.println("authority = " + aURL.getAuthority());
            System.out.println("host = " + aURL.getHost());
            System.out.println("port = " + aURL.getPort());
            System.out.println("path = " + aURL.getPath());
            System.out.println("query = " + aURL.getQuery());
            System.out.println("filename = " + aURL.getFile());
            System.out.println("ref = " + aURL.getRef());
             */
            this.host = value;
        }
    }

    public static class ConfigurationException extends Exception {
        public ConfigurationException(String msg) {
            super(msg);
        }
    }

}
