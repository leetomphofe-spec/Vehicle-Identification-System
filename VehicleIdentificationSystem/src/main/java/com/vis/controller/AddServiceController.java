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
            errorLabel.setText("");
        }
    }

    @FXML
    private void handleSave() {
        try {
            Vehicle selectedVehicle = vehicleCombo.getSelectionModel().getSelectedItem();
            if (selectedVehicle == null) {
                errorLabel.setText("Please select a vehicle.");
                return;
            }

            int vehicleId = selectedVehicle.getId();

            LocalDate serviceDate = datePicker.getValue();
            if (serviceDate == null) {
                errorLabel.setText("Please select a service date.");
                return;
            }
            String date = serviceDate.toString();

            String type = typeCombo.getValue();
            String desc = descField.getText().trim();
            double cost = Double.parseDouble(costField.getText().trim());

            if (type == null || type.isEmpty()) {
                errorLabel.setText("Service type is required.");
                return;
            }

            ServiceRecord sr = new ServiceRecord(0, vehicleId, date, type, desc, cost, selectedVehicle.getRegistrationNumber());
            workshopDAO.addServiceRecord(sr);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Service record saved for vehicle: " + selectedVehicle.getRegistrationNumber());
            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("Cost must be a number.");
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