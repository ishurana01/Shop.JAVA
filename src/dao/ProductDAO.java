package dao;

import db.DBConnection;
import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM products WHERE stock > 0";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("image_url")
                        ));
            }
        } catch (SQLException e) {
            System.out.println("Fetch products error: " + e.getMessage());
        }
        return list;
    }

    public Product getProductById(int id) {
        String query = "SELECT * FROM products WHERE product_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("image_url")
                );
            }
        } catch (SQLException e) {
            System.out.println("Get product error: " + e.getMessage());
        }
        return null;
    }

    public boolean addProduct(String name, String category,
                              double price, int stock, String imageUrl) {
        String query = "INSERT INTO products (name, category, price, stock, image_url) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setInt(4, stock);
            ps.setString(5, imageUrl != null ? imageUrl : "");
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Add product error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStock(int productId, int newStock) {
        String query = "UPDATE products SET stock=? WHERE product_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, newStock);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Update stock error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        String query = "DELETE FROM products WHERE product_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
            return false;
        }
    }
}