package com.cloudhopper.commons.locale;

// java imports
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// third party imports
import org.apache.log4j.Logger;

// my imports


/**
 * This class implements utilities for working with classes.
 *
 * @author Joe Lauer
 */
public class CountryUtil {
    
    private static Logger logger = Logger.getLogger(CountryUtil.class);

    //private static ArrayList<Country> countriesByCode;
    private static ArrayList<Country> countriesByName;

    static {
        // load the resource file
        InputStream is = CountryUtil.class.getResourceAsStream("iso3166.txt");
        if (is == null) {
            throw new RuntimeException("Not able to locate iso3166.txt file");
        }
        try {
            countriesByName = parse(is);
        } catch (Exception e) {
            throw new RuntimeException("Error while loading or parsing iso3166.txt resource", e);
        } finally {
            try { is.close(); } catch (Exception e) {}
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
        return countriesByName;
    }
    
}
