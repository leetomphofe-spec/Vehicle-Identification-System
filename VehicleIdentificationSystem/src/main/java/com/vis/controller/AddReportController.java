package com.vis.controller;

import com.vis.model.PoliceDAO;
import com.vis.model.PoliceReport;
import com.vis.model.Vehicle;
import com.vis.model.VehicleDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddReportController {

    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private TextField vehicleIdField;
    @FXML private TextField regNumberField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextArea descField;
    @FXML private TextField officerField;
    @FXML private Label errorLabel;

    private final PoliceDAO policeDAO = new PoliceDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML
    public void initialize() {
        // Setup report types dropdown
        typeCombo.setItems(FXCollections.observableArrayList(
                "Accident", "Theft", "Stolen Vehicle", "Missing Vehicle",
                "Suspicious Activity", "Vandalism", "Hit and Run", "Other"
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

        // Setup officer name validation
        setupOfficerValidation();

        // Setup description validation
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
     * Officer Name Validation: Only letters and spaces
     * Prevents numbers and special characters
     */
    private void setupOfficerValidation() {
        officerField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                officerField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
                return;
            }

            // Allow only letters, spaces, and hyphens (for names like "John-Smith")
            if (!newVal.matches("[a-zA-Z\\s-]*")) {
                officerField.setText(oldVal);
                return;
            }

            if (newVal.length() > 50) {
                officerField.setText(oldVal);
                errorLabel.setText("Officer name cannot exceed 50 characters.");
                return;
            }

            officerField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Description Validation: Letters, numbers, spaces, basic punctuation
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

        // Validate report date
        LocalDate reportDate = datePicker.getValue();
        if (reportDate == null) {
            errorLabel.setText("Please select a report date.");
            datePicker.requestFocus();
            return;
        }

        // Validate date is not in the future
        if (reportDate.isAfter(LocalDate.now())) {
            errorLabel.setText("Report date cannot be in the future.");
            datePicker.requestFocus();
            return;
        }

        String date = reportDate.toString();

        // Validate report type
        String type = typeCombo.getValue();
        if (type == null || type.isEmpty()) {
            errorLabel.setText("Please select a report type.");
            typeCombo.requestFocus();
            return;
        }

        // Validate officer name
        String officer = officerField.getText().trim();
        if (officer.isEmpty()) {
            errorLabel.setText("Please enter officer name.");
            officerField.requestFocus();
            return;
        }

        // Officer name validation - only letters, spaces, hyphens
        if (!officer.matches("[a-zA-Z\\s-]+")) {
            errorLabel.setText("Officer name must contain only letters, spaces, and hyphens.");
            officerField.requestFocus();
            return;
        }

        // Get description (optional)
        String desc = descField.getText().trim();

        try {
            PoliceReport rpt = new PoliceReport(0, vehicleId, date, type, desc, officer, selectedVehicle.getRegistrationNumber());
            policeDAO.addReport(rpt);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Police report saved for vehicle: " + selectedVehicle.getRegistrationNumber());
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