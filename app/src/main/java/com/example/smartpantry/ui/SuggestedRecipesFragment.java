package com.example.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.MainActivity;
import com.example.smartpantry.R;
import com.example.smartpantry.Recipe_Detail_Activity;
import com.example.smartpantry.adapter.RecipeAdapter;
import com.example.smartpantry.data.Repository;
import com.example.smartpantry.data.RepositoryImpl;
import com.example.smartpantry.data.ResultCallback;
import com.example.smartpantry.matcher.RecipeMatcher;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.util.IntentKeys;

import java.util.List;

// The Suggested Recipes screen
public class SuggestedRecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private final Repository repository = RepositoryImpl.getInstance();

    /** Only the response to the latest load is used; older ones are ignored. */
    private int loadGeneration;

    private RecipeAdapter adapter;
    private View content;
    private TextView summary;
    private View progress;
    private View emptyLayout;
    private View errorLayout;
    private TextView errorMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        content = view.findViewById(R.id.layout_recipes_content);
        summary = view.findViewById(R.id.tv_match_summary);
        progress = view.findViewById(R.id.progress_recipes);
        emptyLayout = view.findViewById(R.id.layout_empty_recipes);
        errorLayout = view.findViewById(R.id.layout_error_recipes);
        errorMessage = view.findViewById(R.id.tv_error_recipes_message);
        RecyclerView recycler = view.findViewById(R.id.recycler_recipes);

        adapter = new RecipeAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        view.findViewById(R.id.btn_go_to_pantry).setOnClickListener(v ->
                ((MainActivity) requireActivity()).showPantryTab());
        view.findViewById(R.id.btn_retry_recipes).setOnClickListener(v -> loadData());
    }

    /** Reload each time the tab is shown, because the pantry may have changed. */
    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    /** Release view references so a destroyed screen can be garbage collected. */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        content = null;
        summary = null;
        progress = null;
        emptyLayout = null;
        errorLayout = null;
        errorMessage = null;
    }

    // loading

    /** Step 1 of 2: read the pantry. */
    private void loadData() {
        final int generation = ++loadGeneration;
        showLoading();
        repository.getPantryItems(new ResultCallback<List<PantryItem>>() {
            @Override
            public void onSuccess(List<PantryItem> pantry) {
                if (!isViewAlive() || generation != loadGeneration) return;
                loadRecipes(pantry, generation);
            }

            @Override
            public void onError(String message) {
                if (!isViewAlive() || generation != loadGeneration) return;
                showError(message);
            }
        });
    }

    /** Step 2 of 2: read the recipes, then match them against the pantry. */
    private void loadRecipes(List<PantryItem> pantry, int generation) {
        repository.getRecipes(new ResultCallback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> recipes) {
                if (!isViewAlive() || generation != loadGeneration) return;
                showMatches(RecipeMatcher.findMatches(recipes, pantry));
            }

            @Override
            public void onError(String message) {
                if (!isViewAlive() || generation != loadGeneration) return;
                showError(message);
            }
        });
    }

    private boolean isViewAlive() {
        return isAdded() && getView() != null && content != null;
    }

    // screen state

    private void showLoading() {
        progress.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    private void showMatches(List<Recipe> matches) {
        progress.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);

        if (matches.isEmpty()) {
            content.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            return;
        }

        emptyLayout.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        summary.setText(getResources().getQuantityString(
                R.plurals.recipes_match_plural, matches.size(), matches.size()));
        summary.setVisibility(View.VISIBLE);
        adapter.submitList(matches);
    }

    private void showError(String message) {
        progress.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        errorMessage.setText(message);
        errorLayout.setVisibility(View.VISIBLE);
    }

    // RecipeAdapter.OnRecipeClickListener

    /** Only the recipe's id travels in the Intent; the detail screen loads the rest. */
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(requireContext(), Recipe_Detail_Activity.class);
        intent.putExtra(IntentKeys.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}