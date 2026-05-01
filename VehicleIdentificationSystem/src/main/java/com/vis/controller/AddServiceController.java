package com.vis.controller;

import com.vis.model.ServiceRecord;
import com.vis.model.WorkshopDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddServiceController {

    @FXML private TextField vehicleIdField;
    @FXML private TextField dateField;
    @FXML private TextField typeField;
    @FXML private TextArea descField;
    @FXML private TextField costField;
    @FXML private Label errorLabel;

    private final WorkshopDAO workshopDAO = new WorkshopDAO();

    @FXML
    private void handleSave() {
        try {
            int vehicleId = Integer.parseInt(vehicleIdField.getText().trim());
            String date = dateField.getText().trim();
            String type = typeField.getText().trim();
            String desc = descField.getText().trim();
            double cost = Double.parseDouble(costField.getText().trim());

            if (date.isEmpty() || type.isEmpty()) {
                errorLabel.setText("Date and type are required.");
                return;
            }

            ServiceRecord sr = new ServiceRecord(0, vehicleId, date, type, desc, cost, "");
            workshopDAO.addServiceRecord(sr);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Service record saved.");
            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("Vehicle ID and Cost must be numbers.");
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