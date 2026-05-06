package com.vis.controller;

import com.vis.model.Vehicle;
import com.vis.model.VehicleDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class EditVehicleController {

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
    private Vehicle vehicle;
    private Runnable onVehicleUpdated;
    private Stage stage;

    @FXML
    public void initialize() {
        statusCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "Active", "Stolen", "Recovered", "Under Repair", "Imported"
        ));
        statusCombo.getSelectionModel().selectFirst();

        // Setup all validations
        setupRegistrationValidation();
        setupMakeValidation();
        setupModelValidation();
        setupYearValidation();
        setupOwnerIdValidation();
        setupColorValidation();
        setupEngineValidation();
        setupTransmissionValidation();
        setupFuelTypeValidation();
        setupMileageValidation();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        if (stage != null) {
            stage.setResizable(true);
            stage.setMinWidth(550);
            stage.setMinHeight(650);
            stage.setMaximized(false);
        }
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        loadVehicleData();
    }

    public void setOnVehicleUpdated(Runnable onVehicleUpdated) {
        this.onVehicleUpdated = onVehicleUpdated;
    }

    private void loadVehicleData() {
        if (vehicle == null) return;

        regField.setText(vehicle.getRegistrationNumber());
        makeField.setText(vehicle.getMake());
        modelField.setText(vehicle.getModel());
        yearField.setText(String.valueOf(vehicle.getYear()));
        ownerIdField.setText(String.valueOf(vehicle.getOwnerId()));
        colorField.setText(vehicle.getColor());
        statusCombo.setValue(vehicle.getStatus());
        engineField.setText(vehicle.getEngine());
        transmissionField.setText(vehicle.getTransmission());
        fuelTypeField.setText(vehicle.getFuelType());
        mileageField.setText(vehicle.getMileage() > 0 ? String.valueOf(vehicle.getMileage()) : "");
        descriptionArea.setText(vehicle.getDescription());

        // Set green borders for valid loaded fields
        regField.setStyle("-fx-border-color: #22c55e;");
        makeField.setStyle("-fx-border-color: #22c55e;");
        modelField.setStyle("-fx-border-color: #22c55e;");
        yearField.setStyle("-fx-border-color: #22c55e;");
        ownerIdField.setStyle("-fx-border-color: #22c55e;");
    }

    /**
     * Registration Validation: Format: A 123 LS (Letter + space + 2-4 digits + space + LS)
     */
    private void setupRegistrationValidation() {
        regField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                regField.setStyle("-fx-border-color: #cbd5e1;");
                errorLabel.setText("");
                return;
            }

            // Convert to uppercase automatically
            String upperVal = newVal.toUpperCase();
            if (!upperVal.equals(newVal)) {
                regField.setText(upperVal);
                return;
            }

            // Allow only letters, spaces, digits
            if (!newVal.matches("[A-Z0-9\\s]*")) {
                regField.setText(oldVal);
                return;
            }

            String fullPattern = "^[A-Z]\\s\\d{2,4}\\sLS$";
            if (newVal.matches(fullPattern)) {
                regField.setStyle("-fx-border-color: #22c55e;");
                errorLabel.setText("");
            } else {
                regField.setStyle("-fx-border-color: #ef4444;");
                errorLabel.setText("Format: A 123 LS");
            }
        });
    }

    /**
     * Make Validation: Letters only (A-Z, a-z)
     */
    private void setupMakeValidation() {
        makeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                makeField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }

            if (!newVal.matches("[a-zA-Z]*")) {
                makeField.setText(oldVal);
                return;
            }

            if (newVal.length() > 20) {
                makeField.setText(oldVal);
                errorLabel.setText("Make cannot exceed 20 characters.");
                return;
            }

            makeField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Model Validation: Letters, numbers, spaces, hyphens
     */
    private void setupModelValidation() {
        modelField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                modelField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }

            if (!newVal.matches("[a-zA-Z0-9\\s-]*")) {
                modelField.setText(oldVal);
                return;
            }

            if (newVal.length() > 30) {
                modelField.setText(oldVal);
                errorLabel.setText("Model cannot exceed 30 characters.");
                return;
            }

            modelField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Year Validation: Only 4-digit numbers (1900-current year+1)
     */
    private void setupYearValidation() {
        yearField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                yearField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }

            if (!newVal.matches("\\d*")) {
                yearField.setText(oldVal);
                return;
            }

            if (newVal.length() > 4) {
                yearField.setText(oldVal);
                errorLabel.setText("Year must be 4 digits.");
                return;
            }

            if (newVal.length() == 4) {
                int year = Integer.parseInt(newVal);
                int currentYear = java.time.Year.now().getValue();
                if (year >= 1900 && year <= currentYear + 1) {
                    yearField.setStyle("-fx-border-color: #22c55e;");
                    errorLabel.setText("");
                } else {
                    yearField.setStyle("-fx-border-color: #ef4444;");
                    errorLabel.setText("Year must be between 1900 and " + (currentYear + 1));
                }
            } else {
                yearField.setStyle("-fx-border-color: #eab308;");
                errorLabel.setText("Enter 4-digit year");
            }
        });
    }

    /**
     * Owner ID Validation: Only numbers
     */
    private void setupOwnerIdValidation() {
        ownerIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                ownerIdField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }

            if (!newVal.matches("\\d*")) {
                ownerIdField.setText(oldVal);
                return;
            }

            ownerIdField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Color Validation: Letters only
     */
    private void setupColorValidation() {
        colorField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                colorField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }

            if (!newVal.matches("[a-zA-Z]*")) {
                colorField.setText(oldVal);
                return;
            }

            if (newVal.length() > 15) {
                colorField.setText(oldVal);
                errorLabel.setText("Color cannot exceed 15 characters.");
                return;
            }

            colorField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Engine Validation: Letters, numbers, spaces, dots, hyphens
     */
    private void setupEngineValidation() {
        engineField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                engineField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }

            if (!newVal.matches("[a-zA-Z0-9\\s\\.-]*")) {
                engineField.setText(oldVal);
                return;
            }

            if (newVal.length() > 50) {
                engineField.setText(oldVal);
                errorLabel.setText("Engine info cannot exceed 50 characters.");
                return;
            }

            engineField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Transmission Validation: Letters, numbers, spaces, hyphens
     */
    private void setupTransmissionValidation() {
        transmissionField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                transmissionField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }

            if (!newVal.matches("[a-zA-Z0-9\\s-]*")) {
                transmissionField.setText(oldVal);
                return;
            }

            if (newVal.length() > 40) {
                transmissionField.setText(oldVal);
                errorLabel.setText("Transmission cannot exceed 40 characters.");
                return;
            }

            transmissionField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Fuel Type Validation: Letters only
     */
    private void setupFuelTypeValidation() {
        fuelTypeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                fuelTypeField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }

            if (!newVal.matches("[a-zA-Z]*")) {
                fuelTypeField.setText(oldVal);
                return;
            }

            if (newVal.length() > 20) {
                fuelTypeField.setText(oldVal);
                errorLabel.setText("Fuel type cannot exceed 20 characters.");
                return;
            }

            fuelTypeField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    /**
     * Mileage Validation: Only numbers
     */
    private void setupMileageValidation() {
        mileageField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                mileageField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }

            if (!newVal.matches("\\d*")) {
                mileageField.setText(oldVal);
                return;
            }

            if (newVal.length() > 7) {
                mileageField.setText(oldVal);
                errorLabel.setText("Mileage cannot exceed 7 digits.");
                return;
            }

            mileageField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    @FXML
    private void handleSave() {
        // Clear previous error
        errorLabel.setText("");

        try {
            // Get values
            String reg = regField.getText().trim().toUpperCase();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            String yearS = yearField.getText().trim();
            String ownS = ownerIdField.getText().trim();
            String color = colorField.getText().trim();
            String description = descriptionArea.getText().trim();
            String status = statusCombo.getValue();
            String engine = engineField.getText().trim();
            String transmission = transmissionField.getText().trim();
            String fuelType = fuelTypeField.getText().trim();
            String mileageS = mileageField.getText().trim();

            // Validate required fields
            if (reg.isEmpty() || make.isEmpty() || model.isEmpty() || yearS.isEmpty() || ownS.isEmpty()) {
                errorLabel.setText("All required fields (*) are required.");
                return;
            }

            // Validate Registration format
            if (!reg.matches("^[A-Z]\\s\\d{2,4}\\sLS$")) {
                errorLabel.setText("Registration must follow format: A 123 LS");
                regField.requestFocus();
                return;
            }

            // Validate Make (letters only)
            if (!make.matches("[a-zA-Z]+")) {
                errorLabel.setText("Make must contain only letters.");
                makeField.requestFocus();
                return;
            }

            // Validate Year
            if (!yearS.matches("\\d{4}")) {
                errorLabel.setText("Year must be 4 digits.");
                yearField.requestFocus();
                return;
            }
            int year = Integer.parseInt(yearS);
            int currentYear = java.time.Year.now().getValue();
            if (year < 1900 || year > currentYear + 1) {
                errorLabel.setText("Year must be between 1900 and " + (currentYear + 1));
                yearField.requestFocus();
                return;
            }

            // Validate Owner ID
            if (!ownS.matches("\\d+")) {
                errorLabel.setText("Owner ID must be a number.");
                ownerIdField.requestFocus();
                return;
            }
            int ownerId = Integer.parseInt(ownS);

            // Validate Color (letters only, optional)
            if (!color.isEmpty() && !color.matches("[a-zA-Z]+")) {
                errorLabel.setText("Color must contain only letters.");
                colorField.requestFocus();
                return;
            }

            // Validate Engine (optional)
            if (!engine.isEmpty() && !engine.matches("[a-zA-Z0-9\\s\\.-]*")) {
                errorLabel.setText("Engine contains invalid characters.");
                engineField.requestFocus();
                return;
            }

            // Validate Transmission (optional)
            if (!transmission.isEmpty() && !transmission.matches("[a-zA-Z0-9\\s-]*")) {
                errorLabel.setText("Transmission contains invalid characters.");
                transmissionField.requestFocus();
                return;
            }

            // Validate Fuel Type (letters only, optional)
            if (!fuelType.isEmpty() && !fuelType.matches("[a-zA-Z]+")) {
                errorLabel.setText("Fuel type must contain only letters.");
                fuelTypeField.requestFocus();
                return;
            }

            // Validate Mileage (numbers only, optional)
            int mileage = 0;
            if (!mileageS.isEmpty()) {
                if (!mileageS.matches("\\d+")) {
                    errorLabel.setText("Mileage must be a number.");
                    mileageField.requestFocus();
                    return;
                }
                mileage = Integer.parseInt(mileageS);
                if (mileage > 9999999) {
                    errorLabel.setText("Mileage cannot exceed 9,999,999 km.");
                    mileageField.requestFocus();
                    return;
                }
            }

            // Update vehicle
            vehicle.setRegistrationNumber(reg);
            vehicle.setMake(make);
            vehicle.setModel(model);
            vehicle.setYear(year);
            vehicle.setOwnerId(ownerId);
            vehicle.setColor(color);
            vehicle.setDescription(description);
            vehicle.setStatus(status);
            vehicle.setEngine(engine);
            vehicle.setTransmission(transmission);
            vehicle.setFuelType(fuelType);
            vehicle.setMileage(mileage);

            vehicleDAO.updateVehicle(vehicle);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle updated successfully.");

            if (onVehicleUpdated != null) {
                onVehicleUpdated.run();
            }

            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("Year, Owner ID, and Mileage must be valid numbers.");
        } catch (SQLException e) {
            errorLabel.setText("Database Error: " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage currentStage = (Stage) saveBtn.getScene().getWindow();
        currentStage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}