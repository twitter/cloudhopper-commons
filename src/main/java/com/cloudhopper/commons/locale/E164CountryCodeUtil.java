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
package com.cloudhopper.commons.locale;

// java imports
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

// third party imports
import org.apache.log4j.Logger;

// my imports

/**
 * Utility class for statically loading E.164 and performing lookups.
 * @author Joe Lauer
 */
public class E164CountryCodeUtil {
    private static Logger logger = Logger.getLogger(E164CountryCodeUtil.class);

    // all country codes hashed by prefix
    private static HashMap<String,E164CountryCode> byPrefix;
    // dynamic max prefix length in our list of prefixes -- specifically for
    // speeding up lookups within the hash
    private static int maxPrefixLength;

    static {
        // load the resource file
        InputStream is = E164CountryCodeUtil.class.getResourceAsStream("e164CountryCode.txt");
        if (is == null) {
            throw new RuntimeException("Not able to locate e164CountryCode.txt file");
        }

        try {
            byPrefix = parse(is);
        } catch (Exception e) {
            throw new RuntimeException("Error while loading or parsing e164CountryCode.txt resource", e);
        } finally {
            try { is.close(); } catch (Exception e) {}
        }

        // determine the max prefix length
        maxPrefixLength = 1;
        for (E164CountryCode code : byPrefix.values()) {
            if (code.getPrefix().length() > maxPrefixLength) {
                // update the max prefix length
                maxPrefixLength = code.getPrefix().length();
            }
        }
    }

    private E164CountryCodeUtil() {
        // only static
    }

    /**
     * Looks up an E164CountryCode object by analyzing the address and returning
     * the best match.  For example, 12125551212 will return an entry for the US.
     * @param address The address to lookup.
     * @return
     */
    public static E164CountryCode lookup(String address) {
        // analyze just the first few chars -- max of 4 or length of address
        int len = (address.length() > maxPrefixLength ? maxPrefixLength : address.length());
        // search backwards first, return the first result
        for (int i = len; i > 0; i--) {
            String prefix = address.substring(0, i);
            E164CountryCode entry = byPrefix.get(prefix);
            if (entry != null) {
                return entry;
            }
        }
        // nothing found
        return null;
    }

    public static HashMap<String,E164CountryCode> parse(InputStream is) throws IOException {
        // convert into a buffered reader
        BufferedReader in =  new BufferedReader(new InputStreamReader(is));
        String line = null; //not declared within while loop
        HashMap<String,E164CountryCode> codes = new HashMap<String,E164CountryCode>();
        /*
         * readLine is a bit quirky :
         * it returns the content of a line MINUS the newline.
         * it returns null only for the END of the stream.
         * it returns an empty String if two newlines appear in a row.
         */
        while ((line = in.readLine()) != null) {
            if (!line.equals("") && !line.startsWith("#")) {
                E164CountryCode code = E164CountryCode.parse(line);
                codes.put(code.getPrefix(), code);
            }
        }
        return codes;
    }
    
}
