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
 * Charset for UCS2 (ISO-10646-UCS-2 in Java).
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class UCS2Charset extends JavaCharset {
    
    public UCS2Charset() {
        super("ISO-10646-UCS-2");
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
        if (bytes.length % 2 == 0) {
            return bytes.length / 2;
        } else {
            return (bytes.length / 2) + 1;
        }
    }

}
