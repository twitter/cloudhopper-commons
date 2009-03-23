
package com.cloudhopper.commons.xbean;

/**
 * Interface to convert String values to Java objects.
 *
 * @author joelauer
 */
public interface PropertyConverter {

    public Object convert(String value) throws ConversionException;

}
