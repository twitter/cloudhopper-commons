package net.cloudhopper.commons.locale;

import java.io.IOException;

/**
 * The class needs to be able to receive and use a list or countries that
 * are in accordance with the ISO 3166 code lists.  These may be
 * stored on a database, textfile or other storage mechanism.  This class is
 * therefore designed in a way that does not hard-code country variables.
 * There should therefore be no need to sub-class this class for different
 * countries.  The class is instead generic for all countries and can handle
 * and provide feedback to clients on field content.
 */
public final class Country implements Cloneable, Comparable {

    /** Constructs a new ISOCountry from an ISO code and name.  The code is
     *  length checked (length =2).  There is no other validation on the code
     *  or name. */
    Country(String code, String name) throws IllegalArgumentException {
        name_ = name;
        if (code.length() != 2) {
            throw new IllegalArgumentException(CODE_LENGTH);
        } else {
            code_ = code;
        }
    }

    /** Return a copy of this object. */
    public Object clone() {
        Country country = null;
        try {
            country = (Country) super.clone();
            if (country.code_ != null) {
                country.code_ = getCode(); //String is immutable no need to clone
                country.name_ = getName(); //String is immutable no need to clone
            }
        } catch (CloneNotSupportedException e) {
        } // Won't happen
        return country;
    }

    /** Returns the country code string.  If this is a valid ISO code it should
     *  be comparable to another country code. */
    public String getCode() {
        return code_;
    }

    /** Returns the country name string.  This string is not validated in any
     *  way and should not be used for comparative purposes. Only country codes
     *  are used to compare ISOCountry equality.*/
    public String getName() {
        return name_;
    }

    /** Compares two ISOCountry objects for ordering.
     *  Returns the value <code>zero</code> if the two countries have equal
     *  country codes.  Returns a value less than <code>zero</code> if this
     *  country code is before the country argument and a value greater than
     *  <code>zero</code> if this country is after the country argument. Will
     *  result in alphabetical sorting. */
    public int compareTo(Country country) {
        String thisCode = this.getCode();
        String otherCode = country.getCode();
        return thisCode.compareTo(otherCode);
    }

    /** Compares this country to another Object.  If the Object isan ISOCountry,
     * this function behaves like <code>compareTo(Date)</code>.  Otherwise,
     * it throws a <code>ClassCastException</code>. */
    public int compareTo(Object o) {
        return compareTo((Country) o);
    }
    
    /**
     * Tests if a country is equal to another one by 2 letter Country Code.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Country))
            return false;
        return (compareTo((Country)o) == 0);
    }

    @Override
    public int hashCode() {
        return this.code_.hashCode();
    }

    @Override
    public String toString() {
        return "Country [code=" + this.code_ + ",name=" + this.name_ + "]";
    }

    /** A string that stipulates the code length.  Used for error messages. */
    private static final String CODE_LENGTH = "An ISO country code is two characters in length.";
    /** An ISO country code.  Its length is validated in the constructor. */
    private String code_;
    /** An ISO country name.  Not validated. */
    private String name_;


    // AF AFG 004 Afghanistan
    protected static Country parse(String line) throws IOException {
        try {
            int pos = line.indexOf(' ');
            if (pos < 0) throw new IOException("Invalid format, could not parse 2 char code");
            String code2 = line.substring(0, pos);
            int pos2 = line.indexOf(' ', pos+1);
            if (pos2 < 0) throw new IOException("Invalid format, could not parse 3 char code");
            String code3 = line.substring(pos+1, pos2);
            int pos3 = line.indexOf(' ', pos2+1);
            if (pos3 < 0) throw new IOException("Invalid format, could not parse 3 char digit code");
            String num3 = line.substring(pos2+1, pos3);
            // rest of line is name
            String name = line.substring(pos3+1).trim();
            // was there a name?
            if (name == null || name.equals("")) {
                throw new IOException("Country name was null or empty");
            }
            // create the new country
            return new Country(code2, name);
        } catch (Exception e) {
            throw new IOException("Failed while parsing country for line: " + line, e);
        }
    }
}
