package com.sassyscreation.sassyscreationgui;

import com.sassyscreation.sassyscreationgui.dao.OrderDAO;
import com.sassyscreation.sassyscreationgui.model.Order;

import java.sql.SQLException;
import java.util.List;

// =====================================================================
//  READ-ONLY TEST: this writes nothing to the database.
//  It only prints orders, the status summary and the completed revenue,
//  so it is safe to run as many times as you like.
// =====================================================================

public class OrderDAOTest {
    public static void main(String[] args) {
        OrderDAO dao = new OrderDAO();
        try {
            List<Order> orders = dao.getAllOrders();
            System.out.println("All orders: " + orders.size());
            for (Order o : orders) {
                System.out.println(o);
            }

            System.out.println("Status summary: " + dao.getStatusSummary());

            List<Order> completed = dao.getCompletedOrders();
            System.out.println("Completed orders: " + completed.size());
            for (Order o : completed) {
                System.out.println(o);
            }
            System.out.println("Completed revenue: " + dao.getCompletedRevenue());
        } catch (SQLException e) {
            System.out.println("FAILED: " + e.getMessage());
        }
    }
}