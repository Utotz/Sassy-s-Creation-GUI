package com.sassyscreation.sassyscreationgui.dao;

import com.sassyscreation.sassyscreationgui.db.DBConnection;
import com.sassyscreation.sassyscreationgui.model.StandSale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    }
}