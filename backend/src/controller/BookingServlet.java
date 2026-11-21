package controller;

import dao.BookingDAO;
import dao.PorterDAO;
import dao.ServiceTypeDAO;
import model.Booking;
import model.Porter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/api/booking/*")
public class BookingServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final ServiceTypeDAO serviceTypeDAO = new ServiceTypeDAO();
    private final PorterDAO porterDAO = new PorterDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("passengerId") == null) {
            out.print("{\"success\":false,\"message\":\"Please login as passenger\"}");
            return;
        }
        int passengerId = (Integer) session.getAttribute("passengerId");

        String action = req.getParameter("action");
        if (action == null) action = "create";

        if ("create".equals(action)) {
            try {
                int stationId = Integer.parseInt(req.getParameter("stationId"));
                int pickupId = Integer.parseInt(req.getParameter("pickupId"));
                int dropId = Integer.parseInt(req.getParameter("dropId"));
                int serviceTypeId = Integer.parseInt(req.getParameter("serviceTypeId"));
                int numBags = Integer.parseInt(req.getParameter("numBags"));
                String luggageType = req.getParameter("luggageType");
                if (luggageType == null) luggageType = "General";
                BigDecimal weight = new BigDecimal(req.getParameter("weight") != null ? req.getParameter("weight") : "0");
                boolean isHeavy = "1".equals(req.getParameter("isHeavy")) || "true".equalsIgnoreCase(req.getParameter("isHeavy"));
                String specialHandling = req.getParameter("specialHandling");
                String instructions = req.getParameter("instructions");

                BigDecimal fare = serviceTypeDAO.calculateFare(serviceTypeId, numBags, isHeavy);

                int bookingId = bookingDAO.createBooking(passengerId, stationId, pickupId, dropId,
                        serviceTypeId, numBags, luggageType, weight, isHeavy, specialHandling, instructions, fare);

                if (bookingId > 0) {
                    Booking b = bookingDAO.findById(bookingId);
                    out.print("{\"success\":true,\"message\":\"Booking created\",\"bookingId\":" + bookingId +
                            ",\"bookingCode\":\"" + b.getBookingCode() + "\",\"estimatedFare\":" + fare + "}");
                } else {
                    out.print("{\"success\":false,\"message\":\"Failed to create booking\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                out.print("{\"success\":false,\"message\":\"Invalid input data\"}");
            }
        } else if ("cancel".equals(action)) {
            int bookingId = Integer.parseInt(req.getParameter("bookingId"));
            boolean ok = bookingDAO.updateStatus(bookingId, "CANCELLED",
                    (Integer) session.getAttribute("userId"), "Cancelled by passenger");
            out.print("{\"success\":" + ok + ",\"message\":\"" + (ok ? "Booking cancelled" : "Cancel failed") + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        if (session == null) {
            out.print("{\"success\":false,\"message\":\"Not logged in\"}");
            return;
        }

        String path = req.getPathInfo();
        if (path == null) path = "/";

        if (path.equals("/my") || path.equals("/passenger")) {
            Integer passengerId = (Integer) session.getAttribute("passengerId");
            if (passengerId == null) {
                out.print("{\"success\":false,\"message\":\"Not a passenger\"}");
                return;
            }
            List<Booking> list = bookingDAO.findByPassenger(passengerId);
            out.print(toJsonList(list));
        } else if (path.startsWith("/")) {
            try {
                int id = Integer.parseInt(path.substring(1));
                Booking b = bookingDAO.findById(id);
                if (b != null) out.print(toJson(b));
                else out.print("{\"success\":false,\"message\":\"Not found\"}");
            } catch (NumberFormatException e) {
                out.print("{\"success\":false,\"message\":\"Invalid id\"}");
            }
        }
    }

    private String toJsonList(List<Booking> list) {
        StringBuilder sb = new StringBuilder("{\"success\":true,\"bookings\":[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJsonObject(list.get(i)));
        }
        sb.append("]}");
        return sb.toString();
    }

    private String toJson(Booking b) {
        return "{\"success\":true,\"booking\":" + toJsonObject(b) + "}";
    }

    private String toJsonObject(Booking b) {
        return "{" +
                "\"bookingId\":" + b.getBookingId() + "," +
                "\"bookingCode\":\"" + esc(b.getBookingCode()) + "\"," +
                "\"status\":\"" + esc(b.getStatus()) + "\"," +
                "\"estimatedFare\":" + b.getEstimatedFare() + "," +
                "\"finalFare\":" + (b.getFinalFare() != null ? b.getFinalFare() : "null") + "," +
                "\"passengerName\":\"" + esc(b.getPassengerName()) + "\"," +
                "\"porterName\":\"" + esc(b.getPorterName()) + "\"," +
                "\"stationName\":\"" + esc(b.getStationName()) + "\"," +
                "\"pickupLocation\":\"" + esc(b.getPickupLocation()) + "\"," +
                "\"dropLocation\":\"" + esc(b.getDropLocation()) + "\"," +
                "\"serviceName\":\"" + esc(b.getServiceName()) + "\"," +
                "\"numberOfBags\":" + b.getNumberOfBags() + "," +
                "\"bookedAt\":\"" + (b.getBookedAt() != null ? b.getBookedAt().toString() : "") + "\"," +
                "\"paymentStatus\":\"" + esc(b.getPaymentStatus()) + "\"" +
                "}";
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", " ");
    }
}