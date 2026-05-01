package com.vis.controller;

import com.vis.model.Violation;
import com.vis.model.PoliceDAO;
import com.vis.model.Vehicle;
import com.vis.model.VehicleDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddViolationController {

    @FXML private TextField vehicleIdField;
    @FXML private TextField regNumberField;
    @FXML private TextField dateField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField fineField;
    @FXML private TextArea descField;
    @FXML private Label errorLabel;

    private final PoliceDAO policeDAO = new PoliceDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private Runnable onViolationAdded;

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(
                "Speeding", "Running Red Light", "Parking Violation",
                "Driving Without License", "No Insurance", "Drunk Driving",
                "Reckless Driving", "Wrong Way", "Illegal U-Turn", "Expired Registration"
        ));
        typeCombo.getSelectionModel().selectFirst();

        // Auto-fetch vehicle ID when registration number is entered
        regNumberField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                autoFetchVehicleId();
            }
        });
    }

    public void setOnViolationAdded(Runnable onViolationAdded) {
        this.onViolationAdded = onViolationAdded;
    }

    private void autoFetchVehicleId() {
        String regNumber = regNumberField.getText().trim();
        if (regNumber.isEmpty()) return;

        try {
            Vehicle vehicle = vehicleDAO.searchByRegistration(regNumber);
            if (vehicle != null) {
                vehicleIdField.setText(String.valueOf(vehicle.getId()));
                vehicleIdField.setStyle("-fx-border-color: #22c55e;");
            } else {
                vehicleIdField.setText("");
                vehicleIdField.setStyle("-fx-border-color: #ef4444;");
                errorLabel.setText("Vehicle not found with registration: " + regNumber);
            }
        } catch (SQLException e) {
            errorLabel.setText("Error searching vehicle: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            int vehicleId = Integer.parseInt(vehicleIdField.getText().trim());
            String regNumber = regNumberField.getText().trim();
            String date = dateField.getText().trim();
            String type = typeCombo.getValue();
            double fine = Double.parseDouble(fineField.getText().trim());
            String description = descField.getText().trim();

            if (date.isEmpty() || type.isEmpty()) {
                errorLabel.setText("Date and violation type are required.");
                return;
            }

            if (vehicleId <= 0) {
                errorLabel.setText("Valid Vehicle ID is required.");
                return;
            }

            Violation violation = new Violation(0, vehicleId, date, type, fine, "Unpaid", regNumber);
            policeDAO.addViolation(violation);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Violation recorded successfully.");

            if (onViolationAdded != null) {
                onViolationAdded.run();
            }

            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("Vehicle ID and Fine must be numbers.");
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) (errorLabel.getScene().getWindow());
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}