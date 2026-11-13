package com.sassyscreation.sassyscreationgui;

import com.sassyscreation.sassyscreationgui.dao.ProductDAO;
import com.sassyscreation.sassyscreationgui.model.Product;

import java.sql.SQLException;

public class ProductDAOTest {
    public static void main(String[] args) {
        try {
            ProductDAO dao = new ProductDAO();
            for (Product p : dao.getAllProducts()) {
                System.out.println(p);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
