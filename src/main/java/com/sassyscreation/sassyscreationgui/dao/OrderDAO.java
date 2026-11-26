package com.sassyscreation.sassyscreationgui.dao;

import com.sassyscreation.sassyscreationgui.db.DBConnection;
import com.sassyscreation.sassyscreationgui.model.Order;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderDAO {

    /** The SELECT and JOIN shared by the order lists; each method adds its own WHERE / ORDER BY. */
    private static final String ORDER_SELECT = "SELECT o.order_id, o.customer_name, o.product_id, p.name, o.color, "
            + "o.custom_description, o.quantity, o.unit_price, o.arrangement, "
            + "o.schedule_date, o.status "
            + "FROM orders o "
            + "JOIN products p ON p.product_id = o.product_id ";

    // =====================================================================
    //  LOGIC: READ ALL ORDERS
    //  JOINs products to get each product's name. ORDER BY schedule_date DESC, order_id DESC
    //  so the newest scheduled order is first and same-day orders keep a stable order.
    //  READ-ONLY: this method changes nothing in the database.
    // =====================================================================

    public List<Order> getAllOrders() throws SQLException {
        String sql = ORDER_SELECT + "ORDER BY o.schedule_date DESC, o.order_id DESC";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(mapOrder(rs));
            }
        }
        return orders;
    }   // getAllOrders ends here

    // =====================================================================
    //  LOGIC RESPONSIBLE FOR THE COMPLETED ORDERS LIST
    //  Same SELECT as getAllOrders, but only status = 'Done'. This is a query on the
    //  orders table, not a separate table (decision L38). READ-ONLY.
    // =====================================================================

    public List<Order> getCompletedOrders() throws SQLException {
        String sql = ORDER_SELECT + "WHERE o.status = ? "
                + "ORDER BY o.schedule_date DESC, o.order_id DESC";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Done");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }
        return orders;
    }   // getCompletedOrders ends here

    // =====================================================================
    //  LOGIC RESPONSIBLE FOR THE COMPLETED ORDERS REVENUE
    //  SUM(quantity * unit_price) over Done orders only. COALESCE turns "no Done orders"
    //  (SUM = NULL) into 0. An order counts on its schedule_date (decision L39).
    //  READ-ONLY.
    // =====================================================================

    public BigDecimal getCompletedRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(quantity * unit_price), 0) AS revenue "
                + "FROM orders WHERE status = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Done");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("revenue");
                }
            }
        }
        return BigDecimal.ZERO;
    }   // getCompletedRevenue ends here

    // =====================================================================
    //  LOGIC RESPONSIBLE FOR THE DONE / PENDING / CANCELLED SUMMARY
    //  COUNT(*) ... GROUP BY status gives one row per status that has orders. A status with
    //  no orders returns no row, so the map starts at 0 for all three and the query result
    //  overwrites them. LinkedHashMap keeps the order Pending, Done, Cancelled. READ-ONLY.
    // =====================================================================

    public Map<String, Integer> getStatusSummary() throws SQLException {
        String sql = "SELECT status, COUNT(*) AS total FROM orders GROUP BY status";
        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("Pending", 0);
        summary.put("Done", 0);
        summary.put("Cancelled", 0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                summary.put(rs.getString("status"), rs.getInt("total"));
            }
        }
        return summary;
    }   // getStatusSummary ends here

    // =====================================================================
    //  LOGIC: TURN ONE DATABASE ROW INTO AN Order OBJECT
    //  Used by every method above that returns Order objects, so the column-to-field
    //  mapping exists in one place only. custom_description may be NULL (getString gives null).
    // =====================================================================

    private Order mapOrder(ResultSet rs) throws SQLException {
        return new Order(
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
        );
    }   // mapOrder ends here
}