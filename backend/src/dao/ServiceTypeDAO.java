package dao;

import model.ServiceType;
import util.DBConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTypeDAO {

    public List<ServiceType> findAllActive() {
        List<ServiceType> list = new ArrayList<>();
        String sql = "SELECT * FROM service_types WHERE is_active = 1 ORDER BY base_fare";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ServiceType st = new ServiceType();
                st.setServiceTypeId(rs.getInt("service_type_id"));
                st.setServiceName(rs.getString("service_name"));
                st.setDescription(rs.getString("description"));
                st.setBaseFare(rs.getBigDecimal("base_fare"));
                st.setPerBagCharge(rs.getBigDecimal("per_bag_charge"));
                st.setHeavyLuggageCharge(rs.getBigDecimal("heavy_luggage_charge"));
                st.setActive(rs.getBoolean("is_active"));
                list.add(st);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ServiceType findById(int id) {
        String sql = "SELECT * FROM service_types WHERE service_type_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ServiceType st = new ServiceType();
                    st.setServiceTypeId(rs.getInt("service_type_id"));
                    st.setServiceName(rs.getString("service_name"));
                    st.setDescription(rs.getString("description"));
                    st.setBaseFare(rs.getBigDecimal("base_fare"));
                    st.setPerBagCharge(rs.getBigDecimal("per_bag_charge"));
                    st.setHeavyLuggageCharge(rs.getBigDecimal("heavy_luggage_charge"));
                    st.setActive(rs.getBoolean("is_active"));
                    return st;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal calculateFare(int serviceTypeId, int numBags, boolean isHeavy) {
        ServiceType st = findById(serviceTypeId);
        if (st == null) return java.math.BigDecimal.ZERO;
        java.math.BigDecimal total = st.getBaseFare()
                .add(st.getPerBagCharge().multiply(new java.math.BigDecimal(numBags)));
        if (isHeavy) {
            total = total.add(st.getHeavyLuggageCharge());
        }
        return total;
    }
}