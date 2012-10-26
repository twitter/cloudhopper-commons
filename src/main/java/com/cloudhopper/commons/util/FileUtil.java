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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Utility class for handling Files.
 *
 * NOTE: Some code copied from the JXL project.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class FileUtil {

    private FileUtil() {
        // do nothing
    }

	private static boolean equals(InputStream is1, InputStream is2) throws IOException {
        int BUFFSIZE = 1024;
        byte buf1[] = new byte[BUFFSIZE];
        byte buf2[] = new byte[BUFFSIZE];

		if (is1 == is2) {
            return true;
        }
		if (is1 == null && is2 == null) {
            return true;
        }
		if (is1 == null || is2 == null) {
            return false;
        }
        
        int read1 = -1;
        int read2 = -1;

        do {
            int offset1 = 0;
            while (offset1 < BUFFSIZE && (read1 = is1.read(buf1, offset1, BUFFSIZE-offset1)) >= 0) {
                offset1 += read1;
            }

            int offset2 = 0;
            while (offset2 < BUFFSIZE && (read2 = is2.read(buf2, offset2, BUFFSIZE-offset2)) >= 0) {
                offset2 += read2;
            }

            if (offset1 != offset2) {
                return false;
            }

            if (offset1 != BUFFSIZE) {
                Arrays.fill(buf1, offset1, BUFFSIZE, (byte)0);
                Arrays.fill(buf2, offset2, BUFFSIZE, (byte)0);
            }

            if (!Arrays.equals(buf1, buf2)) {
                return false;
            }

        } while (read1 >= 0 && read2 >= 0);

        if (read1 < 0 && read2 < 0) {
            return true;	// both at EOF
        }

        return false;
	}

    /**
     * Tests whether the contents of two files equals each other by performing
     * a byte-by-byte comparison.  Each byte must match each other in both files.
     * @param file1 The file to compare
     * @param file2 The other file to compare
     * @return True if file contents are equal, otherwise false.
     * @throws IOException Thrown if there is an underlying IO error while
     *      attempt to compare the bytes.
     */
	public static boolean equals(File file1, File file2) throws IOException {
		// file lengths must match
        if (file1.length() != file2.length()) {
            return false;
        }

        InputStream is1 = null;
		InputStream is2 = null;

		try {
			is1 = new FileInputStream(file1);
			is2 = new FileInputStream(file2);
			return equals(is1, is2);
		} finally {
            // make sure input streams are closed
            if (is1 != null) {
                try { is1.close(); } catch (Exception e) { }
            }
            if (is2 != null) {
                try { is2.close(); } catch (Exception e) { }
            }
		}
	}


    /**
     * Checks if the extension is valid.  This method only permits letters, digits,
     * and an underscore character.
     * @param extension The file extension to validate
     * @return True if its valid, otherwise false
     */
    public static boolean isValidFileExtension(String extension) {
        for (int i = 0; i < extension.length(); i++) {
            char c = extension.charAt(i);
            if (!(Character.isDigit(c) || Character.isLetter(c) || c == '_')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Parse the filename and return the file extension.  For example, if the
     * file is "app.2006-10-10.log", then this method will return "log".  Will
     * only return the last file extension.  For example, if the filename ends
     * with ".log.gz", then this method will return "gz"
     * @param String to process containing the filename
     * @return The file extension (without leading period) such as "gz" or "txt"
     *      or null if none exists.
     */
    public static String parseFileExtension(String filename) {
        // if null, return null
        if (filename == null) {
            return null;
        }
        // find position of last period
        int pos = filename.lastIndexOf('.');
        // did one exist or have any length?
        if (pos < 0 || (pos+1) >= filename.length()) {
            return null;
        }
        // parse extension
        return filename.substring(pos+1);
    }
    

    /**
     * Finds all files (non-recursively) in a directory based on the FileFilter.
     * This method is slightly different than the JDK File.listFiles() version
     * since it throws an error if the directory does not exist or is not a
     * directory.  Also, this method only finds files and will skip including
     * directories.
     */
    public static File[] findFiles(File dir, FileFilter filter) throws FileNotFoundException {
        if (!dir.exists()) {
            throw new FileNotFoundException("Directory " + dir + " does not exist.");
        }

        if (!dir.isDirectory()) {
            throw new FileNotFoundException("File " + dir + " is not a directory.");
        }

        // being matching process, create array for returning results
        ArrayList<File> files = new ArrayList<File>();

        // get all files in this directory
        File[] allFiles = dir.listFiles();

        // were any files returned?
        if (allFiles != null && allFiles.length > 0) {
            // loop thru every file in the dir
            for (File f : allFiles) {
                // only match files, not a directory
                if (f.isFile()) {
                    // delegate matching to provided file matcher
                    if (filter.accept(f)) {
                        files.add(f);
                    }
                }
            }
        }

        // based on filesystem, order of files not guaranteed -- sort now
        File[] r = files.toArray(new File[0]);
        Arrays.sort(r);
        return r;
    }


    /**
     * Get all of the files (not dirs) under <CODE>dir</CODE>
     * @param dir Directory to search.
     * @return all the files under <CODE>dir</CODE>
     */
    /**
    public static Set<File> getRecursiveFiles(File dir) throws IOException {
        if (!dir.isDirectory()) {
            HashSet<File> one = new HashSet<File>();
            one.add(dir);
            return one;
        } else {
            Set<File> ret = recurseDir(dir);
            return ret;
        }
    }

    private static Set<File> recurseDir(File dir) throws IOException {
        HashSet<File> c = new HashSet<File>();
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                c.addAll(recurseDir(files[i]));
            } else {
                c.add(files[i]);
            }
        }
        return c;
    }

     */

    /**
    public static boolean rmdir(File dir, boolean recursive) throws IOException {
        // make sure this is a directory
        if (!dir.isDirectory()) {
            throw new IOException("File " + dir + " is not a directory");
        }

        File[] files = dir.listFiles();

        // are there files?
        if (files != null && files.length > 0 && !recursive) {
            throw new IOException("Directory " + dir + " is not empty, cannot be deleted");
        }

        for (File file : files) {
            if (file.isDirectory()) {
                rmdir(file);
            } else {
                file.delete();
            }
        }

        // finally remove this directory
        return dir.delete();
    }

    private static boolean rmdir(File dir) throws IOException {
        // make sure this is a directory
        if (!dir.isDirectory()) {
            throw new IOException("File " + dir + " is not a directory");
        }

        File[] files = dir.listFiles();

        // are there files?
        if (files != null && files.length > 0 && !recursive) {
            throw new IOException("Directory " + dir + " is not empty, cannot be deleted");
        }

        boolean success = true;

        for (File file : files) {
            if (file.isDirectory()) {
                success |= rmdir(file);
            } else {
                success |= file.delete();
            }
        }

        // was the recursive part okay?

        // finally remove this directory
        return dir.delete();
    }
     */
    

    /**
     * Copy dest.length bytes from the inputstream into the dest bytearray.
     * @param is
     * @param dest
     * @throws IOException
     */
    /**
    public static void copy(InputStream is, byte[] dest) throws IOException {
        int len = dest.length;
        int ofs = 0;
        while (len > 0) {
            int size = is.read(dest, ofs, len);
            ofs += size;
            len -= size;
        }
    }
     */

    /**
     * Copy the source file to the target file.
     * @param sourceFile The source file to copy from
     * @param targetFile The target file to copy to
     * @throws FileAlreadyExistsException Thrown if the target file already
     *      exists.  This exception is a subclass of IOException, so catching
     *      an IOException is enough if you don't care about this specific reason.
     * @throws IOException Thrown if an error during the copy
     */
    public static void copy(File sourceFile, File targetFile) throws FileAlreadyExistsException, IOException {
        copy(sourceFile, targetFile, false);
    }

    /**
     * Copy the source file to the target file while optionally permitting an
     * overwrite to occur in case the target file already exists.
     * @param sourceFile The source file to copy from
     * @param targetFile The target file to copy to
     * @return True if an overwrite occurred, otherwise false.
     * @throws FileAlreadyExistsException Thrown if the target file already
     *      exists and an overwrite is not permitted.  This exception is a
     *      subclass of IOException, so catching an IOException is enough if you
     *      don't care about this specific reason.
     * @throws IOException Thrown if an error during the copy
     */
    public static boolean copy(File sourceFile, File targetFile, boolean overwrite) throws FileAlreadyExistsException, IOException {
        boolean overwriteOccurred = false;

        // check if the targetFile already exists
        if (targetFile.exists()) {
            // if overwrite is not allowed, throw an exception
            if (!overwrite) {
                throw new FileAlreadyExistsException("Target file " + targetFile + " already exists");
            } else {
                // set the flag that it occurred
                overwriteOccurred = true;
            }
        }

        // proceed with copy
        FileInputStream fis = new FileInputStream(sourceFile);
        FileOutputStream fos = new FileOutputStream(targetFile);
        fis.getChannel().transferTo(0, sourceFile.length(), fos.getChannel());
        fis.close();
        fos.flush();
        fos.close();

        return overwriteOccurred;
    }

    /**
    public static boolean copy(Set<File> sources, File toDir) throws IOException {
        boolean completeSuccess = true;
        int index = 0;
        for (File source : sources) {
            File target = new File(toDir, source.getName());
            try {
                copy(source, target);
            } catch (IOException ioe) {
                completeSuccess = false;
                ioe.printStackTrace();
            }
            index++;
        }
        return completeSuccess;
    }
     */

    /**
     * Copy the contents of is to os.
     * @param is
     * @param os
     * @param buf Can be null
     * @param close If true, is is closed after the copy.
     * @throws IOException
     */
    /**
    public static final void copy(InputStream is, OutputStream os, byte[] buf, boolean close) throws IOException {
        int len;
        if (buf == null) {
            buf = new byte[4096];
        }
        while ((len = is.read(buf)) > 0) {
            os.write(buf, 0, len);
        }
        os.flush();
        if (close) {
            is.close();
        }
    }


    public static void flush(byte[] data, File toFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(toFile);
        fos.write(data);
        fos.flush();
        fos.close();
    }
     */

    /**
     * Read <CODE>f</CODE> and return as byte[]
     * @param f
     * @throws IOException
     * @return bytes from <CODE>f</CODE>
     */
    /**
    public static final byte[] load(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        return load(fis, true);
    }
     */

    /**
     * Copy the contents of is to the returned byte array.
     * @param is
     * @param close If true, is is closed after the copy.
     * @throws IOException
     */
    /**
    public static final byte[] load(InputStream is, boolean close) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        copy(is, os, null, close);
        return os.toByteArray();
    }
     */

    /**
     * Class to compare Files by their embedded DateTimes.
     */
    public static class FileNameDateTimeComparator implements Comparator<File> {

        private String pattern;
        private DateTimeZone zone;

        /**
         * Creates a default instance where the pattern is "yyyy-MM-dd" and the
         * default timezone of UTC.
         */
        public FileNameDateTimeComparator() {
            this(null, null);
        }

        public FileNameDateTimeComparator(String pattern, DateTimeZone zone) {
            if (pattern == null) {
                this.pattern = "yyyy-MM-dd";
            } else {
                this.pattern = pattern;
            }
            if (zone == null) {
                this.zone = DateTimeZone.UTC;
            } else {
                this.zone = zone;
            }
        }

        public int compare(File f1, File f2) {
            // extract datetimes from both files
            DateTime dt1 = DateTimeUtil.parseEmbedded(f1.getName(), pattern, zone);
            DateTime dt2 = DateTimeUtil.parseEmbedded(f2.getName(), pattern, zone);
            // compare these two
            return dt1.compareTo(dt2);
        }
    }

}
