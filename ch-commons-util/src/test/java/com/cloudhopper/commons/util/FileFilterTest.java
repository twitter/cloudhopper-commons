package com.cloudhopper.commons.util;

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

import com.cloudhopper.commons.util.filefilter.CompositeFileFilter;
import com.cloudhopper.commons.util.filefilter.FileExtensionFilter;
import com.cloudhopper.commons.util.filefilter.FileNameDateTimeFilter;
import com.cloudhopper.commons.util.filefilter.FileNameEndsWithFilter;
import com.cloudhopper.commons.util.filefilter.FileNameStartsWithFilter;
import java.io.File;
import java.io.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;

/**
 * Tests FileFilter packages.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class FileFilterTest {
    private static final Logger logger = LoggerFactory.getLogger(FileFilterTest.class);

    @Test
    public void fileNameDateFilter() throws Exception {
        //
        // create sample directory with a couple entries
        //
        File targetDir = new File("target/sample/fileNameDateFilter");
        targetDir.mkdirs();

        File file0 = new File(targetDir, "sample1.mpg");
        file0.createNewFile();

        File file1 = new File(targetDir, "sample2.csv");
        file1.createNewFile();

        File file2 = new File(targetDir, "sample-2009-06-24.log");
        file2.createNewFile();

        File file3 = new File(targetDir, "sample-2009-06.txt");
        file3.createNewFile();

        //
        // try fileNameDateFilter
        //
        File[] files = FileUtil.findFiles(targetDir, new FileNameDateTimeFilter(new DateTime(2009,6,24,0,0,0,0,DateTimeZone.UTC)));

        Assert.assertEquals(1, files.length);
        Assert.assertEquals(file2, files[0]);
    }
    
    @Test
    public void fileExtensionFilter() throws Exception {
        //
        // create sample directory with a couple entries
        //
        File targetDir = new File("target/sample/fileExtensionFilter");
        targetDir.mkdirs();


        File file0 = new File(targetDir, "sample1.mpg");
        file0.createNewFile();

        File file1 = new File(targetDir, "sample2.csv");
        file1.createNewFile();

        File file2 = new File(targetDir, "sample3-2009-06-24.log");
        file2.createNewFile();

        File file3 = new File(targetDir, "sample4-2009-06.txt");
        file3.createNewFile();

        // try 1 extension
        File[] files = FileUtil.findFiles(targetDir, new FileExtensionFilter("mpg"));

        Assert.assertEquals(1, files.length);
        Assert.assertEquals(file0, files[0]);

        // try multiple extensions
        File[] files2 = FileUtil.findFiles(targetDir, new FileExtensionFilter(new String[] { "mpg", "txt" }));

        Assert.assertEquals(2, files2.length);
        for (File f : files2) {
            logger.debug(f.toString());
        }

        Assert.assertEquals(file0, files2[0]);
        Assert.assertEquals(file3, files2[1]);


        // try multiple extensions, case sensitive
        File[] files3 = FileUtil.findFiles(targetDir, new FileExtensionFilter(new String[] { "Mpg", "Txt" }, true));

        Assert.assertEquals(0, files3.length);


        // try multiple extensions, case insensitive (default setting)
        File[] files4 = FileUtil.findFiles(targetDir, new FileExtensionFilter(new String[] { "Mpg", "Txt" }));

        Assert.assertEquals(2, files4.length);
        Assert.assertEquals(file0, files4[0]);
        Assert.assertEquals(file3, files4[1]);
    }


    @Test
    public void compositeFilter() throws Exception {
        //
        // create sample directory with a couple entries
        //
        File targetDir = new File("target/sample/compositeFilter");
        targetDir.mkdirs();

        File file0 = new File(targetDir, "sample1-2009-06-23.csv");
        file0.createNewFile();

        File file1 = new File(targetDir, "sample2-2009-06-24.log");
        file1.createNewFile();

        File file2 = new File(targetDir, "sample3-2009-06.txt");
        file2.createNewFile();


        // this should find 2 files
        File[] files0 = FileUtil.findFiles(targetDir, new FileNameDateTimeFilter(new DateTime(2009,6,24,0,0,0,0,DateTimeZone.UTC)));

        Assert.assertEquals(2, files0.length);
        Assert.assertEquals(file0, files0[0]);
        Assert.assertEquals(file1, files0[1]);


        // now limit to a file extension
        File[] files1 = FileUtil.findFiles(targetDir, new CompositeFileFilter(new FileFilter[] {
            new FileNameDateTimeFilter(new DateTime(2009,6,24,0,0,0,0,DateTimeZone.UTC)),
            new FileExtensionFilter("log")
        }));

        Assert.assertEquals(1, files1.length);
        Assert.assertEquals(file1, files1[0]);
    }


    @Test
    public void fileNameStartsWithFilter() throws Exception {
        //
        // create sample directory with a couple entries
        //
        File targetDir = new File("target/sample/fileNameStartsWithFilter");
        targetDir.mkdirs();


        File file0 = new File(targetDir, "nanpa-sample1.mpg");
        file0.createNewFile();

        File file1 = new File(targetDir, "nanpa-sample2.csv");
        file1.createNewFile();

        File file2 = new File(targetDir, "sample3-2009-06-24.log");
        file2.createNewFile();

        File file3 = new File(targetDir, "sample4-2009-06.txt");
        file3.createNewFile();

        // try 1 extension
        File[] files = FileUtil.findFiles(targetDir, new FileNameStartsWithFilter("nanpa-"));

        Assert.assertEquals(2, files.length);
        Assert.assertEquals(file0, files[0]);
        Assert.assertEquals(file1, files[1]);
    }

    @Test
    public void fileNameEndsWithFilter() throws Exception {
        //
        // create sample directory with a couple entries
        //
        File targetDir = new File("target/sample/fileNameEndsWithFilter");
        targetDir.mkdirs();


        File file0 = new File(targetDir, "nanpa-sample1.mpg");
        file0.createNewFile();

        File file1 = new File(targetDir, "nanpa-sample2.csv");
        file1.createNewFile();

        File file2 = new File(targetDir, "sample3-2009-06-24.log");
        file2.createNewFile();

        File file3 = new File(targetDir, "sample4-2009-06.txt.gz");
        file3.createNewFile();

        // try 1 extension
        File[] files = FileUtil.findFiles(targetDir, new FileNameEndsWithFilter(".txt.gz"));

        Assert.assertEquals(1, files.length);
        Assert.assertEquals(file3, files[0]);

    }

}
