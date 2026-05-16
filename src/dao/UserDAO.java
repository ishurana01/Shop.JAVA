package dao;

import db.DBConnection;
import model.User;
import java.sql.*;

public class UserDAO {

    public User login(String email, String password) {
        String query = "SELECT * FROM users WHERE email=? AND password=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }

    public boolean register(String name, String email, String password) {
        String query = "INSERT INTO users (name, email, password, role) VALUES (?,?,?,'customer')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Register error: " + e.getMessage());
            return false;
        }
    }
}