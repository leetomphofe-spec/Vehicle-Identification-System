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
            // Load the FXML file - get the root directly
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/vis/fxml/VehicleDetailPopup.fxml")
            );
            Parent root = loader.load();

            // Create scene with the root
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/com/vis/css/styles.css").toExternalForm()
            );

            // Get the controller and set data
            VehicleDetailPopupController controller = loader.getController();
            controller.setVehicle(vehicle);
            controller.setUserRole(userRole);
            controller.setOnDataChanged(onDataChanged);

            // Create and configure the stage
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Vehicle Details - " + vehicle.getRegistrationNumber());
            stage.setScene(scene);
            stage.setMinWidth(400);
            stage.setMinHeight(250);
            controller.setStage(stage);

            // Load vehicle data into the controller
            controller.loadVehicleData();

            // Show the popup
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load VehicleDetailPopup: " + e.getMessage());
        }
    }
}