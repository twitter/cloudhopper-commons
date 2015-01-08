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

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration of an HttpServer.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class HttpServerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerConfiguration.class);

    // name of server such as "mainHttpServer" or "managementHttpServer"
    protected String name;
    // should sessions be enabled?
    protected Boolean sessionsEnabled;
    // resource base dir (for files, html, images, etc)
    protected String resourceBaseDirectory;
    // list of connectors this server will bind to
    protected ArrayList<HttpConnectorConfiguration> connectors;
    // list of SSL connectors this server will bind to
    protected ArrayList<HttpSslConnectorConfiguration> sslConnectors;
    // min number of threads to service requests
    protected Integer minThreads;
    // max number of threads to service requests
    protected Integer maxThreads;
    // queue size for handling connections that cannot be immediately handled
    protected int maxQueueSize;
    // time to keep an idle thread around
    protected long threadKeepAliveTimeout;
    // time for graceful shutdown (to finish serving existing connections while not allowing new
    protected Integer gracefulShutdownTime;
    // disable jetty's internal shutdown hook?  only do this if we'll explicitly call stop
    protected boolean jettyAutoShutdownDisabled;
    // jmx domain to use for mbean
    protected String jmxDomain;
    
    public HttpServerConfiguration() {
        this.sessionsEnabled = true;
        this.connectors = new ArrayList<HttpConnectorConfiguration>();
        this.sslConnectors = new ArrayList<HttpSslConnectorConfiguration>();
        this.minThreads = 5;
        this.maxThreads = 50;
        this.threadKeepAliveTimeout = 60000;
        this.jettyAutoShutdownDisabled = false;
	this.jmxDomain = "com.cloudhopper.jetty." + safeGetName();
    }

    public String getResourceBaseDirectory() {
        return resourceBaseDirectory;
    }

    public void setResourceBaseDirectory(String resourceBaseDirectory) {
        this.resourceBaseDirectory = resourceBaseDirectory;
    }

    public void addConnector(HttpConnectorConfiguration value) {
        this.connectors.add(value);
    }

    public void addSslConnector(HttpSslConnectorConfiguration value) {
        this.sslConnectors.add(value);
    }

    /**
     * Checks if this server has at least one connector (either cleartext or
     * SSL) configured.
     * @return True if has at least one, otherwise false.
     */
    public boolean hasAtLeastOneConnector() {
        return (this.connectors.size() > 0 || this.sslConnectors.size() > 0);
    }
    
    public String getPortString() {
        StringBuilder ports = new StringBuilder();
        if (this.connectors.size() > 0) {
            for (HttpConnectorConfiguration config : this.connectors) {
                if (ports.length() > 0) {
                    ports.append(", ");
                }
                ports.append(config.getPort());
            }
        }
        if (this.sslConnectors.size() > 0) {
            for (HttpConnectorConfiguration config : this.sslConnectors) {
                if (ports.length() > 0) {
                    ports.append(", ");
                }
                ports.append(config.getPort());
                ports.append("(SSL)");
            }
        }
        return ports.toString();
    }

    public ArrayList<HttpConnectorConfiguration> getConnectors() {
        return this.connectors;
    }

    public ArrayList<HttpSslConnectorConfiguration> getSslConnectors() {
        return this.sslConnectors;
    }

    public void setSessionsEnabled(Boolean value) {
        this.sessionsEnabled = value;
    }

    public Boolean isSessionsEnabled() {
        return this.sessionsEnabled;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getName() {
        return this.name;
    }
    
    public String safeGetName() {
        return (this.name == null ? "default" : this.name);
    }

    public Integer getMinThreads() {
        return this.minThreads;
    }

    public void setMinThreads(Integer value) {
        this.minThreads = value;
    }

    public Integer getMaxThreads() {
        return this.maxThreads;
    }

    public void setMaxThreads(Integer value) {
        this.maxThreads = value;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public long getThreadKeepAliveTimeout() {
        return threadKeepAliveTimeout;
    }

    public void setThreadKeepAliveTimeout(long threadKeepAliveTimeout) {
        this.threadKeepAliveTimeout = threadKeepAliveTimeout;
    }

    public Integer getGracefulShutdownTime() {
        return gracefulShutdownTime;
    }

    public void setGracefulShutdownTime(Integer time) {
        this.gracefulShutdownTime = time;
    }
    
    public boolean isJettyAutoShutdownDisabled() {
        return this.jettyAutoShutdownDisabled;
    }

    public void setJettyAutoShutdownDisabled(boolean disabled) {
        this.jettyAutoShutdownDisabled = disabled;
    }

    public void setJmxDomain(String value) {
        this.jmxDomain = value;
    }

    public String getJmxDomain() {
        return this.jmxDomain;
    }
    
}
