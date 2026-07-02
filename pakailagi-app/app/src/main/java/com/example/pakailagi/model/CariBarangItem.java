package com.example.pakailagi.model;

public class CariBarangItem {
    private String idItem;       // Firebase node key
    private String itemName;
    private String location;
    private String itemCondition;
    private String imageUrl;     // URL from Firebase Storage / server
    private int imageResId;      // Local drawable fallback (0 if not used)

    // Constructor for Firebase data (no local drawable)
    public CariBarangItem(String idItem, String itemName, String location, String itemCondition, String imageUrl) {
        this.idItem = idItem;
        this.itemName = itemName;
        this.location = location;
        this.itemCondition = itemCondition;
        this.imageUrl = imageUrl;
        this.imageResId = 0;
    }

    // Legacy constructor (int resId) kept for any existing call sites
    public CariBarangItem(String idItem, String itemName, String location, String itemCondition, int imageResId) {
        this.idItem = idItem;
        this.itemName = itemName;
        this.location = location;
        this.itemCondition = itemCondition;
        this.imageResId = imageResId;
        this.imageUrl = null;
    }

    public String getIdItem() { return idItem; }
    public String getItemName() { return itemName; }
    public String getLocation() { return location; }
    public String getItemCondition() { return itemCondition; }
    public String getImageUrl() { return imageUrl; }
    public int getImageResId() { return imageResId; }
}