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

import com.cloudhopper.commons.util.filefilter.FileNameDateTimeFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests FileUtil class.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class FileUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(FileUtilTest.class);
    
    private static final String TEST_FILE = "CHANGELOG.md";

    @Test
    public void copyAndEquals() throws Exception {
        //
        // test copy and equals(file, file) commands -- since we need to compare
        // the files to make sure the copy worked :-)
        //

        //  file to use for tests
        File sourceFile = new File(TEST_FILE);

        // copy original source to build directory -- becomes our actual source file
        File targetFile = new File("target", sourceFile.getName() + ".2");

        // in case "ant clean" wasn't run before this unit test, let's make sure
        // this file is deleted before we copy
        targetFile.delete();

        FileUtil.copy(sourceFile, targetFile);

        // files objects should NOT match
        Assert.assertEquals(false, sourceFile.equals(targetFile));
        // file contents SHOULD match
        Assert.assertEquals(true, FileUtil.equals(sourceFile, targetFile));

        //
        // try to copy the source to the target again, this should fail since
        // the target already exists
        //
        try {
            FileUtil.copy(sourceFile, targetFile);
            Assert.fail("Copy should have failed since target file already exists");
        } catch (FileAlreadyExistsException e) {
            // correct behavior
        }

        //
        // copy the source file once again, this time permitting an overwrite
        //
        boolean overwritten = FileUtil.copy(sourceFile, targetFile, true);
        Assert.assertTrue(overwritten);
    }

    @Test
    public void isValidFileExtension() throws Exception {
        Assert.assertEquals(true, FileUtil.isValidFileExtension("log"));
        Assert.assertEquals(true, FileUtil.isValidFileExtension("log2"));
        Assert.assertEquals(true, FileUtil.isValidFileExtension("log_bak"));
        Assert.assertEquals(false, FileUtil.isValidFileExtension(".log"));
        Assert.assertEquals(false, FileUtil.isValidFileExtension("$log"));
        Assert.assertEquals(false, FileUtil.isValidFileExtension(" log"));
    }

    @Test
    public void parseFileExtension() throws Exception {
        String ext0 = FileUtil.parseFileExtension("app.208-05-01.log");
        Assert.assertEquals("log", ext0);
        ext0 = FileUtil.parseFileExtension("app.208-05-01.log.gz");
        Assert.assertEquals("gz", ext0);
        ext0 = FileUtil.parseFileExtension("app.log.gz");
        Assert.assertEquals("gz", ext0);
        ext0 = FileUtil.parseFileExtension("app");
        Assert.assertEquals(null, ext0);
        ext0 = FileUtil.parseFileExtension("app.");
        Assert.assertEquals(null, ext0);
        ext0 = FileUtil.parseFileExtension(".");
        Assert.assertEquals(null, ext0);
        ext0 = FileUtil.parseFileExtension("");
        Assert.assertEquals(null, ext0);
        ext0 = FileUtil.parseFileExtension(null);
        Assert.assertEquals(null, ext0);
    }

    @Test(expected=FileNotFoundException.class)
    public void findFilesThrowsException0() throws Exception {
        // samplelogs2 does not exist!
        FileUtil.findFiles(new File("target/samplelogs2"), null);
    }

    @Test(expected=FileNotFoundException.class)
    public void findFilesThrowsException1() throws Exception {
        // build.xml is not a directory!
        FileUtil.findFiles(new File("build.xml"), null);
    }

    /**
    @Test
    public void findFiles() throws Exception {
        // samplelogs directory must exist -- needs to be created and populated
        // by the ant build process -- this should match every file by default
        File[] f0 = FileUtil.findFiles(new File("target/samplelogs"), new AllFileMatcher());
        // exactly 4 files should exist starting from today's date and then
        // the preceeding 3 days before that as well
        Assert.assertEquals(4, f0.length);

        f0 = FileUtil.findFiles(new File("target/samplelogs"), new NoFileMatcher());
        // no files should have matched
        Assert.assertEquals(0, f0.length);
    }

    // helper class
    private class AllFileMatcher implements FileFilter {
        // accept every file by default
        public boolean accept(File pathname) {
            return true;
        }
    }

    // helper class
    private class NoFileMatcher implements FileFilter {
        // not match every file by default
        public boolean accept(File pathname) {
            return false;
        }
    }
     */

    @Test
    public void fileNameDateTimeComparatorClass() throws Exception {
        //
        // create sample directory with a couple entries
        //
        File targetDir = new File("target/sample/fileNameDateTimeComparatorClass");
        targetDir.mkdirs();

        File file0 = new File(targetDir, "sample1-2009-06-25.log");
        file0.createNewFile();

        File file1 = new File(targetDir, "sample2-2009-06-23.log");
        file1.createNewFile();

        File file2 = new File(targetDir, "sample3-2009-06-24.log");
        file2.createNewFile();

        //
        // alphabetically, these files should actually go sample1, sample2, then sample3
        // but if we sort by the embedded date, they shoudl go sample2, sample3, then sample1
        //

        // try 1 extension
        File[] files = FileUtil.findFiles(targetDir, new FileNameDateTimeFilter(new DateTime(2009,6,25,0,0,0,0,DateTimeZone.UTC)));

        // sort by embedded date
        Arrays.sort(files, new FileUtil.FileNameDateTimeComparator("yyyy-MM-dd", DateTimeZone.UTC));

        Assert.assertEquals(3, files.length);
        Assert.assertEquals(file1, files[0]);
        Assert.assertEquals(file2, files[1]);
        Assert.assertEquals(file0, files[2]);
    }

}
