
package com.cloudhopper.commons.vfs.provider;

import com.jcraft.jsch.Session;

import com.cloudhopper.commons.vfs.*;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;

/**
 * FTP and FTPS virtual filesystem.
 * 
 * @author joelauer
 */
public class FtpVirtualFileSystem extends BaseVirtualFileSystem {
    private static final Logger logger = Logger.getLogger(FtpVirtualFileSystem.class);

    // client (will support either FTP or FTPS)
    private FTPClient ftp;
    // are we in ssl mode?
    private boolean ssl;

    public FtpVirtualFileSystem() {
        super();
        // default ssl mode to false
        ssl = false;
    }

    
    @Override
    public void validateURL() throws FileSystemException {
        // only a hostname needs to be configured
        if (getURL().getHost() == null) {
            throw new FileSystemException("The FTP(s) protocol requires a host");
        }
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
            } catch (NoSuchAlgorithmException e) {
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

            // if we're using a secure protocol, encrypt the data channel
            if (getProtocol() == Protocol.FTPS) {
                logger.info("Requesting FTP data channel to also be encrypted with SSL/TLS");
                ((FTPSClient)ftp).execPROT("P");
            }
        } catch (IOException e) {
            if (ftp.isConnected()) {
                try { ftp.disconnect(); } catch (Exception ex) {}
            }
            ftp = null;
            throw new FileSystemException("Unabled to connect to FTP server @ " + getURL().getHost(), e);
        }





        /**




        try {
            session = jsch.getSession(getURL().getUsername(), getURL().getHost(), (getURL().getPort() == null ? 22 : getURL().getPort().intValue()));
        } catch (JSchException e) {
            throw new FileSystemException("Unable to create SSH session: " + e.getMessage(), e);
        }

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
         */
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
                logger.warn(e);
            }
            ftp = null;
        }

        logger.info("Disconnected to remote FTP server @ " + getURL().getHost());
    }

    public boolean exists(String filename) throws FileSystemException {

        /**
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
         */

        return false;
    }


    public void copy(InputStream in, String filename) throws FileSystemException {
        /**
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
         */
    }
    
}
