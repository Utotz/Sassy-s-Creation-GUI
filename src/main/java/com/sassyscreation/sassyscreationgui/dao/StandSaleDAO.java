package com.sassyscreation.sassyscreationgui.dao;

import com.sassyscreation.sassyscreationgui.db.DBConnection;
import com.sassyscreation.sassyscreationgui.model.StandSale;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StandSaleDAO {

    // =====================================================================
    //  LOGIC: READ ALL STAND SALES
    //  JOINs products to get each product's name. ORDER BY sale_date DESC, sale_id DESC
    //  so the newest sale is first and same-day sales keep a stable order.
    // =====================================================================

    public List<StandSale> getAllSales() throws SQLException {
        String sql = "SELECT s.sale_id, s.product_id, p.name, s.quantity, "
                + "s.unit_price, s.sale_date "
                + "FROM stand_sales s "
                + "JOIN products p ON p.product_id = s.product_id "
                + "ORDER BY s.sale_date DESC, s.sale_id DESC";
        List<StandSale> sales = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sales.add(new StandSale(
                        rs.getInt("sale_id"),
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("unit_price"),
                        rs.getDate("sale_date").toLocalDate()
                ));
            }
        }
        return sales;
    }   // getAllSales ends here

    // =====================================================================
    //  LOGIC RESPONSIBLE FOR RECORDING A SALE AND DEDUCTING STOCK (TRANSACTION)
    //  Everything below uses ONE connection with autocommit off, so it is all-or-nothing:
    //    1. UPDATE products SET stock_quantity = stock_quantity - ?  ... AND stock_quantity >= ?
    //       0 rows updated = not enough stock (or no such product) -> exception
    //    2. SELECT price FROM products   (the price comes from the database, never from the screen)
    //    3. INSERT INTO stand_sales      (unit_price = the price read in step 2)
    //    4. commit()  - or rollback() if a SQLException occurred, so the stock is never lost
    //  The connection is closed by try-with-resources.
    // =====================================================================


    public int insertSale(int productId, int quantity, LocalDate saleDate) throws SQLException {
        String deductSql = "UPDATE products SET stock_quantity = stock_quantity - ? "
                + "WHERE product_id = ? AND stock_quantity >= ?";
        String priceSql = "SELECT price FROM products WHERE product_id = ?";
        String insertSql = "INSERT INTO stand_sales (product_id, quantity, unit_price, sale_date) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);          // start the transaction
            try {
                try (PreparedStatement ps = conn.prepareStatement(deductSql)) {
                    ps.setInt(1, quantity);
                    ps.setInt(2, productId);
                    ps.setInt(3, quantity);
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Not enough stock, or the product does not exist.");
                    }
                }

                BigDecimal unitPrice;
                try (PreparedStatement ps = conn.prepareStatement(priceSql)) {
                    ps.setInt(1, productId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Product not found.");
                        }
                        unitPrice = rs.getBigDecimal("price");
                    }
                }

                int newSaleId;
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, quantity);
                    ps.setBigDecimal(3, unitPrice);
                    ps.setDate(4, java.sql.Date.valueOf(saleDate));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        newSaleId = keys.getInt(1);
                    }
                }

                conn.commit();                  // both changes become permanent together
                return newSaleId;
            } catch (SQLException e) {
                conn.rollback();                // undo the stock change if anything failed
                throw e;
            }
        }
    }   // insertSale ends here
    // =====================================================================
    //  LOGIC RESPONSIBLE FOR DELETING A SALE AND GIVING THE STOCK BACK (TRANSACTION)
    //  Mirror of insertSale: ONE connection, autocommit off, all-or-nothing.
    //    1. SELECT product_id, quantity FROM stand_sales  (what to give back, read from the database)
    //       no row = the sale is already gone -> exception
    //    2. UPDATE products SET stock_quantity = stock_quantity + ?
    //    3. DELETE FROM stand_sales WHERE sale_id = ?
    //    4. commit() - or rollback() on any SQLException, so stock and sales never disagree
    // =====================================================================

    public void deleteSale(int saleId) throws SQLException {
        String findSql = "SELECT product_id, quantity FROM stand_sales WHERE sale_id = ?";
        String restoreSql = "UPDATE products SET stock_quantity = stock_quantity + ? "
                + "WHERE product_id = ?";
        String deleteSql = "DELETE FROM stand_sales WHERE sale_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);          // start the transaction
            try {
                int productId;
                int quantity;
                try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                    ps.setInt(1, saleId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Sale not found. It may already have been deleted.");
                        }
                        productId = rs.getInt("product_id");
                        quantity = rs.getInt("quantity");
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(restoreSql)) {
                    ps.setInt(1, quantity);
                    ps.setInt(2, productId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                    ps.setInt(1, saleId);
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Sale not found. It may already have been deleted.");
                    }
                }

                conn.commit();                  // stock back AND row gone, together
            } catch (SQLException e) {
                conn.rollback();                // undo the stock change if anything failed
                throw e;
            }
        }
    }   // deleteSale ends here
}