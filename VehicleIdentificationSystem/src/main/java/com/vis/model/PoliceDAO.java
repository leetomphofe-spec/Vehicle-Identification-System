package com.vis.model;

import com.vis.db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class PoliceDAO {

    // ========== POLICE REPORTS ==========

    public ObservableList<PoliceReport> getAllReports() throws SQLException {
        ObservableList<PoliceReport> list = FXCollections.observableArrayList();
        String sql = "SELECT pr.report_id, pr.vehicle_id, pr.report_date::TEXT, " +
                "pr.report_type, pr.description, pr.officer_name, v.registration_number " +
                "FROM PoliceReport pr JOIN Vehicle v ON pr.vehicle_id=v.vehicle_id " +
                "ORDER BY pr.report_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new PoliceReport(
                        rs.getInt("report_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("report_date"),
                        rs.getString("report_type"),
                        rs.getString("description"),
                        rs.getString("officer_name"),
                        rs.getString("registration_number")));
            }
        }
        return list;
    }

    public void addReport(PoliceReport r) throws SQLException {
        String sql = "INSERT INTO PoliceReport(vehicle_id,report_date,report_type,description,officer_name) " +
                "VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getVehicleId());
            ps.setDate(2, java.sql.Date.valueOf(r.getReportDate()));
            ps.setString(3, r.getReportType());
            ps.setString(4, r.getDescription());
            ps.setString(5, r.getOfficerName());
            ps.executeUpdate();
        }
    }

    // ========== VIOLATIONS ==========

    // Updated query WITHOUT description column
    public ObservableList<Violation> getAllViolations() throws SQLException {
        ObservableList<Violation> list = FXCollections.observableArrayList();
        String sql = "SELECT vl.violation_id, vl.vehicle_id, vl.violation_date::TEXT, " +
                "vl.violation_type, vl.fine_amount, vl.status, " +
                "v.registration_number " +
                "FROM Violation vl JOIN Vehicle v ON vl.vehicle_id=v.vehicle_id " +
                "ORDER BY vl.violation_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Violation v = new Violation(
                        rs.getInt("violation_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("violation_date"),
                        rs.getString("violation_type"),
                        rs.getDouble("fine_amount"),
                        rs.getString("status"),
                        rs.getString("registration_number")
                );
                list.add(v);
            }
        }
        return list;
    }

    // Add this method to PoliceDAO.java if not already present
    public void addViolation(Violation v) throws SQLException {
        String sql = "INSERT INTO Violation(vehicle_id, violation_date, violation_type, fine_amount, status) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getVehicleId());
            ps.setDate(2, java.sql.Date.valueOf(v.getViolationDate()));
            ps.setString(3, v.getViolationType());
            ps.setDouble(4, v.getFineAmount());
            ps.setString(5, v.getStatus() != null ? v.getStatus() : "Unpaid");
            ps.executeUpdate();
        }
    }



    public void updateViolation(Violation v) throws SQLException {
        // Updated WITHOUT description column
        String sql = "UPDATE Violation SET violation_date=?, violation_type=?, fine_amount=?, status=? WHERE violation_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(v.getViolationDate()));
            ps.setString(2, v.getViolationType());
            ps.setDouble(3, v.getFineAmount());
            ps.setString(4, v.getStatus());
            ps.setInt(5, v.getId());
            ps.executeUpdate();
        }
    }

    public void deleteViolation(int violationId) throws SQLException {
        String sql = "DELETE FROM Violation WHERE violation_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, violationId);
            ps.executeUpdate();
        }
    }

    public void markViolationPaid(int violationId) throws SQLException {
        String sql = "UPDATE Violation SET status='Paid' WHERE violation_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, violationId);
            ps.executeUpdate();
        }
    }
}