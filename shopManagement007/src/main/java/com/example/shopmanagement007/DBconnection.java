package com.example.shopmanagement007;

import java.sql.*;

public class DBconnection {
    String url = "jdbc:mysql://localhost:3306/shop_management";
    String username = "root";
    String password = "*#LonlyBoy00#*";

    public Connection dbConnection() {
        try{
            return DriverManager.getConnection(url, username, password);
        }catch (Exception e) {
            System.out.println("problem in connection");
            return null;
        }
        //Database Connection basic set up
    }

    public void insertData(String description, String code, String unitName, String unitPrice, String unitSalePrice, String quantity, String vat) {
        Connection conn = dbConnection();
        try(
                PreparedStatement ps = conn.prepareStatement("INSERT INTO stock_details(description, code, unit, price, salePrice, quantity, vat) VALUES(?,?,?,?,?,?,?)");
                ) {
            ps.setString(1, description);
            ps.setString(2, code);
            ps.setString(3, unitName);
            ps.setString(4, unitPrice);
            ps.setString(5, unitSalePrice);
            ps.setString(6, quantity);
            ps.setString(7, vat);

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("A new record has been inserted successfully.");
            }
        }
        catch (SQLException e) {
            System.out.println("problem in insert function");
        }
        //This method inserts in stock_details database
    }
}
