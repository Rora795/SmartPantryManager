package com.smartpantry.app.model;

/**
 * One required ingredient line inside a recipe, e.g. "2 pcs of Tomato".
 * Maps to a row in the 'recipe_ingredients' table.
 */
public class Ingredient {

    private String name;      // required ingredient name, e.g. "Tomato"
    private double quantity;  // required amount
    private String unit;      // required unit

    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
}
