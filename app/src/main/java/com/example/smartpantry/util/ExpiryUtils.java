package com.example.smartpantry.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Date arithmetic for expiry dates stored as "yyyy-MM-dd" text.
 * Compares whole calendar days (not hours), so an item expiring today
 * is 0 days away at any time of day.
 */
public final class ExpiryUtils {

    private static final long MILLIS_PER_DAY = 24L * 60 * 60 * 1000;

    private ExpiryUtils() {
    }

    /**
     * @return days from today until the date: 0 = today, 1 = tomorrow,
     * negative = already expired; null if there is no date or it is unreadable
     */
    public static Integer daysUntilExpiry(String isoDate) {
        return daysUntilExpiry(isoDate, Calendar.getInstance());
    }

    /** Same, with "today" supplied so it can be tested with a fixed date. */
    public static Integer daysUntilExpiry(String isoDate, Calendar today) {
        if (isoDate == null) {
            return null;
        }
        String[] parts = isoDate.trim().split("-");
        if (parts.length != 3) {
            return null;
        }
        try {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            if (month < 1 || month > 12 || day < 1 || day > 31) {
                return null;
            }
            long expiryDay = epochDay(year, month, day);
            long todayDay = epochDay(today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH) + 1,
                    today.get(Calendar.DAY_OF_MONTH));
            return (int) (expiryDay - todayDay);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** True if the date is before today. Items with no date are never expired. */
    public static boolean isExpired(String isoDate) {
        Integer days = daysUntilExpiry(isoDate);
        return days != null && days < 0;
    }

    /** True if the date is today or within the next withinDays days (not yet expired). */
    public static boolean isExpiringSoon(String isoDate, int withinDays) {
        Integer days = daysUntilExpiry(isoDate);
        return days != null && days >= 0 && days <= withinDays;
    }

    /** Days since 1970-01-01 for a calendar date, computed in UTC to avoid daylight-saving drift. */
    private static long epochDay(int year, int month, int day) {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.clear();
        utc.set(year, month - 1, day);
        return Math.floorDiv(utc.getTimeInMillis(), MILLIS_PER_DAY);
    }
}