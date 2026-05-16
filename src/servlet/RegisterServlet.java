package servlet;

import com.google.gson.Gson;
import dao.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");

        String name     = req.getParameter("name");
        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.register(name, email, password);

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "Registered successfully" : "Email already exists");

        res.getWriter().write(new Gson().toJson(result));
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
        res.setStatus(200);
    }
}