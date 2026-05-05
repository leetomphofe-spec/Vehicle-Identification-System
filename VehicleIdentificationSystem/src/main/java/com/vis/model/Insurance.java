package com.vis.model;

import javafx.beans.property.*;

public class Insurance extends BaseEntity {

    private final IntegerProperty vehicleId = new SimpleIntegerProperty();
    private final StringProperty vehicleInfo = new SimpleStringProperty();
    private final StringProperty registrationNumber = new SimpleStringProperty();
    private final StringProperty provider = new SimpleStringProperty();
    private final StringProperty policyNumber = new SimpleStringProperty();
    private final StringProperty startDate = new SimpleStringProperty();
    private final StringProperty expiryDate = new SimpleStringProperty();
    private final StringProperty coverageType = new SimpleStringProperty();
    private final DoubleProperty coverageAmount = new SimpleDoubleProperty();
    private final DoubleProperty premium = new SimpleDoubleProperty();
    private final StringProperty status = new SimpleStringProperty();

    public Insurance() {
        super();
    }

    public Insurance(int id, int vehicleId, String provider, String policyNumber,
                     String startDate, String expiryDate, String coverageType,
                     double coverageAmount, double premium, String status) {
        super(id);
        setVehicleId(vehicleId);
        setProvider(provider);
        setPolicyNumber(policyNumber);
        setStartDate(startDate);
        setExpiryDate(expiryDate);
        setCoverageType(coverageType);
        setCoverageAmount(coverageAmount);
        setPremium(premium);
        setStatus(status);
    }

    public Insurance(int id, int vehicleId, String registrationNumber, String vehicleInfo,
                     String provider, String policyNumber, String startDate, String expiryDate,
                     String coverageType, double coverageAmount, double premium, String status) {
        super(id);
        setVehicleId(vehicleId);
        setRegistrationNumber(registrationNumber);
        setVehicleInfo(vehicleInfo);
        setProvider(provider);
        setPolicyNumber(policyNumber);
        setStartDate(startDate);
        setExpiryDate(expiryDate);
        setCoverageType(coverageType);
        setCoverageAmount(coverageAmount);
        setPremium(premium);
        setStatus(status);
    }

    public int getVehicleId() { return vehicleId.get(); }
    public void setVehicleId(int v) { vehicleId.set(v); }
    public IntegerProperty vehicleIdProperty() { return vehicleId; }

    public String getVehicleInfo() { return vehicleInfo.get(); }
    public void setVehicleInfo(String v) { vehicleInfo.set(v); }
    public StringProperty vehicleInfoProperty() { return vehicleInfo; }

    public String getRegistrationNumber() { return registrationNumber.get(); }
    public void setRegistrationNumber(String v) { registrationNumber.set(v); }
    public StringProperty registrationNumberProperty() { return registrationNumber; }

    public String getProvider() { return provider.get(); }
    public void setProvider(String v) { provider.set(v); }
    public StringProperty providerProperty() { return provider; }

    public String getPolicyNumber() { return policyNumber.get(); }
    public void setPolicyNumber(String v) { policyNumber.set(v); }
    public StringProperty policyNumberProperty() { return policyNumber; }

    public String getStartDate() { return startDate.get(); }
    public void setStartDate(String v) { startDate.set(v); }
    public StringProperty startDateProperty() { return startDate; }

    public String getExpiryDate() { return expiryDate.get(); }
    public void setExpiryDate(String v) { expiryDate.set(v); }
    public StringProperty expiryDateProperty() { return expiryDate; }

    public String getCoverageType() { return coverageType.get(); }
    public void setCoverageType(String v) { coverageType.set(v); }
    public StringProperty coverageTypeProperty() { return coverageType; }

    public double getCoverageAmount() { return coverageAmount.get(); }
    public void setCoverageAmount(double v) { coverageAmount.set(v); }
    public DoubleProperty coverageAmountProperty() { return coverageAmount; }

    public double getPremium() { return premium.get(); }
    public void setPremium(double v) { premium.set(v); }
    public DoubleProperty premiumProperty() { return premium; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { status.set(v); }
    public StringProperty statusProperty() { return status; }

    @Override
    public String getDisplayInfo() {
        return String.format("[Insurance] %s | %s | %s", getRegistrationNumber(), getProvider(), getStatus());
    }
}