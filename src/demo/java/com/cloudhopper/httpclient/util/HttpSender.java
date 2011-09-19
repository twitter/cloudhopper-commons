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
public class HttpSender {
    private static final Logger logger = Logger.getLogger(HttpSender.class);

    static public Response postXml(String url, String username, String password, String requestXml) throws Exception {
        //
        // trust any SSL connection
        //
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

        HttpPost post = new HttpPost(url);

        StringEntity postEntity = new StringEntity(requestXml, "ISO-8859-1");
        postEntity.setContentType("text/xml; charset=\"ISO-8859-1\"");
        post.addHeader("SOAPAction", "\"\"");
        post.setEntity(postEntity);

        long start = System.currentTimeMillis();

        client.getCredentialsProvider().setCredentials(
            new AuthScope(null, AuthScope.ANY_PORT),
            new UsernamePasswordCredentials(username, password));

        BasicHttpContext localcontext = new BasicHttpContext();

        // Generate BASIC scheme object and stick it to the local
        // execution context
        BasicScheme basicAuth = new BasicScheme();
        localcontext.setAttribute("preemptive-auth", basicAuth);

        // Add as the first request interceptor
        client.addRequestInterceptor(new PreemptiveAuth(), 0);

        HttpResponse httpResponse = client.execute(post, localcontext);
        HttpEntity responseEntity = httpResponse.getEntity();

        Response rsp = new Response();

        // set the status line and reason
        rsp.statusCode = httpResponse.getStatusLine().getStatusCode();
        rsp.statusLine = httpResponse.getStatusLine().getReasonPhrase();

        // get an input stream
        rsp.body = EntityUtils.toString(responseEntity);

        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        client.getConnectionManager().shutdown();

        return rsp;
    }

    static class PreemptiveAuth implements HttpRequestInterceptor {

        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
            
            AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
            
            // If no auth scheme avaialble yet, try to initialize it preemptively
            if (authState.getAuthScheme() == null) {
                AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                if (authScheme != null) {
                    Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
                    if (creds == null) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }
                    authState.setAuthScheme(authScheme);
                    authState.setCredentials(creds);
                }
            }
            
        }
        
    }

    static public class Response {
        public int statusCode;
        public String statusLine;
        public String body;
    }
}
