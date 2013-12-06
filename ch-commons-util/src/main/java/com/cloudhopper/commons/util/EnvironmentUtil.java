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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Common environment methods.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class EnvironmentUtil {

    public static final String HOST_NAME = "HOST_NAME";
    public static final String HOST_DOMAIN = "HOST_DOMAIN";
    public static final String HOST_FQDN = "HOST_FQDN";

    /**
     * Creates a Properties object containing common system properties that are
     * determined using various Java routines.  For example, determining the
     * hostname that the application is running on.
     *
     * HOST_NAME
     * HOST_DOMAIN
     *
     * @return
     */
    public static Properties createCommonSystemProperties() throws EnvironmentException {
        // create a new system properties object
        Properties systemProperties = new Properties();

        // host name, domain, fqdn
        String hostFQDN = getHostFQDN();

        if (!StringUtil.isEmpty(hostFQDN)) {
            // save the fqdn
            systemProperties.put(HOST_FQDN, hostFQDN);

            // split up the fqdn to find the host name and domain
            String[] hostInfo = splitHostFQDN(hostFQDN);

            // save the host name and domain, defaulting to an empty string if null
            systemProperties.put(HOST_NAME, (hostInfo[0] != null ? hostInfo[0] : ""));
            systemProperties.put(HOST_DOMAIN, (hostInfo[1] != null ? hostInfo[1] : ""));

            // add a new host name
            //systemProperties.put(HOST_NAME, hostname);
        } else {
            systemProperties.put(HOST_FQDN, "");
            systemProperties.put(HOST_NAME, "");
            systemProperties.put(HOST_DOMAIN, "");
        }

        return systemProperties;
    }


    public static String getHostFQDN() throws EnvironmentException {
        try {
            InetAddress addr = InetAddress.getLocalHost();

            // get IP address
            byte[] ipAddr = addr.getAddress();

            // get hostname
            return addr.getHostName();
        } catch (UnknownHostException e) {
            throw new EnvironmentException("Unable to determine hostname", e);
        }
    }


    public static String[] splitHostFQDN(String fqdn) {
        String[] result = new String[2];

        if (StringUtil.isEmpty(fqdn)) {
            return result;
        }

        // try to determine the domain name
        int pos = fqdn.indexOf('.');
        if (pos >= 0) {
            result[0] = fqdn.substring(0, pos);
            if (pos+1 < fqdn.length()) {
                result[1] = fqdn.substring(pos+1);
            }
        } else {
            // entire string is the host name
            result[0] = fqdn;
        }

        return result;
    }

}
