package com.vis.controller;

import com.vis.model.CustomerDAO;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;

/**
 * LoginController - MVC Controller for Login.fxml
 */
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;
    @FXML private Button        loginButton;

    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    public void initialize() {
        // DropShadow effect on login button
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#0ea5e9"));
        shadow.setRadius(12);
        shadow.setSpread(0.4);
        loginButton.setEffect(shadow);

        // FadeTransition - continuous fade in/out on login button
        FadeTransition fade = new FadeTransition(Duration.seconds(1.4), loginButton);
        fade.setFromValue(1.0);
        fade.setToValue(0.55);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();

        // Allow Enter key to trigger login
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            boolean valid = customerDAO.validateLogin(username, password);
            if (valid) {
                String role = customerDAO.getUserRole(username);
                openDashboard(username, role);
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        } catch (SQLException e) {
            // Demo mode: allow login without DB
            System.err.println("DB not available, demo mode: " + e.getMessage());
            openDashboard(username, "ADMIN");
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    private void openDashboard(String username, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/vis/fxml/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dc = loader.getController();
            dc.initData(username, role);

            Scene scene = new Scene(root, 700, 500);
            scene.getStylesheets().add(
                    getClass().getResource("/com/vis/css/styles.css").toExternalForm());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setTitle("VIS - Dashboard [" + role + "]");
        } catch (Exception e) {
            errorLabel.setText("Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
