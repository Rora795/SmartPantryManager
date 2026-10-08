package com.smartpantry.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smartpantry.app.model.Ingredient;
import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Single on-device SQLite data layer (Section 3.2 - SQLite option).
 *
 * Tables
 *   pantry(_id, name, quantity, unit, expiry)
 *   recipes(_id, name, steps)
 *   recipe_ingredients(_id, recipe_id -> recipes._id, name, quantity, unit)
 *
 * Responsibilities
 *   - create the schema
 *   - seed 18 recipes on first run (onCreate)
 *   - full CRUD for pantry items
 *   - read recipes (+ their ingredient lines) for the matching engine
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "smart_pantry.db";
    public static final int DB_VERSION = 1;

    // pantry table
    public static final String T_PANTRY = "pantry";
    public static final String P_ID = "_id";
    public static final String P_NAME = "name";
    public static final String P_QTY = "quantity";
    public static final String P_UNIT = "unit";
    public static final String P_EXPIRY = "expiry";

    // recipes table
    public static final String T_RECIPES = "recipes";
    public static final String R_ID = "_id";
    public static final String R_NAME = "name";
    public static final String R_STEPS = "steps";

    // recipe_ingredients table
    public static final String T_RI = "recipe_ingredients";
    public static final String RI_ID = "_id";
    public static final String RI_RECIPE = "recipe_id";
    public static final String RI_NAME = "name";
    public static final String RI_QTY = "quantity";
    public static final String RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_PANTRY + " ("
                + P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + P_NAME + " TEXT NOT NULL, "
                + P_QTY + " REAL NOT NULL, "
                + P_UNIT + " TEXT, "
                + P_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + T_RECIPES + " ("
                + R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + R_NAME + " TEXT NOT NULL, "
                + R_STEPS + " TEXT)");

        db.execSQL("CREATE TABLE " + T_RI + " ("
                + RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RI_RECIPE + " INTEGER NOT NULL, "
                + RI_NAME + " TEXT NOT NULL, "
                + RI_QTY + " REAL NOT NULL, "
                + RI_UNIT + " TEXT, "
                + "FOREIGN KEY(" + RI_RECIPE + ") REFERENCES " + T_RECIPES + "(" + R_ID + "))");

        RecipeSeed.seed(db); // pre-load the 18 starter recipes
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_RI);
        db.execSQL("DROP TABLE IF EXISTS " + T_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + T_PANTRY);
        onCreate(db);
    }

    // ------------------------------------------------------------------
    // PANTRY CRUD
    // ------------------------------------------------------------------

    /** CREATE: insert a new pantry item, returns the new row id. */
    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = toValues(item);
        return db.insert(T_PANTRY, null, cv);
    }

    /** READ: all pantry items ordered by name. */
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(T_PANTRY, null, null, null, null, null, P_NAME + " COLLATE NOCASE ASC");
        while (c.moveToNext()) {
            list.add(new PantryItem(
                    c.getLong(c.getColumnIndexOrThrow(P_ID)),
                    c.getString(c.getColumnIndexOrThrow(P_NAME)),
                    c.getDouble(c.getColumnIndexOrThrow(P_QTY)),
                    c.getString(c.getColumnIndexOrThrow(P_UNIT)),
                    c.getString(c.getColumnIndexOrThrow(P_EXPIRY))));
        }
        c.close();
        return list;
    }

    /** READ one item by id (used to pre-fill the edit form). */
    public PantryItem getPantryItem(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(T_PANTRY, null, P_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        PantryItem item = null;
        if (c.moveToFirst()) {
            item = new PantryItem(
                    c.getLong(c.getColumnIndexOrThrow(P_ID)),
                    c.getString(c.getColumnIndexOrThrow(P_NAME)),
                    c.getDouble(c.getColumnIndexOrThrow(P_QTY)),
                    c.getString(c.getColumnIndexOrThrow(P_UNIT)),
                    c.getString(c.getColumnIndexOrThrow(P_EXPIRY)));
        }
        c.close();
        return item;
    }

    /** UPDATE an existing pantry item. Returns rows affected. */
    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(T_PANTRY, toValues(item), P_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    /** DELETE a pantry item by id. Returns rows affected. */
    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(T_PANTRY, P_ID + "=?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(PantryItem item) {
        ContentValues cv = new ContentValues();
        cv.put(P_NAME, item.getName());
        cv.put(P_QTY, item.getQuantity());
        cv.put(P_UNIT, item.getUnit());
        cv.put(P_EXPIRY, item.getExpiryDate());
        return cv;
    }

    // ------------------------------------------------------------------
    // RECIPE READS (for the matching engine)
    // ------------------------------------------------------------------

    /** Read every recipe together with its required ingredient lines. */
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor rc = db.query(T_RECIPES, null, null, null, null, null, R_NAME + " ASC");
        while (rc.moveToNext()) {
            long id = rc.getLong(rc.getColumnIndexOrThrow(R_ID));
            Recipe recipe = new Recipe(
                    id,
                    rc.getString(rc.getColumnIndexOrThrow(R_NAME)),
                    rc.getString(rc.getColumnIndexOrThrow(R_STEPS)));

            Cursor ic = db.query(T_RI, null, RI_RECIPE + "=?",
                    new String[]{String.valueOf(id)}, null, null, null);
            while (ic.moveToNext()) {
                recipe.addIngredient(new Ingredient(
                        ic.getString(ic.getColumnIndexOrThrow(RI_NAME)),
                        ic.getDouble(ic.getColumnIndexOrThrow(RI_QTY)),
                        ic.getString(ic.getColumnIndexOrThrow(RI_UNIT))));
            }
            ic.close();
            recipes.add(recipe);
        }
        rc.close();
        return recipes;
    }

    /** Read a single recipe (with ingredients) for the detail screen. */
    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Recipe recipe = null;
        Cursor rc = db.query(T_RECIPES, null, R_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (rc.moveToFirst()) {
            recipe = new Recipe(
                    id,
                    rc.getString(rc.getColumnIndexOrThrow(R_NAME)),
                    rc.getString(rc.getColumnIndexOrThrow(R_STEPS)));
            Cursor ic = db.query(T_RI, null, RI_RECIPE + "=?",
                    new String[]{String.valueOf(id)}, null, null, null);
            while (ic.moveToNext()) {
                recipe.addIngredient(new Ingredient(
                        ic.getString(ic.getColumnIndexOrThrow(RI_NAME)),
                        ic.getDouble(ic.getColumnIndexOrThrow(RI_QTY)),
                        ic.getString(ic.getColumnIndexOrThrow(RI_UNIT))));
            }
            ic.close();
        }
        rc.close();
        return recipe;
    }
}
