package com.example.pakailagi.model;

public class ItemModel {
    private String id;
    private String itemName;
    private String donorName;
    private String status;
    private String imageUrl;

    // Constructor kosong (Wajib ada untuk Firebase)
    public ItemModel() {
    }

    // Constructor lengkap
    public ItemModel(String id, String itemName, String donorName, String status, String imageUrl) {
        this.id = id;
        this.itemName = itemName;
        this.donorName = donorName;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // --- Getter dan Setter ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}