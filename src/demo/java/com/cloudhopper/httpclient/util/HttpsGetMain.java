
package com.cloudhopper.httpclient.util;


import org.apache.log4j.Logger;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;

/**
 *
 * @author joelauer
 */
public class HttpsGetMain {
    private static final Logger logger = Logger.getLogger(HttpsGetMain.class);

    static public void main(String[] args) throws Exception {
        //
        // target urls
        //
        String strURL = "https://localhost:9443/";


        DefaultHttpClient client = new DefaultHttpClient();

        // setup this client to not verify SSL certificates
        HttpClientFactory.configureWithNoSslCertificateVerification(client);

        // add pre-emptive authentication
        HttpClientFactory.configureWithPreemptiveBasicAuth(client, "user0", "pass0");

        HttpGet get = new HttpGet(strURL);

        HttpResponse httpResponse = client.execute(get);

        HttpEntity responseEntity = httpResponse.getEntity();

        //
        // was the request OK?
        //
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            logger.error("Request failed with StatusCode=" + httpResponse.getStatusLine().getStatusCode());
        }

        // get an input stream
        String responseBody = EntityUtils.toString(responseEntity);

        logger.debug("----------------------------------------");
        logger.debug(responseBody);
        logger.debug("----------------------------------------");
        
        client.getConnectionManager().shutdown();
    }
}
