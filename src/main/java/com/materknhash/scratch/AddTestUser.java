package com.materknhash.scratch;

import com.materknhash.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddTestUser {
    public static void main(String[] args) {
        String sql = "INSERT INTO users (username, password, role) VALUES ('employee', '123456', 'EMPLOYEE')";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Test User 'employee' added successfully!");
        } catch (Exception e) {
            System.out.println("Error or User already exists: " + e.getMessage());
        }
    }
}
