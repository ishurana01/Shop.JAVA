package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import dao.OrderDAO;
import model.CartItem;
import model.Product;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import service.EmailService;

import java.io.*;
import java.util.*;

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws  IOException {

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }

        Gson gson = new Gson();
        JsonObject body = gson.fromJson(sb.toString(), JsonObject.class);

        int userId         = body.get("userId").getAsInt();
        double totalAmount = body.get("totalAmount").getAsDouble();
        JsonArray cartArray = body.getAsJsonArray("cart");

        List<CartItem> cartItems = new ArrayList<>();
        for (int i = 0; i < cartArray.size(); i++) {
            JsonObject item = cartArray.get(i).getAsJsonObject();
            Product p = new Product(
                    item.get("productId").getAsInt(),
                    item.get("name").getAsString(),
                    item.get("category").getAsString(),
                    item.get("price").getAsDouble(),
                    item.get("stock").getAsInt(),
                    item.has("imageUrl") ? item.get("imageUrl").getAsString() : ""
            );
            cartItems.add(new CartItem(p, item.get("qty").getAsInt()));
        }

        OrderDAO orderDAO = new OrderDAO();
        int orderId = orderDAO.placeOrder(userId, cartItems, totalAmount);

        if (orderId != -1) {
            String customerName  = body.has("customerName")
                    ? body.get("customerName").getAsString() : "Customer";
            String customerEmail = body.has("customerEmail")
                    ? body.get("customerEmail").getAsString() : "";


            // Extract address
            String addressText = "Not provided";
            if (body.has("address") && !body.get("address").isJsonNull()) {
                try {
                    JsonObject addr = body.getAsJsonObject("address");

                    String fullName = addr.has("fullName") ? addr.get("fullName").getAsString() : "";
                    String phone    = addr.has("phone")    ? addr.get("phone").getAsString()    : "";
                    String street   = addr.has("street")   ? addr.get("street").getAsString()   : "";
                    String city     = addr.has("city")     ? addr.get("city").getAsString()     : "";
                    String state    = addr.has("state")    ? addr.get("state").getAsString()    : "";
                    String pincode  = addr.has("pincode")  ? addr.get("pincode").getAsString()  : "";

                    // Log for debugging
                    System.out.println("Address received: " + addr.toString());

                    if (!fullName.isEmpty()) {
                        addressText = fullName + "\n" + phone + "\n" +
                                street   + ", " + city  + ",\n" +
                                state    + " — " + pincode;
                    }
                } catch(Exception e) {
                    System.out.println("Address parse error: " + e.getMessage());
                }
            }

            final String finalAddress = addressText;
            final int finalOrderId    = orderId;

            new Thread(() -> {
                EmailService emailService = new EmailService();
                emailService.sendInvoiceEmail(
                        finalOrderId, customerName,
                        customerEmail, cartItems, finalAddress  // ← pass address
                );
            }).start();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", orderId != -1);
        result.put("orderId", orderId);

        res.getWriter().write(gson.toJson(result));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws  IOException {

        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");

        OrderDAO orderDAO = new OrderDAO();
        List<Map<String, Object>> orders = orderDAO.getAllOrdersAsMap();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("orders", orders);

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