package com.example.smartpantry.matcher;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// TEMPORARY, so the Suggested Recipes screen works
public final class RecipeMatcher {

    private static final double EPSILON = 1e-9;

    private RecipeMatcher() {
    }

    /** Returns only the recipes the user can make right now from the pantry. */
    public static List<Recipe> findMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (isStrictMatch(recipe, pantry)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    /** True only if every single ingredient of the recipe is available. */
    public static boolean isStrictMatch(Recipe recipe, List<PantryItem> pantry) {
        List<RecipeIngredient> needed = recipe.getIngredients();
        if (needed.isEmpty()) {
            return false; // a recipe with no ingredients is never suggested
        }
        for (RecipeIngredient ingredient : needed) {
            if (!isIngredientAvailable(ingredient, pantry)) {
                return false;
            }
        }
        return true;
    }

    /** True if the pantry holds at least the required quantity of this ingredient. */
    public static boolean isIngredientAvailable(RecipeIngredient ingredient, List<PantryItem> pantry) {
        String name = clean(ingredient.getName());
        String unit = clean(ingredient.getUnit());
        double have = 0;
        for (PantryItem item : pantry) {
            if (clean(item.getName()).equals(name) && clean(item.getUnit()).equals(unit)) {
                have += item.getQuantity();
            }
        }
        return have + EPSILON >= ingredient.getQuantity();
    }

    private static String clean(String text) {
        return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
    }
}