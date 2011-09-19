/**
 * Copyright (C) 2011 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.cloudhopper.commons.sql;

// java imports
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * Common utilities for woking with JDBC.
 *
 * @author joelauer
 */
public class JdbcUtil {
    private static Logger logger = Logger.getLogger(JdbcUtil.class);

    /**
     * Safely closes resources and logs errors.
     * @param conn Connection to close
     * @param stmt Statement to close
     * @param rs ResultSet to close
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
        close(conn);
    }

    /**
     * Safely closes resources and logs errors.
     * @param stmt Statement to close
     * @param rs ResultSet to close
     */
    public static void close(Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
    }

    /**
     * Safely closes resources and logs errors.
     * @param conn Connection to close
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }
    }

    /**
     * Safely closes resources and logs errors.
     * @param stmt Statement to close
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }
    }

    /**
     * Safely closes resources and logs errors.
     * @param rs ResultSet to close
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                logger.error(ex);
            }
        }
    }
}

