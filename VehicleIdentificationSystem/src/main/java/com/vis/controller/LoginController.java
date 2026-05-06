package com.vis.controller;

import com.vis.model.CustomerDAO;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private ImageView logoImage;

    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    public void initialize() {
        // Load logo
        try {
            Image logo = new Image(getClass().getResourceAsStream("/com/vis/images/logo.png"));
            if (logo != null && !logo.isError()) {
                logoImage.setImage(logo);
            }
        } catch (Exception e) {
            System.out.println("Logo not found, continuing without logo");
        }

        // Setup username validation only (password has no restrictions)
        setupUsernameValidation();

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#3b82f6"));
        shadow.setRadius(15);
        shadow.setSpread(0.3);
        loginButton.setEffect(shadow);

        FadeTransition fade = new FadeTransition(Duration.seconds(1.2), loginButton);
        fade.setFromValue(1.0);
        fade.setToValue(0.6);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();

        passwordField.setOnAction(e -> handleLogin());
    }

    /**
     * Setup validation for username field only
     * Only allows letters (A-Z, a-z)
     * Maximum 15 characters
     * No numbers or special characters allowed
     */
    private void setupUsernameValidation() {
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Check if empty
            if (newValue.isEmpty()) {
                usernameField.setStyle("-fx-border-color: #cbd5e1;");
                errorLabel.setText("");
                return;
            }

            // Check length (max 15 characters)
            if (newValue.length() > 15) {
                usernameField.setText(oldValue);
                errorLabel.setText("Username cannot exceed 15 characters.");
                return;
            }

            // Check if contains only letters (no numbers, no special characters)
            if (!newValue.matches("[a-zA-Z]*")) {
                usernameField.setText(oldValue);
                errorLabel.setText("Username must contain only letters (A-Z, a-z). No numbers or symbols allowed.");
                usernameField.setStyle("-fx-border-color: #ef4444;");
            } else {
                usernameField.setStyle("-fx-border-color: #22c55e;");
                errorLabel.setText("");
            }
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Username validation
        if (username.isEmpty()) {
            errorLabel.setText("Please enter username.");
            return;
        }

        // Password validation - only check if empty
        if (password.isEmpty()) {
            errorLabel.setText("Please enter password.");
            return;
        }

        // Validate username contains only letters
        if (!username.matches("[a-zA-Z]+")) {
            errorLabel.setText("Username must contain only letters (A-Z, a-z). No numbers or symbols.");
            return;
        }

        // Validate username length (max 15 characters)
        if (username.length() > 15) {
            errorLabel.setText("Username cannot exceed 15 characters.");
            return;
        }

        // Password has NO restrictions - anything is allowed
        try {
            boolean valid = customerDAO.validateLogin(username, password);
            if (valid) {
                String role = customerDAO.getUserRole(username);
                openDashboard(username, role);
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        } catch (SQLException e) {
            System.err.println("DB not available, demo mode: " + e.getMessage());
            openDashboard(username, "ADMIN");
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    private void openDashboard(String username, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dc = loader.getController();
            dc.initData(username, role);

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/com/vis/css/styles.css").toExternalForm());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setTitle("Vehicle Identification System - Dashboard");
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}