package com.vis.controller;

import com.vis.model.Customer;
import com.vis.model.CustomerDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddCustomerController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private Label errorLabel;
    @FXML private Button saveBtn;

    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    public void initialize() {
        // Setup validations
        setupNameValidation();
        setupPhoneValidation();
        setupEmailValidation();
        setupAddressValidation();
    }

    /**
     * Name Validation: Only letters (A-Z, a-z) and spaces
     * No numbers, no special characters
     */
    private void setupNameValidation() {
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                nameField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
                return;
            }

            // Allow only letters and spaces
            if (!newVal.matches("[a-zA-Z\\s]*")) {
                nameField.setText(oldVal);
                return;
            }

            // Max 50 characters
            if (newVal.length() > 50) {
                nameField.setText(oldVal);
                errorLabel.setText("Name cannot exceed 50 characters.");
                return;
            }

            nameField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Phone Validation: Only numbers, max 15 digits
     * Lesotho format: +266 5XXX XXXX or 5XXX XXXX
     */
    private void setupPhoneValidation() {
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                phoneField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
                return;
            }

            // Allow only numbers, spaces, and plus sign
            if (!newVal.matches("[0-9\\s+]*")) {
                phoneField.setText(oldVal);
                return;
            }

            // Remove spaces for length check
            String digitsOnly = newVal.replaceAll("[\\s+]", "");
            if (digitsOnly.length() > 15) {
                phoneField.setText(oldVal);
                errorLabel.setText("Phone number cannot exceed 15 digits.");
                return;
            }

            phoneField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Email Validation: Must follow email structure
     * Format: username@domain.com
     */
    private void setupEmailValidation() {
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                emailField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
                return;
            }

            // Basic email pattern check
            String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if (newVal.matches(emailPattern)) {
                emailField.setStyle("-fx-border-color: #22c55e;");
                errorLabel.setText("");
            } else {
                emailField.setStyle("-fx-border-color: #ef4444;");
                errorLabel.setText("Invalid email format. Example: name@domain.com");
            }
        });
    }

    /**
     * Address Validation: Letters, numbers, spaces, commas, periods, hyphens
     */
    private void setupAddressValidation() {
        addressField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                addressField.setStyle("-fx-border-color: #334155;");
                return;
            }

            // Allow letters, numbers, spaces, commas, periods, hyphens
            if (!newVal.matches("[a-zA-Z0-9\\s,\\.\\-]*")) {
                addressField.setText(oldVal);
                return;
            }

            if (newVal.length() > 200) {
                addressField.setText(oldVal);
                errorLabel.setText("Address cannot exceed 200 characters.");
                return;
            }

            addressField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    @FXML
    private void handleSave() {
        // Clear previous error
        errorLabel.setText("");

        // Get values
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        // Validate Name
        if (name.isEmpty()) {
            errorLabel.setText("Name is required.");
            nameField.requestFocus();
            return;
        }

        if (!name.matches("[a-zA-Z\\s]+")) {
            errorLabel.setText("Name must contain only letters and spaces.");
            nameField.requestFocus();
            return;
        }

        if (name.length() > 50) {
            errorLabel.setText("Name cannot exceed 50 characters.");
            nameField.requestFocus();
            return;
        }

        // Validate Phone (optional but if provided must be valid)
        if (!phone.isEmpty()) {
            String digitsOnly = phone.replaceAll("[\\s+]", "");
            if (!digitsOnly.matches("\\d+")) {
                errorLabel.setText("Phone number must contain only digits.");
                phoneField.requestFocus();
                return;
            }
            if (digitsOnly.length() > 15) {
                errorLabel.setText("Phone number cannot exceed 15 digits.");
                phoneField.requestFocus();
                return;
            }
        }

        // Validate Email
        if (email.isEmpty()) {
            errorLabel.setText("Email is required.");
            emailField.requestFocus();
            return;
        }

        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailPattern)) {
            errorLabel.setText("Invalid email format. Example: name@domain.com");
            emailField.requestFocus();
            return;
        }

        // Validate Address (optional)
        if (!address.isEmpty() && address.length() > 200) {
            errorLabel.setText("Address cannot exceed 200 characters.");
            addressField.requestFocus();
            return;
        }

        try {
            Customer c = new Customer(0, name, address, phone, email);
            customerDAO.addCustomer(c);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Customer " + name + " added successfully with ID: " + c.getId());
            closeWindow();

        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) saveBtn.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}