package com.cloudhopper.commons.rfs;

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

import com.cloudhopper.commons.rfs.provider.*;

/**
 * Enumerates the protocols supported by this remote filesystem.
 *
 * @author joelauer
 */
public enum Protocol {
    
    /** sftp (SSH/SCP Secure File Transfer Protocol) */
    SFTP("sftp", SftpRemoteFileSystem.class),
    /** ftp (File Transfer Protocol) */
    FTP("ftp", FtpRemoteFileSystem.class),
    /** ftps (SSL/TLS File Transfer Protocol) */
    FTPS("ftps", FtpRemoteFileSystem.class);       // serviced by same provider

    private final String name;
    private final Class<? extends RemoteFileSystem> vfsClass;

    Protocol(final String name, final Class<? extends RemoteFileSystem> vfsClass) {
        this.name = name;
        this.vfsClass = vfsClass;
    }

    /**
     * Gets the name of this protocol such as "sftp"
     */
    public String getName() {
        return this.name;
    }

    public Class<? extends RemoteFileSystem> getProvider() {
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
