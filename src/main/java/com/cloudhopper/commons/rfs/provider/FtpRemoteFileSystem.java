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


import com.cloudhopper.commons.rfs.*;
import com.cloudhopper.commons.util.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FTP and FTPS remote filesystem.
 * 
 * @author joelauer
 */
public class FtpRemoteFileSystem extends BaseRemoteFileSystem {
    private static final Logger logger = LoggerFactory.getLogger(FtpRemoteFileSystem.class);

    public enum Mode {
        PASSIVE,
        ACTIVE
    };

    // client (will support either FTP or FTPS)
    private FTPClient ftp;
    // are we in ssl mode?
    private boolean ssl;
    // which mode should we switch to?  PASSIVE or ACTIVE?
    private Mode mode;
    // should we create the directory path if it doesn't exist?
    private boolean mkdir;

    public FtpRemoteFileSystem() {
        super();
        // default ssl mode to false
        ssl = false;
        // default mode to passive
        mode = Mode.PASSIVE;
    }

    
    @Override
    public void validateURL() throws FileSystemException {
        // only a hostname needs to be configured
        if (getURL().getHost() == null) {
            throw new FileSystemException("The FTP(s) protocol requires a host");
        }

        // "mode" can either be PASSIVE or ACTIVE
        String tempMode = getURL().getQueryProperty("mode");
        if (StringUtil.isEmpty(tempMode)) {
            mode = Mode.PASSIVE;
        } else {
            if (tempMode.equalsIgnoreCase("passive")) {
                mode = Mode.PASSIVE;
            } else if (tempMode.equalsIgnoreCase("active")) {
                mode = Mode.ACTIVE;
            } else {
                throw new FileSystemException("Invalid FTP mode parameter value '" + tempMode + "'");
            }
        }
        logger.info("FTP mode set to " + mode);

        // "mkdir" can either be true or false
        String tempMkdir = getURL().getQueryProperty("mkdir");
        if (StringUtil.isEmpty(tempMkdir)) {
            mkdir = false;
        } else {
            if (tempMkdir.equalsIgnoreCase("true")) {
                mkdir = true;
            } else if (tempMkdir.equalsIgnoreCase("false")) {
                mkdir = false;
            } else {
                throw new FileSystemException("Invalid FTP mkdir parameter value '" + tempMkdir + "'");
            }
        }
        logger.info("FTP create missing parent directories? " + mkdir);

    }

    public void connect() throws FileSystemException {
        // make sure we don't connect twice
        if (ftp != null) {
            throw new FileSystemException("Already connected to FTP(s) server");
        }

        // either create an SSL FTP client or normal one
        if (getProtocol() == Protocol.FTPS) {
            try {
                ftp = new FTPSClient();
            } catch (Exception e) {  //} catch (NoSuchAlgorithmException e) {
                throw new FileSystemException("Unable to create FTPS client: " + e.getMessage(), e);
            }
        } else {
            ftp = new FTPClient();
        }

        //
        // connect to ftp(s) server
        //
        try {
            int reply;

            // either connect to the default port or an overridden one
            if (getURL().getPort() == null) {
                ftp.connect(getURL().getHost());
            } else {
                ftp.connect(getURL().getHost(), getURL().getPort().intValue());
            }

            // After connection attempt, check reply code to verify we're connected
            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                // make sure we're definitely disconnected before we throw exception
                try { ftp.disconnect(); } catch (Exception e) { }
                ftp = null;
                throw new FileSystemException("FTP server refused connection (replyCode=" + reply + ")");
            }

            logger.info("Connected to remote FTP server @ " + getURL().getHost() + " (not authenticated yet)");
            
        } catch (IOException e) {
            if (ftp.isConnected()) {
                try { ftp.disconnect(); } catch (Exception ex) {}
            }
            ftp = null;
            throw new FileSystemException("Unabled to connect to FTP server @ " + getURL().getHost(), e);
        }

        //
        // login either anonymously or with user/pass combo
        //
        try {
            boolean loggedIn = false;
            if (getURL().getUsername() == null) {
                logger.info("Logging in anonymously to FTP server");
                loggedIn = ftp.login( "anonymous", "" );
            } else {
                logger.info("Logging in with username and password to FTP server");
                loggedIn = ftp.login(getURL().getUsername(), (getURL().getPassword() == null ? "" : getURL().getPassword()));
            }

            // did the login work?
            if (!loggedIn) {
                throw new FileSystemException("Login failed with FTP server (reply=" + ftp.getReplyString() + ")");
            }

            //
            // if we're using a secure protocol, encrypt the data channel
            //
            if (getProtocol() == Protocol.FTPS) {
                logger.info("Requesting FTP data channel to also be encrypted with SSL/TLS");
                ((FTPSClient)ftp).execPROT("P");
                // ignore if this actually worked or not -- file just fail to copy
            }

            //
            // make sure we're using binary files
            //
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                throw new FileSystemException("FTP server failed to switch to binary file mode (reply=" + ftp.getReplyString() + ")");
            }


            // should we go into passive or active mode?
            if (mode == Mode.ACTIVE) {
                ftp.enterLocalActiveMode();
                if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                    throw new FileSystemException("FTP server failed to switch to active mode (reply=" + ftp.getReplyString() + ")");
                }
            } else if (mode == Mode.PASSIVE) {
                ftp.enterLocalPassiveMode();
                if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                    throw new FileSystemException("FTP server failed to switch to passive mode (reply=" + ftp.getReplyString() + ")");
                }
            }

            //
            // handle making or changing directories
            //
            // if the path is not empty
            if (!StringUtil.isEmpty(getURL().getPath())) {
                // recursively iterate thru directory path and attempt to create the path
                StringTokenizer path = new StringTokenizer(getURL().getPath(), "/");

                // create an array list of tokens
                ArrayList<String> pathParts = new ArrayList<String>();
                while (path.hasMoreTokens()) {
                    pathParts.add(path.nextToken());
                }

                // index we'll start searching for
                int i = 0;

                // determine path of directories we're going to take
                if (pathParts.size() > 0 && pathParts.get(i).equals("~")) {
                    // stay in home directory once logged in
                    // just increment what we'll search from
                    i = 1;
                } else {
                    // change to root directory first
                    if (!ftp.changeWorkingDirectory("/")) {
                        throw new FileSystemException("FTP server failed to change to root directory (reply=" + ftp.getReplyString() + ")");
                    }
                }

                for ( ; i < pathParts.size(); i++) {
                    // try to change to this directory
                    String pathPart = pathParts.get(i);
                    boolean changedDir = ftp.changeWorkingDirectory(pathPart);
                    if (!changedDir) {
                        if (!mkdir) {
                            // now try to change to it again
                            if (!ftp.changeWorkingDirectory(pathPart)) {
                                throw new FileSystemException("Unable to change to directory " + getURL().getPath() + " on FTP server: " + pathPart + " does not exist");
                            }
                        } else {
                            // try to create it
                            logger.info("Making new directory on FTP server: " + pathPart);
                            if (!ftp.makeDirectory(pathPart)) {
                                throw new FileSystemException("Unable to make directory '" + pathPart + "' on FTP server");
                            }
                            // now try to change to it again
                            if (!ftp.changeWorkingDirectory(pathPart)) {
                                throw new FileSystemException("Unable to change to new directory '" + pathPart + "' on FTP server");
                            }
                        }
                    }
                }
                
            // just print out our working directory
            } else {
                // staying in whatever directory we were assigned by default
                // for information purposeds, let's try to print out that dir
                String currentDir = ftp.printWorkingDirectory();
                logger.info("Current FTP working directory: " + currentDir);
            }

        } catch (FileSystemException e) {
            // make sure to disconnect, then rethrow error
            try { ftp.disconnect(); } catch (Exception ex) { }
            ftp = null;
            throw e;
        } catch (IOException e) {
            // make sure we're definitely disconnected before we throw exception
            try { ftp.disconnect(); } catch (Exception ex) { }
            ftp = null;
            throw new FileSystemException("Underlying IO exception with FTP server during login and setup process", e);
        }
    }

    public void disconnect() throws FileSystemException {
        // we can't disconnect twice
        if (ftp == null) {
            throw new FileSystemException("Already disconnected from FTP server");
        }
        
        if (ftp != null) {
            try {
                ftp.disconnect();
            } catch (Exception e) {
                logger.warn("", e);
            }
            ftp = null;
        }

        logger.info("Disconnected to remote FTP server @ " + getURL().getHost());
    }

    public boolean exists(String filename) throws FileSystemException {
        // we have to be connected
        if (ftp == null) {
            throw new FileSystemException("Not yet connected to FTP server");
        }

        try {
            // check if the file already exists
            FTPFile[] files = ftp.listFiles(filename);

            // did this command succeed?
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                throw new FileSystemException("FTP server failed to get file listing (reply=" + ftp.getReplyString() + ")");
            }

            if (files != null && files.length > 0) {
                // this file already exists
                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
            throw new FileSystemException("Underlying IO exception with FTP server while checking if file exists", e);
        }
    }


    public void copy(InputStream in, String filename) throws FileSystemException {
        // does this filename already exist?
        if (exists(filename)) {
            throw new FileSystemException("File " + filename + " already exists on FTP server");
        }

        // copy the file
        try {
            // this overwrites by default
            boolean stored = ftp.storeFile(filename, in);
            if (!stored) {
                throw new FileSystemException("FTP server failed to store file (reply=" + ftp.getReplyString() + ")");
            }
        } catch (IOException e) {
            throw new FileSystemException("Failed to copy data during PUT with SFTP server", e);
        }
    }
    
}
