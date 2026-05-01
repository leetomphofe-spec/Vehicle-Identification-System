package com.vis.model;

import javafx.beans.property.*;

/**
 * VehicleImage model - Stores images associated with vehicles
 */
public class VehicleImage {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId = new SimpleIntegerProperty();
    private final StringProperty imagePath = new SimpleStringProperty();
    private final StringProperty imageName = new SimpleStringProperty();
    private final BooleanProperty isPrimary = new SimpleBooleanProperty();
    private final StringProperty uploadDate = new SimpleStringProperty();

    public VehicleImage() {}

    public VehicleImage(int id, int vehicleId, String imagePath, String imageName, boolean isPrimary, String uploadDate) {
        setId(id);
        setVehicleId(vehicleId);
        setImagePath(imagePath);
        setImageName(imageName);
        setIsPrimary(isPrimary);
        setUploadDate(uploadDate);
    }

    // Getters and Setters
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getVehicleId() { return vehicleId.get(); }
    public void setVehicleId(int value) { vehicleId.set(value); }
    public IntegerProperty vehicleIdProperty() { return vehicleId; }

    public String getImagePath() { return imagePath.get(); }
    public void setImagePath(String value) { imagePath.set(value); }
    public StringProperty imagePathProperty() { return imagePath; }

    public String getImageName() { return imageName.get(); }
    public void setImageName(String value) { imageName.set(value); }
    public StringProperty imageNameProperty() { return imageName; }

    public boolean getIsPrimary() { return isPrimary.get(); }
    public void setIsPrimary(boolean value) { isPrimary.set(value); }
    public BooleanProperty isPrimaryProperty() { return isPrimary; }

    public String getUploadDate() { return uploadDate.get(); }
    public void setUploadDate(String value) { uploadDate.set(value); }
    public StringProperty uploadDateProperty() { return uploadDate; }
}