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
