
package com.cloudhopper.commons.vfs;

// java imports
import java.net.MalformedURLException;

// third party imports
import org.apache.log4j.Logger;

// my imports
import com.cloudhopper.commons.util.URL;
import com.cloudhopper.commons.util.URLParser;
import com.cloudhopper.commons.vfs.provider.BaseVirtualFileSystem;

/**
 * Factory for creating virtual filesystem providers from a URL.
 * 
 * @author joelauer
 */
public class VirtualFileSystemFactory {
    private static final Logger logger = Logger.getLogger(VirtualFileSystemFactory.class);

    private VirtualFileSystemFactory() {
        // only static methods
    }

    public static VirtualFileSystem create(String url) throws MalformedURLException, FileSystemException {
        // parse url into a URL object
        URL r = URLParser.parse(url);
        // delegate responsibility to other method
        return create(r);
    }

    public static VirtualFileSystem create(URL url) throws MalformedURLException, FileSystemException {
        // try to match the url to a provider
        Protocol protocol = Protocol.findByName(url.getProtocol());
        if (protocol == null) {
            throw new MalformedURLException("Unsupported virtual filesystem protocol '" + url.getProtocol() + "'");
        }

        // create a new instance of the provider and return it
        BaseVirtualFileSystem vfs = null;
        try {
            Class vfsClass = protocol.getProvider();
            // create a new instance, cast it
            vfs = (BaseVirtualFileSystem)vfsClass.newInstance();
        } catch (Exception e) {
            throw new FileSystemException("Could not create instance of " + protocol.getProvider(), e);
        }

        // setup any common properties
        vfs.setURL(url);

        // validate the properties
        vfs.validateURL();

        return vfs;
    }

}
