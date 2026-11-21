package dao;

import model.Porter;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PorterDAO {

    public int createPorter(int userId, String fullName, int experienceYears) throws SQLException {
        String sql = "INSERT INTO porters (user_id, full_name, experience_years, verification_status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, fullName);
            ps.setInt(3, experienceYears);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public Porter findByUserId(int userId) {
        String sql = "SELECT p.*, u.email, u.phone, s.station_name FROM porters p " +
                     "JOIN users u ON p.user_id = u.user_id " +
                     "LEFT JOIN stations s ON p.current_station_id = s.station_id " +
                     "WHERE p.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Porter findById(int porterId) {
        String sql = "SELECT p.*, u.email, u.phone, s.station_name FROM porters p " +
                     "JOIN users u ON p.user_id = u.user_id " +
                     "LEFT JOIN stations s ON p.current_station_id = s.station_id " +
                     "WHERE p.porter_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, porterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Porter> findAvailableAtStation(int stationId) {
        List<Porter> list = new ArrayList<>();
        String sql = "SELECT p.*, u.email, u.phone, s.station_name FROM porters p " +
                     "JOIN users u ON p.user_id = u.user_id " +
                     "LEFT JOIN stations s ON p.current_station_id = s.station_id " +
                     "WHERE p.verification_status = 'VERIFIED' AND p.is_available = 1 " +
                     "AND p.current_station_id = ? AND u.is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Porter> findAll() {
        List<Porter> list = new ArrayList<>();
        String sql = "SELECT p.*, u.email, u.phone, s.station_name FROM porters p " +
                     "JOIN users u ON p.user_id = u.user_id " +
                     "LEFT JOIN stations s ON p.current_station_id = s.station_id ORDER BY p.porter_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateAvailability(int porterId, boolean available) {
        String sql = "UPDATE porters SET is_available = ? WHERE porter_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, porterId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateVerification(int porterId, String status, int adminUserId) {
        String sql = "UPDATE porters SET verification_status = ?, verified_by = ?, verified_at = NOW() WHERE porter_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, adminUserId);
            ps.setInt(3, porterId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setStation(int porterId, int stationId) {
        String sql = "UPDATE porters SET current_station_id = ? WHERE porter_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            ps.setInt(2, porterId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Porter map(ResultSet rs) throws SQLException {
        Porter p = new Porter();
        p.setPorterId(rs.getInt("porter_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setFullName(rs.getString("full_name"));
        p.setExperienceYears(rs.getInt("experience_years"));
        p.setProfilePhoto(rs.getString("profile_photo"));
        p.setVerificationStatus(rs.getString("verification_status"));
        p.setAverageRating(rs.getBigDecimal("average_rating"));
        p.setTotalRatings(rs.getInt("total_ratings"));
        p.setAvailable(rs.getBoolean("is_available"));
        int sid = rs.getInt("current_station_id");
        p.setCurrentStationId(rs.wasNull() ? null : sid);
        p.setStationName(rs.getString("station_name"));
        p.setEmail(rs.getString("email"));
        p.setPhone(rs.getString("phone"));
        return p;
    }
}