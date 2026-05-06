package com.vis.controller;

import com.vis.model.Violation;
import com.vis.model.PoliceDAO;
import com.vis.model.Vehicle;
import com.vis.model.VehicleDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddViolationController {

    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private TextField vehicleIdField;
    @FXML private TextField regNumberField;
    @FXML private TextField vehicleDetailsField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField fineField;
    @FXML private TextArea descField;
    @FXML private Label errorLabel;

    private final PoliceDAO policeDAO = new PoliceDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private Runnable onViolationAdded;

    @FXML
    public void initialize() {
        // Setup violation types dropdown
        typeCombo.setItems(FXCollections.observableArrayList(
                "Speeding", "Running Red Light", "Parking Violation",
                "Driving Without License", "No Insurance", "Drunk Driving",
                "Reckless Driving", "Wrong Way", "Illegal U-Turn", "Expired Registration",
                "No Seatbelt", "Using Phone While Driving", "Overloading"
        ));
        typeCombo.getSelectionModel().selectFirst();

        // Set today's date as default in date picker
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

        // Setup fine amount validation (NUMBERS ONLY - no decimals, no letters)
        setupFineValidation();

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
            vehicleDetailsField.setText(vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getYear() + ")");

            vehicleIdField.setStyle("-fx-border-color: #22c55e;");
            regNumberField.setStyle("-fx-border-color: #22c55e;");
            vehicleDetailsField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        }
    }

    /**
     * Fine Validation: ONLY NUMBERS allowed (0-9)
     * No decimal points, no letters, no special characters
     */
    private void setupFineValidation() {
        fineField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                fineField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
                return;
            }

            // Allow ONLY numbers (0-9) - NO decimal points, NO letters, NO special characters
            if (!newVal.matches("\\d*")) {
                fineField.setText(oldVal);
                return;
            }

            // Max length (10 digits)
            if (newVal.length() > 10) {
                fineField.setText(oldVal);
                errorLabel.setText("Fine amount cannot exceed 10 digits.");
                return;
            }

            fineField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Description Validation
     */
    private void setupDescriptionValidation() {
        descField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                descField.setStyle("-fx-border-color: #334155;");
                return;
            }

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

    public void setOnViolationAdded(Runnable onViolationAdded) {
        this.onViolationAdded = onViolationAdded;
    }

    @FXML
    private void handleSave() {
        // Clear previous error
        errorLabel.setText("");

        // Validate vehicle selection
        Vehicle selectedVehicle = vehicleCombo.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            errorLabel.setText("Please select a vehicle.");
            vehicleCombo.requestFocus();
            return;
        }

        int vehicleId = selectedVehicle.getId();
        String regNumber = selectedVehicle.getRegistrationNumber();

        // Validate violation date
        LocalDate violationDate = datePicker.getValue();
        if (violationDate == null) {
            errorLabel.setText("Please select a violation date.");
            datePicker.requestFocus();
            return;
        }

        // Validate date is not in the future
        if (violationDate.isAfter(LocalDate.now())) {
            errorLabel.setText("Violation date cannot be in the future.");
            datePicker.requestFocus();
            return;
        }

        String date = violationDate.toString();

        // Validate violation type
        String type = typeCombo.getValue();
        if (type == null || type.isEmpty()) {
            errorLabel.setText("Please select a violation type.");
            typeCombo.requestFocus();
            return;
        }

        // Validate fine amount - MUST BE NUMBERS ONLY
        String fineText = fineField.getText().trim();
        if (fineText.isEmpty()) {
            errorLabel.setText("Please enter the fine amount.");
            fineField.requestFocus();
            return;
        }

        // Check if fine contains only numbers
        if (!fineText.matches("\\d+")) {
            errorLabel.setText("Fine amount must contain only numbers (0-9). No decimals or letters allowed.");
            fineField.requestFocus();
            return;
        }

        int fine;
        try {
            fine = Integer.parseInt(fineText);
            if (fine < 0) {
                errorLabel.setText("Fine amount cannot be negative.");
                fineField.requestFocus();
                return;
            }
            if (fine > 999999) {
                errorLabel.setText("Fine amount cannot exceed 999,999.");
                fineField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter a valid fine amount (numbers only).");
            fineField.requestFocus();
            return;
        }

        // Get description (optional)
        String description = descField.getText().trim();

        try {
            Violation violation = new Violation(0, vehicleId, date, type, fine, "Unpaid", regNumber);
            violation.setDescription(description);
            policeDAO.addViolation(violation);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Violation recorded successfully for vehicle: " + regNumber);

            if (onViolationAdded != null) {
                onViolationAdded.run();
            }

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