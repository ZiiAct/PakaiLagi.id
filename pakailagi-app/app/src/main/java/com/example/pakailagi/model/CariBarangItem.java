package com.example.pakailagi.model;

public class CariBarangItem {
    private String idItem; // Sesuai PDM
    private String itemName;
    private String location;
    private String itemCondition; // Sesuai PDM
    private int imageResId; // Sementara pakai int untuk gambar lokal

    public CariBarangItem(String idItem, String itemName, String location, String itemCondition, int imageResId) {
        this.idItem = idItem;
        this.itemName = itemName;
        this.location = location;
        this.itemCondition = itemCondition;
        this.imageResId = imageResId;
    }

    public String getItemName() { return itemName; }
    public String getLocation() { return location; }
    public String getItemCondition() { return itemCondition; }
    public int getImageResId() { return imageResId; }
}