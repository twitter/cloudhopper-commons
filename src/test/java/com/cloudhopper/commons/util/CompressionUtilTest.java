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

import com.cloudhopper.commons.util.CompressionUtil.Algorithm;
import java.io.File;
import org.junit.*;

/**
 * Tests CompressionUtil class.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class CompressionUtilTest {
    
    private static final String TEST_FILE = "CHANGELOG.md";

    @Test
    public void isAlgorithmSupported() throws Exception {
        Assert.assertEquals(true, CompressionUtil.isAlgorithmSupported("gzip"));
        Assert.assertEquals(true, CompressionUtil.isAlgorithmSupported("zip"));
        Assert.assertEquals(false, CompressionUtil.isAlgorithmSupported("gzip2"));
        //Assert.assertEquals(true, CompressionUtil.isAlgorithmSupported("lzma"));
    }

    @Test
    public void isFileExtensionSupported() throws Exception {
        Assert.assertEquals(true, CompressionUtil.isFileExtensionSupported("gz"));
        Assert.assertEquals(true, CompressionUtil.isFileExtensionSupported("zip"));
        Assert.assertEquals(false, CompressionUtil.isFileExtensionSupported("txt"));
        Assert.assertEquals(false, CompressionUtil.isFileExtensionSupported("gz2"));
    }

    @Test
    public void zipCompressAndUncompress() throws Exception {
        compressAndUncompress("zip");
    }

    @Test
    public void gzipCompressAndUncompress() throws Exception {
        compressAndUncompress("gzip");
    }

    @Ignore
    public void compressAndUncompress(String algorithm) throws Exception {
        //
        // compress a file, rename it, then uncompress it and compare the two files
        //

        // test file to use for tests
        File originalSourceFile = new File(TEST_FILE);

        // copy original source to build directory -- becomes our actual source file
        File sourceFile = new File("target", originalSourceFile.getName());
        FileUtil.copy(originalSourceFile, sourceFile, true);

        // NOTE: just for junit, let's make sure the compressed file does not exist yet
        // it may exist if "ant clean" not run between tests
        Algorithm a = Algorithm.findByName(algorithm);
        File expectedCompressedFile = new File(sourceFile.getParentFile(), sourceFile.getName() + "." + a.getFileExtension());
        expectedCompressedFile.delete();

        // compress the source file (original should have been deleted)
        File compressedFile = CompressionUtil.compress(sourceFile, algorithm, true);

        // uncompress this file
        File newUncompressedFile = CompressionUtil.uncompress(compressedFile, true);

        // compare the two files
        boolean result = FileUtil.equals(originalSourceFile, newUncompressedFile);
        Assert.assertTrue(result);
    }

    @Test
    public void compressNoOverwrite() throws Exception {
        // test file to use for tests
        File originalSourceFile = new File(TEST_FILE);

        // copy original source to build directory -- becomes our actual source file
        File sourceFile = new File("target", originalSourceFile.getName());
        FileUtil.copy(originalSourceFile, sourceFile, true);

        // NOTE: just for junit, let's make sure the compressed file does not exist yet
        // it may exist if "ant clean" not run between tests
        Algorithm a = Algorithm.findByName("gzip");
        File expectedCompressedFile = new File(sourceFile.getParentFile(), sourceFile.getName() + "." + a.getFileExtension());
        expectedCompressedFile.delete();

        // compress the source file (original should have been deleted)
        CompressionUtil.compress(sourceFile, a.getName(), false);

        try {
            CompressionUtil.compress(sourceFile, a.getName(), false);
            Assert.fail("Should have failed");
        } catch (FileAlreadyExistsException e) {
            // correct behavior
        }
    }

}
