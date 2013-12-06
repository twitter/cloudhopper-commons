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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * http://ziesemer.dev.java.net/
 *
 * @author Mark A. Ziesemer
 * <a href="http://www.ziesemer.com.">&lt;www.ziesemer.com&gt;</a>
 */
public class URLDecoder implements Appendable, Flushable, Closeable{
	
	protected static final int DEFAULT_BUFFER_SIZE = 1 << 10;
	
	protected final Appendable out;
	protected final Charset charset;
	protected final CharBuffer singleCharBuffer = CharBuffer.allocate(1);
	
	protected final CharsetDecoder cd;
	protected final ByteBuffer bb;
	protected final CharBuffer cb;
	
	/**
	 * Decodes to the specified {@link Appendable},
	 * 	using {@link #DEFAULT_BUFFER_SIZE}
	 * 	and {@link URLCodec#UTF_8}.
	 * Convenience method for {@link #URLDecoder(Appendable, int, Charset)}.
	 */
	public URLDecoder(Appendable out){
		this(out, DEFAULT_BUFFER_SIZE, URLCodec.UTF_8);
	}
	
	/**
	 * Decodes to the specified {@link Appendable}
	 * 	with the specified bufferSize,
	 * 	using {@link URLCodec#UTF_8}.
	 * Convenience method for {@link #URLDecoder(Appendable, int, Charset)}.
	 */
	public URLDecoder(Appendable out, int bufferSize){
		this(out, bufferSize, URLCodec.UTF_8);
	}
	
	/**
	 * Decodes to the specified {@link Appendable}
	 * 	with the specified bufferSize and charset.
	 */
	public URLDecoder(Appendable out, int bufferSize, Charset charset){
		this.out = out;
		this.charset = charset;
		
		this.cd = charset.newDecoder()
			.onMalformedInput(CodingErrorAction.REPLACE)
			.onUnmappableCharacter(CodingErrorAction.REPLACE);
		this.bb = ByteBuffer.allocate(bufferSize);
		this.cb = CharBuffer.allocate((int)Math.ceil(bufferSize * cd.maxCharsPerByte()));
	}
	
	public Appendable append(CharSequence in) throws IOException{
		// Ideally, this would share code with URLCodec#decodeUrl(CharSequence, OutputStream),
		// 	but due to operational difference and no common interface between
		// 	ByteBuffer and OutputStream, any attempts to share code result in
		// 	severe performance penalties.
		
		if(in == null) return this;
		for(int i=0, len=in.length(); i<len; i++){
			char c = in.charAt(i);
			if(c == '+'){
				bb.put((byte)' ');
			}else if(c == '%'){
				int x = Character.digit(in.charAt(++i), 16);
				int y = Character.digit(in.charAt(++i), 16);
				if(x == -1 || y == -1){
					throw new IOException("Invalid URL encoding");
				}
				bb.put((byte)((x << 4) + y));
			}else{
				bb.put((byte)c);
			}
			if(!bb.hasRemaining()){
				flush(false);
			}
		}
		return this;
	}
	
	public Appendable append(char c) throws IOException{
		singleCharBuffer.put(0, c);
		return append(singleCharBuffer);
	}
	
	public Appendable append(CharSequence csq, int start, int end) throws IOException{
		return append(csq.subSequence(start, end));
	}
	
	protected void flush(boolean endOfInput) throws IOException{
		bb.flip();
		CoderResult cr = cd.decode(bb, cb, endOfInput);
		if(cr.isUnderflow()){
			cb.flip();
			out.append(cb);
			cb.clear();
		}else{
			throw new IOException("Unexpected: " + cr);
		}
		bb.clear();
		if(endOfInput){
			cr = cd.flush(cb);
			if(cr.isUnderflow()){
				cb.flip();
				out.append(cb);
				cb.clear();
			}else{
				throw new IOException("Unexpected: " + cr);
			}
			cd.reset();
		}
	}
	
	public void flush() throws IOException{
		flush(true);
	}
	
	public void close() throws IOException{
		flush();
	}
}
