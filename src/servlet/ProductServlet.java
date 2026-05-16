package servlet;

import com.google.gson.Gson;
import dao.ProductDAO;
import model.Product;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/ProductServlet")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");

        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.getAllProducts();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("products", products);

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