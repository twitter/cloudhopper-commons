package com.cloudhopper.jetty;

/*
 * #%L
 * ch-jetty
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

/**
 * Configuration of an SSL Connector for an HttpServer.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class HttpSslConnectorConfiguration extends HttpConnectorConfiguration {

    private String keystoreFile;            // relative to home directory
    private String keystorePassword;
    private String keyManagerPassword;      // in case of it being different
    private String truststoreFile;          // relative to home directory
    private String truststorePassword;
    private String certAlias;

    public HttpSslConnectorConfiguration() {
        super();
    }

    public String getKeystoreFile() {
        return this.keystoreFile;
    }

    public void setKeystoreFile(String value) {
        this.keystoreFile = value;
    }

    public String getKeystorePassword() {
        return this.keystorePassword;
    }

    public void setKeystorePassword(String value) {
        this.keystorePassword = value;
    }

    public String getKeyManagerPassword() {
        return keyManagerPassword;
    }

    public void setKeyManagerPassword(String keyManagerPassword) {
        this.keyManagerPassword = keyManagerPassword;
    }

    public String getTruststoreFile() {
        return this.truststoreFile;
    }

    public void setTruststoreFile(String value) {
        this.truststoreFile = value;
    }

    public String getTruststorePassword() {
        return this.truststorePassword;
    }

    public void setTruststorePassword(String value) {
        this.truststorePassword = value;
    }

    public String getCertAlias() {
        return certAlias;
    }

    public void setCertAlias(String certAlias) {
        this.certAlias = certAlias;
    }

}
