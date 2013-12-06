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

import com.cloudhopper.commons.rfs.*;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SFTP remote filesystem.
 *
 * The URL configuration for SFTP is fairly simple.  However, the path component
 * is flexible.  To stay in the default directory after logging in, do not include
 * a path component on the URL such as "sftp://user@host".  If a path is included,
 * it will be treated as an absolute path on the remote system, such that the
 * URL "sftp://user@host/" will result in an attempt to change directories to "/"
 * after connecting.
 * 
 * @author joelauer
 */
public class SftpRemoteFileSystem extends BaseRemoteFileSystem {
    private static final Logger logger = LoggerFactory.getLogger(SftpRemoteFileSystem.class);

    private JSch jsch;
    private Session session;
    private ChannelSftp channel;

    public SftpRemoteFileSystem() {
        super();
    }

    /**
     * Best attempt to find a default .ssh directory on this particular server.
     * While more directories may be attempted, for now the user's home directory
     * will be scanned for a .ssh directory.
     * @return An array of .ssh directories to search or null if none were
     *      found.
     */
    protected File[] findSshDirs() {
        ArrayList<File> dirs = new ArrayList<File>();

        // user's home directory and .ssh subdir
        File sshHomeDir = new File(System.getProperty("user.home"), ".ssh");
        if (sshHomeDir.exists() && sshHomeDir.isDirectory()) {
            dirs.add(sshHomeDir);
        }

        // FIXME: any other directories we should try to scan?

        return dirs.toArray(new File[0]);
    }

    /**
     * Best attempt to find all .ssh private keys (identity) by searching inside
     * every provided .ssh directory.  Currently searches for any "id_rsa" or
     * "id_dsa" files.
     */
    protected File[] findSshPrivateKeys(File[] sshDirs) {
        ArrayList<File> files = new ArrayList<File>();

        // search every directory
        for (File sshDir : sshDirs) {
            File f0 = new File(sshDir, "id_dsa");
            if (f0.exists() && f0.canRead() && f0.isFile()) {
                files.add(f0);
            }

            File f1 = new File(sshDir, "id_rsa");
            if (f1.exists() && f1.canRead() && f1.isFile()) {
                files.add(f1);
            }
        }

        return files.toArray(new File[0]);
    }



    @Override
    public void validateURL() throws FileSystemException {
        // a username and host must have been configured
        if (getURL().getUsername() == null) {
            throw new FileSystemException("The SFTP protocol requires a username");
        }
        if (getURL().getHost() == null) {
            throw new FileSystemException("The SFTP protocol requires a host");
        }
    }

    public void connect() throws FileSystemException {
        // make sure we don't connect twice
        if (session != null) {
            throw new FileSystemException("Already connected to SFTP server");
        }

        // validate the url -- required for sftp (user and host)

        // setup a new SFTP session
        jsch = new JSch();

        // attempt to load identities from the operating system
        // find any .ssh directories we'll scan
        File[] sshDirs = findSshDirs();
        for (File sshDir : sshDirs) {
            logger.info("Going to scan directory for .ssh private keys: " + sshDir.getAbsolutePath());
        }

        // find any identities that we'll then load
        File[] sshPrivateKeys = findSshPrivateKeys(sshDirs);
        for (File sshPrivateKeyFile : sshPrivateKeys) {
            logger.info("Attempting to load .ssh private key (identity): " + sshPrivateKeyFile.getAbsolutePath());
            try {
                jsch.addIdentity(sshPrivateKeyFile.getAbsolutePath());
            } catch (JSchException e) {
                logger.warn("Failed to load private key file " + sshPrivateKeyFile + " - going to ignore");
            }
        }

        try {
            session = jsch.getSession(getURL().getUsername(), getURL().getHost(), (getURL().getPort() == null ? 22 : getURL().getPort().intValue()));
        } catch (JSchException e) {
            throw new FileSystemException("Unable to create SSH session: " + e.getMessage(), e);
        }

        // create fully trusted instance -- any hosts will be accepted
        session.setUserInfo(new UserInfo() {
            public String getPassphrase() {
                return null;
            }
            public String getPassword() {
                return null;
            }
            public boolean promptPassphrase(String string) {
                return false;
            }
            public boolean promptPassword(String string) {
                return false;
            }
            // called when a host's authenticity is questioned
            public boolean promptYesNo(String string) {
                //logger.debug("Jsch promptYesNo: " + string);
                return true;
            }
            public void showMessage(String string) {
                //logger.debug("Jsch showMessage: " + string);
            }
        });

        // if the password is set
        if (getURL().getPassword() != null) {
            session.setPassword(getURL().getPassword());
        }

        // don't cause app to hang
        session.setDaemonThread(true);

        try {
            session.connect();
        } catch (JSchException e) {
            session = null;
            throw new FileSystemException("Unable to connect to SSH server: " + e.getMessage(), e);
        }

        logger.info("Connected to remote SSH server " + getURL().getUsername() + "@" + getURL().getHost());

        // create an SFTP channel
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            // in case the channel failed, always close the parent session first
            try { session.disconnect(); } catch (Exception ex) { }
            session = null;
            throw new FileSystemException("Unable to create SFTP channel on SSH session: " + e.getMessage(), e);
        }

        // based on the URL, make a decision if we should attempt to change dirs
        if (getURL().getPath() != null) {
            logger.info("Changing SFTP directory to: " + getURL().getPath());
            try {
                channel.cd(getURL().getPath());
            } catch (SftpException e) {
                // make sure we disconnect
                try { disconnect(); } catch (Exception ex) { }
                session = null;
                throw new FileSystemException("Unable to change directory on SFTP channel to " + getURL().getPath(), e);
            }
        } else {
            // staying in whatever directory we were assigned by default
            // for information purposeds, let's try to print out that dir
            try {
                String currentDir = channel.pwd();
                logger.info("Current SFTP directory: " + currentDir);
            } catch (SftpException e) {
                // ignore this error
                logger.warn("Unable to get current directory -- safe to ignore");
            }
        }
    }

    public void disconnect() throws FileSystemException {
        // we can't disconnect twice
        if (session == null) {
            throw new FileSystemException("Already disconnected from SFTP server");
        }

        // close channel
        if (channel != null) {
            try {
                channel.disconnect();
            } catch (Exception e) {
                logger.warn("", e);
            }
            channel = null;
        }

        if (session != null) {
            try {
                session.disconnect();
            } catch (Exception e) {
                logger.warn("", e);
            }
            session = null;
        }

        logger.info("Disconnected to remote SSH server " + getURL().getUsername() + "@" + getURL().getHost());
    }

    public boolean exists(String filename) throws FileSystemException {
        // we have to be connected
        if (channel == null) {
            throw new FileSystemException("Not yet connected to SFTP server");
        }

        // easiest way to check if a file already exists is to do a file stat
        // this method will error out if the remote file does not exist!
        try {
            SftpATTRS attrs = channel.stat(filename);
            // if we get here, then file exists
            return true;
        } catch (SftpException e) {
            // map "no file" message to return correct result
            if (e.getMessage().toLowerCase().indexOf("no such file") >= 0) {
                return false;
            }
            // otherwise, this means an underlying error occurred
            throw new FileSystemException("Underlying error with SFTP session while checking if file exists", e);
        }
    }


    public void copy(InputStream in, String filename) throws FileSystemException {
        // does this filename already exist?
        if (exists(filename)) {
            throw new FileSystemException("File " + filename + " already exists on SFTP server");
        }

        // copy the file
        try {
            channel.put(in, filename);
        } catch (SftpException e) {
            throw new FileSystemException("Failed to copy data during PUT with SFTP server", e);
        }
    }
    
}
