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

    @FXML
    public void initialize() {
        statusCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "Active", "Stolen", "Recovered", "Under Repair", "Imported"
        ));
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
    }

    @FXML
    private void handleSave() {
        try {
            String reg = regField.getText().trim();
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

            if (reg.isEmpty() || make.isEmpty() || model.isEmpty() || yearS.isEmpty() || ownS.isEmpty()) {
                errorLabel.setText("All required fields (*) are required.");
                return;
            }

            int year = Integer.parseInt(yearS);
            int ownerId = Integer.parseInt(ownS);
            int mileage = 0;
            if (!mileageS.isEmpty()) {
                mileage = Integer.parseInt(mileageS);
            }

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
            errorLabel.setText("Year, Owner ID, and Mileage must be numbers.");
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