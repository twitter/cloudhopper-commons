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

// java imports
import java.net.MalformedURLException;

// third party imports
import org.apache.log4j.Logger;

// my imports
import com.cloudhopper.commons.util.URL;
import com.cloudhopper.commons.util.URLParser;
import com.cloudhopper.commons.rfs.provider.BaseRemoteFileSystem;

/**
 * Factory for creating remote filesystems from a URL.
 * 
 * @author joelauer
 */
public class RemoteFileSystemFactory {
    private static final Logger logger = Logger.getLogger(RemoteFileSystemFactory.class);

    private RemoteFileSystemFactory() {
        // only static methods
    }

    /**
     * Creates a new RemoteFileSystem by parsing the URL and creating the
     * correct underlying provider to support the protocol.
     */
    public static RemoteFileSystem create(String url) throws MalformedURLException, FileSystemException {
        // parse url into a URL object
        URL r = URLParser.parse(url);
        // delegate responsibility to other method
        return create(r);
    }

    public static RemoteFileSystem create(URL url) throws MalformedURLException, FileSystemException {
        // try to match the url to a provider
        Protocol protocol = Protocol.findByName(url.getProtocol());
        if (protocol == null) {
            throw new MalformedURLException("Unsupported virtual filesystem protocol '" + url.getProtocol() + "'");
        }

        // create a new instance of the provider and return it
        BaseRemoteFileSystem vfs = null;
        try {
            Class vfsClass = protocol.getProvider();
            // create a new instance, cast it
            vfs = (BaseRemoteFileSystem)vfsClass.newInstance();
        } catch (Exception e) {
            throw new FileSystemException("Could not create instance of " + protocol.getProvider(), e);
        }

        // setup any common properties
        vfs.setURL(url);
        vfs.setProtocol(protocol);

        // validate the properties
        vfs.validateURL();

        return vfs;
    }

}
