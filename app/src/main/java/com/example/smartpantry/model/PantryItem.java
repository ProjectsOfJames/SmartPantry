package com.example.smartpantry.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * One row of the pantry_items table. Serializable so it can be passed
 * between activities as an Intent extra.
 */
public class PantryItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private double quantity;
    private String unit;

    /** ISO date "yyyy-MM-dd", or null when the item has no expiry date. */
    @SerializedName("expiry_date")
    private String expiryDate;

    /** Required by Gson. */
    public PantryItem() {
    }

    public PantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    /** Builds the JSON body used for create/update calls (no id). */
    public PantryItemRequest toRequest() {
        return new PantryItemRequest(name, quantity, unit, expiryDate);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}