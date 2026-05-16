package servlet;

import com.google.gson.Gson;
import dao.AddressDAO;
import model.Address;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/AddressServlet")
public class AddressServlet extends HttpServlet {

    // GET — fetch all addresses for a user
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");

        int userId = Integer.parseInt(req.getParameter("userId"));
        AddressDAO dao = new AddressDAO();
        List<Address> addresses = dao.getAddressesByUser(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("addresses", addresses);
        res.getWriter().write(new Gson().toJson(result));
    }

    // POST — save, delete, or set default
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");

        String action = req.getParameter("action");
        AddressDAO dao = new AddressDAO();
        boolean success = false;

        switch (action) {
            case "save":
                success = dao.saveAddress(
                        Integer.parseInt(req.getParameter("userId")),
                        req.getParameter("fullName"),
                        req.getParameter("phone"),
                        req.getParameter("street"),
                        req.getParameter("city"),
                        req.getParameter("state"),
                        req.getParameter("pincode"),
                        "1".equals(req.getParameter("isDefault"))
                );
                break;
            case "delete":
                success = dao.deleteAddress(
                        Integer.parseInt(req.getParameter("addressId"))
                );
                break;
            case "setDefault":
                success = dao.setDefault(
                        Integer.parseInt(req.getParameter("addressId")),
                        Integer.parseInt(req.getParameter("userId"))
                );
                break;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
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