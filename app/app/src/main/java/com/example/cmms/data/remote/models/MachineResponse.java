package com.example.cmms.data.remote.models;

public class MachineResponse {
    private String id;
    private String name;
    private String serialNumber;
    private String locationId;
    private double operatingHours;
    private boolean isActive;
    private String purchaseDate;
    private double purchasePrice;
    private LocationRef location;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSerialNumber() { return serialNumber; }
    public String getLocationId() { return locationId; }
    public double getOperatingHours() { return operatingHours; }
    public boolean isActive() { return isActive; }
    public String getPurchaseDate() { return purchaseDate; }
    public double getPurchasePrice() { return purchasePrice; }
    public LocationRef getLocation() { return location; }

    public static class LocationRef {
        private String id;
        private String name;
        private String type;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getType() { return type; }
    }
}
