package com.smartpantry.app.logic;

import com.smartpantry.app.model.Ingredient;
import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.model.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The single most important piece of business logic in the app (Section 2.3):
 * the STRICT-MATCHING RULE.
 *
 * A recipe qualifies as "suggested" ONLY IF every single ingredient it
 * requires is present in the pantry in at least the required quantity.
 * If even one ingredient is missing or under-stocked, the recipe is excluded.
 *
 * The optional "Almost There" helper returns recipes missing exactly ONE
 * ingredient (Section 8 bonus) kept completely separate from the strict list.
 */
public class RecipeMatcher {

    /** Build a quick lookup of normalised-name -> pantry item. */
    private Map<String, PantryItem> indexPantry(List<PantryItem> pantry) {
        Map<String, PantryItem> map = new HashMap<>();
        for (PantryItem item : pantry) {
            map.put(IngredientNormalizer.normalizeName(item.getName()), item);
        }
        return map;
    }

    /**
     * How many of this recipe's ingredients the pantry CANNOT satisfy.
     * 0 means the recipe strictly matches.
     */
    private int countMissing(Recipe recipe, Map<String, PantryItem> pantryIndex) {
        int missing = 0;
        for (Ingredient req : recipe.getIngredients()) {
            String key = IngredientNormalizer.normalizeName(req.getName());
            PantryItem have = pantryIndex.get(key);
            if (have == null) {
                missing++;                       // ingredient not in pantry at all
            } else if (!IngredientNormalizer.quantitySatisfies(
                    have.getQuantity(), have.getUnit(),
                    req.getQuantity(), req.getUnit())) {
                missing++;                       // present but not enough
            }
        }
        return missing;
    }

    /**
     * STRICT list: recipes for which countMissing == 0.
     */
    public List<Recipe> getStrictMatches(List<Recipe> allRecipes, List<PantryItem> pantry) {
        Map<String, PantryItem> idx = indexPantry(pantry);
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : allRecipes) {
            if (!r.getIngredients().isEmpty() && countMissing(r, idx) == 0) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * BONUS "Almost There" list (Section 8): recipes missing EXACTLY one
     * ingredient. Kept separate from the strict suggestions.
     */
    public List<Recipe> getAlmostThere(List<Recipe> allRecipes, List<PantryItem> pantry) {
        Map<String, PantryItem> idx = indexPantry(pantry);
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : allRecipes) {
            if (!r.getIngredients().isEmpty() && countMissing(r, idx) == 1) {
                result.add(r);
            }
        }
        return result;
    }
}
