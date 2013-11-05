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

package com.cloudhopper.commons.rfs.provider;

/*
 * #%L
 * ch-commons-rfs
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

import com.cloudhopper.commons.util.URL;
import com.cloudhopper.commons.rfs.FileSystemException;
import com.cloudhopper.commons.rfs.Protocol;
import com.cloudhopper.commons.rfs.RemoteFileSystem;
import java.io.File;
import java.io.FileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all remote filesystem providers.
 * 
 * @author joelauer
 */
public abstract class BaseRemoteFileSystem implements RemoteFileSystem {
    private static final Logger logger = LoggerFactory.getLogger(BaseRemoteFileSystem.class);

    // url associated with this rfs
    private URL url;
    // the protocol enumeration associated with this rfs
    private Protocol protocol;

    public BaseRemoteFileSystem() {
        // do nothing
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public void setURL(URL url) {
        this.url = url;
    }

    /**
     * Gets the URL associated with this remote filesystem.
     * @return
     */
    public URL getURL() {
        return this.url;
    }

    /**
     * Validate the URL configured with this filesystem.  Each protocol will
     * have its own requirements that must be met.  For example, the sftp
     * protocol requires a username while an ftp connection could be anonymous.
     * @throws FileSystemException Thrown if the URL is not valid and cannot
     *      be used with this particular filesystem.
     */
    public abstract void validateURL() throws FileSystemException;

    
    public void copy(File srcFile) throws FileSystemException {
        // always just call the other method
        copy(srcFile, srcFile.getName());
    }

    public void copy(File srcFile, String filename) throws FileSystemException {
        // make sure the file exists
        if (!srcFile.exists()) {
            throw new FileSystemException("Source file " + srcFile.getName() + " does not exist");
        }

        // make sure we are permitted to read it
        if (!srcFile.canRead()) {
            throw new FileSystemException("Cannot read source file " + srcFile.getName() + " (permission denied?)");
        }

        // open an input stream
        FileInputStream in = null;
        try {
            in = new FileInputStream(srcFile);
        } catch (Exception e) {
            throw new FileSystemException("Unable to create input stream for file " + srcFile.getName(), e);
        }

        try {
            // delegate handling to input stream method
            copy(in, filename);

            logger.info("Successfully copied file " + srcFile.getName() + " to remote filesystem");
        } finally {
            // make sure file input stream is closed
            try { in.close(); } catch (Exception ex) {}
        }
    }

    public void move(File srcFile) throws FileSystemException {
        move(srcFile, srcFile.getName());
    }

    public void move(File srcFile, String filename) throws FileSystemException {
        // initially, we need to copy this file first
        copy(srcFile, filename);

        // if there is an error during the copy, an exception would have been thrown
        // therefore, it should be okay to delete the file now
        srcFile.delete();

        logger.info("Successfully deleted file " + srcFile.getName() + " from local filesystem");
    }

}
