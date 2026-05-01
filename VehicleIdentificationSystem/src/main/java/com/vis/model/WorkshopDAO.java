package com.vis.model;

import com.vis.db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class WorkshopDAO {

    public ObservableList<ServiceRecord> getAllServiceRecords() throws SQLException {
        ObservableList<ServiceRecord> list = FXCollections.observableArrayList();
        String sql = "SELECT sr.service_id, sr.vehicle_id, sr.service_date::TEXT, " +
                     "sr.service_type, sr.description, sr.cost, v.registration_number " +
                     "FROM ServiceRecord sr JOIN Vehicle v ON sr.vehicle_id=v.vehicle_id " +
                     "ORDER BY sr.service_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ServiceRecord(
                        rs.getInt("service_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("service_date"),
                        rs.getString("service_type"),
                        rs.getString("description"),
                        rs.getDouble("cost"),
                        rs.getString("registration_number")));
            }
        }
        return list;
    }

    public void addServiceRecord(ServiceRecord s) throws SQLException {
        String sql = "INSERT INTO ServiceRecord(vehicle_id,service_date,service_type,description,cost) " +
                     "VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getVehicleId());
            ps.setDate(2, java.sql.Date.valueOf(s.getServiceDate()));
            ps.setString(3, s.getServiceType());
            ps.setString(4, s.getDescription());
            ps.setDouble(5, s.getCost());
            ps.executeUpdate();
        }
    }

    public void deleteServiceRecord(int serviceId) throws SQLException {
        String sql = "DELETE FROM ServiceRecord WHERE service_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            ps.executeUpdate();
        }
    }
}
