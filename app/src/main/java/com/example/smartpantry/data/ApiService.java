package com.example.smartpantry.data;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.PantryItemRequest;
import com.example.smartpantry.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit description of the PostgREST endpoints. PostgREST turns each table
 * into a URL; rows are filtered with query parameters such as ?id=eq.5.
 * "Prefer: return=representation" makes writes return the affected row.
 */
public interface ApiService {

    // ---- pantry_items ----

    @GET("pantry_items?order=name.asc")
    Call<List<PantryItem>> getPantryItems();

    @Headers("Prefer: return=representation")
    @POST("pantry_items")
    Call<List<PantryItem>> createPantryItem(@Body PantryItemRequest body);

    // idFilter
    @Headers("Prefer: return=representation")
    @PATCH("pantry_items")
    Call<List<PantryItem>> updatePantryItem(@Query("id") String idFilter,
                                            @Body PantryItemRequest body);

    // idFilter
    @DELETE("pantry_items")
    Call<Void> deletePantryItem(@Query("id") String idFilter);

    // ---- recipes  ----

    @GET("recipes?select=*,recipe_ingredients(*)&order=name.asc")
    Call<List<Recipe>> getRecipes();

    // idFilter
    @GET("recipes?select=*,recipe_ingredients(*)")
    Call<List<Recipe>> getRecipe(@Query("id") String idFilter);
}