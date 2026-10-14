package com.smartpantry.app.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.smartpantry.app.R;
import com.smartpantry.app.db.DatabaseHelper;
import com.smartpantry.app.model.Ingredient;
import com.smartpantry.app.model.Recipe;

/**
 * Recipe Detail screen. Receives a recipe id through an Intent extra, loads it
 * from SQLite and shows the full required-ingredient list and the method.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView titleView = findViewById(R.id.text_recipe_name);
        TextView ingredientsView = findViewById(R.id.text_recipe_ingredients);
        TextView stepsView = findViewById(R.id.text_recipe_steps);

        long id = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        DatabaseHelper db = new DatabaseHelper(this);
        Recipe recipe = db.getRecipe(id);

        if (recipe == null) {
            titleView.setText(R.string.recipe_not_found);
            return;
        }

        setTitle(recipe.getName());
        titleView.setText(recipe.getName());

        StringBuilder sb = new StringBuilder();
        for (Ingredient ing : recipe.getIngredients()) {
            sb.append("\u2022 ")
              .append(trimNumber(ing.getQuantity())).append(" ")
              .append(ing.getUnit()).append("  ")
              .append(ing.getName()).append("\n");
        }
        ingredientsView.setText(sb.toString().trim());
        stepsView.setText(recipe.getSteps());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /** Show 3 as "3" instead of "3.0" but keep 1.5 as "1.5". */
    private String trimNumber(double d) {
        if (d == Math.floor(d)) return String.valueOf((long) d);
        return String.valueOf(d);
    }
}
