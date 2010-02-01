
package com.cloudhopper.httpclient.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * Utility class for creating schemes to be used with the Apacha Jakarta
 * HttpClient.
 *
 * @author joelauer
 */
public class SchemeFactory {

    private SchemeFactory() {
        // only static methods
    }

    static public Scheme createHttpScheme() {
        return new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
    }

    static public Scheme createHttpsScheme() throws NoSuchAlgorithmException {
        SSLSocketFactory socketFactory = new SSLSocketFactory(SSLContext.getDefault());
        return new Scheme("https", socketFactory, 443);
    }

    static public Scheme createDoNotVerifyHttpsScheme() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager sslTrustManager = new DoNotVerifySSLCertificateTrustManager();
        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[] { sslTrustManager }, null);
        SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return new Scheme("https", sf, 443);
    }
    
}
