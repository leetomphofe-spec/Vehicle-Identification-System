package com.vis.model;

import com.vis.db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class InsuranceDAO {

    public ObservableList<Insurance> getAllInsurance() throws SQLException {
        ObservableList<Insurance> list = FXCollections.observableArrayList();
        String sql = "SELECT i.*, v.registration_number, v.make, v.model " +
                "FROM Insurance i " +
                "JOIN Vehicle v ON i.vehicle_id = v.vehicle_id " +
                "ORDER BY i.insurance_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String vehicleInfo = rs.getString("make") + " " + rs.getString("model");
                Insurance ins = new Insurance(
                        rs.getInt("insurance_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        vehicleInfo,
                        rs.getString("provider"),
                        rs.getString("policy_number"),
                        rs.getString("start_date"),
                        rs.getString("expiry_date"),
                        rs.getString("coverage_type"),
                        rs.getDouble("coverage_amount"),
                        rs.getDouble("premium"),
                        rs.getString("status")
                );
                list.add(ins);
            }
        }
        return list;
    }

    public Insurance getInsuranceById(int insuranceId) throws SQLException {
        String sql = "SELECT i.*, v.registration_number, v.make, v.model FROM Insurance i " +
                "JOIN Vehicle v ON i.vehicle_id = v.vehicle_id " +
                "WHERE i.insurance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, insuranceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String vehicleInfo = rs.getString("make") + " " + rs.getString("model");
                return new Insurance(
                        rs.getInt("insurance_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        vehicleInfo,
                        rs.getString("provider"),
                        rs.getString("policy_number"),
                        rs.getString("start_date"),
                        rs.getString("expiry_date"),
                        rs.getString("coverage_type"),
                        rs.getDouble("coverage_amount"),
                        rs.getDouble("premium"),
                        rs.getString("status")
                );
            }
        }
        return null;
    }

    public Insurance getInsuranceByVehicleId(int vehicleId) throws SQLException {
        String sql = "SELECT i.*, v.registration_number, v.make, v.model FROM Insurance i " +
                "JOIN Vehicle v ON i.vehicle_id = v.vehicle_id " +
                "WHERE i.vehicle_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String vehicleInfo = rs.getString("make") + " " + rs.getString("model");
                return new Insurance(
                        rs.getInt("insurance_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        vehicleInfo,
                        rs.getString("provider"),
                        rs.getString("policy_number"),
                        rs.getString("start_date"),
                        rs.getString("expiry_date"),
                        rs.getString("coverage_type"),
                        rs.getDouble("coverage_amount"),
                        rs.getDouble("premium"),
                        rs.getString("status")
                );
            }
        }
        return null;
    }

    public void addInsurance(Insurance insurance) throws SQLException {
        String sql = "INSERT INTO Insurance (vehicle_id, provider, policy_number, " +
                "start_date, expiry_date, coverage_type, coverage_amount, premium, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, insurance.getVehicleId());
            ps.setString(2, insurance.getProvider());
            ps.setString(3, insurance.getPolicyNumber());
            ps.setDate(4, Date.valueOf(insurance.getStartDate()));
            ps.setDate(5, Date.valueOf(insurance.getExpiryDate()));
            ps.setString(6, insurance.getCoverageType());
            ps.setDouble(7, insurance.getCoverageAmount());
            ps.setDouble(8, insurance.getPremium());
            ps.setString(9, insurance.getStatus());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                insurance.setId(rs.getInt(1));
            }
        }
    }

    public void updateInsurance(Insurance insurance) throws SQLException {
        String sql = "UPDATE Insurance SET provider=?, policy_number=?, start_date=?, " +
                "expiry_date=?, coverage_type=?, coverage_amount=?, premium=?, status=? " +
                "WHERE insurance_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, insurance.getProvider());
            ps.setString(2, insurance.getPolicyNumber());
            ps.setDate(3, Date.valueOf(insurance.getStartDate()));
            ps.setDate(4, Date.valueOf(insurance.getExpiryDate()));
            ps.setString(5, insurance.getCoverageType());
            ps.setDouble(6, insurance.getCoverageAmount());
            ps.setDouble(7, insurance.getPremium());
            ps.setString(8, insurance.getStatus());
            ps.setInt(9, insurance.getId());
            ps.executeUpdate();
        }
    }

    public void deleteInsurance(int insuranceId) throws SQLException {
        String sql = "DELETE FROM Insurance WHERE insurance_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, insuranceId);
            ps.executeUpdate();
        }
    }

    public int getActivePoliciesCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Insurance WHERE status = 'Active' AND expiry_date >= CURRENT_DATE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getExpiringPoliciesCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Insurance WHERE expiry_date BETWEEN CURRENT_DATE AND CURRENT_DATE + 30 AND status = 'Active'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getExpiredPoliciesCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Insurance WHERE expiry_date < CURRENT_DATE OR status = 'Expired'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public double getTotalPremium() throws SQLException {
        String sql = "SELECT COALESCE(SUM(premium), 0) FROM Insurance WHERE status = 'Active'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }
}