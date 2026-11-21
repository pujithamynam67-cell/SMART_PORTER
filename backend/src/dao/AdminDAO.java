package dao;

import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AdminDAO {

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try (Connection conn = DBConnection.getConnection()) {
            stats.put("totalPassengers", getCount(conn, "SELECT COUNT(*) FROM passengers"));
            stats.put("totalPorters", getCount(conn, "SELECT COUNT(*) FROM porters"));
            stats.put("availablePorters", getCount(conn, "SELECT COUNT(*) FROM porters WHERE is_available = 1 AND verification_status = 'VERIFIED'"));
            stats.put("pendingPorters", getCount(conn, "SELECT COUNT(*) FROM porters WHERE verification_status = 'PENDING'"));
            stats.put("totalBookings", getCount(conn, "SELECT COUNT(*) FROM bookings"));
            stats.put("activeBookings", getCount(conn, "SELECT COUNT(*) FROM bookings WHERE status IN ('SEARCHING','PORTER_ASSIGNED','PORTER_ACCEPTED','PORTER_ARRIVING','SERVICE_STARTED')"));
            stats.put("completedBookings", getCount(conn, "SELECT COUNT(*) FROM bookings WHERE status = 'SERVICE_COMPLETED'"));
            stats.put("cancelledBookings", getCount(conn, "SELECT COUNT(*) FROM bookings WHERE status = 'CANCELLED'"));

            String revSql = "SELECT COALESCE(SUM(final_fare),0) FROM bookings WHERE status = 'SERVICE_COMPLETED'";
            try (PreparedStatement ps = conn.prepareStatement(revSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats.put("totalRevenue", rs.getBigDecimal(1));
                else stats.put("totalRevenue", BigDecimal.ZERO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    private int getCount(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}