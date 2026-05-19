package dao;

import db.DBConnection;
import model.CartItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDAO {

    public int placeOrder(int userId, List<CartItem> cartItems, double totalAmount) {
        String orderQuery = "INSERT INTO orders (user_id, total_amount) VALUES (?, ?)";
        String itemQuery  = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?,?,?,?)";
        String stockQuery = "UPDATE products SET stock = stock - ? WHERE product_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            PreparedStatement orderPs = con.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            orderPs.setInt(1, userId);
            orderPs.setDouble(2, totalAmount);
            orderPs.executeUpdate();

            ResultSet keys = orderPs.getGeneratedKeys();
            int orderId = -1;
            if (keys.next()) orderId = keys.getInt(1);

            for (CartItem item : cartItems) {
                PreparedStatement itemPs = con.prepareStatement(itemQuery);
                itemPs.setInt(1, orderId);
                itemPs.setInt(2, item.getProduct().getProductId());
                itemPs.setInt(3, item.getQuantity());
                itemPs.setDouble(4, item.getProduct().getPrice());
                itemPs.executeUpdate();

                PreparedStatement stockPs = con.prepareStatement(stockQuery);
                stockPs.setInt(1, item.getQuantity());
                stockPs.setInt(2, item.getProduct().getProductId());
                stockPs.executeUpdate();
            }

            con.commit();
            return orderId;

        } catch (SQLException e) {
            System.out.println("Order error: " + e.getMessage());
            return -1;
        }
    }

    public void printAllOrders() {
        String query = "SELECT o.order_id, u.name, o.order_date, o.total_amount " +
                "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                "ORDER BY o.order_date DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            System.out.println("\n All Orders:");
            System.out.println("-".repeat(65));
            System.out.printf("%-10s %-20s %-25s %-10s%n",
                    "Order ID", "Customer", "Date", "Amount");
            System.out.println("-".repeat(65));

            while (rs.next()) {
                System.out.printf("%-10d %-20s %-25s Rs.%-10.2f%n",
                        rs.getInt("order_id"),
                        rs.getString("name"),
                        rs.getString("order_date"),
                        rs.getDouble("total_amount")
                );
            }
        } catch (SQLException e) {
            System.out.println("Fetch orders error: " + e.getMessage());
        }
    }
    public List<Map<String, Object>> getAllOrdersAsMap() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query =
                "SELECT o.order_id, u.name AS \"customerName\", " +
                        "o.order_date, o.total_amount " +
                        "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                        "ORDER BY o.order_date DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("orderId",      rs.getInt("order_id"));
                row.put("customerName", rs.getString("customerName"));
                row.put("orderDate",    rs.getString("order_date"));
                row.put("totalAmount",  rs.getDouble("total_amount"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}