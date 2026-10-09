package com.smartpantry.app.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Seeds 18 starter recipes on first run (called from DatabaseHelper.onCreate).
 * Kept in its own class so DatabaseHelper stays readable.
 *
 * Ingredient names/units are stored in simple everyday terms; the matching
 * engine normalises them (see IngredientNormalizer) so trivial differences
 * such as "tomato" vs "tomatoes" still match.
 */
final class RecipeSeed {

    private RecipeSeed() { }

    static void seed(SQLiteDatabase db) {
        addRecipe(db, "Scrambled Eggs",
                "1. Beat the eggs with the milk and a pinch of salt.\n"
                        + "2. Melt butter in a pan over low heat.\n"
                        + "3. Pour in the eggs and stir gently until just set.",
                new String[][]{{"Egg", "3", "pcs"}, {"Milk", "30", "ml"}, {"Butter", "10", "g"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Tomato Omelette",
                "1. Whisk the eggs.\n2. Fry chopped tomato in oil for 2 minutes.\n"
                        + "3. Add eggs and cook until set, season with salt.",
                new String[][]{{"Egg", "2", "pcs"}, {"Tomato", "1", "pcs"}, {"Oil", "15", "ml"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Cheese Toast",
                "1. Butter the bread.\n2. Lay cheese slices on top.\n3. Grill until the cheese melts and the bread is golden.",
                new String[][]{{"Bread", "2", "pcs"}, {"Cheese", "40", "g"}, {"Butter", "10", "g"}});

        addRecipe(db, "Pasta Aglio e Olio",
                "1. Boil the pasta in salted water.\n2. Gently fry sliced garlic in olive oil.\n3. Toss drained pasta in the garlic oil, season with salt.",
                new String[][]{{"Pasta", "200", "g"}, {"Garlic", "3", "pcs"}, {"Oil", "30", "ml"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Tomato Pasta",
                "1. Cook the pasta.\n2. Simmer chopped tomatoes with garlic and oil into a sauce.\n3. Combine and season.",
                new String[][]{{"Pasta", "200", "g"}, {"Tomato", "3", "pcs"}, {"Garlic", "2", "pcs"}, {"Oil", "20", "ml"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Pancakes",
                "1. Mix flour, milk, egg and sugar into a batter.\n2. Pour ladles onto a hot buttered pan.\n3. Flip when bubbles appear; cook until golden.",
                new String[][]{{"Flour", "150", "g"}, {"Milk", "200", "ml"}, {"Egg", "1", "pcs"}, {"Sugar", "20", "g"}, {"Butter", "10", "g"}});

        addRecipe(db, "Boiled Rice",
                "1. Rinse the rice.\n2. Add water and a little salt.\n3. Simmer covered until the water is absorbed.",
                new String[][]{{"Rice", "200", "g"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Egg Fried Rice",
                "1. Scramble the eggs and set aside.\n2. Fry cooked rice with oil.\n3. Stir in the egg and salt.",
                new String[][]{{"Rice", "200", "g"}, {"Egg", "2", "pcs"}, {"Oil", "20", "ml"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Garlic Butter Rice",
                "1. Fry garlic in butter.\n2. Add cooked rice and toss.\n3. Season with salt.",
                new String[][]{{"Rice", "200", "g"}, {"Garlic", "2", "pcs"}, {"Butter", "20", "g"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Mashed Potatoes",
                "1. Boil the potatoes until soft.\n2. Mash with butter and milk.\n3. Season with salt.",
                new String[][]{{"Potato", "400", "g"}, {"Butter", "30", "g"}, {"Milk", "50", "ml"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Potato Fries",
                "1. Cut potatoes into sticks.\n2. Toss in oil and salt.\n3. Fry or bake until crisp and golden.",
                new String[][]{{"Potato", "400", "g"}, {"Oil", "50", "ml"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Vegetable Stir Fry",
                "1. Heat oil in a wok.\n2. Stir fry onion, carrot and garlic on high heat.\n3. Finish with salt.",
                new String[][]{{"Onion", "1", "pcs"}, {"Carrot", "2", "pcs"}, {"Garlic", "2", "pcs"}, {"Oil", "20", "ml"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Carrot Soup",
                "1. Soften onion in butter.\n2. Add chopped carrot and water, simmer until soft.\n3. Blend and season.",
                new String[][]{{"Carrot", "4", "pcs"}, {"Onion", "1", "pcs"}, {"Butter", "20", "g"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "French Toast",
                "1. Beat egg with milk and sugar.\n2. Soak the bread slices.\n3. Fry in butter until golden on both sides.",
                new String[][]{{"Bread", "2", "pcs"}, {"Egg", "1", "pcs"}, {"Milk", "50", "ml"}, {"Sugar", "10", "g"}, {"Butter", "10", "g"}});

        addRecipe(db, "Cheese Omelette",
                "1. Whisk the eggs with salt.\n2. Pour into a buttered pan.\n3. Add cheese, fold and serve.",
                new String[][]{{"Egg", "3", "pcs"}, {"Cheese", "40", "g"}, {"Butter", "10", "g"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Simple Salad",
                "1. Chop tomato, onion and cucumber.\n2. Toss with oil and salt.",
                new String[][]{{"Tomato", "2", "pcs"}, {"Onion", "1", "pcs"}, {"Cucumber", "1", "pcs"}, {"Oil", "15", "ml"}, {"Salt", "1", "tsp"}});

        addRecipe(db, "Sugar Milk Tea",
                "1. Brew the tea.\n2. Add warm milk and sugar to taste.",
                new String[][]{{"Tea", "2", "tsp"}, {"Milk", "150", "ml"}, {"Sugar", "15", "g"}});

        addRecipe(db, "Butter Garlic Bread",
                "1. Mix softened butter with crushed garlic.\n2. Spread on the bread.\n3. Bake until crisp.",
                new String[][]{{"Bread", "2", "pcs"}, {"Butter", "30", "g"}, {"Garlic", "2", "pcs"}});
    }

    /** Insert one recipe and all of its required ingredient rows. */
    private static void addRecipe(SQLiteDatabase db, String name, String steps, String[][] ingredients) {
        ContentValues r = new ContentValues();
        r.put(DatabaseHelper.R_NAME, name);
        r.put(DatabaseHelper.R_STEPS, steps);
        long recipeId = db.insert(DatabaseHelper.T_RECIPES, null, r);

        for (String[] ing : ingredients) {
            ContentValues iv = new ContentValues();
            iv.put(DatabaseHelper.RI_RECIPE, recipeId);
            iv.put(DatabaseHelper.RI_NAME, ing[0]);
            iv.put(DatabaseHelper.RI_QTY, Double.parseDouble(ing[1]));
            iv.put(DatabaseHelper.RI_UNIT, ing[2]);
            db.insert(DatabaseHelper.T_RI, null, iv);
        }
    }
}
