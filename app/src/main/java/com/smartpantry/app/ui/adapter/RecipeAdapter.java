package com.smartpantry.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.app.R;
import com.smartpantry.app.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter for the suggested-recipes list. Each row shows the recipe
 * name and how many ingredients it needs; tapping opens the detail screen.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {

    public interface OnRecipeClick {
        void run(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClick onClick;

    public RecipeAdapter(OnRecipeClick onClick) {
        this.onClick = onClick;
    }

    public void setRecipes(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recipe, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Recipe recipe = recipes.get(position);
        h.name.setText(recipe.getName());
        h.count.setText(h.count.getContext().getString(
                R.string.label_ingredient_count, recipe.getIngredients().size()));
        h.itemView.setOnClickListener(v -> onClick.run(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name, count;

        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.row_recipe_name);
            count = itemView.findViewById(R.id.row_recipe_count);
        }
    }
}
