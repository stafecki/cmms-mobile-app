package com.example.cmms.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class MachineResponse {
    private String id;
    private String name;

    @SerializedName("serial_number")
    private String serialNumber;

    @SerializedName("operating_hours")
    private double operatingHours;

    @SerializedName("is_active")
    private boolean isActive;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSerialNumber() { return serialNumber; }
    public double getOperatingHours() { return operatingHours; }
    public boolean isActive() { return isActive; }
}