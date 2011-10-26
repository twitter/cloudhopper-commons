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

/**
 * Charset for UTF-8.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class UTF8Charset extends JavaCharset {
    
    public UTF8Charset() {
        super("UTF8");
    }

    @Override
    public int estimateEncodeByteLength(CharSequence str0) {
        if (str0 == null) {
            return 0;
        }
        // let's double the estimate
        return str0.length() * 2;
    }

    @Override
    public int estimateDecodeCharLength(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        // best guess would be 8-bit chars
        return bytes.length;
    }

    /**
     * Does not actually calculate a proper UTF-8 length, but rather a Modified
     * UTF-8 byte length.  It normally matches a real UTF-8 encoding but isn't
     * technically completely valid.
     * @deprecated 
     */
    @Deprecated
    public static int calculateByteLength(final String s) {
        return ModifiedUTF8Charset.calculateByteLength(s);
    }
}
