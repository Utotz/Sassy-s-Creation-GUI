package com.sassyscreation.sassyscreationgui.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// =====================================================================
//  LOGIC: THE ONE PLACE THAT OPENS A DATABASE CONNECTION
//  Every DAO calls getConnection(). Local development settings (XAMPP MariaDB on
//  this computer). A new connection per call; callers close it with try-with-resources.
// =====================================================================

public class DBConnection {
        private static final String URL = "jdbc:mariadb://localhost:3306/sassys_creation_db";
        private static final String USER = "root";
        private static final String PASSWORD = "";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }

}
