package servlet;

import com.google.gson.Gson;
import dao.UserDAO;
import model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");

        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(email, password);

        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            result.put("success", true);
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.getUserId());
            userData.put("name",   user.getName());
            userData.put("email",  user.getEmail());
            userData.put("role",   user.getRole());
            result.put("user", userData);
        } else {
            result.put("success", false);
            result.put("message", "Invalid email or password");
        }

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