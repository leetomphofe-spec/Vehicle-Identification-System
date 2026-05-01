package com.vis.controller;

import com.vis.model.Vehicle;
import com.vis.model.VehicleDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddVehicleController {

    @FXML
    private TextField regField;
    @FXML
    private TextField makeField;
    @FXML
    private TextField modelField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField ownerIdField;
    @FXML
    private TextField colorField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<String> statusCombo;
    @FXML
    private TextField engineField;
    @FXML
    private TextField transmissionField;
    @FXML
    private TextField fuelTypeField;
    @FXML
    private TextField mileageField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button saveBtn;

    private final VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML
    public void initialize() {
        statusCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "Active", "Stolen", "Recovered", "Under Repair", "Imported"
        ));
        statusCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSave() {
        try {
            String reg = regField.getText().trim();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            String yearS = yearField.getText().trim();
            String ownS = ownerIdField.getText().trim();
            String color = colorField != null ? colorField.getText().trim() : "";
            String description = descriptionArea != null ? descriptionArea.getText().trim() : "";
            String status = statusCombo != null ? statusCombo.getValue() : "Active";
            String engine = engineField != null ? engineField.getText().trim() : "";
            String transmission = transmissionField != null ? transmissionField.getText().trim() : "";
            String fuelType = fuelTypeField != null ? fuelTypeField.getText().trim() : "";
            String mileageS = mileageField != null ? mileageField.getText().trim() : "";

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

            Vehicle v = new Vehicle(0, reg, make, model, year, ownerId, description, color, status, "",
                    engine, transmission, fuelType, mileage);
            vehicleDAO.addVehicle(v);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Vehicle " + reg + " added successfully.");
            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("Year, Owner ID, and Mileage must be numbers.");
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