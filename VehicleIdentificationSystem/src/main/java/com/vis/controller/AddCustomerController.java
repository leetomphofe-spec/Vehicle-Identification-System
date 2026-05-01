package com.vis.controller;

import com.vis.model.Customer;
import com.vis.model.CustomerDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * AddCustomerController - MVC Controller for AddCustomer.fxml
 */
public class AddCustomerController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private Label     errorLabel;
    @FXML private Button    saveBtn;

    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    private void handleSave() {
        try {
            String name    = nameField.getText().trim();
            String address = addressField.getText().trim();
            String phone   = phoneField.getText().trim();
            String email   = emailField.getText().trim();

            if (name.isEmpty()) {
                errorLabel.setText("Name is required.");
                return;
            }

            Customer c = new Customer(0, name, address, phone, email);
            customerDAO.addCustomer(c);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Customer " + name + " added successfully.");
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
