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

// java imports
import com.cloudhopper.commons.util.EnvironmentException;
import com.cloudhopper.commons.util.EnvironmentUtil;
import java.util.*;

/**
 * Demos EnvironmentUtil class.
 * 
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public class EnvironmentUtilMain {
    
    public static void main(String[] args) throws EnvironmentException {
        Properties systemProperties = EnvironmentUtil.createCommonSystemProperties();
        for (String systemProperty : systemProperties.stringPropertyNames()) {
            String value = systemProperties.getProperty(systemProperty);
            System.out.println(systemProperty + ": " + value);
        }
    }
}
