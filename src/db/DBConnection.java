package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/ecommerce_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Ishu@2007";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("DB Connection Failed: " + e.getMessage());
            return null;
        }
    }
}