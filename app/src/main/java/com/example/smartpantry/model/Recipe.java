package com.example.smartpantry.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private String name;

    /** Preparation steps, one per line. */
    private String steps;

    @SerializedName("recipe_ingredients")
    private List<RecipeIngredient> ingredients;

    public Recipe() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSteps() {
        return steps;
    }

    /** Never returns null. */
    public List<RecipeIngredient> getIngredients() {
        return ingredients == null ? new ArrayList<RecipeIngredient>() : ingredients;
    }

    /** Splits the stored steps text into one entry per non-empty line. */
    public List<String> getStepList() {
        List<String> result = new ArrayList<>();
        if (steps != null) {
            for (String line : steps.split("\\r?\\n")) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
        }
        return result;
    }
}