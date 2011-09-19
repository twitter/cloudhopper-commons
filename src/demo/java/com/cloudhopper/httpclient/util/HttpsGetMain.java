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
