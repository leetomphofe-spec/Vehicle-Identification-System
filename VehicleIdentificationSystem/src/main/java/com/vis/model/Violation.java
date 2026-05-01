package com.vis.model;

import javafx.beans.property.*;

public class Violation extends BaseEntity {

    private final IntegerProperty vehicleId = new SimpleIntegerProperty();
    private final StringProperty violationDate = new SimpleStringProperty();
    private final StringProperty violationType = new SimpleStringProperty();
    private final DoubleProperty fineAmount = new SimpleDoubleProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty regNumber = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();  // ADD THIS

    public Violation() {
        super();
    }

    // Constructor without description
    public Violation(int id, int vehicleId, String violationDate,
                     String violationType, double fineAmount,
                     String status, String regNumber) {
        super(id);
        setVehicleId(vehicleId);
        setViolationDate(violationDate);
        setViolationType(violationType);
        setFineAmount(fineAmount);
        setStatus(status);
        setRegNumber(regNumber);
        setDescription("");
    }

    // Constructor with description - ADD THIS
    public Violation(int id, int vehicleId, String violationDate,
                     String violationType, double fineAmount,
                     String status, String regNumber, String description) {
        super(id);
        setVehicleId(vehicleId);
        setViolationDate(violationDate);
        setViolationType(violationType);
        setFineAmount(fineAmount);
        setStatus(status);
        setRegNumber(regNumber);
        setDescription(description);
    }

    // Getters and Setters
    public int getVehicleId() { return vehicleId.get(); }
    public void setVehicleId(int v) { vehicleId.set(v); }
    public IntegerProperty vehicleIdProperty() { return vehicleId; }

    public String getViolationDate() { return violationDate.get(); }
    public void setViolationDate(String v) { violationDate.set(v); }
    public StringProperty violationDateProperty() { return violationDate; }

    public String getViolationType() { return violationType.get(); }
    public void setViolationType(String v) { violationType.set(v); }
    public StringProperty violationTypeProperty() { return violationType; }

    public double getFineAmount() { return fineAmount.get(); }
    public void setFineAmount(double v) { fineAmount.set(v); }
    public DoubleProperty fineAmountProperty() { return fineAmount; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { status.set(v); }
    public StringProperty statusProperty() { return status; }

    public String getRegNumber() { return regNumber.get(); }
    public void setRegNumber(String v) { regNumber.set(v); }
    public StringProperty regNumberProperty() { return regNumber; }

    public String getDescription() { return description.get(); }  // ADD THIS
    public void setDescription(String v) { description.set(v); }  // ADD THIS
    public StringProperty descriptionProperty() { return description; }  // ADD THIS

    @Override
    public String getDisplayInfo() {
        return String.format("[Violation] %s | %s | M%.2f | %s",
                getRegNumber(), getViolationType(), getFineAmount(), getStatus());
    }
}