package com.example.cmms.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class NotificationResponse {
    private String id;
    private String type;
    private String title;
    private String message;

    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("created_at")
    private String createdAt;

    public String getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }
}