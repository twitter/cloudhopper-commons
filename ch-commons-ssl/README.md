Cloudhopper by Twitter
============================

cloudhopper-commons-ssl
-----------------------

Utility Java classes for using SSL. Originally part of cloudhopper-smpp, but pulled out
for use elsewhere.

Contributors
------------

Garth Patil

Installation
------------

Library versions are published to the Maven Central Repository.
Just add the following dependency to your project maven pom.xml:

    <dependency>
      <groupId>com.cloudhopper</groupId>
      <artifactId>ch-commons-ssl</artifactId>
      <version>[1.0.0,)</version>
    </dependency>

Usage
-----

### Example:

    // Create a SSL configuration:
    SslConfiguration sslConfig = new SslConfiguration();
    sslConfig.setKeyStorePath("path/to/keystore");
    sslConfig.setKeyStorePassword("changeit");
    sslConfig.setKeyManagerPassword("changeit");
    sslConfig.setTrustStorePath("path/to/keystore");
    sslConfig.setTrustStorePassword("changeit");
    ...

    // And get an SSLContext
	SslContextFactory sslContextFactory = new SslContextFactory(sslConfig);
	SSLContext sslContext = sslContextFactory.getSslContext();


### Require client auth

    sslConfig.setNeedClientAuth(true);


### Validate certificates

    sslConfig.setValidateCerts(true);
    sslConfig.setValidatePeerCerts(true);


## Generating key pairs and certificates

Generating Keys and Certificates with the JDK's keytool

    keytool -keystore keystore -alias smpp -genkey -keyalg RSA

Generating Keys and Certificates with OpenSSL

    openssl genrsa -des3 -out smpp.key
    openssl req -new -x509 -key smpp.key -out smpp.crt

## Requesting a trusted certificate

Generating a CSR from keytool

    keytool -certreq -alias smpp -keystore keystore -file smpp.csr

Generating a CSR from OpenSSL

    openssl req -new -key smpp.key -out smpp.csr

## Loading keys and certificates

Loading Certificates with keytool

The following command loads a PEM encoded certificate in the smpp.crt file into a JSSE keystore:

    keytool -keystore keystore -import -alias smpp -file smpp.crt -trustcacerts

Loading Keys and Certificates via PKCS12

If you have a key and certificate in separate files, you need to combine them into a PKCS12 format file to load into a new keystore. The certificate can be one you generated yourself or one returned from a CA in response to your CSR. 

The following OpenSSL command combines the keys in smpp.key and the certificate in the smpp.crt file into the smpp.pkcs12 file.

    openssl pkcs12 -inkey smpp.key -in smpp.crt -export -out smpp.pkcs12
    keytool -importkeystore -srckeystore smpp.pkcs12 -srcstoretype PKCS12 -destkeystore keystore


## Appendix

### Interop with stunnel

This library has been tested with stunnel4 wrapping both client and servers. There is a sample stunnel.conf in src/test/resources. The SSL implementation should be compatible with other TLS/SSL encryption wrappers, assuming the JDK you are using supports the same cryptographic algorithms as the encryption wrapper.

### Known issues
