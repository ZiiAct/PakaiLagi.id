package com.example.pakailagi.model;

public class WishlistItem {
    private String itemId;   // Firebase key (hibahReq node key)
    private String name;
    private String location;
    private String condition;

    public WishlistItem() {}

    public WishlistItem(String itemId, String name, String location, String condition) {
        this.itemId = itemId;
        this.name = name;
        this.location = location;
        this.condition = condition;
    }

    // Legacy constructor (3-arg) kept so existing callers don't break
    public WishlistItem(String name, String location, String condition) {
        this.itemId = "";
        this.name = name;
        this.location = location;
        this.condition = condition;
    }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}
