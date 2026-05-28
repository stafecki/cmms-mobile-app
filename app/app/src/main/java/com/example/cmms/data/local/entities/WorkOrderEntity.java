package com.example.cmms.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "work_orders")
public class WorkOrderEntity {

    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String status;
    private String priority;
    private String description;
    private boolean bhpConfirmed;
    private String createdAt;
    private String machineId;
    private String machineName;
    private String assignedToId;
    private String assignedToName;
    private String reportedById;
    private String reportedByName;
    private String partsText;

    public WorkOrderEntity(@NonNull String id, String title, String status, String priority,
                           String description, boolean bhpConfirmed, String createdAt,
                           String machineId, String machineName,
                           String assignedToId, String assignedToName,
                           String reportedById, String reportedByName,
                           String partsText) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.description = description;
        this.bhpConfirmed = bhpConfirmed;
        this.createdAt = createdAt;
        this.machineId = machineId;
        this.machineName = machineName;
        this.assignedToId = assignedToId;
        this.assignedToName = assignedToName;
        this.reportedById = reportedById;
        this.reportedByName = reportedByName;
        this.partsText = partsText;
    }

    @NonNull
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getDescription() { return description; }
    public boolean isBhpConfirmed() { return bhpConfirmed; }
    public String getCreatedAt() { return createdAt; }
    public String getMachineId() { return machineId; }
    public String getMachineName() { return machineName; }
    public String getAssignedToId() { return assignedToId; }
    public String getAssignedToName() { return assignedToName; }
    public String getReportedById() { return reportedById; }
    public String getReportedByName() { return reportedByName; }
    public String getPartsText() { return partsText; }
}
