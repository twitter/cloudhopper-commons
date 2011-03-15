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

package com.cloudhopper.commons.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.log4j.Logger;

/**
 *
 * @author joelauer
 */
public abstract class JavaCharset extends BaseCharset {
    private static final Logger logger = Logger.getLogger(JavaCharset.class);

    private final java.nio.charset.Charset charset;

    public JavaCharset(String charsetName) {
        try {
            this.charset = java.nio.charset.Charset.forName(charsetName);
        } catch (UnsupportedCharsetException e) {
            throw new IllegalArgumentException("Unsupported Java charset [" + charsetName + "]");
        }
    }

    public byte[] encode(CharSequence str0) {
        if (str0 == null) {
            return null;
        }

        return str0.toString().getBytes(charset);
    }

    public void decode(byte[] bytes, StringBuilder buffer) {
        if (bytes == null) {
            // append nothing
            return;
        }
        
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        CharBuffer charBuffer = charset.decode(byteBuffer);
        buffer.append(charBuffer);
    }

}
