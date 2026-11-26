package com.sassyscreation.sassyscreationgui;

import com.sassyscreation.sassyscreationgui.dao.OrderDAO;
import com.sassyscreation.sassyscreationgui.model.Order;

import java.sql.SQLException;
import java.util.List;

// =====================================================================
//  READ-ONLY TEST: this writes nothing to the database.
//  It only prints every order, so it is safe to run as many times as you like.
// =====================================================================

public class OrderDAOTest {
    public static void main(String[] args) {
        OrderDAO dao = new OrderDAO();
        try {
            List<Order> orders = dao.getAllOrders();
            System.out.println("Orders found: " + orders.size());
            for (Order o : orders) {
                System.out.println(o);
            }
        } catch (SQLException e) {
            System.out.println("FAILED: " + e.getMessage());
        }
    }
}