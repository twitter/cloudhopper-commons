package com.cloudhopper.commons.util.filefilter;

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
import java.io.File;
import java.io.FileFilter;

// my imports
import com.cloudhopper.commons.util.FileUtil;

/**
 * Accepts a file based on its file extension against a list of one or more
 * acceptable file extensions.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class FileExtensionFilter implements FileFilter {

    private boolean caseSensitive;
    private String[] extensions;

    /**
     * Creates a new instance of a <code>FileExtensionMatcher</code> that will
     * perform a case insensitive match to this file extension.
     * @param extension The file extension to match against a File
     * @throws IllegalArgumentException Thrown if the file extension is not
     *      formatted correctly such as containing a period.
     */
    public FileExtensionFilter(String extension) throws IllegalArgumentException {
        this(extension, false);
    }

    /**
     * Creates a new instance of a <code>FileExtensionMatcher</code> that will
     * perform a case insensitive match to this array of file extensions.
     * @param extension The array of file extensions to match against a File
     * @throws IllegalArgumentException Thrown if the file extension is not
     *      formatted correctly such as containing a period.
     */
    public FileExtensionFilter(String[] extensions) throws IllegalArgumentException {
        this(extensions, false);
    }

    /**
     * Creates a new instance of a <code>FileExtensionMatcher</code> that will
     * perform a match to this file extension.
     * @param extension The file extension to match against a File
     * @param caseSensitive If true the extension must match the case of the
     *      provided extension, otherwise a case insensitive match will occur.
     * @throws IllegalArgumentException Thrown if the file extension is not
     *      formatted correctly such as containing a period.
     */
    public FileExtensionFilter(String extension, boolean caseSensitive) throws IllegalArgumentException {
        this(new String[] { extension }, caseSensitive);
    }

    /**
     * Creates a new instance of a <code>FileExtensionMatcher</code> that will
     * perform a match to this array of file extensions.
     * @param extension The array of file extensions to match against a File
     * @param caseSensitive If true the extension must match the case of the
     *      provided extension, otherwise a case insensitive match will occur.
     * @throws IllegalArgumentException Thrown if the file extension is not
     *      formatted correctly such as containing a period.
     */
    public FileExtensionFilter(String[] extensions, boolean caseSensitive) throws IllegalArgumentException {
        // check each extension
        for (String ext : extensions) {
            if (!FileUtil.isValidFileExtension(ext)) {
                throw new IllegalArgumentException("Invalid file extension '" + ext + "' cannot be matched");
            }
        }
        this.caseSensitive = caseSensitive;
        this.extensions = extensions;
    }

    /**
     * Accepts a File by its file extension.
     * @param file The file to match
     * @return True if the File matches this the array of acceptable file
     *      extensions.
     */
    public boolean accept(File file) {
        // extract this file's extension
        String fileExt = FileUtil.parseFileExtension(file.getName());

        // a file extension might not have existed
        if (fileExt == null) {
            // if no file extension extracted, this definitely is not a match
            return false;
        }

        // does it match our list of acceptable file extensions?
        for (String extension : extensions) {
            if (caseSensitive) {
                if (fileExt.equals(extension)) {
                    return true;
                }
            } else {
                if (fileExt.equalsIgnoreCase(extension)) {
                    return true;
                }
            }
        }
        
        // if we got here, then no match was found
        return false;
    }

}
