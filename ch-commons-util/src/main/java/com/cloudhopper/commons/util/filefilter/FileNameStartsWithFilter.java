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

/**
 * Accepts a file based on whether its filename startsWith a specific string.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class FileNameStartsWithFilter implements FileFilter {

    private boolean caseSensitive;
    private String string0;

    public FileNameStartsWithFilter(String string0) {
        this(string0, false);
    }

    public FileNameStartsWithFilter(String string0, boolean caseSensitive) throws IllegalArgumentException {
        this.caseSensitive = caseSensitive;
        this.string0 = string0;
    }

    /**
     * Accepts a File if its filename startsWith a specific string.
     * @param file The file to match
     * @return True if the File startsWith the specific string, otherwise false
     */
    public boolean accept(File file) {
        if (caseSensitive) {
            return file.getName().startsWith(string0);
        } else {
            return file.getName().toLowerCase().startsWith(string0.toLowerCase());
        }
    }

}
