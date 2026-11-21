package controller;

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

@WebServlet("/api/porter/*")
public class PorterServlet extends HttpServlet {

    private final PorterDAO porterDAO = new PorterDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("porterId") == null) {
            out.print("{\"success\":false,\"message\":\"Please login as porter\"}");
            return;
        }
        int porterId = (Integer) session.getAttribute("porterId");
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";

        if ("/requests".equals(path)) {
            List<Booking> pending = bookingDAO.findPendingForPorter(porterId);
            out.print(listToJson(pending));
        } else if ("/mybookings".equals(path)) {
            List<Booking> list = bookingDAO.findByPorter(porterId);
            out.print(listToJson(list));
        } else if ("/profile".equals(path)) {
            Porter p = porterDAO.findById(porterId);
            if (p != null) {
                out.print("{\"success\":true,\"porter\":{" +
                        "\"porterId\":" + p.getPorterId() + "," +
                        "\"fullName\":\"" + p.getFullName() + "\"," +
                        "\"experience\":" + p.getExperienceYears() + "," +
                        "\"rating\":" + p.getAverageRating() + "," +
                        "\"totalRatings\":" + p.getTotalRatings() + "," +
                        "\"available\":" + p.isAvailable() + "," +
                        "\"verificationStatus\":\"" + p.getVerificationStatus() + "\"," +
                        "\"stationName\":\"" + (p.getStationName() != null ? p.getStationName() : "") + "\"" +
                        "}}");
            } else {
                out.print("{\"success\":false}");
            }
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
        if (session == null || session.getAttribute("porterId") == null) {
            out.print("{\"success\":false,\"message\":\"Please login as porter\"}");
            return;
        }
        int porterId = (Integer) session.getAttribute("porterId");
        int userId = (Integer) session.getAttribute("userId");

        String action = req.getParameter("action");

        if ("toggleAvailability".equals(action)) {
            Porter p = porterDAO.findById(porterId);
            boolean newState = !p.isAvailable();
            boolean ok = porterDAO.updateAvailability(porterId, newState);
            out.print("{\"success\":" + ok + ",\"available\":" + newState + "}");
        } else if ("accept".equals(action)) {
            int bookingId = Integer.parseInt(req.getParameter("bookingId"));
            boolean ok = bookingDAO.assignPorter(bookingId, porterId);
            out.print("{\"success\":" + ok + ",\"message\":\"" + (ok ? "Booking accepted" : "Could not accept (already taken or unavailable)") + "\"}");
        } else if ("updateStatus".equals(action)) {
            int bookingId = Integer.parseInt(req.getParameter("bookingId"));
            String status = req.getParameter("status");
            boolean ok = bookingDAO.updateStatus(bookingId, status, userId, "Updated by porter");
            out.print("{\"success\":" + ok + "}");
        } else if ("setStation".equals(action)) {
            int stationId = Integer.parseInt(req.getParameter("stationId"));
            boolean ok = porterDAO.setStation(porterId, stationId);
            out.print("{\"success\":" + ok + "}");
        } else {
            out.print("{\"success\":false,\"message\":\"Unknown action\"}");
        }
    }

    private String listToJson(List<Booking> list) {
        StringBuilder sb = new StringBuilder("{\"success\":true,\"bookings\":[");
        for (int i = 0; i < list.size(); i++) {
            Booking b = list.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"bookingId\":").append(b.getBookingId())
              .append(",\"bookingCode\":\"").append(b.getBookingCode()).append("\"")
              .append(",\"status\":\"").append(b.getStatus()).append("\"")
              .append(",\"passengerName\":\"").append(b.getPassengerName() != null ? b.getPassengerName() : "").append("\"")
              .append(",\"pickupLocation\":\"").append(b.getPickupLocation() != null ? b.getPickupLocation() : "").append("\"")
              .append(",\"dropLocation\":\"").append(b.getDropLocation() != null ? b.getDropLocation() : "").append("\"")
              .append(",\"estimatedFare\":").append(b.getEstimatedFare())
              .append(",\"numberOfBags\":").append(b.getNumberOfBags())
              .append(",\"serviceName\":\"").append(b.getServiceName() != null ? b.getServiceName() : "").append("\"")
              .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }
}