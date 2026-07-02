package com.example.pakailagi.model;

import com.google.firebase.database.PropertyName;

public class ItemModel {
    private String id;
    private String itemName;
    private String donorId;
    private String donorName;
    private String status;
    private String imageUrl;
    private String location;
    private String condition;
    private String description;
    private String category;
    private boolean availability;

    // Constructor kosong (Wajib ada untuk Firebase deserialization)
    public ItemModel() {}

    // --- Getter dan Setter ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    @PropertyName("id_users")
    public String getDonorId() { return donorId; }
    @PropertyName("id_users")
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @PropertyName("itemImage")
    public String getImageUrl() { return imageUrl; }
    @PropertyName("itemImage")
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @PropertyName("pickupLocation")
    public String getLocation() { return location; }
    @PropertyName("pickupLocation")
    public void setLocation(String location) { this.location = location; }

    @PropertyName("itemCondition")
    public String getCondition() { return condition; }
    @PropertyName("itemCondition")
    public void setCondition(String condition) { this.condition = condition; }

    @PropertyName("itemDescription")
    public String getDescription() { return description; }
    @PropertyName("itemDescription")
    public void setDescription(String description) { this.description = description; }

    @PropertyName("id_itemCategory")
    public String getCategory() { return category; }
    @PropertyName("id_itemCategory")
    public void setCategory(String category) { this.category = category; }

    public boolean isAvailability() { return availability; }
    public void setAvailability(boolean availability) { this.availability = availability; }
}