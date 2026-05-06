package com.vis.controller;

import com.vis.model.Vehicle;
import com.vis.model.VehicleDAO;
import com.vis.model.CustomerDAO;
import com.vis.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;

public class AddVehicleController {

    @FXML private TextField regField;
    @FXML private TextField makeField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private ComboBox<Customer> ownerCombo;  // Changed from TextField to ComboBox
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
    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    public void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList(
                "Active", "Stolen", "Recovered", "Under Repair", "Imported"
        ));
        statusCombo.getSelectionModel().selectFirst();

        // Load customers into dropdown
        loadCustomersIntoCombo();

        // Setup validations
        setupRegistrationValidation();
        setupMakeValidation();
        setupModelValidation();
        setupYearValidation();
        setupColorValidation();
        setupEngineValidation();
        setupTransmissionValidation();
        setupFuelTypeValidation();
        setupMileageValidation();
    }

    /**
     * Load customers from database into the Owner dropdown
     */
    private void loadCustomersIntoCombo() {
        try {
            ObservableList<Customer> customers = customerDAO.getAllCustomers();
            ownerCombo.setItems(customers);

            // Set cell factory to display customer name and ID
            ownerCombo.setCellFactory(param -> new ListCell<Customer>() {
                @Override
                protected void updateItem(Customer customer, boolean empty) {
                    super.updateItem(customer, empty);
                    if (empty || customer == null) {
                        setText(null);
                    } else {
                        setText(customer.getId() + " - " + customer.getName());
                    }
                }
            });

            // Set button cell to display selected item
            ownerCombo.setButtonCell(new ListCell<Customer>() {
                @Override
                protected void updateItem(Customer customer, boolean empty) {
                    super.updateItem(customer, empty);
                    if (empty || customer == null) {
                        setText("Select Owner...");
                    } else {
                        setText(customer.getId() + " - " + customer.getName());
                    }
                }
            });

            // Add prompt text
            ownerCombo.setPromptText("Select Owner...");

        } catch (SQLException e) {
            errorLabel.setText("Error loading customers: " + e.getMessage());
        }
    }

    /**
     * Registration Validation: Only allows format: A 123 LS
     */
    private void setupRegistrationValidation() {
        regField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                regField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
                return;
            }

            // Convert to uppercase automatically
            String upperVal = newVal.toUpperCase();
            if (!upperVal.equals(newVal)) {
                regField.setText(upperVal);
                return;
            }

            // Allow only: letters, spaces, digits
            String allowedPattern = "[A-Z0-9\\s]*";
            if (!newVal.matches(allowedPattern)) {
                regField.setText(oldVal);
                return;
            }

            // Full validation after each change
            String fullPattern = "^[A-Z]\\s\\d{2,4}\\sLS$";
            if (newVal.matches(fullPattern)) {
                regField.setStyle("-fx-border-color: #22c55e;");
                errorLabel.setText("");
            } else {
                regField.setStyle("-fx-border-color: #ef4444;");
                errorLabel.setText("Registration must follow format: A 123 LS");
            }
        });
    }

    /**
     * Make Validation: Only letters A-Z, a-z
     */
    private void setupMakeValidation() {
        makeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                makeField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
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
     * Model Validation: Letters, numbers, spaces, hyphens only
     */
    private void setupModelValidation() {
        modelField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                modelField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
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
     * Year Validation: Only numbers
     */
    private void setupYearValidation() {
        yearField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                yearField.setStyle("-fx-border-color: #334155;");
                errorLabel.setText("");
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
     * Color Validation: Only letters
     */
    private void setupColorValidation() {
        colorField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                colorField.setStyle("-fx-border-color: #334155;");
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
     * Engine Validation
     */
    private void setupEngineValidation() {
        engineField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                engineField.setStyle("-fx-border-color: #334155;");
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
     * Transmission Validation
     */
    private void setupTransmissionValidation() {
        transmissionField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                transmissionField.setStyle("-fx-border-color: #334155;");
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
     * Fuel Type Validation: Only letters
     */
    private void setupFuelTypeValidation() {
        fuelTypeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                fuelTypeField.setStyle("-fx-border-color: #334155;");
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
                mileageField.setStyle("-fx-border-color: #334155;");
                return;
            }

            if (!newVal.matches("\\d*")) {
                mileageField.setText(oldVal);
                return;
            }

            if (newVal.length() > 6) {
                mileageField.setText(oldVal);
                errorLabel.setText("Mileage cannot exceed 999,999 km.");
                return;
            }

            mileageField.setStyle("-fx-border-color: #22c55e;");
            errorLabel.setText("");
        });
    }

    @FXML
    private void handleSave() {
        // Validate registration
        String reg = regField.getText().trim();
        if (!reg.matches("^[A-Z]\\s\\d{2,4}\\sLS$")) {
            errorLabel.setText("Invalid registration format. Use: A 123 LS");
            regField.requestFocus();
            return;
        }

        // Validate make
        String make = makeField.getText().trim();
        if (make.isEmpty() || !make.matches("[a-zA-Z]+") || make.length() > 20) {
            errorLabel.setText("Make must contain only letters (max 20 characters).");
            makeField.requestFocus();
            return;
        }

        // Validate model
        String model = modelField.getText().trim();
        if (model.isEmpty() || model.length() > 30) {
            errorLabel.setText("Model is required (max 30 characters).");
            modelField.requestFocus();
            return;
        }

        // Validate year
        String yearS = yearField.getText().trim();
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

        // Validate owner selection
        Customer selectedOwner = ownerCombo.getSelectionModel().getSelectedItem();
        if (selectedOwner == null) {
            errorLabel.setText("Please select an owner from the dropdown.");
            ownerCombo.requestFocus();
            return;
        }
        int ownerId = selectedOwner.getId();

        // Get other fields
        String color = colorField.getText().trim();
        String description = descriptionArea.getText().trim();
        String status = statusCombo.getValue();
        String engine = engineField.getText().trim();
        String transmission = transmissionField.getText().trim();
        String fuelType = fuelTypeField.getText().trim();
        int mileage = mileageField.getText().trim().isEmpty() ? 0 : Integer.parseInt(mileageField.getText().trim());

        try {
            Vehicle v = new Vehicle(0, reg, make, model, year, ownerId, description, color, status, "",
                    engine, transmission, fuelType, mileage);
            vehicleDAO.addVehicle(v);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Vehicle " + reg + " added successfully.");
            closeWindow();

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
        ((Stage) saveBtn.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}