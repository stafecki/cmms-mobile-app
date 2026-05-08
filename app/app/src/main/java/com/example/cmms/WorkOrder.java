package com.example.cmms;

public class WorkOrder {

    private String title;
    private String description;
    private String priority;
    private String status;

    public WorkOrder(String title, String description, String priority, String status) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }
}
