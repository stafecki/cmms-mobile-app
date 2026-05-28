package com.example.cmms.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class CreateMachineRequest {
    private String name;

    @SerializedName("serialNumber")
    private String serialNumber;

    @SerializedName("locationId")
    private String locationId;

    @SerializedName("operatingHours")
    private double operatingHours;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("purchaseDate")
    private String purchaseDate;

    @SerializedName("purchasePrice")
    private double purchasePrice;

    public CreateMachineRequest(String name, String serialNumber, String locationId,
                                 double operatingHours, boolean isActive,
                                 String purchaseDate, double purchasePrice) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.locationId = locationId;
        this.operatingHours = operatingHours;
        this.isActive = isActive;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
    }
}
