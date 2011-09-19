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

package com.cloudhopper.commons.rfs;

/**
 * Root exception for any interactions with a remote filesystem.
 * 
 * @author joelauer
 */
public class FileSystemException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an instance of <code>FileSystemException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FileSystemException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>FileSystemException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FileSystemException(String msg, Throwable t) {
        super(msg, t);
    }
}
