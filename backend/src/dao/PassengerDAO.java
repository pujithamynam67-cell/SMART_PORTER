package dao;

import model.Passenger;
import util.DBConnection;

import java.sql.*;

public class PassengerDAO {

    public int createPassenger(int userId, String fullName, String gender) throws SQLException {
        String sql = "INSERT INTO passengers (user_id, full_name, gender) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, fullName);
            ps.setString(3, gender);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public Passenger findByUserId(int userId) {
        String sql = "SELECT p.*, u.email, u.phone FROM passengers p JOIN users u ON p.user_id = u.user_id WHERE p.user_id = ?";
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

    public Passenger findById(int passengerId) {
        String sql = "SELECT p.*, u.email, u.phone FROM passengers p JOIN users u ON p.user_id = u.user_id WHERE p.passenger_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Passenger map(ResultSet rs) throws SQLException {
        Passenger p = new Passenger();
        p.setPassengerId(rs.getInt("passenger_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setFullName(rs.getString("full_name"));
        p.setGender(rs.getString("gender"));
        p.setEmail(rs.getString("email"));
        p.setPhone(rs.getString("phone"));
        return p;
    }
}