package controller;

import dao.PassengerDAO;
import dao.PorterDAO;
import dao.UserDAO;
import model.Passenger;
import model.Porter;
import model.User;
import util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final PorterDAO porterDAO = new PorterDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role"); // optional filter

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            out.print("{\"success\":false,\"message\":\"Email and password are required\"}");
            return;
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null || !user.isActive()) {
            out.print("{\"success\":false,\"message\":\"Invalid credentials\"}");
            return;
        }

        // Support both hashed and plain demo passwords for sample data
        boolean valid = PasswordUtil.verifyPassword(password, user.getPasswordHash())
                || password.equals(user.getPasswordHash()) // fallback if sample used plain
                || isDemoPassword(email, password);

        if (!valid) {
            out.print("{\"success\":false,\"message\":\"Invalid credentials\"}");
            return;
        }

        if (role != null && !role.isEmpty() && !user.getRole().equalsIgnoreCase(role)) {
            out.print("{\"success\":false,\"message\":\"Please login with the correct role\"}");
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("role", user.getRole());
        session.setAttribute("phone", user.getPhone());

        String redirect = "index.html";
        String name = "";

        if ("PASSENGER".equals(user.getRole())) {
            Passenger p = passengerDAO.findByUserId(user.getUserId());
            if (p != null) {
                session.setAttribute("passengerId", p.getPassengerId());
                session.setAttribute("fullName", p.getFullName());
                name = p.getFullName();
            }
            redirect = "passenger-dashboard.html";
        } else if ("PORTER".equals(user.getRole())) {
            Porter p = porterDAO.findByUserId(user.getUserId());
            if (p != null) {
                session.setAttribute("porterId", p.getPorterId());
                session.setAttribute("fullName", p.getFullName());
                session.setAttribute("verificationStatus", p.getVerificationStatus());
                name = p.getFullName();
            }
            redirect = "porter-dashboard.html";
        } else if ("ADMIN".equals(user.getRole())) {
            session.setAttribute("fullName", "Administrator");
            name = "Administrator";
            redirect = "admin-dashboard.html";
        }

        out.print("{\"success\":true,\"message\":\"Login successful\",\"role\":\"" + user.getRole() +
                "\",\"name\":\"" + name + "\",\"redirect\":\"" + redirect + "\"}");
    }

    private boolean isDemoPassword(String email, String password) {
        // Demo credentials for easy testing
        if (email.equals("admin@smartporter.com") && password.equals("admin123")) return true;
        if (email.endsWith("@email.com") && (password.equals("passenger123") || password.equals("porter123"))) return true;
        return false;
    }
}