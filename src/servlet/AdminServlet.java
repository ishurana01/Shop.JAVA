package servlet;

import com.google.gson.Gson;
import dao.ProductDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");

        String action = req.getParameter("action");
        ProductDAO dao = new ProductDAO();
        boolean success = false;

        switch (action) {
            case "add":
                success = dao.addProduct(
                        req.getParameter("name"),
                        req.getParameter("category"),
                        Double.parseDouble(req.getParameter("price")),
                        Integer.parseInt(req.getParameter("stock")),
                        req.getParameter("imageUrl")
                );
                break;
            case "update":
                success = dao.updateStock(
                        Integer.parseInt(req.getParameter("productId")),
                        Integer.parseInt(req.getParameter("stock"))
                );
                break;
            case "delete":
                success = dao.deleteProduct(
                        Integer.parseInt(req.getParameter("productId"))
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