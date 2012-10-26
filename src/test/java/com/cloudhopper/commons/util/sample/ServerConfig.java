package com.cloudhopper.commons.util.sample;

/*
 * #%L
 * ch-commons-util
 * %%
 * Copyright (C) 2012 Cloudhopper by Twitter
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
 * Sample configuration object for a server.
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class ServerConfig {
    
    private String host;
    private int port;

    public ServerConfig() {
        // do nothing
    }

    public int getPort() {
        return port;
    }

    public void setPort(int value) throws ServerConfigException {
        // only non-negative
        if (value <= 0) {
            throw new ServerConfigException("Port must be > 0");
        }
        this.port = value;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String value) {
        this.host = value;
    }
}
