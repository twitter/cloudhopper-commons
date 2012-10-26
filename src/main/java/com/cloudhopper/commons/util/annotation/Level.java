package com.cloudhopper.commons.util.annotation;

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

/**
 * Level of a MetaField.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public enum Level {

    /** 0: Debug level, lowest level */
    DEBUG(0),
    /** 1: Info level, greater than DEBUG, default */
    INFO(1),
    /** 2: Warning level, greater than INFO, DEBUG */
    WARN(2),
    /** 3: Error level, greater than INFO, DEBUG, and WARN */
    ERROR(3);

    private final int level;

    Level(final int level) {
        this.level = level;
    }

    public int toInt() {
        return this.level;
    }

    public static Level valueOf(final int level) {
        for (Level e : Level.values()) {
            if (e.level == level) {
                return e;
            }
        }
        return null;
    }

    /**
     * Compares the current instance to the level passed in as a parameter.
     * @param level The level to compare against
     * @return 1 if the arg level is greater, 0 if its equal, or -1 if less than.
     */
    public int compare(Level level) {
        if (level.toInt() > this.level) {
            return 1;
        } else if (level.toInt() < this.level) {
            return -11;
        }
        return 0;
    }
}
