package com.vis.model;

import javafx.beans.property.*;

/**
 * ServiceRecord model - extends BaseEntity (Inheritance).
 */
public class ServiceRecord extends BaseEntity {

    private final IntegerProperty vehicleId   = new SimpleIntegerProperty();
    private final StringProperty  serviceDate = new SimpleStringProperty();
    private final StringProperty  serviceType = new SimpleStringProperty();
    private final StringProperty  description = new SimpleStringProperty();
    private final DoubleProperty  cost        = new SimpleDoubleProperty();
    private final StringProperty  regNumber   = new SimpleStringProperty();

    public ServiceRecord() { super(); }

    public ServiceRecord(int id, int vehicleId, String serviceDate,
                         String serviceType, String description,
                         double cost, String regNumber) {
        super(id);
        setVehicleId(vehicleId);
        setServiceDate(serviceDate);
        setServiceType(serviceType);
        setDescription(description);
        setCost(cost);
        setRegNumber(regNumber);
    }

    public IntegerProperty vehicleIdProperty()   { return vehicleId; }
    public StringProperty  serviceDateProperty() { return serviceDate; }
    public StringProperty  serviceTypeProperty() { return serviceType; }
    public StringProperty  descriptionProperty() { return description; }
    public DoubleProperty  costProperty()        { return cost; }
    public StringProperty  regNumberProperty()   { return regNumber; }

    public int    getVehicleId()   { return vehicleId.get(); }
    public String getServiceDate() { return serviceDate.get(); }
    public String getServiceType() { return serviceType.get(); }
    public String getDescription() { return description.get(); }
    public double getCost()        { return cost.get(); }
    public String getRegNumber()   { return regNumber.get(); }

    public void setVehicleId(int v)     { vehicleId.set(v); }
    public void setServiceDate(String v){ serviceDate.set(v); }
    public void setServiceType(String v){ serviceType.set(v); }
    public void setDescription(String v){ description.set(v); }
    public void setCost(double v)       { cost.set(v); }
    public void setRegNumber(String v)  { regNumber.set(v); }

    @Override
    public String getDisplayInfo() {
        return String.format("[Service] %s | %s | %s | M%.2f",
                getRegNumber(), getServiceDate(), getServiceType(), getCost());
    }
}
