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

//import SevenZip.LzmaAlone;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for compressing and uncompressing a file.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class CompressionUtil {
    private static final Logger logger = LoggerFactory.getLogger(CompressionUtil.class);

    /**
     * Enumeration of all supported compression algorithms by this utility.
     */
    public enum Algorithm {
        /** gzip */
        GZIP("gzip", "gz", new GzipCompressor()),
        /** poorly supported for now */
        //LZMA("lzma", "7z", new LzmaCompressor()),
        /** zip */
        ZIP("zip", "zip", new ZipCompressor());

        private final String name;
        private final String fileExt;
        private final Compressor compressor;

        Algorithm(final String name, final String fileExt, final Compressor compressor) {
            this.name = name;
            this.fileExt = fileExt;
            this.compressor = compressor;
        }

        /**
         * Gets the name of this compression algorithm such as "gzip".
         * @return The name of this compression algorithm
         */
        public String getName() {
            return this.name;
        }

        /**
         * Gets the extension associated with this compression algorithm such
         * as "gz" for gzip or "zip" for zip.
         * @return The compression algorithm file extension
         */
        public String getFileExtension() {
            return this.fileExt;
        }

        private Compressor getCompressor() {
            return this.compressor;
        }

        /**
         * Finds the matching compression algorithm by performing a case insensitive
         * search based on its name such as "gzip".
         * @param name The algorithm name such as "gzip" or "zip"
         * @return The matching algorithm or null if no algorithm was found
         */
        public static Algorithm findByName(final String name) {
            for (Algorithm e : Algorithm.values()) {
                if (e.name.equalsIgnoreCase(name)) {
                    return e;
                }
            }
            return null;
        }

        /**
         * Finds the matching compression algorithm by performing a case insensitive
         * search based on its file extension such as "gz" for gzip.
         * @param name The algorithm file extension such as "gz" or "zip"
         * @return The matching algorithm or null if no algorithm was found
         */
        public static Algorithm findByFileExtension(final String fileExt) {
            for (Algorithm e : Algorithm.values()) {
                if (e.fileExt.equalsIgnoreCase(fileExt)) {
                    return e;
                }
            }
            return null;
        }
    }


    /**
     * Checks if the compression algorithm is supported by this utility class.
     * For example, "gzip" would return true while "unknown" would
     * return false.
     * @param algorithm The compression algorithm such as "gzip" or "zip"
     * @return True if the algorithm is supported, otherwise false.
     */
    public static boolean isAlgorithmSupported(String algorithm) {
        return (Algorithm.findByName(algorithm) != null);
    }


    /**
     * Checks if the file extension such as "gz" or "zip" is supported by this
     * utility class.  A file extension is supported if a compression algorithm
     * exists.
     * @param fileExt The file extension to check such as "gz" or "zip"
     * @return True if the file extension is supported, otherwise false.
     */
    public static boolean isFileExtensionSupported(String fileExt) {
        return (Algorithm.findByFileExtension(fileExt) != null);
    }
    

    /**
     * Compresses the source file using a variety of supported compression
     * algorithms.  This method will create a target file in the same
     * directory as the source file and will append a file extension matching
     * the compression algorithm used.  For example, using "gzip" will mean a
     * source file of "app.log" would be compressed to "app.log.gz". 
     * @param sourceFile The uncompressed file
     * @param algorithm The compression algorithm to use
     * @param deleteSourceFileAfterCompressed Delete the original source file
     *      only if the compression was successful.
     * @return The compressed file
     * @throws FileAlreadyExistsException Thrown if the target file already
     *      exists and an overwrite is not permitted.  This exception is a
     *      subclass of IOException, so its safe to only catch an IOException
     *      if no specific action is required for this case.
     * @throws IOException Thrown if an error occurs while attempting to
     *      compress the source file.
     */
    public static File compress(File sourceFile, String algorithm, boolean deleteSourceFileAfterCompressed) throws FileAlreadyExistsException, IOException {
        return compress(sourceFile, sourceFile.getParentFile(), algorithm, deleteSourceFileAfterCompressed);
    }
    

    /**
     * Compresses the source file using a variety of supported compression
     * algorithms.  This method will create a destination file in the destination
     * directory and will append a file extension matching the compression
     * algorithm used.  For example, using "gzip" will mean a source file of
     * "app.log" would be compressed to "app.log.gz" and placed in the destination
     * directory.
     * @param sourceFile The uncompressed file
     * @param targetDir The target directory or null if same directory as sourceFile
     * @param algorithm The compression algorithm to use
     * @param deleteSourceFileAfterCompressed Delete the original source file
     *      only if the compression was successful.
     * @return The compressed file
     * @throws FileAlreadyExistsException Thrown if the target file already
     *      exists and an overwrite is not permitted.  This exception is a
     *      subclass of IOException, so its safe to only catch an IOException
     *      if no specific action is required for this case.
     * @throws IOException Thrown if an error occurs while attempting to
     *      compress the source file.
     */
    public static File compress(File sourceFile, File targetDir, String algorithm, boolean deleteSourceFileAfterCompressed) throws FileAlreadyExistsException, IOException {
        // make sure the destination directory is a directory
        if (!targetDir.isDirectory()) {
            throw new IOException("Cannot compress file since target directory " + targetDir + " neither exists or is a directory");
        }

        // try to find compression algorithm
        Algorithm a = Algorithm.findByName(algorithm);

        // was it found?
        if (a == null) {
            throw new IOException("Compression algorithm '" + algorithm + "' is not supported");
        }

        // create a target file with the default file extension for this algorithm
        File targetFile = new File(targetDir, sourceFile.getName() + "." + a.getFileExtension());

        compress(a, sourceFile, targetFile, deleteSourceFileAfterCompressed);

        return targetFile;
    }


    /**
     * Uncompresses the source file using a variety of supported compression
     * algorithms.  This method will create a file in the same directory as
     * the source file by stripping the compression algorithm's file extension from
     * the source filename.  For example, using "gzip" will mean a source file of
     * "app.log.gz" would be uncompressed to "app.log".
     * @param sourceFile The compressed file
     * @param deleteSourceFileAfterUncompressed Delete the original source file
     *      only if the uncompression was successful.
     * @return The uncompressed file
     * @throws FileAlreadyExistsException Thrown if the target file already
     *      exists and an overwrite is not permitted.  This exception is a
     *      subclass of IOException, so its safe to only catch an IOException
     *      if no specific action is required for this case.
     * @throws IOException Thrown if an error occurs while attempting to
     *      uncompress the source file.
     */
    public static File uncompress(File sourceFile, boolean deleteSourceFileAfterUncompressed) throws FileAlreadyExistsException, IOException {
        return uncompress(sourceFile, sourceFile.getParentFile(), deleteSourceFileAfterUncompressed);
    }


    /**
     * Uncompresses the source file using a variety of supported compression
     * algorithms.  This method will create a file in the target
     * directory by stripping the compression algorithm's file extension from
     * the source filename.  For example, using "gzip" will mean a source file of
     * "app.log.gz" would be uncompressed to "app.log" and placed in the target
     * directory.
     * @param sourceFile The compressed file
     * @param targetDir The target directory or null to uncompress in same directory as source file
     * @param deleteSourceFileAfterUncompressed Delete the original source file
     *      only if the uncompression was successful.
     * @return The uncompressed file
     * @throws FileAlreadyExistsException Thrown if the target file already
     *      exists and an overwrite is not permitted.  This exception is a
     *      subclass of IOException, so its safe to only catch an IOException
     *      if no specific action is required for this case.
     * @throws IOException Thrown if an error occurs while attempting to
     *      uncompress the source file.
     */
    public static File uncompress(File sourceFile, File targetDir, boolean deleteSourceFileAfterUncompressed) throws FileAlreadyExistsException, IOException {
        //
        // figure out compression algorithm used by its extension
        //
        String fileExt = FileUtil.parseFileExtension(sourceFile.getName());

        // was a file extension parsed
        if (fileExt == null) {
            throw new IOException("File '" + sourceFile + "' must contain a file extension in order to lookup the compression algorithm");
        }

        // try to find compression algorithm
        Algorithm a = Algorithm.findByFileExtension(fileExt);

        // was it not found?
        if (a == null) {
            throw new IOException("Unrecognized or unsupported compression algorithm for file extension '" + fileExt + "'");
        }

        // make sure the destination directory is a directory
        if (!targetDir.isDirectory()) {
            throw new IOException("Cannot uncompress file since target directory " + targetDir + " neither exists or is a directory");
        }

        //
        // create a target file by stripping the file extension from the original file
        //
        String filename = sourceFile.getName();
        filename = filename.substring(0, filename.length()-(fileExt.length()+1));

        File targetFile = new File(targetDir, filename);

        uncompress(a, sourceFile, targetFile, deleteSourceFileAfterUncompressed);

        return targetFile;
    }


    private static void compress(Algorithm a, File sourceFile, File targetFile, boolean deleteSourceFileAfterCompressed) throws FileAlreadyExistsException, IOException {
        // check if the src file exists
        if (!sourceFile.canRead()) {
            throw new IOException("Source file " + sourceFile + " neither exists or can be read");
        }

        // make sure the target file does not exist!
        if (targetFile.exists()) {
            throw new FileAlreadyExistsException("Target file " + targetFile + " already exists - cannot overwrite!");
        }

        // try to compress the file
        a.getCompressor().compress(sourceFile, targetFile);

        // delete file after compressing
        if (deleteSourceFileAfterCompressed) {
            sourceFile.delete();
        }
    }


    private static void uncompress(Algorithm a, File sourceFile, File targetFile, boolean deleteSourceFileAfterUncompressed) throws FileAlreadyExistsException, IOException {
        // check if the src file exists
        if (!sourceFile.canRead()) {
            throw new IOException("Source file " + sourceFile + " neither exists or can be read");
        }

        // make sure the target file does not exist!
        if (targetFile.exists()) {
            throw new FileAlreadyExistsException("Target file " + targetFile + " already exists - cannot overwrite!");
        }

        // try to uncompress the file
        a.getCompressor().uncompress(sourceFile, targetFile);

        // delete file after uncompressing
        if (deleteSourceFileAfterUncompressed) {
            sourceFile.delete();
        }
    }

    private static void uncompress(Algorithm a, InputStream srcIn, OutputStream destOut) throws IOException {
        // try to uncompress the file
        a.getCompressor().uncompress(srcIn, destOut);
    }


    /**
     * Interface all supported compression algorithms must implement.
     */
    private static interface Compressor {

        public void compress(File srcFile, File destFile) throws IOException;

        public void compress(InputStream srcIn, OutputStream destOut) throws IOException;

        public void uncompress(File srcFile, File destFile) throws IOException;

        public void uncompress(InputStream srcIn, OutputStream destOut) throws IOException;
                
    }


    /**
     * Compressor using the GZIP compression algorithm.
     */
    private static class GzipCompressor implements Compressor {

        @Override
        public void compress(File srcFile, File destFile) throws IOException {
            FileInputStream in = null;
            GZIPOutputStream out = null;

            try {
                // create an input stream from the source file
                in = new FileInputStream(srcFile);
                // create an output stream that's gzipped
                out = new GZIPOutputStream(new FileOutputStream(destFile));

                // transfer bytes from the input file to the GZIP output stream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                // close the input stream
                in.close();
                in = null;

                // finish and close the gzip file
                out.finish();
                out.close();
                out = null;
            } finally {
                // make sure everything is closed
                if (in != null) {
                    try { in.close(); } catch (Exception e) { }
                }
                if (out != null) {
                    logger.warn("Output stream for GZIP compressed file was not null -- indicates error with compression occurred");
                    try { out.close(); } catch (Exception e) { }
                }
            }
        }

        @Override
        public void compress(InputStream srcIn, OutputStream destOut) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void uncompress(File srcFile, File destFile) throws IOException {
            InputStream in = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(destFile);
            uncompress(in, out);
        }

        @Override
        public void uncompress(InputStream srcIn, OutputStream destOut) throws IOException {
            GZIPInputStream in = null;

            try {
                // create an input stream from the source
                in = new GZIPInputStream(srcIn);

                // transfer bytes from the GZIP input file to the output stream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    destOut.write(buf, 0, len);
                }

                // close the GZIP input stream
                in.close();
                in = null;

                // close the output file
                destOut.close();
                destOut = null;
            } finally {
                // make sure everything is closed
                if (in != null) {
                    try { in.close(); } catch (Exception e) { }
                }
                if (destOut != null) {
                    try { destOut.close(); } catch (Exception e) { }
                }
            }
        }

    }


    /**
     * Compressor using the ZIP compression algorithm.
     */
    private static class ZipCompressor implements Compressor {

        @Override
        public void compress(File srcFile, File destFile) throws IOException {
            FileInputStream in = null;
            ZipOutputStream out = null;

            try {
                // create an input stream from the source file
                in = new FileInputStream(srcFile);
                // create an output stream that's gzipped
                out = new ZipOutputStream(new FileOutputStream(destFile));

                // add a new zip entry to the output stream
                out.putNextEntry(new ZipEntry(srcFile.getName()));

                // transfer bytes from the input file to the zip output stream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                // close the input stream
                in.close();
                in = null;

                // finish and close the gzip file
                out.closeEntry();
                out.finish();
                out.close();
                out = null;
            } finally {
                // always make sure the input stream is closed!
                if (in != null) {
                    try { in.close(); } catch (Exception e) { }
                }
                // if the out var is not null, then something went wrong above
                // and we did not compress the destination file correctly
                if (out != null) {
                    logger.warn("Output stream for ZIP compressed file was not null -- indicates error with compression occurred");
                    try { out.close(); } catch (Exception e) { }
                }
            }
        }

        @Override
        public void compress(InputStream srcIn, OutputStream destOut) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void uncompress(File srcFile, File destFile) throws IOException {
            InputStream in = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(destFile);
            uncompress(in, out);
        }

        // NOTE: only reads the first zip file in the archive
        @Override
        public void uncompress(InputStream srcIn, OutputStream destOut) throws IOException {
            ZipInputStream in = null;

            try {
                // create an input stream from the source
                in = new ZipInputStream(srcIn);

                // just get the first entry
                ZipEntry ze = in.getNextEntry();

                // transfer bytes from the GZIP input file to the output stream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    destOut.write(buf, 0, len);
                }

                // see if there are more entries in this zip file
                if (in.getNextEntry() != null) {
                    throw new IOException("Zip file/inputstream contained more than one entry (this method cannot support)");
                }

                // close the zip inputstream
                in.closeEntry();
                in.close();
                in = null;

                // close the output file
                destOut.close();
                destOut = null;
            } finally {
                // make sure everything is closed
                if (in != null) {
                    try { in.close(); } catch (Exception e) { }
                }
                if (destOut != null) {
                    try { destOut.close(); } catch (Exception e) { }
                }
            }
        }


        
    }


    /**
     * Compressor using the LZMA/7-zip compression algorithm.
     */
    /** removed in 6.0.0
    private static class LzmaCompressor implements Compressor {

        @Override
        public void compress(File srcFile, File destFile) throws IOException {
            // FIXME: VERY SIMPLE IMPL -- LIKELY SHOULD TRY TO EMULATE THIS BETTER
            // by pulling code from its main
            try {
                
                // compress using LZMA
                LzmaAlone.main(new String[]{"e", srcFile.getCanonicalPath(), destFile.getCanonicalPath()});
            } catch (Exception ex) {
                logger.error("", ex);
                throw new IOException("LZMA compression failed: " + ex.getMessage());
            }
        }

        @Override
        public void uncompress(File srcFile, File destFile) throws IOException {
            throw new UnsupportedOperationException("LZMA uncompress not supported yet.");
        }

        @Override
        public void uncompress(InputStream srcIn, OutputStream destOut) throws IOException {
            throw new UnsupportedOperationException("LZMA uncompress not supported yet.");
        }

        @Override
        public void compress(InputStream srcIn, OutputStream destOut) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    */
}
