package com.materknhash.core;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main Entry Point for Materknhash ERP System.
 * Fulfills JavaFX and layered architecture requirements.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/materknhash/view/Login.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            primaryStage.setTitle("Materknhash ERP - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
            System.out.println("Materknhash ERP Login Screen Loaded.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load Login Screen: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
