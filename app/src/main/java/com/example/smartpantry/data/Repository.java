package com.example.smartpantry.data;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;

import java.util.List;

/**
 * The only way screens read or write data. Screens depend on this interface,
 * not on Retrofit, so the storage layer can change without touching the UI.
 * Every method is asynchronous and reports back on the main thread.
 */
public interface Repository {

    // ---- Pantry ----

    /** Read: all pantry items, sorted by name. */
    void getPantryItems(ResultCallback<List<PantryItem>> callback);

    /** Create: returns the saved item including its generated id. */
    void addPantryItem(PantryItem item, ResultCallback<PantryItem> callback);

    /** Update: replaces name, quantity, unit and expiry of the item with the same id. */
    void updatePantryItem(PantryItem item, ResultCallback<PantryItem> callback);

    /** Delete: removes the item with the given id. onSuccess receives null. */
    void deletePantryItem(long id, ResultCallback<Void> callback);

    // ---- Recipes  ----

    /** All recipes, each with its ingredient list. */
    void getRecipes(ResultCallback<List<Recipe>> callback);

    /** A single recipe with its ingredient list. */
    void getRecipe(long id, ResultCallback<Recipe> callback);
}