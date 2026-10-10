package com.smartpantry.app.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalises ingredient names and units so that the strict-matching rule
 * (Section 2.3) is robust to trivial real-world messiness, e.g.
 *   "Tomatoes" vs "tomato", "  Egg " vs "egg", "grams" vs "g".
 *
 * This is deliberately NOT a full NLP solution. It just does:
 *   - trim + lowercase
 *   - collapse internal whitespace
 *   - naive singular-isation (strip a trailing 's'/'es')
 *   - map common unit aliases onto a canonical unit
 * so that a naive exact-string match no longer breaks on trivial differences.
 */
public final class IngredientNormalizer {

    private IngredientNormalizer() { }

    /** Canonical unit aliases -> canonical form. */
    private static final Map<String, String> UNIT_ALIASES = new HashMap<>();
    static {
        UNIT_ALIASES.put("gram", "g");
        UNIT_ALIASES.put("grams", "g");
        UNIT_ALIASES.put("g", "g");
        UNIT_ALIASES.put("kg", "kg");
        UNIT_ALIASES.put("kilogram", "kg");
        UNIT_ALIASES.put("kilograms", "kg");
        UNIT_ALIASES.put("ml", "ml");
        UNIT_ALIASES.put("millilitre", "ml");
        UNIT_ALIASES.put("millilitres", "ml");
        UNIT_ALIASES.put("milliliter", "ml");
        UNIT_ALIASES.put("l", "l");
        UNIT_ALIASES.put("litre", "l");
        UNIT_ALIASES.put("litres", "l");
        UNIT_ALIASES.put("liter", "l");
        UNIT_ALIASES.put("pcs", "pcs");
        UNIT_ALIASES.put("pc", "pcs");
        UNIT_ALIASES.put("piece", "pcs");
        UNIT_ALIASES.put("pieces", "pcs");
        UNIT_ALIASES.put("tbsp", "tbsp");
        UNIT_ALIASES.put("tablespoon", "tbsp");
        UNIT_ALIASES.put("tablespoons", "tbsp");
        UNIT_ALIASES.put("tsp", "tsp");
        UNIT_ALIASES.put("teaspoon", "tsp");
        UNIT_ALIASES.put("teaspoons", "tsp");
        UNIT_ALIASES.put("cup", "cup");
        UNIT_ALIASES.put("cups", "cup");
    }

    /** Factors to convert a unit to its base unit within the same family. */
    private static final Map<String, Double> TO_BASE = new HashMap<>();
    static {
        TO_BASE.put("g", 1.0);
        TO_BASE.put("kg", 1000.0);   // base = grams
        TO_BASE.put("ml", 1.0);
        TO_BASE.put("l", 1000.0);    // base = millilitres
    }

    /** Normalise an ingredient NAME: trim, lowercase, collapse spaces, singularise. */
    public static String normalizeName(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toLowerCase().replaceAll("\\s+", " ");
        return singularize(s);
    }

    /** Very small, naive singular-isation that is good enough for food words. */
    private static String singularize(String s) {
        if (s.length() > 3 && s.endsWith("es")
                && (s.endsWith("oes") || s.endsWith("ches") || s.endsWith("shes"))) {
            return s.substring(0, s.length() - 2); // tomatoes -> tomato
        }
        if (s.length() > 3 && s.endsWith("s") && !s.endsWith("ss")) {
            return s.substring(0, s.length() - 1); // eggs -> egg
        }
        return s;
    }

    /** Normalise a UNIT onto its canonical alias (defaults to the trimmed input). */
    public static String normalizeUnit(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toLowerCase();
        String canon = UNIT_ALIASES.get(s);
        return canon != null ? canon : s;
    }

    /**
     * Returns true if {@code have} is enough to cover {@code need}, taking
     * unit families into account (kg vs g, l vs ml). If the two units are not
     * in a known convertible family we fall back to comparing the canonical
     * unit strings directly.
     */
    public static boolean quantitySatisfies(double haveQty, String haveUnit,
                                             double needQty, String needUnit) {
        String hu = normalizeUnit(haveUnit);
        String nu = normalizeUnit(needUnit);

        Double hFactor = TO_BASE.get(hu);
        Double nFactor = TO_BASE.get(nu);

        // Both units belong to the same convertible family (mass OR volume)?
        boolean sameMass = isMass(hu) && isMass(nu);
        boolean sameVolume = isVolume(hu) && isVolume(nu);
        if (hFactor != null && nFactor != null && (sameMass || sameVolume)) {
            double haveBase = haveQty * hFactor;
            double needBase = needQty * nFactor;
            return haveBase + 1e-9 >= needBase;
        }

        // Otherwise require the same canonical unit and compare quantities.
        if (hu.equals(nu)) {
            return haveQty + 1e-9 >= needQty;
        }
        // Unknown / mismatched units: be lenient on unit but still require qty.
        return haveQty + 1e-9 >= needQty;
    }

    private static boolean isMass(String u) { return "g".equals(u) || "kg".equals(u); }
    private static boolean isVolume(String u) { return "ml".equals(u) || "l".equals(u); }
}
