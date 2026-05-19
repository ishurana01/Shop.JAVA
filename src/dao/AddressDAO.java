package dao;

import db.DBConnection;
import model.Address;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

    // Get all addresses for a user
    public List<Address> getAddressesByUser(int userId) {
        List<Address> list = new ArrayList<>();
        String query = "SELECT * FROM addresses WHERE user_id = ? ORDER BY is_default DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Address(
                        rs.getInt("address_id"),
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("pincode"),
                        rs.getShort("is_default") == 1
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Save new address
    public boolean saveAddress(int userId, String fullName, String phone,
                               String street, String city, String state,
                               String pincode, boolean isDefault) {
        // if new address is default, unset all others first
        if (isDefault) unsetDefault(userId);

        String query = "INSERT INTO addresses (user_id, full_name, phone, street, city, state, pincode, is_default) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, fullName);
            ps.setString(3, phone);
            ps.setString(4, street);
            ps.setString(5, city);
            ps.setString(6, state);
            ps.setString(7, pincode);
            ps.setInt(8, isDefault ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete address
    public boolean deleteAddress(int addressId) {
        String query = "DELETE FROM addresses WHERE address_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, addressId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Set an address as default
    public boolean setDefault(int addressId, int userId) {
        unsetDefault(userId);
        String query = "UPDATE addresses SET is_default = 1 WHERE address_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, addressId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper — unset all defaults for a user
    private void unsetDefault(int userId) {
        String query = "UPDATE addresses SET is_default = 0 WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}