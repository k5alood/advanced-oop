package com.materknhash.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a Sale transaction (Invoice Header).
 */
public class Sale implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private Timestamp saleDate;
    private double totalAmount;
    private int userId;
    private String customerName; // Optional extension
    private List<SaleItem> items = new ArrayList<>();

    public Sale() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Timestamp getSaleDate() { return saleDate; }
    public void setSaleDate(Timestamp saleDate) { this.saleDate = saleDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
}
