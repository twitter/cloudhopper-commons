package com.cloudhopper.commons.util.demo;

/*
 * #%L
 * ch-commons-util
 * %%
 * Copyright (C) 2012 Cloudhopper by Twitter
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

// java imports

import com.cloudhopper.commons.util.CompressionUtil;
import com.cloudhopper.commons.util.FileUtil;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class CompressionUtilMain {
    private static final Logger logger = LoggerFactory.getLogger(CompressionUtilMain.class);
    
    public static void main(String[] args) throws Exception {

        // copy "Dependency.txt" to build directory -- this will be our test file for all compress/uncompress
        File sourceFile = new File("Dependency.txt");
        File targetFile = new File("build", sourceFile.getName());
        FileUtil.copy(sourceFile, targetFile, true);

        // compress the targetFile, then uncompress and compare with original
        File compressedFile = CompressionUtil.compress(targetFile, "zip", false);

        // renamed this compressed file
        File newCompressedFile = new File("build/Dependency2.txt.zip");
        compressedFile.renameTo(newCompressedFile);

        // uncompress this file
        File newUncompressedFile = CompressionUtil.uncompress(newCompressedFile, new File("build"), true);

        // compare the two files
        boolean result = FileUtil.equals(sourceFile, newUncompressedFile);
        logger.debug("file compare = " + result);

    }

}
