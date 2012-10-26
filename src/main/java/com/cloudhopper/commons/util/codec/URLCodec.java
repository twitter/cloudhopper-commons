package com.cloudhopper.commons.util.codec;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;

/**
 * http://ziesemer.dev.java.net/
 *
 * @author Mark A. Ziesemer <a href="http://www.ziesemer.com.">&lt;www.ziesemer.com&gt;</a>
 */
public class URLCodec{
	/**
	 * http://tools.ietf.org/html/rfc3986#section-2
	 */
	protected static final BitSet UNRESERVED = new BitSet(256);
	protected static final Charset UTF_8 = Charset.forName("UTF-8");

	static{
		// http://tools.ietf.org/html/rfc3986#section-2.3
		for(int i = 'a'; i <= 'z'; i++){
			UNRESERVED.set(i);
		}
		for(int i = 'A'; i <= 'Z'; i++){
			UNRESERVED.set(i);
		}
		for(int i = '0'; i <= '9'; i++){
			UNRESERVED.set(i);
		}

		UNRESERVED.set('-');
		UNRESERVED.set('_');
		UNRESERVED.set('.');
		UNRESERVED.set('*');
		// Will be replaced with '+'.
		UNRESERVED.set(' ');
	}
	
	/**
	 * Convenience method for {@link #encode(CharSequence, Appendable)}.
	 */
	public static CharSequence encode(CharSequence in) throws IOException{
		if(in == null) return null;
		// Assume that some but not all characters need to be escaped.
		StringBuilder sb = new StringBuilder(in.length() * 2);
		encode(in, sb);
		return sb;
	}
	
	/**
	 * Convenience method for {@link #encode(CharSequence, Charset, Appendable)}.
	 */
	public static void encode(CharSequence in, Appendable out) throws IOException{
		encode(in, UTF_8, out);
	}
	
	public static void encode(CharSequence in, Charset charset, Appendable out) throws IOException{
		CharBuffer cb;
		if(in instanceof CharBuffer){
			cb = (CharBuffer)in;
		}else if(in != null){
			cb = CharBuffer.wrap(in);
		}else{
			return;
		}
		encode(charset.encode(cb), out);
	}

	public static void encode(ByteBuffer in, Appendable out) throws IOException{
		if(in == null) return;
		while(in.hasRemaining()){
			int b = in.get();
			if(b < 0){
				b+= 256;
			}
			if(UNRESERVED.get(b)){
				if(b == ' '){
					b = '+';
				}
				out.append((char)b);
			}else{
				out.append('%');
				char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
				char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
				out.append(hex1);
				out.append(hex2);
			}
		}
		in.clear();
	}
	
	public static CharSequence decode(CharSequence in) throws IOException{
		if(in == null) return null;
		StringBuilder sb = new StringBuilder(in.length());
		decode(in, sb);
		return sb;
	}
	
	/**
	 * Convenience method for {@link #decode(CharSequence, Charset, Appendable)}.
	 */
	public static void decode(CharSequence in, Appendable out) throws IOException{
		decode(in, UTF_8, out);
	}
	
	public static void decode(CharSequence in, Charset charset, Appendable out) throws IOException{
		// This works a bit differently, due to the lack of an Appendable-equivalent for bytes (rather than chars).
		// As such, there is no common interface for OutputStream and ByteBuffer.
		// OutputStream is used for the base method, as an implementation can be continually appended to without limit.
		if(in == null) return;
		URLDecoder ud = new URLDecoder(out, in.length(), charset);
		ud.append(in);
		ud.close();
	}
	
	/**
	 * Convenience method for {@link #decode(CharSequence, OutputStream)}.
	 */
	public static byte[] decodeToBytes(CharSequence in) throws IOException{
		if(in == null) return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(in.length());
		decode(in, baos);
		return baos.toByteArray();
	}
	
	public static void decode(CharSequence in, OutputStream out) throws IOException{
		CharBuffer cb;
		if(in instanceof CharBuffer){
			cb = (CharBuffer)in;
		}else if(in != null){
			cb = CharBuffer.wrap(in);
		}else{
			return;
		}
		while(cb.hasRemaining()){
			char c = cb.get();
			if(c == '+'){
				out.write(' ');
			}else if(c == '%'){
				int x = Character.digit(cb.get(), 16);
				int y = Character.digit(cb.get(), 16);
				if(x == -1 || y == -1){
					throw new IOException("Invalid URL encoding");
				}
				out.write((x << 4) + y);
			}else{
				out.write(c);
			}
		}
	}
}
