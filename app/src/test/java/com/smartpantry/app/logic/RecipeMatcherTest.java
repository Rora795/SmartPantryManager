package com.smartpantry.app.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.model.Recipe;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pure-Java unit tests for the strict-matching rule (Section 2.3).
 * These run on the local JVM (no device/emulator needed):  ./gradlew test
 */
public class RecipeMatcherTest {

    private final RecipeMatcher matcher = new RecipeMatcher();

    private PantryItem p(String name, double qty, String unit) {
        return new PantryItem(1, name, qty, unit, null);
    }

    private Recipe recipe(String name, String[][] ings) {
        Recipe r = new Recipe(1, name, "steps");
        for (String[] i : ings) {
            r.addIngredient(new com.smartpantry.app.model.Ingredient(
                    i[0], Double.parseDouble(i[1]), i[2]));
        }
        return r;
    }

    @Test
    public void fullMatch_isSuggested() {
        Recipe eggs = recipe("Eggs", new String[][]{{"Egg", "2", "pcs"}, {"Salt", "1", "tsp"}});
        List<PantryItem> pantry = Arrays.asList(p("Egg", 3, "pcs"), p("Salt", 5, "tsp"));
        List<Recipe> strict = matcher.getStrictMatches(new ArrayList<>(Arrays.asList(eggs)), pantry);
        assertEquals(1, strict.size());
    }

    @Test
    public void oneIngredientMissing_isExcluded() {
        Recipe eggs = recipe("Eggs", new String[][]{{"Egg", "2", "pcs"}, {"Salt", "1", "tsp"}});
        List<PantryItem> pantry = Arrays.asList(p("Egg", 3, "pcs")); // no salt
        List<Recipe> strict = matcher.getStrictMatches(new ArrayList<>(Arrays.asList(eggs)), pantry);
        assertTrue(strict.isEmpty());
    }

    @Test
    public void notEnoughQuantity_isExcluded() {
        Recipe eggs = recipe("Eggs", new String[][]{{"Egg", "5", "pcs"}});
        List<PantryItem> pantry = Arrays.asList(p("Egg", 2, "pcs")); // only 2 of 5
        assertTrue(matcher.getStrictMatches(new ArrayList<>(Arrays.asList(eggs)), pantry).isEmpty());
    }

    @Test
    public void pluralAndCase_stillMatch() {
        Recipe r = recipe("Salad", new String[][]{{"Tomato", "2", "pcs"}});
        List<PantryItem> pantry = Arrays.asList(p("  tomatoes ", 4, "pcs")); // messy name
        assertEquals(1, matcher.getStrictMatches(new ArrayList<>(Arrays.asList(r)), pantry).size());
    }

    @Test
    public void unitConversion_kgCoversGrams() {
        Recipe r = recipe("Cake", new String[][]{{"Flour", "500", "g"}});
        List<PantryItem> pantry = Arrays.asList(p("Flour", 1, "kg")); // 1kg >= 500g
        assertEquals(1, matcher.getStrictMatches(new ArrayList<>(Arrays.asList(r)), pantry).size());
    }

    @Test
    public void almostThere_missingExactlyOne() {
        Recipe r = recipe("Eggs", new String[][]{{"Egg", "2", "pcs"}, {"Salt", "1", "tsp"}});
        List<PantryItem> pantry = Arrays.asList(p("Egg", 3, "pcs")); // missing only salt
        assertFalse(matcher.getAlmostThere(new ArrayList<>(Arrays.asList(r)), pantry).isEmpty());
    }
}
