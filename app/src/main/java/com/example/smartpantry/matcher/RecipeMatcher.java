package com.example.smartpantry.matcher;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A recipe is suggested ONLY if EVERY ingredient it needs is in the pantry in at least the required quantity
public final class RecipeMatcher {

    /** Tolerance for floating point rounding, for example after unit conversion. */
    private static final double EPSILON = 1e-9;

    private RecipeMatcher() {
    }

    /** Returns only the recipes the user can make right now from the pantry. */
    public static List<Recipe> findMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Double> stock = buildStock(pantry); // computed once for all recipes
        List<Recipe> matches = new ArrayList<>();
        if (recipes == null) {
            return matches;
        }
        for (Recipe recipe : recipes) {
            if (isStrictMatch(recipe, stock)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    /** True only if every single ingredient of the recipe is available. */
    public static boolean isStrictMatch(Recipe recipe, List<PantryItem> pantry) {
        return isStrictMatch(recipe, buildStock(pantry));
    }

    /** True if the pantry holds at least the required quantity of this ingredient. */
    public static boolean isIngredientAvailable(RecipeIngredient ingredient, List<PantryItem> pantry) {
        return isAvailable(ingredient, buildStock(pantry));
    }

    // internals

    private static boolean isStrictMatch(Recipe recipe, Map<String, Double> stock) {
        List<RecipeIngredient> needed = recipe.getIngredients();
        if (needed.isEmpty()) {
            return false; // a recipe with no ingredients is never suggested
        }
        for (RecipeIngredient ingredient : needed) {
            if (!isAvailable(ingredient, stock)) {
                return false; // a single missing ingredient disqualifies the recipe
            }
        }
        return true;
    }

    private static boolean isAvailable(RecipeIngredient ingredient, Map<String, Double> stock) {
        String name = IngredientNormaliser.normalize(ingredient.getName());
        if (name.isEmpty()) {
            return false;
        }
        UnitConverter.Measure required =
                UnitConverter.toBase(ingredient.getQuantity(), ingredient.getUnit());
        Double have = stock.get(stockKey(name, required.dimension));
        return have != null && have + EPSILON >= required.amount;
    }

    /**
     * Totals the pantry per ingredient and dimension, in base units.
     * Key: "normalized name|dimension", for example "tomato|count" or "flour|mass".
     */
    private static Map<String, Double> buildStock(List<PantryItem> pantry) {
        Map<String, Double> stock = new HashMap<>();
        if (pantry == null) {
            return stock;
        }
        for (PantryItem item : pantry) {
            String name = IngredientNormaliser.normalize(item.getName());
            if (name.isEmpty() || item.getQuantity() <= 0) {
                continue;
            }
            UnitConverter.Measure measure = UnitConverter.toBase(item.getQuantity(), item.getUnit());
            String key = stockKey(name, measure.dimension);
            Double current = stock.get(key);
            stock.put(key, (current == null ? 0 : current) + measure.amount);
        }
        return stock;
    }

    private static String stockKey(String normalizedName, String dimension) {
        return normalizedName + "|" + dimension;
    }
}