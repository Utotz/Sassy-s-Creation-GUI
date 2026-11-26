package com.sassyscreation.sassyscreationgui.dao;

import com.sassyscreation.sassyscreationgui.db.DBConnection;
import com.sassyscreation.sassyscreationgui.model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // =====================================================================
    //  LOGIC: READ ALL ORDERS
    //  JOINs products to get each product's name. ORDER BY schedule_date DESC, order_id DESC
    //  so the newest scheduled order is first and same-day orders keep a stable order.
    //  READ-ONLY: this method changes nothing in the database.
    // =====================================================================

    public List<Order> getAllOrders() throws SQLException {
        String sql = "SELECT o.order_id, o.customer_name, o.product_id, p.name, o.color, "
                + "o.custom_description, o.quantity, o.unit_price, o.arrangement, "
                + "o.schedule_date, o.status "
                + "FROM orders o "
                + "JOIN products p ON p.product_id = o.product_id "
                + "ORDER BY o.schedule_date DESC, o.order_id DESC";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("color"),
                        rs.getString("custom_description"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("unit_price"),
                        rs.getString("arrangement"),
                        rs.getDate("schedule_date").toLocalDate(),
                        rs.getString("status")
                ));
            }
        }
        return orders;
    }   // getAllOrders ends here
}