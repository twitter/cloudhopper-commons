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

// java imports
import java.util.Random;

/**
 * Utility class for using and generating random object such as strings. Useful
 * for generating things like random passwords, verification codes, etc.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class RandomUtil {

    private static final String chars = "abcdefghijklmonpqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static Random random = new Random();

    /**
     * Creates a random string using only lowercase ascii letters, uppercase
     * ascii letters, and digits.  This method internally uses a static instance
     * of a Random() object.
     * @param length The length of random string to generate
     * @return A new random string with a length specified in the length parameter
     */
    public static String generateString(int length) {
        char[] buf = new char[length];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = chars.charAt(random.nextInt(chars.length()));
        }
        return new String(buf);
    }

}