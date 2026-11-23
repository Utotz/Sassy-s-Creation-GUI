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

    /** All stand sales, newest first. Sales on the same date: highest sale_id first. */
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

    /** Records a stand sale and deducts stock in ONE transaction. Returns the new sale_id. */
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
}