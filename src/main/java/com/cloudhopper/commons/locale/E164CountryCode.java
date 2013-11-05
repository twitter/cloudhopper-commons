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

/*
 * #%L
 * ch-commons-locale
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

/**
 * This class represents the E.164 country code (dialing code) for dialing a phone
 * number.  For example "1", is the dialing code for North America -- you'll
 * need to resort to analyzing the area code to figure out if that means the USA,
 * Canada, or Caribbean.
 *
 * @author joelauer
 */
public class E164CountryCode {

    private String prefix;
    private String iso;

    /**
     * Constructs a new CountryCode from a dialing code prefix to a country ISO code.
     * The iso is length checked (length = 2).  There is no other validation on
     * the code or name.
     */
    E164CountryCode(String prefix, String iso) throws IllegalArgumentException {
        this.prefix = prefix;
        if (iso.length() != 2) {
            throw new IllegalArgumentException("Country ISO code must be 2 chars in length");
        } else {
            this.iso = iso;
        }
    }

    /**
     * Returns the prefix for the country such as 1 for the US or 49 for Germany.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the 2 digit ISO code for the country.
     */
    public String getIso() {
        return iso;
    }

    // Parses Format: 1804 US
    protected static E164CountryCode parse(String line) throws IOException {
        try {
            int pos = line.indexOf(' ');
            if (pos < 0) throw new IOException("Invalid format, could not parse prefix");
            String prefix = line.substring(0, pos);

            // rest of line is iso
            String iso = line.substring(pos+1).trim();

            // was there a name?
            if (iso == null || iso.equals("")) {
                throw new IOException("Country ISO was null or empty");
            }

            // create the new country code
            return new E164CountryCode(prefix, iso);
        } catch (Exception e) {
            throw new IOException("Failed while parsing country code for line: " + line, e);
        }
    }

}
