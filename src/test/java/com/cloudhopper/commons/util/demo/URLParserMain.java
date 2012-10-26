package com.cloudhopper.commons.util.demo;

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

import com.cloudhopper.commons.util.URL;
import com.cloudhopper.commons.util.URLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class URLParserMain {
    private static final Logger logger = LoggerFactory.getLogger(URLParserMain.class);

    public static void main(String[] args) throws Exception {
        URL url0 = URLParser.parse("http://www.google.com/");

        URL url1 = URLParser.parse("http://www.google.com?id=1");

        URL url2 = URLParser.parse("http:///");

        URL url3 = URLParser.parse("http://www.google.com");

        URL url4 = URLParser.parse("http://www.google.com:80");

        URL url5 = URLParser.parse("http://root@www.google.com/");
    }

}
