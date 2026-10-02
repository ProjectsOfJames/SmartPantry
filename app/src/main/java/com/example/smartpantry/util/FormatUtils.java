package com.example.smartpantry.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Small helpers for displaying quantities consistently across screens. */
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
}