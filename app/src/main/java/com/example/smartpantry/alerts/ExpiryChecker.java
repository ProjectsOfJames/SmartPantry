package com.example.smartpantry.alerts;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.ExpiryUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Decides which pantry items need an alert. Shared by the background worker
 * and the "Check now" button so both always give the same answer
 */
public final class ExpiryChecker {

    private ExpiryChecker() {
    }

    /**
     * Items that expire within withinDays days or are already expired,
     * soonest first. Items without an expiry date are ignored.
     */
    public static List<PantryItem> findExpiringSoon(List<PantryItem> items, int withinDays) {
        List<PantryItem> result = new ArrayList<>();
        if (items == null) {
            return result;
        }
        for (PantryItem item : items) {
            Integer days = ExpiryUtils.daysUntilExpiry(item.getExpiryDate());
            if (days != null && days <= withinDays) {
                result.add(item);
            }
        }
        Collections.sort(result, (a, b) -> Integer.compare(daysOf(a), daysOf(b)));
        return result;
    }

    private static int daysOf(PantryItem item) {
        Integer days = ExpiryUtils.daysUntilExpiry(item.getExpiryDate());
        return days == null ? Integer.MAX_VALUE : days;
    }
}