
package com.cloudhopper.commons.sql;

// third party imports
import org.junit.*;
import org.apache.log4j.Logger;

// my imports

public class DataSourceManagerTest {

    private static final Logger logger = Logger.getLogger(DataSourceManagerTest.class);

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
