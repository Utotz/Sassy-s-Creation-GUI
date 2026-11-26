package com.sassyscreation.sassyscreationgui.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Order {

    private final int orderId;
    private final String customerName;
    private final int productId;
    /** Product name from the products table (joined when loading), used for display. */
    private final String productName;
    private final String color;
    /** Customization text; null when the order has none. */
    private final String customDescription;
    private final int quantity;
    /** Agreed price per piece for this order. */
    private final BigDecimal unitPrice;
    private final String arrangement;   // 'Pickup' or 'Meetup'
    private final LocalDate scheduleDate;
    private final String status;        // 'Pending', 'Done' or 'Cancelled'

    public Order(int orderId, String customerName, int productId, String productName,
                 String color, String customDescription, int quantity, BigDecimal unitPrice,
                 String arrangement, LocalDate scheduleDate, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productId = productId;
        this.productName = productName;
        this.color = color;
        this.customDescription = customDescription;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.arrangement = arrangement;
        this.scheduleDate = scheduleDate;
        this.status = status;
    }

    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getColor() { return color; }
    public String getCustomDescription() { return customDescription; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getArrangement() { return arrangement; }
    public LocalDate getScheduleDate() { return scheduleDate; }
    public String getStatus() { return status; }

    // =====================================================================
    //  LOGIC: DERIVED VALUE
    //  The total of an order is calculated from quantity and unit price, never stored
    //  (same rule as StandSale, decision L40).
    // =====================================================================

    /** quantity x unitPrice. Calculated here, not stored in the database. */
    public BigDecimal getTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "Order{id=" + orderId +
                ", customer='" + customerName +
                "', product='" + productName +
                "', qty=" + quantity +
                ", unitPrice=" + unitPrice +
                ", total=" + getTotal() +
                ", date=" + scheduleDate +
                ", status=" + status + "}";
    }
}