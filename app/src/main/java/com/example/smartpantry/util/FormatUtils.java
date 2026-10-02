package com.example.smartpantry.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** helpers for displaying and parsing quantities consistently across screens. */
public final class FormatUtils {

    private FormatUtils() {
    }

    /** 500.0 becomes "500", 0.5 becomes "0.5", 1.25 becomes "1.25". */
    public static String formatQuantity(double quantity) {
        DecimalFormat format = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));
        return format.format(quantity);
    }

    /** 500.0 and "g" become "500 g". */
    public static String formatAmount(double quantity, String unit) {
        return formatQuantity(quantity) + " " + (unit == null ? "" : unit);
    }

    // Parses what the user typed into a number, accepts both "1.5" and "1,5"
    public static Double parseQuantity(String text) {
        if (text == null) {
            return null;
        }
        String cleaned = text.trim().replace(',', '.');
        if (cleaned.isEmpty()) {
            return null;
        }
        try {
            double value = Double.parseDouble(cleaned);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}