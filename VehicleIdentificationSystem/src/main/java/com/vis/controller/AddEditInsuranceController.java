package com.vis.controller;

import com.vis.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddEditInsuranceController {

    @FXML private Label titleLabel;
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
    private Insurance editingInsurance = null;
    private Runnable onSaveComplete;

    @FXML
    public void initialize() {
        // Setup dropdowns
        providerCombo.setItems(FXCollections.observableArrayList(
                "Lesotho Insurance Corporation", "Alliance Insurance", "Metropolitan Lesotho",
                "Momentum", "Old Mutual", "Sanlam", "Liberty", "Discovery"
        ));
        providerCombo.getSelectionModel().selectFirst();

        coverageCombo.setItems(FXCollections.observableArrayList(
                "Comprehensive", "Third Party Only", "Third Party Fire & Theft",
                "Liability Only", "Collision Coverage", "Gap Insurance"
        ));
        coverageCombo.getSelectionModel().selectFirst();

        statusCombo.setItems(FXCollections.observableArrayList(
                "Active", "Expired", "Cancelled", "Pending"
        ));
        statusCombo.getSelectionModel().selectFirst();

        // Set default dates
        startDatePicker.setValue(LocalDate.now());
        expiryDatePicker.setValue(LocalDate.now().plusYears(1));

        // Setup number-only validation for coverage amount
        setupCoverageAmountValidation();

        // Setup number-only validation for premium
        setupPremiumValidation();

        // Setup policy number validation (letters, numbers, hyphens only)
        setupPolicyValidation();

        // Load vehicles
        loadVehicles();

        // Vehicle selection listener
        vehicleCombo.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                vehicleIdField.setText(String.valueOf(newVal.getId()));
                regNumberField.setText(newVal.getRegistrationNumber());
                vehicleDetailsField.setText(newVal.getMake() + " " + newVal.getModel() + " (" + newVal.getYear() + ")");
            }
        });

        // Set vehicle combo display
        vehicleCombo.setCellFactory(param -> new ListCell<Vehicle>() {
            @Override
            protected void updateItem(Vehicle v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.getRegistrationNumber() + " - " + v.getMake() + " " + v.getModel());
            }
        });
        vehicleCombo.setButtonCell(new ListCell<Vehicle>() {
            @Override
            protected void updateItem(Vehicle v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? "Select a vehicle..." : v.getRegistrationNumber() + " - " + v.getMake() + " " + v.getModel());
            }
        });
    }

    /**
     * Validation for Coverage Amount - Only numbers allowed (no letters, no special characters)
     */
    private void setupCoverageAmountValidation() {
        coverageAmountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                coverageAmountField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
                return;
            }

            // Allow only numbers (0-9) - NO letters, NO decimal points, NO special characters
            if (!newVal.matches("\\d*")) {
                coverageAmountField.setText(oldVal);
                return;
            }

            // Check length (max 10 digits)
            if (newVal.length() > 10) {
                coverageAmountField.setText(oldVal);
                errorLabel.setText("Coverage amount cannot exceed 10 digits.");
                return;
            }

            coverageAmountField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Validation for Premium - Only numbers allowed (no letters, no special characters)
     */
    private void setupPremiumValidation() {
        premiumField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                premiumField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
                return;
            }

            // Allow only numbers (0-9) - NO letters, NO decimal points, NO special characters
            if (!newVal.matches("\\d*")) {
                premiumField.setText(oldVal);
                return;
            }

            // Check length (max 8 digits)
            if (newVal.length() > 8) {
                premiumField.setText(oldVal);
                errorLabel.setText("Premium cannot exceed 8 digits.");
                return;
            }

            premiumField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Validation for Policy Number - Letters, numbers, hyphens, spaces only
     */
    private void setupPolicyValidation() {
        policyField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                policyField.setStyle("-fx-border-color: #334155;");
                return;
            }

            // Allow letters, numbers, hyphens, spaces
            if (!newVal.matches("[A-Za-z0-9\\-\\s]*")) {
                policyField.setText(oldVal);
                return;
            }

            if (newVal.length() > 50) {
                policyField.setText(oldVal);
                errorLabel.setText("Policy number cannot exceed 50 characters.");
                return;
            }

            policyField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    public void setEditMode(Insurance insurance) {
        this.editingInsurance = insurance;
        titleLabel.setText("Edit Insurance Policy");
        vehicleCombo.setDisable(true);
        loadInsuranceData();
    }

    public void setAddMode() {
        this.editingInsurance = null;
        titleLabel.setText("Add Insurance Policy");
        vehicleCombo.setDisable(false);
    }

    public void setOnSaveComplete(Runnable callback) {
        this.onSaveComplete = callback;
    }

    private void loadVehicles() {
        try {
            vehicleCombo.setItems(vehicleDAO.getAllVehicles());
        } catch (SQLException e) {
            errorLabel.setText("Error loading vehicles: " + e.getMessage());
        }
    }

    private void loadInsuranceData() {
        if (editingInsurance == null) return;

        for (Vehicle v : vehicleCombo.getItems()) {
            if (v.getId() == editingInsurance.getVehicleId()) {
                vehicleCombo.getSelectionModel().select(v);
                break;
            }
        }

        providerCombo.setValue(editingInsurance.getProvider());
        policyField.setText(editingInsurance.getPolicyNumber());

        try {
            startDatePicker.setValue(LocalDate.parse(editingInsurance.getStartDate()));
            expiryDatePicker.setValue(LocalDate.parse(editingInsurance.getExpiryDate()));
        } catch(Exception e) {}

        coverageCombo.setValue(editingInsurance.getCoverageType());
        coverageAmountField.setText(String.valueOf((long) editingInsurance.getCoverageAmount()));
        premiumField.setText(String.valueOf((long) editingInsurance.getPremium()));
        statusCombo.setValue(editingInsurance.getStatus());

        // Set green borders for loaded fields
        coverageAmountField.setStyle("-fx-border-color: #22c55e;");
        premiumField.setStyle("-fx-border-color: #22c55e;");
        policyField.setStyle("-fx-border-color: #22c55e;");
    }

    @FXML
    private void handleSave() {
        try {
            // Validate vehicle selection
            Vehicle selectedVehicle = vehicleCombo.getSelectionModel().getSelectedItem();
            if (selectedVehicle == null && editingInsurance == null) {
                errorLabel.setText("Please select a vehicle.");
                vehicleCombo.requestFocus();
                return;
            }

            int vehicleId = editingInsurance != null ? editingInsurance.getVehicleId() : selectedVehicle.getId();
            String regNumber = editingInsurance != null ? editingInsurance.getRegistrationNumber() : selectedVehicle.getRegistrationNumber();

            // Validate policy number
            String policyNumber = policyField.getText().trim();
            if (policyNumber.isEmpty()) {
                errorLabel.setText("Policy Number is required.");
                policyField.requestFocus();
                return;
            }

            // Validate dates
            LocalDate startDate = startDatePicker.getValue();
            LocalDate expiryDate = expiryDatePicker.getValue();

            if (startDate == null) {
                errorLabel.setText("Please select a start date.");
                startDatePicker.requestFocus();
                return;
            }

            if (expiryDate == null) {
                errorLabel.setText("Please select an expiry date.");
                expiryDatePicker.requestFocus();
                return;
            }

            if (expiryDate.isBefore(startDate)) {
                errorLabel.setText("Expiry date cannot be before start date.");
                expiryDatePicker.requestFocus();
                return;
            }

            // Validate provider
            String provider = providerCombo.getValue();
            if (provider == null || provider.isEmpty()) {
                errorLabel.setText("Please select an insurance provider.");
                providerCombo.requestFocus();
                return;
            }

            // Validate coverage type
            String coverageType = coverageCombo.getValue();
            if (coverageType == null || coverageType.isEmpty()) {
                errorLabel.setText("Please select a coverage type.");
                coverageCombo.requestFocus();
                return;
            }

            // Validate coverage amount (must be a number)
            String coverageText = coverageAmountField.getText().trim();
            if (coverageText.isEmpty()) {
                errorLabel.setText("Coverage amount is required.");
                coverageAmountField.requestFocus();
                return;
            }

            // Check if coverage amount contains only numbers
            if (!coverageText.matches("\\d+")) {
                errorLabel.setText("Coverage amount must contain only numbers (0-9).");
                coverageAmountField.requestFocus();
                return;
            }

            long coverageAmountLong = Long.parseLong(coverageText);
            if (coverageAmountLong <= 0) {
                errorLabel.setText("Coverage amount must be greater than 0.");
                coverageAmountField.requestFocus();
                return;
            }
            double coverageAmount = coverageAmountLong;

            // Validate premium (must be a number)
            String premiumText = premiumField.getText().trim();
            if (premiumText.isEmpty()) {
                errorLabel.setText("Premium amount is required.");
                premiumField.requestFocus();
                return;
            }

            // Check if premium contains only numbers
            if (!premiumText.matches("\\d+")) {
                errorLabel.setText("Premium must contain only numbers (0-9).");
                premiumField.requestFocus();
                return;
            }

            long premiumLong = Long.parseLong(premiumText);
            if (premiumLong <= 0) {
                errorLabel.setText("Premium must be greater than 0.");
                premiumField.requestFocus();
                return;
            }
            double premium = premiumLong;

            // Validate status
            String status = statusCombo.getValue();
            if (status == null || status.isEmpty()) {
                errorLabel.setText("Please select a status.");
                statusCombo.requestFocus();
                return;
            }

            if (editingInsurance != null) {
                // Update existing insurance
                editingInsurance.setProvider(provider);
                editingInsurance.setPolicyNumber(policyNumber);
                editingInsurance.setStartDate(startDate.toString());
                editingInsurance.setExpiryDate(expiryDate.toString());
                editingInsurance.setCoverageType(coverageType);
                editingInsurance.setCoverageAmount(coverageAmount);
                editingInsurance.setPremium(premium);
                editingInsurance.setStatus(status);
                insuranceDAO.updateInsurance(editingInsurance);
                showAlert("Success", "Insurance policy updated successfully.");
            } else {
                // Add new insurance
                Insurance insurance = new Insurance(0, vehicleId, provider, policyNumber,
                        startDate.toString(), expiryDate.toString(), coverageType,
                        coverageAmount, premium, status);
                insuranceDAO.addInsurance(insurance);
                showAlert("Success", "Insurance policy added successfully for vehicle: " + regNumber);
            }

            if (onSaveComplete != null) onSaveComplete.run();
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

    @FXML private void handleCancel() { closeWindow(); }

    private void closeWindow() {
        ((Stage) errorLabel.getScene().getWindow()).close();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}