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

import java.io.File;
import java.io.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import com.cloudhopper.commons.util.DateTimeUtil;

/**
 * Filters a file based on an embedded date within the filename.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class FileNameDateTimeFilter implements FileFilter {
    private static final Logger logger = LoggerFactory.getLogger(FileNameDateTimeFilter.class);

    private String pattern;
    private DateTime cutoffDate;
    private DateTimeZone zone;

    public FileNameDateTimeFilter(DateTime cutoffDate) {
        this(cutoffDate, null, null);
    }

    public FileNameDateTimeFilter(DateTime cutoffDate, DateTimeZone zone) {
        this(cutoffDate, null, zone);
    }

    public FileNameDateTimeFilter(DateTime cutoffDate, String pattern) {
        this(cutoffDate, pattern, null);
    }

    public FileNameDateTimeFilter(DateTime cutoffDate, String pattern, DateTimeZone zone) {
        this.cutoffDate = cutoffDate;
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

    @Override
    public boolean accept(File file) {
        // parse out the date contained within the filename
        DateTime d = null;

        try {
            d = DateTimeUtil.parseEmbedded(file.getName(), pattern, zone);
        } catch (Exception e) {
            // ignore for matching
            return false;
        }

        logger.trace("Filename '" + file.getName() + "' contained an embedded date of " + d);

        // does the cutoff date occurr before or equal
        if (d.isBefore(cutoffDate) || d.isEqual(cutoffDate)) {
            logger.trace("Filename '" + file.getName() + "' embedded date of " + d + " occurred beforeOrEquals " + d.isBefore(cutoffDate));
            return true;
        }

        // if we get here, then the date wasn't right
        logger.trace("Skipping filename '" + file.getName() + "' since its embedded date of " + d + " occurred after " + cutoffDate);
        return false;
    }

}
