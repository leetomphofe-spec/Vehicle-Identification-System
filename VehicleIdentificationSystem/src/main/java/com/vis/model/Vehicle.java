package com.vis.model;

import javafx.beans.property.*;

public class Vehicle extends BaseEntity {

    private final StringProperty registrationNumber = new SimpleStringProperty();
    private final StringProperty make = new SimpleStringProperty();
    private final StringProperty model = new SimpleStringProperty();
    private final IntegerProperty year = new SimpleIntegerProperty();
    private final IntegerProperty ownerId = new SimpleIntegerProperty();
    private final StringProperty ownerName = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty color = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty primaryImagePath = new SimpleStringProperty();
    private final StringProperty engine = new SimpleStringProperty();
    private final StringProperty transmission = new SimpleStringProperty();
    private final StringProperty fuelType = new SimpleStringProperty();
    private final IntegerProperty mileage = new SimpleIntegerProperty();

    public Vehicle() {
        super();
    }

    public Vehicle(int id, String regNum, String make, String model, int year, int ownerId,
                   String description, String color, String status, String primaryImagePath,
                   String engine, String transmission, String fuelType, int mileage) {
        super(id);
        setRegistrationNumber(regNum);
        setMake(make);
        setModel(model);
        setYear(year);
        setOwnerId(ownerId);
        setDescription(description);
        setColor(color);
        setStatus(status);
        setPrimaryImagePath(primaryImagePath);
        setEngine(engine);
        setTransmission(transmission);
        setFuelType(fuelType);
        setMileage(mileage);
    }

    public Vehicle(int id, String regNum, String make, String model, int year, int ownerId) {
        super(id);
        setRegistrationNumber(regNum);
        setMake(make);
        setModel(model);
        setYear(year);
        setOwnerId(ownerId);
        setStatus("Active");
        setDescription("");
        setColor("");
        setPrimaryImagePath("");
        setEngine("");
        setTransmission("");
        setFuelType("");
        setMileage(0);
    }

    public String getRegistrationNumber() { return registrationNumber.get(); }
    public void setRegistrationNumber(String v) { registrationNumber.set(v); }
    public StringProperty registrationNumberProperty() { return registrationNumber; }

    public String getMake() { return make.get(); }
    public void setMake(String v) { make.set(v); }
    public StringProperty makeProperty() { return make; }

    public String getModel() { return model.get(); }
    public void setModel(String v) { model.set(v); }
    public StringProperty modelProperty() { return model; }

    public int getYear() { return year.get(); }
    public void setYear(int v) { year.set(v); }
    public IntegerProperty yearProperty() { return year; }

    public int getOwnerId() { return ownerId.get(); }
    public void setOwnerId(int v) { ownerId.set(v); }
    public IntegerProperty ownerIdProperty() { return ownerId; }

    public String getOwnerName() { return ownerName.get(); }
    public void setOwnerName(String v) { ownerName.set(v); }
    public StringProperty ownerNameProperty() { return ownerName; }

    public String getDescription() { return description.get(); }
    public void setDescription(String v) { description.set(v); }
    public StringProperty descriptionProperty() { return description; }

    public String getColor() { return color.get(); }
    public void setColor(String v) { color.set(v); }
    public StringProperty colorProperty() { return color; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { status.set(v); }
    public StringProperty statusProperty() { return status; }

    public String getPrimaryImagePath() { return primaryImagePath.get(); }
    public void setPrimaryImagePath(String v) { primaryImagePath.set(v); }
    public StringProperty primaryImagePathProperty() { return primaryImagePath; }

    public String getEngine() { return engine.get(); }
    public void setEngine(String v) { engine.set(v); }
    public StringProperty engineProperty() { return engine; }

    public String getTransmission() { return transmission.get(); }
    public void setTransmission(String v) { transmission.set(v); }
    public StringProperty transmissionProperty() { return transmission; }

    public String getFuelType() { return fuelType.get(); }
    public void setFuelType(String v) { fuelType.set(v); }
    public StringProperty fuelTypeProperty() { return fuelType; }

    public int getMileage() { return mileage.get(); }
    public void setMileage(int v) { mileage.set(v); }
    public IntegerProperty mileageProperty() { return mileage; }

    @Override
    public String getDisplayInfo() {
        return String.format("[Vehicle] %s | %s %s (%d)",
                getRegistrationNumber(), getMake(), getModel(), getYear());
    }
}