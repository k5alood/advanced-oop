package com.materknhash.model;

import java.sql.Timestamp;

/**
 * Model representing a User in the system.
 * Demonstrates Encapsulation (private fields, public getters/setters).
 */
public class User {
    private int id;
    private String username;
    private String password;
    private UserRole role;
    private Timestamp createdAt;

    // Enum for User Roles
    public enum UserRole {
        ADMIN, EMPLOYEE, SELLER
    }

    public User() {}

    public User(int id, String username, String password, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" + "username='" + username + "', role=" + role + '}';
    }
}
