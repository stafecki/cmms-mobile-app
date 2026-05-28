package com.example.cmms.data.remote.models;

public class NotificationResponse {
    private String id;
    private String type;
    private String title;
    private String message;
    private boolean isRead;
    private String createdAt;

    public String getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }
}
