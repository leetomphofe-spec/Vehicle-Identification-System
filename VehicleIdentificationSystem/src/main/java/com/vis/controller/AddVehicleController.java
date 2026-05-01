package com.vis.controller;

import com.vis.model.Vehicle;
import com.vis.model.VehicleDAO;
import com.vis.model.CustomerDAO;
import com.vis.model.Customer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;

public class AddVehicleController {

    @FXML private TextField regField;
    @FXML private TextField makeField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField ownerIdField;
    @FXML private TextField colorField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField engineField;
    @FXML private TextField transmissionField;
    @FXML private TextField fuelTypeField;
    @FXML private TextField mileageField;
    @FXML private Label errorLabel;
    @FXML private Button saveBtn;

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    public void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList(
                "Active", "Stolen", "Recovered", "Under Repair", "Imported"
        ));
        statusCombo.getSelectionModel().selectFirst();

        // Add listener to validate owner ID as user types
        ownerIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateOwnerId();
        });
    }

    private void validateOwnerId() {
        String ownerIdText = ownerIdField.getText().trim();
        if (ownerIdText.isEmpty()) {
            ownerIdField.setStyle("-fx-border-color: #334155;");
            errorLabel.setText("");
            return;
        }

        try {
            int ownerId = Integer.parseInt(ownerIdText);
            boolean exists = vehicleDAO.customerExists(ownerId);
            if (exists) {
                ownerIdField.setStyle("-fx-border-color: #22c55e;");
                errorLabel.setText("");
            } else {
                ownerIdField.setStyle("-fx-border-color: #ef4444;");
                errorLabel.setText("Customer ID " + ownerId + " does not exist. Please add the customer first.");
            }
        } catch (NumberFormatException e) {
            ownerIdField.setStyle("-fx-border-color: #ef4444;");
            errorLabel.setText("Owner ID must be a number.");
        } catch (SQLException e) {
            ownerIdField.setStyle("-fx-border-color: #eab308;");
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            String reg = regField.getText().trim();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            String yearS = yearField.getText().trim();
            String ownS = ownerIdField.getText().trim();
            String color = colorField != null ? colorField.getText().trim() : "";
            String description = descriptionArea != null ? descriptionArea.getText().trim() : "";
            String status = statusCombo != null ? statusCombo.getValue() : "Active";
            String engine = engineField != null ? engineField.getText().trim() : "";
            String transmission = transmissionField != null ? transmissionField.getText().trim() : "";
            String fuelType = fuelTypeField != null ? fuelTypeField.getText().trim() : "";
            String mileageS = mileageField != null ? mileageField.getText().trim() : "";

            if (reg.isEmpty() || make.isEmpty() || model.isEmpty() || yearS.isEmpty() || ownS.isEmpty()) {
                errorLabel.setText("All required fields (*) are required.");
                return;
            }

            int year = Integer.parseInt(yearS);
            int ownerId = Integer.parseInt(ownS);

            // Validate owner exists before saving
            if (!vehicleDAO.customerExists(ownerId)) {
                errorLabel.setText("Customer ID " + ownerId + " does not exist. Please add the customer first.");
                return;
            }

            int mileage = 0;
            if (!mileageS.isEmpty()) {
                mileage = Integer.parseInt(mileageS);
            }

            Vehicle v = new Vehicle(0, reg, make, model, year, ownerId, description, color, status, "",
                    engine, transmission, fuelType, mileage);
            vehicleDAO.addVehicle(v);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Vehicle " + reg + " added successfully.");
            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("Year, Owner ID, and Mileage must be numbers.");
        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key")) {
                errorLabel.setText("Customer does not exist. Please enter a valid Customer ID.");
            } else {
                errorLabel.setText("Database Error: " + e.getMessage());
            }
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