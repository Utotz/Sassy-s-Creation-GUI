package com.sassyscreation.sassyscreationgui.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StandSale {

    private final int saleId;
    private final int productId;
    /** Product name from the products table (joined when loading), used for display. */
    private final String productName;
    private final int quantity;
    /** Price per piece at the moment of the sale. */
    private final BigDecimal unitPrice;
    private final LocalDate saleDate;

    public StandSale(int saleId, int productId, String productName,
                     int quantity, BigDecimal unitPrice, LocalDate saleDate) {
        this.saleId = saleId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.saleDate = saleDate;
    }

    public int getSaleId() { return saleId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public LocalDate getSaleDate() { return saleDate; }

    // =====================================================================
    //  LOGIC: DERIVED VALUE
    //  The total of a sale is calculated from quantity and unit price, never stored
    //  (normalization: one source of truth, decision L40).
    // =====================================================================

    /** quantity x unitPrice. Calculated here, not stored in the database. */
    public BigDecimal getTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "StandSale{id=" + saleId +
                ", productId=" + productId +
                ", product='" + productName +
                "', qty=" + quantity +
                ", unitPrice=" + unitPrice +
                ", total=" + getTotal() +
                ", date=" + saleDate + "}";
    }
}