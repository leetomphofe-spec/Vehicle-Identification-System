package com.vis.controller;

import com.vis.model.ServiceRecord;
import com.vis.model.WorkshopDAO;
import com.vis.model.Vehicle;
import com.vis.model.VehicleDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddServiceController {

    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private TextField vehicleIdField;
    @FXML private TextField regNumberField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextArea descField;
    @FXML private TextField costField;
    @FXML private Label errorLabel;

    private final WorkshopDAO workshopDAO = new WorkshopDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML
    public void initialize() {
        // Setup service types dropdown
        typeCombo.setItems(FXCollections.observableArrayList(
                "Oil Change", "Full Service", "Brake Service", "Tire Rotation",
                "Engine Tune-up", "Transmission Service", "Battery Replacement",
                "Air Conditioning Service", "Wheel Alignment", "Timing Belt Replacement",
                "Coolant Flush", "Fuel System Cleaning", "Inspection"
        ));
        typeCombo.getSelectionModel().selectFirst();

        // Set today's date as default
        datePicker.setValue(LocalDate.now());
        datePicker.setPromptText("YYYY-MM-DD");

        // Load vehicles into dropdown
        loadVehiclesIntoCombo();

        // Add listener for vehicle selection
        vehicleCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateVehicleDetails(newVal);
            }
        });

        // Setup cost validation - only numbers and decimal point
        setupCostValidation();

        // Setup description validation - prevent invalid characters
        setupDescriptionValidation();
    }

    private void loadVehiclesIntoCombo() {
        try {
            ObservableList<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            vehicleCombo.setItems(vehicles);

            vehicleCombo.setCellFactory(param -> new ListCell<Vehicle>() {
                @Override
                protected void updateItem(Vehicle vehicle, boolean empty) {
                    super.updateItem(vehicle, empty);
                    if (empty || vehicle == null) {
                        setText(null);
                    } else {
                        setText(vehicle.getRegistrationNumber() + " - " + vehicle.getMake() + " " + vehicle.getModel());
                    }
                }
            });

            vehicleCombo.setButtonCell(new ListCell<Vehicle>() {
                @Override
                protected void updateItem(Vehicle vehicle, boolean empty) {
                    super.updateItem(vehicle, empty);
                    if (empty || vehicle == null) {
                        setText("Select a vehicle...");
                    } else {
                        setText(vehicle.getRegistrationNumber() + " - " + vehicle.getMake() + " " + vehicle.getModel());
                    }
                }
            });

        } catch (SQLException e) {
            errorLabel.setText("Error loading vehicles: " + e.getMessage());
        }
    }

    private void populateVehicleDetails(Vehicle vehicle) {
        if (vehicle != null) {
            vehicleIdField.setText(String.valueOf(vehicle.getId()));
            regNumberField.setText(vehicle.getRegistrationNumber());
            vehicleIdField.setStyle("-fx-border-color: #22c55e;");
            regNumberField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        }
    }

    /**
     * Cost Validation: Only numbers and decimal point
     * Prevents letters and invalid characters
     */
    private void setupCostValidation() {
        costField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                costField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
                return;
            }

            // Allow only numbers, decimal point, and one optional decimal point
            if (!newVal.matches("\\d*\\.?\\d*")) {
                costField.setText(oldVal);
                return;
            }

            // Check if multiple decimal points
            if (newVal.indexOf('.') != newVal.lastIndexOf('.')) {
                costField.setText(oldVal);
                return;
            }

            // Check length (max 10 characters total)
            if (newVal.length() > 12) {
                costField.setText(oldVal);
                errorLabel.setText("Cost cannot exceed 12 characters.");
                return;
            }

            // Validate that after decimal point there are max 2 digits
            if (newVal.contains(".")) {
                String[] parts = newVal.split("\\.");
                if (parts.length > 1 && parts[1].length() > 2) {
                    costField.setText(oldVal);
                    errorLabel.setText("Cost can have at most 2 decimal places.");
                    return;
                }
            }

            costField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Description Validation: Letters, numbers, spaces, basic punctuation
     * Prevents special characters that might cause issues
     */
    private void setupDescriptionValidation() {
        descField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                descField.setStyle("-fx-border-color: #334155;");
                return;
            }

            // Allow letters, numbers, spaces, commas, periods, hyphens, apostrophes
            if (!newVal.matches("[a-zA-Z0-9\\s,\\.\\-']*")) {
                descField.setText(oldVal);
                return;
            }

            if (newVal.length() > 500) {
                descField.setText(oldVal);
                errorLabel.setText("Description cannot exceed 500 characters.");
                return;
            }

            descField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    @FXML
    private void handleSave() {
        // Validate vehicle selection
        Vehicle selectedVehicle = vehicleCombo.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            errorLabel.setText("Please select a vehicle.");
            vehicleCombo.requestFocus();
            return;
        }

        int vehicleId = selectedVehicle.getId();

        // Validate service date
        LocalDate serviceDate = datePicker.getValue();
        if (serviceDate == null) {
            errorLabel.setText("Please select a service date.");
            datePicker.requestFocus();
            return;
        }

        // Validate date is not in the future
        if (serviceDate.isAfter(LocalDate.now())) {
            errorLabel.setText("Service date cannot be in the future.");
            datePicker.requestFocus();
            return;
        }

        String date = serviceDate.toString();

        // Validate service type
        String type = typeCombo.getValue();
        if (type == null || type.isEmpty()) {
            errorLabel.setText("Please select a service type.");
            typeCombo.requestFocus();
            return;
        }

        // Validate cost
        String costText = costField.getText().trim();
        if (costText.isEmpty()) {
            errorLabel.setText("Please enter the cost.");
            costField.requestFocus();
            return;
        }

        double cost;
        try {
            cost = Double.parseDouble(costText);
            if (cost < 0) {
                errorLabel.setText("Cost cannot be negative.");
                costField.requestFocus();
                return;
            }
            if (cost > 999999.99) {
                errorLabel.setText("Cost cannot exceed 999,999.99.");
                costField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter a valid cost (numbers only).");
            costField.requestFocus();
            return;
        }

        // Get description (optional)
        String desc = descField.getText().trim();

        try {
            ServiceRecord sr = new ServiceRecord(0, vehicleId, date, type, desc, cost, selectedVehicle.getRegistrationNumber());
            workshopDAO.addServiceRecord(sr);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Service record saved for vehicle: " + selectedVehicle.getRegistrationNumber());
            closeWindow();

        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key")) {
                errorLabel.setText("Invalid vehicle selected.");
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