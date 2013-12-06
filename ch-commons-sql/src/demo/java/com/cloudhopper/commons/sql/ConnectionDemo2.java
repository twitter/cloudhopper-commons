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

import com.cloudhopper.commons.xbean.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joelauer
 */
public class ConnectionDemo2 {

    private static Logger logger = LoggerFactory.getLogger(ConnectionDemo2.class);

    public static void main(String[] args) throws Exception {

        String xml = new StringBuilder(200)
            .append("<configuration>")
            .append(" <datasource>")
            .append("  <name>main</name>")
            
            .append("  <provider>BASIC</provider>")
            //.append("  <provider>C3P0</provider>")
            //.append("  <provider>PROXOOL</provider>")

            .append("  <debug>true</debug>")
            .append("  <jmx>true</jmx>")
            .append("  <jmxDomain>com.cloudhopper.stratus</jmxDomain>")

            .append("  <minPoolSize>1</minPoolSize>")
            .append("  <maxPoolSize>1</maxPoolSize>")
            .append("  <checkoutTimeout>5000</checkoutTimeout>")
            .append("  <activeConnectionTimeout>10000</activeConnectionTimeout>")

            // you can override properties, but it doesn't make much sense to
            //.append("  <vendor>MSSQL</vendor>")
            //.append("  <driver>com.MyDriver</driver>")

            // configure the datasource via url
            .append("  <url>jdbc:mysql://localhost:3306/stratus001?useTimezone=true&amp;useLegacyDatetimeCode=false&amp;serverTimezone=UTC</url>")

            //.append("  <url>jdbc:jtds:sqlserver://localhost/dbname</url>")
            //.append("  <url>jdbc:jtds:sybase://localhost/dbname</url>")
            //.append("  <url>jdbc:unsupported://localhost/dbname</url>")

            // or configure the datasource via properties....

            // can I provide default properties for certain values?

            //.append("  <url>testing</url>")

            .append("  <username>root</username>")
            .append("  <password>test</password>")

            .append(" </datasource>")
            .append("</configuration>")
            .toString();

        XmlBean xbean = new XmlBean();

        DataSourceConfiguration config = new DataSourceConfiguration();
        xbean.configure(xml, config, "/configuration/datasource");

        logger.debug("DataSource " + config.toString());

        DataSource ds = DataSourceManager.create(config);

        // dsFactory.destroyDataSource();

        /**
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl("jdbc:mysql://localhost:3306/stratus001?useTimezone=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        cpds.setUser("root");
        cpds.setPassword("test");

        // the settings below are optional -- c3p0 can work with defaults
        cpds.setMinPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(20);

        DataSource ds = (DataSource)cpds;
         */

        /**
        DataSource ds = null;
         */

        logger.debug("DataSource Class: " + ds.getClass());

        // hmm... proxool doesn't seem to like immediately getting a connection
        // and then checking it out...
        //Thread.sleep(1000);

        logger.debug("Getting first connection....");
        Connection conn = ds.getConnection();

        logger.debug("Getting second connection, should timeout....");
        try {
            Connection conn2 = ds.getConnection();
        } catch (Exception e) {
            logger.error(e);
        }

        System.in.read();

    }

}
