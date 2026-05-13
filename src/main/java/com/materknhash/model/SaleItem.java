package com.materknhash.model;

/**
 * Model representing an item in a sale (Invoice Line).
 */
public class SaleItem implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int saleId;
    private int productId;
    private String productName; // Helper
    private int quantity;
    private double unitPrice;
    private double totalPrice; // Calculated

    public SaleItem() {}

    public SaleItem(int productId, String productName, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
        this.totalPrice = quantity * this.unitPrice;
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { 
        this.unitPrice = unitPrice; 
        this.totalPrice = this.quantity * unitPrice;
    }

    public double getTotalPrice() { return totalPrice; }
}
