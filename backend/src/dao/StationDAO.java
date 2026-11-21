package dao;

import model.Station;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationDAO {

    public List<Station> findAllActive() {
        List<Station> list = new ArrayList<>();
        String sql = "SELECT * FROM stations WHERE is_active = 1 ORDER BY station_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Station s = new Station();
                s.setStationId(rs.getInt("station_id"));
                s.setStationCode(rs.getString("station_code"));
                s.setStationName(rs.getString("station_name"));
                s.setCity(rs.getString("city"));
                s.setState(rs.getString("state"));
                s.setActive(rs.getBoolean("is_active"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getLocationsByStation(int stationId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT location_id, location_name, location_type FROM station_locations WHERE station_id = ? AND is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("locationId", rs.getInt("location_id"));
                    m.put("locationName", rs.getString("location_name"));
                    m.put("locationType", rs.getString("location_type"));
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}