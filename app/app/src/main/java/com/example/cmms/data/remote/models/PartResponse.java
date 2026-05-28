package com.example.cmms.data.remote.models;

public class PartResponse {
    private String id;
    private String name;
    private int stockQuantity;
    private double unitPrice;

    public String getId() { return id; }
    public String getName() { return name; }
    public int getStockQuantity() { return stockQuantity; }
    public double getUnitPrice() { return unitPrice; }
}
