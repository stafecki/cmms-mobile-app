package com.example.cmms.data.remote.models;

public class WorkOrderPartResponse {
    private String id;
    private String workOrderId;
    private String partId;
    private int quantity;
    private PartRef part;

    public String getId() { return id; }
    public String getWorkOrderId() { return workOrderId; }
    public String getPartId() { return partId; }
    public int getQuantity() { return quantity; }
    public PartRef getPart() { return part; }

    public static class PartRef {
        private String id;
        private String name;

        public String getId() { return id; }
        public String getName() { return name; }
    }
}
