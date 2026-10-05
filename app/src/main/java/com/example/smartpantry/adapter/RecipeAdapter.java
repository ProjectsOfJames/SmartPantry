package com.example.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.Recipe;

import java.util.Objects;

/** Displays the suggested recipes as a list of tappable cards. */
public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder> {

    /** Implemented by the screen that owns the list. */
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private static final DiffUtil.ItemCallback<Recipe> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Recipe>() {
                @Override
                public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
                    return Objects.equals(oldItem.getName(), newItem.getName())
                            && oldItem.getIngredients().size() == newItem.getIngredients().size();
                }
            };

    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    /** Holds the views of one row and fills them from a Recipe. */
    class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvSummary;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_recipe_name);
            tvSummary = itemView.findViewById(R.id.tv_recipe_summary);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRecipeClick(getItem(position));
                }
            });
        }

        void bind(Recipe recipe) {
            tvName.setText(recipe.getName());
            tvSummary.setText(itemView.getContext().getString(
                    R.string.ingredient_count, recipe.getIngredients().size()));
        }
    }
}