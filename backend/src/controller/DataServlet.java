package controller;

import dao.PorterDAO;
import dao.ServiceTypeDAO;
import dao.StationDAO;
import model.Porter;
import model.ServiceType;
import model.Station;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/api/data/*")
public class DataServlet extends HttpServlet {

    private final StationDAO stationDAO = new StationDAO();
    private final ServiceTypeDAO serviceTypeDAO = new ServiceTypeDAO();
    private final PorterDAO porterDAO = new PorterDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";

        if ("/stations".equals(path)) {
            List<Station> list = stationDAO.findAllActive();
            StringBuilder sb = new StringBuilder("{\"success\":true,\"stations\":[");
            for (int i = 0; i < list.size(); i++) {
                Station s = list.get(i);
                if (i > 0) sb.append(",");
                sb.append("{\"stationId\":").append(s.getStationId())
                  .append(",\"code\":\"").append(s.getStationCode()).append("\"")
                  .append(",\"name\":\"").append(s.getStationName()).append("\"")
                  .append(",\"city\":\"").append(s.getCity()).append("\"")
                  .append("}");
            }
            sb.append("]}");
            out.print(sb.toString());
        } else if (path.startsWith("/locations/")) {
            try {
                int stationId = Integer.parseInt(path.substring("/locations/".length()));
                List<Map<String, Object>> locs = stationDAO.getLocationsByStation(stationId);
                StringBuilder sb = new StringBuilder("{\"success\":true,\"locations\":[");
                for (int i = 0; i < locs.size(); i++) {
                    Map<String, Object> m = locs.get(i);
                    if (i > 0) sb.append(",");
                    sb.append("{\"locationId\":").append(m.get("locationId"))
                      .append(",\"name\":\"").append(m.get("locationName")).append("\"")
                      .append(",\"type\":\"").append(m.get("locationType")).append("\"")
                      .append("}");
                }
                sb.append("]}");
                out.print(sb.toString());
            } catch (Exception e) {
                out.print("{\"success\":false}");
            }
        } else if ("/services".equals(path)) {
            List<ServiceType> list = serviceTypeDAO.findAllActive();
            StringBuilder sb = new StringBuilder("{\"success\":true,\"services\":[");
            for (int i = 0; i < list.size(); i++) {
                ServiceType st = list.get(i);
                if (i > 0) sb.append(",");
                sb.append("{\"serviceTypeId\":").append(st.getServiceTypeId())
                  .append(",\"name\":\"").append(st.getServiceName()).append("\"")
                  .append(",\"description\":\"").append(st.getDescription() != null ? st.getDescription() : "").append("\"")
                  .append(",\"baseFare\":").append(st.getBaseFare())
                  .append(",\"perBagCharge\":").append(st.getPerBagCharge())
                  .append(",\"heavyCharge\":").append(st.getHeavyLuggageCharge())
                  .append("}");
            }
            sb.append("]}");
            out.print(sb.toString());
        } else if (path.startsWith("/porters/")) {
            try {
                int stationId = Integer.parseInt(path.substring("/porters/".length()));
                List<Porter> list = porterDAO.findAvailableAtStation(stationId);
                StringBuilder sb = new StringBuilder("{\"success\":true,\"porters\":[");
                for (int i = 0; i < list.size(); i++) {
                    Porter p = list.get(i);
                    if (i > 0) sb.append(",");
                    sb.append("{\"porterId\":").append(p.getPorterId())
                      .append(",\"fullName\":\"").append(p.getFullName()).append("\"")
                      .append(",\"rating\":").append(p.getAverageRating())
                      .append(",\"experience\":").append(p.getExperienceYears())
                      .append(",\"phone\":\"").append(p.getPhone()).append("\"")
                      .append("}");
                }
                sb.append("]}");
                out.print(sb.toString());
            } catch (Exception e) {
                out.print("{\"success\":false}");
            }
        } else if ("/calculateFare".equals(path)) {
            try {
                int serviceTypeId = Integer.parseInt(req.getParameter("serviceTypeId"));
                int numBags = Integer.parseInt(req.getParameter("numBags"));
                boolean isHeavy = "1".equals(req.getParameter("isHeavy")) || "true".equalsIgnoreCase(req.getParameter("isHeavy"));
                java.math.BigDecimal fare = serviceTypeDAO.calculateFare(serviceTypeId, numBags, isHeavy);
                out.print("{\"success\":true,\"estimatedFare\":" + fare + "}");
            } catch (Exception e) {
                out.print("{\"success\":false,\"message\":\"Invalid parameters\"}");
            }
        } else {
            out.print("{\"success\":false,\"message\":\"Unknown endpoint\"}");
        }
    }
}