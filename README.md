# Smart Pantry Manager

An offline Android app (written in **Java**) that helps you reduce food waste by
tracking the ingredients you actually have at home and suggesting **only** the
recipes you can cook right now — no shopping trip, and no recipe shown unless you
already have *every* ingredient it needs.

> Module: **Mobile App Development 700** — Practical Assignment (Smart Pantry Manager).

---

## What it does

- **Pantry management** — add, edit and delete ingredients (name, quantity, unit,
  optional expiry date).
- **Pantry list** — a `RecyclerView` bound to the SQLite database.
- **Recipe collection** — 18 recipes seeded into the database on first run.
- **Suggested Recipes** — runs the strict-matching rule over your pantry and lists
  only the recipes you can make right now (friendly message when none match).
- **Recipe detail** — full required-ingredient list and preparation method.
- **Settings** — expiring-soon alerts toggle + metric/imperial unit preference
  (persisted in `SharedPreferences`).

## The core logic — strict matching (assignment Section 2.3)

A recipe is only “suggested” if **every** required ingredient is present in the
pantry **in at least the required quantity**. If even one ingredient is missing
or under-stocked, the recipe is excluded.

Implemented in:
- `logic/RecipeMatcher.java` — counts missing ingredients; 0 missing = suggested.
- `logic/IngredientNormalizer.java` — makes matching robust to trivial messiness
  (case, extra spaces, singular/plural like `tomato` vs `tomatoes`, and unit
  families like `kg` vs `g`, `l` vs `ml`).

An optional **“Almost There”** helper (`getAlmostThere`, recipes missing exactly one
ingredient) is included and kept separate from the strict list (Section 8 bonus).

Unit tests for this logic live in
`app/src/test/java/com/smartpantry/app/logic/RecipeMatcherTest.java` and run with
`./gradlew test` (no emulator needed).

## Database choice — SQLite (and why)

This app uses **SQLite via `SQLiteOpenHelper`** (`db/DatabaseHelper.java`).

Why SQLite over Firebase / PostgreSQL for this app:
- The data (your own pantry + a fixed recipe set) is **personal and local** —
  there is no need for cloud sync, accounts, or a network connection.
- It keeps the app **fully offline** and simple to demo — no backend to host, no
  API keys, no latency.
- `SQLiteOpenHelper` is exactly the persistence approach covered in the module,
  so it is straightforward to explain and defend.

**CRUD** is fully implemented for pantry items (Create/Read/Update/Delete) and the
data persists across app restarts because it lives in the on-device database file
`smart_pantry.db`, not in memory.

### Data model

```
pantry(_id, name, quantity, unit, expiry)
recipes(_id, name, steps)
recipe_ingredients(_id, recipe_id -> recipes._id, name, quantity, unit)
```

## Screens & navigation

A single host `MainActivity` swaps three fragments via a **BottomNavigationView**
(Pantry / Suggestions / Settings). Two further screens are separate Activities
reached through **Intents**:
- `AddEditIngredientActivity` (add + edit form, with input validation)
- `RecipeDetailActivity` (receives a recipe id as an Intent extra)

## Build & run

1. Open the project folder in **Android Studio** (Giraffe/Hedgehog or newer).
2. Let Gradle sync. Android Studio will generate the Gradle wrapper JAR and
   `local.properties` (SDK path) automatically if they are missing.
3. Select a device / emulator (API 24+) and press **Run**.

Requirements: Android Gradle Plugin 8.1, compileSdk 34, minSdk 24, Java 8.

## Out of scope (per the brief)

No Google Maps, no mapping SDK, no GPS/location features; no payments; not
published to Play Store. The app declares **no** `INTERNET` or location
permissions.

## Project structure

```
app/src/main/java/com/smartpantry/app/
  db/      DatabaseHelper.java, RecipeSeed.java      (SQLite + seed data)
  model/   PantryItem, Recipe, Ingredient           (POJOs)
  logic/   RecipeMatcher, IngredientNormalizer       (strict-matching core)
  ui/      MainActivity, 3 fragments, 2 activities
  ui/adapter/  PantryAdapter, RecipeAdapter          (custom RecyclerView adapters)
```
