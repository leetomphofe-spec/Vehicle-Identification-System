package com.vis.controller;

import com.vis.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Collectors;

public class VehicleDetailPopupController {

    @FXML private Label regLabel;
    @FXML private Label statusBadge;
    @FXML private Label yearMakeModelLabel;
    @FXML private Label descriptionLabel;
    @FXML private ImageView primaryImageView;
    @FXML private StackPane imageContainer;

    @FXML private Label violationCountValue;
    @FXML private Label unpaidFinesValue;
    @FXML private Label paidFinesValue;
    @FXML private Label totalFinesValue;
    @FXML private Label lastServiceValue;
    @FXML private Label nextServiceValue;
    @FXML private Label serviceCountValue;
    @FXML private Label reportCountValue;
    @FXML private Label insuranceStatusValue;

    @FXML private Label engineValue;
    @FXML private Label transmissionValue;
    @FXML private Label fuelTypeValue;
    @FXML private Label mileageValue;

    @FXML private TabPane tabPane;
    @FXML private GridPane basicInfoGrid;
    @FXML private TextArea descriptionTextArea;
    @FXML private Button saveDescBtn;
    @FXML private GridPane techSpecsGrid;
    @FXML private TextField engineField;
    @FXML private TextField transmissionField;
    @FXML private TextField fuelTypeField;
    @FXML private TextField mileageField;
    @FXML private Button saveSpecsBtn;

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

    @FXML private GridPane insuranceGrid;
    @FXML private FlowPane thumbnailPane;
    @FXML private Button uploadPhotoBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button qrBtn;
    @FXML private Button printBtn;
    @FXML private Button closeBtn;

    private Vehicle vehicle;
    private String userRole = "ADMIN";
    private Runnable onDataChanged;
    private Stage stage;

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private VehicleImageDAO imageDAO = new VehicleImageDAO();
    private WorkshopDAO workshopDAO = new WorkshopDAO();
    private PoliceDAO policeDAO = new PoliceDAO();

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole != null ? userRole : "ADMIN";
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        setupTables();
        setupRoleBasedVisibility();
    }

    public void loadVehicleData() {
        if (vehicle == null) return;

        // Header
        regLabel.setText(vehicle.getRegistrationNumber());
        statusBadge.setText(vehicle.getStatus() != null ? vehicle.getStatus() : "Active");
        statusBadge.setStyle(getStatusStyle(vehicle.getStatus()));
        yearMakeModelLabel.setText(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel());

        // Description
        String descriptionText = (vehicle.getDescription() != null && !vehicle.getDescription().isEmpty())
                ? vehicle.getDescription() : "No description provided";
        descriptionLabel.setText(descriptionText);
        descriptionTextArea.setText(descriptionText);

        // Images
        loadPrimaryImage();
        loadThumbnails();

        // Technical Specs
        engineValue.setText((vehicle.getEngine() != null && !vehicle.getEngine().isEmpty()) ? vehicle.getEngine() : "Not specified");
        transmissionValue.setText((vehicle.getTransmission() != null && !vehicle.getTransmission().isEmpty()) ? vehicle.getTransmission() : "Not specified");
        fuelTypeValue.setText((vehicle.getFuelType() != null && !vehicle.getFuelType().isEmpty()) ? vehicle.getFuelType() : "Not specified");
        mileageValue.setText(vehicle.getMileage() > 0 ? String.format("%,d km", vehicle.getMileage()) : "Not recorded");

        engineField.setText(vehicle.getEngine());
        transmissionField.setText(vehicle.getTransmission());
        fuelTypeField.setText(vehicle.getFuelType());
        mileageField.setText(vehicle.getMileage() > 0 ? String.valueOf(vehicle.getMileage()) : "");

        // Load all data
        loadQuickStats();
        loadServiceHistory();
        loadViolations();
        loadPoliceReports();
        loadInsuranceInfo();
        loadBasicInfo();
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

        // Color rows for violations
        violationTable.setRowFactory(tv -> {
            TableRow<Violation> row = new TableRow<>();
            row.itemProperty().addListener((obs, old, newVal) -> {
                if (newVal != null && "Unpaid".equalsIgnoreCase(newVal.getStatus())) {
                    row.setStyle("-fx-background-color: #7f1d1d;");
                } else if (newVal != null && "Paid".equalsIgnoreCase(newVal.getStatus())) {
                    row.setStyle("-fx-background-color: #14532d;");
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });
    }

    private void setupRoleBasedVisibility() {
        if (userRole == null) return;
        boolean canEdit = userRole.equals("ADMIN") || userRole.equals("CUSTOMER");
        boolean isAdmin = userRole.equals("ADMIN");
        boolean isPolice = userRole.equals("POLICE");

        if (saveDescBtn != null) saveDescBtn.setVisible(canEdit);
        if (saveSpecsBtn != null) saveSpecsBtn.setVisible(canEdit);
        if (uploadPhotoBtn != null) uploadPhotoBtn.setVisible(canEdit);
        if (editBtn != null) editBtn.setVisible(canEdit);
        if (deleteBtn != null) deleteBtn.setVisible(isAdmin);
        if (qrBtn != null) qrBtn.setVisible(isPolice);

        if (descriptionTextArea != null) descriptionTextArea.setEditable(canEdit);
        if (engineField != null) engineField.setEditable(canEdit);
        if (transmissionField != null) transmissionField.setEditable(canEdit);
        if (fuelTypeField != null) fuelTypeField.setEditable(canEdit);
        if (mileageField != null) mileageField.setEditable(canEdit);
    }

    private void loadPrimaryImage() {
        if (vehicle.getPrimaryImagePath() != null && !vehicle.getPrimaryImagePath().isEmpty()) {
            try {
                File imageFile = new File(vehicle.getPrimaryImagePath());
                if (imageFile.exists()) {
                    Image img = new Image(imageFile.toURI().toString(), 320, 240, true, true);
                    primaryImageView.setImage(img);
                } else {
                    primaryImageView.setImage(null);
                }
            } catch (Exception e) {
                primaryImageView.setImage(null);
            }
        } else {
            primaryImageView.setImage(null);
        }
    }

    private void loadThumbnails() {
        thumbnailPane.getChildren().clear();
        try {
            var images = imageDAO.getImagesForVehicle(vehicle.getId());
            for (VehicleImage img : images) {
                VBox thumbBox = new VBox(5);
                thumbBox.setAlignment(javafx.geometry.Pos.CENTER);
                thumbBox.setStyle("-fx-cursor: hand;");
                ImageView thumb = new ImageView();
                thumb.setFitWidth(70);
                thumb.setFitHeight(60);
                thumb.setPreserveRatio(true);
                try {
                    File f = new File(img.getImagePath());
                    if (f.exists()) {
                        thumb.setImage(new Image(f.toURI().toString(), 70, 60, true, true));
                    }
                } catch (Exception e) {}
                if (img.getIsPrimary()) {
                    Label primaryBadge = new Label("★ PRIMARY");
                    primaryBadge.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 9px; -fx-font-weight: bold;");
                    thumbBox.getChildren().add(primaryBadge);
                }
                thumbBox.getChildren().add(thumb);
                thumbBox.setOnMouseClicked(e -> {
                    try {
                        File fullFile = new File(img.getImagePath());
                        if (fullFile.exists()) {
                            Image fullImg = new Image(fullFile.toURI().toString(), 320, 240, true, true);
                            primaryImageView.setImage(fullImg);
                        }
                    } catch (Exception ex) {}
                });
                thumbnailPane.getChildren().add(thumbBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadQuickStats() {
        try {
            var allViolations = policeDAO.getAllViolations();
            var vehicleViolations = allViolations.stream()
                    .filter(v -> v.getVehicleId() == vehicle.getId())
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
                    .filter(s -> s.getVehicleId() == vehicle.getId())
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
                    .filter(r -> r.getVehicleId() == vehicle.getId())
                    .count();
            reportCountValue.setText(String.valueOf(reportCount));
            insuranceStatusValue.setText("Active");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadServiceHistory() {
        try {
            var services = workshopDAO.getAllServiceRecords();
            var filtered = services.stream()
                    .filter(s -> s.getVehicleId() == vehicle.getId())
                    .collect(Collectors.toList());
            serviceTable.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadViolations() {
        try {
            var violations = policeDAO.getAllViolations();
            var filtered = violations.stream()
                    .filter(v -> v.getVehicleId() == vehicle.getId())
                    .collect(Collectors.toList());
            violationTable.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPoliceReports() {
        try {
            var reports = policeDAO.getAllReports();
            var filtered = reports.stream()
                    .filter(r -> r.getVehicleId() == vehicle.getId())
                    .collect(Collectors.toList());
            reportTable.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadInsuranceInfo() {
        insuranceGrid.getChildren().clear();
        addInsuranceRow("Insurance Provider:", "Lesotho Insurance Corporation", 0);
        addInsuranceRow("Policy Number:", "LIC-VIS-" + vehicle.getId() + "-2024", 1);
        addInsuranceRow("Start Date:", "2024-01-01", 2);
        addInsuranceRow("Expiry Date:", "2025-01-01", 3);
        addInsuranceRow("Coverage Type:", "Comprehensive", 4);
        addInsuranceRow("Coverage Amount:", "M500,000", 5);
        addInsuranceRow("Premium:", "M5,500 per year", 6);
    }

    private void addInsuranceRow(String label, String value, int row) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px; -fx-font-weight: bold;");
        insuranceGrid.add(lbl, 0, row);
        insuranceGrid.add(val, 1, row);
    }

    private void loadBasicInfo() {
        basicInfoGrid.getChildren().clear();
        addBasicInfoRow("Registration Number:", vehicle.getRegistrationNumber(), 0);
        addBasicInfoRow("Make:", vehicle.getMake(), 1);
        addBasicInfoRow("Model:", vehicle.getModel(), 2);
        addBasicInfoRow("Year:", String.valueOf(vehicle.getYear()), 3);
        addBasicInfoRow("Color:", (vehicle.getColor() != null && !vehicle.getColor().isEmpty()) ? vehicle.getColor() : "Not specified", 4);
        addBasicInfoRow("Status:", vehicle.getStatus() != null ? vehicle.getStatus() : "Active", 5);
        addBasicInfoRow("Owner:", vehicle.getOwnerName() != null ? vehicle.getOwnerName() : "Unknown", 6);
    }

    private void addBasicInfoRow(String label, String value, int row) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        Label val = new Label(value != null ? value : "N/A");
        val.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px; -fx-font-weight: bold;");
        basicInfoGrid.add(lbl, 0, row);
        basicInfoGrid.add(val, 1, row);
    }

    private String getStatusStyle(String status) {
        if (status == null) return "-fx-background-color: #64748b; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 12px;";
        switch (status.toLowerCase()) {
            case "active": return "-fx-background-color: #22c55e; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 12px;";
            case "stolen": return "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 12px;";
            case "recovered": return "-fx-background-color: #eab308; -fx-text-fill: black; -fx-background-radius: 12; -fx-font-size: 12px;";
            default: return "-fx-background-color: #64748b; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 12px;";
        }
    }

    @FXML private void handleSaveDescription() {
        try {
            vehicle.setDescription(descriptionTextArea.getText());
            vehicleDAO.updateVehicle(vehicle);
            descriptionLabel.setText(descriptionTextArea.getText());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Description updated");
            if (onDataChanged != null) onDataChanged.run();
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update: " + ex.getMessage());
        }
    }

    @FXML private void handleSaveTechnicalSpecs() {
        try {
            vehicle.setEngine(engineField.getText());
            vehicle.setTransmission(transmissionField.getText());
            vehicle.setFuelType(fuelTypeField.getText());
            int mileage = 0;
            if (!mileageField.getText().trim().isEmpty()) {
                mileage = Integer.parseInt(mileageField.getText());
            }
            vehicle.setMileage(mileage);
            vehicleDAO.updateVehicle(vehicle);

            engineValue.setText(vehicle.getEngine().isEmpty() ? "Not specified" : vehicle.getEngine());
            transmissionValue.setText(vehicle.getTransmission().isEmpty() ? "Not specified" : vehicle.getTransmission());
            fuelTypeValue.setText(vehicle.getFuelType().isEmpty() ? "Not specified" : vehicle.getFuelType());
            mileageValue.setText(vehicle.getMileage() > 0 ? String.format("%,d km", vehicle.getMileage()) : "Not recorded");

            showAlert(Alert.AlertType.INFORMATION, "Success", "Technical specifications updated");
            if (onDataChanged != null) onDataChanged.run();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Mileage must be a number");
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + ex.getMessage());
        }
    }

    @FXML private void handleUploadPhoto() {
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
                String fileName = "vehicle_" + vehicle.getId() + "_" + System.currentTimeMillis() + extension;
                File destFile = new File(imagesDir, fileName);
                java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                boolean isFirstImage = thumbnailPane.getChildren().isEmpty();
                VehicleImage newImage = new VehicleImage(0, vehicle.getId(),
                        destFile.getAbsolutePath(), fileName, isFirstImage, LocalDate.now().toString());
                imageDAO.addVehicleImage(newImage);
                if (isFirstImage) {
                    vehicle.setPrimaryImagePath(destFile.getAbsolutePath());
                    vehicleDAO.updateVehicle(vehicle);
                    loadPrimaryImage();
                }
                loadThumbnails();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Photo uploaded successfully!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Upload Error", "Failed to upload image: " + e.getMessage());
            }
        }
    }

    @FXML private void handleEditVehicle() {
        showAlert(Alert.AlertType.INFORMATION, "Edit", "Edit functionality would open here");
    }

    @FXML private void handleDeleteVehicle() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Vehicle");
        confirm.setContentText("Are you sure you want to delete vehicle " + vehicle.getRegistrationNumber() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    vehicleDAO.hardDeleteVehicle(vehicle.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Vehicle deleted successfully");
                    if (onDataChanged != null) onDataChanged.run();
                    stage.close();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                }
            }
        });
    }

    @FXML private void handleGenerateQR() {
        showAlert(Alert.AlertType.INFORMATION, "QR Code", "QR Code generation would open here");
    }

    @FXML private void handlePrintReport() {
        showAlert(Alert.AlertType.INFORMATION, "Print", "Print functionality would generate a PDF report");
    }

    @FXML private void handleClose() {
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