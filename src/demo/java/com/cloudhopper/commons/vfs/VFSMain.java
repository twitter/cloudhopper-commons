
package com.cloudhopper.commons.vfs;


import com.cloudhopper.commons.util.URL;
import com.cloudhopper.commons.util.URLParser;
import java.io.File;
import org.apache.log4j.Logger;

/**
 *
 * @author joelauer
 */
public class VFSMain {

    private static final Logger logger = Logger.getLogger(VFSMain.class);

    public static void main(String[] args) throws Exception {

        //String r = "sftp://joelauer@magnum";
        String r = "ftp://joelauer:b1zn%40tch!@localhost/home/joelauer";

        URL url = URLParser.parse(r);

        logger.info("protocol [" + url.getProtocol() + "]");
        logger.info("username [" + url.getUsername() + "]");
        logger.info("password [" + url.getPassword() + "]");
        logger.info("host [" + url.getHost() + "]");

        VirtualFileSystem vfs = VirtualFileSystemFactory.create(url);

        logger.debug("created vfs instance: " + vfs.getClass());

        // connect to the fs
        vfs.connect();

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
        vfs.copy(file);

        // disconnect from the fs
        vfs.disconnect();
    }



}
