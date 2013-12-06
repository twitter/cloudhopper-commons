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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * <p>Similar to {@link URLEncoder}, but designed to work with byte and byte array inputs.</p>
 * @author Mark A. Ziesemer
 * <a href="http://www.ziesemer.com.">&lt;www.ziesemer.com&gt;</a>
 */
public class URLEncoderOutputStream extends OutputStream{
	
	protected final Appendable out;
	protected final ByteBuffer singleByteBuffer = ByteBuffer.allocate(1);
	
	/**
	 * Encodes to the specified {@link Appendable}.
	 */
	public URLEncoderOutputStream(Appendable out){
		this.out = out;
	}
	
	@Override
	public void write(int b) throws IOException{
		singleByteBuffer.put(0, (byte)b);
		URLCodec.encode(singleByteBuffer, out);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException{
		URLCodec.encode(ByteBuffer.wrap(b, off, len), out);
	}
}
