package com.vis.controller;

import com.vis.model.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.stage.*;
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    // --- Dashboard ---
    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label totalVehiclesLabel;
    @FXML private Label totalCustomersLabel;
    @FXML private Label totalReportsLabel;
    @FXML private Label totalViolationsLabel;
    @FXML private ProgressBar progressBar;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private VBox activityLogBox;
    @FXML private Pagination pagination;
    @FXML private TabPane mainTabPane;

    // --- Vehicle Table ---
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, Integer> vIdCol;
    @FXML private TableColumn<Vehicle, String> vRegCol, vMakeCol, vModelCol, vOwnerCol;
    @FXML private TableColumn<Vehicle, Integer> vYearCol;
    @FXML private TextField searchRegField;

    // --- Service Table (Workshop) ---
    @FXML private TableView<ServiceRecord> serviceTable;
    @FXML private TableColumn<ServiceRecord, Integer> sIdCol;
    @FXML private TableColumn<ServiceRecord, String> sRegCol, sDateCol, sTypeCol, sDescCol;
    @FXML private TableColumn<ServiceRecord, Double> sCostCol;

    // --- Police Report Table ---
    @FXML private TableView<PoliceReport> reportTable;
    @FXML private TableColumn<PoliceReport, Integer> prIdCol;
    @FXML private TableColumn<PoliceReport, String> prRegCol, prDateCol, prTypeCol, prDescCol, prOfficerCol;

    // --- Violation Table ---
    @FXML private TableView<Violation> violationTable;
    @FXML private TableColumn<Violation, Integer> vlIdCol;
    @FXML private TableColumn<Violation, String> vlRegCol, vlDateCol, vlTypeCol, vlStatusCol;
    @FXML private TableColumn<Violation, Double> vlFineCol;

    // --- Customer Table ---
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> cIdCol;
    @FXML private TableColumn<Customer, String> cNameCol, cAddressCol, cPhoneCol, cEmailCol;

    // DAOs
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final PoliceDAO policeDAO = new PoliceDAO();
    private final WorkshopDAO workshopDAO = new WorkshopDAO();

    private String currentUser = "Admin";
    private String currentRole = "ADMIN";

    @FXML
    public void initialize() {
        setupVehicleTable();
        setupServiceTable();
        setupPoliceTable();
        setupViolationTable();
        setupCustomerTable();
        setupActivityLog();
        setupDateTime();
        setupPagination();
        setupProgressAnimation();
        loadAllData();
    }

    public void initData(String username, String role) {
        this.currentUser = username;
        this.currentRole = role;
        welcomeLabel.setText("Welcome, " + username + " [" + role + "]");
    }

    // ---- TABLE SETUP ----

    private void setupVehicleTable() {
        vIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        vRegCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        vMakeCol.setCellValueFactory(new PropertyValueFactory<>("make"));
        vModelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        vYearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        vOwnerCol.setCellValueFactory(new PropertyValueFactory<>("ownerName"));

        // DOUBLE-CLICK HANDLER FOR VEHICLE TABLE
        vehicleTable.setRowFactory(tv -> {
            TableRow<Vehicle> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Vehicle selectedVehicle = row.getItem();
                    if (selectedVehicle != null) {
                        openVehicleDetailPopup(selectedVehicle);
                    }
                }
            });
            return row;
        });
    }

    private void setupServiceTable() {
        sIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        sRegCol.setCellValueFactory(new PropertyValueFactory<>("regNumber"));
        sDateCol.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        sTypeCol.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        sDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        sCostCol.setCellValueFactory(new PropertyValueFactory<>("cost"));

        // DOUBLE-CLICK HANDLER FOR SERVICE TABLE
        serviceTable.setRowFactory(tv -> {
            TableRow<ServiceRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ServiceRecord selectedService = row.getItem();
                    if (selectedService != null) {
                        openVehicleFromServiceRecord(selectedService);
                    }
                }
            });
            return row;
        });

        // Make registration column clickable as a link
        sRegCol.setCellFactory(column -> {
            TableCell<ServiceRecord, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-text-fill: #38bdf8; -fx-underline: true; -fx-cursor: hand;");
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                    ServiceRecord record = cell.getTableView().getItems().get(cell.getIndex());
                    if (record != null) {
                        openVehicleFromServiceRecord(record);
                    }
                }
            });
            return cell;
        });
    }

    private void setupPoliceTable() {
        prIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        prRegCol.setCellValueFactory(new PropertyValueFactory<>("regNumber"));
        prDateCol.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        prTypeCol.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        prDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        prOfficerCol.setCellValueFactory(new PropertyValueFactory<>("officerName"));

        // DOUBLE-CLICK HANDLER FOR POLICE REPORT TABLE
        reportTable.setRowFactory(tv -> {
            TableRow<PoliceReport> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    PoliceReport selectedReport = row.getItem();
                    if (selectedReport != null) {
                        openVehicleFromPoliceReport(selectedReport);
                    }
                }
            });
            return row;
        });

        // Make registration column clickable
        prRegCol.setCellFactory(column -> {
            TableCell<PoliceReport, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-text-fill: #38bdf8; -fx-underline: true; -fx-cursor: hand;");
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                    PoliceReport report = cell.getTableView().getItems().get(cell.getIndex());
                    if (report != null) {
                        openVehicleFromPoliceReport(report);
                    }
                }
            });
            return cell;
        });
    }

    private void setupViolationTable() {
        vlIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        vlRegCol.setCellValueFactory(new PropertyValueFactory<>("regNumber"));
        vlDateCol.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
        vlTypeCol.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        vlFineCol.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        vlStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // DOUBLE-CLICK HANDLER FOR VIOLATION TABLE
        violationTable.setRowFactory(tv -> {
            TableRow<Violation> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Violation selectedViolation = row.getItem();
                    if (selectedViolation != null) {
                        openVehicleFromViolation(selectedViolation);
                    }
                }
            });
            return row;
        });

        // Make registration column clickable
        vlRegCol.setCellFactory(column -> {
            TableCell<Violation, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-text-fill: #38bdf8; -fx-underline: true; -fx-cursor: hand;");
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                    Violation violation = cell.getTableView().getItems().get(cell.getIndex());
                    if (violation != null) {
                        openVehicleFromViolation(violation);
                    }
                }
            });
            return cell;
        });
    }

    private void setupCustomerTable() {
        cIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        cNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        cAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        cPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        cEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    // ---- HELPER METHODS TO OPEN VEHICLE DETAIL FROM DIFFERENT SOURCES ----

    private void openVehicleFromServiceRecord(ServiceRecord service) {
        try {
            Vehicle vehicle = vehicleDAO.getVehicleById(service.getVehicleId());
            if (vehicle != null) {
                openVehicleDetailPopup(vehicle);
            } else {
                showAlert(Alert.AlertType.WARNING, "Vehicle Not Found",
                        "Could not find vehicle details for this service record.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load vehicle: " + e.getMessage());
        }
    }

    private void openVehicleFromPoliceReport(PoliceReport report) {
        try {
            Vehicle vehicle = vehicleDAO.getVehicleById(report.getVehicleId());
            if (vehicle != null) {
                openVehicleDetailPopup(vehicle);
            } else {
                showAlert(Alert.AlertType.WARNING, "Vehicle Not Found",
                        "Could not find vehicle details for this police report.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load vehicle: " + e.getMessage());
        }
    }

    private void openVehicleFromViolation(Violation violation) {
        try {
            Vehicle vehicle = vehicleDAO.getVehicleById(violation.getVehicleId());
            if (vehicle != null) {
                openVehicleDetailPopup(vehicle);
            } else {
                showAlert(Alert.AlertType.WARNING, "Vehicle Not Found",
                        "Could not find vehicle details for this violation.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load vehicle: " + e.getMessage());
        }
    }

    // ---- MAIN METHOD TO OPEN VEHICLE DETAIL POPUP ----

    private void openVehicleDetailPopup(Vehicle vehicle) {
        try {
            VehicleDetailPopup detailPopup = new VehicleDetailPopup(
                    vehicle,
                    currentRole,
                    () -> {
                        loadVehicles();
                        loadServiceRecords();
                        loadPoliceReports();
                        loadViolations();
                    }
            );
            detailPopup.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open vehicle details: " + e.getMessage());
        }
    }

    // ---- ACTIVITY LOG ----
    private void setupActivityLog() {
        String[] activities = {
                "🔵 System started", "✅ Admin logged in", "🚗 Vehicle A 123 LS checked",
                "📋 Police report filed for B 456 LS", "🔧 Service record added for C 789 LS",
                "👤 Customer John Mokoena updated", "⚠️ Violation recorded for D 321 LS",
                "✅ Violation E 654 LS marked Paid", "🚗 Vehicle F 987 LS registered",
                "📋 Theft report filed for G 111 LS", "🔧 Oil change logged for H 222 LS",
                "👤 New customer Sara Mofolo added", "🔵 Database sync completed",
                "✅ Officer Sgt. Mosotho filed report", "🚗 Vehicle I 333 LS searched",
                "⚠️ Speeding violation for J 444 LS", "🔧 Brake service for A 123 LS",
                "📋 Insurance query logged", "🔵 Pagination loaded",
                "✅ System health check passed", "🔧 Battery replaced on E 654 LS",
                "👤 Customer David Letsie queried", "📋 Accident report closed"
        };
        for (String act : activities) {
            Label lbl = new Label(act);
            lbl.getStyleClass().add("activity-item");
            lbl.setMaxWidth(Double.MAX_VALUE);
            activityLogBox.getChildren().add(lbl);
        }
    }

    // ---- PAGINATION ----
    private void setupPagination() {
        pagination.setPageCount(5);
        pagination.currentPageIndexProperty().addListener((obs, old, nv) ->
                setStatus("Page " + (nv.intValue() + 1) + " of 5"));
    }

    // ---- PROGRESS ----
    private void setupProgressAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0.0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(progressBar.progressProperty(), 0.75))
        );
        timeline.play();

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#38bdf8"));
        glow.setRadius(10);
        progressIndicator.setEffect(glow);
    }

    // ---- DATE TIME ----
    private void setupDateTime() {
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            String now = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            dateTimeLabel.setText(now);
        }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    // ---- LOAD DATA ----
    private void loadAllData() {
        loadVehicles();
        loadServiceRecords();
        loadPoliceReports();
        loadViolations();
        loadCustomers();
    }

    @FXML
    public void refreshVehicles() {
        loadVehicles();
    }

    @FXML
    public void refreshServiceRecords() {
        loadServiceRecords();
    }

    @FXML
    public void refreshPoliceReports() {
        loadPoliceReports();
    }

    @FXML
    public void refreshViolations() {
        loadViolations();
    }

    @FXML
    public void refreshCustomers() {
        loadCustomers();
    }

    private void loadVehicles() {
        try {
            ObservableList<Vehicle> list = vehicleDAO.getAllVehicles();
            vehicleTable.setItems(list);
            totalVehiclesLabel.setText(String.valueOf(list.size()));
            setStatus("Vehicles loaded: " + list.size());
        } catch (SQLException e) {
            loadDemoVehicles();
        }
    }

    private void loadDemoVehicles() {
        ObservableList<Vehicle> demo = javafx.collections.FXCollections.observableArrayList(
                new Vehicle(1, "A 123 LS", "Toyota", "Corolla", 2018, 1),
                new Vehicle(2, "B 456 LS", "Honda", "Civic", 2020, 2),
                new Vehicle(3, "C 789 LS", "Ford", "Ranger", 2019, 3),
                new Vehicle(4, "D 321 LS", "Hyundai", "i20", 2021, 4),
                new Vehicle(5, "E 654 LS", "Nissan", "Navara", 2017, 5));
        demo.forEach(v -> v.setOwnerName("Demo Owner"));
        vehicleTable.setItems(demo);
        totalVehiclesLabel.setText(String.valueOf(demo.size()));
        setStatus("Demo mode - no DB connection");
    }

    private void loadServiceRecords() {
        try {
            ObservableList<ServiceRecord> records = workshopDAO.getAllServiceRecords();
            serviceTable.setItems(records);
            setStatus("Service records loaded: " + records.size());
        } catch (SQLException e) {
            setStatus("Workshop: " + e.getMessage());
        }
    }

    private void loadPoliceReports() {
        try {
            ObservableList<PoliceReport> rpts = policeDAO.getAllReports();
            reportTable.setItems(rpts);
            totalReportsLabel.setText(String.valueOf(rpts.size()));
        } catch (SQLException e) {
            setStatus("Police: " + e.getMessage());
        }
    }

    private void loadViolations() {
        try {
            ObservableList<Violation> vls = policeDAO.getAllViolations();
            violationTable.setItems(vls);
            totalViolationsLabel.setText(String.valueOf(vls.size()));
        } catch (SQLException e) {
            setStatus("Violations: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        try {
            ObservableList<Customer> list = customerDAO.getAllCustomers();
            customerTable.setItems(list);
            totalCustomersLabel.setText(String.valueOf(list.size()));
        } catch (SQLException e) {
            setStatus("Customers: " + e.getMessage());
        }
    }

    // ---- VEHICLE ACTIONS ----

    @FXML
    public void handleSearchVehicle() {
        String reg = searchRegField.getText().trim();
        if (reg.isEmpty()) {
            loadVehicles();
            return;
        }
        try {
            Vehicle v = vehicleDAO.searchByRegistration(reg);
            if (v != null) {
                vehicleTable.setItems(javafx.collections.FXCollections.observableArrayList(v));
                setStatus("Found: " + v.getDisplayInfo());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Not Found",
                        "No vehicle found with registration: " + reg);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "DB Error", e.getMessage());
        }
    }

    @FXML
    public void handleDeleteVehicle() {
        Vehicle sel = vehicleTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a vehicle first.");
            return;
        }
        try {
            vehicleDAO.deleteVehicle(sel.getId());
            loadVehicles();
            setStatus("Vehicle deleted: " + sel.getRegistrationNumber());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    public void openAddVehicleDialog() {
        openDialog("/com/vis/fxml/AddVehicle.fxml", "Add Vehicle", 550, 650);
        loadVehicles();
    }

    @FXML
    public void openAddServiceDialog() {
        openDialog("/com/vis/fxml/AddService.fxml", "Add Service Record", 430, 380);
        loadServiceRecords();
    }

    @FXML
    public void openAddReportDialog() {
        openDialog("/com/vis/fxml/AddReport.fxml", "Add Police Report", 430, 380);
        loadPoliceReports();
    }

    @FXML
    public void openAddCustomerDialog() {
        openDialog("/com/vis/fxml/AddCustomer.fxml", "Add Customer", 420, 340);
        loadCustomers();
    }

    @FXML
    public void handleDeleteCustomer() {
        Customer sel = customerTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a customer first.");
            return;
        }
        try {
            customerDAO.deleteCustomer(sel.getId());
            loadCustomers();
            setStatus("Customer deleted: " + sel.getName());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    public void handleDeleteService() {
        ServiceRecord sel = serviceTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a record first.");
            return;
        }
        try {
            workshopDAO.deleteServiceRecord(sel.getId());
            loadServiceRecords();
            setStatus("Service record deleted.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    public void handleMarkPaid() {
        Violation sel = violationTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a violation first.");
            return;
        }
        try {
            policeDAO.markViolationPaid(sel.getId());
            loadViolations();
            setStatus("Violation marked as Paid.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    // ---- MENU ACTIONS ----
    @FXML
    public void showDashboard() {
        mainTabPane.getSelectionModel().select(0);
    }

    @FXML
    public void showVehicles() {
        mainTabPane.getSelectionModel().select(1);
    }

    @FXML
    public void showAddVehicle() {
        openAddVehicleDialog();
    }

    @FXML
    public void showSearch() {
        mainTabPane.getSelectionModel().select(1);
        searchRegField.requestFocus();
    }

    @FXML
    public void showWorkshop() {
        mainTabPane.getSelectionModel().select(2);
    }

    @FXML
    public void showPolice() {
        mainTabPane.getSelectionModel().select(3);
    }

    @FXML
    public void showCustomers() {
        mainTabPane.getSelectionModel().select(4);
    }

    @FXML
    public void showAbout() {
        showAlert(Alert.AlertType.INFORMATION, "About VIS",
                "Vehicle Identification System v1.0\n" +
                        "Limkokwing University Lesotho\n" +
                        "Object Oriented Programming II\n" +
                        "Built with JavaFX + PostgreSQL");
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 500, 400);
            scene.getStylesheets().add(getClass().getResource("/com/vis/css/styles.css").toExternalForm());
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Vehicle Identification System");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handledoubleclickvehicle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/VehicleDetailPopup.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 500, 400);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Vehicle Identification System");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---- HELPERS ----

    private void openDialog(String fxmlPath, String title, int w, int h) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, w, h);
            scene.getStylesheets().add(getClass().getResource("/com/vis/css/styles.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error opening form", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

}