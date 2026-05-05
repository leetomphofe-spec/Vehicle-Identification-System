package com.vis.controller;

import com.vis.model.Insurance;
import com.vis.model.InsuranceDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class EditInsuranceController {

    @FXML private TextField insuranceIdField;
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
    private Insurance currentInsurance;
    private Runnable onInsuranceUpdated;

    @FXML
    public void initialize() {
        providerCombo.setItems(FXCollections.observableArrayList(
                "Lesotho Insurance Corporation", "Alliance Insurance", "Metropolitan Lesotho",
                "Momentum", "Old Mutual", "Sanlam", "Liberty", "Discovery",
                "Standard Lesotho Bank Insurance", "Nedbank Insurance", "FNB Insurance"
        ));

        coverageCombo.setItems(FXCollections.observableArrayList(
                "Comprehensive", "Third Party Only", "Third Party Fire & Theft",
                "Liability Only", "Collision Coverage", "Personal Injury Protection",
                "Uninsured Motorist", "Medical Payments", "Gap Insurance"
        ));

        statusCombo.setItems(FXCollections.observableArrayList(
                "Active", "Expired", "Cancelled", "Pending", "Suspended"
        ));
    }

    public void setInsurance(Insurance insurance) {
        this.currentInsurance = insurance;
        loadInsuranceData();
    }

    public void setOnInsuranceUpdated(Runnable onInsuranceUpdated) {
        this.onInsuranceUpdated = onInsuranceUpdated;
    }

    private void loadInsuranceData() {
        if (currentInsurance == null) return;

        insuranceIdField.setText(String.valueOf(currentInsurance.getId()));
        vehicleIdField.setText(String.valueOf(currentInsurance.getVehicleId()));
        regNumberField.setText(currentInsurance.getRegistrationNumber());
        vehicleDetailsField.setText(currentInsurance.getVehicleInfo());
        providerCombo.setValue(currentInsurance.getProvider());
        policyField.setText(currentInsurance.getPolicyNumber());

        try {
            startDatePicker.setValue(LocalDate.parse(currentInsurance.getStartDate()));
            expiryDatePicker.setValue(LocalDate.parse(currentInsurance.getExpiryDate()));
        } catch (Exception e) {
            // Handle date parsing error
        }

        coverageCombo.setValue(currentInsurance.getCoverageType());
        coverageAmountField.setText(String.valueOf(currentInsurance.getCoverageAmount()));
        premiumField.setText(String.valueOf(currentInsurance.getPremium()));
        statusCombo.setValue(currentInsurance.getStatus());
    }

    @FXML
    private void handleSave() {
        try {
            String provider = providerCombo.getValue();
            String policyNumber = policyField.getText().trim();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate expiryDate = expiryDatePicker.getValue();
            String coverageType = coverageCombo.getValue();
            double coverageAmount = Double.parseDouble(coverageAmountField.getText().trim());
            double premium = Double.parseDouble(premiumField.getText().trim());
            String status = statusCombo.getValue();

            if (policyNumber.isEmpty()) {
                errorLabel.setText("Policy Number is required.");
                return;
            }

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

            if (coverageAmount <= 0) {
                errorLabel.setText("Coverage amount must be greater than 0.");
                return;
            }

            if (premium <= 0) {
                errorLabel.setText("Premium must be greater than 0.");
                return;
            }

            currentInsurance.setProvider(provider);
            currentInsurance.setPolicyNumber(policyNumber);
            currentInsurance.setStartDate(startDate.toString());
            currentInsurance.setExpiryDate(expiryDate.toString());
            currentInsurance.setCoverageType(coverageType);
            currentInsurance.setCoverageAmount(coverageAmount);
            currentInsurance.setPremium(premium);
            currentInsurance.setStatus(status);

            insuranceDAO.updateInsurance(currentInsurance);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Insurance policy updated successfully.");

            if (onInsuranceUpdated != null) {
                onInsuranceUpdated.run();
            }

            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("Coverage Amount and Premium must be valid numbers.");
        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
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