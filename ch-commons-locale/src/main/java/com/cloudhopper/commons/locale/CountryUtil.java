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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements utilities for working with classes.
 *
 * @author Joe Lauer
 */
public class CountryUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(CountryUtil.class);

    private static HashMap<String,Country> byIso2;
    private static ArrayList<Country> byName;

    static {
        // load the resource file
        InputStream is = CountryUtil.class.getResourceAsStream("iso3166.txt");
        if (is == null) {
            throw new RuntimeException("Not able to locate iso3166.txt file");
        }

        try {
            byName = parse(is);
        } catch (Exception e) {
            throw new RuntimeException("Error while loading or parsing iso3166.txt resource", e);
        } finally {
            try { is.close(); } catch (Exception e) {}
        }

        // generate our list by iso2
        byIso2 = new HashMap<String,Country>();
        for (Country country : byName) {
            // add a reference to the same country, check to make sure no overlap
            Country prevCountry = byIso2.put(country.getCode(), country);
            if (prevCountry != null) {
                throw new RuntimeException("Overlap of country ISO2 value of " + country.getCode());
            }
        }

        // copy into countries by name
        //countriesByName = new ArrayList<Country>();
        //Collections.copy(countriesByCode, countriesByName);

        /**
        Locale.getISOCountries();

        countries = new ArrayList<Country>();
        for (Locale locale : Locale.getAvailableLocales()) {
            final String country = locale.
            if (country.length() > 0) {
                countries.add(new Country(locale.getISO3Country(), country));
            }
        }
        // sort by display country
        Collections.sort(countries, new Comparator() {
            public int compare(Object a, Object b) {
                return ((Country) a).getDescription().compareToIgnoreCase(((Country) b).getDescription());
            }
        });
         */
    }

    private CountryUtil() {
        // only static
    }

    public static Country lookupByIso2(String iso2) {
        return byIso2.get(iso2);
    }

    public static ArrayList<Country> parse(InputStream is) throws IOException {
        // convert into a buffered reader
        BufferedReader in =  new BufferedReader(new InputStreamReader(is));
        String line = null; //not declared within while loop
        ArrayList<Country> c = new ArrayList<Country>();
        /*
         * readLine is a bit quirky :
         * it returns the content of a line MINUS the newline.
         * it returns null only for the END of the stream.
         * it returns an empty String if two newlines appear in a row.
         */
        while ((line = in.readLine()) != null) {
            if (!line.equals("") && !line.startsWith("#")) {
                // this is a country we need to parse
                // AF AFG 004 Afghanistan
                c.add(Country.parse(line));
            }
        }
        return c;
    }


    /**
     * Returns a list of countries sorted by name (i.e. France before United States)
     * @return
     */
    public static List<Country> getCountriesByName() {
        return byName;
    }
    
}
