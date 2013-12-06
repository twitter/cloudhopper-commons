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

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class BufferIsFullException extends BufferException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>BufferIsFullException</code> without detail message.
     */
    public BufferIsFullException() {
        super();
    }


    /**
     * Constructs an instance of <code>BufferIsFullException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public BufferIsFullException(String msg) {
        super(msg);
    }
}
