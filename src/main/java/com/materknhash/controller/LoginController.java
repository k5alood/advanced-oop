package com.materknhash.controller;

import com.materknhash.model.User;
import com.materknhash.service.AuthService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for the Login Screen.
 * Handles user input and triggers authentication.
 */
public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<User.UserRole> roleComboBox;
    @FXML
    private Label errorLabel;
    @FXML
    private ImageView logoView;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList(User.UserRole.values()));
        roleComboBox.setValue(User.UserRole.ADMIN);

        new Thread(() -> {
            try (java.sql.Connection conn = com.materknhash.util.DatabaseConnection.getConnection();
                    java.sql.PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT IGNORE INTO users (username, password, role) VALUES ('employee', '123456', 'EMPLOYEE')")) {
                pstmt.executeUpdate();
            } catch (Exception e) {
            }
        }).start();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        User.UserRole role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            showError("Please fill all fields.");
            return;
        }

        boolean success = authService.login(username, password, role);

        if (success) {
            System.out.println("Login Successful! Welcome " + username);
            navigateToDashboard();
        } else {
            showError("Invalid username, password, or role.");
        }
    }

    private void navigateToDashboard() {
        try {
            java.net.URL resource = getClass().getResource("/com/materknhash/view/Dashboard.fxml");
            if (resource == null) {
                showError("Dashboard.fxml not found at /com/materknhash/view/Dashboard.fxml");
                return;
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(resource);
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) usernameField.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            stage.setScene(scene);
            stage.setTitle("Materknhash ERP - Dashboard");
            stage.setResizable(true);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Surface the real root cause message (Claude's fix)
            Throwable rootCause = e;
            while (rootCause.getCause() != null)
                rootCause = rootCause.getCause();
            String msg = rootCause.getMessage() != null ? rootCause.getMessage() : rootCause.getClass().getSimpleName();
            showError("Error loading Dashboard: " + msg);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    // Future expansion: Transition logic
    // private void transitionToDashboard() { ... }
}
