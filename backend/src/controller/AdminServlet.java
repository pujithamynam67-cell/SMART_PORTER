package controller;

import dao.AdminDAO;
import dao.BookingDAO;
import dao.PorterDAO;
import model.Booking;
import model.Porter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();
    private final PorterDAO porterDAO = new PorterDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            out.print("{\"success\":false,\"message\":\"Admin access required\"}");
            return;
        }

        String path = req.getPathInfo() != null ? req.getPathInfo() : "/stats";

        if ("/stats".equals(path)) {
            Map<String, Object> stats = adminDAO.getDashboardStats();
            StringBuilder sb = new StringBuilder("{\"success\":true,\"stats\":{");
            boolean first = true;
            for (Map.Entry<String, Object> e : stats.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(e.getKey()).append("\":");
                Object v = e.getValue();
                if (v instanceof String) sb.append("\"").append(v).append("\"");
                else sb.append(v);
            }
            sb.append("}}");
            out.print(sb.toString());
        } else if ("/porters".equals(path)) {
            List<Porter> list = porterDAO.findAll();
            StringBuilder sb = new StringBuilder("{\"success\":true,\"porters\":[");
            for (int i = 0; i < list.size(); i++) {
                Porter p = list.get(i);
                if (i > 0) sb.append(",");
                sb.append("{\"porterId\":").append(p.getPorterId())
                  .append(",\"fullName\":\"").append(p.getFullName()).append("\"")
                  .append(",\"email\":\"").append(p.getEmail()).append("\"")
                  .append(",\"phone\":\"").append(p.getPhone()).append("\"")
                  .append(",\"experience\":").append(p.getExperienceYears())
                  .append(",\"verificationStatus\":\"").append(p.getVerificationStatus()).append("\"")
                  .append(",\"available\":").append(p.isAvailable())
                  .append(",\"rating\":").append(p.getAverageRating())
                  .append("}");
            }
            sb.append("]}");
            out.print(sb.toString());
        } else if ("/bookings".equals(path)) {
            List<Booking> list = bookingDAO.findAll();
            StringBuilder sb = new StringBuilder("{\"success\":true,\"bookings\":[");
            for (int i = 0; i < list.size(); i++) {
                Booking b = list.get(i);
                if (i > 0) sb.append(",");
                sb.append("{\"bookingId\":").append(b.getBookingId())
                  .append(",\"bookingCode\":\"").append(b.getBookingCode()).append("\"")
                  .append(",\"status\":\"").append(b.getStatus()).append("\"")
                  .append(",\"passengerName\":\"").append(b.getPassengerName() != null ? b.getPassengerName() : "").append("\"")
                  .append(",\"porterName\":\"").append(b.getPorterName() != null ? b.getPorterName() : "").append("\"")
                  .append(",\"stationName\":\"").append(b.getStationName() != null ? b.getStationName() : "").append("\"")
                  .append(",\"estimatedFare\":").append(b.getEstimatedFare())
                  .append(",\"bookedAt\":\"").append(b.getBookedAt() != null ? b.getBookedAt().toString() : "").append("\"")
                  .append("}");
            }
            sb.append("]}");
            out.print(sb.toString());
        } else {
            out.print("{\"success\":false,\"message\":\"Unknown endpoint\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            out.print("{\"success\":false,\"message\":\"Admin access required\"}");
            return;
        }
        int adminUserId = (Integer) session.getAttribute("userId");

        String action = req.getParameter("action");
        if ("verifyPorter".equals(action)) {
            int porterId = Integer.parseInt(req.getParameter("porterId"));
            String status = req.getParameter("status"); // VERIFIED or REJECTED
            boolean ok = porterDAO.updateVerification(porterId, status, adminUserId);
            out.print("{\"success\":" + ok + "}");
        } else {
            out.print("{\"success\":false,\"message\":\"Unknown action\"}");
        }
    }
}