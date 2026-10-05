package com.example.smartpantry.matcher;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts quantities with different units into one base unit per dimension so
 * they can be compared: 0.5 kg and 500 g are the same amount.
 *
 *   mass   : base unit grams        (g, kg)
 *   volume : base unit millilitres  (ml, l, tsp = 5 ml, tbsp = 15 ml, cup = 250 ml)
 *   count  : base unit pieces       (pcs)
 */
public final class UnitConverter {

    public static final String MASS = "mass";
    public static final String VOLUME = "volume";
    public static final String COUNT = "count";

    /** Alternative spellings mapped to the canonical unit code. */
    private static final Map<String, String> ALIASES = new HashMap<>();
    private static final Map<String, String> DIMENSION_OF = new HashMap<>();
    private static final Map<String, Double> FACTOR_TO_BASE = new HashMap<>();

    static {
        register("g", MASS, 1, "g", "gram", "grams", "gm");
        register("kg", MASS, 1000, "kg", "kgs", "kilogram", "kilograms", "kilo", "kilos");
        register("ml", VOLUME, 1, "ml", "millilitre", "milliliter", "millilitres", "milliliters");
        register("l", VOLUME, 1000, "l", "lt", "litre", "liter", "litres", "liters");
        register("tsp", VOLUME, 5, "tsp", "tsps", "teaspoon", "teaspoons");
        register("tbsp", VOLUME, 15, "tbsp", "tbsps", "tbs", "tablespoon", "tablespoons");
        register("cup", VOLUME, 250, "cup", "cups");
        register("pcs", COUNT, 1, "pcs", "pc", "piece", "pieces", "each", "whole", "unit", "units");
    }

    private static void register(String code, String dimension, double factor, String... names) {
        DIMENSION_OF.put(code, dimension);
        FACTOR_TO_BASE.put(code, factor);
        for (String name : names) {
            ALIASES.put(name, code);
        }
    }

    private UnitConverter() {
    }

    /** A quantity expressed in the base unit of its dimension. */
    public static final class Measure {
        public final double amount;
        public final String dimension;

        Measure(double amount, String dimension) {
            this.amount = amount;
            this.dimension = dimension;
        }
    }

    /**
     * @return the canonical unit code ("kg", "tbsp", "pcs", ...), or the cleaned
     * text itself when the unit is not recognised. A missing unit counts as pieces.
     */
    public static String canonicalUnit(String unit) {
        String cleaned = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        if (cleaned.isEmpty()) {
            return "pcs";
        }
        String code = ALIASES.get(cleaned);
        return code != null ? code : cleaned;
    }

    /** Converts a quantity in any known unit to the base unit of its dimension. */
    public static Measure toBase(double quantity, String unit) {
        String code = canonicalUnit(unit);
        Double factor = FACTOR_TO_BASE.get(code);
        if (factor == null) {
            return new Measure(quantity, "other:" + code);
        }
        return new Measure(quantity * factor, DIMENSION_OF.get(code));
    }

    /** True if quantities in these two units can be compared with each other. */
    public static boolean areCompatible(String unitA, String unitB) {
        return toBase(1, unitA).dimension.equals(toBase(1, unitB).dimension);
    }
}