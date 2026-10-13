package com.smartpantry.app.ui;

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

import com.smartpantry.app.R;
import com.smartpantry.app.db.DatabaseHelper;
import com.smartpantry.app.logic.RecipeMatcher;
import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.model.Recipe;
import com.smartpantry.app.ui.adapter.RecipeAdapter;

import java.util.List;

/**
 * Suggested Recipes screen. Runs the strict-matching rule (Section 2.3) over
 * the current pantry and lists ONLY the recipes the user can make right now.
 * When nothing matches it shows a friendly message instead of a blank screen.
 */
public class SuggestedRecipesFragment extends Fragment {

    private DatabaseHelper db;
    private final RecipeMatcher matcher = new RecipeMatcher();
    private RecipeAdapter adapter;
    private RecyclerView recycler;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(requireContext());

        recycler = view.findViewById(R.id.recycler_suggested);
        emptyView = view.findViewById(R.id.text_empty_suggested);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new RecipeAdapter(this::openDetail);
        recycler.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(); // recompute every time we return, pantry may have changed
    }

    private void refresh() {
        List<PantryItem> pantry = db.getAllPantryItems();
        List<Recipe> allRecipes = db.getAllRecipes();
        List<Recipe> strict = matcher.getStrictMatches(allRecipes, pantry);

        adapter.setRecipes(strict);
        boolean empty = strict.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void openDetail(Recipe recipe) {
        Intent i = new Intent(requireContext(), RecipeDetailActivity.class);
        i.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(i);
    }
}
