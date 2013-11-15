package com.cloudhopper.commons.sql;

/*
 * #%L
 * ch-commons-sql
 * %%
 * Copyright (C) 2012 - 2013 Cloudhopper by Twitter
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

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceManagerTest {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceManagerTest.class);

    @Test
    public void createFailsWithMissingDefaultProperties() throws Exception {
        DataSourceConfiguration config = new DataSourceConfiguration();

        try {
            DataSourceManager.create(config);
            Assert.fail("create() should have failed");
        } catch (SQLConfigurationException e) {
            // correct
        }

        config.setUsername("test");
        try {
            DataSourceManager.create(config);
            Assert.fail("create() should have failed");
        } catch (SQLConfigurationException e) {
            // correct
        }

        config.setPassword("test");
        try {
            DataSourceManager.create(config);
            Assert.fail("create() should have failed");
        } catch (SQLConfigurationException e) {
            // correct
        }

        config.setName("test");
        try {
            DataSourceManager.create(config);
            Assert.fail("create() should have failed");
        } catch (SQLConfigurationException e) {
            // correct
        }

        // last required property, now it should succeed
        config.setUrl("jdbc:mysql://localhost:3306/stratus001");
    }

}
