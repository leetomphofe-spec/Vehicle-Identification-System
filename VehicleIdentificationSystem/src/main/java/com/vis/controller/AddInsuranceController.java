package com.vis.controller;

import com.vis.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddInsuranceController {

    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private TextField vehicleIdField;
    @FXML private TextField regNumberField;
    @FXML private TextField vehicleDetailsField;
    @FXML private ComboBox<String> providerCombo;
    @FXML private TextField policyField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker expiryDatePicker;
    @FXML private ComboBox<String> coverageCombo;
    @FXML private TextField coverageAmountField;
    @FXML private TextField premiumField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label errorLabel;

    private final InsuranceDAO insuranceDAO = new InsuranceDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private Runnable onInsuranceAdded;

    @FXML
    public void initialize() {
        // Setup Insurance Providers dropdown
        providerCombo.setItems(FXCollections.observableArrayList(
                "Lesotho Insurance Corporation", "Alliance Insurance", "Metropolitan Lesotho",
                "Momentum", "Old Mutual", "Sanlam", "Liberty", "Discovery",
                "Standard Lesotho Bank Insurance", "Nedbank Insurance", "FNB Insurance"
        ));
        providerCombo.getSelectionModel().selectFirst();

        // Setup Coverage Types dropdown
        coverageCombo.setItems(FXCollections.observableArrayList(
                "Comprehensive", "Third Party Only", "Third Party Fire & Theft",
                "Liability Only", "Collision Coverage", "Personal Injury Protection",
                "Uninsured Motorist", "Medical Payments", "Gap Insurance"
        ));
        coverageCombo.getSelectionModel().selectFirst();

        // Setup Status dropdown
        statusCombo.setItems(FXCollections.observableArrayList(
                "Active", "Expired", "Cancelled", "Pending", "Suspended"
        ));
        statusCombo.getSelectionModel().selectFirst();

        // Set default dates
        startDatePicker.setValue(LocalDate.now());
        expiryDatePicker.setValue(LocalDate.now().plusYears(1));

        // Set date formats
        startDatePicker.setPromptText("YYYY-MM-DD");
        expiryDatePicker.setPromptText("YYYY-MM-DD");

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

            // Custom cell factory to display registration + make/model
            vehicleCombo.setCellFactory(param -> new ListCell<Vehicle>() {
                @Override
                protected void updateItem(Vehicle vehicle, boolean empty) {
                    super.updateItem(vehicle, empty);
                    if (empty || vehicle == null) {
                        setText(null);
                    } else {
                        setText(vehicle.getRegistrationNumber() + " - " + vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getYear() + ")");
                    }
                }
            });

            // Button cell to display selected item
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
            vehicleDetailsField.setText(vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getYear() + ") - Color: " + vehicle.getColor());

            // Style for success
            vehicleIdField.setStyle("-fx-border-color: #22c55e;");
            regNumberField.setStyle("-fx-border-color: #22c55e;");
            vehicleDetailsField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        }
    }

    public void setOnInsuranceAdded(Runnable onInsuranceAdded) {
        this.onInsuranceAdded = onInsuranceAdded;
    }

    @FXML
    private void handleSave() {
        try {
            // Validate vehicle selection
            Vehicle selectedVehicle = vehicleCombo.getSelectionModel().getSelectedItem();
            if (selectedVehicle == null) {
                errorLabel.setText("Please select a vehicle.");
                return;
            }

            int vehicleId = selectedVehicle.getId();
            String regNumber = selectedVehicle.getRegistrationNumber();

            // Validate policy number
            String policyNumber = policyField.getText().trim();
            if (policyNumber.isEmpty()) {
                errorLabel.setText("Policy Number is required.");
                return;
            }

            // Validate dates
            LocalDate startDate = startDatePicker.getValue();
            LocalDate expiryDate = expiryDatePicker.getValue();

            if (startDate == null) {
                errorLabel.setText("Please select a start date.");
                return;
            }

            if (expiryDate == null) {
                errorLabel.setText("Please select an expiry date.");
                return;
            }

            if (expiryDate.isBefore(startDate)) {
                errorLabel.setText("Expiry date cannot be before start date.");
                return;
            }

            String provider = providerCombo.getValue();
            String coverageType = coverageCombo.getValue();
            double coverageAmount = Double.parseDouble(coverageAmountField.getText().trim());
            double premium = Double.parseDouble(premiumField.getText().trim());
            String status = statusCombo.getValue();

            if (coverageAmount <= 0) {
                errorLabel.setText("Coverage amount must be greater than 0.");
                return;
            }

            if (premium <= 0) {
                errorLabel.setText("Premium must be greater than 0.");
                return;
            }

            // Create and save insurance
            Insurance insurance = new Insurance(0, vehicleId, provider, policyNumber,
                    startDate.toString(), expiryDate.toString(), coverageType,
                    coverageAmount, premium, status);
            insuranceDAO.addInsurance(insurance);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Insurance policy added successfully for vehicle: " + regNumber);

            if (onInsuranceAdded != null) {
                onInsuranceAdded.run();
            }

            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("Coverage Amount and Premium must be valid numbers.");
        } catch (SQLException e) {
            if (e.getMessage().contains("unique constraint")) {
                errorLabel.setText("Policy number already exists. Please use a different policy number.");
            } else {
                errorLabel.setText("Database error: " + e.getMessage());
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