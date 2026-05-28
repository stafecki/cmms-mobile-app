package com.example.cmms.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class UpdateMachineRequest {
    private String name;

    @SerializedName("serialNumber")
    private String serialNumber;

    @SerializedName("locationId")
    private String locationId;

    @SerializedName("operatingHours")
    private Double operatingHours;

    @SerializedName("purchaseDate")
    private String purchaseDate;

    @SerializedName("purchasePrice")
    private Double purchasePrice;

    public UpdateMachineRequest(String name, String serialNumber, String locationId,
                                Double operatingHours, String purchaseDate, Double purchasePrice) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.locationId = locationId;
        this.operatingHours = operatingHours;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
    }
}
