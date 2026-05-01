package com.vis.model;

import com.vis.db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class VehicleDAO {

    // READ - Get all vehicles
    public ObservableList<Vehicle> getAllVehicles() throws SQLException {
        ObservableList<Vehicle> list = FXCollections.observableArrayList();
        String sql = "SELECT v.*, c.name as owner_name " +
                "FROM Vehicle v LEFT JOIN Customer c ON v.owner_id = c.customer_id " +
                "ORDER BY v.vehicle_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Vehicle v = new Vehicle(
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getInt("owner_id"),
                        rs.getString("description") != null ? rs.getString("description") : "",
                        rs.getString("color") != null ? rs.getString("color") : "",
                        rs.getString("status") != null ? rs.getString("status") : "Active",
                        rs.getString("primary_image_path") != null ? rs.getString("primary_image_path") : "",
                        rs.getString("engine") != null ? rs.getString("engine") : "",
                        rs.getString("transmission") != null ? rs.getString("transmission") : "",
                        rs.getString("fuel_type") != null ? rs.getString("fuel_type") : "",
                        rs.getInt("mileage")
                );
                v.setOwnerName(rs.getString("owner_name"));
                list.add(v);
            }
        }
        return list;
    }

    // READ - Search by registration
    public Vehicle searchByRegistration(String reg) throws SQLException {
        String sql = "SELECT v.*, c.name as owner_name " +
                "FROM Vehicle v LEFT JOIN Customer c ON v.owner_id = c.customer_id " +
                "WHERE v.registration_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Vehicle v = new Vehicle(
                            rs.getInt("vehicle_id"),
                            rs.getString("registration_number"),
                            rs.getString("make"),
                            rs.getString("model"),
                            rs.getInt("year"),
                            rs.getInt("owner_id"),
                            rs.getString("description") != null ? rs.getString("description") : "",
                            rs.getString("color") != null ? rs.getString("color") : "",
                            rs.getString("status") != null ? rs.getString("status") : "Active",
                            rs.getString("primary_image_path") != null ? rs.getString("primary_image_path") : "",
                            rs.getString("engine") != null ? rs.getString("engine") : "",
                            rs.getString("transmission") != null ? rs.getString("transmission") : "",
                            rs.getString("fuel_type") != null ? rs.getString("fuel_type") : "",
                            rs.getInt("mileage")
                    );
                    v.setOwnerName(rs.getString("owner_name"));
                    return v;
                }
            }
        }
        return null;
    }

    // READ - Get vehicle by ID (ADD THIS METHOD)
    public Vehicle getVehicleById(int id) throws SQLException {
        String sql = "SELECT v.*, c.name as owner_name " +
                "FROM Vehicle v LEFT JOIN Customer c ON v.owner_id = c.customer_id " +
                "WHERE v.vehicle_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Vehicle v = new Vehicle(
                            rs.getInt("vehicle_id"),
                            rs.getString("registration_number"),
                            rs.getString("make"),
                            rs.getString("model"),
                            rs.getInt("year"),
                            rs.getInt("owner_id"),
                            rs.getString("description") != null ? rs.getString("description") : "",
                            rs.getString("color") != null ? rs.getString("color") : "",
                            rs.getString("status") != null ? rs.getString("status") : "Active",
                            rs.getString("primary_image_path") != null ? rs.getString("primary_image_path") : "",
                            rs.getString("engine") != null ? rs.getString("engine") : "",
                            rs.getString("transmission") != null ? rs.getString("transmission") : "",
                            rs.getString("fuel_type") != null ? rs.getString("fuel_type") : "",
                            rs.getInt("mileage")
                    );
                    v.setOwnerName(rs.getString("owner_name"));
                    return v;
                }
            }
        }
        return null;
    }

    // CREATE - Add vehicle
    public void addVehicle(Vehicle v) throws SQLException {
        String sql = "INSERT INTO Vehicle (registration_number, make, model, year, owner_id, description, color, status, primary_image_path, engine, transmission, fuel_type, mileage) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getRegistrationNumber());
            ps.setString(2, v.getMake());
            ps.setString(3, v.getModel());
            ps.setInt(4, v.getYear());
            ps.setInt(5, v.getOwnerId());
            ps.setString(6, v.getDescription());
            ps.setString(7, v.getColor());
            ps.setString(8, v.getStatus());
            ps.setString(9, v.getPrimaryImagePath());
            ps.setString(10, v.getEngine());
            ps.setString(11, v.getTransmission());
            ps.setString(12, v.getFuelType());
            ps.setInt(13, v.getMileage());
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                v.setId(generatedKeys.getInt(1));
            }
        }
    }

    // UPDATE
    public void updateVehicle(Vehicle v) throws SQLException {
        String sql = "UPDATE Vehicle SET registration_number=?, make=?, model=?, year=?, owner_id=?, description=?, color=?, status=?, primary_image_path=?, engine=?, transmission=?, fuel_type=?, mileage=? WHERE vehicle_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getRegistrationNumber());
            ps.setString(2, v.getMake());
            ps.setString(3, v.getModel());
            ps.setInt(4, v.getYear());
            ps.setInt(5, v.getOwnerId());
            ps.setString(6, v.getDescription());
            ps.setString(7, v.getColor());
            ps.setString(8, v.getStatus());
            ps.setString(9, v.getPrimaryImagePath());
            ps.setString(10, v.getEngine());
            ps.setString(11, v.getTransmission());
            ps.setString(12, v.getFuelType());
            ps.setInt(13, v.getMileage());
            ps.setInt(14, v.getId());
            ps.executeUpdate();
        }
    }

    // Soft delete - set status to 'Deleted'
    public void deleteVehicle(int id) throws SQLException {
        String sql = "UPDATE Vehicle SET status = 'Deleted' WHERE vehicle_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Hard delete (only for admin)
    public void hardDeleteVehicle(int id) throws SQLException {
        String sql = "DELETE FROM Vehicle WHERE vehicle_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Update vehicle status
    public void updateVehicleStatus(int id, String status) throws SQLException {
        String sql = "UPDATE Vehicle SET status = ? WHERE vehicle_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}