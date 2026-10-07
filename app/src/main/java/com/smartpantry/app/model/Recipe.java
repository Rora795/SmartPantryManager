package com.smartpantry.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe: a name, a set of required ingredients, and preparation steps.
 * 'recipes' holds the name/steps; 'recipe_ingredients' holds the lines.
 */
public class Recipe {

    private long id;
    private String name;
    private String steps;                 // newline-separated preparation method
    private List<Ingredient> ingredients; // required ingredients

    public Recipe(long id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
        this.ingredients = new ArrayList<>();
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getSteps() { return steps; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public void addIngredient(Ingredient i) { this.ingredients.add(i); }
}
