package com.vis.model;

import javafx.beans.property.*;

/**
 * PoliceReport model - extends BaseEntity (Inheritance).
 */
public class PoliceReport extends BaseEntity {

    private final IntegerProperty vehicleId     = new SimpleIntegerProperty();
    private final StringProperty  reportDate    = new SimpleStringProperty();
    private final StringProperty  reportType    = new SimpleStringProperty();
    private final StringProperty  description   = new SimpleStringProperty();
    private final StringProperty  officerName   = new SimpleStringProperty();
    private final StringProperty  regNumber     = new SimpleStringProperty();

    public PoliceReport() { super(); }

    public PoliceReport(int id, int vehicleId, String reportDate,
                        String reportType, String description,
                        String officerName, String regNumber) {
        super(id);
        setVehicleId(vehicleId);
        setReportDate(reportDate);
        setReportType(reportType);
        setDescription(description);
        setOfficerName(officerName);
        setRegNumber(regNumber);
    }

    public IntegerProperty vehicleIdProperty()   { return vehicleId; }
    public StringProperty  reportDateProperty()  { return reportDate; }
    public StringProperty  reportTypeProperty()  { return reportType; }
    public StringProperty  descriptionProperty() { return description; }
    public StringProperty  officerNameProperty() { return officerName; }
    public StringProperty  regNumberProperty()   { return regNumber; }

    public int    getVehicleId()   { return vehicleId.get(); }
    public String getReportDate()  { return reportDate.get(); }
    public String getReportType()  { return reportType.get(); }
    public String getDescription() { return description.get(); }
    public String getOfficerName() { return officerName.get(); }
    public String getRegNumber()   { return regNumber.get(); }

    public void setVehicleId(int v)    { vehicleId.set(v); }
    public void setReportDate(String v)  { reportDate.set(v); }
    public void setReportType(String v)  { reportType.set(v); }
    public void setDescription(String v) { description.set(v); }
    public void setOfficerName(String v) { officerName.set(v); }
    public void setRegNumber(String v)   { regNumber.set(v); }

    @Override
    public String getDisplayInfo() {
        return String.format("[PoliceReport] %s | %s | %s | %s",
                getRegNumber(), getReportDate(), getReportType(), getOfficerName());
    }
}
