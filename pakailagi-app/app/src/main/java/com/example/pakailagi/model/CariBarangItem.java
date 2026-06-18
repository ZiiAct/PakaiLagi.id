package com.example.pakailagi.model;

public class CariBarangItem {
    private final String name;
    private final String location;

    public CariBarangItem(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
}
