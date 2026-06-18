package com.example.pakailagi.model;

public class PengajuanItem {

    public enum Status { PENDING, READY }

    private final String name;
    private final String distance;
    private final String date;
    private final Status status;
    private final String pickupLocation;

    public PengajuanItem(String name, String distance, String date,
                         Status status, String pickupLocation) {
        this.name = name;
        this.distance = distance;
        this.date = date;
        this.status = status;
        this.pickupLocation = pickupLocation;
    }

    public String getName()           { return name; }
    public String getDistance()       { return distance; }
    public String getDate()           { return date; }
    public Status getStatus()         { return status; }
    public String getPickupLocation() { return pickupLocation; }
}
