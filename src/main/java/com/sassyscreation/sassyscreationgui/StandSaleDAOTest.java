package com.sassyscreation.sassyscreationgui;

import com.sassyscreation.sassyscreationgui.dao.StandSaleDAO;
import com.sassyscreation.sassyscreationgui.model.StandSale;

import java.sql.SQLException;

public class StandSaleDAOTest {
    public static void main(String[] args) {
        try {
            StandSaleDAO dao = new StandSaleDAO();
            for (StandSale s : dao.getAllSales()) {
                System.out.println(s);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}