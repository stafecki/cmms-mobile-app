package com.example.cmms.data.remote.models;

public class UpdateWorkOrderRequest {
    private String title;
    private String description;
    private String priority;

    public UpdateWorkOrderRequest(String title, String description, String priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
}
