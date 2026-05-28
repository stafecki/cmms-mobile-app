package com.example.cmms.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class CreateWorkOrderRequest {

    @SerializedName("machineId")
    private String machineId;

    private String title;
    private String description;
    private String priority;

    public CreateWorkOrderRequest(String machineId, String title, String description, String priority) {
        this.machineId = machineId;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
}
