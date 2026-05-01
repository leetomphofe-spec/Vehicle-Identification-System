package com.vis.controller;

import com.vis.model.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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

    // Dashboard labels
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
    @FXML private Tab dashboardTab;
    @FXML private Tab vehiclesTab;
    @FXML private Tab workshopTab;
    @FXML private Tab policeTab;
    @FXML private Tab customersTab;

    // Vehicle table components
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, Integer> vIdCol;
    @FXML private TableColumn<Vehicle, String> vRegCol;
    @FXML private TableColumn<Vehicle, String> vMakeCol;
    @FXML private TableColumn<Vehicle, String> vModelCol;
    @FXML private TableColumn<Vehicle, Integer> vYearCol;
    @FXML private TableColumn<Vehicle, String> vOwnerCol;
    @FXML private TextField searchRegField;

    // Vehicle buttons
    @FXML private Button searchBtn;
    @FXML private Button refreshVehiclesBtn;
    @FXML private Button addVehicleBtn;
    @FXML private Button deleteVehicleBtn;

    // Service table components
    @FXML private TableView<ServiceRecord> serviceTable;
    @FXML private TableColumn<ServiceRecord, Integer> sIdCol;
    @FXML private TableColumn<ServiceRecord, String> sRegCol;
    @FXML private TableColumn<ServiceRecord, String> sDateCol;
    @FXML private TableColumn<ServiceRecord, String> sTypeCol;
    @FXML private TableColumn<ServiceRecord, String> sDescCol;
    @FXML private TableColumn<ServiceRecord, Double> sCostCol;

    // Service buttons
    @FXML private Button refreshServiceBtn;
    @FXML private Button addServiceBtn;
    @FXML private Button deleteServiceBtn;

    // Police report components
    @FXML private TableView<PoliceReport> reportTable;
    @FXML private TableColumn<PoliceReport, Integer> prIdCol;
    @FXML private TableColumn<PoliceReport, String> prRegCol;
    @FXML private TableColumn<PoliceReport, String> prDateCol;
    @FXML private TableColumn<PoliceReport, String> prTypeCol;
    @FXML private TableColumn<PoliceReport, String> prDescCol;
    @FXML private TableColumn<PoliceReport, String> prOfficerCol;

    // Report buttons
    @FXML private Button refreshReportsBtn;
    @FXML private Button addReportBtn;

    // Violation components
    @FXML private TableView<Violation> violationTable;
    @FXML private TableColumn<Violation, Integer> vlIdCol;
    @FXML private TableColumn<Violation, String> vlRegCol;
    @FXML private TableColumn<Violation, String> vlDateCol;
    @FXML private TableColumn<Violation, String> vlTypeCol;
    @FXML private TableColumn<Violation, Double> vlFineCol;
    @FXML private TableColumn<Violation, String> vlStatusCol;

    // Violation buttons
    @FXML private Button refreshViolationsBtn;
    @FXML private Button addViolationBtn;
    @FXML private Button markPaidBtn;

    // Customer components
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> cIdCol;
    @FXML private TableColumn<Customer, String> cNameCol;
    @FXML private TableColumn<Customer, String> cAddressCol;
    @FXML private TableColumn<Customer, String> cPhoneCol;
    @FXML private TableColumn<Customer, String> cEmailCol;

    // Customer buttons
    @FXML private Button refreshCustomersBtn;
    @FXML private Button addCustomerBtn;
    @FXML private Button deleteCustomerBtn;

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
        welcomeLabel.setText("Welcome, " + username + " (" + role + ")");
        applyRoleBasedAccess();
    }

    private void applyRoleBasedAccess() {
        System.out.println("Applying role-based access for: " + currentRole);

        // First disable all action buttons by default
        disableAllActionButtons();

        switch (currentRole) {
            case "ADMIN":
                enableAdminAccess();
                break;
            case "POLICE":
                enablePoliceAccess();
                break;
            case "USER":
            default:
                enableUserAccess();
                break;
        }
    }

    private void disableAllActionButtons() {
        // Vehicle buttons
        if (addVehicleBtn != null) addVehicleBtn.setDisable(true);
        if (deleteVehicleBtn != null) deleteVehicleBtn.setDisable(true);
        if (addVehicleBtn != null) addVehicleBtn.setVisible(false);
        if (deleteVehicleBtn != null) deleteVehicleBtn.setVisible(false);

        // Service buttons
        if (addServiceBtn != null) addServiceBtn.setDisable(true);
        if (deleteServiceBtn != null) deleteServiceBtn.setDisable(true);
        if (addServiceBtn != null) addServiceBtn.setVisible(false);
        if (deleteServiceBtn != null) deleteServiceBtn.setVisible(false);

        // Report buttons
        if (addReportBtn != null) addReportBtn.setDisable(true);
        if (addReportBtn != null) addReportBtn.setVisible(false);

        // Violation buttons
        if (addViolationBtn != null) addViolationBtn.setDisable(true);
        if (markPaidBtn != null) markPaidBtn.setDisable(true);
        if (addViolationBtn != null) addViolationBtn.setVisible(false);

        // Customer buttons
        if (addCustomerBtn != null) addCustomerBtn.setDisable(true);
        if (deleteCustomerBtn != null) deleteCustomerBtn.setDisable(true);
        if (addCustomerBtn != null) addCustomerBtn.setVisible(false);
        if (deleteCustomerBtn != null) deleteCustomerBtn.setVisible(false);
    }

    private void enableAdminAccess() {
        // Vehicle buttons - full access
        if (addVehicleBtn != null) {
            addVehicleBtn.setDisable(false);
            addVehicleBtn.setVisible(true);
        }
        if (deleteVehicleBtn != null) {
            deleteVehicleBtn.setDisable(false);
            deleteVehicleBtn.setVisible(true);
        }
        if (searchBtn != null) searchBtn.setDisable(false);
        if (refreshVehiclesBtn != null) refreshVehiclesBtn.setDisable(false);

        // Service buttons - full access
        if (addServiceBtn != null) {
            addServiceBtn.setDisable(false);
            addServiceBtn.setVisible(true);
        }
        if (deleteServiceBtn != null) {
            deleteServiceBtn.setDisable(false);
            deleteServiceBtn.setVisible(true);
        }
        if (refreshServiceBtn != null) refreshServiceBtn.setDisable(false);

        // Report buttons - full access
        if (addReportBtn != null) {
            addReportBtn.setDisable(false);
            addReportBtn.setVisible(true);
        }
        if (refreshReportsBtn != null) refreshReportsBtn.setDisable(false);

        // Violation buttons - full access
        if (addViolationBtn != null) {
            addViolationBtn.setDisable(false);
            addViolationBtn.setVisible(true);
        }
        if (markPaidBtn != null) {
            markPaidBtn.setDisable(false);
            markPaidBtn.setVisible(true);
        }
        if (refreshViolationsBtn != null) refreshViolationsBtn.setDisable(false);

        // Customer buttons - full access
        if (addCustomerBtn != null) {
            addCustomerBtn.setDisable(false);
            addCustomerBtn.setVisible(true);
        }
        if (deleteCustomerBtn != null) {
            deleteCustomerBtn.setDisable(false);
            deleteCustomerBtn.setVisible(true);
        }
        if (refreshCustomersBtn != null) refreshCustomersBtn.setDisable(false);
    }

    private void enablePoliceAccess() {
        // Vehicle buttons - view only
        if (addVehicleBtn != null) {
            addVehicleBtn.setDisable(true);
            addVehicleBtn.setVisible(false);
        }
        if (deleteVehicleBtn != null) {
            deleteVehicleBtn.setDisable(true);
            deleteVehicleBtn.setVisible(false);
        }
        if (searchBtn != null) searchBtn.setDisable(false);
        if (refreshVehiclesBtn != null) refreshVehiclesBtn.setDisable(false);

        // Service buttons - view only
        if (addServiceBtn != null) {
            addServiceBtn.setDisable(true);
            addServiceBtn.setVisible(false);
        }
        if (deleteServiceBtn != null) {
            deleteServiceBtn.setDisable(true);
            deleteServiceBtn.setVisible(false);
        }
        if (refreshServiceBtn != null) refreshServiceBtn.setDisable(false);

        // Report buttons - can add reports
        if (addReportBtn != null) {
            addReportBtn.setDisable(false);
            addReportBtn.setVisible(true);
        }
        if (refreshReportsBtn != null) refreshReportsBtn.setDisable(false);

        // Violation buttons - can mark paid only
        if (addViolationBtn != null) {
            addViolationBtn.setDisable(true);
            addViolationBtn.setVisible(false);
        }
        if (markPaidBtn != null) {
            markPaidBtn.setDisable(false);
            markPaidBtn.setVisible(true);
        }
        if (refreshViolationsBtn != null) refreshViolationsBtn.setDisable(false);

        // Customer buttons - view only
        if (addCustomerBtn != null) {
            addCustomerBtn.setDisable(true);
            addCustomerBtn.setVisible(false);
        }
        if (deleteCustomerBtn != null) {
            deleteCustomerBtn.setDisable(true);
            deleteCustomerBtn.setVisible(false);
        }
        if (refreshCustomersBtn != null) refreshCustomersBtn.setDisable(false);
    }

    private void enableUserAccess() {
        // Vehicle buttons - view only
        if (addVehicleBtn != null) {
            addVehicleBtn.setDisable(true);
            addVehicleBtn.setVisible(false);
        }
        if (deleteVehicleBtn != null) {
            deleteVehicleBtn.setDisable(true);
            deleteVehicleBtn.setVisible(false);
        }
        if (searchBtn != null) searchBtn.setDisable(false);
        if (refreshVehiclesBtn != null) refreshVehiclesBtn.setDisable(false);

        // Service buttons - view only
        if (addServiceBtn != null) {
            addServiceBtn.setDisable(true);
            addServiceBtn.setVisible(false);
        }
        if (deleteServiceBtn != null) {
            deleteServiceBtn.setDisable(true);
            deleteServiceBtn.setVisible(false);
        }
        if (refreshServiceBtn != null) refreshServiceBtn.setDisable(false);

        // Report buttons - view only
        if (addReportBtn != null) {
            addReportBtn.setDisable(true);
            addReportBtn.setVisible(false);
        }
        if (refreshReportsBtn != null) refreshReportsBtn.setDisable(false);

        // Violation buttons - view only
        if (addViolationBtn != null) {
            addViolationBtn.setDisable(true);
            addViolationBtn.setVisible(false);
        }
        if (markPaidBtn != null) {
            markPaidBtn.setDisable(true);
            markPaidBtn.setVisible(false);
        }
        if (refreshViolationsBtn != null) refreshViolationsBtn.setDisable(false);

        // Customer buttons - view only
        if (addCustomerBtn != null) {
            addCustomerBtn.setDisable(true);
            addCustomerBtn.setVisible(false);
        }
        if (deleteCustomerBtn != null) {
            deleteCustomerBtn.setDisable(true);
            deleteCustomerBtn.setVisible(false);
        }
        if (refreshCustomersBtn != null) refreshCustomersBtn.setDisable(false);
    }

    private void setupVehicleTable() {
        vIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        vRegCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        vMakeCol.setCellValueFactory(new PropertyValueFactory<>("make"));
        vModelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        vYearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        vOwnerCol.setCellValueFactory(new PropertyValueFactory<>("ownerName"));

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
    }

    private void setupPoliceTable() {
        prIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        prRegCol.setCellValueFactory(new PropertyValueFactory<>("regNumber"));
        prDateCol.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        prTypeCol.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        prDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        prOfficerCol.setCellValueFactory(new PropertyValueFactory<>("officerName"));
    }

    private void setupViolationTable() {
        vlIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        vlRegCol.setCellValueFactory(new PropertyValueFactory<>("regNumber"));
        vlDateCol.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
        vlTypeCol.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        vlFineCol.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        vlStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Color rows based on status
        violationTable.setRowFactory(tv -> {
            TableRow<Violation> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    if ("Unpaid".equalsIgnoreCase(newVal.getStatus())) {
                        row.setStyle("-fx-background-color: #7f1d1d;");
                    } else if ("Paid".equalsIgnoreCase(newVal.getStatus())) {
                        row.setStyle("-fx-background-color: #14532d;");
                    } else {
                        row.setStyle("");
                    }
                }
            });
            return row;
        });
    }

    private void setupCustomerTable() {
        cIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        cNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        cAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        cPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        cEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void openVehicleFromServiceRecord(ServiceRecord service) {
        try {
            Vehicle vehicle = vehicleDAO.getVehicleById(service.getVehicleId());
            if (vehicle != null) {
                openVehicleDetailPopup(vehicle);
            } else {
                showAlert(Alert.AlertType.WARNING, "Vehicle Not Found", "Could not find vehicle details.");
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
                showAlert(Alert.AlertType.WARNING, "Vehicle Not Found", "Could not find vehicle details.");
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
                showAlert(Alert.AlertType.WARNING, "Vehicle Not Found", "Could not find vehicle details.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load vehicle: " + e.getMessage());
        }
    }

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

    private void setupActivityLog() {
        String[] activities = {
                "System started", "Admin logged in", "Vehicle A 123 LS checked",
                "Police report filed for B 456 LS", "Service record added for C 789 LS",
                "Customer John Mokoena updated", "Violation recorded for D 321 LS",
                "Violation E 654 LS marked Paid", "Vehicle F 987 LS registered",
                "Theft report filed for G 111 LS", "Oil change logged for H 222 LS",
                "New customer Sara Mofolo added", "Database sync completed",
                "Officer Sgt. Mosotho filed report", "Vehicle I 333 LS searched",
                "Speeding violation for J 444 LS", "Brake service for A 123 LS",
                "Insurance query logged", "Pagination loaded",
                "System health check passed", "Battery replaced on E 654 LS",
                "Customer David Letsie queried", "Accident report closed"
        };
        for (String act : activities) {
            Label lbl = new Label(act);
            lbl.getStyleClass().add("activity-item");
            lbl.setMaxWidth(Double.MAX_VALUE);
            activityLogBox.getChildren().add(lbl);
        }
    }

    private void setupDateTime() {
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            dateTimeLabel.setText(now);
        }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void setupPagination() {
        pagination.setPageCount(5);
        pagination.currentPageIndexProperty().addListener((obs, old, nv) ->
                setStatus("Page " + (nv.intValue() + 1) + " of 5"));
    }

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
        ObservableList<Vehicle> demo = FXCollections.observableArrayList(
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

    @FXML
    public void handleSearchVehicle() {
        String reg = searchRegField.getText().trim();
        if (reg.isEmpty()) {
            loadVehicles();
            setStatus("Showing all vehicles");
            return;
        }
        try {
            Vehicle v = vehicleDAO.searchByRegistration(reg);
            if (v != null) {
                vehicleTable.setItems(FXCollections.observableArrayList(v));
                setStatus("Found: " + v.getRegistrationNumber());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Not Found", "No vehicle found with registration: " + reg);
                vehicleTable.setItems(FXCollections.observableArrayList());
                setStatus("No vehicle found with registration: " + reg);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            setStatus("Search error: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteVehicle() {
        if (!currentRole.equals("ADMIN")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can delete vehicles.");
            return;
        }

        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a vehicle to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Vehicle");
        confirm.setContentText("Are you sure you want to delete vehicle: " + selected.getRegistrationNumber() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    vehicleDAO.hardDeleteVehicle(selected.getId());
                    loadVehicles();
                    setStatus("Vehicle deleted: " + selected.getRegistrationNumber());
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle deleted successfully.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    public void handleDeleteCustomer() {
        if (!currentRole.equals("ADMIN")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can delete customers.");
            return;
        }

        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Customer");
        confirm.setContentText("Are you sure you want to delete customer: " + selected.getName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    customerDAO.deleteCustomer(selected.getId());
                    loadCustomers();
                    setStatus("Customer deleted: " + selected.getName());
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    public void handleDeleteService() {
        if (!currentRole.equals("ADMIN")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can delete service records.");
            return;
        }

        ServiceRecord selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a service record to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Service Record");
        confirm.setContentText("Delete service record for vehicle: " + selected.getRegNumber() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    workshopDAO.deleteServiceRecord(selected.getId());
                    loadServiceRecords();
                    setStatus("Service record deleted.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    public void handleMarkPaid() {
        if (!currentRole.equals("ADMIN") && !currentRole.equals("POLICE")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN and POLICE can mark violations as paid.");
            return;
        }

        Violation selected = violationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a violation to mark as paid.");
            return;
        }

        if ("Paid".equalsIgnoreCase(selected.getStatus())) {
            showAlert(Alert.AlertType.INFORMATION, "Already Paid", "This violation is already marked as paid.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Mark as Paid");
        confirm.setHeaderText("Mark Violation as Paid");
        confirm.setContentText("Mark violation #" + selected.getId() + " for vehicle " + selected.getRegNumber() + " as paid?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    policeDAO.markViolationPaid(selected.getId());
                    loadViolations();
                    setStatus("Violation marked as Paid.");
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Violation marked as paid.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    public void openAddViolationDialog() {
        if (!currentRole.equals("ADMIN")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can add violations.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/AddViolation.fxml"));
            Parent root = loader.load();

            AddViolationController controller = loader.getController();
            controller.setOnViolationAdded(() -> {
                loadViolations();
                setStatus("Violation added successfully");
            });

            Scene scene = new Scene(root, 450, 380);
            scene.getStylesheets().add(getClass().getResource("/com/vis/css/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Add Violation");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadViolations();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open add violation form: " + e.getMessage());
        }
    }

    @FXML
    public void openAddVehicleDialog() {
        if (!currentRole.equals("ADMIN")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can add vehicles.");
            return;
        }
        openDialog("/com/vis/fxml/AddVehicle.fxml", "Add Vehicle", 550, 650);
        loadVehicles();
    }

    @FXML
    public void openAddServiceDialog() {
        if (!currentRole.equals("ADMIN")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can add service records.");
            return;
        }
        openDialog("/com/vis/fxml/AddService.fxml", "Add Service Record", 430, 380);
        loadServiceRecords();
    }

    @FXML
    public void openAddReportDialog() {
        if (!currentRole.equals("ADMIN") && !currentRole.equals("POLICE")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN and POLICE can add police reports.");
            return;
        }
        openDialog("/com/vis/fxml/AddReport.fxml", "Add Police Report", 430, 380);
        loadPoliceReports();
    }

    @FXML
    public void openAddCustomerDialog() {
        if (!currentRole.equals("ADMIN")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can add customers.");
            return;
        }
        openDialog("/com/vis/fxml/AddCustomer.fxml", "Add Customer", 420, 340);
        loadCustomers();
    }

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
                "Vehicle Identification System v1.0\nLimkokwing University Lesotho\nObject Oriented Programming II\nBuilt with JavaFX + PostgreSQL");
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