package com.example.cmms.data.remote.models;

public class UpdateOperatingHoursRequest {
    private final double operatingHours;

    public UpdateOperatingHoursRequest(double operatingHours) {
        this.operatingHours = operatingHours;
    }

    public double getOperatingHours() { return operatingHours; }
}
