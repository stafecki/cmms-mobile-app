package com.example.cmms.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class WorkOrderResponse {
    private String id;
    private String title;
    private String status;
    private String priority;
    private String description;

    @SerializedName("bhp_confirmed")
    private boolean bhpConfirmed;

    @SerializedName("created_at")
    private String createdAt;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getDescription() { return description; }
    public boolean isBhpConfirmed() { return bhpConfirmed; }
    public String getCreatedAt() { return createdAt; }
}