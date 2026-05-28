package com.example.cmms.data.remote.models;

import java.util.List;

public class WorkOrderResponse {
    private String id;
    private String title;
    private String status;
    private String priority;
    private String description;
    private boolean bhpConfirmed;
    private String machineId;
    private String createdAt;
    private MachineRef machine;
    private UserRef reportedBy;
    private UserRef assignedTo;
    private List<PartUsage> parts;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getDescription() { return description; }
    public boolean isBhpConfirmed() { return bhpConfirmed; }
    public String getMachineId() { return machineId; }
    public String getCreatedAt() { return createdAt; }
    public MachineRef getMachine() { return machine; }
    public UserRef getReportedBy() { return reportedBy; }
    public UserRef getAssignedTo() { return assignedTo; }
    public List<PartUsage> getParts() { return parts; }

    public static class MachineRef {
        private String id;
        private String name;

        public String getId() { return id; }
        public String getName() { return name; }
    }

    public static class UserRef {
        private String id;
        private String name;

        public String getId() { return id; }
        public String getName() { return name; }
    }

    public static class PartUsage {
        private PartRef part;
        private int quantity;

        public PartRef getPart() { return part; }
        public int getQuantity() { return quantity; }
    }

    public static class PartRef {
        private String id;
        private String name;

        public String getId() { return id; }
        public String getName() { return name; }
    }
}
