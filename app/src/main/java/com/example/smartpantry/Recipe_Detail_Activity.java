package com.example.smartpantry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.data.Repository;
import com.example.smartpantry.data.RepositoryImpl;
import com.example.smartpantry.data.ResultCallback;
import com.example.smartpantry.matcher.RecipeMatcher;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;
import com.example.smartpantry.util.FormatUtils;
import com.example.smartpantry.util.IntentKeys;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

/**
 * The Recipe Detail screen. It receives only a recipe id through the Intent,
 * loads that recipe and the pantry, then shows the full ingredient list (each
 * marked as in the pantry or not) and the numbered method.
 */
public class Recipe_Detail_Activity extends AppCompatActivity {

    private final Repository repository = RepositoryImpl.getInstance();

    private long recipeId;
    private View scrollContent;
    private View progress;
    private TextView tvName;
    private TextView tvStatus;
    private LinearLayout llIngredients;
    private LinearLayout llSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_recipe_detail);
        toolbar.setNavigationOnClickListener(v -> finish());

        scrollContent = findViewById(R.id.scroll_recipe_detail);
        progress = findViewById(R.id.progress_recipe_detail);
        tvName = findViewById(R.id.tv_detail_name);
        tvStatus = findViewById(R.id.tv_detail_status);
        llIngredients = findViewById(R.id.ll_ingredients);
        llSteps = findViewById(R.id.ll_steps);

        // Receive the recipe id sent by the Suggested Recipes screen
        recipeId = getIntent().getLongExtra(IntentKeys.EXTRA_RECIPE_ID, -1);
        if (recipeId < 0) {
            Toast.makeText(this, R.string.error_recipe_missing, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecipe();
    }

    // loading

    /** Step 1 of 2: load the recipe. */
    private void loadRecipe() {
        showLoading(true);
        repository.getRecipe(recipeId, new ResultCallback<Recipe>() {
            @Override
            public void onSuccess(Recipe recipe) {
                if (isDestroyed()) return;
                loadPantry(recipe);
            }

            @Override
            public void onError(String message) {
                if (isDestroyed()) return;
                showLoadError(message);
            }
        });
    }

    /** Step 2 of 2: load the pantry so each ingredient can be checked against it. */
    private void loadPantry(Recipe recipe) {
        repository.getPantryItems(new ResultCallback<List<PantryItem>>() {
            @Override
            public void onSuccess(List<PantryItem> pantry) {
                if (isDestroyed()) return;
                showRecipe(recipe, pantry);
            }

            @Override
            public void onError(String message) {
                if (isDestroyed()) return;
                showLoadError(message);
            }
        });
    }

    private void showLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        scrollContent.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private void showLoadError(String message) {
        showLoading(false);
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_retry, v -> loadRecipe())
                .show();
    }

    // rendering

    private void showRecipe(Recipe recipe, List<PantryItem> pantry) {
        showLoading(false);
        tvName.setText(recipe.getName());

        LayoutInflater inflater = LayoutInflater.from(this);

        // Ingredients: a tick if the pantry covers it, a cross if not
        llIngredients.removeAllViews();
        int available = 0;
        List<RecipeIngredient> ingredients = recipe.getIngredients();
        for (RecipeIngredient ingredient : ingredients) {
            boolean inPantry = RecipeMatcher.isIngredientAvailable(ingredient, pantry);
            if (inPantry) {
                available++;
            }

            View row = inflater.inflate(R.layout.item_recipe_ingredient, llIngredients, false);
            ImageView status = row.findViewById(R.id.iv_ingredient_status);
            status.setImageResource(inPantry ? R.drawable.ic_check : R.drawable.ic_close);
            status.setContentDescription(getString(inPantry
                    ? R.string.cd_ingredient_available : R.string.cd_ingredient_missing));
            ((TextView) row.findViewById(R.id.tv_ingredient_name))
                    .setText(capitalize(ingredient.getName()));
            ((TextView) row.findViewById(R.id.tv_ingredient_amount))
                    .setText(FormatUtils.formatAmount(ingredient.getQuantity(), ingredient.getUnit()));
            llIngredients.addView(row);
        }
        tvStatus.setText(getString(R.string.detail_pantry_status, available, ingredients.size()));

        // Method: numbered steps
        llSteps.removeAllViews();
        List<String> steps = recipe.getStepList();
        for (int i = 0; i < steps.size(); i++) {
            View row = inflater.inflate(R.layout.item_recipe_step, llSteps, false);
            ((TextView) row.findViewById(R.id.tv_step_number)).setText((i + 1) + ".");
            ((TextView) row.findViewById(R.id.tv_step_text)).setText(steps.get(i));
            llSteps.addView(row);
        }
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.substring(0, 1).toUpperCase(Locale.getDefault()) + text.substring(1);
    }
}