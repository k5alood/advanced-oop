package com.materknhash.model;

/**
 * Model representing a Spare Part (Product) in the inventory.
 * Demonstrates Encapsulation and pure OOP design.
 */
public class Product {
    private int id;
    private String partNumber;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private int minStockLevel;
    private int supplierId;
    private String supplierName; // Helper for UI display

    public Product() {}

    public Product(int id, String partNumber, String name, String category, double price, int quantity) {
        this.id = id;
        this.partNumber = partNumber;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    @Override
    public String toString() {
        return name + " (" + partNumber + ")";
    }
}
