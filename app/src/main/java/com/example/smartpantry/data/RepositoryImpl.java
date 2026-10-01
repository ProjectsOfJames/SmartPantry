package com.example.smartpantry.data;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository backed by PostgreSQL through PostgREST.
 *
 * Threading: Call.enqueue() performs the HTTP request on OkHttp's background
 * thread pool (never the UI thread), and Retrofit's Android platform delivers
 * onResponse/onFailure back on the main thread. So every ResultCallback runs on
 * the main thread and screens can update views directly.
 */
public class RepositoryImpl implements Repository {

    private static RepositoryImpl instance;

    private final ApiService api;

    private RepositoryImpl() {
        api = ApiClient.getService();
    }

    public static synchronized Repository getInstance() {
        if (instance == null) {
            instance = new RepositoryImpl();
        }
        return instance;
    }

    /** Converts the raw HTTP body into the type the caller wants. */
    private interface Mapper<T, R> {
        R map(T body) throws Exception;
    }

    // ---------------------------------------------------------------- pantry

    @Override
    public void getPantryItems(ResultCallback<List<PantryItem>> callback) {
        run(api.getPantryItems(),
                body -> body == null ? new ArrayList<PantryItem>() : body,
                callback);
    }

    @Override
    public void addPantryItem(PantryItem item, ResultCallback<PantryItem> callback) {
        run(api.createPantryItem(item.toRequest()),
                body -> first(body, "The server did not return the saved item."),
                callback);
    }

    @Override
    public void updatePantryItem(PantryItem item, ResultCallback<PantryItem> callback) {
        run(api.updatePantryItem("eq." + item.getId(), item.toRequest()),
                body -> first(body, "That item no longer exists."),
                callback);
    }

    @Override
    public void deletePantryItem(long id, ResultCallback<Void> callback) {
        run(api.deletePantryItem("eq." + id), body -> null, callback);
    }

    // --------------------------------------------------------------- recipes

    @Override
    public void getRecipes(ResultCallback<List<Recipe>> callback) {
        run(api.getRecipes(),
                body -> body == null ? new ArrayList<Recipe>() : body,
                callback);
    }

    @Override
    public void getRecipe(long id, ResultCallback<Recipe> callback) {
        run(api.getRecipe("eq." + id),
                body -> first(body, "Recipe not found."),
                callback);
    }

    // --------------------------------------------------------------- helpers

    /** Runs a call asynchronously and reports the mapped result or an error message. */
    private <T, R> void run(Call<T> call, Mapper<T, R> mapper, ResultCallback<R> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> c, Response<T> response) {
                if (!response.isSuccessful()) {
                    callback.onError(describeHttpError(response));
                    return;
                }
                R result;
                try {
                    result = mapper.map(response.body());
                } catch (Exception e) {
                    callback.onError(e.getMessage() != null
                            ? e.getMessage() : "Unexpected response from the server.");
                    return;
                }
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Call<T> c, Throwable t) {
                callback.onError(describeFailure(t));
            }
        });
    }

    private static <E> E first(List<E> list, String notFoundMessage) throws Exception {
        if (list == null || list.isEmpty()) {
            throw new Exception(notFoundMessage);
        }
        return list.get(0);
    }

    /** Turns a non-2xx response into a readable message (PostgREST sends JSON errors). */
    private static String describeHttpError(Response<?> response) {
        String detail = null;
        try {
            if (response.errorBody() != null) {
                String raw = response.errorBody().string();
                JsonObject obj = JsonParser.parseString(raw).getAsJsonObject();
                if (obj.has("message")) {
                    detail = obj.get("message").getAsString();
                }
            }
        } catch (Exception ignored) {
            // body was not JSON: fall back to the status code alone
        }
        String base = "Server error (" + response.code() + ")";
        return detail != null ? base + ": " + detail : base;
    }

    /** Network-level failures: no connection, timeout, server down. */
    private static String describeFailure(Throwable t) {
        if (t instanceof IOException) {
            return "Cannot reach the server. Make sure Docker and PostgREST are running.";
        }
        return t.getMessage() != null ? t.getMessage() : "Unexpected error.";
    }
}