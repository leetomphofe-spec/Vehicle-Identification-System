package com.vis.controller;

import com.vis.model.PoliceDAO;
import com.vis.model.PoliceReport;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * AddReportController - MVC Controller for AddReport.fxml
 */
public class AddReportController {

    @FXML private TextField vehicleIdField;
    @FXML private TextField dateField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextArea  descField;
    @FXML private TextField officerField;
    @FXML private Label     errorLabel;

    private final PoliceDAO policeDAO = new PoliceDAO();

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList(
                "Accident", "Theft", "Violation", "Missing", "Other"));
        typeCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSave() {
        try {
            int    vehicleId  = Integer.parseInt(vehicleIdField.getText().trim());
            String date       = dateField.getText().trim();
            String type       = typeCombo.getValue();
            String desc       = descField.getText().trim();
            String officer    = officerField.getText().trim();

            if (date.isEmpty() || officer.isEmpty()) {
                errorLabel.setText("Date and officer name are required.");
                return;
            }

            PoliceReport rpt = new PoliceReport(0, vehicleId, date, type, desc, officer, "");
            policeDAO.addReport(rpt);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Police report saved.");
            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("Vehicle ID must be a number.");
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML private void handleCancel() { closeWindow(); }

    private void closeWindow() {
        ((Stage) vehicleIdField.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
