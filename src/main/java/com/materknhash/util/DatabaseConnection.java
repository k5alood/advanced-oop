package com.materknhash.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to manage Database Connection using JDBC.
 * Fulfills the "Database + JDBC is mandatory" requirement.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/materknhash_db";
    private static final String USER = "root";
    private static final String PASSWORD = "kha113le2006d"; // Update as per user's MySQL config

    private static Connection connection = null;

    private DatabaseConnection() {
        // Private constructor for Singleton pattern
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database Connected Successfully!");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL Driver not found: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
