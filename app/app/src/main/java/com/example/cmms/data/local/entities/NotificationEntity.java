package com.example.cmms.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "notifications")
public class NotificationEntity {

    @PrimaryKey
    @NonNull
    private String id;
    private String type;
    private String title;
    private String message;
    private boolean isRead;
    private String createdAt;

    public NotificationEntity(@NonNull String id, String type, String title, String message, boolean isRead, String createdAt) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }
}