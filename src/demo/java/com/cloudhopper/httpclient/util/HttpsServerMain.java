
package com.cloudhopper.httpclient.util;

import com.sun.grizzly.SSLConfig;
import com.sun.grizzly.http.Management;
import org.apache.log4j.Logger;
import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import java.io.IOException;
import java.util.Enumeration;
import javax.management.ObjectName;

/**
 *
 * @author joelauer
 */
public class HttpsServerMain extends GrizzlyAdapter {
    private static final Logger logger = Logger.getLogger(HttpsServerMain.class);

    static public void main(String[] args) throws Exception {

        int port = 9443;

        GrizzlyWebServer gws = new GrizzlyWebServer(port, ".", true);

        // need to create SSL config
        SSLConfig sslConfig = new SSLConfig();

        sslConfig.setKeyStorePass("changeit");
        sslConfig.setKeyStoreFile("./test.jks"); //replace with your keystore
        
        gws.setSSLConfig(sslConfig);

        logger.info("Starting HTTPS server on port " + port);

        gws.enableJMX(new Management() {

            public void registerComponent(Object bean, ObjectName oname, String type)
                    throws Exception {
                logger.info("Register JMX: oname=" + oname + ", type=" + type);
                //Registry.getRegistry().registerComponent(bean,oname,type);
            }

            public void unregisterComponent(ObjectName oname) throws Exception {
                //Registry.getRegistry().unregisterComponent(oname);
            }
        });


        try {
            GrizzlyAdapter ga = new HttpsServerMain();
            gws.addGrizzlyAdapter(ga, new String[] {"/"});
            gws.start();
        } catch (IOException e){
            logger.error("", e);
        }
    }

    @Override
    public void service(GrizzlyRequest request, GrizzlyResponse response) {
        try {
            response.getWriter().println("Success, this is SSL!");

            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = (String)headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                logger.debug(headerName + ": " + headerValue);
            }

            //
            // process input stream
            //
            //boolean ok = ParlayXSession.process(this, request.getInputStream(), response.getWriter(), true);

            //if (!ok) {
            //    response.setStatus(500);
            //}
        } catch (Exception e) {
            logger.error("Failed to process request", e);
        }
    }
}
