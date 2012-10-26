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
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * http://ziesemer.dev.java.net/
 *
 * @author Mark A. Ziesemer
 * <a href="http://www.ziesemer.com.">&lt;www.ziesemer.com&gt;</a>
 */
public class URLEncoder implements Appendable, Flushable, Closeable{
	
	protected static final int DEFAULT_BUFFER_SIZE = 1 << 10;
	
	protected final Appendable out;
	protected final Charset charset;
	protected final CharBuffer singleCharBuffer = CharBuffer.allocate(1);
	
	protected final CharsetEncoder ce;
	protected final CharBuffer cb;
	protected final ByteBuffer bb;
	
	/**
	 * Encodes to the specified {@link Appendable},
	 * 	using {@link #DEFAULT_BUFFER_SIZE}
	 * 	and {@link URLCodec#UTF_8}.
	 * Convenience method for {@link #URLEncoder(Appendable, int, Charset)}.
	 */
	public URLEncoder(Appendable out){
		this(out, DEFAULT_BUFFER_SIZE, URLCodec.UTF_8);
	}
	
	/**
	 * Encodes to the specified {@link Appendable}
	 * 	with the specified bufferSize,
	 * 	using {@link URLCodec#UTF_8}.
	 * Convenience method for {@link #URLEncoder(Appendable, int, Charset)}.
	 */
	public URLEncoder(Appendable out, int bufferSize){
		this(out, bufferSize, URLCodec.UTF_8);
	}
	
	/**
	 * Encodes to the specified {@link Appendable}
	 * 	with the specified bufferSize and charset.
	 */
	public URLEncoder(Appendable out, int bufferSize, Charset charset){
		this.out = out;
		this.charset = charset;
		
		this.ce = charset.newEncoder()
			.onMalformedInput(CodingErrorAction.REPLACE)
			.onUnmappableCharacter(CodingErrorAction.REPLACE);
		this.cb = CharBuffer.allocate(bufferSize);
		this.bb = ByteBuffer.allocate((int)Math.ceil(bufferSize * ce.maxBytesPerChar()));
	}
	
	public Appendable append(CharSequence in) throws IOException{
		for(int start = 0, len = in.length(); start < len;){
			int read = Math.min(cb.remaining(), len - start);
			cb.append(in, start, start + read);
			start += read;
			if(!cb.hasRemaining()){
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
		cb.flip();
		CoderResult cr = ce.encode(cb, bb, endOfInput);
		if(cr.isUnderflow()){
			bb.flip();
			URLCodec.encode(bb, out);
		}else{
			throw new IOException("Unexpected: " + cr);
		}
		cb.clear();
		if(endOfInput){
			cr = ce.flush(bb);
			if(cr.isUnderflow()){
				bb.flip();
				URLCodec.encode(bb, out);
			}else{
				throw new IOException("Unexpected: " + cr);
			}
			ce.reset();
		}
	}
	
	public void flush() throws IOException{
		flush(true);
	}
	
	public void close() throws IOException{
		flush();
	}

}
