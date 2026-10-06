package com.example.smartpantry.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Reads and writes the user's preferences with SharedPreferences, so they
 * survive closing the app
 */
public class SettingsManager {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_ALERT_DAYS = "expiry_alert_days";
    private static final String KEY_UNIT_SYSTEM = "unit_system";

    public static final String UNITS_METRIC = "metric";
    public static final String UNITS_KITCHEN = "kitchen";

    public static final int DEFAULT_ALERT_DAYS = 3;

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Off by default, because turning it on also asks for notification permission. */
    public boolean isExpiryAlertsEnabled() {
        return prefs.getBoolean(KEY_EXPIRY_ALERTS, false);
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    /** How many days before the expiry date an item counts as "expiring soon". */
    public int getAlertDays() {
        return prefs.getInt(KEY_ALERT_DAYS, DEFAULT_ALERT_DAYS);
    }

    public void setAlertDays(int days) {
        prefs.edit().putInt(KEY_ALERT_DAYS, days).apply();
    }

    /** UNITS_METRIC or UNITS_KITCHEN. */
    public String getUnitSystem() {
        return prefs.getString(KEY_UNIT_SYSTEM, UNITS_METRIC);
    }

    public void setUnitSystem(String unitSystem) {
        prefs.edit().putString(KEY_UNIT_SYSTEM, unitSystem).apply();
    }

    /** The unit pre-selected in the Add Ingredient form. */
    public String getDefaultUnit() {
        return UNITS_KITCHEN.equals(getUnitSystem()) ? "cup" : "g";
    }
}