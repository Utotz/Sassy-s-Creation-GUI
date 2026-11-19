package com.sassyscreation.sassyscreationgui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {

        String url =
                "jdbc:mariadb://localhost:3306/sassys_creation_db";

        String username = "root";
        String password = ""; // Only if your local account has no password

        try (Connection connection =
                     DriverManager.getConnection(url, username, password)) {

            System.out.println("Database connected successfully!");

        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }
    }
}
