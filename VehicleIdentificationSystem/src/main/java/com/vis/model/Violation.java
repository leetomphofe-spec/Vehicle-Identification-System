package com.vis.model;

import javafx.beans.property.*;

/**
 * Violation model - extends BaseEntity (Inheritance).
 */
public class Violation extends BaseEntity {

    private final IntegerProperty vehicleId     = new SimpleIntegerProperty();
    private final StringProperty  violationDate = new SimpleStringProperty();
    private final StringProperty  violationType = new SimpleStringProperty();
    private final DoubleProperty  fineAmount    = new SimpleDoubleProperty();
    private final StringProperty  status        = new SimpleStringProperty();
    private final StringProperty  regNumber     = new SimpleStringProperty();

    public Violation() { super(); }

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
    }

    public IntegerProperty vehicleIdProperty()     { return vehicleId; }
    public StringProperty  violationDateProperty() { return violationDate; }
    public StringProperty  violationTypeProperty() { return violationType; }
    public DoubleProperty  fineAmountProperty()    { return fineAmount; }
    public StringProperty  statusProperty()        { return status; }
    public StringProperty  regNumberProperty()     { return regNumber; }

    public int    getVehicleId()     { return vehicleId.get(); }
    public String getViolationDate() { return violationDate.get(); }
    public String getViolationType() { return violationType.get(); }
    public double getFineAmount()    { return fineAmount.get(); }
    public String getStatus()        { return status.get(); }
    public String getRegNumber()     { return regNumber.get(); }

    public void setVehicleId(int v)       { vehicleId.set(v); }
    public void setViolationDate(String v){ violationDate.set(v); }
    public void setViolationType(String v){ violationType.set(v); }
    public void setFineAmount(double v)   { fineAmount.set(v); }
    public void setStatus(String v)       { status.set(v); }
    public void setRegNumber(String v)    { regNumber.set(v); }

    @Override
    public String getDisplayInfo() {
        return String.format("[Violation] %s | %s | M%.2f | %s",
                getRegNumber(), getViolationType(), getFineAmount(), getStatus());
    }
}
