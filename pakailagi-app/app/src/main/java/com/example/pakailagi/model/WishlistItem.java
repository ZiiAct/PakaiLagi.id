package com.example.pakailagi.model;

public class WishlistItem {
    private String name;
    private String location;
    private String condition;

    public WishlistItem(String name, String location, String condition) {
        this.name = name;
        this.location = location;
        this.condition = condition;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getCondition() { return condition; }
}
