package com.sassyscreation.sassyscreationgui.dao;

import com.sassyscreation.sassyscreationgui.db.DBConnection;
import com.sassyscreation.sassyscreationgui.model.Product;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // =====================================================================
    //  LOGIC: READ ALL PRODUCTS
    //  Used by Inventory and Stand Sale. Sorted by name; image_path may be null.
    // =====================================================================

    public List<Product> getAllProducts() throws SQLException {
        String sql = "SELECT product_id, name, category, price, "
                + "stock_quantity, low_stock_threshold, is_customizable, image_path "
                + "FROM products WHERE is_active = 1 ORDER BY name";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("low_stock_threshold"),
                        rs.getBoolean("is_customizable"),
                        rs.getString("image_path")
                ));
            }
        }
        return products;
    }


    // =====================================================================
    //  LOGIC: ADD A PRODUCT
    //  Parameterized INSERT (never string-concatenated). Returns the generated id,
    //  or -1 if the database returned none.
    // =====================================================================

    /** Inserts a new product and returns its generated id. imagePath may be null. */
    public int insertProduct(String name, String category, BigDecimal price,
                             int stock, String imagePath) throws SQLException {
        String sql = "INSERT INTO products (name, category, price, stock_quantity, image_path) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, category);
            ps.setBigDecimal(3, price);
            ps.setInt(4, stock);
            ps.setString(5, imagePath);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }


    // =====================================================================
    //  LOGIC: RESTOCK A PRODUCT
    //  Adds `quantity` to the current stock in one atomic UPDATE, so it is
    //  always correct even if the stock changed since the list was loaded.
    //  Returns true if a product row was updated.
    // =====================================================================

    public boolean addStock(int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        }
    }
}