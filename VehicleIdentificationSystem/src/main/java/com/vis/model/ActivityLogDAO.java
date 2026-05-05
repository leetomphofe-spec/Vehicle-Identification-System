package com.vis.model;

import com.vis.db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {

    // Add activity to database
    public void addActivity(String activity, String username) throws SQLException {
        String sql = "INSERT INTO ActivityLog (activity, username, activity_date) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, activity);
            ps.setString(2, username);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        }
    }

    // Get all activities (for pagination)
    public List<String> getAllActivities() throws SQLException {
        List<String> activities = new ArrayList<>();
        String sql = "SELECT activity, username, activity_date FROM ActivityLog ORDER BY activity_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String activity = rs.getString("activity");
                String username = rs.getString("username");
                Timestamp date = rs.getTimestamp("activity_date");
                String formattedActivity = String.format("[%s] %s - %s",
                        date.toString().substring(0, 19), username, activity);
                activities.add(formattedActivity);
            }
        }
        return activities;
    }

    // Get activities with pagination
    public ObservableList<String> getActivitiesPaginated(int offset, int limit) throws SQLException {
        ObservableList<String> activities = FXCollections.observableArrayList();
        String sql = "SELECT activity, username, activity_date FROM ActivityLog ORDER BY activity_date DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String activity = rs.getString("activity");
                String username = rs.getString("username");
                Timestamp date = rs.getTimestamp("activity_date");
                String formattedActivity = String.format("[%s] %s - %s",
                        date.toString().substring(0, 19), username, activity);
                activities.add(formattedActivity);
            }
        }
        return activities;
    }

    // Get total count of activities
    public int getActivityCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ActivityLog";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Clear old activities (keep last 100)
    public void cleanupOldActivities() throws SQLException {
        String sql = "DELETE FROM ActivityLog WHERE activity_id NOT IN (SELECT activity_id FROM ActivityLog ORDER BY activity_date DESC LIMIT 100)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}