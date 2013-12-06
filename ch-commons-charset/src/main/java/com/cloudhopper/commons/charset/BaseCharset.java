package com.cloudhopper.commons.charset;

/*
 * #%L
 * ch-commons-charset
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

/**
 * Base Charset class implementing common functionality.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public abstract class BaseCharset implements Charset {

    /**
     * Default implementation that simply returns a String by creating a new
     * StringBuffer, appending to it, and then returning a new String.  NOTE:
     * This method is NOT efficient since it requires a double copy of a new
     * String.  Some charsets will override this default implementation to
     * provide a more efficient impl.
     */
    @Override
    public String decode(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder(estimateDecodeCharLength(bytes));
        decode(bytes, buffer);
        return buffer.toString();
    }
    
    @Override
    public String normalize(CharSequence str0) {
        byte[] bytes = this.encode(str0);
        // normalizing a string should never be result in a longer string
        StringBuilder buf = new StringBuilder(str0.length());
        this.decode(bytes, buf);
        return buf.toString();
    }

}
