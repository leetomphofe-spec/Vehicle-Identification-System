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
import java.util.List;

public class DashboardController {

    // Dashboard labels
    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label totalVehiclesLabel;
    @FXML private Label totalCustomersLabel;
    @FXML private Label totalReportsLabel;
    @FXML private Label totalViolationsLabel;
    @FXML private Label totalInsuranceLabel;
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
    @FXML private Tab insuranceTab;

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

    // Insurance components
    @FXML private TableView<Insurance> insuranceTable;
    @FXML private TableColumn<Insurance, Integer> insIdCol;
    @FXML private TableColumn<Insurance, String> insRegCol;
    @FXML private TableColumn<Insurance, String> insVehicleCol;
    @FXML private TableColumn<Insurance, String> insProviderCol;
    @FXML private TableColumn<Insurance, String> insPolicyCol;
    @FXML private TableColumn<Insurance, String> insStartDateCol;
    @FXML private TableColumn<Insurance, String> insExpiryDateCol;
    @FXML private TableColumn<Insurance, String> insCoverageCol;
    @FXML private TableColumn<Insurance, Double> insAmountCol;
    @FXML private TableColumn<Insurance, String> insStatusCol;

    // Insurance buttons
    @FXML private Button refreshInsuranceBtn;
    @FXML private Button addInsuranceBtn;
    @FXML private Button editInsuranceBtn;
    @FXML private Button deleteInsuranceBtn;

    // Insurance statistics labels
    @FXML private Label activePoliciesLabel;
    @FXML private Label expiringPoliciesLabel;
    @FXML private Label expiredPoliciesLabel;
    @FXML private Label totalPremiumLabel;

    // DAOs
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final PoliceDAO policeDAO = new PoliceDAO();
    private final WorkshopDAO workshopDAO = new WorkshopDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();
    private final InsuranceDAO insuranceDAO = new InsuranceDAO();

    private String currentUser = "Admin";
    private String currentRole = "ADMIN";

    private ObservableList<String> allActivities = FXCollections.observableArrayList();
    private final int ITEMS_PER_PAGE = 8;

    @FXML
    public void initialize() {
        setupVehicleTable();
        setupServiceTable();
        setupPoliceTable();
        setupViolationTable();
        setupCustomerTable();
        setupInsuranceTable();
        setupDateTime();
        setupProgressAnimation();
        setupPagination();
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
        switch (currentRole) {
            case "ADMIN": enableAdminAccess(); break;
            case "POLICE": enablePoliceAccess(); break;
            default: enableUserAccess(); break;
        }
    }

    private void enableAdminAccess() {
        if (addVehicleBtn != null) { addVehicleBtn.setDisable(false); addVehicleBtn.setVisible(true); }
        if (deleteVehicleBtn != null) { deleteVehicleBtn.setDisable(false); deleteVehicleBtn.setVisible(true); }
        if (searchBtn != null) searchBtn.setDisable(false);
        if (refreshVehiclesBtn != null) refreshVehiclesBtn.setDisable(false);
        if (addServiceBtn != null) { addServiceBtn.setDisable(false); addServiceBtn.setVisible(true); }
        if (deleteServiceBtn != null) { deleteServiceBtn.setDisable(false); deleteServiceBtn.setVisible(true); }
        if (refreshServiceBtn != null) refreshServiceBtn.setDisable(false);
        if (addReportBtn != null) { addReportBtn.setDisable(false); addReportBtn.setVisible(true); }
        if (refreshReportsBtn != null) refreshReportsBtn.setDisable(false);
        if (addViolationBtn != null) { addViolationBtn.setDisable(false); addViolationBtn.setVisible(true); }
        if (markPaidBtn != null) { markPaidBtn.setDisable(false); markPaidBtn.setVisible(true); }
        if (refreshViolationsBtn != null) refreshViolationsBtn.setDisable(false);
        if (addCustomerBtn != null) { addCustomerBtn.setDisable(false); addCustomerBtn.setVisible(true); }
        if (deleteCustomerBtn != null) { deleteCustomerBtn.setDisable(false); deleteCustomerBtn.setVisible(true); }
        if (refreshCustomersBtn != null) refreshCustomersBtn.setDisable(false);
        if (addInsuranceBtn != null) { addInsuranceBtn.setDisable(false); addInsuranceBtn.setVisible(true); }
        if (editInsuranceBtn != null) { editInsuranceBtn.setDisable(false); editInsuranceBtn.setVisible(true); }
        if (deleteInsuranceBtn != null) { deleteInsuranceBtn.setDisable(false); deleteInsuranceBtn.setVisible(true); }
        if (refreshInsuranceBtn != null) refreshInsuranceBtn.setDisable(false);
    }

    private void enablePoliceAccess() {
        if (addVehicleBtn != null) { addVehicleBtn.setDisable(true); addVehicleBtn.setVisible(false); }
        if (deleteVehicleBtn != null) { deleteVehicleBtn.setDisable(true); deleteVehicleBtn.setVisible(false); }
        if (searchBtn != null) searchBtn.setDisable(false);
        if (refreshVehiclesBtn != null) refreshVehiclesBtn.setDisable(false);
        if (addServiceBtn != null) { addServiceBtn.setDisable(true); addServiceBtn.setVisible(false); }
        if (deleteServiceBtn != null) { deleteServiceBtn.setDisable(true); deleteServiceBtn.setVisible(false); }
        if (refreshServiceBtn != null) refreshServiceBtn.setDisable(false);
        if (addReportBtn != null) { addReportBtn.setDisable(false); addReportBtn.setVisible(true); }
        if (refreshReportsBtn != null) refreshReportsBtn.setDisable(false);
        if (addViolationBtn != null) { addViolationBtn.setDisable(false); addViolationBtn.setVisible(true); }
        if (markPaidBtn != null) { markPaidBtn.setDisable(false); markPaidBtn.setVisible(true); }
        if (refreshViolationsBtn != null) refreshViolationsBtn.setDisable(false);
        if (addCustomerBtn != null) { addCustomerBtn.setDisable(true); addCustomerBtn.setVisible(false); }
        if (deleteCustomerBtn != null) { deleteCustomerBtn.setDisable(true); deleteCustomerBtn.setVisible(false); }
        if (refreshCustomersBtn != null) refreshCustomersBtn.setDisable(false);
        if (addInsuranceBtn != null) { addInsuranceBtn.setDisable(true); addInsuranceBtn.setVisible(false); }
        if (editInsuranceBtn != null) { editInsuranceBtn.setDisable(true); editInsuranceBtn.setVisible(false); }
        if (deleteInsuranceBtn != null) { deleteInsuranceBtn.setDisable(true); deleteInsuranceBtn.setVisible(false); }
        if (refreshInsuranceBtn != null) refreshInsuranceBtn.setDisable(false);
    }

    private void enableUserAccess() {
        if (addVehicleBtn != null) { addVehicleBtn.setDisable(true); addVehicleBtn.setVisible(false); }
        if (deleteVehicleBtn != null) { deleteVehicleBtn.setDisable(true); deleteVehicleBtn.setVisible(false); }
        if (searchBtn != null) searchBtn.setDisable(false);
        if (refreshVehiclesBtn != null) refreshVehiclesBtn.setDisable(false);
        if (addServiceBtn != null) { addServiceBtn.setDisable(true); addServiceBtn.setVisible(false); }
        if (deleteServiceBtn != null) { deleteServiceBtn.setDisable(true); deleteServiceBtn.setVisible(false); }
        if (refreshServiceBtn != null) refreshServiceBtn.setDisable(false);
        if (addReportBtn != null) { addReportBtn.setDisable(true); addReportBtn.setVisible(false); }
        if (refreshReportsBtn != null) refreshReportsBtn.setDisable(false);
        if (addViolationBtn != null) { addViolationBtn.setDisable(true); addViolationBtn.setVisible(false); }
        if (markPaidBtn != null) { markPaidBtn.setDisable(true); markPaidBtn.setVisible(false); }
        if (refreshViolationsBtn != null) refreshViolationsBtn.setDisable(false);
        if (addCustomerBtn != null) { addCustomerBtn.setDisable(true); addCustomerBtn.setVisible(false); }
        if (deleteCustomerBtn != null) { deleteCustomerBtn.setDisable(true); deleteCustomerBtn.setVisible(false); }
        if (refreshCustomersBtn != null) refreshCustomersBtn.setDisable(false);
        if (addInsuranceBtn != null) { addInsuranceBtn.setDisable(true); addInsuranceBtn.setVisible(false); }
        if (editInsuranceBtn != null) { editInsuranceBtn.setDisable(true); editInsuranceBtn.setVisible(false); }
        if (deleteInsuranceBtn != null) { deleteInsuranceBtn.setDisable(true); deleteInsuranceBtn.setVisible(false); }
        if (refreshInsuranceBtn != null) refreshInsuranceBtn.setDisable(false);
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
    }

    private void setupCustomerTable() {
        cIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        cNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        cAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        cPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        cEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void setupInsuranceTable() {
        insIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        insRegCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        insVehicleCol.setCellValueFactory(new PropertyValueFactory<>("vehicleInfo"));
        insProviderCol.setCellValueFactory(new PropertyValueFactory<>("provider"));
        insPolicyCol.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
        insStartDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        insExpiryDateCol.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        insCoverageCol.setCellValueFactory(new PropertyValueFactory<>("coverageType"));
        insAmountCol.setCellValueFactory(new PropertyValueFactory<>("coverageAmount"));
        insStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void openVehicleDetailPopup(Vehicle vehicle) {
        try {
            VehicleDetailPopup detailPopup = new VehicleDetailPopup(vehicle, currentRole, () -> {
                loadVehicles();
                loadServiceRecords();
                loadPoliceReports();
                loadViolations();
            });
            detailPopup.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open vehicle details: " + e.getMessage());
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

    private void setupPagination() {
        loadActivitiesFromDatabase();
        int totalPages = (int) Math.ceil((double) allActivities.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        pagination.setPageCount(totalPages);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(pageIndex -> {
            VBox pageBox = new VBox(5);
            pageBox.setStyle("-fx-background-color: #0f172a; -fx-padding: 10;");
            int start = pageIndex * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, allActivities.size());
            for (int i = start; i < end; i++) {
                String activity = allActivities.get(i);
                Label activityLabel = new Label(activity);
                activityLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-padding: 5; -fx-border-color: #1e293b; -fx-border-width: 0 0 1 0;");
                activityLabel.setWrapText(true);
                activityLabel.setMaxWidth(Double.MAX_VALUE);
                pageBox.getChildren().add(activityLabel);
            }
            if (pageBox.getChildren().isEmpty()) {
                Label noDataLabel = new Label("No activity records found");
                noDataLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
                pageBox.getChildren().add(noDataLabel);
            }
            return pageBox;
        });
        pagination.currentPageIndexProperty().addListener((obs, old, nv) -> {
            setStatus("Page " + (nv.intValue() + 1) + " of " + pagination.getPageCount());
        });
    }

    private void loadActivitiesFromDatabase() {
        try {
            allActivities.clear();
            List<String> activities = activityLogDAO.getAllActivities();
            allActivities.addAll(activities);
            activityLogBox.getChildren().clear();
            int count = 0;
            for (String activity : activities) {
                if (count++ >= 10) break;
                Label lbl = new Label(activity);
                lbl.getStyleClass().add("activity-item");
                lbl.setMaxWidth(Double.MAX_VALUE);
                activityLogBox.getChildren().add(lbl);
            }
            setStatus("Activities loaded: " + activities.size());
        } catch (SQLException e) {
            setStatus("Error loading activities: " + e.getMessage());
            loadFallbackActivities();
        }
    }

    private void loadFallbackActivities() {
        allActivities.clear();
        String[] fallbackActivities = {
                "System started - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "Database connection established",
                "User " + currentUser + " logged in",
                "Dashboard initialized",
                "System ready"
        };
        for (String act : fallbackActivities) {
            allActivities.add(act);
        }
    }

    private void logActivity(String activity) {
        try {
            activityLogDAO.addActivity(activity, currentUser);
            loadActivitiesFromDatabase();
            setupPagination();
        } catch (SQLException e) {
            System.err.println("Failed to log activity: " + e.getMessage());
        }
    }

    private void loadAllData() {
        loadVehicles();
        loadServiceRecords();
        loadPoliceReports();
        loadViolations();
        loadCustomers();
        loadInsurance();
    }

    @FXML public void refreshVehicles() { loadVehicles(); logActivity("Vehicles list refreshed by " + currentUser); }
    @FXML public void refreshServiceRecords() { loadServiceRecords(); logActivity("Service records refreshed by " + currentUser); }
    @FXML public void refreshPoliceReports() { loadPoliceReports(); logActivity("Police reports refreshed by " + currentUser); }
    @FXML public void refreshViolations() { loadViolations(); logActivity("Violations list refreshed by " + currentUser); }
    @FXML public void refreshCustomers() { loadCustomers(); logActivity("Customers list refreshed by " + currentUser); }
    @FXML public void refreshInsurance() { loadInsurance(); logActivity("Insurance policies refreshed by " + currentUser); }

    private void loadVehicles() {
        try {
            ObservableList<Vehicle> list = vehicleDAO.getAllVehicles();
            vehicleTable.setItems(list);
            totalVehiclesLabel.setText(String.valueOf(list.size()));
            setStatus("Vehicles loaded: " + list.size());
        } catch (SQLException e) {
            setStatus("Error loading vehicles: " + e.getMessage());
        }
    }

    private void loadServiceRecords() {
        try {
            ObservableList<ServiceRecord> records = workshopDAO.getAllServiceRecords();
            serviceTable.setItems(records);
            setStatus("Service records loaded: " + records.size());
        } catch (SQLException e) {
            setStatus("Workshop error: " + e.getMessage());
        }
    }

    private void loadPoliceReports() {
        try {
            ObservableList<PoliceReport> rpts = policeDAO.getAllReports();
            reportTable.setItems(rpts);
            totalReportsLabel.setText(String.valueOf(rpts.size()));
        } catch (SQLException e) {
            setStatus("Police error: " + e.getMessage());
        }
    }

    private void loadViolations() {
        try {
            ObservableList<Violation> vls = policeDAO.getAllViolations();
            violationTable.setItems(vls);
            totalViolationsLabel.setText(String.valueOf(vls.size()));
        } catch (SQLException e) {
            setStatus("Violations error: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        try {
            ObservableList<Customer> list = customerDAO.getAllCustomers();
            customerTable.setItems(list);
            totalCustomersLabel.setText(String.valueOf(list.size()));
        } catch (SQLException e) {
            setStatus("Customers error: " + e.getMessage());
        }
    }

    private void loadInsurance() {
        try {
            ObservableList<Insurance> list = insuranceDAO.getAllInsurance();
            insuranceTable.setItems(list);
            activePoliciesLabel.setText(String.valueOf(insuranceDAO.getActivePoliciesCount()));
            expiringPoliciesLabel.setText(String.valueOf(insuranceDAO.getExpiringPoliciesCount()));
            expiredPoliciesLabel.setText(String.valueOf(insuranceDAO.getExpiredPoliciesCount()));
            totalPremiumLabel.setText(String.format("M%.2f", insuranceDAO.getTotalPremium()));
            totalInsuranceLabel.setText(String.valueOf(insuranceDAO.getActivePoliciesCount()));
            setStatus("Insurance policies loaded: " + list.size());
        } catch (SQLException e) {
            setStatus("Insurance error: " + e.getMessage());
        }
    }

    @FXML public void handleSearchVehicle() {
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
                logActivity("Searched for vehicle: " + reg);
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

    @FXML public void handleDeleteVehicle() {
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
                    logActivity("Vehicle deleted: " + selected.getRegistrationNumber() + " by " + currentUser);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle deleted successfully.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                }
            }
        });
    }

    @FXML public void handleDeleteCustomer() {
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
                    logActivity("Customer deleted: " + selected.getName() + " by " + currentUser);
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                }
            }
        });
    }

    @FXML public void handleDeleteService() {
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
                    logActivity("Service record deleted for vehicle: " + selected.getRegNumber() + " by " + currentUser);
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                }
            }
        });
    }

    @FXML public void handleMarkPaid() {
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
                    logActivity("Violation #" + selected.getId() + " marked as paid by " + currentUser);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Violation marked as paid.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update: " + e.getMessage());
                }
            }
        });
    }

    @FXML public void openAddViolationDialog() {
        if (!currentRole.equals("ADMIN") && !currentRole.equals("POLICE")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN and POLICE can add violations.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/AddViolation.fxml"));
            Parent root = loader.load();
            AddViolationController controller = loader.getController();
            controller.setOnViolationAdded(() -> {
                loadViolations();
                setStatus("Violation added successfully");
                logActivity("New violation added by " + currentUser);
            });
            Scene scene = new Scene(root, 500, 480);
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

    @FXML public void openAddVehicleDialog() {
        openDialog("/com/vis/fxml/AddVehicle.fxml", "Add Vehicle", 550, 650);
        loadVehicles();
        logActivity("New vehicle added by " + currentUser);
    }

    @FXML public void openAddServiceDialog() {
        openDialog("/com/vis/fxml/AddService.fxml", "Add Service Record", 500, 500);
        loadServiceRecords();
        logActivity("New service record added by " + currentUser);
    }

    @FXML public void openAddReportDialog() {
        openDialog("/com/vis/fxml/AddReport.fxml", "Add Police Report", 500, 500);
        loadPoliceReports();
        logActivity("New police report added by " + currentUser);
    }

    @FXML public void openAddCustomerDialog() {
        openDialog("/com/vis/fxml/AddCustomer.fxml", "Add Customer", 420, 340);
        loadCustomers();
        logActivity("New customer added by " + currentUser);
    }

    @FXML public void openAddInsuranceDialog() {
        if (!currentRole.equals("ADMIN")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can add insurance policies.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/AddEditInsurance.fxml"));
            Parent root = loader.load();
            AddEditInsuranceController controller = loader.getController();
            controller.setAddMode();
            controller.setOnSaveComplete(() -> {
                loadInsurance();
                setStatus("Insurance policy added successfully");
                logActivity("New insurance policy added by " + currentUser);
            });
            Scene scene = new Scene(root, 550, 620);
            scene.getStylesheets().add(getClass().getResource("/com/vis/css/styles.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Add Insurance Policy");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            loadInsurance();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open add insurance form: " + e.getMessage());
        }
    }

    @FXML public void handleEditInsurance() {
        if (!currentRole.equals("ADMIN")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can edit insurance policies.");
            return;
        }
        Insurance selected = insuranceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an insurance policy to edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vis/fxml/AddEditInsurance.fxml"));
            Parent root = loader.load();
            AddEditInsuranceController controller = loader.getController();
            controller.setEditMode(selected);
            controller.setOnSaveComplete(() -> {
                loadInsurance();
                setStatus("Insurance policy updated successfully");
                logActivity("Insurance policy updated by " + currentUser);
            });
            Scene scene = new Scene(root, 550, 620);
            scene.getStylesheets().add(getClass().getResource("/com/vis/css/styles.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Edit Insurance Policy");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            loadInsurance();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open edit insurance form: " + e.getMessage());
        }
    }

    @FXML public void handleDeleteInsurance() {
        if (!currentRole.equals("ADMIN")) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only ADMIN can delete insurance policies.");
            return;
        }
        Insurance selected = insuranceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an insurance policy to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Insurance Policy");
        confirm.setContentText("Are you sure you want to delete policy: " + selected.getPolicyNumber() + " for vehicle " + selected.getRegistrationNumber() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    insuranceDAO.deleteInsurance(selected.getId());
                    loadInsurance();
                    setStatus("Insurance policy deleted");
                    logActivity("Insurance policy deleted: " + selected.getPolicyNumber() + " by " + currentUser);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Insurance policy deleted.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                }
            }
        });
    }

    @FXML public void showDashboard() { mainTabPane.getSelectionModel().select(0); loadActivitiesFromDatabase(); setupPagination(); }
    @FXML public void showVehicles() { mainTabPane.getSelectionModel().select(1); }
    @FXML public void showAddVehicle() { openAddVehicleDialog(); }
    @FXML public void showSearch() { mainTabPane.getSelectionModel().select(1); searchRegField.requestFocus(); }
    @FXML public void showWorkshop() { mainTabPane.getSelectionModel().select(2); }
    @FXML public void showPolice() { mainTabPane.getSelectionModel().select(3); }
    @FXML public void showCustomers() { mainTabPane.getSelectionModel().select(4); }
    @FXML public void showInsurance() { mainTabPane.getSelectionModel().select(5); refreshInsurance(); }

    @FXML public void showAbout() {
        showAlert(Alert.AlertType.INFORMATION, "About VIS",
                "Vehicle Identification System v2.0\nLimkokwing University Lesotho\nObject Oriented Programming II\nBuilt with JavaFX + PostgreSQL\n\nFeatures:\n- Dynamic Activity Log from Database\n- Pagination with 8 items per page\n- Role-based Access Control\n- Full CRUD Operations\n- Insurance Management Module");
    }

    @FXML public void handleExit() { logActivity("User " + currentUser + " logged out"); Platform.exit(); }

    @FXML public void handleLogout() {
        logActivity("User " + currentUser + " logged out");
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