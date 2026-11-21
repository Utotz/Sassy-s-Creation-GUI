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
    /** File name inside resources/.../images/ (e.g. "diamond-top.jpg") or a full file path; may be null. */
    private final String imagePath;

    /** Old constructor kept so existing code (e.g. ProductDAOTest) still compiles. */
    public Product(int productId, String name,
                   String category, BigDecimal price,
                   int stockQuantity, int lowStockThreshold, boolean customizable) {
        this(productId, name, category, price, stockQuantity, lowStockThreshold, customizable, null);
    }

    public Product(int productId, String name,
                   String category, BigDecimal price,
                   int stockQuantity, int lowStockThreshold, boolean customizable,
                   String imagePath) {

        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.lowStockThreshold = lowStockThreshold;
        this.customizable = customizable;
        this.imagePath = imagePath;
    }

    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public boolean isCustomizable() { return customizable; }
    public String getImagePath() { return imagePath; }

    @Override
    public String toString() {
        return "Product{id=" + productId +
                ", name='" + name +
                "', category='" + category +
                "', price=" + price +
                ", stock=" + stockQuantity +
                ", lowStockAt=" + lowStockThreshold +
                ", customizable=" + customizable +
                ", imagePath=" + imagePath + "}";
    }
}