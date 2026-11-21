package controller;

import dao.PassengerDAO;
import dao.PorterDAO;
import dao.UserDAO;
import model.User;
import util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final PorterDAO porterDAO = new PorterDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String role = req.getParameter("role");
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String gender = req.getParameter("gender");
        String experience = req.getParameter("experience");

        if (role == null || fullName == null || email == null || phone == null || password == null) {
            out.print("{\"success\":false,\"message\":\"All required fields must be filled\"}");
            return;
        }

        if (password.length() < 6) {
            out.print("{\"success\":false,\"message\":\"Password must be at least 6 characters\"}");
            return;
        }

        if (userDAO.emailExists(email.trim())) {
            out.print("{\"success\":false,\"message\":\"Email already registered\"}");
            return;
        }
        if (userDAO.phoneExists(phone.trim())) {
            out.print("{\"success\":false,\"message\":\"Phone number already registered\"}");
            return;
        }

        try {
            String hash = PasswordUtil.hashPassword(password);
            User user = new User(email.trim(), hash, phone.trim(), role.toUpperCase());
            int userId = userDAO.createUser(user);
            if (userId <= 0) {
                out.print("{\"success\":false,\"message\":\"Registration failed\"}");
                return;
            }

            if ("PASSENGER".equalsIgnoreCase(role)) {
                passengerDAO.createPassenger(userId, fullName.trim(), gender);
            } else if ("PORTER".equalsIgnoreCase(role)) {
                int exp = 0;
                try { exp = Integer.parseInt(experience); } catch (Exception e) {}
                porterDAO.createPorter(userId, fullName.trim(), exp);
            } else {
                out.print("{\"success\":false,\"message\":\"Invalid role\"}");
                return;
            }

            out.print("{\"success\":true,\"message\":\"Registration successful! Please login.\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"message\":\"Database error during registration\"}");
        }
    }
}