package com.example.smartpantry.util;

/** Keys for the extras passed between screens with Intents */
public final class IntentKeys {

    /** A {@code PantryItem} (Serializable) to edit. Absent when adding a new item */
    public static final String EXTRA_PANTRY_ITEM = "com.example.smartpantry.EXTRA_PANTRY_ITEM";

    /** The id (long) of the recipe to show */
    public static final String EXTRA_RECIPE_ID = "com.example.smartpantry.EXTRA_RECIPE_ID";

    private IntentKeys() {
    }
}