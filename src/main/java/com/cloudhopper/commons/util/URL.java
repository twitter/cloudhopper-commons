package com.cloudhopper.commons.util;

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

import com.cloudhopper.commons.util.codec.URLCodec;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Represents a URL and its various properties.  Not all properties are currently
 * supported by this class
 * 
 * A URL is of the form:
 * 
 *   protocol://[ username [: password ]@] host [: port ][/path][?query_string][#anchor]
 * 
 * The best method for creating a URL is to use the URLParser class.  While this utility class doesn't provide
 * any more features yet than the internal Java URL class, the plan is to add
 * those in the future.
 *
 * Also, this class and its associated parser internally use the modern
 * StringBuilder class vs. the StringBuffer class and avoid any synchronization
 * overhead.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class URL {

    private String protocol;
    private String username;
    private String password;
    private String host;
    private Integer port;
    private String path;
    private String query;
    private Properties queryProperties;
    //private String anchor;

    public URL() {
        // do nothing
    }

    /**
     * Builds and returns a properly encoded URL in its full string form such as
     * "http://www.google.com/".  The URL will have all its values properly
     * escaped such as a space " " being converted into "+" while the AT char
     * "@" will be replaced by its % (percent) encoded equivalent.
     * 
     * @return The URL in its full string form
     */
    @Override
    public String toString() {
        StringBuilder url = new StringBuilder();
        
        if (!StringUtil.isEmpty(this.protocol)) {
            url.append(this.protocol);
            url.append("://");
        }
        
        if (!StringUtil.isEmpty(this.username)) {
            url.append(encode(this.username));
            if (this.password != null) {
                url.append(':');
                url.append(encode(this.password));
            }
            url.append('@');
        }

        if (!StringUtil.isEmpty(this.host)) {
            url.append(this.host);
        }

        if (this.port != null) {
            url.append(':');
            url.append(this.port);
        }

        if (!StringUtil.isEmpty(this.path)) {
            url.append(this.path);
        }

        if (!StringUtil.isEmpty(this.query)) {
            url.append('?');
            url.append(this.query);
        }
        
        return url.toString();
    }

    /**
     * Sets the protocol such as "http" if the URL is "http://www.google.com"
     * @param protocol The protocol or null if not set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Gets the protocol such as "http" if the URL is "http://www.google.com"
     * @return The protocol or null if not set
     */
    public String getProtocol() {
        return this.protocol;
    }

    /**
     * Sets the username such as "root" if the URL is "http://root@www.google.com/"
     * or "ro@t" if the URL is "http://ro%40t@www.google.com/".
     * The value should be in its decoded form (not URL encoded).
     * @param username The username in URL-decoded form or null if not set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the username such as "root" if the URL is "http://root@www.google.com/"
     * or "ro@t" if the URL is "http://ro%40t@www.google.com/".
     * The value will be decoded from its "URL encoding" form if it contained
     * characters that require escaping.
     * @return The username in URL-decoded form or null if not set
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the password such as "test" if the URL is "http://root:test@www.google.com/"
     * or "t@st" if the URL is "http://root:t%40st@www.google.com/".
     * The value should be in its decoded form (not URL encoded).
     * @param username The password in URL-decoded form or null if not set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the password such as "test" if the URL is "http://root:test@www.google.com/"
     * or "t@st" if the URL is "http://root:t%40st@www.google.com/".
     * The value will be decoded from its "URL encoding" form if it contained
     * characters that require escaping.
     * @return The password in URL-decoded form  or null if not set
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the host such as "www.google.com" if the URL is "http://www.google.com"
     * @return  The host or null if not set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets the host such as "www.google.com" if the URL is "http://www.google.com"
     * @return  The host or null if not set
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Sets the port such as 80 if the URL should specifically include a port such as
     * "http://www.google.com:80/".  If the URL should not include a port, set
     * this value to null.
     * @return  The port or null if not set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Gets the port such as 80 if the URL specifically included a port such as
     * "http://www.google.com:80/".  If the URL did not include a port, this
     * will return null such as the URL "http://www.google.com/"
     * @return  The port or null if not set
     */
    public Integer getPort() {
        return this.port;
    }

    /**
     * Sets the path such as "/" if the URL is "http://www.google.com/" or
     * "/My Documents/index.html" if the URL is "http://www.google.com/My+Documents/index.html".
     * The value should be in its decoded form (not URL encoded).
     * @return The path in URL-decoded form or null if not set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets the path such as "/" if the URL is "http://www.google.com/" or
     * "/My Documents/index.html" if the URL is "http://www.google.com/My+Documents/index.html".
     * The value will be decoded from its "URL encoding" form if it contained
     * characters that require escaping. If a path was not included after the
     * host[:port] portion of the URL, then this value will be null.
     * @return The path in URL-decoded form or null if not set
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Gets the file name of this <code>URL</code>.
     * The returned file portion will be
     * the same as <CODE>getPath()</CODE>, plus the concatenation of
     * the value of <CODE>getQuery()</CODE>, if any. If there is
     * no query portion, this method and <CODE>getPath()</CODE> will
     * return identical results.
     *
     * @return  the file name of this <code>URL</code>,
     * or an empty string if one does not exist
     */
    /**
    public String getFile() {
        return this.file;
    }
     */

    /**
     * Gets the query such as "id=1&pk=3" if the URL was "http://www.google.com/index.html?id=1&pk=3".
     * The query is the entire part after a ?.  If no query was set, this will
     * be null.
     * @return  The query or null if not set
     */
    public String getQuery() {
        return this.query;
    }

    public void setQuery(String value) throws MalformedURLException {
        if (this.queryProperties == null) {
            this.queryProperties = new Properties();
        }
        parseQueryProperties(value, this.queryProperties);
        this.query = value;
    }

    public String getQueryProperty(String name) {
        if (this.queryProperties == null) {
            return null;
        }
        return this.queryProperties.getProperty(name);
    }

    public Properties getQueryProperties() {
        return this.queryProperties;
    }

    /**
    public void setQueryProperty(String name, String value) {
        if (this.queryProperties == null) {
            this.queryProperties = new Properties();
        }
        this.queryProperties.put(name, value);
    }
     */

    /**
     * Extracts the key value pairs from the query string
     *
     * @param toAddTo Properties key-value pairs will be added to this properties set
     */
    /**
    public void getQueryProperties(Properties toAddTo) {
        String q = getQuery();
        getQueryProperties(q, toAddTo);
    }
     */

    /**
     * Tool function: queries a query string and adds the key/value pairs to the specified
     * properties map
     *
     * @param q String
     * @param toAddTo Properties
     */
    public static void parseQueryProperties(String q, Properties props) throws MalformedURLException {
        if (q == null || q.length() == 0) {
            return;
        }
        for (StringTokenizer iter = new StringTokenizer(q, "&");
            iter.hasMoreElements();) {
            String pair = iter.nextToken();
            int split = pair.indexOf('=');
            if (split <= 0) {
                throw new RuntimeException("Invalid pair [" + pair  + "] in query string [" + q + "]");
            } else {
                String tempKey = pair.substring(0, split);
                String tempValue = pair.substring(split + 1);
                StringBuilder key = new StringBuilder(tempKey.length());
                StringBuilder value = new StringBuilder(tempValue.length());
                try {
                    URLCodec.decode(tempKey, key);
                    URLCodec.decode(tempValue, value);
                } catch (IOException e) {
                    throw new MalformedURLException("Invalid encoding in [" + pair + "] in query string [" + q + "]");
                }
                props.setProperty(key.toString(), value.toString());
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof URL) {
            URL otherURL = (URL)object;
            String otherURLString = otherURL.toString();
            return this.toString().equals(otherURLString);
        } else if (object instanceof String) {
            String otherURLString = (String)object;
            return this.toString().equals(otherURLString);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        String urlString = toString();
        int hash = 7;
        hash = 79 * hash + (urlString != null ? urlString.hashCode() : 0);
        return hash;
    }

    static private String encode(String str0) throws IllegalArgumentException {
        StringBuilder buf = new StringBuilder(str0.length());
        try {
            URLCodec.encode(str0, buf);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid param: URL encoding '" + str0 + "'");
        }
        return buf.toString();
    }
}

