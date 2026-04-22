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

    public WorkOrderEntity(@NonNull String id, String title, String status, String priority, String description, boolean bhpConfirmed, String createdAt) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.description = description;
        this.bhpConfirmed = bhpConfirmed;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getDescription() { return description; }
    public boolean isBhpConfirmed() { return bhpConfirmed; }
    public String getCreatedAt() { return createdAt; }
}