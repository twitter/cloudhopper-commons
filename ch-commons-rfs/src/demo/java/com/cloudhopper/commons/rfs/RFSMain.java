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

import com.cloudhopper.commons.util.URL;
import com.cloudhopper.commons.util.URLParser;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class RFSMain {

    private static final Logger logger = LoggerFactory.getLogger(RFSMain.class);

    public static void main(String[] args) throws Exception {

        //String r = "sftp://joelauer@magnum";
        String r = "ftp://user:pass@host/path/to/dir/";

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
