package com.cloudhopper.sxmp;

/*
 * #%L
 * ch-sxmp
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
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
 * Enumeration of all text encodings supported by the SXMP protocol.
 * 
 * @author joelauer
 */
public enum TextEncoding {

    ISO_8859_1 ("ISO-8859-1"),
    UTF_8 ("UTF-8");

    private final String charset;

    TextEncoding(final String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return this.charset;
    }
    
    public static TextEncoding valueOfCharset(final String charset) {
        for (TextEncoding e : TextEncoding.values()) {
            if (e.charset.equalsIgnoreCase(charset)) {
                return e;
            }
        }
        return null;
    }

}
