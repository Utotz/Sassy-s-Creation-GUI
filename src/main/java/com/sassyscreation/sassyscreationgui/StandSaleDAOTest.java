package com.sassyscreation.sassyscreationgui;

import com.sassyscreation.sassyscreationgui.dao.StandSaleDAO;

import java.sql.SQLException;
import java.time.LocalDate;

// WARNING: this test writes a real sale and deducts stock. Run once, then clean up.


public class StandSaleDAOTest {
    public static void main(String[] args) {
        StandSaleDAO dao = new StandSaleDAO();
        int productId = 2; // CHANGE to the product_id you chose (stock_quantity >= 2)

        // Test 1: a normal sale of 1 piece, should succeed
        try {
            int newId = dao.insertSale(productId, 1, LocalDate.now());
            System.out.println("Test 1 OK. New sale id: " + newId);
        } catch (SQLException e) {
            System.out.println("Test 1 FAILED: " + e.getMessage());
        }

        // Test 2: far more than the stock, must fail and change nothing
        try {
            dao.insertSale(productId, 999999, LocalDate.now());
            System.out.println("Test 2 ERROR: this should have failed");
        } catch (SQLException e) {
            System.out.println("Test 2 OK (expected failure): " + e.getMessage());
        }
    }
}