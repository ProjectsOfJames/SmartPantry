package com.example.smartpantry.model;

import com.google.gson.annotations.SerializedName;

// JSON body for creating or updating a pantry item
public class PantryItemRequest {

    private final String name;
    private final double quantity;
    private final String unit;

    @SerializedName("expiry_date")
    private final String expiryDate;

    public PantryItemRequest(String name, double quantity, String unit, String expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }
}