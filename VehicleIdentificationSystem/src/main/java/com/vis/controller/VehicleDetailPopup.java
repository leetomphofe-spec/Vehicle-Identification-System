package com.vis.controller;

import com.vis.model.Vehicle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VehicleDetailPopup {

    private final Vehicle vehicle;
    private final String userRole;
    private final Runnable onDataChanged;

    public VehicleDetailPopup(Vehicle vehicle, String userRole, Runnable onDataChanged) {
        this.vehicle = vehicle;
        this.userRole = userRole;
        this.onDataChanged = onDataChanged;
    }

    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/VehicleDetailPopup.fxml"));
            Parent root = loader.load();

            // CORRECT: Get VehicleDetailPopupController, NOT DashboardController
            VehicleDetailPopupController controller = loader.getController();
            controller.setVehicle(vehicle);
            controller.setUserRole(userRole);
            controller.setOnDataChanged(onDataChanged);

            Scene scene = new Scene(root, 600, 500);
            scene.getStylesheets().add(getClass().getResource("/com/vis/css/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Vehicle Details - " + vehicle.getRegistrationNumber());
            stage.setScene(scene);
            stage.setMinWidth(900);
            stage.setMinHeight(650);
            controller.setStage(stage);

            controller.loadVehicleData();

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load VehicleDetailPopup: " + e.getMessage());
        }
    }
}