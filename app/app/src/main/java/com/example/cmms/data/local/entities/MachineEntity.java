package com.example.cmms.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "machines")
public class MachineEntity {

    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String serialNumber;
    private double operatingHours;
    private boolean isActive;

    public MachineEntity(@NonNull String id, String name, String serialNumber, double operatingHours, boolean isActive) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.operatingHours = operatingHours;
        this.isActive = isActive;
    }

    @NonNull
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSerialNumber() { return serialNumber; }
    public double getOperatingHours() { return operatingHours; }
    public boolean isActive() { return isActive; }
}