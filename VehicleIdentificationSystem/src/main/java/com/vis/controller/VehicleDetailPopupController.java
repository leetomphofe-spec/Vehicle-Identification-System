package com.vis.controller;

import com.vis.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Collectors;

public class VehicleDetailPopupController {

    // Header
    @FXML private Label regLabel;
    @FXML private Label statusBadge;
    @FXML private Label yearMakeModelLabel;
    @FXML private Label descriptionLabel;
    @FXML private Button closeBtn;

    // Left Panel - Vehicle List
    @FXML private ListView<Vehicle> vehicleListView;

    // Images
    @FXML private ImageView primaryImageView;
    @FXML private StackPane imageContainer;
    @FXML private Label noImageLabel;
    @FXML private FlowPane thumbnailPane;
    @FXML private Button uploadPhotoBtn;

    // Technical Specifications Display
    @FXML private Label engineValue;
    @FXML private Label transmissionValue;
    @FXML private Label fuelTypeValue;
    @FXML private Label mileageValue;

    // Technical Specifications Edit Fields
    @FXML private TextField engineField;
    @FXML private TextField transmissionField;
    @FXML private TextField fuelTypeField;
    @FXML private TextField mileageField;
    @FXML private Button saveSpecsBtn;

    // Basic Info
    @FXML private GridPane basicInfoGrid;
    @FXML private TextArea descriptionTextArea;
    @FXML private Button saveDescBtn;

    // Tables
    @FXML private TableView<ServiceRecord> serviceTable;
    @FXML private TableColumn<ServiceRecord, Integer> sIdCol;
    @FXML private TableColumn<ServiceRecord, String> sDateCol;
    @FXML private TableColumn<ServiceRecord, String> sTypeCol;
    @FXML private TableColumn<ServiceRecord, String> sDescCol;
    @FXML private TableColumn<ServiceRecord, Double> sCostCol;

    @FXML private TableView<Violation> violationTable;
    @FXML private TableColumn<Violation, Integer> vIdCol;
    @FXML private TableColumn<Violation, String> vDateCol;
    @FXML private TableColumn<Violation, String> vTypeCol;
    @FXML private TableColumn<Violation, Double> vFineCol;
    @FXML private TableColumn<Violation, String> vStatusCol;

    @FXML private TableView<PoliceReport> reportTable;
    @FXML private TableColumn<PoliceReport, Integer> rIdCol;
    @FXML private TableColumn<PoliceReport, String> rDateCol;
    @FXML private TableColumn<PoliceReport, String> rTypeCol;
    @FXML private TableColumn<PoliceReport, String> rDescCol;
    @FXML private TableColumn<PoliceReport, String> rOfficerCol;

    // Insurance
    @FXML private GridPane insuranceGrid;

    // Quick Stats
    @FXML private Label violationCountValue;
    @FXML private Label unpaidFinesValue;
    @FXML private Label paidFinesValue;
    @FXML private Label totalFinesValue;
    @FXML private Label lastServiceValue;
    @FXML private Label nextServiceValue;
    @FXML private Label serviceCountValue;
    @FXML private Label reportCountValue;
    @FXML private Label insuranceStatusValue;

    // Footer Buttons
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button qrBtn;
    @FXML private Button printBtn;

    @FXML private TabPane tabPane;

    private Vehicle currentVehicle;
    private String userRole = "ADMIN";
    private Runnable onDataChanged;
    private Stage stage;

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private VehicleImageDAO imageDAO = new VehicleImageDAO();
    private WorkshopDAO workshopDAO = new WorkshopDAO();
    private PoliceDAO policeDAO = new PoliceDAO();
    private InsuranceDAO insuranceDAO = new InsuranceDAO();

    private ObservableList<Vehicle> allVehicles = FXCollections.observableArrayList();

    public void setVehicle(Vehicle vehicle) {
        this.currentVehicle = vehicle;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole != null ? userRole : "ADMIN";
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        // Enable maximize/minimize buttons
        if (stage != null) {
            stage.setResizable(true);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
        }
    }

    @FXML
    public void initialize() {
        setupTables();
        setupVehicleListView();
        setupEditFieldValidations();
        applyRoleBasedAccess();
    }

    /**
     * Setup validations for edit fields
     */
    private void setupEditFieldValidations() {
        // Engine validation - letters, numbers, spaces, dots, hyphens
        engineField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                engineField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }
            if (!newVal.matches("[a-zA-Z0-9\\s\\.-]*")) {
                engineField.setText(oldVal);
                return;
            }
            if (newVal.length() > 50) {
                engineField.setText(oldVal);
                showAlert(Alert.AlertType.WARNING, "Validation", "Engine info cannot exceed 50 characters.");
                return;
            }
            engineField.setStyle("-fx-border-color: #22c55e;");
        });

        // Transmission validation - letters, numbers, spaces, hyphens
        transmissionField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                transmissionField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }
            if (!newVal.matches("[a-zA-Z0-9\\s-]*")) {
                transmissionField.setText(oldVal);
                return;
            }
            if (newVal.length() > 40) {
                transmissionField.setText(oldVal);
                showAlert(Alert.AlertType.WARNING, "Validation", "Transmission info cannot exceed 40 characters.");
                return;
            }
            transmissionField.setStyle("-fx-border-color: #22c55e;");
        });

        // Fuel Type validation - letters only
        fuelTypeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                fuelTypeField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }
            if (!newVal.matches("[a-zA-Z]*")) {
                fuelTypeField.setText(oldVal);
                return;
            }
            if (newVal.length() > 20) {
                fuelTypeField.setText(oldVal);
                showAlert(Alert.AlertType.WARNING, "Validation", "Fuel type cannot exceed 20 characters.");
                return;
            }
            fuelTypeField.setStyle("-fx-border-color: #22c55e;");
        });

        // Mileage validation - numbers only
        mileageField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                mileageField.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }
            if (!newVal.matches("\\d*")) {
                mileageField.setText(oldVal);
                return;
            }
            if (newVal.length() > 7) {
                mileageField.setText(oldVal);
                showAlert(Alert.AlertType.WARNING, "Validation", "Mileage cannot exceed 7 digits.");
                return;
            }
            mileageField.setStyle("-fx-border-color: #22c55e;");
        });

        // Description validation
        descriptionTextArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                descriptionTextArea.setStyle("-fx-border-color: #cbd5e1;");
                return;
            }
            if (newVal.length() > 1000) {
                descriptionTextArea.setText(oldVal);
                showAlert(Alert.AlertType.WARNING, "Validation", "Description cannot exceed 1000 characters.");
                return;
            }
            descriptionTextArea.setStyle("-fx-border-color: #22c55e;");
        });
    }

    private void setupVehicleListView() {
        loadAllVehicles();

        vehicleListView.setCellFactory(param -> new ListCell<Vehicle>() {
            @Override
            protected void updateItem(Vehicle vehicle, boolean empty) {
                super.updateItem(vehicle, empty);
                if (empty || vehicle == null) {
                    setText(null);
                } else {
                    setText(vehicle.getRegistrationNumber() + " - " + vehicle.getMake() + " " + vehicle.getModel());
                }
            }
        });

        vehicleListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentVehicle = newVal;
                loadVehicleData();
                if (onDataChanged != null) onDataChanged.run();
            }
        });
    }

    private void loadAllVehicles() {
        try {
            allVehicles.clear();
            allVehicles.addAll(vehicleDAO.getAllVehicles());
            vehicleListView.setItems(allVehicles);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load vehicles: " + e.getMessage());
        }
    }

    public void loadVehicleData() {
        if (currentVehicle == null) return;

        System.out.println("Loading vehicle data for ID: " + currentVehicle.getId() + " | Role: " + userRole);

        vehicleListView.getSelectionModel().select(currentVehicle);

        // Header
        regLabel.setText(currentVehicle.getRegistrationNumber());
        String status = currentVehicle.getStatus() != null ? currentVehicle.getStatus() : "Active";
        statusBadge.setText(status);
        statusBadge.setStyle(getStatusStyle(status));
        yearMakeModelLabel.setText(currentVehicle.getYear() + " " + currentVehicle.getMake() + " " + currentVehicle.getModel());

        // Description
        String descriptionText = (currentVehicle.getDescription() != null && !currentVehicle.getDescription().isEmpty())
                ? currentVehicle.getDescription() : "No description provided";
        descriptionLabel.setText(descriptionText);
        descriptionTextArea.setText(descriptionText);

        // Images
        loadPrimaryImage();
        loadThumbnails();

        // Technical Specs - Display values
        String engine = (currentVehicle.getEngine() != null && !currentVehicle.getEngine().isEmpty()) ? currentVehicle.getEngine() : "Not specified";
        String transmission = (currentVehicle.getTransmission() != null && !currentVehicle.getTransmission().isEmpty()) ? currentVehicle.getTransmission() : "Not specified";
        String fuelType = (currentVehicle.getFuelType() != null && !currentVehicle.getFuelType().isEmpty()) ? currentVehicle.getFuelType() : "Not specified";
        String mileage = currentVehicle.getMileage() > 0 ? String.format("%,d km", currentVehicle.getMileage()) : "Not recorded";

        engineValue.setText(engine);
        transmissionValue.setText(transmission);
        fuelTypeValue.setText(fuelType);
        mileageValue.setText(mileage);

        // Technical Specs - Edit fields
        engineField.setText(currentVehicle.getEngine());
        transmissionField.setText(currentVehicle.getTransmission());
        fuelTypeField.setText(currentVehicle.getFuelType());
        mileageField.setText(currentVehicle.getMileage() > 0 ? String.valueOf(currentVehicle.getMileage()) : "");

        // Load all data
        loadBasicInfo();
        loadQuickStats();
        loadServiceHistory();
        loadViolations();
        loadPoliceReports();
        loadInsuranceFromDatabase();

        // Apply role-based UI restrictions
        applyRoleBasedUI();

        System.out.println("Vehicle data loaded successfully");
    }

    private void applyRoleBasedUI() {
        boolean isAdmin = "ADMIN".equals(userRole);
        boolean isPolice = "POLICE".equals(userRole);
        boolean isUser = "USER".equals(userRole);

        if (isPolice || isUser) {
            // Hide edit/save buttons
            if (saveDescBtn != null) saveDescBtn.setVisible(false);
            if (saveSpecsBtn != null) saveSpecsBtn.setVisible(false);
            if (uploadPhotoBtn != null) uploadPhotoBtn.setVisible(false);
            if (editBtn != null) editBtn.setVisible(false);
            if (deleteBtn != null) deleteBtn.setVisible(false);

            if (isPolice) {
                if (qrBtn != null) qrBtn.setVisible(true);
                if (printBtn != null) printBtn.setVisible(true);
            } else {
                if (qrBtn != null) qrBtn.setVisible(false);
                if (printBtn != null) printBtn.setVisible(false);
            }

            // Make edit fields read-only
            if (descriptionTextArea != null) {
                descriptionTextArea.setEditable(false);
                descriptionTextArea.setStyle("-fx-background-color: #f8fafc; -fx-text-fill: #64748b; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
            }

            String readOnlyStyle = "-fx-background-color: #f8fafc; -fx-text-fill: #64748b; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-padding: 8;";
            if (engineField != null) engineField.setStyle(readOnlyStyle);
            if (transmissionField != null) transmissionField.setStyle(readOnlyStyle);
            if (fuelTypeField != null) fuelTypeField.setStyle(readOnlyStyle);
            if (mileageField != null) mileageField.setStyle(readOnlyStyle);
        }
    }

    private void applyRoleBasedAccess() {
        boolean isAdmin = "ADMIN".equals(userRole);

        if (isAdmin) {
            if (saveDescBtn != null) saveDescBtn.setVisible(true);
            if (saveSpecsBtn != null) saveSpecsBtn.setVisible(true);
            if (uploadPhotoBtn != null) uploadPhotoBtn.setVisible(true);
            if (editBtn != null) editBtn.setVisible(true);
            if (deleteBtn != null) deleteBtn.setVisible(true);
            if (qrBtn != null) qrBtn.setVisible(true);
            if (printBtn != null) printBtn.setVisible(true);

            if (descriptionTextArea != null) descriptionTextArea.setEditable(true);
            if (engineField != null) engineField.setEditable(true);
            if (transmissionField != null) transmissionField.setEditable(true);
            if (fuelTypeField != null) fuelTypeField.setEditable(true);
            if (mileageField != null) mileageField.setEditable(true);
        }
    }

    private void loadInsuranceFromDatabase() {
        try {
            Insurance insurance = insuranceDAO.getInsuranceByVehicleId(currentVehicle.getId());

            if (insurance != null) {
                updateInsuranceGrid(insurance);
                System.out.println("Insurance loaded for vehicle: " + currentVehicle.getRegistrationNumber());
            } else {
                showNoInsuranceMessage();
                System.out.println("No insurance found for vehicle: " + currentVehicle.getRegistrationNumber());
            }
        } catch (SQLException e) {
            System.err.println("Error loading insurance: " + e.getMessage());
            showNoInsuranceMessage();
        }
    }

    private void updateInsuranceGrid(Insurance insurance) {
        insuranceGrid.getChildren().clear();

        String startDate = insurance.getStartDate();
        String expiryDate = insurance.getExpiryDate();

        String statusText = insurance.getStatus();
        String statusColor = "#3b82f6";

        try {
            LocalDate expiry = LocalDate.parse(expiryDate);
            LocalDate today = LocalDate.now();
            long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, expiry);

            if (daysRemaining < 0) {
                statusText = "EXPIRED";
                statusColor = "#ef4444";
            } else if (daysRemaining < 30) {
                statusText = "EXPIRING SOON (" + daysRemaining + " days)";
                statusColor = "#f97316";
            } else {
                statusText = insurance.getStatus() + " (" + daysRemaining + " days left)";
                statusColor = "#3b82f6";
            }
        } catch (Exception e) {
            // Use default status
        }

        String[][] insuranceData = {
                {"Insurance Provider:", insurance.getProvider()},
                {"Policy Number:", insurance.getPolicyNumber()},
                {"Start Date:", startDate},
                {"Expiry Date:", expiryDate},
                {"Coverage Type:", insurance.getCoverageType()},
                {"Coverage Amount:", String.format("M%,.2f", insurance.getCoverageAmount())},
                {"Premium:", String.format("M%,.2f per year", insurance.getPremium())},
                {"Status:", statusText}
        };

        for (int i = 0; i < insuranceData.length; i++) {
            Label lbl = new Label(insuranceData[i][0]);
            lbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-font-weight: bold;");

            Label val = new Label(insuranceData[i][1]);
            if (insuranceData[i][0].equals("Status:")) {
                val.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else {
                val.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 14px;");
            }
            insuranceGrid.add(lbl, 0, i);
            insuranceGrid.add(val, 1, i);
        }

        if (insuranceStatusValue != null) {
            insuranceStatusValue.setText(insurance.getStatus());
            if ("Active".equalsIgnoreCase(insurance.getStatus())) {
                insuranceStatusValue.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else {
                insuranceStatusValue.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 14px; -fx-font-weight: bold;");
            }
        }
    }

    private void showNoInsuranceMessage() {
        insuranceGrid.getChildren().clear();

        Label noInsuranceLabel = new Label("No insurance policy found for this vehicle.");
        noInsuranceLabel.setStyle("-fx-text-fill: #f97316; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label addInsuranceLabel = new Label("Go to Insurance tab in Dashboard to add a policy.");
        addInsuranceLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");

        insuranceGrid.add(noInsuranceLabel, 0, 0);
        insuranceGrid.add(addInsuranceLabel, 0, 1);

        if (insuranceStatusValue != null) {
            insuranceStatusValue.setText("No Insurance");
            insuranceStatusValue.setStyle("-fx-text-fill: #f97316; -fx-font-size: 14px; -fx-font-weight: bold;");
        }
    }

    private void setupTables() {
        // Service Table
        sIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        sDateCol.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        sTypeCol.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        sDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        sCostCol.setCellValueFactory(new PropertyValueFactory<>("cost"));

        // Violation Table
        vIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        vDateCol.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
        vTypeCol.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        vFineCol.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        vStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Police Report Table
        rIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        rDateCol.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        rTypeCol.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        rDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        rOfficerCol.setCellValueFactory(new PropertyValueFactory<>("officerName"));

        // Color rows for violations - using subtle colors
        violationTable.setRowFactory(tv -> {
            TableRow<Violation> row = new TableRow<>();
            row.itemProperty().addListener((obs, old, newVal) -> {
                if (newVal != null) {
                    if ("Unpaid".equalsIgnoreCase(newVal.getStatus())) {
                        row.setStyle("-fx-background-color: #fef3c7;"); // Soft amber
                    } else if ("Paid".equalsIgnoreCase(newVal.getStatus())) {
                        row.setStyle("-fx-background-color: #dbeafe;"); // Soft blue
                    } else {
                        row.setStyle("");
                    }
                }
            });
            return row;
        });
    }

    private void loadPrimaryImage() {
        if (currentVehicle.getPrimaryImagePath() != null && !currentVehicle.getPrimaryImagePath().isEmpty()) {
            try {
                File imageFile = new File(currentVehicle.getPrimaryImagePath());
                if (imageFile.exists()) {
                    Image img = new Image(imageFile.toURI().toString(), 280, 220, true, true);
                    primaryImageView.setImage(img);
                    if (noImageLabel != null) noImageLabel.setVisible(false);
                } else {
                    primaryImageView.setImage(null);
                    if (noImageLabel != null) noImageLabel.setVisible(true);
                }
            } catch (Exception e) {
                primaryImageView.setImage(null);
                if (noImageLabel != null) noImageLabel.setVisible(true);
            }
        } else {
            primaryImageView.setImage(null);
            if (noImageLabel != null) noImageLabel.setVisible(true);
        }
    }

    private void loadThumbnails() {
        thumbnailPane.getChildren().clear();
        try {
            var images = imageDAO.getImagesForVehicle(currentVehicle.getId());
            for (VehicleImage img : images) {
                VBox thumbBox = new VBox(5);
                thumbBox.setAlignment(javafx.geometry.Pos.CENTER);
                thumbBox.setStyle("-fx-cursor: hand; -fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 4;");

                ImageView thumb = new ImageView();
                thumb.setFitWidth(90);
                thumb.setFitHeight(80);
                thumb.setPreserveRatio(true);

                try {
                    File f = new File(img.getImagePath());
                    if (f.exists()) {
                        thumb.setImage(new Image(f.toURI().toString(), 90, 80, true, true));
                    }
                } catch (Exception e) {}

                if (img.getIsPrimary()) {
                    Label primaryBadge = new Label("PRIMARY");
                    primaryBadge.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 9px; -fx-font-weight: bold;");
                    thumbBox.getChildren().add(primaryBadge);
                }
                thumbBox.getChildren().add(thumb);

                final String imagePath = img.getImagePath();
                thumbBox.setOnMouseClicked(e -> {
                    try {
                        File fullFile = new File(imagePath);
                        if (fullFile.exists()) {
                            Image fullImg = new Image(fullFile.toURI().toString(), 280, 220, true, true);
                            primaryImageView.setImage(fullImg);
                            if (noImageLabel != null) noImageLabel.setVisible(false);
                        }
                    } catch (Exception ex) {}
                });
                thumbnailPane.getChildren().add(thumbBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBasicInfo() {
        basicInfoGrid.getChildren().clear();

        int row = 0;
        addBasicInfoRow("Registration Number:", currentVehicle.getRegistrationNumber(), row++);
        addBasicInfoRow("Make:", currentVehicle.getMake(), row++);
        addBasicInfoRow("Model:", currentVehicle.getModel(), row++);
        addBasicInfoRow("Year:", String.valueOf(currentVehicle.getYear()), row++);
        addBasicInfoRow("Color:", (currentVehicle.getColor() != null && !currentVehicle.getColor().isEmpty()) ? currentVehicle.getColor() : "Not specified", row++);
        addBasicInfoRow("Status:", currentVehicle.getStatus() != null ? currentVehicle.getStatus() : "Active", row++);
        addBasicInfoRow("Owner ID:", String.valueOf(currentVehicle.getOwnerId()), row++);
        addBasicInfoRow("Owner Name:", currentVehicle.getOwnerName() != null ? currentVehicle.getOwnerName() : "Unknown", row++);
    }

    private void addBasicInfoRow(String label, String value, int row) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label val = new Label(value != null ? value : "N/A");
        val.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 14px;");

        basicInfoGrid.add(lbl, 0, row);
        basicInfoGrid.add(val, 1, row);
    }

    private void loadQuickStats() {
        try {
            var allViolations = policeDAO.getAllViolations();
            var vehicleViolations = allViolations.stream()
                    .filter(v -> v.getVehicleId() == currentVehicle.getId())
                    .collect(Collectors.toList());

            int totalCount = vehicleViolations.size();
            double unpaidTotal = vehicleViolations.stream()
                    .filter(v -> "Unpaid".equalsIgnoreCase(v.getStatus()))
                    .mapToDouble(Violation::getFineAmount)
                    .sum();
            double paidTotal = vehicleViolations.stream()
                    .filter(v -> "Paid".equalsIgnoreCase(v.getStatus()))
                    .mapToDouble(Violation::getFineAmount)
                    .sum();
            double allTotal = unpaidTotal + paidTotal;

            violationCountValue.setText(String.valueOf(totalCount));
            unpaidFinesValue.setText(String.format("M%.2f", unpaidTotal));
            paidFinesValue.setText(String.format("M%.2f", paidTotal));
            totalFinesValue.setText(String.format("M%.2f", allTotal));

            var allServices = workshopDAO.getAllServiceRecords();
            var vehicleServices = allServices.stream()
                    .filter(s -> s.getVehicleId() == currentVehicle.getId())
                    .sorted(Comparator.comparing(ServiceRecord::getServiceDate).reversed())
                    .collect(Collectors.toList());

            serviceCountValue.setText(String.valueOf(vehicleServices.size()));

            if (!vehicleServices.isEmpty()) {
                ServiceRecord latest = vehicleServices.get(0);
                lastServiceValue.setText(latest.getServiceDate());
                try {
                    LocalDate lastDate = LocalDate.parse(latest.getServiceDate());
                    LocalDate nextDate = lastDate.plusMonths(6);
                    nextServiceValue.setText(nextDate.toString());
                } catch (Exception e) {
                    nextServiceValue.setText("Not calculated");
                }
            } else {
                lastServiceValue.setText("Never");
                nextServiceValue.setText("N/A");
            }

            var allReports = policeDAO.getAllReports();
            long reportCount = allReports.stream()
                    .filter(r -> r.getVehicleId() == currentVehicle.getId())
                    .count();
            reportCountValue.setText(String.valueOf(reportCount));

        } catch (SQLException e) {
            e.printStackTrace();
            violationCountValue.setText("0");
            unpaidFinesValue.setText("M0.00");
            paidFinesValue.setText("M0.00");
            totalFinesValue.setText("M0.00");
            serviceCountValue.setText("0");
            lastServiceValue.setText("N/A");
            nextServiceValue.setText("N/A");
            reportCountValue.setText("0");
        }
    }

    private void loadServiceHistory() {
        try {
            var services = workshopDAO.getAllServiceRecords();
            var filtered = services.stream()
                    .filter(s -> s.getVehicleId() == currentVehicle.getId())
                    .collect(Collectors.toList());
            serviceTable.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) {
            e.printStackTrace();
            serviceTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void loadViolations() {
        try {
            var violations = policeDAO.getAllViolations();
            var filtered = violations.stream()
                    .filter(v -> v.getVehicleId() == currentVehicle.getId())
                    .collect(Collectors.toList());
            violationTable.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) {
            e.printStackTrace();
            violationTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void loadPoliceReports() {
        try {
            var reports = policeDAO.getAllReports();
            var filtered = reports.stream()
                    .filter(r -> r.getVehicleId() == currentVehicle.getId())
                    .collect(Collectors.toList());
            reportTable.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) {
            e.printStackTrace();
            reportTable.setItems(FXCollections.observableArrayList());
        }
    }

    private String getStatusStyle(String status) {
        if (status == null) return "-fx-background-color: #e2e8f0; -fx-text-fill: #64748b; -fx-background-radius: 20; -fx-font-size: 12px; -fx-padding: 5 14 5 14; -fx-font-weight: bold;";
        switch (status.toLowerCase()) {
            case "active":
                return "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 12px; -fx-padding: 5 14 5 14; -fx-font-weight: bold;";
            case "stolen":
                return "-fx-background-color: #f97316; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 12px; -fx-padding: 5 14 5 14; -fx-font-weight: bold;";
            case "recovered":
                return "-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 12px; -fx-padding: 5 14 5 14; -fx-font-weight: bold;";
            default:
                return "-fx-background-color: #e2e8f0; -fx-text-fill: #64748b; -fx-background-radius: 20; -fx-font-size: 12px; -fx-padding: 5 14 5 14; -fx-font-weight: bold;";
        }
    }

    @FXML
    private void handleSaveDescription() {
        if (!"ADMIN".equals(userRole)) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can edit descriptions.");
            return;
        }

        String description = descriptionTextArea.getText().trim();
        if (description.length() > 1000) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Description cannot exceed 1000 characters.");
            return;
        }

        try {
            currentVehicle.setDescription(description);
            vehicleDAO.updateVehicle(currentVehicle);
            descriptionLabel.setText(description);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Description updated");
            if (onDataChanged != null) onDataChanged.run();
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update: " + ex.getMessage());
        }
    }

    @FXML
    private void handleSaveTechnicalSpecs() {
        if (!"ADMIN".equals(userRole)) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can edit technical specifications.");
            return;
        }

        try {
            String engine = engineField.getText().trim();
            String transmission = transmissionField.getText().trim();
            String fuelType = fuelTypeField.getText().trim();

            // Validate engine
            if (!engine.isEmpty() && !engine.matches("[a-zA-Z0-9\\s\\.-]*")) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Engine contains invalid characters.");
                return;
            }

            // Validate transmission
            if (!transmission.isEmpty() && !transmission.matches("[a-zA-Z0-9\\s-]*")) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Transmission contains invalid characters.");
                return;
            }

            // Validate fuel type
            if (!fuelType.isEmpty() && !fuelType.matches("[a-zA-Z]*")) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Fuel type must contain only letters.");
                return;
            }

            currentVehicle.setEngine(engine);
            currentVehicle.setTransmission(transmission);
            currentVehicle.setFuelType(fuelType);

            int mileage = 0;
            if (!mileageField.getText().trim().isEmpty()) {
                String mileageText = mileageField.getText().trim();
                if (!mileageText.matches("\\d+")) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Mileage must contain only numbers.");
                    return;
                }
                mileage = Integer.parseInt(mileageText);
                if (mileage > 9999999) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Mileage cannot exceed 9,999,999 km.");
                    return;
                }
            }
            currentVehicle.setMileage(mileage);
            vehicleDAO.updateVehicle(currentVehicle);

            engineValue.setText(engine.isEmpty() ? "Not specified" : engine);
            transmissionValue.setText(transmission.isEmpty() ? "Not specified" : transmission);
            fuelTypeValue.setText(fuelType.isEmpty() ? "Not specified" : fuelType);
            mileageValue.setText(mileage > 0 ? String.format("%,d km", mileage) : "Not recorded");

            showAlert(Alert.AlertType.INFORMATION, "Success", "Technical specifications updated");
            if (onDataChanged != null) onDataChanged.run();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Mileage must be a number");
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + ex.getMessage());
        }
    }

    @FXML
    private void handleUploadPhoto() {
        if (!"ADMIN".equals(userRole)) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can upload photos.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Vehicle Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                File imagesDir = new File("vehicle_images");
                if (!imagesDir.exists()) imagesDir.mkdirs();
                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String fileName = "vehicle_" + currentVehicle.getId() + "_" + System.currentTimeMillis() + extension;
                File destFile = new File(imagesDir, fileName);
                java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                boolean isFirstImage = thumbnailPane.getChildren().isEmpty();
                VehicleImage newImage = new VehicleImage(0, currentVehicle.getId(),
                        destFile.getAbsolutePath(), fileName, isFirstImage, LocalDate.now().toString());
                imageDAO.addVehicleImage(newImage);
                if (isFirstImage) {
                    currentVehicle.setPrimaryImagePath(destFile.getAbsolutePath());
                    vehicleDAO.updateVehicle(currentVehicle);
                    loadPrimaryImage();
                }
                loadThumbnails();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Photo uploaded successfully!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Upload Error", "Failed to upload image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleEditVehicle() {
        if (!"ADMIN".equals(userRole)) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can edit vehicles.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/EditVehicle.fxml"));
            Parent root = loader.load();

            EditVehicleController controller = loader.getController();
            controller.setVehicle(currentVehicle);
            controller.setOnVehicleUpdated(() -> {
                loadVehicleData();
                if (onDataChanged != null) onDataChanged.run();
            });

            Scene scene = new Scene(root, 550, 650);
            scene.getStylesheets().add(getClass().getResource("/com/vis/css/styles.css").toExternalForm());

            Stage editStage = new Stage();
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.setTitle("Edit Vehicle - " + currentVehicle.getRegistrationNumber());
            editStage.setScene(scene);
            editStage.setResizable(true);  // Allow maximize/minimize
            editStage.setMinWidth(550);
            editStage.setMinHeight(650);
            controller.setStage(editStage);  // Pass stage reference
            editStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open edit form: " + e.getMessage());
        }
    }
    @FXML
    private void handleDeleteVehicle() {
        if (!"ADMIN".equals(userRole)) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can delete vehicles.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Vehicle");
        confirm.setContentText("Are you sure you want to delete vehicle " + currentVehicle.getRegistrationNumber() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    vehicleDAO.hardDeleteVehicle(currentVehicle.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Vehicle deleted successfully");
                    if (onDataChanged != null) onDataChanged.run();
                    stage.close();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleGenerateQR() {
        if (!"ADMIN".equals(userRole) && !"POLICE".equals(userRole)) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN and POLICE can generate QR codes.");
            return;
        }

        String qrData = "Vehicle: " + currentVehicle.getRegistrationNumber() + "\n" +
                "Make: " + currentVehicle.getMake() + "\n" +
                "Model: " + currentVehicle.getModel() + "\n" +
                "Year: " + currentVehicle.getYear() + "\n" +
                "Owner: " + currentVehicle.getOwnerName();
        showAlert(Alert.AlertType.INFORMATION, "QR Code", "QR Code would be generated with:\n\n" + qrData);
    }

    @FXML
    private void handlePrintReport() {
        if (!"ADMIN".equals(userRole) && !"POLICE".equals(userRole)) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN and POLICE can print reports.");
            return;
        }

        StringBuilder report = new StringBuilder();
        report.append("VEHICLE IDENTIFICATION SYSTEM REPORT\n");
        report.append("=====================================\n\n");
        report.append("Registration: ").append(currentVehicle.getRegistrationNumber()).append("\n");
        report.append("Make/Model: ").append(currentVehicle.getMake()).append(" ").append(currentVehicle.getModel()).append("\n");
        report.append("Year: ").append(currentVehicle.getYear()).append("\n");
        report.append("Color: ").append(currentVehicle.getColor() != null ? currentVehicle.getColor() : "Not specified").append("\n");
        report.append("Status: ").append(currentVehicle.getStatus()).append("\n");
        report.append("Owner: ").append(currentVehicle.getOwnerName() != null ? currentVehicle.getOwnerName() : "Unknown").append("\n");
        report.append("\n--- Technical Specifications ---\n");
        report.append("Engine: ").append(currentVehicle.getEngine() != null ? currentVehicle.getEngine() : "N/A").append("\n");
        report.append("Transmission: ").append(currentVehicle.getTransmission() != null ? currentVehicle.getTransmission() : "N/A").append("\n");
        report.append("Fuel Type: ").append(currentVehicle.getFuelType() != null ? currentVehicle.getFuelType() : "N/A").append("\n");
        report.append("Mileage: ").append(currentVehicle.getMileage() > 0 ? currentVehicle.getMileage() + " km" : "Not recorded").append("\n");

        showAlert(Alert.AlertType.INFORMATION, "Print Report", "Report would be printed:\n\n" + report.toString());
    }

    @FXML
    private void handleClose() {
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}