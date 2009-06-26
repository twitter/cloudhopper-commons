
package com.cloudhopper.commons.rfs.provider;


import com.cloudhopper.commons.rfs.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;

/**
 * FTP and FTPS remote filesystem.
 * 
 * @author joelauer
 */
public class FtpRemoteFileSystem extends BaseRemoteFileSystem {
    private static final Logger logger = Logger.getLogger(FtpRemoteFileSystem.class);

    // client (will support either FTP or FTPS)
    private FTPClient ftp;
    // are we in ssl mode?
    private boolean ssl;

    public FtpRemoteFileSystem() {
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

            //
            // use passive mode as default because most of us are behind firewalls these days.
            //
            ftp.enterLocalPassiveMode();
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                throw new FileSystemException("FTP server failed to switch to passive mode (reply=" + ftp.getReplyString() + ")");
            }

            //
            // change directories if requested
            //
            if (getURL().getPath() != null) {
                logger.info("Changing FTP directory to: " + getURL().getPath());
                if (!ftp.changeWorkingDirectory(getURL().getPath())) {
                    throw new FileSystemException("FTP server failed to change working directory (reply=" + ftp.getReplyString() + ")");
                }
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
                logger.warn(e);
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
