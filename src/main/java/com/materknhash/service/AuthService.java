package com.materknhash.service;

import com.materknhash.dao.UserDAO;
import com.materknhash.model.User;
import com.materknhash.util.PasswordUtil;
import com.materknhash.util.SessionManager;

/**
 * Service class handling authentication logic.
 * Decouples the UI from the Data Access Layer.
 */
public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Authenticates a user against the database.
     * @param username The username input
     * @param password The raw password input
     * @param role The selected role
     * @return true if authentication succeeds, false otherwise
     */
    public boolean login(String username, String password, User.UserRole role) {
        User user = userDAO.getByUsername(username);
        
        if (user != null && user.getRole() == role) {
            // Check password (In this version, we compare directly or via PasswordUtil)
            // Note: init.sql uses 'admin123' as plain text for easy setup.
            // In a real app, always use hashed comparison: PasswordUtil.checkPassword(password, user.getPassword())
            if (user.getPassword().equals(password)) {
                SessionManager.getInstance().setCurrentUser(user);
                return true;
            }
        }
        return false;
    }

    public void logout() {
        SessionManager.getInstance().setCurrentUser(null);
    }
}
