
package com.cloudhopper.commons.vfs;

import com.cloudhopper.commons.vfs.provider.*;

/**
 * Enumerates the protocols supported by this virtual filesystem.
 *
 * @author joelauer
 */
public enum Protocol {
    
    /** sftp */
    SFTP("sftp", SftpVirtualFileSystem.class);

    private final String name;
    private final Class<? extends VirtualFileSystem> vfsClass;

    Protocol(final String name, final Class<? extends VirtualFileSystem> vfsClass) {
        this.name = name;
        this.vfsClass = vfsClass;
    }

    /**
     * Gets the name of this protocol such as "sftp"
     */
    public String getName() {
        return this.name;
    }

    public Class<? extends VirtualFileSystem> getProvider() {
        return this.vfsClass;
    }

    /**
     * Finds the matching protocol by its name such as "sftp"
     * @param name The protocol/scheme name such as "sftp"
     * @return The matching protocol or null if not supported
     */
    public static Protocol findByName(final String name) {
        for (Protocol e : Protocol.values()) {
            if (e.name.equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }
    
}
