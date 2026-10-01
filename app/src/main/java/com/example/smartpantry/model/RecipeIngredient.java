package com.example.smartpantry.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** One row of the recipe_ingredients table: what a recipe needs. */
public class RecipeIngredient implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    @SerializedName("recipe_id")
    private long recipeId;

    private String name;
    private double quantity;
    private String unit;

    public RecipeIngredient() {
    }

    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public long getId() {
        return id;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }
}