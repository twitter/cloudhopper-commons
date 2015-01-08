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
 * Configuration of a Connector for an HttpServer.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class HttpConnectorConfiguration {

    // port to bind server to
    private Integer port;
    // optional hostname/ip address to bind to (null for any)
    private String host;
    // max idle time for the connector (defaults to 30000)
    private int maxIdleTime;
    // should we reuse an address (defaults to true)
    private boolean reuseAddress;
    // should we keep track of stats (defaults to true)
    private boolean statsEnabled;
    // should we use NIO (defaults to true)
    private boolean nonBlockingSocketsEnabled;

    public HttpConnectorConfiguration() {
        this.port = null;
        this.host = null;
        this.maxIdleTime = 30000;
        this.reuseAddress = true;
        this.statsEnabled = true;
	this.nonBlockingSocketsEnabled = true;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer value) {
        this.port = value;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String value) {
        this.host = value;
    }

    public int getMaxIdleTime() {
        return this.maxIdleTime;
    }

    public void setMaxIdleTime(int value) {
        this.maxIdleTime = value;
    }

    public boolean isReuseAddress() {
        return reuseAddress;
    }

    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    public boolean isStatsEnabled() {
        return statsEnabled;
    }

    public void setStatsEnabled(boolean statsEnabled) {
        this.statsEnabled = statsEnabled;
    }

    public boolean isNonBlockingSocketsEnabled() {
	return nonBlockingSocketsEnabled;
    }

    public void setNonBlockingSocketsEnabled(boolean nonBlockingSocketsEnabled) {
	this.nonBlockingSocketsEnabled = nonBlockingSocketsEnabled;
    }
    
}
