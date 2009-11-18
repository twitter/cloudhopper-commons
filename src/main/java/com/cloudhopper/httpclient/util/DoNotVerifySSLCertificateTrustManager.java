
package com.cloudhopper.httpclient.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/**
 * TrustManager that will trust any SSL certificate.  This implementation will
 * not verify any attributes of the SSL certificate.  This was based on a sample
 * provided on the Apache website.
 *
 * @author joelauer
 */
public class DoNotVerifySSLCertificateTrustManager implements X509TrustManager {

    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // do not verify
    }

    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // do not verify
    }

    public X509Certificate[] getAcceptedIssuers() {
        // do not verify by returning null
        return null;
    }

}
