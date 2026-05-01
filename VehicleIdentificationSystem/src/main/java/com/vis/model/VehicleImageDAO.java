package com.vis.model;

import com.vis.db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class VehicleImageDAO {

    public void addVehicleImage(VehicleImage image) throws SQLException {
        String sql = "INSERT INTO VehicleImage (vehicle_id, image_path, image_name, is_primary, upload_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, image.getVehicleId());
            ps.setString(2, image.getImagePath());
            ps.setString(3, image.getImageName());
            ps.setBoolean(4, image.getIsPrimary());
            ps.setDate(5, Date.valueOf(image.getUploadDate()));
            ps.executeUpdate();
        }
    }

    public ObservableList<VehicleImage> getImagesForVehicle(int vehicleId) throws SQLException {
        ObservableList<VehicleImage> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM VehicleImage WHERE vehicle_id = ? ORDER BY is_primary DESC, upload_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new VehicleImage(
                        rs.getInt("image_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("image_path"),
                        rs.getString("image_name"),
                        rs.getBoolean("is_primary"),
                        rs.getString("upload_date")
                ));
            }
        }
        return list;
    }

    public VehicleImage getPrimaryImage(int vehicleId) throws SQLException {
        String sql = "SELECT * FROM VehicleImage WHERE vehicle_id = ? AND is_primary = true LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new VehicleImage(
                        rs.getInt("image_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("image_path"),
                        rs.getString("image_name"),
                        rs.getBoolean("is_primary"),
                        rs.getString("upload_date")
                );
            }
        }
        return null;
    }

    public void deleteImage(int imageId) throws SQLException {
        String sql = "DELETE FROM VehicleImage WHERE image_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, imageId);
            ps.executeUpdate();
        }
    }
}