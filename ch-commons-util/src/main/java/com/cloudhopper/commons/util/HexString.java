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

import static com.cloudhopper.commons.util.HexUtil.toByteArray;
import static com.cloudhopper.commons.util.HexUtil.toHexString;

import java.util.Arrays;

/**
 * Utility class for representing hex strings. Instances are immutable. Hex digits are always in caps. 
 * 
 * Primarily used to aid testing, the implementation is naive and not performant.
 * <b>NOT RECOMMENDED FOR USE IN PRODUCTION</b>.
 * 
 * @author John Woolf (twitter: @jwoolf330 or <a href="http://twitter.com/jwoolf330" target=window>http://twitter.com/jwoolf330</a>)
 *
 */
public final class HexString {
	
	public static final HexString NULL = new HexString(new byte[0], "");
	
	/**
	 * Create a new <code>HexString</code> instance from the given hexadecimal string.
	 * If <code>null</code> or an empty string is passed, will return a <code>HexString</code> instance
	 * with empty hex value and zero-length byte array.
	 * 
	 * @param hex The hexadecimal string
	 * @return Immutable <code>HexString</code> instance encapsulating the hexadecimal string
	 * @throws IllegalArgumentException if malformed hex string (invalid or odd number of characters) is passed
	 */
	public static HexString valueOf(String hex) {
		return (hex == null || hex.equals("")) ? NULL : createHexString(hex);
	}
	
	/**
	 * Create a new <code>HexString</code> instance from the given byte array.
	 * If <code>null</code> or zero-length byte array is passed, will return a <code>HexString</code> instance
	 * with empty hex value and zero-length byte array.
	 * 
	 * @param bytes The byte array
	 * @return Immutable <code>HexString</code> instance encapsulating the byte array
	 */
	public static HexString valueOf(byte[] bytes) {
		return (bytes == null || bytes.length == 0) ? NULL : createHexString(bytes);
	}
	
	private static HexString createHexString(String hex) { // though method is private, leaving checks in place to show intent
		if (hex == null) {
			throw new IllegalArgumentException("hex string argument cannot be null; use HexString.NULL instead");
		} else if (hex.equals("")) {
			throw new IllegalArgumentException("hex string argument cannot be empty; use HexString.NULL instead");    			
		}
		byte[] bytes = toByteArray(hex);
		return new HexString(bytes, hex.toUpperCase());
	}
	
	private static HexString createHexString(byte[] bytes) { // though method is private, leaving checks in place to show intent
		if (bytes == null) {
			throw new IllegalArgumentException("bytes argument cannot be null; use HexString.NULL instead");
		} else if (bytes.length == 0) {
			throw new IllegalArgumentException("bytes argument cannot be zero length; use HexString.NULL instead");
		} 
		byte[] copyOfbytes = copyByteArray(bytes);
		String hex = toHexString(copyOfbytes).toUpperCase();
		return new HexString(copyOfbytes, hex);
	}
	
	private static byte[] copyByteArray(byte[] bytes) {
        return Arrays.copyOf(bytes, bytes.length);
    }

	private final String hex;
	private final byte[] bytes;
	
	private HexString(byte[] bytes, String hex) {
		this.bytes = bytes;
		this.hex = hex;			
	}
	
	/**
	 * @return The instance as an unformatted hex string - i.e. \"AB45C03F\" 
	 */
	public String asString() {
		return hex;
	}

	/**
	 * N.B. <code>asBytes()</code> returns a copy of the underlying byte array
	 * 
	 * @return The instance as its underlying byte array 
	 */
	public byte[] asBytes() {
		return copyByteArray(bytes);
	}
	
    @Override
	public String toString() {
		return "0x" + hex;
	}
    
    @Override
    public boolean equals(Object other) {        	
    	if (other != null && other instanceof HexString) {
    		HexString hexString = (HexString) other;
    		return this.hex.equals(hexString.hex);
    	} else {
    		return false;
    	}
    }
    
    @Override
    public int hashCode() {
    	return this.hex.hashCode();
    }

}