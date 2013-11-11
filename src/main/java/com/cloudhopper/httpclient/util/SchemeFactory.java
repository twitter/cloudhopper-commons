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

/*
 * #%L
 * ch-httpclient-util
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.CertificateException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
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

    /* 
     * Creates a SSL scheme with possibly a truststore and/or a keystore. 
     *
     * Truststore is primarily used to validate a server's SSL certificate.
     * And the keystore holds client certicates for SSL client authentication.
     *
     * Properly handles null keystore/truststores, and keystore/truststores without passwords.
     *
     * Added to support SSL mutual authentication in CA-Telus.
     */
    static public Scheme createHttpsScheme( File keystoreFile, 
					    String keystorePassword, 
					    File truststoreFile, 
					    String truststorePassword ) 
	throws NoSuchAlgorithmException, KeyStoreException, FileNotFoundException,
	       IOException, KeyManagementException, CertificateException, 
	       UnrecoverableKeyException
    {
	
	if( keystoreFile == null && truststoreFile == null ){
	    // To insure we don't break anything, if keystore and trust store is not specified, 
	    // call the legacy createHttpsScheme.
	    return createHttpsScheme();
	} else {
	    // Configure https scheme with a keystore to authenticate ourselves to the server
	    // and/or a truststore to verify the server's certificate.
	    KeyStore keystore = null;
	    if( keystoreFile != null ){
		keystore  = KeyStore.getInstance( KeyStore.getDefaultType() );        
		FileInputStream instream = new FileInputStream( keystoreFile ); 
		try {
		    // A null password is valid when the keystore does not have a password.
		    if( keystorePassword != null ){
			keystore.load(instream, keystorePassword.toCharArray());
		    } else {
			keystore.load(instream, null );
		    }
		} finally {
		    instream.close();
		}
		
	    }
	    KeyStore truststore = null;
	    if( truststoreFile != null ){
	        truststore = KeyStore.getInstance( KeyStore.getDefaultType() );        
		FileInputStream instream = new FileInputStream( truststoreFile ); 
		try {
		    // A null password is valid when the keystore does not have a password.
		    if( truststorePassword != null ){
			truststore.load(instream, truststorePassword.toCharArray());
		    } else {
			truststore.load(instream, null);
		    }
		} finally {
		    instream.close();
		}
	    }
	    // Not sure if identifing which params were passed in as null and calling the 
	    // appropriate constructor is necessary, because the Apache Docs don't describe
	    // what happens when we pass in null. Play it conservative rather than test the
	    // behavior. 
	    SSLSocketFactory socketFactory;
	    if( keystore != null && truststore != null ){
		socketFactory = new SSLSocketFactory( keystore, keystorePassword, truststore );
	    } else if( keystore != null ){
		socketFactory = new SSLSocketFactory( keystore, keystorePassword );
	    } else {
		socketFactory = new SSLSocketFactory( truststore );
	    }
	    return new Scheme("https", socketFactory, 443);
	} 
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
