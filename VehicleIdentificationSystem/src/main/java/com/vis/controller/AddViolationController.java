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
import java.time.format.DateTimeFormatter;

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

        // Setup date format
        datePicker.setPromptText("YYYY-MM-DD");
        datePicker.setShowWeekNumbers(false);

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

            // Set cell factory to display registration number and make/model
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

            // Set button cell to display selected item
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

            // Style for success
            vehicleIdField.setStyle("-fx-border-color: #22c55e;");
            regNumberField.setStyle("-fx-border-color: #22c55e;");
            vehicleDetailsField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        }
    }

    public void setOnViolationAdded(Runnable onViolationAdded) {
        this.onViolationAdded = onViolationAdded;
    }

    @FXML
    private void handleSave() {
        try {
            // Get selected vehicle
            Vehicle selectedVehicle = vehicleCombo.getSelectionModel().getSelectedItem();
            if (selectedVehicle == null) {
                errorLabel.setText("Please select a vehicle.");
                return;
            }

            int vehicleId = selectedVehicle.getId();
            String regNumber = selectedVehicle.getRegistrationNumber();

            // Get date from date picker
            LocalDate violationDate = datePicker.getValue();
            if (violationDate == null) {
                errorLabel.setText("Please select a violation date.");
                return;
            }
            String date = violationDate.toString();

            String type = typeCombo.getValue();
            double fine = Double.parseDouble(fineField.getText().trim());
            String description = descField != null ? descField.getText().trim() : "";

            if (type == null || type.isEmpty()) {
                errorLabel.setText("Violation type is required.");
                return;
            }

            if (fine <= 0) {
                errorLabel.setText("Fine amount must be greater than 0.");
                return;
            }

            Violation violation = new Violation(0, vehicleId, date, type, fine, "Unpaid", regNumber);
            violation.setDescription(description);
            policeDAO.addViolation(violation);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Violation recorded successfully for vehicle: " + regNumber);

            if (onViolationAdded != null) {
                onViolationAdded.run();
            }

            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("Fine amount must be a valid number.");
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