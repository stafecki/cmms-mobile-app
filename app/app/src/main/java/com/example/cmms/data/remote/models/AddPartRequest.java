package com.example.cmms.data.remote.models;

public class AddPartRequest {
    private final String partId;
    private final int quantity;

    public AddPartRequest(String partId, int quantity) {
        this.partId = partId;
        this.quantity = quantity;
    }

    public String getPartId() { return partId; }
    public int getQuantity() { return quantity; }
}
