
package com.cloudhopper.httpclient.util;

import java.io.IOException;

import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.log4j.Logger;

import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;

/**
 *
 * @author joelauer
 */
public class HttpPostMain {
    private static final Logger logger = Logger.getLogger(HttpPostMain.class);

    static public void main(String[] args) throws Exception {
        //
        // target urls
        //
        String strURL = "http://209.226.31.233:9009/SendSmsService/b98183b99a1f473839ce569c78b84dbd";

        // Username: Twitter
        // Password: Twitter123

        TrustManager easyTrustManager = new X509TrustManager() {
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
                // allow all
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
                // allow all
            }

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);

        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);
        SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme https = new Scheme("https", sf, 443);

        //SchemeRegistry sr = new SchemeRegistry();
        //sr.register(http);
        //sr.register(https);

        // create and initialize scheme registry
        //SchemeRegistry schemeRegistry = new SchemeRegistry();
        //schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        // create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        //ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(schemeRegistry);
        //cm.setMaxTotalConnections(1);

        DefaultHttpClient client = new DefaultHttpClient();

        client.getConnectionManager().getSchemeRegistry().register(https);


//        for (int i = 0; i < 1; i++) {
            //
            // create a new ticket id
            //
            //String ticketId = TicketUtil.generate(1, System.currentTimeMillis());

            /**
            StringBuilder string0 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n")
                .append("       <S:Header>\n")
                .append("               <ns3:TransactionID xmlns:ns4=\"http://vmp.vzw.com/schema\"\n")
                .append("xmlns:ns3=\"http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-6-MM7-1-4\">" + ticketId + "</ns3:TransactionID>\n")
                .append("       </S:Header>\n")
                .append("       <S:Body>\n")
                .append("               <ns2:OptinReq xmlns:ns4=\"http://schemas.xmlsoap.org/soap/envelope/\"\n")
                .append("xmlns:ns3=\"http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-6-MM7-1-4\"\n")
                .append("xmlns:ns2=\"http://vmp.vzw.com/schema\">\n")
                .append("                      <ns2:VASPID>twitter</ns2:VASPID>\n")
                .append("                      <ns2:VASID>tm33t!</ns2:VASID>\n")
                .append("                      <ns2:ShortCode>800080008001</ns2:ShortCode>\n")
                .append("                      <ns2:Number>9257089093</ns2:Number>\n")
                .append("                      <ns2:Source>provider</ns2:Source>\n")
                .append("                      <ns2:Message/>\n")
                .append("               </ns2:OptinReq>\n")
                .append("       </S:Body>\n")
                .append("</S:Envelope>");
             */

            // simple send sms
            StringBuilder string1 = new StringBuilder(200)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:loc=\"http://www.csapi.org/schema/parlayx/sms/send/v2_3/local\">\n")
                .append("   <soapenv:Header/>\n")
                .append("   <soapenv:Body>\n")
                .append("      <loc:sendSms>\n")
                .append("         <loc:addresses>tel:+16472260233</loc:addresses>\n")
                .append("         <loc:senderName>6388</loc:senderName>\n")
                .append("         <loc:message>Test Message &</loc:message>\n")
                .append("      </loc:sendSms>\n")
                .append("   </soapenv:Body>\n")
                .append("</soapenv:Envelope>\n");

            // startSmsNotification - place to deliver SMS to
            

            String req = string1.toString();
            logger.debug("Request XML -> \n" + req);


            HttpPost post = new HttpPost(strURL);

            StringEntity postEntity = new StringEntity(req, "ISO-8859-1");
            postEntity.setContentType("text/xml; charset=\"ISO-8859-1\"");
            post.addHeader("SOAPAction", "\"\"");
            post.setEntity(postEntity);

            long start = System.currentTimeMillis();

            client.getCredentialsProvider().setCredentials(
                new AuthScope("209.226.31.233", AuthScope.ANY_PORT), 
                new UsernamePasswordCredentials("Twitter", "Twitter123"));

            
            BasicHttpContext localcontext = new BasicHttpContext();

            // Generate BASIC scheme object and stick it to the local 
            // execution context
            BasicScheme basicAuth = new BasicScheme();
            localcontext.setAttribute("preemptive-auth", basicAuth);
        
            // Add as the first request interceptor
            client.addRequestInterceptor(new PreemptiveAuth(), 0);

            HttpResponse httpResponse = client.execute(post, localcontext);
            HttpEntity responseEntity = httpResponse.getEntity();

            //
            // was the request OK?
            //
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                logger.error("Request failed with StatusCode=" + httpResponse.getStatusLine().getStatusCode());
            }

            // get an input stream
            String responseBody = EntityUtils.toString(responseEntity);

            long stop = System.currentTimeMillis();

            logger.debug("----------------------------------------");
            logger.debug("Response took " + (stop-start) + " ms");
            logger.debug(responseBody);
            logger.debug("----------------------------------------");
//        }

        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        client.getConnectionManager().shutdown();
    }

    static class PreemptiveAuth implements HttpRequestInterceptor {

        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
            
            AuthState authState = (AuthState) context.getAttribute(
                    ClientContext.TARGET_AUTH_STATE);
            
            // If no auth scheme avaialble yet, try to initialize it preemptively
            if (authState.getAuthScheme() == null) {
                AuthScheme authScheme = (AuthScheme) context.getAttribute(
                        "preemptive-auth");
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                        ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(
                        ExecutionContext.HTTP_TARGET_HOST);
                if (authScheme != null) {
                    Credentials creds = credsProvider.getCredentials(
                            new AuthScope(
                                    targetHost.getHostName(), 
                                    targetHost.getPort()));
                    if (creds == null) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }
                    authState.setAuthScheme(authScheme);
                    authState.setCredentials(creds);
                }
            }
            
        }
        
    }
}
