
package com.cloudhopper.commons.rfs;


import com.cloudhopper.commons.util.URL;
import com.cloudhopper.commons.util.URLParser;
import java.io.File;
import org.apache.log4j.Logger;

/**
 *
 * @author joelauer
 */
public class RFSMain {

    private static final Logger logger = Logger.getLogger(RFSMain.class);

    public static void main(String[] args) throws Exception {

        //String r = "sftp://joelauer@magnum";
        String r = "ftp://joelauer@localhost/home/joelauer";

        URL url = URLParser.parse(r);

        logger.info("protocol [" + url.getProtocol() + "]");
        logger.info("username [" + url.getUsername() + "]");
        logger.info("password [" + url.getPassword() + "]");
        logger.info("host [" + url.getHost() + "]");

        RemoteFileSystem rfs = RemoteFileSystemFactory.create(url);

        logger.debug("created rfs instance: " + rfs.getClass());

        // connect to the fs
        rfs.connect();

        File file = new File("Dependency.txt");

        // does the file already exist?
        /**
        if (vfs.exists(file.getName())) {
            logger.info("File " + file.getName() + " already exists on remote system");
        } else {
            logger.info("File " + file.getName() + " does not yet exist on remote system");
        }
         */

        // copy the file
        rfs.copy(file);

        // disconnect from the fs
        rfs.disconnect();
    }



}
