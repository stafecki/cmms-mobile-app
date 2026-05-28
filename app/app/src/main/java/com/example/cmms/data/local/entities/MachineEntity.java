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
    private String locationId;
    private String locationName;
    private String purchaseDate;
    private double purchasePrice;

    public MachineEntity(@NonNull String id, String name, String serialNumber,
                         double operatingHours, boolean isActive,
                         String locationId, String locationName,
                         String purchaseDate, double purchasePrice) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.operatingHours = operatingHours;
        this.isActive = isActive;
        this.locationId = locationId;
        this.locationName = locationName;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
    }

    @NonNull
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSerialNumber() { return serialNumber; }
    public double getOperatingHours() { return operatingHours; }
    public boolean isActive() { return isActive; }
    public String getLocationId() { return locationId; }
    public String getLocationName() { return locationName; }
    public String getPurchaseDate() { return purchaseDate; }
    public double getPurchasePrice() { return purchasePrice; }
}
