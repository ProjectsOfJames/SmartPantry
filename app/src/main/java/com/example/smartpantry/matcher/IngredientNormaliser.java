package com.example.smartpantry.matcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Turns an ingredient name into a canonical form so that trivially different
 * spellings compare equal: "Tomatoes", " tomato " and "TOMATO" all become
 * "tomato"
 *
 * Steps: lowercase, drop punctuation, collapse spaces, singularize the last
 * word (the noun, as in "bell peppers" or "chicken breasts")
 */
public final class IngredientNormaliser {

    /** Plurals that do not follow the simple rules. */
    private static final Map<String, String> IRREGULAR_PLURALS = new HashMap<>();

    /** Words that end in "s" but are not plurals. */
    private static final Set<String> UNCHANGED = new HashSet<>(Arrays.asList(
            "molasses", "hummus", "asparagus", "couscous", "citrus", "watercress", "swiss", "bass"));

    /** Alternative names mapped onto one canonical name  */
    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        IRREGULAR_PLURALS.put("leaves", "leaf");
        IRREGULAR_PLURALS.put("loaves", "loaf");
        IRREGULAR_PLURALS.put("halves", "half");
        IRREGULAR_PLURALS.put("cookies", "cookie");
        IRREGULAR_PLURALS.put("brownies", "brownie");

        SYNONYMS.put("capsicum", "bell pepper");
        SYNONYMS.put("aubergine", "eggplant");
        SYNONYMS.put("brinjal", "eggplant");
        SYNONYMS.put("courgette", "zucchini");
        SYNONYMS.put("baby marrow", "zucchini");
        SYNONYMS.put("spring onion", "green onion");
        SYNONYMS.put("scallion", "green onion");
        SYNONYMS.put("garbanzo bean", "chickpea");
    }

    private IngredientNormaliser() {
    }

    /** @return the canonical form of the name, or "" if there is nothing usable in it */
    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String text = raw.toLowerCase(Locale.ROOT)
                .replace("'", "")
                .replace("\u2019", "")
                .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .trim()
                .replaceAll("\\s+", " ");
        if (text.isEmpty()) {
            return "";
        }

        int lastSpace = text.lastIndexOf(' ');
        String prefix = text.substring(0, lastSpace + 1); // "" for a single word
        String result = prefix + singularize(text.substring(lastSpace + 1));

        String synonym = SYNONYMS.get(result);
        return synonym != null ? synonym : result;
    }

    /** True if both names are the same ingredient once normalized. */
    public static boolean sameIngredient(String a, String b) {
        String na = normalize(a);
        return !na.isEmpty() && na.equals(normalize(b));
    }

    /** Converts one English plural noun to its singular form. */
    static String singularize(String word) {
        int length = word.length();
        if (length <= 3 || UNCHANGED.contains(word)) {
            return word;
        }
        String irregular = IRREGULAR_PLURALS.get(word);
        if (irregular != null) {
            return irregular;
        }
        // not plurals: glass, hummus, analysis
        if (word.endsWith("ss") || word.endsWith("us") || word.endsWith("is")) {
            return word;
        }
        // berries -> berry, cherries -> cherry
        if (word.endsWith("ies") && length > 4) {
            return word.substring(0, length - 3) + "y";
        }
        // tomatoes -> tomato, potatoes -> potato
        if (word.endsWith("oes")) {
            return word.substring(0, length - 2);
        }
        // peaches -> peach, radishes -> radish, boxes -> box
        if (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("sses")
                || word.endsWith("xes") || word.endsWith("zes")) {
            return word.substring(0, length - 2);
        }
        // eggs -> egg, onions -> onion, cloves -> clove
        if (word.endsWith("s")) {
            return word.substring(0, length - 1);
        }
        return word;
    }
}