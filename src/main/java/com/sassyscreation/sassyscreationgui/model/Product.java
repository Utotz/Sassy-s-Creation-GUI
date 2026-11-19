package com.sassyscreation.sassyscreationgui.model;

import java.math.BigDecimal;

public class Product {

    private final int productId;
    private final String name;
    private final String category;
    private final BigDecimal price;
    private final int stockQuantity;
    private final int lowStockThreshold;
    private final boolean customizable;

    public Product (int productId, String name,
                    String category, BigDecimal price,
                    int stockQuantity, int lowStockThreshold, boolean customizable) {

        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.lowStockThreshold = lowStockThreshold;
        this.customizable = customizable;
    }

    public int getProductId() {return productId; }
    public String getName() {return name; }
    public String getCategory() {return category; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() {return stockQuantity; }
    public int getLowStockThreshold() {return  lowStockThreshold; }
    public boolean isCustomizable() {return customizable; }

    @Override
    public String toString() {
        return "Product{id=" + productId +
                ", name='" + name +
                "', category='" + category +
                "', price=" + price +
                ",stock=" + stockQuantity +
                ", lowStockAt=" + lowStockThreshold +
                ", customizable=" + customizable + "}";
    }
}
