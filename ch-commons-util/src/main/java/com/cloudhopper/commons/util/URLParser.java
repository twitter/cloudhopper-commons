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

import java.net.MalformedURLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cloudhopper.commons.util.codec.URLCodec;
import java.io.IOException;

/**
 * A URL parser for the following pattern:
 * 
 * protocol://[username[:password]@][host[:port]][/path]
 *
 * Examples:
 *   p:///
 *   p://h
 *   p://h:p
 *   p://u@h:p
 *   p://u:p@h:p
 *   p://h/path/to/something
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class URLParser {
    private static final Logger logger = LoggerFactory.getLogger(URLParser.class);

    private URLParser() {
        // static methods only
    }

    static private String decode(String str0) throws MalformedURLException {
        StringBuilder buf = new StringBuilder(str0.length());
        try {
            URLCodec.decode(str0, buf);
        } catch (IOException e) {
            throw new MalformedURLException("Invalid URL: failed while URL decoding '" + str0 + "'");
        }
        return buf.toString();
    }

    static public URL parse(String url) throws MalformedURLException {

        int pos = 0;

//        logger.debug("parsing URL: " + url);

        //
        // parse protocol
        //
        int i = url.indexOf("://");
        if (i < 0) {
            throw new MalformedURLException("Invalid URL [" + url + "]: no protocol specified");
        }

        // the url we'll be returning
        URL r = new URL();
        
        String protocol = url.substring(0, i);
        r.setProtocol(protocol);
//        logger.debug("parsed protocol: " + protocol);

        // skip :// part
        pos = i + 3;

        // username[:password]
        i = url.indexOf('@', pos);
        if (i >= 0) {
            // found url to contain a username and possibly a password
//            logger.debug("found @ char to indicate username:password");
            String userPass = url.substring(pos, i);
            int atPos = userPass.indexOf(':');
            if (atPos >= 0) {
                // password exists in this string
                String username = userPass.substring(0, atPos);
                String password = userPass.substring(atPos+1);
                if (username != null && username.length() > 0) {
                    r.setUsername(decode(username));
                }
                if (password != null && password.length() > 0) {
                    r.setPassword(decode(password));
                }
            } else {
//                logger.debug("userPass part only includes a username");
                if (userPass.length() > 0) {
                    r.setUsername(decode(userPass));
                }
            }
            // update the position for the next parsing section
            pos = i + 1;
        }

        //
        // host[:port]
        //
        i = url.indexOf('/', pos);
        if (i < 0) {
            // maybe to the query string then
            i = url.indexOf('?', pos);
            if (i < 0) {
                // host:port is to the complete end of this string
                i = url.length();
            }
        }

        // extract entire host and/or port
        String hostPort = url.substring(pos, i);

        // did a host actually exist?
        if (hostPort != null && hostPort.length() > 0) {
            // does this hostPort contain a port?
            int colPos = hostPort.indexOf(':');
            if (colPos >= 0) {
                String host = hostPort.substring(0, colPos);
                r.setHost(host);
//                logger.debug("parsed host: " + host);

                String tempPort = hostPort.substring(colPos+1);
                try {
                    Integer port = Integer.valueOf(tempPort);
                    r.setPort(port);
//                    logger.debug("parsed port: " + port);
                } catch (NumberFormatException e) {
                    throw new MalformedURLException("Invalid URL [" + url + "]: port '" + tempPort + "' was not an integer");
                }
            } else {
                // entire string is the host
                r.setHost(hostPort);
//                logger.debug("parsed host: " + hostPort);
            }
        } else {
//            logger.debug("no host parsed");
        }

        // next position we'll start parsing from actually starts next
        pos = i;

        // we may be done
        if (pos >= url.length()) {
//            logger.debug("early parsing exist after host:port section");
            return r;
        }

        // if we get here, then we know there is more data in the url to parse
        // the next character will either be / or ?
        if (url.charAt(pos) == '/') {
            // we either will read to end of string or till ?
            i = url.indexOf('?');
            if (i < 0) {
                // read till end of string
                i = url.length();
            }
            String path = url.substring(pos, i);
            r.setPath(decode(path));
//            logger.debug("parsed path: " + path);
        }

        pos = i;

        // we may be done
        if (pos >= url.length()) {
//            logger.debug("early parsing exist after path section");
            return r;
        }

        // we may have parsed the path above, now parse the query string
        if (url.charAt(pos) == '?') {
            String query = url.substring(pos+1);
            if (query != null && query.length() > 0) {
                r.setQuery(query);
            }
        }

        return r;
    }

}

