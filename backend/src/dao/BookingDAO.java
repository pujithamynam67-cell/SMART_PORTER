package dao;

import model.Booking;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    /**
     * Create booking inside a transaction. Returns bookingId or -1 on failure.
     */
    public int createBooking(int passengerId, int stationId, int pickupId, int dropId,
                             int serviceTypeId, int numBags, String luggageType,
                             BigDecimal weight, boolean isHeavy, String specialHandling,
                             String instructions, BigDecimal estimatedFare) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String code = "SP" + System.currentTimeMillis();

            String sqlBooking = "INSERT INTO bookings (booking_code, passenger_id, station_id, " +
                    "pickup_location_id, drop_location_id, service_type_id, status, estimated_fare, special_instructions) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'SEARCHING', ?, ?)";
            int bookingId;
            try (PreparedStatement ps = conn.prepareStatement(sqlBooking, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, code);
                ps.setInt(2, passengerId);
                ps.setInt(3, stationId);
                ps.setInt(4, pickupId);
                ps.setInt(5, dropId);
                ps.setInt(6, serviceTypeId);
                ps.setBigDecimal(7, estimatedFare);
                ps.setString(8, instructions);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        return -1;
                    }
                    bookingId = keys.getInt(1);
                }
            }

            String sqlLuggage = "INSERT INTO luggage_details (booking_id, number_of_bags, luggage_type, " +
                    "approximate_weight, is_heavy, special_handling) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlLuggage)) {
                ps.setInt(1, bookingId);
                ps.setInt(2, numBags);
                ps.setString(3, luggageType);
                ps.setBigDecimal(4, weight);
                ps.setBoolean(5, isHeavy);
                ps.setString(6, specialHandling);
                ps.executeUpdate();
            }

            String sqlFare = "INSERT INTO fare_details (booking_id, base_fare, luggage_charge, heavy_charge, total_fare) " +
                    "VALUES (?, ?, ?, ?, ?)";
            // Simplified - real values should come from service type
            try (PreparedStatement ps = conn.prepareStatement(sqlFare)) {
                ps.setInt(1, bookingId);
                ps.setBigDecimal(2, estimatedFare.multiply(new BigDecimal("0.6")));
                ps.setBigDecimal(3, estimatedFare.multiply(new BigDecimal("0.3")));
                ps.setBigDecimal(4, isHeavy ? new BigDecimal("20") : BigDecimal.ZERO);
                ps.setBigDecimal(5, estimatedFare);
                ps.executeUpdate();
            }

            String sqlHist = "INSERT INTO booking_status_history (booking_id, old_status, new_status, remarks) VALUES (?, NULL, 'SEARCHING', 'Booking created')";
            try (PreparedStatement ps = conn.prepareStatement(sqlHist)) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            conn.commit();
            return bookingId;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return -1;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    /**
     * Assign porter with proper locking / transaction to prevent double booking.
     */
    public boolean assignPorter(int bookingId, int porterId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Lock booking
            String checkBooking = "SELECT status FROM bookings WHERE booking_id = ? FOR UPDATE";
            String status;
            try (PreparedStatement ps = conn.prepareStatement(checkBooking)) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    status = rs.getString("status");
                }
            }
            if (!"SEARCHING".equals(status) && !"PORTER_ASSIGNED".equals(status)) {
                conn.rollback();
                return false;
            }

            // Lock porter and check availability + no active booking
            String checkPorter = "SELECT is_available, verification_status FROM porters WHERE porter_id = ? FOR UPDATE";
            try (PreparedStatement ps = conn.prepareStatement(checkPorter)) {
                ps.setInt(1, porterId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || !"VERIFIED".equals(rs.getString("verification_status")) || !rs.getBoolean("is_available")) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            String activeCheck = "SELECT COUNT(*) FROM bookings WHERE porter_id = ? AND status IN " +
                    "('PORTER_ASSIGNED','PORTER_ACCEPTED','PORTER_ARRIVING','SERVICE_STARTED')";
            try (PreparedStatement ps = conn.prepareStatement(activeCheck)) {
                ps.setInt(1, porterId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Assign
            String updateBooking = "UPDATE bookings SET porter_id = ?, status = 'PORTER_ACCEPTED', accepted_at = NOW() WHERE booking_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBooking)) {
                ps.setInt(1, porterId);
                ps.setInt(2, bookingId);
                ps.executeUpdate();
            }

            String updatePorter = "UPDATE porters SET is_available = 0 WHERE porter_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updatePorter)) {
                ps.setInt(1, porterId);
                ps.executeUpdate();
            }

            String hist = "INSERT INTO booking_status_history (booking_id, old_status, new_status, remarks) VALUES (?, ?, 'PORTER_ACCEPTED', 'Porter accepted booking')";
            try (PreparedStatement ps = conn.prepareStatement(hist)) {
                ps.setInt(1, bookingId);
                ps.setString(2, status);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    public boolean updateStatus(int bookingId, String newStatus, Integer changedBy, String remarks) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String getOld = "SELECT status, porter_id FROM bookings WHERE booking_id = ? FOR UPDATE";
            String oldStatus;
            Integer porterId;
            try (PreparedStatement ps = conn.prepareStatement(getOld)) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    oldStatus = rs.getString("status");
                    int pid = rs.getInt("porter_id");
                    porterId = rs.wasNull() ? null : pid;
                }
            }

            StringBuilder updateSql = new StringBuilder("UPDATE bookings SET status = ?");
            if ("SERVICE_STARTED".equals(newStatus)) updateSql.append(", started_at = NOW()");
            if ("SERVICE_COMPLETED".equals(newStatus)) updateSql.append(", completed_at = NOW(), final_fare = estimated_fare");
            if ("CANCELLED".equals(newStatus)) updateSql.append(", cancelled_at = NOW()");
            updateSql.append(" WHERE booking_id = ?");

            try (PreparedStatement ps = conn.prepareStatement(updateSql.toString())) {
                ps.setString(1, newStatus);
                ps.setInt(2, bookingId);
                ps.executeUpdate();
            }

            if (("SERVICE_COMPLETED".equals(newStatus) || "CANCELLED".equals(newStatus)) && porterId != null) {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE porters SET is_available = 1 WHERE porter_id = ?")) {
                    ps.setInt(1, porterId);
                    ps.executeUpdate();
                }
            }

            String hist = "INSERT INTO booking_status_history (booking_id, old_status, new_status, changed_by, remarks) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(hist)) {
                ps.setInt(1, bookingId);
                ps.setString(2, oldStatus);
                ps.setString(3, newStatus);
                if (changedBy != null) ps.setInt(4, changedBy); else ps.setNull(4, Types.INTEGER);
                ps.setString(5, remarks);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    public Booking findById(int bookingId) {
        String sql = "SELECT b.*, pas.full_name AS passenger_name, por.full_name AS porter_name, " +
                "s.station_name, pl.location_name AS pickup_location, dl.location_name AS drop_location, " +
                "st.service_name, ld.number_of_bags, ld.luggage_type, ld.is_heavy, " +
                "pay.payment_status, pay.payment_method " +
                "FROM bookings b " +
                "JOIN passengers pas ON b.passenger_id = pas.passenger_id " +
                "LEFT JOIN porters por ON b.porter_id = por.porter_id " +
                "JOIN stations s ON b.station_id = s.station_id " +
                "JOIN station_locations pl ON b.pickup_location_id = pl.location_id " +
                "JOIN station_locations dl ON b.drop_location_id = dl.location_id " +
                "JOIN service_types st ON b.service_type_id = st.service_type_id " +
                "LEFT JOIN luggage_details ld ON b.booking_id = ld.booking_id " +
                "LEFT JOIN payments pay ON b.booking_id = pay.booking_id " +
                "WHERE b.booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapFull(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Booking> findByPassenger(int passengerId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, pas.full_name AS passenger_name, por.full_name AS porter_name, " +
                "s.station_name, pl.location_name AS pickup_location, dl.location_name AS drop_location, " +
                "st.service_name, ld.number_of_bags, ld.luggage_type, ld.is_heavy, " +
                "pay.payment_status, pay.payment_method " +
                "FROM bookings b " +
                "JOIN passengers pas ON b.passenger_id = pas.passenger_id " +
                "LEFT JOIN porters por ON b.porter_id = por.porter_id " +
                "JOIN stations s ON b.station_id = s.station_id " +
                "JOIN station_locations pl ON b.pickup_location_id = pl.location_id " +
                "JOIN station_locations dl ON b.drop_location_id = dl.location_id " +
                "JOIN service_types st ON b.service_type_id = st.service_type_id " +
                "LEFT JOIN luggage_details ld ON b.booking_id = ld.booking_id " +
                "LEFT JOIN payments pay ON b.booking_id = pay.booking_id " +
                "WHERE b.passenger_id = ? ORDER BY b.booked_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFull(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> findByPorter(int porterId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, pas.full_name AS passenger_name, por.full_name AS porter_name, " +
                "s.station_name, pl.location_name AS pickup_location, dl.location_name AS drop_location, " +
                "st.service_name, ld.number_of_bags, ld.luggage_type, ld.is_heavy, " +
                "pay.payment_status, pay.payment_method " +
                "FROM bookings b " +
                "JOIN passengers pas ON b.passenger_id = pas.passenger_id " +
                "LEFT JOIN porters por ON b.porter_id = por.porter_id " +
                "JOIN stations s ON b.station_id = s.station_id " +
                "JOIN station_locations pl ON b.pickup_location_id = pl.location_id " +
                "JOIN station_locations dl ON b.drop_location_id = dl.location_id " +
                "JOIN service_types st ON b.service_type_id = st.service_type_id " +
                "LEFT JOIN luggage_details ld ON b.booking_id = ld.booking_id " +
                "LEFT JOIN payments pay ON b.booking_id = pay.booking_id " +
                "WHERE b.porter_id = ? ORDER BY b.booked_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, porterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFull(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> findAll() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, pas.full_name AS passenger_name, por.full_name AS porter_name, " +
                "s.station_name, pl.location_name AS pickup_location, dl.location_name AS drop_location, " +
                "st.service_name, ld.number_of_bags, ld.luggage_type, ld.is_heavy, " +
                "pay.payment_status, pay.payment_method " +
                "FROM bookings b " +
                "JOIN passengers pas ON b.passenger_id = pas.passenger_id " +
                "LEFT JOIN porters por ON b.porter_id = por.porter_id " +
                "JOIN stations s ON b.station_id = s.station_id " +
                "JOIN station_locations pl ON b.pickup_location_id = pl.location_id " +
                "JOIN station_locations dl ON b.drop_location_id = dl.location_id " +
                "JOIN service_types st ON b.service_type_id = st.service_type_id " +
                "LEFT JOIN luggage_details ld ON b.booking_id = ld.booking_id " +
                "LEFT JOIN payments pay ON b.booking_id = pay.booking_id " +
                "ORDER BY b.booked_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapFull(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> findPendingForPorter(int porterId) {
        // For demo: show SEARCHING bookings at same station
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, pas.full_name AS passenger_name, NULL AS porter_name, " +
                "s.station_name, pl.location_name AS pickup_location, dl.location_name AS drop_location, " +
                "st.service_name, ld.number_of_bags, ld.luggage_type, ld.is_heavy, " +
                "NULL AS payment_status, NULL AS payment_method " +
                "FROM bookings b " +
                "JOIN passengers pas ON b.passenger_id = pas.passenger_id " +
                "JOIN stations s ON b.station_id = s.station_id " +
                "JOIN station_locations pl ON b.pickup_location_id = pl.location_id " +
                "JOIN station_locations dl ON b.drop_location_id = dl.location_id " +
                "JOIN service_types st ON b.service_type_id = st.service_type_id " +
                "LEFT JOIN luggage_details ld ON b.booking_id = ld.booking_id " +
                "JOIN porters por ON por.porter_id = ? " +
                "WHERE b.status = 'SEARCHING' AND b.station_id = por.current_station_id " +
                "ORDER BY b.booked_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, porterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFull(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Booking mapFull(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setBookingCode(rs.getString("booking_code"));
        b.setPassengerId(rs.getInt("passenger_id"));
        int pid = rs.getInt("porter_id");
        b.setPorterId(rs.wasNull() ? null : pid);
        b.setStationId(rs.getInt("station_id"));
        b.setPickupLocationId(rs.getInt("pickup_location_id"));
        b.setDropLocationId(rs.getInt("drop_location_id"));
        b.setServiceTypeId(rs.getInt("service_type_id"));
        b.setStatus(rs.getString("status"));
        b.setEstimatedFare(rs.getBigDecimal("estimated_fare"));
        b.setFinalFare(rs.getBigDecimal("final_fare"));
        b.setSpecialInstructions(rs.getString("special_instructions"));
        b.setBookedAt(rs.getTimestamp("booked_at"));
        b.setAcceptedAt(rs.getTimestamp("accepted_at"));
        b.setStartedAt(rs.getTimestamp("started_at"));
        b.setCompletedAt(rs.getTimestamp("completed_at"));
        b.setCancelledAt(rs.getTimestamp("cancelled_at"));
        b.setCancellationReason(rs.getString("cancellation_reason"));
        b.setPassengerName(rs.getString("passenger_name"));
        b.setPorterName(rs.getString("porter_name"));
        b.setStationName(rs.getString("station_name"));
        b.setPickupLocation(rs.getString("pickup_location"));
        b.setDropLocation(rs.getString("drop_location"));
        b.setServiceName(rs.getString("service_name"));
        b.setNumberOfBags(rs.getInt("number_of_bags"));
        b.setLuggageType(rs.getString("luggage_type"));
        b.setHeavy(rs.getBoolean("is_heavy"));
        b.setPaymentStatus(rs.getString("payment_status"));
        b.setPaymentMethod(rs.getString("payment_method"));
        return b;
    }
}